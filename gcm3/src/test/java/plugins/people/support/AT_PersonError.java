package plugins.people.support;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import tools.annotations.UnitTest;
import tools.annotations.UnitTestMethod;

@UnitTest(target = PersonError.class)
public class AT_PersonError {

	@Test
	@UnitTestMethod(name = "getDescription", args = {})
	public void test() {
		//show that each description is a unique, non-null and non-empty string 
		Set<String> descriptions = new LinkedHashSet<>();
		for(PersonError personError : PersonError.values()) {
			String description = personError.getDescription();			
			assertNotNull(description,"null description for "+personError);			
			assertTrue(description.length()>0, "empty string for "+personError);
			boolean unique = descriptions.add(description);
			assertTrue(unique,"description for "+personError+" is not unique");
		}
	}
}
