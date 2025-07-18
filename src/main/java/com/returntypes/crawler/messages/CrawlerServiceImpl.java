package com.returntypes.crawler.messages;

import java.io.ByteArrayOutputStream;
import java.nio.file.FileSystems;

import com.returntypes.crawler.RepositoryCrawler;

public class CrawlerServiceImpl implements CrawlerService {
    MainApplicationService mainApplicationService;

    public CrawlerServiceImpl(MainApplicationService mainApplicationService) {
        this.mainApplicationService = mainApplicationService;
    }

    public String getFileContent(String path, CrawlerOptions options) throws Exception{
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        RepositoryCrawler repositoryCrawler = new RepositoryCrawler(mainApplicationService, outputStream, options);
        repositoryCrawler.crawlRepository(FileSystems.getDefault().getPath(path), false);
        return outputStream.toString();
    }

    public String getDirectoryContents(String path, CrawlerOptions options) throws Exception{
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        RepositoryCrawler repositoryCrawler = new RepositoryCrawler(mainApplicationService, outputStream, options);
        repositoryCrawler.crawlRepository(FileSystems.getDefault().getPath(path), true);
        return outputStream.toString();
    }

    public String parseSourceCode(String code, CrawlerOptions options) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        RepositoryCrawler repositoryCrawler = new RepositoryCrawler(mainApplicationService, outputStream, options);
        repositoryCrawler.parseSourceCode(code);
        return outputStream.toString();
    }
}
