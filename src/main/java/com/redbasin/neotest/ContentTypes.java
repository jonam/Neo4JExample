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

public enum ContentTypes {
    
    /**
     * A data uri.
     */
    JSON("application/json");
    
    private final String value;

    private static final Map<String, ContentTypes> stringToEnum = new HashMap<String, ContentTypes>();

    static { // init map from constant name to enum constant
        for (ContentTypes en : values())
            stringToEnum.put(en.toString(), en);
    }

    ContentTypes(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    public static ContentTypes fromString(String value) {
        return stringToEnum.get(value);
    }   
}