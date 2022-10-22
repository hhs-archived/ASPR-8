package tools.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for JUnit test classes
 * 
 * @author Shawn Hatch
 *
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface UnitTestMethod {
	String name();

	Class<?> target() default Object.class;

	Class<?>[] args();
	UnitTag[] tags() default {};
	
	boolean manual() default false;

}