package com.learn.log;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LombokTest {

    public void test() {
        log.debug("Lombok debug日志");
        log.info("Lombok info日志");
        log.warn("Lombok warn日志");
        log.error("Lombok error日志");

    }
}
