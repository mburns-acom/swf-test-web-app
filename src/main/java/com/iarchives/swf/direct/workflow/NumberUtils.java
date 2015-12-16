package com.iarchives.swf.direct.workflow;

public class NumberUtils {

	public static int safeLongToInt(long l) {
	    if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
	        throw new IllegalArgumentException
	            (l + " cannot be cast to int without changing its value.");
	    }
	    return (int) l;
	}
	
	public static Long safeLong(Object obj) {
		if (obj instanceof Integer) {
			return new Long((Integer) obj);
		}
		else if (obj instanceof Long) {
			return (Long) obj;
		}
		else if (obj instanceof String) {
			Long.parseLong((String) obj);
		}
		
		return null;
	}
	
}
