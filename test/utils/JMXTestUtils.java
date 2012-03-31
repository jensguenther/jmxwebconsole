/**
 * 
 */
package utils;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.UUID;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.StandardMBean;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

import jobs.bootstrap.StartMBeanServer;

import models.api.v1.JMXConnectionModel;

/**
 * @author jguenther
 *
 */
public class JMXTestUtils {
	// -------------------------------------------------------------------------------------------------------
	// MBeanServer
	// -------------------------------------------------------------------------------------------------------

	public static void startMBeanServer() throws IOException{
		StartMBeanServer.startMBeanServer();
	}

	public static void addMBeanServerDomain(String domain) {
		try {
			TestMBean tmb = new TestMBean() {};
			StandardMBean smb = new StandardMBean(tmb, TestMBean.class);
			ObjectName oname = new ObjectName(domain, "key", "value");
			StartMBeanServer.getCurrentMBeanServer().registerMBean(smb, oname);
		} catch (Exception e){
			throw new RuntimeException(e);
		}
	}
	
	// -------------------------------------------------------------------------------------------------------
	// Factory
	// -------------------------------------------------------------------------------------------------------

	public static JMXConnectionModel createJMXConnection() {
		JMXConnectionModel connection = new JMXConnectionModel();
		connection.id = UUID.randomUUID();
		connection.host = StartMBeanServer.DEFAULT_MBEANSERVER_HOST;
		connection.port = StartMBeanServer.DEFAULT_MBEANSERVER_PORT;
		return connection;
	}

	
}
