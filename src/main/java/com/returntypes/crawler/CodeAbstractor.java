package com.returntypes.crawler;

import com.github.javaparser.utils.Log;
import com.googlecode.jsonrpc4j.JsonRpcServer;
import com.returntypes.crawler.messages.CrawlerService;
import com.returntypes.crawler.messages.CrawlerServiceImpl;

public class CodeAbstractor {
    public static void main(String[] args) {
        Log.setAdapter(new LoggerServiceLogAdapter());
        try {
            CrawlerService crawlerService = new CrawlerServiceImpl();
            JsonRpcServer rpcServer = new JsonRpcServer(crawlerService, CrawlerService.class);
            while (true) {
                rpcServer.handleRequest(System.in, System.out);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
