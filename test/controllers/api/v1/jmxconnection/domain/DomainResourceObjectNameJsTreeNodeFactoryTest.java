/**
 * 
 */
package controllers.api.v1.jmxconnection.domain;

import static controllers.api.v1.jmxconnection.domain.DomainResource.ObjectNameJsTreeNodeConstants.METADATA_MODEL_KEY_ID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Map;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import models.jstree.JMXNode;
import models.jstree.JsTreeNode;

import org.junit.Test;

import play.libs.Codec;
import controllers.api.v1.jmxconnection.domain.DomainResource.ObjectNameJsTreeNodeFactory;

/**
 * @author jguenther
 *
 */
public class DomainResourceObjectNameJsTreeNodeFactoryTest {

	private final static String BASE_URL = "/baseurl/";
	
	@Test
	public void testCreateJsTreeNode_withOneKey() throws MalformedObjectNameException {
		ObjectName oname = new ObjectName("domain:key=value");
		
		// when
		JsTreeNode node = ObjectNameJsTreeNodeFactory.INSTANCE.createJsTreeNode(BASE_URL, oname);
		
		// then
		assertEquals(Codec.encodeBASE64("domain:key=value"), getId(node));
		assertEquals("value", getTitle(node));
		assertEquals(MBeanNode.METADATA_VALUE_TYPE_MBEAN, getType(node));
		assertEquals(getId(node), getModel(node).get(METADATA_MODEL_KEY_ID));
		assertEquals(BASE_URL+getId(node), getUrl(node));
	}

	@Test
	public void testCreateJsTreeNode_withThreeKeys() throws MalformedObjectNameException {
		ObjectName oname = new ObjectName("domain:key1=value1,key2=value2,key3=value3");
		
		// when
		JsTreeNode containerNode1 = ObjectNameJsTreeNodeFactory.INSTANCE.createJsTreeNode(BASE_URL, oname);
		
		// then (container node level 0 checks)
		assertEquals(Codec.encodeBASE64("domain:key1=value1"), getId(containerNode1));
		assertEquals("value1", getTitle(containerNode1));
		assertEquals(FolderNode.METADATA_VALUE_TYPE_FOLDER, getType(containerNode1));
		assertEquals(getId(containerNode1), getModel(containerNode1).get(METADATA_MODEL_KEY_ID));
		assertNull(getUrl(containerNode1));
		// and (container node level 1 checks)
		JsTreeNode containerNode2 = containerNode1.getChildren().get(0);
		assertEquals(Codec.encodeBASE64("domain:key1=value1,key2=value2"), getId(containerNode2));
		assertEquals("value2", getTitle(containerNode2));
		assertEquals(FolderNode.METADATA_VALUE_TYPE_FOLDER, getType(containerNode2));
		assertEquals(getId(containerNode2), getModel(containerNode2).get(METADATA_MODEL_KEY_ID));
		assertNull(getUrl(containerNode2));
		// and (single child node level 2 check)
		JsTreeNode node3 = containerNode2.getChildren().get(0);
		assertEquals(Codec.encodeBASE64("domain:key1=value1,key2=value2,key3=value3"), getId(node3));
		assertEquals("value3", getTitle(node3));
		assertEquals(MBeanNode.METADATA_VALUE_TYPE_MBEAN, getType(node3));
		assertEquals(getId(node3), getModel(node3).get(METADATA_MODEL_KEY_ID));
		assertEquals(BASE_URL+getId(node3), getUrl(node3));
		
	}
	
	// -------------------------------------------------------------------------------------------------------
	// convenience
	// -------------------------------------------------------------------------------------------------------

	private String getId(JsTreeNode node){
		return (String)node.getData().getAttr().get(JsTreeNode.ATTR_KEY_ID);
	}
	
	private String getTitle(JsTreeNode node){
		return node.getData().getTitle();
	}

	private String getType(JsTreeNode node){
		return (String)node.getMetadata().get(JMXNode.METADATA_KEY_TYPE);
	}
	
	private Map<String,String> getModel(JsTreeNode node){
		return (Map<String,String>)node.getMetadata().get(JMXNode.METADATA_KEY_MODEL);
	}

	private String getUrl(JsTreeNode node){
		return (String)node.getMetadata().get(JMXNode.METADATA_KEY_URL);
	}

}
