/**
 * 
 */
package models.jstree;

import java.util.Map;

/**
 * @author jguenther
 *
 */
public class JMXNode extends JsTreeNode {
	// -------------------------------------------------------------------------------------------------------
	// static
	// -------------------------------------------------------------------------------------------------------

	public final static String METADATA_KEY_MODEL = "model";
	public final static String METADATA_KEY_TYPE = "type";
	public final static String METADATA_KEY_URL = "url";

	public static Builder updateJMXNode(JMXNode node){
		return new Builder(node);
	}
	
	// -------------------------------------------------------------------------------------------------------
	// inner classes
	// -------------------------------------------------------------------------------------------------------

	public static class Builder extends JsTreeNode.Builder {

		protected Builder(JMXNode node) {
			super(node);
		}

		public Builder withModel(Map<String, Object> model){
			putMetadata(METADATA_KEY_MODEL, model);
			return this;
		}

		public Builder withType(String type){
			putMetadata(METADATA_KEY_TYPE, type);
			return this;
		}

		public Builder withUrl(String url){
			putMetadata(METADATA_KEY_URL, url);
			return this;
		}
	}
}
