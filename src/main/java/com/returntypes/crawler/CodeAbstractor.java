package com.returntypes.crawler;

import com.googlecode.jsonrpc4j.JsonRpcServer;
import com.googlecode.jsonrpc4j.ProxyUtil;
import com.returntypes.crawler.messages.CrawlerService;
import com.returntypes.crawler.messages.CrawlerServiceImpl;
import com.returntypes.crawler.messages.JsonRpcClientStream;
import com.returntypes.crawler.messages.MainApplicationService;

public class CodeAbstractor {
    public static void main(String[] args) {
        try {
            final MainApplicationService mainApplicationService = ProxyUtil.createClientProxy(
                MainApplicationService.class.getClassLoader(),
                MainApplicationService.class,
                new JsonRpcClientStream());

            final CrawlerService crawlerService = new CrawlerServiceImpl(mainApplicationService);
            JsonRpcServer rpcServer = new JsonRpcServer(crawlerService, CrawlerService.class);
            rpcServer.setErrorResolver(new RpcErrorResolver());

            while (true) {
                rpcServer.handleRequest(System.in, System.out);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
