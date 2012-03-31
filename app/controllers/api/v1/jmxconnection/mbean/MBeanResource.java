/**
 * 
 */
package controllers.api.v1.jmxconnection.mbean;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;

import models.api.v1.JMXConnectionModel;
import models.jstree.JsTreeNode;
import play.data.validation.Validation;
import play.libs.Codec;
import controllers.ResourceController;

/**
 * @author jguenther
 *
 */
public class MBeanResource extends ResourceController {

	public static void list(UUID connId, String mBeanId){
    	if(Validation.hasErrors()){
    		badRequest();
    	}
    	
    	JMXConnectionModel connection = JMXConnectionModel.findById(connId);
    	if(connection == null){
			notFound("no such JMXConnection: "+connId.toString());
    	}
    	
    	ObjectName mBeanObjectName = decodeObjectName(mBeanId);
    	MBeanInfo mBeanInfo = getMBeanInfo(connection, mBeanObjectName);
    	String baseUrl = "/api/v1/jmxconnection/"+connId+"/mbean/"+mBeanId+"/";
    	renderJSON(createMBeanInfoJMXNodes(baseUrl, connId.toString(), mBeanId, mBeanInfo));
	}

	public static void attributes(UUID connId, String mBeanId){
    	if(Validation.hasErrors()){
    		badRequest();
    	}
    	
    	JMXConnectionModel connection = JMXConnectionModel.findById(connId);
    	if(connection == null){
			notFound("no such JMXConnection: "+connId.toString());
    	}
    	
    	ObjectName mBeanObjectName = decodeObjectName(mBeanId);
    	MBeanInfo mBeanInfo = getMBeanInfo(connection, mBeanObjectName);
    	AttributeList attributeList = getAttributeList(connection, mBeanObjectName, mBeanInfo.getAttributes());
    	
    	renderJSON( createMBeanAttributeModels(connId.toString(), mBeanId, attributeList));
	}
	
	// -------------------------------------------------------------------------------------------------------
	// internal
	// -------------------------------------------------------------------------------------------------------
	
	/**
	 * FIXME: DRY: MBeanResource, MBeanWebSocket
	 *  
	 * @param connId
	 * @param mBeanId
	 * @param attributeList
	 * @return
	 */
	private static List<MBeanAttributeModel> createMBeanAttributeModels(
			String connId,
			String mBeanId,
			AttributeList attributeList)
	{
		List<MBeanAttributeModel> mBeanAttributeModels = new LinkedList<MBeanAttributeModel>();
		for(Attribute a : attributeList.asList()){
			mBeanAttributeModels.add(new MBeanAttributeModel(connId, mBeanId, a.getName(), a.getValue()));
		}
		return mBeanAttributeModels;
	}
	
	private static List<JsTreeNode> createMBeanInfoJMXNodes(
			String baseUrl,
			String connId,
			String mBeanId,
			MBeanInfo mBeanInfo)
	{
		Map<String, Object> model = createMBeanModel(connId, mBeanId);
		List<JsTreeNode> roots = new LinkedList<JsTreeNode>();
		
		MBeanAttributeInfo[] attributes = mBeanInfo.getAttributes();
		if(attributes != null && attributes.length > 0){
			roots.add(new MBeanAttributesNode(baseUrl+"attributes", model, attributes));
		}
		
		MBeanOperationInfo[] operations = mBeanInfo.getOperations();
		if(operations != null && operations.length > 0){
			roots.add(new MBeanOperationsNode(baseUrl+"operations", operations));
		}
		
		// Fixme: whether or not a MBean should have a notifications part depends on the MBean implementing NotificationBroadcaster
		MBeanNotificationInfo[] notifications = mBeanInfo.getNotifications();
		if(notifications != null && notifications.length > 0){
			roots.add(new MBeanNotificationsNode(baseUrl+"notifications"));
		}
		
		return roots;
	}
	
	private static Map<String, Object> createMBeanModel(String connId, String mBeanId) {
		Map<String, Object> model = new HashMap<String, Object>(2);
		model.put("connId", connId);
		model.put("mBeanId", mBeanId);
		
		return model;
	}

	/**
	 * FIXME: DRY: MBeanResource, MBeanWebSocket
	 * 
	 * @param connection
	 * @param mBeanObjectName
	 * @return
	 */
	private static MBeanInfo getMBeanInfo(JMXConnectionModel connection, ObjectName mBeanObjectName){
		try {
			return connection.getMBeanServerConnection().getMBeanInfo(mBeanObjectName);
		} catch (Exception e) {
			error(e);
			// won't be reached.
			return null;
		}
	}
	
	/**
	 * FIXME: DRY: MBeanResource, MBeanWebSocket
	 *  
	 * @param connection
	 * @param mBeanObjectName
	 * @param mBeanAttributeInfos
	 * @return
	 */
	private static AttributeList getAttributeList(
			JMXConnectionModel connection, 
			ObjectName mBeanObjectName, 
			MBeanAttributeInfo[] mBeanAttributeInfos)
	{
		// extract the attribute names
		String[] attributes = new String[mBeanAttributeInfos.length];
		for(int i = 0; i < mBeanAttributeInfos.length; i++){
			attributes[i] = mBeanAttributeInfos[i].getName();
		}
		
		try {
			// get the AttributeList
			return connection.getMBeanServerConnection().getAttributes(mBeanObjectName, attributes);
		} catch (Exception e) {
			error(e);
			// won't be reached.
			return null;
		}
	}
	
	/**
	 * FIXME: DRY: MBeanResource, MBeanWebSocket
	 * 
	 * @param mBeanId
	 * @return
	 */
	private static ObjectName decodeObjectName(String mBeanId) {
        try {
            return ObjectName.getInstance(new String(Codec.decodeBASE64(mBeanId)));
        } catch (MalformedObjectNameException e) {
            Validation.addError("mBeanId", "Invalid ObjectName: "+new String(Codec.decodeBASE64(mBeanId)));
            badRequest();
            // won't be reached as badRequest throws a Result
            return null;
		}
	}
}
