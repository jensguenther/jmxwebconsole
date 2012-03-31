/**
 * 
 */
package utils;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author jguenther
 *
 */
public class RunTimeTypeCollectionAdapterTest {

	@Test
	public void testGsonBug231(){
		Container container = new Container(new SubClass("baseField", "subClassField"));
		
		// this tests the existence of the bug, note that the SubClass.subClassField won't be serialized
		assertEquals("{\"bases\":[{\"baseField\":\"baseField\"}]}", new Gson().toJson(container));
	}
	
	@Test
	public void testGsonBug231_workaroundWithCustomCollectionJsonSerializer(){
		Container container = new Container(new SubClass("baseField", "subClassField"));
		
		Gson gson =	new GsonBuilder()
			.registerTypeHierarchyAdapter(Collection.class, new RunTimeTypeCollectionAdapter())
			.create();
		
		assertEquals("{\"bases\":[{\"subClassField\":\"subClassField\",\"baseField\":\"baseField\"}]}", gson.toJson(container));
		
	}
	
	// -------------------------------------------------------------------------------------------------------
	// inner classes
	// -------------------------------------------------------------------------------------------------------

	private static class Container {
		private List<Base> bases = new ArrayList<Base>(1);
		
		Container(Base base){
			bases.add(base);
		}
	}
	
	private static class Base {
		@SuppressWarnings("unused")
		private String baseField;
		
		Base(String baseField){
			this.baseField = baseField;
		}
	}
	
	private static class SubClass extends Base {
		@SuppressWarnings("unused")
		private String subClassField;
		
		SubClass(String baseField, String subClassField){
			super(baseField);
			this.subClassField = subClassField;
		}
	}
}
