package com.learn.log;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LogTest {

    private Logger logger = LoggerFactory.getLogger(LogTest.class);

    @Test
    public void test() {
        logger.debug("debug日志");
        logger.info("info日志");
        logger.warn("warn日志");
        logger.error("error日志");
    }

}
