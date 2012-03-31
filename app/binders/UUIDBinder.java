package binders;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.UUID;

import play.data.binding.Global;
import play.data.binding.TypeBinder;
import play.data.validation.Validation;

@Global
public class UUIDBinder implements TypeBinder<UUID> {

	@Override
	public Object bind(
			String name, 
			Annotation[] annotations, 
			String value,
			Class actualClass, 
			Type genericType) 
	{
		try {
			return UUID.fromString(value);
		} catch (IllegalArgumentException iaExc){
			Validation.addError(name, "Invalid UUID string: "+value);
			return null;
		}
	}
 
}
