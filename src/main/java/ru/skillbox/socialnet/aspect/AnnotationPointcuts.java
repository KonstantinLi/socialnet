package ru.skillbox.socialnet.aspect;

import org.aspectj.lang.annotation.Pointcut;

public class AnnotationPointcuts {
    @Pointcut("(@target(ru.skillbox.socialnet.annotation.Info) || " +
            "@annotation(ru.skillbox.socialnet.annotation.InfoLoggable)) &&" +
            "!@annotation(ru.skillbox.socialnet.annotation.DebugLoggable) && " +
            "!@annotation(ru.skillbox.socialnet.annotation.NotLoggable)")
    public void info() {
    }

    @Pointcut("(@target(ru.skillbox.socialnet.annotation.Debug) || " +
            "@annotation(ru.skillbox.socialnet.annotation.DebugLoggable)) &&" +
            "!@annotation(ru.skillbox.socialnet.annotation.InfoLoggable) && " +
            "!@annotation(ru.skillbox.socialnet.annotation.NotLoggable)")
    public void debug() {
    }
}
