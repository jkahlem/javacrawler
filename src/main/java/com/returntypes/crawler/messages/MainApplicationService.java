package com.returntypes.crawler.messages;

import com.googlecode.jsonrpc4j.JsonRpcParam;

/**
 * The rpc interface for the main application
 */
public interface MainApplicationService {
    /**
     * Logs the passed message
     * 
     * @param string the log message
     */
    public void log(@JsonRpcParam("message") String string);
    
    /**
     * Logs the passed message as an error
     * 
     * @param string the error message
     */
    public void error(@JsonRpcParam("message") String string);
}
