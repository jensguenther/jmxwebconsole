/**
 * 
 */
package controllers.api.v1.jmxconnection.mbean;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import models.api.v1.JMXConnectionModel;

import org.apache.commons.lang.exception.ExceptionUtils;

import com.google.gson.GsonBuilder;

import play.Logger;
import play.data.validation.Validation;
import play.jobs.Job;
import play.libs.Codec;
import play.libs.F.Either;
import play.libs.F.Promise;
import play.mvc.Http.WebSocketEvent;
import play.mvc.Http.WebSocketFrame;
import play.mvc.WebSocketController;
import utils.RunTimeTypeCollectionAdapter;

/**
 * @author jguenther
 *
 */
public class MBeanWebSocket extends WebSocketController {

	/**
	 * Output a MBean's attributes
	 */
	public static void attributes(UUID connId, String mBeanId) {
		doAttributes(connId, mBeanId, null);
	}

	/**
	 * Outputs a MBean's attribute 
	 */
	public static void attribute(UUID connId, String mBeanId, String name){
		doAttributes(connId, mBeanId, new String[]{name});
	}
	
	private static void doAttributes(UUID connId, String mBeanId, String[] names){
    	if(Validation.hasErrors()){
    		Logger.error("Could not create WebSocket MBeanWebSocket.attributes: one or multiple validation errors. Disconnecting.");
    		disconnect();
    	}
    	
    	JMXConnectionModel connection = JMXConnectionModel.findById(connId);
    	if(connection == null){
    		Logger.error("Could not create WebSocket MBeanWebSocket.attributes: no such JMXConnection: "+connId.toString()+". Disconnecting.");
    		disconnect();
    	}
    	
    	ObjectName mBeanObjectName = decodeObjectName(mBeanId);
    	MBeanInfo mBeanInfo = getMBeanInfo(connection, mBeanObjectName);

		TimerJob tj = new TimerJob(connection, mBeanId, mBeanObjectName, mBeanInfo, names);
		while (inbound.isOpen() && outbound.isOpen()) {
			try {
				Either<String, WebSocketEvent> data = await(Promise.waitEither(tj.now(), inbound.nextEvent()));
				if(data._1.isDefined()){
					outbound.send(data._1.get());
				} else {
					WebSocketEvent e = data._2.get();
					if(e instanceof WebSocketFrame && ((WebSocketFrame)e).textData.equals("close")){
						disconnect();						
					}
				}
			} catch (Throwable t) {
				Logger.error(ExceptionUtils.getStackTrace(t));
			}
		}
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
			MBeanAttributeInfo[] mBeanAttributeInfos,
			String[] attributeNames )
	{
		// extract the attribute names
		String[] attributes = new String[mBeanAttributeInfos.length];
		List<String> aNames = (attributeNames == null)? null : Arrays.asList(attributeNames);
		int c = 0;
		for(int i = 0; i < mBeanAttributeInfos.length; i++){
			String actName = mBeanAttributeInfos[i].getName();
			if(aNames == null || aNames.contains(actName)){
				attributes[c] = actName;
				c++;
			}
		}
		
		try {
			// get the AttributeList
			return connection.getMBeanServerConnection().getAttributes(mBeanObjectName, attributes);
		} catch (Exception e) {
    		Logger.error("Could not create WebSocket MBeanWebSocket.attributes: exception: "+e.getMessage()+". Disconnecting.");
    		disconnect();
			throw new RuntimeException(e);
		}
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
    		Logger.error("Could not create WebSocket MBeanWebSocket.attributes: exception: "+e.getMessage()+". Disconnecting.");
    		disconnect();
			throw new RuntimeException(e);
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
    		Logger.error("Could not create WebSocket MBeanWebSocket.attributes: exception: "+e.getMessage()+". Disconnecting.");
    		disconnect();
			throw new RuntimeException(e);
		}
	}

	private static class TimerJob extends Job<String> {

		
		private JMXConnectionModel connection;
		private ObjectName mBeanObjectName;
		private MBeanInfo mBeanInfo;
		private String mBeanId;
		private String[] attributeName;

		TimerJob(JMXConnectionModel connection, String mBeanId, ObjectName mBeanObjectName, MBeanInfo mBeanInfo, String[] attributeName ){
			this.connection = connection;
			this.mBeanId = mBeanId;
			this.mBeanObjectName = mBeanObjectName;
			this.mBeanInfo = mBeanInfo;
			this.attributeName = attributeName;
		}
		
		@Override
		public String doJobWithResult() throws InterruptedException {
			Thread.sleep(2000);
	    	AttributeList attributeList = 
	    			getAttributeList(connection, mBeanObjectName, mBeanInfo.getAttributes(), attributeName);
	    	
	    	return renderJSON( createMBeanAttributeModels(connection.id.toString(), mBeanId, attributeList));
		}

		/**
		 * @param createMBeanAttributeModels
		 * @return
		 */
		private String renderJSON(List<MBeanAttributeModel> createMBeanAttributeModels) {
			// FIXME: DRY: RenderJson, MBeanWebSocket
			return new GsonBuilder()
				.registerTypeHierarchyAdapter(Collection.class, new RunTimeTypeCollectionAdapter())
				.disableHtmlEscaping()
				.create()
				.toJson(createMBeanAttributeModels);
		}
	}
}
