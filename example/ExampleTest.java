
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
    public void is5Prime() {
		Assert.assertTrue(test.isPrime(5));
    }
	
	@PointValue(5)
    public void is899Prime() {
		Assert.assertFalse(test.isPrime(899));
    }
	
	@PointValue(value=3, extraCredit=true)
	public void thisOneIsHarder () {
		Assert.assertTrue(!test.isPrime(1));
	}

	@PointValue(7)
	public void primesBelow50() {
		Assert.assertEquals(669, test.primesLessThan(5000));
	}
}
