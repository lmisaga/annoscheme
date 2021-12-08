package org.annoscheme.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
@Repeatable(Actions.class)
public @interface Action {

	String message() default "";

	ActionType actionType() default ActionType.ACTION;

	String[] diagramIdentifiers();

	String parentMessage() default "";

}
