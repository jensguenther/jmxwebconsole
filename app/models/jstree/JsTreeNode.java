/**
 * 
 */
package models.jstree;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Java data counterpart for JsTree.
 *  
 * @author jguenther
 *
 */
public class JsTreeNode {

	// -------------------------------------------------------------------------------------------------------
	// statics
	// -------------------------------------------------------------------------------------------------------

	public static final String ATTR_KEY_ID = "id";
	public static final String METADATA_KEY_TITLE = "title";
	public static final String METADATA_KEY_ICON = "icon";
	
	public static Builder createJsTreeNode() {
		return updateJsTreeNode(new JsTreeNode());
	}

	public static Builder from(JsTreeNode node){
		return (Builder)new Builder(new JsTreeNode())
			.with(node);
		
	}

	public static Builder updateJsTreeNode(JsTreeNode node){
		return new Builder(node);
	}
	
	// -------------------------------------------------------------------------------------------------------
	// attributes
	// -------------------------------------------------------------------------------------------------------

	private Data data = new Data();
	private Map<String, Object> metadata = new HashMap<String, Object>();

	private String state;
	private List<JsTreeNode> children = null;

	// -------------------------------------------------------------------------------------------------------
	// methods
	// -------------------------------------------------------------------------------------------------------

	/**
	 * @return the data
	 */
	public Data getData() {
		return data;
	}
	
	/**
	 * @return the metadata
	 */
	public Map<String, Object> getMetadata() {
		return metadata;
	}

	public String getState(){
		return state;
	}

	public boolean hasChildren(){
		return !(children == null || children.isEmpty());
	}
	
	/**
	 * Might be {@code null}.
	 * @return
	 */
	public List<JsTreeNode> getChildren(){
		return children;
	}
	
	// -------------------------------------------------------------------------------------------------------
	// builder
	// -------------------------------------------------------------------------------------------------------

	public static class Builder {

		protected JsTreeNode bean;

		protected Builder(JsTreeNode bean) {
			this.bean = bean;
		}

		public JsTreeNode build() {
			checkState();
			
			return bean;
		}
		
		private void checkState() {
			if(this.bean.state == null && this.bean.children != null){
				withState(State.CLOSED);
			}
		}

		/**
		 * Deep copies the node. All data set before is lost.
		 * 
		 * @param node
		 * @return
		 */
		public Builder with(JsTreeNode node){
			this.bean.data = new Data(node.data);
			this.bean.metadata.clear();
			this.bean.metadata.putAll(node.metadata);
			
			this.bean.state = node.state;
			this.bean.children = (node.children != null)? new LinkedList<JsTreeNode>(node.children) : null;
			
			return this;
		}
		
		/**
		 * <pre>
		 * 	{ data : { attr : { <key> : <value> }}}
		 * </pre>
		 */
		public Builder putDataAttr(String key, Object value){
			if(value == null){
				return this;
			}
			this.bean.data.attr.put(key, value);
			return this;
		}
		
		/**
		 * <pre>
		 * 	{ data : { attr : { id : <id> }}}
		 * </pre>
		 */
		public Builder withId(String id){
			return putDataAttr(JsTreeNode.ATTR_KEY_ID, id);
		}
		
		/**
		 * <pre>
		 * 	{ data : { title : <title> }}
		 * </pre>
		 */
		public Builder withTitle(String title) {
			this.bean.data.title = title;
			return this;
		}
		
		/**
		 * <pre>
		 * 	{ metadata : { <key> : <value> }}
		 * </pre>
		 */
		public Builder putMetadata(String key, Object value){
			if(value == null){
				return this;
			}
			this.bean.metadata.put(key, value);
			return this;
		}
		
		/**
		 * <pre>
		 * 	{ state : <state> }
		 * </pre>
		 */
		public Builder withState(State state) {
			this.bean.state = state.getStateString();
			return this;
		}
		
		/**
		 * <pre>
		 * 	{ children : [ ..., <node> ] }
		 * </pre>
		 */
		public Builder add(JsTreeNode node){
			if(this.bean.children == null){
				this.bean.children = new LinkedList<JsTreeNode>();
			}
			this.bean.children.add(node);
			return this;
		}
		
		public Builder withoutChildren(){
			this.bean.children = null;
			return this;
		}
	}
	
	// -------------------------------------------------------------------------------------------------------
	// inner classes
	// -------------------------------------------------------------------------------------------------------

	public static class Data {
		private String title;
		private Map<String, Object> attr = new HashMap<String, Object>();

		private Data(){}
		private Data(Data data){
			this.title = data.title;
			this.attr.putAll(data.attr);
		}
		
		/**
		 * @return the title
		 */
		public String getTitle() {
			return title;
		}

		/**
		 * @return the attr
		 */
		public Map<String, Object> getAttr() {
			return attr;
		}
	}
	
	public static enum State {
		CLOSED("closed"),
		OPEN("open");
		
		private final String state;
		
		private State(String state){
			this.state = state;
		}
		
		public String getStateString(){
			return state;
		}
	}
}