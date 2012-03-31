/**
 * 
 */
package controllers;

import play.data.validation.Validation;
import play.mvc.Controller;
import play.mvc.Finally;
import results.RenderJson;

/**
 * @author jguenther
 *
 */
public class ResourceController extends Controller {
	
	@Finally
	public static void checkValidationErrors(){
    	if(Validation.hasErrors()){
    		for(play.data.validation.Error error : Validation.errors()){
    			response.print(error.message());
    		}
    	}
	}
	
	protected static void renderJSON(Object o){
    	throw new RenderJson(o);		
	}

}
