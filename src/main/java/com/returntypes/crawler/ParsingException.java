package com.returntypes.crawler;

import com.github.javaparser.ParseResult;
import com.github.javaparser.Problem;
import com.github.javaparser.ast.CompilationUnit;

/**
 * Returns all parser errors occured when parsing a java file
 */
public class ParsingException extends Exception {
    private ParseResult<CompilationUnit> result;

    public ParsingException(ParseResult<CompilationUnit> result) {
        this.result = result;
    }

    @Override
    public String getMessage() {
        return createMessageFromResult(this.result);
    }

    private String createMessageFromResult(ParseResult<CompilationUnit> result) {
        if (result != null && !result.isSuccessful()) {
            String problemMsg = "The following problems occured when parsing the file:\n";
            for (Problem problem : result.getProblems()) {
                problemMsg += problem.getVerboseMessage() + "\n";
            }
            return problemMsg;
        }
        return "";
    }
}
