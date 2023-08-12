package com.example.Resturant.Management.System.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.Data;
import net.bytebuddy.description.method.MethodDescription;
import org.apache.logging.log4j.util.Strings;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import net.bytebuddy.description.method.MethodDescription.TypeToken;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RestaurantUtils {

    private RestaurantUtils(){

    }
    public static ResponseEntity<String>getResponseEntity(String responseMessage, HttpStatus httpStatus){
        return new ResponseEntity<String>("{\"message\":\""+responseMessage+"\"}", httpStatus);
    }

    public static String getUUID(){
        Date date=new Date();
        long time= date.getTime();
        return "Bill-" + time;
    }
    public static JSONArray getJsonArrayFromString(String data) throws JSONException{
        JSONArray jsonArray = new JSONArray(data);
        return jsonArray;
    }
    public static Map<String,Object> getMapFromJson(String data){
        if (!Strings.isEmpty(data))
            return new Gson().fromJson(data,new TypeToken<Map<String,Object>>(){
        }.getType());
        return new HashMap<>();
    }

}
