package models.api.v1;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;


import play.Logger;
import play.cache.Cache;
import play.data.validation.Validation;

public class JMXConnectionModel {

	public static String CONNECTION_TTL = "15mn";
	
	private static final Map<UUID,JMXConnectionModel> ALL_CONNECTIONS = new ConcurrentHashMap<UUID, JMXConnectionModel>(1000, 0.8f, 32);
	
	public static Collection<JMXConnectionModel> findAll(){
		for(JMXConnectionModel connection : ALL_CONNECTIONS.values()){
			if(Cache.get(connection.id.toString()) == null ){
				ALL_CONNECTIONS.remove(connection.id);
			}
		}
		return ALL_CONNECTIONS.values();
	}
	
	public static JMXConnectionModel findById(UUID uuid){
		if(Cache.get(uuid.toString()) != null){
			Cache.set(uuid.toString(), uuid, CONNECTION_TTL);
		} else {
			JMXConnectionModel connection = ALL_CONNECTIONS.remove(uuid);
			if(connection != null){
				// TODO: Close the connection whenever the CONNECTION_TTL time was exceeded
				connection.close();
			}
		}
		
		return ALL_CONNECTIONS.get(uuid);
	}

	// -------------------------------------------------------------------------------------------------------
	// attributes
	// -------------------------------------------------------------------------------------------------------

	transient private final Object lock = new Object();
	
	public UUID id;
	public String host;
	public Integer port;
	
	transient protected JMXConnector jmxConnector;
	transient protected MBeanServerConnection mBeanServerConnection;
	
	// -------------------------------------------------------------------------------------------------------
	// methods - connection
	// -------------------------------------------------------------------------------------------------------

	/**
	 * Returns a lazy created {@link MBeanServerConnection}. Once created and open, subsequential calls will
	 * return the same {@link MBeanServerConnection}.   
	 * 
	 * @return the {@link MBeanServerConnection}
	 * 
	 * @throws IOException
	 * 	if there was a problem in establishing the connection
	 */
	public MBeanServerConnection getMBeanServerConnection()
	throws IOException
	{
		if(mBeanServerConnection == null){
			synchronized (lock) {
				if(mBeanServerConnection == null){
					connect();
				}
			}
		}
		
		return mBeanServerConnection;
	}
	
	private void connect()
	throws IOException
	{
		IOException ioExc = null;
		try {
			JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + this.host + ":" + this.port + "/jmxrmi");
			jmxConnector = JMXConnectorFactory.connect(url, null);
			mBeanServerConnection = jmxConnector.getMBeanServerConnection();
			
			return;
			
		} catch (IOException e) {
			ioExc = e;
		}
		
		mBeanServerConnection = null;
		if(jmxConnector != null){
			try {
				jmxConnector.close();
			} catch (IOException e){
				// not much we can do against that
				Logger.error(e, "could not close jmxConnector which failed before because of: "+ ioExc.getMessage());
			}
			jmxConnector = null;
		}
		
		throw ioExc;
	}
	
	private void close(){
		mBeanServerConnection = null;
		if(jmxConnector != null){
			try {
				jmxConnector.close();
			} catch (IOException e){
				// not much we can do against that
				Logger.error(e, "could not close jmxConnector");
			}
			jmxConnector = null;
		}
	}
	
	// -------------------------------------------------------------------------------------------------------
	// Domains
	// -------------------------------------------------------------------------------------------------------

	public String[] getDomains() throws IOException {
		return getMBeanServerConnection().getDomains();
	}

	// -------------------------------------------------------------------------------------------------------
	// MBeans
	// -------------------------------------------------------------------------------------------------------

	public Set<ObjectName> getObjectNames(String domain) throws MalformedObjectNameException, IOException {
		ObjectName namePattern = 
				ObjectName.getInstance(domain == null || domain.isEmpty()? "*:*" : domain + ":*");
		return getMBeanServerConnection().queryNames(namePattern, null);
	}
	
	// -------------------------------------------------------------------------------------------------------
	// methods - storage
	// -------------------------------------------------------------------------------------------------------

	public void putToCache(){
		// TODO: Close the connection whenever the CONNECTION_TTL time was exceeded
		Cache.set(id.toString(), id, CONNECTION_TTL);
		ALL_CONNECTIONS.put(this.id, this);
	}
	
	public void removeFromCache(){
		close();
		Cache.delete(this.id.toString());
		ALL_CONNECTIONS.remove(this.id);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((host == null) ? 0 : host.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((port == null) ? 0 : port.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JMXConnectionModel other = (JMXConnectionModel) obj;
		if (host == null) {
			if (other.host != null)
				return false;
		} else if (!host.equals(other.host))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (port == null) {
			if (other.port != null)
				return false;
		} else if (!port.equals(other.port))
			return false;
		return true;
	}
	
	
}
