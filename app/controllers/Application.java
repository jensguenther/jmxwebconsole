package controllers;

import play.*;
import play.mvc.*;

import java.text.SimpleDateFormat;
import java.util.*;

import models.*;

public class Application extends Controller {

    public static void index() {
    	renderArgs.put("date", new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z").format(new Date()));
        render();
    }

}