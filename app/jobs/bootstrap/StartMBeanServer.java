/**
 * 
 */
package jobs.bootstrap;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

import play.Logger;
import play.jobs.Job;
import play.jobs.OnApplicationStart;

/**
 * @author jguenther
 *
 */
@OnApplicationStart
public class StartMBeanServer extends Job {

	public final static String DEFAULT_MBEANSERVER_HOST = "localhost";
	public final static int DEFAULT_MBEANSERVER_PORT = 9999;
	
	private static volatile MBeanServer mBeanServer;
	
	@Override
	public void doJob(){
		try {
			startMBeanServer();
		} catch (IOException e) {
			Logger.error(e, "could not start MBeanServer" );
			throw new RuntimeException("could not start MBeanServer", e);
		}
	}
	
	// -------------------------------------------------------------------------------------------------------
	// MBeanServer
	// -------------------------------------------------------------------------------------------------------
	
	public static MBeanServer getCurrentMBeanServer(){
		return mBeanServer;
	}

	public static synchronized void startMBeanServer() throws IOException{
		// start rmi registry to register the MBeanServer with			
		startRMIRegistry();
		
		ArrayList<MBeanServer> regServers = MBeanServerFactory.findMBeanServer(null);
		if(!regServers.isEmpty()){
			mBeanServer = regServers.get(0);
			Logger.info("MBeanServer found");
		} else {
			mBeanServer = MBeanServerFactory.createMBeanServer();
			Logger.info("MBeanServer started");
		}

		startJMXConnectorServer();
	}
	
	private static void startJMXConnectorServer() throws IOException{
		final String serviceUrl = "service:jmx:rmi:///jndi/rmi://"+DEFAULT_MBEANSERVER_HOST+":"+DEFAULT_MBEANSERVER_PORT+"/jmxrmi";
		JMXServiceURL url = new JMXServiceURL(serviceUrl); 
		
		// test connection first, maybe it's already occupied
		if(isJMXConnectorServerRunning(url)){
			return;
		}
		
		JMXConnectorServerFactory.newJMXConnectorServer(url, null, mBeanServer).start();
		Logger.info("MBeanServer available under: "+url.toString());
		
	}
	
	private static boolean isJMXConnectorServerRunning(JMXServiceURL url) {
		try {
			JMXConnector jmxConnector = JMXConnectorFactory.connect(url, null);
			jmxConnector.close();
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	private static void startRMIRegistry() throws RemoteException{
		Registry registry = null;
		try{
			registry = LocateRegistry.getRegistry(DEFAULT_MBEANSERVER_PORT);
			// test if the stub is really available
			registry.list();
		} catch(IOException e){
			// OK, not created yet
			registry = null;
		}
		
		if(registry == null){
			LocateRegistry.createRegistry(DEFAULT_MBEANSERVER_PORT);
			Logger.info("RMIRegistry started");
		}
	}
}
