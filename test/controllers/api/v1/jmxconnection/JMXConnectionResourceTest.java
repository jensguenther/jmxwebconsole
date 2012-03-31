package controllers.api.v1.jmxconnection;

import static org.junit.Assert.*;

import static play.test.FunctionalTest.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import models.api.v1.JMXConnectionModel;

import org.bouncycastle.ocsp.RespID;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.Gson;

import controllers.api.v1.APIConstants;

import play.mvc.Http.Response;
import play.test.FunctionalTest;
import utils.JMXTestUtils;
import utils.regression.ResourceTest;

public class JMXConnectionResourceTest extends ResourceTest {
	
	@BeforeClass
	public static void beforeClass() throws IOException{
		JMXTestUtils.startMBeanServer();
	}
	
	@After
	public void after(){
		// some tests store data, here we delete that
		for(JMXConnectionModel connection : GET_JSON(APIConstants.URI_ROOT_JMXCONNECTION, JMXConnectionModel[].class)){
			DELETE(APIConstants.URI_ROOT_JMXCONNECTION + "/" + connection.id.toString());
		}
	}
	
	// -------------------------------------------------------------------------------------------------------
	// Tests
	// -------------------------------------------------------------------------------------------------------

	@Test
	public void testList_whenNothingWasCached(){
		// when
		JMXConnectionModel[] connections = GET_JSON(APIConstants.URI_ROOT_JMXCONNECTION, JMXConnectionModel[].class);
		
		//then
        assertTrue(connections.length == 0);
	}

	@Test
	public void testList_whenSomethingWasCached(){
		JMXConnectionModel connection = createJMXConnectionModel();
		
		// when
		connection = POST_JSON(APIConstants.URI_ROOT_JMXCONNECTION, connection, JMXConnectionModel.class);
		JMXConnectionModel[] connections = GET_JSON(APIConstants.URI_ROOT_JMXCONNECTION, JMXConnectionModel[].class);
		
		//then
        assertTrue(connections.length == 1);
        assertEquals(connection, connections[0]);
	}
	
	@Test
	public void testPost(){
		JMXConnectionModel connection = createJMXConnectionModel();
		
		// when
		JMXConnectionModel createdConn = POST_JSON(APIConstants.URI_ROOT_JMXCONNECTION, connection, JMXConnectionModel.class);
		
		// then
		assertNotNull(createdConn.id);
		assertEquals(createdConn.host, connection.host);
		assertEquals(createdConn.port, connection.port);
	}
	
	@Test
	public void testGet_whenWasNotPosted(){
		UUID uuid = UUID.randomUUID();
		
		// when
        Response response = GET(APIConstants.URI_ROOT_JMXCONNECTION+"/"+uuid.toString());
        
        // then
        assertStatus(404, response);
	}
	
	@Test
	public void testGet_whenWasPosted(){
		JMXConnectionModel connection = createJMXConnectionModel();
		
		// when
		JMXConnectionModel createdConn = POST_JSON(APIConstants.URI_ROOT_JMXCONNECTION, connection, JMXConnectionModel.class);
        connection = GET_JSON(APIConstants.URI_ROOT_JMXCONNECTION + "/" + createdConn.id.toString(), JMXConnectionModel.class);
        
        // then
        assertNotNull(connection);
	}
	
	@Test
	public void testGet_whenWasDeleted(){
		JMXConnectionModel connection = createJMXConnectionModel();
		
		// when
		JMXConnectionModel createdConn = POST_JSON(APIConstants.URI_ROOT_JMXCONNECTION, connection, JMXConnectionModel.class);
		DELETE(APIConstants.URI_ROOT_JMXCONNECTION + "/" + createdConn.id.toString());
        Response response = GET(APIConstants.URI_ROOT_JMXCONNECTION+"/"+createdConn.id.toString());
        
        // then
        assertStatus(404, response);
	}
	
	@Test
	public void testGet_whenInvalidUUID(){
		// when
        Response response = GET(APIConstants.URI_ROOT_JMXCONNECTION + "/abc");
        
        // then
        assertStatus(400, response);
		assertEquals("Invalid UUID string: abc", response.out.toString());
	}

	@Test
	public void testDelete_whenWasNotPosted(){
		UUID uuid = UUID.randomUUID();
		
		// when
		Response response = DELETE(APIConstants.URI_ROOT_JMXCONNECTION + "/" + uuid.toString());
        
        // then
        assertStatus(404, response);
	}

	@Test
	public void testDelete_whenWasPosted(){
		JMXConnectionModel connection = createJMXConnectionModel();
		
		// when
		JMXConnectionModel createdConn = POST_JSON(APIConstants.URI_ROOT_JMXCONNECTION, connection, JMXConnectionModel.class);
		Response response = DELETE(APIConstants.URI_ROOT_JMXCONNECTION + "/" + createdConn.id.toString());
        
        // then
        assertStatus(200, response);
	}
	
	@Test
	public void testDelete_whenInvalidUUID(){
		// when
		Response response = DELETE(APIConstants.URI_ROOT_JMXCONNECTION + "/abc" );
		
		// then
		assertStatus(400, response);
		assertEquals("Invalid UUID string: abc", response.out.toString());
	}
	
	// -------------------------------------------------------------------------------------------------------
	// factory
	// -------------------------------------------------------------------------------------------------------

	private JMXConnectionModel createJMXConnectionModel() {
		JMXConnectionModel connection = JMXTestUtils.createJMXConnection();
		connection.id = null;
		return connection;
	}

	// -------------------------------------------------------------------------------------------------------
	// Framework
	// -------------------------------------------------------------------------------------------------------
}
