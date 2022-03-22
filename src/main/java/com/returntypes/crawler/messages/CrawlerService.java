package com.returntypes.crawler.messages;

import com.googlecode.jsonrpc4j.JsonRpcParam;

/**
 * The rpc interface for the crawler
 */
public interface CrawlerService {
    /**
     * Crawls and converts the content of the specified file
     * 
     * @param path the file path
     * @param options the crawler options
     * @return the contents of the file as xml
     * @throws Exception
     */
    public String getFileContent(@JsonRpcParam("path") String path, @JsonRpcParam("options") CrawlerOptions options) throws Exception;

    /**
     * Crawls and converts the content of all java files in the specified path
     * 
     * @param path the directory path
     * @param options the crawler options
     * @return the contents of all java files in the directory as one xml
     * @throws Exception
     */
    public String getDirectoryContents(@JsonRpcParam("path") String path, @JsonRpcParam("options") CrawlerOptions options) throws Exception;

    /**
     * Parses the passed source code
     * 
     * @param code the raw source code to parse
     * @param options the crawler options
     * @return the contents of the passed source code in xml
     * @throws Exception
     */
    public String parseSourceCode(@JsonRpcParam("code") String code, @JsonRpcParam("options") CrawlerOptions options) throws Exception;
}
