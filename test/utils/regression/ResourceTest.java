/**
 * 
 */
package utils.regression;

import com.google.gson.Gson;

import controllers.api.v1.APIConstants;
import play.mvc.Http.Response;
import play.test.FunctionalTest;

/**
 * @author jguenther
 *
 */
public abstract class ResourceTest  extends FunctionalTest {

	protected <T> T GET_JSON(String url, Class<T> classOfT) {
		Response response = GET(url); 
		assertStatus(200, response);
        assertContentType(APIConstants.CONTENT_TYPE_JSON, response);
        assertCharset(play.Play.defaultWebEncoding, response);
        
		return new Gson().fromJson(response.out.toString(), classOfT);
	}
	
	protected <T> T POST_JSON(String url, Object body, Class<T> classOfT){
		Response response = POST(url, APIConstants.CONTENT_TYPE_JSON, new Gson().toJson(body));
		assertStatus(201, response);
		
		return new Gson().fromJson(response.out.toString(), classOfT);
	}

}
