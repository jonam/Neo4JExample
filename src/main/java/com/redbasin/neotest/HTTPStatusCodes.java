/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.redbasin.neotest;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author redbasin
 */

public enum HTTPStatusCodes {
    
    /**
     * Http Posted OK
     */
    HTTP_POSTED_OK(201),
    
    HTTP_GET_OK(200);
    
    private final Integer value;

    private static final Map<String, HTTPStatusCodes> stringToEnum = new HashMap<String, HTTPStatusCodes>();

    static { // init map from constant name to enum constant
        for (HTTPStatusCodes en : values())
            stringToEnum.put(en.toString(), en);
    }

    HTTPStatusCodes(Integer value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
    
    public int intValue() {
        return value.intValue();
    }

    public static HTTPStatusCodes fromString(String value) {
        return stringToEnum.get(value);
    }   
}