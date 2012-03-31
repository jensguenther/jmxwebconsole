/**
 * 
 */
package results;

import java.util.Collection;

import com.google.gson.GsonBuilder;

import utils.RunTimeTypeCollectionAdapter;

/**
 * @author jguenther
 *
 */
public class RenderJson extends play.mvc.results.RenderJson {

	public RenderJson(Object o){
		// FIXME: DRY: RenderJson, MBeanWebSocket
		super(new GsonBuilder()
				.registerTypeHierarchyAdapter(Collection.class, new RunTimeTypeCollectionAdapter())
				.disableHtmlEscaping()
				.create()
				.toJson(o));
	}
}
