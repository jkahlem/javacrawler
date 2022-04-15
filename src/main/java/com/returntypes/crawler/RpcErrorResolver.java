package com.returntypes.crawler;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.googlecode.jsonrpc4j.ErrorData;
import com.googlecode.jsonrpc4j.ErrorResolver;

public class RpcErrorResolver implements ErrorResolver {

    @Override
    public JsonError resolveError(Throwable t, Method method, List<JsonNode> arguments) {
        ErrorData errorData = new ErrorData(t.toString(), getStackTrace(t)); 
        if (t.getMessage() == null) {
            return new ErrorResolver.JsonError(JsonError.INTERNAL_ERROR.code,
                "An unexpected error occurred: " + t.toString() + "\n Java Stacktrace: \n" + getStackTrace(t),
                errorData);
        }
        return new ErrorResolver.JsonError(JsonError.INTERNAL_ERROR.code, t.getMessage(), errorData);
    }

    private String getStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.toString();
    }
}
