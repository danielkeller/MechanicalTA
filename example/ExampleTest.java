
import org.junit.Assert;

import org.junit.runner.RunWith;

import mta.api.*;

@RunWith(AssignmentRunner.class)
public class ExampleTest {
	
	ExampleInterface test;
	
	@ModelImpl(ExampleModel.class)
	public ExampleTest(ExampleInterface studentClass) {
		test = studentClass;
	}
	
	@PointValue(5)
    public void testReturn3() {
		Assert.assertEquals(test.returns3(), 3);
    }

	@PointValue(7)
	public void thisFails() {
		throw new RuntimeException("I am bad at programming, and not good at all");
	}
	
	@PointValue(value=3, extraCredit=true)
	public void thisIsExtraCredit () {
	}
}
