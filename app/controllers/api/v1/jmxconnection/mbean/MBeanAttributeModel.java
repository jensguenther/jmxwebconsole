/**
 * 
 */
package controllers.api.v1.jmxconnection.mbean;

import java.util.HashMap;
import java.util.Map;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.ObjectInstance;
import javax.management.ReflectionException;

import play.mvc.results.Error;

/**
 * @author jguenther
 *
 */
public class MBeanAttributeModel {

	// -------------------------------------------------------------------------------------------------------
	// static
	// -------------------------------------------------------------------------------------------------------

	public static String createId(String connId, String mBeanId, String name) {
		return "jmxconnection/"+connId+"/mbean/"+mBeanId+"/attributes/"+name;
	}

	// -------------------------------------------------------------------------------------------------------
	// attributes
	// -------------------------------------------------------------------------------------------------------

	@SuppressWarnings("unused")
	private final String id;
	@SuppressWarnings("unused")
	private final String connId;
	@SuppressWarnings("unused")
	private final String mBeanId;
	@SuppressWarnings("unused")
	private final String name;
	@SuppressWarnings("unused")
	private Object value;

	// -------------------------------------------------------------------------------------------------------
	// constructor
	// -------------------------------------------------------------------------------------------------------

	public MBeanAttributeModel(
			String connId,
			String mBeanId,
			String name,
			Object value)
	{
		this.id = createId(connId, mBeanId, name);
		this.connId = connId;
		this.mBeanId = mBeanId;
		this.name = name;
		this.value = value;
	}
}
