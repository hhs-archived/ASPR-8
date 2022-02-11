package plugins.properties.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.naming.Context;

import org.apache.commons.math3.random.RandomGenerator;
import org.junit.jupiter.api.Test;

import nucleus.testsupport.MockContext;
import util.ContractException;
import util.MutableDouble;
import util.SeedProvider;
import util.annotations.UnitTest;
import util.annotations.UnitTestConstructor;
import util.annotations.UnitTestMethod;

/**
 * Common interface to all person property managers. A person property manager
 * manages all the property values for people for a particular person property
 * identifier.
 * 
 * @author Shawn Hatch
 *
 */

@UnitTest(target = EnumPropertyManager.class)
public class AT_EnumPropertyManager {

	@Test
	@UnitTestMethod(name = "getPropertyValue", args = { int.class })
	public void testGetPropertyValue() {
		RandomGenerator randomGenerator = SeedProvider.getRandomGenerator(5102684240650614254L);

		MockContext mockContext = MockContext.builder().build();

		Color defaultValue = Color.YELLOW;
		PropertyDefinition propertyDefinition = PropertyDefinition.builder().setType(Color.class).setDefaultValue(defaultValue).setTimeTrackingPolicy(TimeTrackingPolicy.TRACK_TIME).build();

		EnumPropertyManager enumPropertyManager = new EnumPropertyManager(mockContext, propertyDefinition, 0);

		/*
		 * We will set the first 300 values multiple times at random
		 */
		Map<Integer, Color> expectedValues = new LinkedHashMap<>();

		for (int i = 0; i < 1000; i++) {
			int id = randomGenerator.nextInt(300);
			Color value = Color.values()[randomGenerator.nextInt(Color.values().length)];
			expectedValues.put(id, value);
			enumPropertyManager.setPropertyValue(id, value);
		}

		/*
		 * if the value was set above, then it should equal the last value place
		 * in the expected values, otherwise it will have the default value.
		 */
		for (int i = 0; i < 300; i++) {
			if (expectedValues.containsKey(i)) {
				assertEquals(expectedValues.get(i), enumPropertyManager.getPropertyValue(i));

			} else {
				assertEquals(defaultValue, (Color) enumPropertyManager.getPropertyValue(i));

			}
		}

		// precondition tests
		ContractException contractException = assertThrows(ContractException.class, () -> enumPropertyManager.getPropertyValue(-1));
		assertEquals(PropertyError.NEGATIVE_INDEX, contractException.getErrorType());

	}

	@Test
	@UnitTestMethod(name = "getPropertyTime", args = { int.class })
	public void testGetPropertyTime() {
		/**
		 * Returns the assignment time when the id's property was last set. Note
		 * that this does not imply that the id exists in the simulation.
		 * 
		 * @throws RuntimeException
		 *             if time tracking is not turned on for this property via
		 *             the policies established in the scenario.
		 * 
		 */
		// public double getPropertyTime(int id);
		RandomGenerator randomGenerator = SeedProvider.getRandomGenerator(2965406559079298427L);

		MutableDouble time = new MutableDouble(0);
		MockContext mockContext = MockContext.builder().setTimeSupplier(() -> time.getValue()).build();

		PropertyDefinition propertyDefinition = PropertyDefinition.builder().setType(Color.class).setDefaultValue(Color.RED).build();

		EnumPropertyManager enumPropertyManager = new EnumPropertyManager(mockContext, propertyDefinition, 0);
		assertThrows(RuntimeException.class, () -> enumPropertyManager.getPropertyTime(0));

		propertyDefinition = PropertyDefinition.builder().setType(Color.class).setDefaultValue(Color.YELLOW).setTimeTrackingPolicy(TimeTrackingPolicy.TRACK_TIME).build();

		EnumPropertyManager enumPropertyManager2 = new EnumPropertyManager(mockContext, propertyDefinition, 0);
		for (int i = 0; i < 1000; i++) {
			int id = randomGenerator.nextInt(300);
			time.setValue(randomGenerator.nextDouble() * 1000);
			Color value = Color.values()[randomGenerator.nextInt(3)];
			enumPropertyManager2.setPropertyValue(id, value);
			assertEquals(time.getValue(), enumPropertyManager2.getPropertyTime(id), 0);
		}

		// precondition tests:
		propertyDefinition = PropertyDefinition.builder().setType(Color.class).setDefaultValue(Color.BLUE).build();
		EnumPropertyManager epm = new EnumPropertyManager(mockContext, propertyDefinition, 0);
		ContractException contractException = assertThrows(ContractException.class, () -> epm.getPropertyTime(0));
		assertEquals(PropertyError.TIME_TRACKING_OFF, contractException.getErrorType());

		contractException = assertThrows(ContractException.class, () -> epm.getPropertyTime(-1));
		assertEquals(PropertyError.NEGATIVE_INDEX, contractException.getErrorType());

	}

	@Test
	@UnitTestMethod(name = "setPropertyValue", args = { int.class, Object.class })
	public void testSetPropertyValue() {

		RandomGenerator randomGenerator = SeedProvider.getRandomGenerator(6716984272666831621L);

		MockContext mockContext = MockContext.builder().build();

		Color defaultValue = Color.YELLOW;
		PropertyDefinition propertyDefinition = PropertyDefinition.builder().setType(Color.class).setDefaultValue(defaultValue).setTimeTrackingPolicy(TimeTrackingPolicy.TRACK_TIME).build();

		EnumPropertyManager enumPropertyManager = new EnumPropertyManager(mockContext, propertyDefinition, 0);

		/*
		 * We will set the first 300 values multiple times at random
		 */
		Map<Integer, Color> expectedValues = new LinkedHashMap<>();

		for (int i = 0; i < 1000; i++) {
			int id = randomGenerator.nextInt(300);
			Color value = Color.values()[randomGenerator.nextInt(Color.values().length)];
			expectedValues.put(id, value);
			enumPropertyManager.setPropertyValue(id, value);
		}

		/*
		 * if the value was set above, then it should equal the last value place
		 * in the expected values, otherwise it will have the default value.
		 */
		for (int i = 0; i < 300; i++) {
			if (expectedValues.containsKey(i)) {
				assertEquals(expectedValues.get(i), enumPropertyManager.getPropertyValue(i));

			} else {
				assertEquals(defaultValue, (Color) enumPropertyManager.getPropertyValue(i));

			}
		}

		// precondition tests
		ContractException contractException = assertThrows(ContractException.class, () -> enumPropertyManager.setPropertyValue(-1, Color.BLUE));
		assertEquals(PropertyError.NEGATIVE_INDEX, contractException.getErrorType());

	}

	@Test
	@UnitTestMethod(name = "removeId", args = { int.class })
	public void testRemoveId() {
		/*
		 * Should have no effect on the value that is stored for the sake of
		 * efficiency.
		 */

		MockContext mockContext = MockContext.builder().build();

		// we will first test the manager with an initial value of false
		Color defaultValue = Color.RED;
		PropertyDefinition propertyDefinition = PropertyDefinition.builder().setType(Color.class).setDefaultValue(defaultValue).setTimeTrackingPolicy(TimeTrackingPolicy.TRACK_TIME).build();

		EnumPropertyManager enumPropertyManager = new EnumPropertyManager(mockContext, propertyDefinition, 0);

		// initially, the value should be the default value for the manager
		assertEquals(defaultValue, (Color) enumPropertyManager.getPropertyValue(5));

		// after setting the value we should be able to retrieve a new value
		Color newValue = Color.BLUE;
		enumPropertyManager.setPropertyValue(5, newValue);
		assertEquals(newValue, (Color) enumPropertyManager.getPropertyValue(5));

		// removing the id from the manager should have no effect, since we do
		// not waste time setting the value back to the default
		enumPropertyManager.removeId(5);

		assertEquals(newValue, (Color) enumPropertyManager.getPropertyValue(5));

		// we will next test the manager with an initial value of true
		propertyDefinition = PropertyDefinition.builder().setType(Color.class).setDefaultValue(defaultValue).setTimeTrackingPolicy(TimeTrackingPolicy.TRACK_TIME).build();

		enumPropertyManager = new EnumPropertyManager(mockContext, propertyDefinition, 0);

		// initially, the value should be the default value for the manager
		assertEquals(defaultValue, (Color) enumPropertyManager.getPropertyValue(5));

		// after setting the value we should be able to retrieve the new value
		enumPropertyManager.setPropertyValue(5, newValue);
		assertEquals(newValue, (Color) enumPropertyManager.getPropertyValue(5));

		// removing the id from the manager should have no effect, since we do
		// not waste time setting the value back to the default
		enumPropertyManager.removeId(5);

		assertEquals(newValue, (Color) enumPropertyManager.getPropertyValue(5));

		// precondition tests
		// precondition tests
		PropertyDefinition def = PropertyDefinition.builder().setType(Color.class).setDefaultValue(Color.YELLOW).setTimeTrackingPolicy(TimeTrackingPolicy.TRACK_TIME).build();
		EnumPropertyManager epm = new EnumPropertyManager(mockContext, def, 0);

		ContractException contractException = assertThrows(ContractException.class, () -> epm.removeId(-1));

		assertEquals(PropertyError.NEGATIVE_INDEX, contractException.getErrorType());

	}

	// Helper enum
	private static enum Color {
		RED, YELLOW, BLUE;
	}

	@Test
	@UnitTestConstructor(args = { Context.class, PropertyDefinition.class, int.class })
	public void testConstructor() {
		MockContext mockContext = MockContext.builder().build();

		PropertyDefinition goodPropertyDefinition = PropertyDefinition.builder().setType(Color.class).setDefaultValue(Color.BLUE).build();
		PropertyDefinition badPropertyDefinition = PropertyDefinition.builder().setType(Boolean.class).setDefaultValue(false).build();
		PropertyDefinition badDoublePropertyDefinition = PropertyDefinition.builder().setType(Double.class).build();

		// if the property definition is null
		ContractException contractException = assertThrows(ContractException.class, () -> new EnumPropertyManager(mockContext, null, 0));
		assertEquals(PropertyError.NULL_PROPERTY_DEFINITION, contractException.getErrorType());

		// if the property definition does not have a type of Enum.class
		contractException = assertThrows(ContractException.class, () -> new EnumPropertyManager(mockContext, badPropertyDefinition, 0));
		assertEquals(PropertyError.PROPERTY_DEFINITION_IMPROPER_TYPE, contractException.getErrorType());

		// if the property definition does not contain a default value
		contractException = assertThrows(ContractException.class, () -> new EnumPropertyManager(mockContext, badDoublePropertyDefinition, 0));
		assertEquals(PropertyError.PROPERTY_DEFINITION_MISSING_DEFAULT, contractException.getErrorType());

		// if the initial size is negative
		contractException = assertThrows(ContractException.class, () -> new EnumPropertyManager(mockContext, goodPropertyDefinition, -1));
		assertEquals(PropertyError.NEGATIVE_INITIAL_SIZE, contractException.getErrorType());

		EnumPropertyManager enumPropertyManager = new EnumPropertyManager(mockContext, goodPropertyDefinition, 0);
		assertNotNull(enumPropertyManager);

	}
	
	@Test
	@UnitTestMethod(name = "incrementCapacity", args = { int.class })
	public void testIncrementCapacity() {
		MutableDouble time = new MutableDouble(0);
		MockContext mockContext = MockContext.builder().setTimeSupplier(() -> time.getValue()).build();

		PropertyDefinition propertyDefinition = PropertyDefinition.builder().setType(Color.class).setDefaultValue(Color.RED).setTimeTrackingPolicy(TimeTrackingPolicy.TRACK_TIME).build();

		EnumPropertyManager enumPropertyManager = new EnumPropertyManager(mockContext, propertyDefinition, 0);

		// precondition tests
		ContractException contractException = assertThrows(ContractException.class, () -> enumPropertyManager.incrementCapacity(-1));
		assertEquals(PropertyError.NEGATIVE_CAPACITY_INCREMENT, contractException.getErrorType());
	}


}
