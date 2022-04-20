package plugins.groups.events;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.naming.Context;

import org.junit.jupiter.api.Test;

import nucleus.EventLabel;
import nucleus.EventLabeler;
import nucleus.util.ContractException;
import plugins.groups.GroupDataManager;
import plugins.groups.support.GroupError;
import plugins.groups.support.GroupId;
import plugins.groups.support.GroupPropertyId;
import plugins.groups.support.GroupTypeId;
import plugins.groups.testsupport.GroupsActionSupport;
import plugins.groups.testsupport.TestGroupPropertyId;
import plugins.groups.testsupport.TestGroupTypeId;
import tools.annotations.UnitTest;
import tools.annotations.UnitTestConstructor;
import tools.annotations.UnitTestMethod;

@UnitTest(target = GroupPropertyUpdateEvent.class)
public class AT_GroupPropertyUpdateEvent {

	@Test
	@UnitTestConstructor(args = { GroupId.class, GroupPropertyId.class, Object.class, Object.class })
	public void testConstructor() {
		// nothing to test
	}

	@Test
	@UnitTestMethod(name = "getCurrentPropertyValue", args = {})
	public void testGetCurrentPropertyValue() {
		GroupId groupId = new GroupId(30);
		GroupPropertyId groupPropertyId = TestGroupPropertyId.GROUP_PROPERTY_1_1_BOOLEAN_MUTABLE_NO_TRACK;
		Object previousValue = "previous";
		Object currentValue = "current";
		GroupPropertyUpdateEvent groupPropertyUpdateEvent = new GroupPropertyUpdateEvent(groupId, groupPropertyId, previousValue, currentValue);
		assertEquals(currentValue, groupPropertyUpdateEvent.getCurrentPropertyValue());
	}

	@Test
	@UnitTestMethod(name = "getGroupId", args = {})
	public void testGetGroupId() {
		GroupId groupId = new GroupId(30);
		GroupPropertyId groupPropertyId = TestGroupPropertyId.GROUP_PROPERTY_1_1_BOOLEAN_MUTABLE_NO_TRACK;
		Object previousValue = "previous";
		Object currentValue = "current";
		GroupPropertyUpdateEvent groupPropertyUpdateEvent = new GroupPropertyUpdateEvent(groupId, groupPropertyId, previousValue, currentValue);
		assertEquals(groupId, groupPropertyUpdateEvent.getGroupId());
	}

	@Test
	@UnitTestMethod(name = "getGroupPropertyId", args = {})
	public void testGetGroupPropertyId() {
		GroupId groupId = new GroupId(30);
		GroupPropertyId groupPropertyId = TestGroupPropertyId.GROUP_PROPERTY_1_1_BOOLEAN_MUTABLE_NO_TRACK;
		Object previousValue = "previous";
		Object currentValue = "current";
		GroupPropertyUpdateEvent groupPropertyUpdateEvent = new GroupPropertyUpdateEvent(groupId, groupPropertyId, previousValue, currentValue);
		assertEquals(groupPropertyId, groupPropertyUpdateEvent.getGroupPropertyId());
	}

	@Test
	@UnitTestMethod(name = "getPreviousPropertyValue", args = {})
	public void testGetPreviousPropertyValue() {
		GroupId groupId = new GroupId(30);
		GroupPropertyId groupPropertyId = TestGroupPropertyId.GROUP_PROPERTY_1_1_BOOLEAN_MUTABLE_NO_TRACK;
		Object previousValue = "previous";
		Object currentValue = "current";
		GroupPropertyUpdateEvent groupPropertyUpdateEvent = new GroupPropertyUpdateEvent(groupId, groupPropertyId, previousValue, currentValue);
		assertEquals(previousValue, groupPropertyUpdateEvent.getPreviousPropertyValue());
	}

	@Test
	@UnitTestMethod(name = "getEventLabelByGroupAndProperty", args = { Context.class, GroupId.class, GroupPropertyId.class })
	public void testGetEventLabelByGroupAndProperty() {

		GroupsActionSupport.testConsumer(10, 3, 5, 2608996249142401870L, (c) -> {

			GroupDataManager groupDataManager = c.getDataManager(GroupDataManager.class).get();
			List<GroupId> groupIds = groupDataManager.getGroupIds();

			Set<EventLabel<GroupPropertyUpdateEvent>> eventLabels = new LinkedHashSet<>();

			for (GroupId groupId : groupIds) {
				GroupTypeId groupTypeId = groupDataManager.getGroupType(groupId);
				Set<GroupPropertyId> groupPropertyIds = groupDataManager.getGroupPropertyIds(groupTypeId);

				for (GroupPropertyId groupPropertyId : groupPropertyIds) {

					EventLabel<GroupPropertyUpdateEvent> eventLabel = GroupPropertyUpdateEvent.getEventLabelByGroupAndProperty(c, groupId, groupPropertyId);

					// show that the event label has the correct event class
					assertEquals(GroupPropertyUpdateEvent.class, eventLabel.getEventClass());

					// show that the event label has the correct primary key
					assertEquals(GroupPropertyUpdateEvent.class, eventLabel.getPrimaryKeyValue());

					// show that the event label has the same id as its
					// associated labeler
					EventLabeler<GroupPropertyUpdateEvent> eventLabeler = GroupPropertyUpdateEvent.getEventLabelerForGroupAndProperty();
					assertEquals(eventLabeler.getEventLabelerId(), eventLabel.getLabelerId());

					// show that two event labels with the same inputs are equal
					EventLabel<GroupPropertyUpdateEvent> eventLabel2 = GroupPropertyUpdateEvent.getEventLabelByGroupAndProperty(c, groupId, groupPropertyId);
					assertEquals(eventLabel, eventLabel2);

					// show that equal event labels have equal hash codes
					assertEquals(eventLabel.hashCode(), eventLabel2.hashCode());

					// show that two event labels with different inputs are not
					// equal
					assertTrue(eventLabels.add(eventLabel));
				}
			}

			// precondition tests

			// if the group id is null
			ContractException contractException = assertThrows(ContractException.class,
					() -> GroupPropertyUpdateEvent.getEventLabelByGroupAndProperty(c, null, TestGroupPropertyId.GROUP_PROPERTY_1_1_BOOLEAN_MUTABLE_NO_TRACK));
			assertEquals(GroupError.NULL_GROUP_ID, contractException.getErrorType());

			// if the group id is unknown
			contractException = assertThrows(ContractException.class,
					() -> GroupPropertyUpdateEvent.getEventLabelByGroupAndProperty(c, new GroupId(100000), TestGroupPropertyId.GROUP_PROPERTY_1_1_BOOLEAN_MUTABLE_NO_TRACK));
			assertEquals(GroupError.UNKNOWN_GROUP_ID, contractException.getErrorType());

			// if the group property id is null
			contractException = assertThrows(ContractException.class, () -> GroupPropertyUpdateEvent.getEventLabelByGroupAndProperty(c, new GroupId(0), null));
			assertEquals(GroupError.NULL_GROUP_PROPERTY_ID, contractException.getErrorType());

			// if the group property id is unknown
			contractException = assertThrows(ContractException.class,
					() -> GroupPropertyUpdateEvent.getEventLabelByGroupAndProperty(c, new GroupId(0), TestGroupPropertyId.getUnknownGroupPropertyId()));
			assertEquals(GroupError.UNKNOWN_GROUP_PROPERTY_ID, contractException.getErrorType());

			// if the group property id is unknown
			contractException = assertThrows(ContractException.class,
					() -> GroupPropertyUpdateEvent.getEventLabelByGroupAndProperty(c, new GroupId(0), TestGroupPropertyId.GROUP_PROPERTY_2_1_BOOLEAN_MUTABLE_TRACK));
			assertEquals(GroupError.UNKNOWN_GROUP_PROPERTY_ID, contractException.getErrorType());

		});
	}

	@Test
	@UnitTestMethod(name = "getEventLabelerForGroupAndProperty", args = {})
	public void testGetEventLabelerForGroupAndProperty() {

		GroupsActionSupport.testConsumer(30, 3, 5, 8688886270722853901L, (c) -> {

			GroupDataManager groupDataManager = c.getDataManager(GroupDataManager.class).get();
			List<GroupId> groupIds = groupDataManager.getGroupIds();

			// create an event labeler
			EventLabeler<GroupPropertyUpdateEvent> eventLabeler = GroupPropertyUpdateEvent.getEventLabelerForGroupAndProperty();

			// show that the event labeler has the correct event class
			assertEquals(GroupPropertyUpdateEvent.class, eventLabeler.getEventClass());

			// show that the event labeler produces the expected event label

			for (GroupId groupId : groupIds) {
				GroupTypeId groupTypeId = groupDataManager.getGroupType(groupId);
				Set<GroupPropertyId> groupPropertyIds = groupDataManager.getGroupPropertyIds(groupTypeId);

				for (GroupPropertyId groupPropertyId : groupPropertyIds) {
					// derive the expected event label for this event
					EventLabel<GroupPropertyUpdateEvent> expectedEventLabel = GroupPropertyUpdateEvent.getEventLabelByGroupAndProperty(c, groupId, groupPropertyId);

					// show that the event label and event labeler have equal id
					// values
					assertEquals(expectedEventLabel.getLabelerId(), eventLabeler.getEventLabelerId());

					// create an event
					GroupPropertyUpdateEvent event = new GroupPropertyUpdateEvent(groupId, groupPropertyId, "previous", "current");

					// show that the event labeler produces the correct event
					// label
					EventLabel<GroupPropertyUpdateEvent> actualEventLabel = eventLabeler.getEventLabel(c, event);

					assertEquals(expectedEventLabel, actualEventLabel);

				}
			}

		});
	}

	@Test
	@UnitTestMethod(name = "getEventLabelByGroup", args = { Context.class, GroupId.class })
	public void testGetEventLabelByGroup() {

		GroupsActionSupport.testConsumer(10, 3, 5, 7912737444879496875L, (c) -> {

			GroupDataManager groupDataManager = c.getDataManager(GroupDataManager.class).get();
			List<GroupId> groupIds = groupDataManager.getGroupIds();

			Set<EventLabel<GroupPropertyUpdateEvent>> eventLabels = new LinkedHashSet<>();

			for (GroupId groupId : groupIds) {

				EventLabel<GroupPropertyUpdateEvent> eventLabel = GroupPropertyUpdateEvent.getEventLabelByGroup(c, groupId);

				// show that the event label has the correct event class
				assertEquals(GroupPropertyUpdateEvent.class, eventLabel.getEventClass());

				// show that the event label has the correct primary key
				assertEquals(GroupPropertyUpdateEvent.class, eventLabel.getPrimaryKeyValue());

				// show that the event label has the same id as its
				// associated labeler
				EventLabeler<GroupPropertyUpdateEvent> eventLabeler = GroupPropertyUpdateEvent.getEventLabelerForGroup();
				assertEquals(eventLabeler.getEventLabelerId(), eventLabel.getLabelerId());

				// show that two event labels with the same inputs are equal
				EventLabel<GroupPropertyUpdateEvent> eventLabel2 = GroupPropertyUpdateEvent.getEventLabelByGroup(c, groupId);
				assertEquals(eventLabel, eventLabel2);

				// show that equal event labels have equal hash codes
				assertEquals(eventLabel.hashCode(), eventLabel2.hashCode());

				// show that two event labels with different inputs are not
				// equal
				assertTrue(eventLabels.add(eventLabel));
			}

			// precondition tests

			// if the group id is null
			ContractException contractException = assertThrows(ContractException.class, () -> GroupPropertyUpdateEvent.getEventLabelByGroup(c, null));
			assertEquals(GroupError.NULL_GROUP_ID, contractException.getErrorType());

			// if the group id is unknown
			contractException = assertThrows(ContractException.class, () -> GroupPropertyUpdateEvent.getEventLabelByGroup(c, new GroupId(100000)));
			assertEquals(GroupError.UNKNOWN_GROUP_ID, contractException.getErrorType());

		});

	}

	@Test
	@UnitTestMethod(name = "getEventLabelerForGroup", args = {})
	public void testGetEventLabelerForGroup() {

		GroupsActionSupport.testConsumer(30, 3, 5, 5829392632134617932L, (c) -> {

			GroupDataManager groupDataManager = c.getDataManager(GroupDataManager.class).get();
			List<GroupId> groupIds = groupDataManager.getGroupIds();

			// create an event labeler
			EventLabeler<GroupPropertyUpdateEvent> eventLabeler = GroupPropertyUpdateEvent.getEventLabelerForGroup();

			// show that the event labeler has the correct event class
			assertEquals(GroupPropertyUpdateEvent.class, eventLabeler.getEventClass());

			// show that the event labeler produces the expected event label

			for (GroupId groupId : groupIds) {

				// derive the expected event label for this event
				EventLabel<GroupPropertyUpdateEvent> expectedEventLabel = GroupPropertyUpdateEvent.getEventLabelByGroup(c, groupId);

				// show that the event label and event labeler have equal id
				// values
				assertEquals(expectedEventLabel.getLabelerId(), eventLabeler.getEventLabelerId());

				// create an event
				GroupPropertyUpdateEvent event = new GroupPropertyUpdateEvent(groupId, TestGroupPropertyId.GROUP_PROPERTY_1_1_BOOLEAN_MUTABLE_NO_TRACK, "previous", "current");

				// show that the event labeler produces the correct event
				// label
				EventLabel<GroupPropertyUpdateEvent> actualEventLabel = eventLabeler.getEventLabel(c, event);

				assertEquals(expectedEventLabel, actualEventLabel);

			}

		});

	}

	@Test
	@UnitTestMethod(name = "getEventLabelByGroupTypeAndProperty", args = { Context.class, GroupTypeId.class, GroupPropertyId.class })
	public void testGetEventLabelByGroupTypeAndProperty() {

		GroupsActionSupport.testConsumer(10, 3, 5, 7297949839974902355L, (c) -> {

			GroupDataManager groupDataManager = c.getDataManager(GroupDataManager.class).get();

			Set<EventLabel<GroupPropertyUpdateEvent>> eventLabels = new LinkedHashSet<>();

			for (GroupTypeId groupTypeId : TestGroupTypeId.values()) {
				Set<GroupPropertyId> groupPropertyIds = groupDataManager.getGroupPropertyIds(groupTypeId);
				for (GroupPropertyId groupPropertyId : groupPropertyIds) {

					EventLabel<GroupPropertyUpdateEvent> eventLabel = GroupPropertyUpdateEvent.getEventLabelByGroupTypeAndProperty(c, groupTypeId, groupPropertyId);

					// show that the event label has the correct event class
					assertEquals(GroupPropertyUpdateEvent.class, eventLabel.getEventClass());

					// show that the event label has the correct primary key
					assertEquals(GroupPropertyUpdateEvent.class, eventLabel.getPrimaryKeyValue());

					// show that the event label has the same id as its
					// associated labeler
					EventLabeler<GroupPropertyUpdateEvent> eventLabeler = GroupPropertyUpdateEvent.getEventLabelerForGroupTypeAndProperty(groupDataManager);
					assertEquals(eventLabeler.getEventLabelerId(), eventLabel.getLabelerId());

					// show that two event labels with the same inputs are equal
					EventLabel<GroupPropertyUpdateEvent> eventLabel2 = GroupPropertyUpdateEvent.getEventLabelByGroupTypeAndProperty(c, groupTypeId, groupPropertyId);
					assertEquals(eventLabel, eventLabel2);

					// show that equal event labels have equal hash codes
					assertEquals(eventLabel.hashCode(), eventLabel2.hashCode());

					// show that two event labels with different inputs are not
					// equal
					assertTrue(eventLabels.add(eventLabel));
				}
			}

			// precondition tests

			// if the group id is null
			ContractException contractException = assertThrows(ContractException.class,
					() -> GroupPropertyUpdateEvent.getEventLabelByGroupTypeAndProperty(c, null, TestGroupPropertyId.GROUP_PROPERTY_1_1_BOOLEAN_MUTABLE_NO_TRACK));
			assertEquals(GroupError.NULL_GROUP_TYPE_ID, contractException.getErrorType());

			// if the group id is unknown
			contractException = assertThrows(ContractException.class, () -> GroupPropertyUpdateEvent.getEventLabelByGroupTypeAndProperty(c, TestGroupTypeId.getUnknownGroupTypeId(),
					TestGroupPropertyId.GROUP_PROPERTY_1_1_BOOLEAN_MUTABLE_NO_TRACK));
			assertEquals(GroupError.UNKNOWN_GROUP_TYPE_ID, contractException.getErrorType());

			// if the group property id is null
			contractException = assertThrows(ContractException.class, () -> GroupPropertyUpdateEvent.getEventLabelByGroupTypeAndProperty(c, TestGroupTypeId.GROUP_TYPE_1, null));
			assertEquals(GroupError.NULL_GROUP_PROPERTY_ID, contractException.getErrorType());

			// if the group property id is unknown
			contractException = assertThrows(ContractException.class,
					() -> GroupPropertyUpdateEvent.getEventLabelByGroupTypeAndProperty(c, TestGroupTypeId.GROUP_TYPE_1, TestGroupPropertyId.getUnknownGroupPropertyId()));
			assertEquals(GroupError.UNKNOWN_GROUP_PROPERTY_ID, contractException.getErrorType());

			// if the group property id is unknown
			contractException = assertThrows(ContractException.class,
					() -> GroupPropertyUpdateEvent.getEventLabelByGroupTypeAndProperty(c, TestGroupTypeId.GROUP_TYPE_1, TestGroupPropertyId.GROUP_PROPERTY_2_1_BOOLEAN_MUTABLE_TRACK));
			assertEquals(GroupError.UNKNOWN_GROUP_PROPERTY_ID, contractException.getErrorType());

		});
	}

	@Test
	@UnitTestMethod(name = "getEventLabelerForGroupTypeAndProperty", args = {})
	public void testGetEventLabelerForGroupTypeAndProperty() {

		GroupsActionSupport.testConsumer(30, 3, 5, 9005403678043381761L, (c) -> {

			GroupDataManager groupDataManager = c.getDataManager(GroupDataManager.class).get();

			// create an event labeler
			EventLabeler<GroupPropertyUpdateEvent> eventLabeler = GroupPropertyUpdateEvent.getEventLabelerForGroupTypeAndProperty(groupDataManager);

			// show that the event labeler has the correct event class
			assertEquals(GroupPropertyUpdateEvent.class, eventLabeler.getEventClass());

			// show that the event labeler produces the expected event label

			for (GroupTypeId groupTypeId : TestGroupTypeId.values()) {
				Set<GroupPropertyId> groupPropertyIds = groupDataManager.getGroupPropertyIds(groupTypeId);
				GroupId groupId = groupDataManager.getGroupsForGroupType(groupTypeId).get(0);

				for (GroupPropertyId groupPropertyId : groupPropertyIds) {
					// derive the expected event label for this event
					EventLabel<GroupPropertyUpdateEvent> expectedEventLabel = GroupPropertyUpdateEvent.getEventLabelByGroupTypeAndProperty(c, groupTypeId, groupPropertyId);

					// show that the event label and event labeler have equal id
					// values
					assertEquals(expectedEventLabel.getLabelerId(), eventLabeler.getEventLabelerId());

					// create an event
					GroupPropertyUpdateEvent event = new GroupPropertyUpdateEvent(groupId, groupPropertyId, "previous", "current");

					// show that the event labeler produces the correct event
					// label
					EventLabel<GroupPropertyUpdateEvent> actualEventLabel = eventLabeler.getEventLabel(c, event);

					assertEquals(expectedEventLabel, actualEventLabel);

				}
			}

		});
	}

	@Test
	@UnitTestMethod(name = "getEventLabelByGroupType", args = { Context.class, GroupTypeId.class })
	public void testGetEventLabelByGroupType() {

		GroupsActionSupport.testConsumer(10, 3, 5, 676016189463079696L, (c) -> {
			GroupDataManager groupDataManager = c.getDataManager(GroupDataManager.class).get();
			Set<EventLabel<GroupPropertyUpdateEvent>> eventLabels = new LinkedHashSet<>();

			for (GroupTypeId groupTypeId : TestGroupTypeId.values()) {

				EventLabel<GroupPropertyUpdateEvent> eventLabel = GroupPropertyUpdateEvent.getEventLabelByGroupType(c, groupTypeId);

				// show that the event label has the correct event class
				assertEquals(GroupPropertyUpdateEvent.class, eventLabel.getEventClass());

				// show that the event label has the correct primary key
				assertEquals(GroupPropertyUpdateEvent.class, eventLabel.getPrimaryKeyValue());

				// show that the event label has the same id as its
				// associated labeler
				EventLabeler<GroupPropertyUpdateEvent> eventLabeler = GroupPropertyUpdateEvent.getEventLabelerForGroupType(groupDataManager);
				assertEquals(eventLabeler.getEventLabelerId(), eventLabel.getLabelerId());

				// show that two event labels with the same inputs are equal
				EventLabel<GroupPropertyUpdateEvent> eventLabel2 = GroupPropertyUpdateEvent.getEventLabelByGroupType(c, groupTypeId);
				assertEquals(eventLabel, eventLabel2);

				// show that equal event labels have equal hash codes
				assertEquals(eventLabel.hashCode(), eventLabel2.hashCode());

				// show that two event labels with different inputs are not
				// equal
				assertTrue(eventLabels.add(eventLabel));

			}

			// precondition tests

			// if the group id is null
			ContractException contractException = assertThrows(ContractException.class, () -> GroupPropertyUpdateEvent.getEventLabelByGroupType(c, null));
			assertEquals(GroupError.NULL_GROUP_TYPE_ID, contractException.getErrorType());

			// if the group id is unknown
			contractException = assertThrows(ContractException.class, () -> GroupPropertyUpdateEvent.getEventLabelByGroupType(c, TestGroupTypeId.getUnknownGroupTypeId()));
			assertEquals(GroupError.UNKNOWN_GROUP_TYPE_ID, contractException.getErrorType());

		});

	}

	@Test
	@UnitTestMethod(name = "getEventLabelerForGroupType", args = {})
	public void testGetEventLabelerForGroupType() {

		GroupsActionSupport.testConsumer(30, 3, 5, 6063816259833737907L, (c) -> {

			GroupDataManager groupDataManager = c.getDataManager(GroupDataManager.class).get();

			// create an event labeler
			EventLabeler<GroupPropertyUpdateEvent> eventLabeler = GroupPropertyUpdateEvent.getEventLabelerForGroupType(groupDataManager);

			// show that the event labeler has the correct event class
			assertEquals(GroupPropertyUpdateEvent.class, eventLabeler.getEventClass());

			// show that the event labeler produces the expected event label

			for (GroupTypeId groupTypeId : TestGroupTypeId.values()) {
				
				GroupId groupId = groupDataManager.getGroupsForGroupType(groupTypeId).get(0);

				// derive the expected event label for this event
				EventLabel<GroupPropertyUpdateEvent> expectedEventLabel = GroupPropertyUpdateEvent.getEventLabelByGroupType(c, groupTypeId);

				// show that the event label and event labeler have equal id
				// values
				assertEquals(expectedEventLabel.getLabelerId(), eventLabeler.getEventLabelerId());

				// create an event
				GroupPropertyUpdateEvent event = new GroupPropertyUpdateEvent(groupId, TestGroupPropertyId.GROUP_PROPERTY_1_1_BOOLEAN_MUTABLE_NO_TRACK, "previous", "current");

				// show that the event labeler produces the correct event
				// label
				EventLabel<GroupPropertyUpdateEvent> actualEventLabel = eventLabeler.getEventLabel(c, event);

				assertEquals(expectedEventLabel, actualEventLabel);

			}

		});
	}

	@Test
	@UnitTestMethod(name = "getEventLabelByAll", args = { Context.class })
	public void testGetEventLabelByAll() {

		GroupsActionSupport.testConsumer(10, 3, 5, 6107267895624819992L, (c) -> {

			Set<EventLabel<GroupPropertyUpdateEvent>> eventLabels = new LinkedHashSet<>();

			EventLabel<GroupPropertyUpdateEvent> eventLabel = GroupPropertyUpdateEvent.getEventLabelByAll();

			// show that the event label has the correct event class
			assertEquals(GroupPropertyUpdateEvent.class, eventLabel.getEventClass());

			// show that the event label has the correct primary key
			assertEquals(GroupPropertyUpdateEvent.class, eventLabel.getPrimaryKeyValue());

			// show that the event label has the same id as its
			// associated labeler
			EventLabeler<GroupPropertyUpdateEvent> eventLabeler = GroupPropertyUpdateEvent.getEventLabelerForAll();
			assertEquals(eventLabeler.getEventLabelerId(), eventLabel.getLabelerId());

			// show that two event labels with the same inputs are equal
			EventLabel<GroupPropertyUpdateEvent> eventLabel2 = GroupPropertyUpdateEvent.getEventLabelByAll();
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
		
		GroupsActionSupport.testConsumer(30, 3, 5, 1091393499559399697L, (c) -> {

			GroupDataManager groupDataManager = c.getDataManager(GroupDataManager.class).get();

			// create an event labeler
			EventLabeler<GroupPropertyUpdateEvent> eventLabeler = GroupPropertyUpdateEvent.getEventLabelerForAll();
			List<GroupId> groupIds = groupDataManager.getGroupIds();
			// show that the event labeler has the correct event class
			assertEquals(GroupPropertyUpdateEvent.class, eventLabeler.getEventClass());

			// show that the event labeler produces the expected event label

			for (GroupId groupId : groupIds) {

				// derive the expected event label for this event
				EventLabel<GroupPropertyUpdateEvent> expectedEventLabel = GroupPropertyUpdateEvent.getEventLabelByAll();

				// show that the event label and event labeler have equal id
				// values
				assertEquals(expectedEventLabel.getLabelerId(), eventLabeler.getEventLabelerId());

				// create an event
				GroupPropertyUpdateEvent event = new GroupPropertyUpdateEvent(groupId, TestGroupPropertyId.GROUP_PROPERTY_1_1_BOOLEAN_MUTABLE_NO_TRACK, "previous", "current");

				// show that the event labeler produces the correct event
				// label
				EventLabel<GroupPropertyUpdateEvent> actualEventLabel = eventLabeler.getEventLabel(c, event);

				assertEquals(expectedEventLabel, actualEventLabel);

			}

		});
	}

}
