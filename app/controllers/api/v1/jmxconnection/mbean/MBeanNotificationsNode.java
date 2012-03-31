/**
 * 
 */
package controllers.api.v1.jmxconnection.mbean;

import models.jstree.JMXNode;
import models.jstree.JsTreeNode;

/**
 * @author jguenther
 *
 */
public class MBeanNotificationsNode extends JMXNode {

	public MBeanNotificationsNode(String url){
		updateJMXNode(this)
		.withUrl(url)
		.withTitle("Notifications")
		.build();
	}
}