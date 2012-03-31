/**
 * 
 */
package controllers.api.v1.jmxconnection.mbean;

import javax.management.MBeanAttributeInfo;

import models.jstree.JMXNode;
import models.jstree.JsTreeNode;

/**
 * @author jguenther
 *
 */
public class MBeanAttributeInfoNode extends JMXNode {

	public MBeanAttributeInfoNode(MBeanAttributeInfo mBeanAttributeInfo){
		updateJsTreeNode(this)
		.withTitle(mBeanAttributeInfo.getName())
		.build();
	}
}
