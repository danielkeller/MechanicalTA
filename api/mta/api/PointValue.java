package mta.api;

import java.lang.annotation.*;

/**
 * Adding this annotation makes the method a test that is worth some number of points.
 * This annotation is used instead of <code>@Test</code>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface PointValue {

	/**
	 * The number of points this test is worth. This amount is added to the total value of the
	 * assignment unless the <code>@ExtraCredit</code> annotation is also present.
	 */
    int value();
    
    /**
     * Success of this test can add to the student's point total, but is not counted in the value
     * of the assignment.
     */
    boolean extraCredit() default false;
}
