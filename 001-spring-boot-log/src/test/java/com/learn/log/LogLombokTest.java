package com.learn.log;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * 使用Lombok工具优化log日志，在idea中需要安装lombok插件
 */
@Slf4j
public class LogLombokTest {

    @Test
    public void test() {
        log.debug("Lombok debug日志");
        log.info("Lombok info日志");
        log.warn("Lombok warn日志");
        log.error("Lombok error日志");

    }
}
