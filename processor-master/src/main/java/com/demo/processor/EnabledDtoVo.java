package com.demo.processor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Administrator
 * @title: LXC
 * @projectName demo
 * @date 2022/1/2615:41
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface EnabledDtoVo {
}
