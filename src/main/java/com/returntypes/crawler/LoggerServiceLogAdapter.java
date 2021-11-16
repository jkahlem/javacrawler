package com.returntypes.crawler;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.function.Supplier;

import com.github.javaparser.utils.Log.Adapter;
import com.googlecode.jsonrpc4j.ProxyUtil;
import com.returntypes.crawler.messages.JsonRpcClientStream;
import com.returntypes.crawler.messages.MainApplicationService;

/**
 * Implements a logger adapter sending the log messages to the main application
 */
public class LoggerServiceLogAdapter implements Adapter {
    MainApplicationService mainApplicationService;

    LoggerServiceLogAdapter() {
        JsonRpcClientStream client = new JsonRpcClientStream();
        this.mainApplicationService = ProxyUtil.createClientProxy(getClass().getClassLoader(), MainApplicationService.class, client);
    }

	@Override
	public void info(Supplier<String> message) {
		this.mainApplicationService.log(message.get());
	}

	@Override
	public void trace(Supplier<String> message) {
		this.mainApplicationService.error(message.get());
	}

	@Override
	public void error(Supplier<Throwable> throwableSupplier, Supplier<String> messageSupplier) {
		if (throwableSupplier == null && messageSupplier == null) {
            return;
        }
        if (throwableSupplier == null) {
            throwableSupplier = () -> null;
        } else if (messageSupplier == null) {
            messageSupplier = () -> null;
        }
        Throwable throwable = throwableSupplier.get();
        String message = messageSupplier.get();
        printErrorToStdErr(throwable, message);
	}

    private void printErrorToStdErr(Throwable throwable, String message) {
        if (message != null) {
            trace(() -> message);
        }
        if (throwable != null) {
            printStackTrace(throwable);
        }
    }

    private void printStackTrace(Throwable throwable) {
        try (StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw)) {
            throwable.printStackTrace(pw);
            trace(sw::toString);
        } catch (IOException e) {
            throw new AssertionError("Error in logging library");
        }
    }
}
