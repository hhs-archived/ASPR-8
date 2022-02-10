package plugins.personproperties.datacontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.random.RandomGenerator;
import org.junit.jupiter.api.Test;

import nucleus.SimulationContext;
import nucleus.DataView;
import nucleus.NucleusError;
import nucleus.testsupport.actionplugin.ActionPluginInitializer;
import nucleus.testsupport.actionplugin.AgentActionPlan;
import plugins.people.datacontainers.PersonDataView;
import plugins.people.support.PersonError;
import plugins.people.support.PersonId;
import plugins.personproperties.events.mutation.PersonPropertyValueAssignmentEvent;
import plugins.personproperties.support.PersonPropertyError;
import plugins.personproperties.support.PersonPropertyId;
import plugins.personproperties.testsupport.PersonPropertiesActionSupport;
import plugins.personproperties.testsupport.TestPersonPropertyId;
import plugins.properties.support.PropertyDefinition;
import plugins.stochastics.StochasticsDataManager;
import util.ContractException;
import util.MutableInteger;
import util.annotations.UnitTest;
import util.annotations.UnitTestConstructor;
import util.annotations.UnitTestMethod;

@UnitTest(target = PersonPropertyDataView.class)
public class AT_PersonPropertyDataView implements DataView {

	

	@Test
	@UnitTestConstructor(args = { SimulationContext.class, PersonPropertyDataManager.class })
	public void testConstructor() {
		PersonPropertiesActionSupport.testConsumer(0, 9132573852302490924L, (c) -> {
			PersonPropertyDataManager personPropertyDataManager = new PersonPropertyDataManager(c);

			ContractException contractException = assertThrows(ContractException.class, () -> new PersonPropertyDataView(null, personPropertyDataManager));
			assertEquals(NucleusError.NULL_CONTEXT, contractException.getErrorType());

			contractException = assertThrows(ContractException.class, () -> new PersonPropertyDataView(c, null));
			assertEquals(PersonPropertyError.NULL_PERSON_PROPERTY_DATA_MANAGER, contractException.getErrorType());

		});
	}

	@Test
	@UnitTestMethod(name = "getPersonPropertyIds", args = {})
	public void testGetPersonPropertyIds() {

		PersonPropertiesActionSupport.testConsumer(0, 8485097765777963229L, (c) -> {
			PersonPropertyDataView personPropertyDataView = c.getDataView(PersonPropertyDataView.class).get();
			EnumSet<TestPersonPropertyId> expectedPropertyIds = EnumSet.allOf(TestPersonPropertyId.class);
			Set<PersonPropertyId> actualPropertyIds = personPropertyDataView.getPersonPropertyIds();
			assertEquals(expectedPropertyIds, actualPropertyIds);
		});

	}

	@Test
	@UnitTestMethod(name = "personPropertyIdExists", args = { PersonPropertyId.class })
	public void testPersonPropertyIdExists() {

		PersonPropertiesActionSupport.testConsumer(0, 4797443283568888200L, (c) -> {
			PersonPropertyDataView personPropertyDataView = c.getDataView(PersonPropertyDataView.class).get();
			for (TestPersonPropertyId testPersonPropertyId : TestPersonPropertyId.values()) {
				assertTrue(personPropertyDataView.personPropertyIdExists(testPersonPropertyId));
			}
			assertFalse(personPropertyDataView.personPropertyIdExists(TestPersonPropertyId.getUnknownPersonPropertyId()));
		});

	}

	@Test
	@UnitTestMethod(name = "getPersonPropertyDefinition", args = { PersonPropertyId.class })
	public void testGetPersonPropertyDefinition() {

		PersonPropertiesActionSupport.testConsumer(0, 138806179316502662L, (c) -> {
			PersonPropertyDataView personPropertyDataView = c.getDataView(PersonPropertyDataView.class).get();

			// show that the person property definitions match expectations
			for (TestPersonPropertyId testPersonPropertyId : TestPersonPropertyId.values()) {
				PropertyDefinition expectedPropertyDefinition = testPersonPropertyId.getPropertyDefinition();
				PropertyDefinition actualPropertyDefinition = personPropertyDataView.getPersonPropertyDefinition(testPersonPropertyId);
				assertEquals(expectedPropertyDefinition, actualPropertyDefinition);
			}

			// precondition tests

			// if the person property id is null
			ContractException contractException = assertThrows(ContractException.class, () -> personPropertyDataView.getPersonPropertyDefinition(null));
			assertEquals(PersonPropertyError.NULL_PERSON_PROPERTY_ID, contractException.getErrorType());

			// if the person property id is unknown
			contractException = assertThrows(ContractException.class, () -> personPropertyDataView.getPersonPropertyDefinition(TestPersonPropertyId.getUnknownPersonPropertyId()));
			assertEquals(PersonPropertyError.UNKNOWN_PERSON_PROPERTY_ID, contractException.getErrorType());

		});
	}

	@Test
	@UnitTestMethod(name = "getPersonPropertyValue", args = { PersonId.class, PersonPropertyId.class })
	public void testGetPersonPropertyValue() {

		PersonPropertiesActionSupport.testConsumer(10, 816143115345188642L, (c) -> {
			PersonPropertyDataView personPropertyDataView = c.getDataView(PersonPropertyDataView.class).get();
			PersonDataView personDataView = c.getDataView(PersonDataView.class).get();
			StochasticsDataManager stochasticsDataManager = c.getDataView(StochasticsDataManager.class).get();
			RandomGenerator randomGenerator = stochasticsDataManager.getRandomGenerator();

			// create a container to hold expectations
			Map<PersonId, Integer> expectedValues = new LinkedHashMap<>();

			// assign random values for property 2 for all the people
			List<PersonId> people = personDataView.getPeople();
			for (PersonId personId : people) {
				int value = randomGenerator.nextInt();
				c.resolveEvent(new PersonPropertyValueAssignmentEvent(personId, TestPersonPropertyId.PERSON_PROPERTY_2_INTEGER_MUTABLE_NO_TRACK, value));
				expectedValues.put(personId, value);
			}

			// show that the values retrieved match expectations
			for (PersonId personId : people) {
				Integer expectedValue = expectedValues.get(personId);
				Integer actualValue = personPropertyDataView.getPersonPropertyValue(personId, TestPersonPropertyId.PERSON_PROPERTY_2_INTEGER_MUTABLE_NO_TRACK);
				assertEquals(expectedValue, actualValue);
			}

			// precondition tests
			PersonId personId = new PersonId(0);
			PersonId unknownPersonId = new PersonId(100000);
			PersonPropertyId personPropertyId = TestPersonPropertyId.PERSON_PROPERTY_2_INTEGER_MUTABLE_NO_TRACK;
			PersonPropertyId unknownPersonPropertyId = TestPersonPropertyId.getUnknownPersonPropertyId();

			// if the person id is null
			ContractException contractException = assertThrows(ContractException.class, () -> personPropertyDataView.getPersonPropertyValue(null, personPropertyId));
			assertEquals(PersonError.NULL_PERSON_ID, contractException.getErrorType());

			// if the person id is unknown
			contractException = assertThrows(ContractException.class, () -> personPropertyDataView.getPersonPropertyValue(unknownPersonId, personPropertyId));
			assertEquals(PersonError.UNKNOWN_PERSON_ID, contractException.getErrorType());

			// if the person property id is null
			contractException = assertThrows(ContractException.class, () -> personPropertyDataView.getPersonPropertyValue(personId, null));
			assertEquals(PersonPropertyError.NULL_PERSON_PROPERTY_ID, contractException.getErrorType());

			// if the person property id is unknown
			contractException = assertThrows(ContractException.class, () -> personPropertyDataView.getPersonPropertyValue(personId, unknownPersonPropertyId));
			assertEquals(PersonPropertyError.UNKNOWN_PERSON_PROPERTY_ID, contractException.getErrorType());

		});
	}

	@Test
	@UnitTestMethod(name = "getPersonPropertyTime", args = { PersonId.class, PersonPropertyId.class })
	public void testGetPersonPropertyTime() {

		ActionPluginInitializer.Builder pluginBuilder = ActionPluginInitializer.builder();
		pluginBuilder.addAgent("agent");

		// show that all person property times are 0 for the time-tracked
		// properties
		pluginBuilder.addAgentActionPlan("agent", new AgentActionPlan(0, (c) -> {

			PersonPropertyDataView personPropertyDataView = c.getDataView(PersonPropertyDataView.class).get();

			PersonDataView personDataView = c.getDataView(PersonDataView.class).get();
			List<PersonId> people = personDataView.getPeople();
			for (PersonId personId : people) {
				double personPropertyTime = personPropertyDataView.getPersonPropertyTime(personId, TestPersonPropertyId.PERSON_PROPERTY_4_BOOLEAN_MUTABLE_TRACK);
				assertEquals(0.0, personPropertyTime);
				personPropertyTime = personPropertyDataView.getPersonPropertyTime(personId, TestPersonPropertyId.PERSON_PROPERTY_5_INTEGER_MUTABLE_TRACK);
				assertEquals(0.0, personPropertyTime);
				personPropertyTime = personPropertyDataView.getPersonPropertyTime(personId, TestPersonPropertyId.PERSON_PROPERTY_6_DOUBLE_MUTABLE_TRACK);
				assertEquals(0.0, personPropertyTime);
			}
		}));

		// Set property 5 for all people at time 1
		pluginBuilder.addAgentActionPlan("agent", new AgentActionPlan(1, (c) -> {

			PersonDataView personDataView = c.getDataView(PersonDataView.class).get();
			List<PersonId> people = personDataView.getPeople();
			RandomGenerator randomGenerator = c.getDataView(StochasticsDataManager.class).get().getRandomGenerator();
			for (PersonId personId : people) {
				c.resolveEvent(new PersonPropertyValueAssignmentEvent(personId, TestPersonPropertyId.PERSON_PROPERTY_5_INTEGER_MUTABLE_TRACK, randomGenerator.nextInt()));
			}
		}));

		// Set property 6 for all people at time 2
		pluginBuilder.addAgentActionPlan("agent", new AgentActionPlan(2, (c) -> {

			PersonDataView personDataView = c.getDataView(PersonDataView.class).get();
			List<PersonId> people = personDataView.getPeople();
			RandomGenerator randomGenerator = c.getDataView(StochasticsDataManager.class).get().getRandomGenerator();
			for (PersonId personId : people) {
				c.resolveEvent(new PersonPropertyValueAssignmentEvent(personId, TestPersonPropertyId.PERSON_PROPERTY_6_DOUBLE_MUTABLE_TRACK, randomGenerator.nextDouble()));
			}
		}));

		// show that the person property times agree with the times above
		pluginBuilder.addAgentActionPlan("agent", new AgentActionPlan(3, (c) -> {
			PersonPropertyDataView personPropertyDataView = c.getDataView(PersonPropertyDataView.class).get();
			PersonDataView personDataView = c.getDataView(PersonDataView.class).get();
			List<PersonId> people = personDataView.getPeople();
			for (PersonId personId : people) {
				double personPropertyTime = personPropertyDataView.getPersonPropertyTime(personId, TestPersonPropertyId.PERSON_PROPERTY_4_BOOLEAN_MUTABLE_TRACK);
				assertEquals(0.0, personPropertyTime);
				personPropertyTime = personPropertyDataView.getPersonPropertyTime(personId, TestPersonPropertyId.PERSON_PROPERTY_5_INTEGER_MUTABLE_TRACK);
				assertEquals(1.0, personPropertyTime);
				personPropertyTime = personPropertyDataView.getPersonPropertyTime(personId, TestPersonPropertyId.PERSON_PROPERTY_6_DOUBLE_MUTABLE_TRACK);
				assertEquals(2.0, personPropertyTime);
			}
		}));

		// precondition tests
		pluginBuilder.addAgentActionPlan("agent", new AgentActionPlan(4, (c) -> {
			PersonPropertyDataView personPropertyDataView = c.getDataView(PersonPropertyDataView.class).get();

			PersonId personId = new PersonId(0);
			PersonId unknownPersonId = new PersonId(100000);
			PersonPropertyId personPropertyId = TestPersonPropertyId.PERSON_PROPERTY_5_INTEGER_MUTABLE_TRACK;
			PersonPropertyId unknownPersonPropertyId = TestPersonPropertyId.getUnknownPersonPropertyId();
			PersonPropertyId untrackedPersonPropertyId = TestPersonPropertyId.PERSON_PROPERTY_1_BOOLEAN_MUTABLE_NO_TRACK;

			// if the person id is null
			ContractException contractException = assertThrows(ContractException.class, () -> personPropertyDataView.getPersonPropertyTime(null, personPropertyId));
			assertEquals(PersonError.NULL_PERSON_ID, contractException.getErrorType());

			// if the person id is unknown
			contractException = assertThrows(ContractException.class, () -> personPropertyDataView.getPersonPropertyTime(unknownPersonId, personPropertyId));
			assertEquals(PersonError.UNKNOWN_PERSON_ID, contractException.getErrorType());

			// if the person property id is null
			contractException = assertThrows(ContractException.class, () -> personPropertyDataView.getPersonPropertyTime(personId, null));
			assertEquals(PersonPropertyError.NULL_PERSON_PROPERTY_ID, contractException.getErrorType());

			// if the person property id is unknown
			contractException = assertThrows(ContractException.class, () -> personPropertyDataView.getPersonPropertyTime(personId, unknownPersonPropertyId));
			assertEquals(PersonPropertyError.UNKNOWN_PERSON_PROPERTY_ID, contractException.getErrorType());

			// if the person property does not have time tracking turned on in
			// the associated property definition
			contractException = assertThrows(ContractException.class, () -> personPropertyDataView.getPersonPropertyTime(personId, untrackedPersonPropertyId));
			assertEquals(PersonPropertyError.PROPERTY_ASSIGNMENT_TIME_NOT_TRACKED, contractException.getErrorType());

		}));

		PersonPropertiesActionSupport.testConsumers(10, 6980289425630085602L, pluginBuilder.build());

	}

	@Test
	@UnitTestMethod(name = "getPeopleWithPropertyValue", args = { PersonPropertyId.class, Object.class })
	public void testGetPeopleWithPropertyValue() {

		PersonPropertiesActionSupport.testConsumer(100, 7917315534360369845L, (c) -> {

			// establish data views
			PersonPropertyDataView personPropertyDataView = c.getDataView(PersonPropertyDataView.class).get();
			PersonDataView personDataView = c.getDataView(PersonDataView.class).get();
			StochasticsDataManager stochasticsDataManager = c.getDataView(StochasticsDataManager.class).get();
			RandomGenerator randomGenerator = stochasticsDataManager.getRandomGenerator();

			/*
			 * Assign random values of 1, 2 or 3 for property 2 to all people.
			 * Build a structure to hold expected results.
			 */
			List<PersonId> people = personDataView.getPeople();
			Map<Integer, Set<PersonId>> expectedValuesToPeople = new LinkedHashMap<>();
			for (int i = 0; i < 3; i++) {
				expectedValuesToPeople.put(i, new LinkedHashSet<>());
			}

			int defaultValue = (Integer) personPropertyDataView.getPersonPropertyDefinition(TestPersonPropertyId.PERSON_PROPERTY_2_INTEGER_MUTABLE_NO_TRACK).getDefaultValue().get();
			for (PersonId personId : people) {
				int value = randomGenerator.nextInt(3);
				// leave out the people with the default value to show that we
				// can still retrieve them by the value
				if (value != defaultValue) {
					c.resolveEvent(new PersonPropertyValueAssignmentEvent(personId, TestPersonPropertyId.PERSON_PROPERTY_2_INTEGER_MUTABLE_NO_TRACK, value));
				}
				expectedValuesToPeople.get(value).add(personId);
			}

			// show that the proper people are returned for each value
			for (Integer value : expectedValuesToPeople.keySet()) {
				List<PersonId> actualPeople = personPropertyDataView.getPeopleWithPropertyValue(TestPersonPropertyId.PERSON_PROPERTY_2_INTEGER_MUTABLE_NO_TRACK, value);
				Set<PersonId> expectedPeople = expectedValuesToPeople.get(value);
				assertEquals(expectedPeople.size(), actualPeople.size());
				assertEquals(expectedPeople, new LinkedHashSet<>(actualPeople));
			}

		});

	}

	@Test
	@UnitTestMethod(name = "getPersonCountForPropertyValue", args = { PersonPropertyId.class, Object.class })
	public void testGetPersonCountForPropertyValue() {
		 
		PersonPropertiesActionSupport.testConsumer(100, 686456599634987511L, (c) -> {


			// establish data views
			PersonPropertyDataView personPropertyDataView = c.getDataView(PersonPropertyDataView.class).get();
			PersonDataView personDataView = c.getDataView(PersonDataView.class).get();
			StochasticsDataManager stochasticsDataManager = c.getDataView(StochasticsDataManager.class).get();
			RandomGenerator randomGenerator = stochasticsDataManager.getRandomGenerator();

			/*
			 * Assign random values of 1, 2 or 3 for property 2 to all people.
			 * Build a structure to hold expected results.
			 */
			List<PersonId> people = personDataView.getPeople();
			Map<Integer, MutableInteger> expectedValuesToPeople = new LinkedHashMap<>();
			for (int i = 0; i < 3; i++) {
				expectedValuesToPeople.put(i, new MutableInteger());
			}

			int defaultValue = (Integer) personPropertyDataView.getPersonPropertyDefinition(TestPersonPropertyId.PERSON_PROPERTY_2_INTEGER_MUTABLE_NO_TRACK).getDefaultValue().get();
			for (PersonId personId : people) {
				int value = randomGenerator.nextInt(3);
				// leave out the people with the default value to show that we
				// can still retrieve them by the value
				if (value != defaultValue) {
					c.resolveEvent(new PersonPropertyValueAssignmentEvent(personId, TestPersonPropertyId.PERSON_PROPERTY_2_INTEGER_MUTABLE_NO_TRACK, value));
				}
				expectedValuesToPeople.get(value).increment();
			}

			// show that the proper counts are returned for each value
			for (Integer value : expectedValuesToPeople.keySet()) {
				int actualCount = personPropertyDataView.getPersonCountForPropertyValue(TestPersonPropertyId.PERSON_PROPERTY_2_INTEGER_MUTABLE_NO_TRACK, value);
				MutableInteger mutableInteger = expectedValuesToPeople.get(value);
				assertEquals(mutableInteger.getValue(), actualCount);
			}

		});
	}

}
