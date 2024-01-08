package com.socialnet.aspect;

import org.aspectj.lang.annotation.Pointcut;

public class AnnotationPointcuts {
    @Pointcut("(@target(com.socialnet.annotation.Info) || " +
            "@annotation(com.socialnet.annotation.InfoLoggable)) &&" +
            "!@annotation(com.socialnet.annotation.DebugLoggable) && " +
            "!@annotation(com.socialnet.annotation.NotLoggable)")
    public void info() {
    }

    @Pointcut("(@target(com.socialnet.annotation.Debug) || " +
            "@annotation(com.socialnet.annotation.DebugLoggable)) &&" +
            "!@annotation(com.socialnet.annotation.InfoLoggable) && " +
            "!@annotation(com.socialnet.annotation.NotLoggable)")
    public void debug() {
    }
}
