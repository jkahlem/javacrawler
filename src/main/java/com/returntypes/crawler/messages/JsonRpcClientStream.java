package com.returntypes.crawler.messages;

import java.lang.reflect.Type;
import java.util.Map;

import com.googlecode.jsonrpc4j.IJsonRpcClient;
import com.googlecode.jsonrpc4j.JsonRpcClient;

public class JsonRpcClientStream extends JsonRpcClient implements IJsonRpcClient {
    public JsonRpcClientStream() {}

    @Override
    public void invoke(String methodName, Object argument) throws Throwable {
        super.invoke(methodName, argument, System.out);
    }

    @Override
    public Object invoke(String methodName, Object argument, Type returnType) throws Throwable {
        this.invoke(methodName, argument);
        return super.readResponse(returnType, System.in);
    }

    @Override
    public Object invoke(String methodName, Object argument, Type returnType, Map<String, String> extraHeaders)
            throws Throwable {
        this.invoke(methodName, argument);
        return super.readResponse(returnType, System.in);
    }

    @Override
    public <T> T invoke(String methodName, Object argument, Class<T> clazz) throws Throwable {
        this.invoke(methodName, argument);
        return super.readResponse(clazz, System.in);
    }

    @Override
    public <T> T invoke(String methodName, Object argument, Class<T> clazz, Map<String, String> extraHeaders)
            throws Throwable {
        this.invoke(methodName, argument);
        return super.readResponse(clazz, System.in);
    }
    
}
