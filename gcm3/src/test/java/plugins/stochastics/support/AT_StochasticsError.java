package plugins.stochastics.support;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import tools.annotations.UnitTest;
import tools.annotations.UnitTestMethod;

@UnitTest(target = StochasticsError.class)
public class AT_StochasticsError {

	@Test
	@UnitTestMethod(name = "getDescription", args = {})
	public void test() {
		
		//show that each description is a unique, non-null and non-empty string 
		Set<String> descriptions = new LinkedHashSet<>();
		for(StochasticsError stochasticsError : StochasticsError.values()) {
			String description = stochasticsError.getDescription();			
			assertNotNull(description,"null description for "+stochasticsError);			
			assertTrue(description.length()>0, "empty string for "+stochasticsError);
			boolean unique = descriptions.add(description);
			assertTrue(unique,"description for "+stochasticsError+" is not unique");
		}
	}
}
