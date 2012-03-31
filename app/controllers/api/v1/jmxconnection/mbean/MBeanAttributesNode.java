/**
 * 
 */
package controllers.api.v1.jmxconnection.mbean;

import java.util.Map;

import javax.management.MBeanAttributeInfo;

import models.jstree.JMXNode;
import models.jstree.JsTreeNode;

/**
 * @author jguenther
 *
 */
public class MBeanAttributesNode extends JMXNode {

	/**
	 * 
	 */
	private static final String METADATA_VALUE_TYPE_MBEAN_ATTRIBUTE_LIST = "mBeanAttributeList";

	// -------------------------------------------------------------------------------------------------------
	// static
	// -------------------------------------------------------------------------------------------------------

	public Builder updateMBeanAttributesJsTreeNode(MBeanAttributesNode node){
		return new Builder(node);
	}
	
	// -------------------------------------------------------------------------------------------------------
	// constructors
	// -------------------------------------------------------------------------------------------------------

	public MBeanAttributesNode(String url, Map<String, Object> model, MBeanAttributeInfo[] attributes){
		updateMBeanAttributesJsTreeNode(this)
		.withMBeanAttributeInfos(attributes)
		.withType(METADATA_VALUE_TYPE_MBEAN_ATTRIBUTE_LIST)
		.withUrl(url)
		.withModel(model)
		.withTitle("Attributes")
		.build();
	}
	
	// -------------------------------------------------------------------------------------------------------
	// inner classes
	// -------------------------------------------------------------------------------------------------------

	public static class Builder extends JMXNode.Builder {
		
		protected Builder(MBeanAttributesNode node){
			super(node);
		}
		
		public Builder withMBeanAttributeInfos(MBeanAttributeInfo[] attributes){
			for(MBeanAttributeInfo mBeanAttributeInfo : attributes){
				withMBeanAttributeInfo(mBeanAttributeInfo);
			}
			return this;
		}

		public Builder withMBeanAttributeInfo(MBeanAttributeInfo mBeanAttributeInfo) {
			add(new MBeanAttributeInfoNode(mBeanAttributeInfo));
			return this;
		}
	}
}
