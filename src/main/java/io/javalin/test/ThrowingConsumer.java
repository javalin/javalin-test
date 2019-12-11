package io.javalin.test;

import java.util.function.Consumer;

@FunctionalInterface
public interface ThrowingConsumer<U> extends Consumer<U> {
    @Override
    default void accept(U u) {
        try {
            acceptThrows(u);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void acceptThrows(U u) throws Exception;
}