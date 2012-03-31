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
public class FolderNode extends JMXNode {
	public final static String METADATA_VALUE_TYPE_FOLDER = "folder";

	public FolderNode(String id, String title, Map<String, Object> model){
		updateJMXNode(this)
		.withModel(model)
		.withType(METADATA_VALUE_TYPE_FOLDER)
		.withId(id)
		.withTitle(title)
		.build();
	}
}
