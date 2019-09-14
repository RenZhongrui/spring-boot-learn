package com.learn.log.config;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;

// 自定义日志配置，在日志中显示IP信息
@Slf4j
public class IPLogConverter extends ClassicConverter {

    @Override
    public String convert(ILoggingEvent iLoggingEvent) {
        try {
            String ip = InetAddress.getLocalHost().getHostAddress();
            if (ip != null) {
                return ip;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return "";
    }
}
