package controllers.api.v1.jmxconnection;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.UUID;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import controllers.ResourceController;

import models.api.v1.JMXConnectionModel;

import play.data.validation.Validation;
import play.mvc.*;
import play.mvc.results.BadRequest;

public class JMXConnectionResource extends ResourceController {
	
    public static void list() {
        renderJSON(JMXConnectionModel.findAll());
    }
    
    public static void post(JMXConnectionModel body) {
    	// test connection
        try {
        	body.getMBeanServerConnection();
        } catch(IOException e){
        	Validation.addError("jmxConnectionModel", e.getMessage());
        	return;
        }
         
    	body.id = UUID.randomUUID();
    	body.putToCache();
    	
    	response.status = 201;
    	renderJSON(body);
    	
    }

    public static void get(UUID connId){
    	if(Validation.hasErrors()){
    		badRequest();
    	}
    	
    	JMXConnectionModel connection = JMXConnectionModel.findById(connId);
    	if(connection != null){
            renderJSON(connection);
    	} else {
    		notFound();
    	}
    }
    
    public static void delete(UUID connId){
    	if(Validation.hasErrors()){
    		badRequest();
    	}
    	
    	JMXConnectionModel connection = JMXConnectionModel.findById(connId);
    	if(connection != null){
    		connection.removeFromCache();
    	} else {
    		notFound();
    	}
    }
}
