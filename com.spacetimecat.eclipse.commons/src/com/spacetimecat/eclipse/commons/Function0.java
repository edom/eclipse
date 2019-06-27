package com.spacetimecat.eclipse.commons;

@FunctionalInterface
public interface Function0<A, B> {

    B apply (A arg) throws Exception;

}
