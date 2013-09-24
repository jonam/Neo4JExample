package com.redbasin.neotest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpClientConnection;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.params.SyncBasicHttpParams;
import org.apache.http.protocol.*;
import org.apache.http.util.EntityUtils;

/**
 * Elemental example for executing multiple POST requests sequentially.
 * <p>
 * Please note the purpose of this application is demonstrate the usage of HttpCore APIs.
 * It is NOT intended to demonstrate the most efficient way of building an HTTP client.
 * <p> 
 * The HttpClient tutorial is useful to read:
 * 
 * http://hc.apache.org/httpcomponents-client-ga/tutorial/html/index.html
 */
public class HttpPostUtil {
    
    private static String hostname = "saibaba.local";
    private static int port = 7474;
    protected static final Log log = LogFactory.getLog(HttpPostUtil.class);
    
    public static void main(String[] args) throws UnsupportedEncodingException, UnknownHostException, IOException, HttpException {
        
        //String uri = "/db/data/index/node/";
        //String host = "saibaba.local";
        //Integer port = 7474;
        String message =  "{" +
               "\"name\" : \"fulltext\"," + 
               "\"config\" : {" +
               "\"type\" : \"fulltext\"," +
               "\"provider\" : \"lucene\"" +
               "} }";
        
        //HttpPostUtil httpPostUtil = new HttpPostUtil();
        ContentTypes contentType = ContentTypes.JSON;
        postJson(NeoURIRequests.NODE_URI, message);
    }
    
    public static String postJson(NeoURIRequests uri, String message) throws UnsupportedEncodingException, UnknownHostException, IOException, HttpException {
        ContentTypes contentType = ContentTypes.JSON;
        return post(uri.toString(), contentType, message);
    }
    
    public static String postJson(NeoURIRequests uri, String indexName, String message) throws UnsupportedEncodingException, UnknownHostException, IOException, HttpException {
        ContentTypes contentType = ContentTypes.JSON;
        return post(uri.toString()+indexName, contentType, message);
    }
    
    public static String get(NeoURIRequests uri, String indexName, String query) throws UnsupportedEncodingException, UnknownHostException, IOException, HttpException {
        ContentTypes contentType = ContentTypes.JSON;
        log.info(uri.toString() + "?query=" + query);
        query = "?query=" + URLEncoder.encode(query);
        return get(uri.toString()+indexName, contentType, query);        
    }
    
    public static String get(NeoURIRequests uri, String indexName) throws UnsupportedEncodingException, UnknownHostException, IOException, HttpException {
        ContentTypes contentType = ContentTypes.JSON;
        return get(uri.toString()+indexName, contentType, "");        
    }
    
    public static String get(NeoURIRequests uri) throws UnsupportedEncodingException, UnknownHostException, IOException, HttpException {
        ContentTypes contentType = ContentTypes.JSON;
        log.info(uri.toString());
        return get(uri.toString(), contentType, "");        
    }
    
    public static String get(String uri, ContentTypes contentType, String query) throws UnsupportedEncodingException, UnknownHostException, IOException, HttpException {
      HttpParams params = new SyncBasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, "UTF-8");
        HttpProtocolParams.setUserAgent(params, "HttpComponents/1.1");
        HttpProtocolParams.setUseExpectContinue(params, true);
        String resp = "";

        HttpProcessor httpproc = new ImmutableHttpProcessor(new HttpRequestInterceptor[] {
                // Required protocol interceptors
                new RequestContent(),
                new RequestTargetHost(),
                // Recommended protocol interceptors
                new RequestConnControl(),
                new RequestUserAgent(),
                new RequestExpectContinue()});

        HttpRequestExecutor httpexecutor = new HttpRequestExecutor();

        HttpContext context = new BasicHttpContext(null);
        HttpHost host = new HttpHost(hostname.toString(), new Integer(port));

        DefaultHttpClientConnection conn = new DefaultHttpClientConnection();
        ConnectionReuseStrategy connStrategy = new DefaultConnectionReuseStrategy();

        context.setAttribute(ExecutionContext.HTTP_CONNECTION, conn);
        context.setAttribute(ExecutionContext.HTTP_TARGET_HOST, host);

        try {

                if (!conn.isOpen()) {
                    Socket socket = new Socket(host.getHostName(), host.getPort());
                    conn.bind(socket, params);
                }
                //log.info(uri.toString() + query);
                //BasicHttpRequest request = new BasicHttpRequest("GET", URLEncoder.encode(uri.toString()) + URLEncoder.encode(query));
                BasicHttpRequest request = new BasicHttpRequest("GET", uri.toString() + query);
                //System.out.println(">> Request URI: " + request.getRequestLine().getUri());
                request.addHeader("content-type", "application/json");
                request.addHeader("X-Stream-Type", "true");
                request.setParams(params);
                httpexecutor.preProcess(request, httpproc, context);
                HttpResponse response = httpexecutor.execute(request, conn, context);
                response.setParams(params);
                httpexecutor.postProcess(response, httpproc, context);
                log.info("<< Response: " + response.getStatusLine());
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode != HTTPStatusCodes.HTTP_GET_OK.intValue()) {
                   throw new RuntimeException("Post request failed, "
                           + "HTTP status code: " + statusCode
                           + "Request URI: " + request.getRequestLine().getUri());   
                }
                resp = EntityUtils.toString(response.getEntity());
                log.info("Streaming = " + response.getEntity().isStreaming());
                //System.out.println(resp);
                //System.out.println("==============");
                if (!connStrategy.keepAlive(response, context)) {
                    conn.close();
                } else {
                    System.out.println("Connection kept alive...");
                }
        } finally {
            conn.close();
        }  
        return resp;
    }

    public static String post(String uri, ContentTypes contentType, String message) throws UnsupportedEncodingException, UnknownHostException, IOException, HttpException  {

        HttpParams params = new SyncBasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, "UTF-8");
        HttpProtocolParams.setUserAgent(params, "Test/1.1");
        HttpProtocolParams.setUseExpectContinue(params, true);

        HttpProcessor httpproc = new ImmutableHttpProcessor(new HttpRequestInterceptor[] {
                // Required protocol interceptors
                new RequestContent(),
                new RequestTargetHost(),
                // Recommended protocol interceptors
                new RequestConnControl(),
                new RequestUserAgent(),
                new RequestExpectContinue()});

        HttpRequestExecutor httpexecutor = new HttpRequestExecutor();

        HttpContext context = new BasicHttpContext(null);

        HttpHost host = new HttpHost(hostname.toString(), new Integer(port));

        DefaultHttpClientConnection conn = new DefaultHttpClientConnection();
        ConnectionReuseStrategy connStrategy = new DefaultConnectionReuseStrategy();

        context.setAttribute(ExecutionContext.HTTP_CONNECTION, conn);
        context.setAttribute(ExecutionContext.HTTP_TARGET_HOST, host);
        String resp = "";

        try {

            HttpEntity requestBodies = new StringEntity(message, "UTF-8");

            if (!conn.isOpen()) {
                Socket socket = new Socket(host.getHostName(), host.getPort());
                conn.bind(socket, params);
            }
            BasicHttpEntityEnclosingRequest request = 
                    new BasicHttpEntityEnclosingRequest("POST", uri
                    /* "/servlets-examples/servlet/RequestInfoExample" */);
            request.addHeader("content-type", "application/json");
            request.addHeader("X-Stream-Type", "true");
            request.setEntity(requestBodies);
            //System.out.println(">> Request URI: " + request.getRequestLine().getUri());

            request.setParams(params);
            httpexecutor.preProcess(request, httpproc, context);
            HttpResponse response = httpexecutor.execute(request, conn, context);
            response.setParams(params);
            httpexecutor.postProcess(response, httpproc, context);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HTTPStatusCodes.HTTP_POSTED_OK.intValue()) {
                throw new RuntimeException("Post request failed, "
                        + "HTTP status code: " + statusCode
                        + "Request URI: " + request.getRequestLine().getUri());
            }
            log.info("<< Response: " + response.getStatusLine());
            resp = EntityUtils.toString(response.getEntity());
            log.info("Streaming = " + response.getEntity().isStreaming());
            //System.out.println(resp);
            //System.out.println("==============");
            if (!connStrategy.keepAlive(response, context)) {
                conn.close();
            } else {
                //System.out.println("Connection kept alive...");
            }
        } finally {
            conn.close();
        }
        return resp;
    }
}