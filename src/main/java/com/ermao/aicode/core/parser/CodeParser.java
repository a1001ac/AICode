package com.ermao.aicode.core.parser;

/**
 * 代码解析器接口
 * @author 21195
 */
public interface CodeParser<T> {

    /**
     * 解析代码
     * @param code
     * @return T
     */
    T parseCode(String code);

}
