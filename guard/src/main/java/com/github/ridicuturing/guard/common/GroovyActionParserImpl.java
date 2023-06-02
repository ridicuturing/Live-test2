package com.github.ridicuturing.guard.common;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReUtil;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * groovy动作解析器impl
 *
 * @author chenzhihai
 */
@Component("groovyActionParser")
public class GroovyActionParserImpl implements ActionParser {


    @Override
    public String analysis(String content) {
        try {
            List<String> codes = ReUtil.findAll("(?<=\n```groovy\n)(.|\n)*?(?=\n``` *\n)", content, 0);
            return CollUtil.getLast(codes);
        } catch (Exception e) {
            throw new IllegalStateException("这个groovy脚本存在编译异常:\n" + e.getMessage() + "\n" + e.getStackTrace()[0].toString());
        }
    }


    @Override
    public Script parse(String code) {
        try {
            return new GroovyShell().parse(code);
        } catch (Exception e) {
            throw new IllegalStateException("这个groovy脚本存在编译异常:\n" + e.getMessage() + "\n" + e.getStackTrace()[0].toString());
        }
    }

    @Override
    public String run(Object script) {
        try {
            return (String) ((Script)script).run();
        } catch (Exception e) {
            throw new IllegalStateException("这个groovy脚本运行时异常:\n" + e.getMessage() + "\n" + e.getStackTrace()[0].toString());
        }
    }

}
