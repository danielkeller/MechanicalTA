import org.junit.runner.RunWith;
import mta.api.*;

@RunWith(AssignmentRunner.class)
public class ExampleTest {
	
	@PointValue(5)
    public void thisAlwaysPasses() {
		new Foo();
    }

	@PointValue(7)
	public void thisFails() {
		throw new RuntimeException("I am bad at programming, and not good at all");
	}
	
	@PointValue(value=3, extraCredit=true)
	public void thisIsExtraCredit () {
	}
}
