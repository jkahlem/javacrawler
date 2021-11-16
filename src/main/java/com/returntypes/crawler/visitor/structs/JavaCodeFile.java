package com.returntypes.crawler.visitor.structs;

import java.util.LinkedList;
import java.util.List;

public class JavaCodeFile implements ClassContainer {
    String filePath;
    String packageName;
    List<SimplifiedImport> imports;
    List<SimplifiedClass> classes;

    public JavaCodeFile(String filePath) {
        this.filePath = filePath;
        this.imports = new LinkedList<SimplifiedImport>();
        this.classes = new LinkedList<SimplifiedClass>();
    }

    public String getFilePath() {
        return filePath;
    }

    public void setPackage(String packageName) {
        this.packageName = packageName;
    }

    public String getPackage() {
        return packageName;
    }

    public void addImport(SimplifiedImport simplifiedImport) {
        imports.add(simplifiedImport);
    }

    public List<SimplifiedImport> getImports() {
        return imports;
    }

    public void addClass(SimplifiedClass simplifiedClass) {
        classes.add(simplifiedClass);
    }

    public List<SimplifiedClass> getClasses() {
        return classes;
    }
}
