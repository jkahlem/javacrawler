package com.returntypes.crawler.messages;

import com.googlecode.jsonrpc4j.JsonRpcParam;

/**
 * The rpc interface for the main application
 */
public interface MainApplicationService {
    /**
     * Reports the progress of the crawling service
     * 
     * @param progress the progress value relative to total
     * @param total the total value
     * @param operation the current operation
     */
    public void reportProgress(@JsonRpcParam("progress") int progress, @JsonRpcParam("total") int total, @JsonRpcParam("operation") String operation);

    /**
     * Reports an error happened during the crawling process (like parser exceptions) which were skipped
     * 
     * @param message the error message
     * @param filePath the path to the file which was processed when the error occured
     */
    public void reportError(@JsonRpcParam("message") String message, @JsonRpcParam("stacktrace") String stacktrace, @JsonRpcParam("filepath") String filePath);
}
