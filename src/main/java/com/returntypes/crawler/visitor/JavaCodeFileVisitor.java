package com.returntypes.crawler.visitor;

import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedList;

/**
 * A visitor building a list of filePaths for *.java files in a given directory (recursively)
 */
public class JavaCodeFileVisitor extends SimpleFileVisitor<Path> {
    LinkedList<Path> filePaths;

    public JavaCodeFileVisitor() {
        filePaths = new LinkedList<Path>();
    }

    @Override
    public FileVisitResult visitFile(Path filePath, BasicFileAttributes attr) {
        if (isJavaCodeFile(filePath, attr)) {
            filePaths.add(filePath);
        }

        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
        if (dir.getFileName().toString().equals(".git")) {
            return FileVisitResult.SKIP_SUBTREE;
        }
        return FileVisitResult.CONTINUE;
    }

    private boolean isJavaCodeFile(Path filePath, BasicFileAttributes attr) {
        return attr.isRegularFile() && filePath.getFileName().toString().endsWith(".java");
    }

    public LinkedList<Path> getFilePaths() {
        return filePaths;
    }
}
