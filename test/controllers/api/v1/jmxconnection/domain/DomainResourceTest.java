/**
 * 
 */
package controllers.api.v1.jmxconnection.domain;

import java.io.IOException;
import java.util.Arrays;

import models.api.v1.JMXConnectionModel;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import controllers.api.v1.APIConstants;
import play.mvc.Http.Response;
import play.test.FunctionalTest;
import utils.JMXTestUtils;
import utils.regression.ResourceTest;

/**
 * @author jguenther
 *
 */
public class DomainResourceTest extends ResourceTest {
	
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
	public void testList_whenInvalidConnectionId(){
		// when
		Response response  = GET(APIConstants.URI_ROOT_JMXCONNECTION+"/abc/domain");
		
		// then
		assertStatus(400,response);
		assertEquals("Invalid UUID string: abc", response.out.toString());
	}

	@Test
	public void testList_whenUnknownConnectionId(){
		// when
		Response response  = GET(APIConstants.URI_ROOT_JMXCONNECTION+"/955f5320-54e2-11e1-b86c-0800200c9a66/domain");
		
		// then
		assertStatus(404,response);
		// we don't check the response body as we use the 404 template
	}
	
	@Test
	public void testList(){
		String domain = "DomainResourceTest.testList";
		// when
		JMXTestUtils.addMBeanServerDomain(domain);
		
		JMXConnectionModel conn = POST_JSON(
				APIConstants.URI_ROOT_JMXCONNECTION, 
				JMXTestUtils.createJMXConnection(), 
				JMXConnectionModel.class);
		
		String[] domains = GET_JSON(
				APIConstants.URI_ROOT_JMXCONNECTION+"/"+conn.id.toString()+"/domain", 
				String[].class);
		
		// then
		assertTrue(Arrays.asList(domains).contains(domain));
	}
}
