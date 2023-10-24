package ru.skillbox.socialnet.aspect;

import org.aspectj.lang.annotation.Pointcut;

public class LoggingPointcuts {
    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *) || " +
            "within(@org.springframework.stereotype.Repository *) || " +
            "within(@org.springframework.stereotype.Component *) || " +
            "within(@org.springframework.stereotype.Service *)")
    public void springBeanPointcut() {
    }

    @Pointcut("within(ru.skillbox.socialnet..*) && !within(ru.skillbox.socialnet.security.JwtRequestFilter)")
    public void applicationPackagePointcut() {
    }
}
