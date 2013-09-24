/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.redbasin.neotest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import org.apache.http.HttpException;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.rest.graphdb.RestGraphDatabase;
import org.neo4j.rest.graphdb.index.RestIndex;


/**
 * @author redbasin
 */
public class Neo4JPostFullTextTest {
    private static enum RelTypes implements RelationshipType {
       INHIBITS
    }
    
    private static RestGraphDatabase graphDb;
    private static Node firstNode;
    private static Node secondNode;
    private static Relationship relationship;
    private static final String DB_PATH= "neo4j-shortest-path";
    
    
    private static void setup() throws URISyntaxException {
        //graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( DB_PATH );
        graphDb = new RestGraphDatabase("http://localhost:7474/db/data");
        registerShutdownHook( graphDb );
    }
    
    private static void registerShutdownHook( RestGraphDatabase graphDb1) {
    // Registers a shutdown hook for the Neo4j instance so that it
    // shuts down nicely when the VM exits (even if you "Ctrl-C" the
    // running example before it's completed)
        Runtime.getRuntime().addShutdownHook( new Thread() {
        @Override
        public void run() {
            graphDb.shutdown();
        }
    } );
    }
    
    public static boolean indexExists(String indexName) 
        throws UnsupportedEncodingException, 
                    MalformedURLException, 
                    IOException, 
                    UnknownHostException, 
                    HttpException {
   
        try {
           return HttpPostUtil.get(NeoURIRequests.NODE_URI, indexName).equals("[ ]");
        } catch (Exception e) {
            // something went wrong, assume index does not exist
        }
        return false;
    }
    
    /**
     * Create a full text index. Any arbitary name can be given.
     * 
     * @param url
     * @param indexName full text index name
     */
    public static String createFullTextIndex(String indexName) 
            throws UnsupportedEncodingException, 
            MalformedURLException, 
            IOException, 
            UnknownHostException, 
            HttpException {
        
        if (indexExists(indexName)) {
            return "{}";
        }
        String message =  "{" +
               "\"name\" : \"" + indexName + "\"," + 
               "\"config\" : {" +
               "\"type\" : \"fulltext\"," +
               "\"provider\" : \"lucene\"" +
               "} }";
        return HttpPostUtil.postJson(NeoURIRequests.NODE_URI, message);
    }
    
    public static void processFullTextIndex(String indexName, Long nodeId, String name, String value) throws UnsupportedEncodingException, MalformedURLException, IOException, UnknownHostException, HttpException {
        //log.info("processFulTextIndex(), indexName " + indexName.toString());
        if (!indexExists(indexName.toString())) {
            String response = createFullTextIndex(indexName);
            /*if (response != null) {
                log.info("response =" + response);
            } */
        }
        String response = addNodeWithPropertyToIndex(indexName.toString(), value, nodeId, name);
    }
    
    public static String addNodeWithPropertyToIndex(String indexName, String value, Long nodeId, String key) 
        throws UnsupportedEncodingException, 
                MalformedURLException, 
                IOException, 
                UnknownHostException, 
                HttpException {
        String uri = "http://localhost:7474" +
                NeoURIRequests.ONLY_NODE_URI + nodeId;
        String message = 
                "{"
                + "\"value\" :" + "\"" + value + "\","
                + "\"uri\" :" + "\"" + uri + "\","
                + "\"key\" :" + "\"" + key + "\""
                + "}"; 
        return HttpPostUtil.postJson(NeoURIRequests.NODE_URI, indexName, message);
    }
    
    public static void main(String[] args) throws Exception {
        setup();
        
        String indexName = "ftindex";
        String field = "longfield";
        Transaction tx = graphDb.beginTx();
        try {
            firstNode = graphDb.createNode();
            tx.success();
        } finally {
            tx.finish();
        }
        
        try {
            StringBuilder val = new StringBuilder();
            for (int i=0; i < 3871; i++) { // breaks after 3870
                //For eg: 3900, returns immediately, post request failed, HTTP status code: 500Request URI: /db/data/index/node/ftindex 
                // very slow for 3871 then gives Exception in thread "main" org.apache.http.ProtocolException: Invalid header: }
                // at org.apache.http.impl.io.AbstractMessageParser.parseHeaders(AbstractMessageParser.java:226)
                val.append("a");
            }
            System.out.println("nodeid = " + firstNode.getId());
            processFullTextIndex(indexName, firstNode.getId(), field, val.toString());
            tx.success();
        } finally {
            tx.finish();
        }
    }
}
