package com.iarchives.swf.direct.workflow;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {

	public static Map<String, Object> fromString(String json) throws RuntimeException {
		
		Map<String, Object> map = null;
		
		if (json != null) {
			ObjectMapper mapper = new ObjectMapper();
			try {
				map = mapper.readValue(json, Map.class);
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		
		if (map == null) {
			map = new HashMap<String, Object>();
		}
		return map;
	}
	
	public static String toString(Map<String, Object> inputMap) throws RuntimeException {
		
		String json = null;

		if (inputMap != null && !inputMap.isEmpty()) {
			ObjectMapper mapper = new ObjectMapper();
			try {
				json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(inputMap);
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		} else {
			json = "";
		}
		
		return json;
	}

}
