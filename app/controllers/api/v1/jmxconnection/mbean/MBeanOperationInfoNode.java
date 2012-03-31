/**
 * 
 */
package controllers.api.v1.jmxconnection.mbean;

import javax.management.MBeanOperationInfo;

import models.jstree.JMXNode;
import models.jstree.JsTreeNode;

/**
 * @author jguenther
 *
 */
public class MBeanOperationInfoNode extends JMXNode {

	public MBeanOperationInfoNode(MBeanOperationInfo mBeanOperationInfo){
		updateJsTreeNode(this)
		.withTitle(mBeanOperationInfo.getName())
		.build();
	}
}
