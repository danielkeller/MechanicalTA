package mta.api;

import java.lang.annotation.*;

/**
 * This annotation is optional, but recommended to add to a constructor.
 * It specifies the class that is used as a model implementation to write
 * tests against. An instance of this class is passed to the constructor
 * when the test is run by JUnit (as opposed to by MechanicalTA).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.CONSTRUCTOR})
public @interface ModelImpl {
	Class<?> value();
}
