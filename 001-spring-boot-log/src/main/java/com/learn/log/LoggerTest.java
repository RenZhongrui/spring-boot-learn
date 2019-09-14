package com.learn.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerTest {

    private Logger logger = LoggerFactory.getLogger(LoggerTest.class);

    public void test() {
        logger.debug("debug日志");
        logger.info("info日志");
        logger.warn("warn日志");
        logger.error("error日志");
    }
}
