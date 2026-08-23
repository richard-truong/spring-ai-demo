package com.eshop.core.application.port.in;

public interface StreamSink {

    void onToken(String token);

    void onComplete();

    void onError(Throwable error);

}
