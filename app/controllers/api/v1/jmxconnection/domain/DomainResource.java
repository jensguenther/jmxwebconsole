/**
 * 
 */
package controllers.api.v1.jmxconnection.domain;

import static controllers.api.v1.APIConstants.URI_ROOT_JMXCONNECTION;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import models.api.v1.JMXConnectionModel;
import models.jstree.JMXNode;
import models.jstree.JsTreeNode;
import play.Logger;
import play.data.validation.Validation;
import play.libs.Codec;
import controllers.ResourceController;

/**
 * @author jguenther
 *
 */
public class DomainResource extends ResourceController {
	
    public static void list(UUID connId) {
    	if(Validation.hasErrors()){
    		badRequest();
    	}
    	
    	JMXConnectionModel connection = JMXConnectionModel.findById(connId);
    	if(connection == null){
			notFound("no such JMXConnection: "+connId.toString());
    	}
    	
        try {
			renderJSON(connection.getDomains());
		} catch (IOException e) {
			Logger.error(e, "could not list domains");
			error("could not list domains");
		}
    }

    public static void get(UUID connId, String domain){
    	if(Validation.hasErrors()){
    		badRequest();
    	}
    	
    	JMXConnectionModel connection = JMXConnectionModel.findById(connId);
    	if(connection == null){
			notFound("no such JMXConnection: "+connId.toString());
    	}
    	
        try {
        	String baseUrl = URI_ROOT_JMXCONNECTION + "/" + connId + "/mbean/";
        	ObjectNameJsTreeNodeTreeBuilder builder = new ObjectNameJsTreeNodeTreeBuilder(baseUrl);
        	for(ObjectName oname : connection.getObjectNames(domain)){
        		builder.add(oname);
        	}
        	renderJSON(builder.build());
		} catch (IOException e) {
			Logger.error(e, "could not list MBean names");
			error("could not list MBean names");
		} catch (MalformedObjectNameException e) {
			Logger.error(e, "could not list MBean names");
			error("could not list MBean names");
		}
    }
    
    // -------------------------------------------------------------------------------------------------------
	// inner classes
	// -------------------------------------------------------------------------------------------------------

    public static abstract class ObjectNameJsTreeNodeConstants {
    	public final static String METADATA_MODEL_KEY_ID = "id";
    }
    /**
     * Factory to create a JsTreeNode from a ObjectName.
     */
    public static class ObjectNameJsTreeNodeFactory extends ObjectNameJsTreeNodeConstants {
    	
    	public final static ObjectNameJsTreeNodeFactory INSTANCE = new ObjectNameJsTreeNodeFactory();
    	private final static Pattern PATTERN_SPLIT_KEYPROPERTY_LIST = Pattern.compile(",");
    	private final static Pattern PATTERN_SPLIT_KEYPROPERTY = Pattern.compile("=");
    	
    	public JsTreeNode createJsTreeNode(String baseUrl, ObjectName oname){
    		String domain = oname.getDomain();
    		String[][] keyProperties = getKeyProperties(oname);

    		// create a JsTreeNode for each tree level --> path
    		List<JsTreeNode> path = new LinkedList<JsTreeNode>();
    		JsTreeNode lastCreatedNode = null;
    		for(int level = keyProperties.length - 1; level >= 0; level--){
    			// create node
    			JsTreeNode createdNode = createJsTreeNode(baseUrl, domain, keyProperties, level);
    			if(lastCreatedNode != null){
    				// created has to be a container node
    				// add last node as child
    				JsTreeNode.updateJsTreeNode(createdNode)
    				.add(lastCreatedNode)
    				.build();
    			}
    			path.add(0, createdNode);
    			lastCreatedNode = createdNode;
    		}
    		
    		return path.get(0);
    	}

		/**
		 * Creates the JsTreeNode for the given tree level
		 * @param domain
		 * @param keyProperties
		 * @param level
		 * @return
		 */
		private JsTreeNode createJsTreeNode(String baseUrl, String domain, String[][] keyProperties, int level) {
			String id = createId(domain, keyProperties, level);
			Map<String, Object> model = createModel(id);
			
			return (level < keyProperties.length - 1)?
					new FolderNode(id, keyProperties[level][1], model) :
					new MBeanNode(id, keyProperties[level][1], baseUrl+id, model);				
		}

		private Map<String, Object> createModel(String id){
			Map<String, Object> modelMap = new HashMap<String, Object>(1);
			modelMap.put(METADATA_MODEL_KEY_ID, id);
			return modelMap;
		}
		/**
		 * Creates the id for the {@link ObjectName} which will have the format:
		 * <pre>
		 * 		domain.value[.value]*
		 * </pre>
		 * 
		 */
		private String createId(String domain, String[][] keyProperties, int level) {
			String id = domain;
			for(int i = 0; i <= level; i++){
				id += ((i == 0)? ":" : ",") + keyProperties[i][0] + "=" + keyProperties[i][1];
			}
			
			return Codec.encodeBASE64(id);
		}

		/**
		 * Returns the KeyValues of the onames as array {{key,value}}
		 * @param oname
		 * @return
		 */
		private String[][] getKeyProperties(ObjectName oname) {
			String[] keyProperties = PATTERN_SPLIT_KEYPROPERTY_LIST.split(oname.getKeyPropertyListString());
			String[][] ret = new String[keyProperties.length][keyProperties.length];
			for(int i = 0; i < keyProperties.length; i++){
				ret[i] = PATTERN_SPLIT_KEYPROPERTY.split(keyProperties[i]);
			}
			return ret;
		}
		
		
    }
    
    /**
     * Builder to build a JsTree data tree from ObjectNames.
     * @author jguenther
     *
     */
    public static class ObjectNameJsTreeNodeTreeBuilder extends ObjectNameJsTreeNodeConstants {
    	private String baseUrl;
    	private List<JsTreeNode> roots = new LinkedList<JsTreeNode>();
    	
    	public ObjectNameJsTreeNodeTreeBuilder(String baseUrl){
    		this.baseUrl = baseUrl;
    	}
    	
    	public List<JsTreeNode> build(){
    		return roots;
    	}
    	
    	public ObjectNameJsTreeNodeTreeBuilder add(ObjectName oname){
    		// convert oname into JsTreeNode
    		JsTreeNode node = ObjectNameJsTreeNodeFactory.INSTANCE.createJsTreeNode(baseUrl, oname);
    		// check if we'd already that node
    		JsTreeNode currentNode = findSameNode(roots, node);
    		if(currentNode == null){
    			// this is a new node so add it and we're done
    			roots.add(node);
    			return this;
    		}
    		
    		// we knew that node already so we need to merge them
    		JsTreeNode currentNodeMerged = mergeNodes(currentNode, node);
    		// replace it in the roots list
    		int currentIndex = roots.indexOf(currentNode);
    		roots.set(currentIndex, currentNodeMerged);
    		
    		return this;
    	}

		/**
		 * Merges the two given nodes into one. Both node have to share the same data.attr.id and data.title
		 * which has to be enforced by the callee.
		 * 
		 * @param nodeA
		 * @param nodeB
		 * @return
		 */
		private JsTreeNode mergeNodes(JsTreeNode mergeBase, JsTreeNode mergeFrom) {
			if(!mergeBase.hasChildren() && !mergeFrom.hasChildren()){
				// if both are empty container we can safely return mergeBase as result
				return mergeBase;
			}
			
			if(mergeFrom.getChildren().isEmpty()){
				// if mergeFrom was an empty container we can safely return mergeBase as result as there
				// is nothing to merge into mergeBase
				return mergeBase;
			}
			
			// we need to recursively do this for all levels
			// we can eventually assume mergeFrom will always have single children in each level and 
			// mergeBase might have multiple ones
			JsTreeNode childOfMergeFrom = mergeFrom.getChildren().get(0);
			JsTreeNode currentMergeBaseChild = findSameNode(mergeBase.getChildren(), childOfMergeFrom);
			if(currentMergeBaseChild == null){
				// mergeBase hasn't so child, so we add childOfMergeFrom and we're done
				mergeBase.getChildren().add(childOfMergeFrom);
				return mergeBase;
			}
			
			// found the same node so we need to merge both
			JsTreeNode newCurrentMergeBaseChild = mergeNodes(currentMergeBaseChild, childOfMergeFrom);
			// last thing to do is to replace currentMergeBaseChild with the new one
			int indexOfCurrentMergeBaseChild = mergeBase.getChildren().indexOf(currentMergeBaseChild);
			mergeBase.getChildren().set(indexOfCurrentMergeBaseChild, newCurrentMergeBaseChild);
			
			return mergeBase;
		}

		/**
		 * Finds within nodes a node with the same node.data.attr.id and node.metadata.type as the given one.
		 *  
		 * @param root
		 * @param node
		 * @return
		 */
		private JsTreeNode findSameNode(Collection<JsTreeNode> nodes, JsTreeNode node) {
			String id = (String)node.getData().getAttr().get(JsTreeNode.ATTR_KEY_ID);
			String type = (String)node.getMetadata().get(JMXNode.METADATA_KEY_TYPE);
			for(JsTreeNode currentNode : nodes){
				if(	id.equals(currentNode.getData().getAttr().get(JsTreeNode.ATTR_KEY_ID))
					&& type.equals(currentNode.getMetadata().get(JMXNode.METADATA_KEY_TYPE)))
				{
					return currentNode;
				}
			}
			return null;
		}
    }
}
