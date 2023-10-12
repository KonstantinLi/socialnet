package ru.skillbox.socialnet.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * аннотация, для методов, на которые будет наложена "точка присоединения"
 * функционал описан в классе OnlineStatusChecker
 * в программном коде "точки присоединения" устанавливается OnLineStatus и LastOnlineTime пользователя
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OnlineStatusUpdate {
}
