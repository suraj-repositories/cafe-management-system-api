package com.ang.cafe.utils;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.ang.cafe.constants.CafeConstants;
import com.google.common.base.Strings;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CafeUtils {

	private CafeUtils() {

	}

	public static ResponseEntity<String> getResponseEntity(String responseMessage, HttpStatus httpStatus) {
		return new ResponseEntity<String>("{\"message\":\"" + responseMessage + "\"}", httpStatus);
	}
	
	public static String getUUID() {
		Date date = new Date();
		long time = date.getTime();
		return CafeConstants.BILL_PREFIX + time;
	}
	
	public static JSONArray jsonArrayFromString(String data) throws JSONException {
		try {
	        return new JSONArray(data);
	    } catch (Exception e) {
	        log.error("Error parsing JSON array: {}", e.getMessage());
	        return new JSONArray(); 
	    }
	}
	
	public static Map<String, Object> getMapFromJson(String data){
		if(!Strings.isNullOrEmpty(data)) {
			return new Gson().fromJson(data, new TypeToken<Map<String, Object>>() {
				private static final long serialVersionUID = 1L;
			}.getType());
		}
		return new HashMap<>();
	}
	
	public static Boolean isFileExists(String path) {
		try {
			File file = new File(path);
			return (file != null && file.exists()) ? Boolean.TRUE : Boolean.FALSE;
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}
	
}
