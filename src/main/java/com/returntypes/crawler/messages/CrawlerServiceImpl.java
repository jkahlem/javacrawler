package com.returntypes.crawler.messages;

import java.io.ByteArrayOutputStream;
import java.nio.file.FileSystems;

import com.returntypes.crawler.RepositoryCrawler;

public class CrawlerServiceImpl implements CrawlerService {
    public String getFileContent(String path, CrawlerOptions options) throws Exception{
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        RepositoryCrawler repositoryCrawler = new RepositoryCrawler(outputStream, options);
        repositoryCrawler.crawlRepository(FileSystems.getDefault().getPath(path), false);
        return outputStream.toString();
    }

    public String getDirectoryContents(String path, CrawlerOptions options) throws Exception{
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        RepositoryCrawler repositoryCrawler = new RepositoryCrawler(outputStream, options);
        repositoryCrawler.crawlRepository(FileSystems.getDefault().getPath(path), true);
        return outputStream.toString();
    }
}
