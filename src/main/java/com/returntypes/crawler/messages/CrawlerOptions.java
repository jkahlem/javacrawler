package com.returntypes.crawler.messages;

public class CrawlerOptions {
    private boolean useAbsolutePaths;
    private boolean useRanges;
    private boolean forced;
    private boolean silent;
    private Integer javaVersion;
    private boolean parseIncomplete;

    public boolean isUseAbsolutePaths() {
        return useAbsolutePaths;
    }

    public boolean isUseRanges() {
        return useRanges;
    }

    public boolean isForced() {
        return forced;
    }

    public boolean isSilent() {
        return silent;
    }

    public Integer getJavaVersion() {
        return javaVersion;
    }

    public boolean isParseIncomplete() {
        return parseIncomplete;
    }
}
