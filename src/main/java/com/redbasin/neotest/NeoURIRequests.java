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

public enum NeoURIRequests {
    
    /**
     * A data uri.
     */
    DATA_URI("/db/data/"),
    
    /**
     * The node index URI.
     */
    NODE_URI("/db/data/index/node/"),
    
    /**
     * Access node URI
     */
    ONLY_NODE_URI("/db/data/node/");
    
    private final String value;

    private static final Map<String, NeoURIRequests> stringToEnum = new HashMap<String, NeoURIRequests>();

    static { // init map from constant name to enum constant
        for (NeoURIRequests en : values()) {
            stringToEnum.put(en.toString(), en);
        }
    }

    NeoURIRequests(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    public static NeoURIRequests fromString(String value) {
        return stringToEnum.get(value);
    }   
}