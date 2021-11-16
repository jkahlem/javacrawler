package com.returntypes.crawler.visitor.structs;

public class SimplifiedImport {
    String importPath;
    boolean wildcardState;
    boolean staticState;

    public void setImportPath(String importPath) {
        this.importPath = importPath;
    }

    public String getImportPath() {
        return this.importPath;
    }

    public void setWildcard(boolean wildcardState) {
        this.wildcardState = wildcardState;
    }

    public boolean isWildcard() {
        return this.wildcardState;
    }

    public void setStatic(boolean staticState) {
        this.staticState = staticState;
    }

    public boolean isStatic() {
        return this.staticState;
    }
}
