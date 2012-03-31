/**
 * 
 */
package controllers.api.v1.jmxconnection.domain;

import java.util.Map;

import models.jstree.JMXNode;
import models.jstree.JsTreeNode;

/**
 * @author jguenther
 *
 */
public class MBeanNode extends JMXNode {
	public final static String METADATA_VALUE_TYPE_MBEAN = "mBean";

	public MBeanNode(String id, String title, String url, Map<String, Object> model){
		updateJMXNode(this)
		.withModel(model)
		.withType(METADATA_VALUE_TYPE_MBEAN)
		.withUrl(url)
		.withId(id)
		.withTitle(title)
		.withoutChildren()
		.withState(State.CLOSED)
		.build();
	}
}