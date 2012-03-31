package models.api.v1;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.registry.LocateRegistry;
import java.util.Arrays;
import java.util.UUID;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

import models.api.v1.JMXConnectionModel;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import play.cache.Cache;
import play.cache.EhCacheImpl;
import utils.JMXTestUtils;

public class JMXConnectionModelTest {

	private static final String CONNECTION_TTL_ORIG = JMXConnectionModel.CONNECTION_TTL;
	
	private JMXConnectionModel connection;
	
	@BeforeClass
	public static void beforeClass() throws IOException{
		// the JMXConnectionModel manages its instances within the play cache, so that might needs to be
		// initialized before
		if(Cache.cacheImpl == null){
			Cache.cacheImpl = EhCacheImpl.newInstance();
		}
		
		// the tests need to have a remotely accessible MBeanServer available, so we start one
		JMXTestUtils.startMBeanServer();
	}
	
	@Before
	public void before(){
		// most tests need a fresh JMXConnectionModel
		connection = JMXTestUtils.createJMXConnection();
	}
	
	@After
	public void after(){
		// in some of the tests we set the cache expiration, here we clear it
		JMXConnectionModel.CONNECTION_TTL = CONNECTION_TTL_ORIG;
		
		// some tests put data into the cache, here we clear it
		for(JMXConnectionModel connection : JMXConnectionModel.findAll()){
			connection.removeFromCache();
		}
	}
	
	// -------------------------------------------------------------------------------------------------------
	// CRUD tests
	// -------------------------------------------------------------------------------------------------------

	@Test
	public void testPutToCache(){
		// when
		connection.putToCache();
		
		// then
		// ok, we simply expect no exception
	}
	
	@Test
	public void testRemoveFromCache_whenWasCached(){		
		// when
		connection.putToCache();
		connection.removeFromCache();
		
		// then
		// ok, we simply expect no exception
	}
	
	@Test
	public void testRemoveFromCache_whenNotWasCached(){
		// when
		connection.removeFromCache();
		
		// then
		// ok, we simply expect no exception
	}
	
	@Test
	public void testFindAll_whenNothingWasCached(){
		// when
		// nothing was cached
		
		// then
		assertTrue(JMXConnectionModel.findAll().isEmpty());
	}
	
	@Test
	public void testFindAll_whenSomethingWasCached(){
		// when
		connection.putToCache();
		
		// then
		assertFalse(JMXConnectionModel.findAll().isEmpty());
	}
	
	@Test
	public void testFindById_whenWasRemovedFromCache(){
		// when
		connection.putToCache();
		connection.removeFromCache();
		
		// then
		assertNull(JMXConnectionModel.findById(connection.id));
	}
	
	@Test
	public void testFindById_whenNotExpiredYet(){
		// when
		connection.putToCache();
		
		// then
		assertSame(connection, JMXConnectionModel.findById(connection.id));
	}
	
	@Test
	public void testFindById_whenAlreadyExpired() throws InterruptedException{
		// when
		JMXConnectionModel.CONNECTION_TTL = "1s";
		connection.putToCache();
		Thread.currentThread().sleep(1001);
		
		// then
		assertNull(JMXConnectionModel.findById(connection.id));
	}
	
	@Test
	public void testFindById_closesConnectionWhenExpired() throws InterruptedException, IOException{
		// when
		JMXConnectionModel.CONNECTION_TTL = "1s";
		connection.putToCache();
		// and
		connection.getMBeanServerConnection(); // creates the conn 
		Thread.currentThread().sleep(1001);
		assertNotNull(connection.mBeanServerConnection);
		
		// then
		assertNull(JMXConnectionModel.findById(connection.id));
		assertNull(connection.mBeanServerConnection);
	}
	
	// -------------------------------------------------------------------------------------------------------
	// DomainModel tests
	// -------------------------------------------------------------------------------------------------------

	@Test
	public void testGetDomainModels() throws IOException {
		String domain = "JMXConnectionModelTest.testGetDomainModels";
		
		// when
		JMXTestUtils.addMBeanServerDomain(domain);
		
		// then
		assertTrue(Arrays.asList(connection.getDomains()).contains(domain));
	}
	
	@Test
	public void testGetMBeanNames() throws IOException, MalformedObjectNameException {
		// when
		String domain = connection.getDomains()[0];
		
		// then
		assertFalse(connection.getObjectNames(domain).isEmpty());
	}
}
