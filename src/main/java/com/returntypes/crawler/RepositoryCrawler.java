package com.returntypes.crawler;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.Optional;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.utils.Log;
import com.returntypes.crawler.visitor.JavaCodeFileVisitor;
import com.returntypes.crawler.messages.CrawlerOptions;
import com.returntypes.crawler.visitor.CodeAbstractionVisitor;
import com.returntypes.crawler.visitor.structs.JavaCodeFile;

/**
 * Converts the contents of a directory to xml format
 */
public class RepositoryCrawler {
    JavaCodeXmlWriter xmlWriter;
    JavaParser javaParser;    
    CrawlerOptions crawlerOptions;
    OutputStream outputStream;

    public RepositoryCrawler(OutputStream outputStream, CrawlerOptions crawlerOptions) {
        this.crawlerOptions = crawlerOptions;
        this.javaParser = new JavaParser();
        this.outputStream = outputStream;
    }

    /**
     * Crawls through the specified path and writes the content of the java files to the output stream in xml format
     * 
     * @param rootPath
     * @throws Exception
     */
    public void crawlRepository(Path rootPath, boolean isRecursive) throws Exception {
        this.xmlWriter = new JavaCodeXmlWriter(this.outputStream, this.crawlerOptions);

        LinkedList<Path> javaFiles = getJavaFilePaths(rootPath, isRecursive);

        int counter = 0;
        for (Path filePath : javaFiles) {
            counter++;
            log("["+ counter + "/" + javaFiles.size() + "] Extract " + filePath.toString());
            try {
                extractJavaCodeFileContents(rootPath, filePath);
            } catch(Exception e) {
                if (!this.crawlerOptions.isForced()) {
                    this.xmlWriter.closeOutputFile();
                    throw e;
                } else {
                    log("Could not parse the file, skip it.");
                    Log.error(e);
                }
            }
        }
        this.xmlWriter.closeOutputFile();
    }

    /**
     * Logs the message if not in silent mode
     * 
     * @param msg
     */
    private void log(String msg) {
        if (!this.crawlerOptions.isSilent())
            Log.info(msg);
    }

    /**
     * Gets the java file paths used for crawling.
     * 
     * @param path target filepath
     * @param isRecursive if false, path is used as a java file path and only that is returned (if the file exists). if true,
     *                    all java file paths in the directory under path will be returned
     * @return the paths to java files to use
     * @throws IOException
     */
    private LinkedList<Path> getJavaFilePaths(Path path, boolean isRecursive) throws IOException {
        if (!isRecursive) {
            File targetFile = new File(path.toString());
            if (!targetFile.exists()) {
                return new LinkedList<Path>();
            }
            LinkedList<Path> list = new LinkedList<Path>();
            list.add(path);
            return list;
        }

        log("Count java files in directory: " + path.toString());
        JavaCodeFileVisitor visitor = new JavaCodeFileVisitor();
        Files.walkFileTree(path, visitor);
        log("Found " + visitor.getFilePaths().size() + " Java Source Code files in the target directory.");
        return visitor.getFilePaths();
    }

    /**
     * Writes the extracted java file contents to the output stream
     * 
     * @param rootPath
     * @param filePath
     * @throws Exception
     */
    private void extractJavaCodeFileContents(Path rootPath, Path filePath) throws Exception {
        CompilationUnit compilationUnit = getCompilationUnit(filePath);
        if (compilationUnit == null) return;

        Path path = filePath;
        if (!crawlerOptions.isUseAbsolutePaths() && rootPath.getParent() != null) {
            path = rootPath.getParent().relativize(filePath);
        }
        JavaCodeFile javaCodeFile = compilationUnit.accept(new CodeAbstractionVisitor(path.toString()), null);

        this.xmlWriter.writeJavaCodeFile(javaCodeFile);
    }

    /**
     * Returns the compilation unit for a java file
     * 
     * @param filePath
     * @return
     * @throws Exception
     */
    private CompilationUnit getCompilationUnit(Path filePath) throws Exception {
        ParseResult<CompilationUnit> parseResult = javaParser.parse(filePath.toFile());
        if (!parseResult.isSuccessful()) {
            throw new ParsingException(parseResult);
        }

        Optional<CompilationUnit> optionalCompilationUnit = parseResult.getResult();
        if (optionalCompilationUnit.isPresent()) {
            return optionalCompilationUnit.get();
        }
        return null;
    }
}
