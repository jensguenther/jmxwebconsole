package binders;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.UUID;

import models.api.v1.JMXConnectionModel;

import com.google.gson.Gson;


import play.data.binding.Global;
import play.data.binding.TypeBinder;

@Global
public class JMXConnectionModelBinder implements TypeBinder<JMXConnectionModel> {

	@Override
	public Object bind(
			String name, 
			Annotation[] annotations, 
			String value,
			Class actualClass, 
			Type genericType) 
	throws Exception {
		return new Gson().fromJson(value, JMXConnectionModel.class);
	}
}
