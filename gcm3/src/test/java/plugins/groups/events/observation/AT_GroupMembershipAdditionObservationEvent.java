package plugins.groups.events.observation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import nucleus.SimulationContext;
import nucleus.EventLabel;
import nucleus.EventLabeler;
import plugins.groups.datacontainers.PersonGroupDataView;
import plugins.groups.events.mutation.GroupCreationEvent;
import plugins.groups.support.GroupError;
import plugins.groups.support.GroupId;
import plugins.groups.support.GroupTypeId;
import plugins.groups.testsupport.GroupsActionSupport;
import plugins.groups.testsupport.TestGroupTypeId;
import plugins.people.datacontainers.PersonDataView;
import plugins.people.support.PersonError;
import plugins.people.support.PersonId;
import util.ContractException;
import util.annotations.UnitTest;
import util.annotations.UnitTestConstructor;
import util.annotations.UnitTestMethod;

@UnitTest(target = GroupMembershipAdditionObservationEvent.class)
public class AT_GroupMembershipAdditionObservationEvent {

	@Test
	@UnitTestConstructor(args = { PersonId.class, GroupId.class })
	public void testConstructor() {
		// nothing to test
	}

	@Test
	@UnitTestMethod(name = "getGroupId", args = {})
	public void testGetGroupId() {
		PersonId personId = new PersonId(12);
		GroupId groupId = new GroupId(23);
		GroupMembershipAdditionObservationEvent groupMembershipAdditionObservationEvent = new GroupMembershipAdditionObservationEvent(personId, groupId);
		assertEquals(groupId, groupMembershipAdditionObservationEvent.getGroupId());
	}

	@Test
	@UnitTestMethod(name = "getPersonId", args = {})
	public void testGetPersonId() {
		PersonId personId = new PersonId(12);
		GroupId groupId = new GroupId(23);
		GroupMembershipAdditionObservationEvent groupMembershipAdditionObservationEvent = new GroupMembershipAdditionObservationEvent(personId, groupId);
		assertEquals(personId, groupMembershipAdditionObservationEvent.getPersonId());
	}

	@Test
	@UnitTestMethod(name = "getEventLabelByGroupAndPerson", args = { SimulationContext.class, GroupId.class, PersonId.class })
	public void testGetEventLabelByGroupAndPerson() {

		GroupsActionSupport.testConsumer(30, 3, 5, 298549072627101248L, (c) -> {

			PersonGroupDataView personGroupDataView = c.getDataView(PersonGroupDataView.class).get();
			List<GroupId> groupIds = personGroupDataView.getGroupIds();

			Set<EventLabel<GroupMembershipAdditionObservationEvent>> eventLabels = new LinkedHashSet<>();

			PersonId personId = new PersonId(0);

			for (GroupId groupId : groupIds) {

				EventLabel<GroupMembershipAdditionObservationEvent> eventLabel = GroupMembershipAdditionObservationEvent.getEventLabelByGroupAndPerson(c, groupId, personId);

				// show that the event label has the correct event class
				assertEquals(GroupMembershipAdditionObservationEvent.class, eventLabel.getEventClass());

				// show that the event label has the correct primary key
				assertEquals(GroupMembershipAdditionObservationEvent.class, eventLabel.getPrimaryKeyValue());

				// show that the event label has the same id as its
				// associated labeler
				EventLabeler<GroupMembershipAdditionObservationEvent> eventLabeler = GroupMembershipAdditionObservationEvent.getEventLabelerForGroupAndPerson();
				assertEquals(eventLabeler.getId(), eventLabel.getLabelerId());

				// show that two event labels with the same inputs are equal
				EventLabel<GroupMembershipAdditionObservationEvent> eventLabel2 = GroupMembershipAdditionObservationEvent.getEventLabelByGroupAndPerson(c, groupId, personId);
				assertEquals(eventLabel, eventLabel2);

				// show that equal event labels have equal hash codes
				assertEquals(eventLabel.hashCode(), eventLabel2.hashCode());

				// show that two event labels with different inputs are not
				// equal
				assertTrue(eventLabels.add(eventLabel));
			}

			// precondition tests

			// if the group id is null
			ContractException contractException = assertThrows(ContractException.class, () -> GroupMembershipAdditionObservationEvent.getEventLabelByGroupAndPerson(c, null, personId));
			assertEquals(GroupError.NULL_GROUP_ID, contractException.getErrorType());

			// if the group id is unknown
			contractException = assertThrows(ContractException.class, () -> GroupMembershipAdditionObservationEvent.getEventLabelByGroupAndPerson(c, new GroupId(100000), personId));
			assertEquals(GroupError.UNKNOWN_GROUP_ID, contractException.getErrorType());

			// if the person id is null
			contractException = assertThrows(ContractException.class, () -> GroupMembershipAdditionObservationEvent.getEventLabelByGroupAndPerson(c, new GroupId(0), null));
			assertEquals(PersonError.NULL_PERSON_ID, contractException.getErrorType());

			// if the group id is unknown
			contractException = assertThrows(ContractException.class, () -> GroupMembershipAdditionObservationEvent.getEventLabelByGroupAndPerson(c, new GroupId(0), new PersonId(10000)));
			assertEquals(PersonError.UNKNOWN_PERSON_ID, contractException.getErrorType());

		});
	}

	@Test
	@UnitTestMethod(name = "getEventLabelerForGroupAndPerson", args = {})
	public void testGetEventLabelerForGroupAndPerson() {

		GroupsActionSupport.testConsumer(30, 3, 5, 4452567174321509486L, (c) -> {

			PersonGroupDataView personGroupDataView = c.getDataView(PersonGroupDataView.class).get();
			List<GroupId> groupIds = personGroupDataView.getGroupIds();

			// create an event labeler
			EventLabeler<GroupMembershipAdditionObservationEvent> eventLabeler = GroupMembershipAdditionObservationEvent.getEventLabelerForGroupAndPerson();

			// show that the event labeler has the correct event class
			assertEquals(GroupMembershipAdditionObservationEvent.class, eventLabeler.getEventClass());

			// show that the event labeler produces the expected event label

			PersonId personId = new PersonId(0);
			for (GroupId groupId : groupIds) {

				// derive the expected event label for this event
				EventLabel<GroupMembershipAdditionObservationEvent> expectedEventLabel = GroupMembershipAdditionObservationEvent.getEventLabelByGroupAndPerson(c, groupId, personId);

				// show that the event label and event labeler have equal id
				// values
				assertEquals(expectedEventLabel.getLabelerId(), eventLabeler.getId());

				// create an event
				GroupMembershipAdditionObservationEvent event = new GroupMembershipAdditionObservationEvent(personId, groupId);

				// show that the event labeler produces the correct event
				// label
				EventLabel<GroupMembershipAdditionObservationEvent> actualEventLabel = eventLabeler.getEventLabel(c, event);

				assertEquals(expectedEventLabel, actualEventLabel);

			}

		});

	}

	@Test
	@UnitTestMethod(name = "getEventLabelByGroup", args = { SimulationContext.class, GroupId.class })
	public void testGetEventLabelByGroup() {

		GroupsActionSupport.testConsumer(0, 3, 5, 8484038291544974628L, (c) -> {
			PersonGroupDataView personGroupDataView = c.getDataView(PersonGroupDataView.class).get();
			Set<EventLabel<GroupMembershipAdditionObservationEvent>> eventLabels = new LinkedHashSet<>();
			TestGroupTypeId testGroupTypeId = TestGroupTypeId.GROUP_TYPE_1;
			for (int i = 0; i < 10; i++) {
				c.resolveEvent(new GroupCreationEvent(testGroupTypeId));
				GroupId groupId = personGroupDataView.getLastIssuedGroupId().get();

				testGroupTypeId = testGroupTypeId.next();

				EventLabel<GroupMembershipAdditionObservationEvent> eventLabel = GroupMembershipAdditionObservationEvent.getEventLabelByGroup(c, groupId);

				// show that the event label has the correct event class
				assertEquals(GroupMembershipAdditionObservationEvent.class, eventLabel.getEventClass());

				// show that the event label has the correct primary key
				assertEquals(GroupMembershipAdditionObservationEvent.class, eventLabel.getPrimaryKeyValue());

				// show that the event label has the same id as its
				// associated labeler
				EventLabeler<GroupMembershipAdditionObservationEvent> eventLabeler = GroupMembershipAdditionObservationEvent.getEventLabelerForGroup();
				assertEquals(eventLabeler.getId(), eventLabel.getLabelerId());

				// show that two event labels with the same inputs are equal
				EventLabel<GroupMembershipAdditionObservationEvent> eventLabel2 = GroupMembershipAdditionObservationEvent.getEventLabelByGroup(c, groupId);
				assertEquals(eventLabel, eventLabel2);

				// show that equal event labels have equal hash codes
				assertEquals(eventLabel.hashCode(), eventLabel2.hashCode());

				// show that two event labels with different inputs are not
				// equal
				assertTrue(eventLabels.add(eventLabel));
			}

			// precondition tests

			// if the group type id is null
			ContractException contractException = assertThrows(ContractException.class, () -> GroupMembershipAdditionObservationEvent.getEventLabelByGroup(c, null));
			assertEquals(GroupError.NULL_GROUP_ID, contractException.getErrorType());

			// if the group type id is unknown
			contractException = assertThrows(ContractException.class, () -> GroupMembershipAdditionObservationEvent.getEventLabelByGroup(c, new GroupId(10000)));
			assertEquals(GroupError.UNKNOWN_GROUP_ID, contractException.getErrorType());

		});

	}

	@Test
	@UnitTestMethod(name = "getEventLabelerForGroup", args = {})
	public void testGetEventLabelerForGroup() {

		GroupsActionSupport.testConsumer(10, 3, 5, 3313438051476160164L, (c) -> {

			PersonGroupDataView personGroupDataView = c.getDataView(PersonGroupDataView.class).get();

			// create an event labeler
			EventLabeler<GroupMembershipAdditionObservationEvent> eventLabeler = GroupMembershipAdditionObservationEvent.getEventLabelerForGroup();

			// show that the event labeler has the correct event class
			assertEquals(GroupMembershipAdditionObservationEvent.class, eventLabeler.getEventClass());

			// show that the event labeler produces the expected event label

			TestGroupTypeId testGroupTypeId = TestGroupTypeId.GROUP_TYPE_1;
			for (int i = 0; i < 10; i++) {

				c.resolveEvent(new GroupCreationEvent(testGroupTypeId));
				GroupId groupId = personGroupDataView.getLastIssuedGroupId().get();
				testGroupTypeId = testGroupTypeId.next();

				// derive the expected event label for this event
				EventLabel<GroupMembershipAdditionObservationEvent> expectedEventLabel = GroupMembershipAdditionObservationEvent.getEventLabelByGroup(c, groupId);

				// show that the event label and event labeler have equal id
				// values
				assertEquals(expectedEventLabel.getLabelerId(), eventLabeler.getId());

				// create an event
				PersonId personId = new PersonId(0);
				GroupMembershipAdditionObservationEvent event = new GroupMembershipAdditionObservationEvent(personId, groupId);

				// show that the event labeler produces the correct event
				// label
				EventLabel<GroupMembershipAdditionObservationEvent> actualEventLabel = eventLabeler.getEventLabel(c, event);

				assertEquals(expectedEventLabel, actualEventLabel);

			}

		});

	}

	@Test
	@UnitTestMethod(name = "getEventLabelByPerson", args = { SimulationContext.class, PersonId.class })
	public void testGetEventLabelByPerson() {

		GroupsActionSupport.testConsumer(10, 3, 5, 5181120908681821960L, (c) -> {
			PersonDataView personDataView = c.getDataView(PersonDataView.class).get();
			List<PersonId> people = personDataView.getPeople();
			Set<EventLabel<GroupMembershipAdditionObservationEvent>> eventLabels = new LinkedHashSet<>();

			for (PersonId personId : people) {

				EventLabel<GroupMembershipAdditionObservationEvent> eventLabel = GroupMembershipAdditionObservationEvent.getEventLabelByPerson(c, personId);

				// show that the event label has the correct event class
				assertEquals(GroupMembershipAdditionObservationEvent.class, eventLabel.getEventClass());

				// show that the event label has the correct primary key
				assertEquals(GroupMembershipAdditionObservationEvent.class, eventLabel.getPrimaryKeyValue());

				// show that the event label has the same id as its
				// associated labeler
				EventLabeler<GroupMembershipAdditionObservationEvent> eventLabeler = GroupMembershipAdditionObservationEvent.getEventLabelerForPerson();
				assertEquals(eventLabeler.getId(), eventLabel.getLabelerId());

				// show that two event labels with the same inputs are equal
				EventLabel<GroupMembershipAdditionObservationEvent> eventLabel2 = GroupMembershipAdditionObservationEvent.getEventLabelByPerson(c, personId);
				assertEquals(eventLabel, eventLabel2);

				// show that equal event labels have equal hash codes
				assertEquals(eventLabel.hashCode(), eventLabel2.hashCode());

				// show that two event labels with different inputs are not
				// equal
				assertTrue(eventLabels.add(eventLabel));
			}

			// precondition tests

			// if the group type id is null
			ContractException contractException = assertThrows(ContractException.class, () -> GroupMembershipAdditionObservationEvent.getEventLabelByPerson(c, null));
			assertEquals(PersonError.NULL_PERSON_ID, contractException.getErrorType());

			// if the group type id is unknown
			contractException = assertThrows(ContractException.class, () -> GroupMembershipAdditionObservationEvent.getEventLabelByPerson(c, new PersonId(10000)));
			assertEquals(PersonError.UNKNOWN_PERSON_ID, contractException.getErrorType());

		});
	}

	@Test
	@UnitTestMethod(name = "getEventLabelerForPerson", args = {})
	public void testGetEventLabelerForPerson() {

		GroupsActionSupport.testConsumer(30, 3, 5, 7591006487215638552L, (c) -> {

			PersonGroupDataView personGroupDataView = c.getDataView(PersonGroupDataView.class).get();
			List<GroupId> groupIds = personGroupDataView.getGroupIds();

			// create an event labeler
			EventLabeler<GroupMembershipAdditionObservationEvent> eventLabeler = GroupMembershipAdditionObservationEvent.getEventLabelerForPerson();

			// show that the event labeler has the correct event class
			assertEquals(GroupMembershipAdditionObservationEvent.class, eventLabeler.getEventClass());

			// show that the event labeler produces the expected event label

			PersonId personId = new PersonId(0);
			for (GroupId groupId : groupIds) {

				// derive the expected event label for this event
				EventLabel<GroupMembershipAdditionObservationEvent> expectedEventLabel = GroupMembershipAdditionObservationEvent.getEventLabelByPerson(c, personId);

				// show that the event label and event labeler have equal id
				// values
				assertEquals(expectedEventLabel.getLabelerId(), eventLabeler.getId());

				// create an event
				GroupMembershipAdditionObservationEvent event = new GroupMembershipAdditionObservationEvent(personId, groupId);

				// show that the event labeler produces the correct event
				// label
				EventLabel<GroupMembershipAdditionObservationEvent> actualEventLabel = eventLabeler.getEventLabel(c, event);

				assertEquals(expectedEventLabel, actualEventLabel);

			}

		});

	}

	@Test
	@UnitTestMethod(name = "getEventLabelByGroupTypeAndPerson", args = { SimulationContext.class, GroupTypeId.class, PersonId.class })
	public void testGetEventLabelByGroupTypeAndPerson() {

		GroupsActionSupport.testConsumer(10, 3, 5, 2396297410749360025L, (c) -> {

			PersonGroupDataView personGroupDataView = c.getDataView(PersonGroupDataView.class).get();
			Set<EventLabel<GroupMembershipAdditionObservationEvent>> eventLabels = new LinkedHashSet<>();

			PersonId personId = new PersonId(0);
			for (TestGroupTypeId testGroupTypeId : TestGroupTypeId.values()) {

				EventLabel<GroupMembershipAdditionObservationEvent> eventLabel = GroupMembershipAdditionObservationEvent.getEventLabelByGroupTypeAndPerson(c, testGroupTypeId, personId);

				// show that the event label has the correct event class
				assertEquals(GroupMembershipAdditionObservationEvent.class, eventLabel.getEventClass());

				// show that the event label has the correct primary key
				assertEquals(GroupMembershipAdditionObservationEvent.class, eventLabel.getPrimaryKeyValue());

				// show that the event label has the same id as its
				// associated labeler
				EventLabeler<GroupMembershipAdditionObservationEvent> eventLabeler = GroupMembershipAdditionObservationEvent.getEventLabelerForGroupTypeAndPerson(personGroupDataView);
				assertEquals(eventLabeler.getId(), eventLabel.getLabelerId());

				// show that two event labels with the same inputs are equal
				EventLabel<GroupMembershipAdditionObservationEvent> eventLabel2 = GroupMembershipAdditionObservationEvent.getEventLabelByGroupTypeAndPerson(c, testGroupTypeId, personId);
				assertEquals(eventLabel, eventLabel2);

				// show that equal event labels have equal hash codes
				assertEquals(eventLabel.hashCode(), eventLabel2.hashCode());

				// show that two event labels with different inputs are not
				// equal
				assertTrue(eventLabels.add(eventLabel));
			}

			// precondition tests

			// if the group type id is null
			ContractException contractException = assertThrows(ContractException.class, () -> GroupMembershipAdditionObservationEvent.getEventLabelByGroupTypeAndPerson(c, null, new PersonId(0)));
			assertEquals(GroupError.NULL_GROUP_TYPE_ID, contractException.getErrorType());

			// if the group type id is unknown
			contractException = assertThrows(ContractException.class,
					() -> GroupMembershipAdditionObservationEvent.getEventLabelByGroupTypeAndPerson(c, TestGroupTypeId.getUnknownGroupTypeId(), new PersonId(0)));
			assertEquals(GroupError.UNKNOWN_GROUP_TYPE_ID, contractException.getErrorType());
			
			// if the person id is null
			contractException = assertThrows(ContractException.class, () -> GroupMembershipAdditionObservationEvent.getEventLabelByGroupTypeAndPerson(c, TestGroupTypeId.GROUP_TYPE_1, null));
			assertEquals(PersonError.NULL_PERSON_ID, contractException.getErrorType());

			// if the person id is unknown
			contractException = assertThrows(ContractException.class,
					() -> GroupMembershipAdditionObservationEvent.getEventLabelByGroupTypeAndPerson(c, TestGroupTypeId.GROUP_TYPE_1, new PersonId(1000000)));
			assertEquals(PersonError.UNKNOWN_PERSON_ID, contractException.getErrorType());


		});
	}

	@Test
	@UnitTestMethod(name = "getEventLabelerForGroupTypeAndPerson", args = {})
	public void testGetEventLabelerForGroupTypeAndPerson() {

		GroupsActionSupport.testConsumer(30, 3, 5, 944196534930517005L, (c) -> {

			PersonGroupDataView personGroupDataView = c.getDataView(PersonGroupDataView.class).get();
			List<GroupId> groupIds = personGroupDataView.getGroupIds();

			// create an event labeler
			EventLabeler<GroupMembershipAdditionObservationEvent> eventLabeler = GroupMembershipAdditionObservationEvent.getEventLabelerForGroupTypeAndPerson(personGroupDataView);

			// show that the event labeler has the correct event class
			assertEquals(GroupMembershipAdditionObservationEvent.class, eventLabeler.getEventClass());

			// show that the event labeler produces the expected event label

			PersonId personId = new PersonId(0);
			for (GroupId groupId : groupIds) {

				GroupTypeId groupTypeId = personGroupDataView.getGroupType(groupId);

				// derive the expected event label for this event
				EventLabel<GroupMembershipAdditionObservationEvent> expectedEventLabel = GroupMembershipAdditionObservationEvent.getEventLabelByGroupTypeAndPerson(c, groupTypeId, personId);

				// show that the event label and event labeler have equal id
				// values
				assertEquals(expectedEventLabel.getLabelerId(), eventLabeler.getId());

				// create an event
				GroupMembershipAdditionObservationEvent event = new GroupMembershipAdditionObservationEvent(personId, groupId);

				// show that the event labeler produces the correct event
				// label
				EventLabel<GroupMembershipAdditionObservationEvent> actualEventLabel = eventLabeler.getEventLabel(c, event);

				assertEquals(expectedEventLabel, actualEventLabel);

			}

		});

	}

	@Test
	@UnitTestMethod(name = "getEventLabelByGroupType", args = { SimulationContext.class, GroupTypeId.class })
	public void testGetEventLabelByGroupType() {

		GroupsActionSupport.testConsumer(0, 3, 5, 4360946626249599442L, (c) -> {
			PersonGroupDataView personGroupDataView = c.getDataView(PersonGroupDataView.class).get();
			Set<EventLabel<GroupMembershipAdditionObservationEvent>> eventLabels = new LinkedHashSet<>();

			for (TestGroupTypeId testGroupTypeId : TestGroupTypeId.values()) {

				EventLabel<GroupMembershipAdditionObservationEvent> eventLabel = GroupMembershipAdditionObservationEvent.getEventLabelByGroupType(c, testGroupTypeId);

				// show that the event label has the correct event class
				assertEquals(GroupMembershipAdditionObservationEvent.class, eventLabel.getEventClass());

				// show that the event label has the correct primary key
				assertEquals(GroupMembershipAdditionObservationEvent.class, eventLabel.getPrimaryKeyValue());

				// show that the event label has the same id as its
				// associated labeler
				EventLabeler<GroupMembershipAdditionObservationEvent> eventLabeler = GroupMembershipAdditionObservationEvent.getEventLabelerForGroupType(personGroupDataView);
				assertEquals(eventLabeler.getId(), eventLabel.getLabelerId());

				// show that two event labels with the same inputs are equal
				EventLabel<GroupMembershipAdditionObservationEvent> eventLabel2 = GroupMembershipAdditionObservationEvent.getEventLabelByGroupType(c, testGroupTypeId);
				assertEquals(eventLabel, eventLabel2);

				// show that equal event labels have equal hash codes
				assertEquals(eventLabel.hashCode(), eventLabel2.hashCode());

				// show that two event labels with different inputs are not
				// equal
				assertTrue(eventLabels.add(eventLabel));
			}

			// precondition tests

			// if the group type id is null
			ContractException contractException = assertThrows(ContractException.class, () -> GroupMembershipAdditionObservationEvent.getEventLabelByGroupType(c, null));
			assertEquals(GroupError.NULL_GROUP_TYPE_ID, contractException.getErrorType());

			// if the group type id is unknown
			contractException = assertThrows(ContractException.class, () -> GroupMembershipAdditionObservationEvent.getEventLabelByGroupType(c, TestGroupTypeId.getUnknownGroupTypeId()));
			assertEquals(GroupError.UNKNOWN_GROUP_TYPE_ID, contractException.getErrorType());

		});

	}

	@Test
	@UnitTestMethod(name = "getEventLabelerForGroupType", args = {})
	public void testGetEventLabelerForGroupType() {

		GroupsActionSupport.testConsumer(10, 3, 5, 825213654032168954L, (c) -> {

			PersonGroupDataView personGroupDataView = c.getDataView(PersonGroupDataView.class).get();

			// create an event labeler
			EventLabeler<GroupMembershipAdditionObservationEvent> eventLabeler = GroupMembershipAdditionObservationEvent.getEventLabelerForGroupType(personGroupDataView);

			// show that the event labeler has the correct event class
			assertEquals(GroupMembershipAdditionObservationEvent.class, eventLabeler.getEventClass());

			// show that the event labeler produces the expected event label

			for (TestGroupTypeId testGroupTypeId : TestGroupTypeId.values()) {

				// derive the expected event label for this event
				EventLabel<GroupMembershipAdditionObservationEvent> expectedEventLabel = GroupMembershipAdditionObservationEvent.getEventLabelByGroupType(c, testGroupTypeId);

				// show that the event label and event labeler have equal id
				// values
				assertEquals(expectedEventLabel.getLabelerId(), eventLabeler.getId());

				c.resolveEvent(new GroupCreationEvent(testGroupTypeId));
				GroupId groupId = personGroupDataView.getLastIssuedGroupId().get();

				PersonId personId = new PersonId(0);
				// create an event
				GroupMembershipAdditionObservationEvent event = new GroupMembershipAdditionObservationEvent(personId, groupId);

				// show that the event labeler produces the correct event
				// label
				EventLabel<GroupMembershipAdditionObservationEvent> actualEventLabel = eventLabeler.getEventLabel(c, event);

				assertEquals(expectedEventLabel, actualEventLabel);

			}

		});

	}

	@Test
	@UnitTestMethod(name = "getEventLabelByAll", args = {})
	public void testGetEventLabelByAll() {

		GroupsActionSupport.testConsumer(0, 3, 5, 4764889447042956281L, (c) -> {

			Set<EventLabel<GroupMembershipAdditionObservationEvent>> eventLabels = new LinkedHashSet<>();

			EventLabel<GroupMembershipAdditionObservationEvent> eventLabel = GroupMembershipAdditionObservationEvent.getEventLabelByAll();

			// show that the event label has the correct event class
			assertEquals(GroupMembershipAdditionObservationEvent.class, eventLabel.getEventClass());

			// show that the event label has the correct primary key
			assertEquals(GroupMembershipAdditionObservationEvent.class, eventLabel.getPrimaryKeyValue());

			// show that the event label has the same id as its
			// associated labeler
			EventLabeler<GroupMembershipAdditionObservationEvent> eventLabeler = GroupMembershipAdditionObservationEvent.getEventLabelerForAll();
			assertEquals(eventLabeler.getId(), eventLabel.getLabelerId());

			// show that two event labels with the same inputs are equal
			EventLabel<GroupMembershipAdditionObservationEvent> eventLabel2 = GroupMembershipAdditionObservationEvent.getEventLabelByAll();
			assertEquals(eventLabel, eventLabel2);

			// show that equal event labels have equal hash codes
			assertEquals(eventLabel.hashCode(), eventLabel2.hashCode());

			// show that two event labels with different inputs are not
			// equal
			assertTrue(eventLabels.add(eventLabel));

		});

	}

	@Test
	@UnitTestMethod(name = "getEventLabelerForAll", args = {})
	public void testGetEventLabelerForAll() {

		GroupsActionSupport.testConsumer(100, 3, 5, 7811446012558625127L, (c) -> {

			PersonGroupDataView personGroupDataView = c.getDataView(PersonGroupDataView.class).get();

			// create an event labeler
			EventLabeler<GroupMembershipAdditionObservationEvent> eventLabeler = GroupMembershipAdditionObservationEvent.getEventLabelerForAll();

			// show that the event labeler has the correct event class
			assertEquals(GroupMembershipAdditionObservationEvent.class, eventLabeler.getEventClass());

			// show that the event labeler produces the expected event label

			for (TestGroupTypeId testGroupTypeId : TestGroupTypeId.values()) {

				// derive the expected event label for this event
				EventLabel<GroupMembershipAdditionObservationEvent> expectedEventLabel = GroupMembershipAdditionObservationEvent.getEventLabelByAll();

				// show that the event label and event labeler have equal id
				// values
				assertEquals(expectedEventLabel.getLabelerId(), eventLabeler.getId());

				c.resolveEvent(new GroupCreationEvent(testGroupTypeId));
				GroupId groupId = personGroupDataView.getLastIssuedGroupId().get();

				// create an event
				PersonId personId = new PersonId(0);
				GroupMembershipAdditionObservationEvent event = new GroupMembershipAdditionObservationEvent(personId, groupId);

				// show that the event labeler produces the correct event
				// label
				EventLabel<GroupMembershipAdditionObservationEvent> actualEventLabel = eventLabeler.getEventLabel(c, event);

				assertEquals(expectedEventLabel, actualEventLabel);

			}

		});

	}
}
