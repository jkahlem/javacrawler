package com.returntypes.crawler;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration.LanguageLevel;
import com.github.javaparser.ast.CompilationUnit;
import com.returntypes.crawler.visitor.JavaCodeFileVisitor;
import com.returntypes.crawler.messages.CrawlerOptions;
import com.returntypes.crawler.messages.MainApplicationService;
import com.returntypes.crawler.visitor.CodeAbstractionVisitor;
import com.returntypes.crawler.visitor.structs.JavaCodeFile;

/**
 * Converts the contents of a directory to xml format
 */
public class RepositoryCrawler {
    final MainApplicationService mainApplicationService;

    JavaCodeXmlWriter xmlWriter;
    JavaParser javaParser;    
    CrawlerOptions crawlerOptions;
    OutputStream outputStream;

    public RepositoryCrawler(final MainApplicationService mainApplicationService,
                             final OutputStream outputStream,
                             final CrawlerOptions crawlerOptions) {
        this.crawlerOptions = crawlerOptions;
        this.javaParser = new JavaParser();
        this.outputStream = outputStream;
        this.mainApplicationService = mainApplicationService;

        if (this.crawlerOptions.getJavaVersion() != null && this.crawlerOptions.getJavaVersion() != 0) {
            LanguageLevel languageLevel = mapJavaVersion(this.crawlerOptions.getJavaVersion());
            if (languageLevel == null) {
                throw new RuntimeException("The specified java version " + this.crawlerOptions.getJavaVersion() + " is not supported.");
            }
            this.javaParser.getParserConfiguration().setLanguageLevel(languageLevel);
        }
    }

    private LanguageLevel mapJavaVersion(int javaVersion) {
        switch (javaVersion) {
            case 7:
                return LanguageLevel.JAVA_7;
            case 8:
                return LanguageLevel.JAVA_8;
            case 9:
                return LanguageLevel.JAVA_9;
            case 10:
                return LanguageLevel.JAVA_10;
            case 11:
                return LanguageLevel.JAVA_11;
            case 12:
                return LanguageLevel.JAVA_12;
            case 13:
                return LanguageLevel.JAVA_13;
            case 14:
                return LanguageLevel.JAVA_14;
            case 15:
                return LanguageLevel.JAVA_15;
            case 16:
                return LanguageLevel.JAVA_16;
            case 17:
                return LanguageLevel.JAVA_17;
        }
        return null;
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
            reportProgress(counter++, javaFiles.size(), "Preprocess files");
            try {
                extractJavaCodeFileContents(rootPath, filePath);
            } catch(Exception e) {
                if (!this.crawlerOptions.isForced()) {
                    this.xmlWriter.closeOutputFile();
                    throw e;
                } else {
                    reportError(e, filePath.toString());
                }
            }
        }
        this.xmlWriter.closeOutputFile();
        this.xmlWriter = null;
    }

    /**
     * Parses the source code and outputs it to the given output stream.
     * 
     * @param sourceCode the java source code to parse
     * @throws Exception
     */
    public void parseSourceCode(String sourceCode) throws Exception {
        this.xmlWriter = new JavaCodeXmlWriter(this.outputStream, this.crawlerOptions);
        try {
            extractJavaCode(getCompilationUnit(sourceCode), "");
        } catch(Exception e) {
            reportError(e, "<no file path>");
        }
        this.xmlWriter.closeOutputFile();
        this.xmlWriter = null;
    }

    private void reportProgress(int progress, int total, String operation) {
        if (!this.crawlerOptions.isSilent())
            mainApplicationService.reportProgress(progress, total, operation);
    }

    private void reportError(Throwable error, String filePath) {
        if (!this.crawlerOptions.isSilent())
            mainApplicationService.reportError(error.getMessage(), getStackTrace(error), filePath);
    }

    private String getStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.toString();
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

        reportProgress(0, 0, "Count java files");
        JavaCodeFileVisitor visitor = new JavaCodeFileVisitor();
        Files.walkFileTree(path, visitor);
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
        extractJavaCode(compilationUnit, path.toString());
    }

    /**
     * Extracts java code from a compilation unit
     * 
     * @param compilationUnit
     * @param path
     * @throws Exception
     */
    private void extractJavaCode(CompilationUnit compilationUnit, String path) throws Exception {
        if (compilationUnit == null) return;
        JavaCodeFile javaCodeFile = compilationUnit.accept(new CodeAbstractionVisitor(path), null);
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
        return getCompilationUnit(javaParser.parse(filePath.toFile()));
    }

    /**
     * Returns the compilation unit for java source code
     * 
     * @param sourceCode
     * @return
     * @throws Exception
     */
    private CompilationUnit getCompilationUnit(String sourceCode) throws Exception {
        return getCompilationUnit(javaParser.parse(sourceCode));
    }

    private CompilationUnit getCompilationUnit(ParseResult<CompilationUnit> parseResult) throws Exception {
        if (!parseResult.isSuccessful() && !this.crawlerOptions.isParseIncomplete()) {
            throw new ParsingException(parseResult);
        }
        return parseResult.getResult().orElse(null);
    }
}
