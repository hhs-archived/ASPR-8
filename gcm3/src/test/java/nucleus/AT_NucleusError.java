package nucleus;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import util.annotations.UnitTest;
import util.annotations.UnitTestMethod;

/**
 * Test class for {@link NucleusError}
 * 
 * @author Shawn Hatch
 *
 */
@UnitTest(target = NucleusError.class)
public class AT_NucleusError {

	/**
	 * Tests {@link NucleusError#getDescription()}
	 */
	@Test
	@UnitTestMethod(name = "getDescription", args = {})
	public void testGetDescription() {
		// show that each ErrorType has a non-null, non-empty description
		for (NucleusError nucleusError : NucleusError.values()) {
			assertNotNull(nucleusError.getDescription());
			assertTrue(nucleusError.getDescription().length() > 0);
		}

		// show that each description is unique (ignoring case as well)
		Set<String> descriptions = new LinkedHashSet<>();
		for (NucleusError nucleusError : NucleusError.values()) {
			assertTrue(descriptions.add(nucleusError.getDescription().toLowerCase()), nucleusError+": "+"Duplicate ErrorType description: " + nucleusError.getDescription());
		}
	}

	/**
	 * Tests {@link NucleusError#valueOf(String)}
	 */
	@Test
	@UnitTestMethod(name = "valueOf", args = { String.class })
	public void testValueOf() {
		// nothing to test
	}

	/**
	 * Tests {@link NucleusError#values()}
	 */
	@Test
	@UnitTestMethod(name = "values", args = {})
	public void testValues() {
		// nothing to test
	}

}
