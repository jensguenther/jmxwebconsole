/**
 * 
 */
package controllers.api.v1.jmxconnection.mbean;

import javax.management.MBeanOperationInfo;

import models.jstree.JMXNode;

/**
 * @author jguenther
 *
 */
public class MBeanOperationsNode extends JMXNode {

	// -------------------------------------------------------------------------------------------------------
	// static
	// -------------------------------------------------------------------------------------------------------

	public static Builder updateMBeanOperationsJsTreeNode(MBeanOperationsNode node){
		return new Builder(node);
	}
	
	// -------------------------------------------------------------------------------------------------------
	// constructor
	// -------------------------------------------------------------------------------------------------------

	public MBeanOperationsNode(String url, MBeanOperationInfo[] operations){
		updateMBeanOperationsJsTreeNode(this)
		.withMBeanOperationInfos(operations)
		.withUrl(url)
		.withTitle("Operations")
		.build();
	}
	
	// -------------------------------------------------------------------------------------------------------
	// inner classes
	// -------------------------------------------------------------------------------------------------------

	public static class Builder extends JMXNode.Builder {
		
		protected Builder(MBeanOperationsNode node){
			super(node);
		}
		
		public Builder withMBeanOperationInfos(MBeanOperationInfo[] operations){
			for(MBeanOperationInfo mBeanOperationInfo : operations){
				withMBeanOperationInfo(mBeanOperationInfo);
			}
			return this;
		}
		
		public Builder withMBeanOperationInfo(MBeanOperationInfo mBeanOperationInfo){
			add(new MBeanOperationInfoNode(mBeanOperationInfo));
			return this;
		}
	}
}