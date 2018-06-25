package com.home.cc.replica.service.util;

import java.util.function.Consumer;
import java.util.function.Function;

public class LambdaExceptionWrapper {
    private LambdaExceptionWrapper(){}

    public static <T> Consumer<T> throwingConsumer(ThrowingConsumer<T> consumer){
        return t -> {
            try {
                consumer.accept(t);

            }catch (Exception ex){
                throw new RuntimeException(ex);
            }
        };
    }

    public static <T, R> Function<T, R> throwingFunction(ThrowingFunction<T, R> function){
        return t -> {
            try {
                return  function.apply(t);

            }catch (Exception ex){
                throw new RuntimeException(ex);
            }
        };
    }
}