package org.annoscheme.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.RUNTIME)
public @interface Conditional {

	String condition() default "";

	BranchingType type() default BranchingType.JOINING;

	boolean joining() default false;

	String[] diagramIdentifiers() default {};

}
