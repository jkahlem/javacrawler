package com.returntypes.crawler;

import java.lang.reflect.Method;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.googlecode.jsonrpc4j.ErrorData;
import com.googlecode.jsonrpc4j.ErrorResolver;

public class RpcErrorResolver implements ErrorResolver {

    @Override
    public JsonError resolveError(Throwable t, Method method, List<JsonNode> arguments) {
        if (t.getMessage() == null) {
            return new ErrorResolver.JsonError(JsonError.INTERNAL_ERROR.code, "An unexpected error occurred: " + t.toString(), new ErrorData("", ""));
        }
        return new ErrorResolver.JsonError(JsonError.INTERNAL_ERROR.code, t.getMessage(), new ErrorData("", ""));
    }
}
