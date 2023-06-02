package com.github.ridicuturing.guard.common;

public interface ActionParser {

    String analysis(String content);

    Object parse(String content);

    String run(Object parser);

}
