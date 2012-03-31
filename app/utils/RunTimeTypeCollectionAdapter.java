/**
 * 
 */
package utils;

import java.lang.reflect.Type;
import java.util.Collection;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Copied shameless from http://code.google.com/p/google-gson/issues/detail?id=231#c30
 * 
 * @author jguenther
 *
 */
public class RunTimeTypeCollectionAdapter implements JsonSerializer<Collection> {

	@Override
	public JsonElement serialize(Collection src, Type typeOfSrc,
			JsonSerializationContext context) {
	      if (src == null) {
	          return null;
	        }
	        JsonArray array = new JsonArray();
	        for (Object child : src) {
	            JsonElement element = context.serialize(child);
	            array.add(element);
	        }
	        return array;
	}
	
}