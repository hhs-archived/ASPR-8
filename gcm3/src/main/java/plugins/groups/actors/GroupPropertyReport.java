package plugins.groups.actors;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import nucleus.ActorContext;
import nucleus.EventLabel;
import plugins.groups.datamanagers.GroupsDataManager;
import plugins.groups.events.GroupAdditionEvent;
import plugins.groups.events.GroupImminentRemovalEvent;
import plugins.groups.events.GroupPropertyDefinitionEvent;
import plugins.groups.events.GroupPropertyUpdateEvent;
import plugins.groups.events.GroupTypeAdditionEvent;
import plugins.groups.support.GroupError;
import plugins.groups.support.GroupId;
import plugins.groups.support.GroupPropertyId;
import plugins.groups.support.GroupTypeId;
import plugins.reports.support.PeriodicReport;
import plugins.reports.support.ReportError;
import plugins.reports.support.ReportHeader;
import plugins.reports.support.ReportId;
import plugins.reports.support.ReportItem;
import plugins.reports.support.ReportPeriod;
import plugins.util.properties.PropertyDefinition;
import util.errors.ContractException;

/**
 * A periodic Report that displays the number of groups having particular values
 * for each group property for a given group type. Only non-zero person counts
 * are reported. The report is further limited to the
 * (GroupType,GroupPropertyId) pairs added to the builder.
 * 
 *
 *
 * Fields
 *
 * GroupType -- the group type of group
 *
 * Property -- the group property identifier
 *
 * Value -- the value of the property
 *
 * GroupCount -- the number of groups having the property value for the given
 * group type
 *
 * @author Shawn Hatch
 *
 */
public final class GroupPropertyReport extends PeriodicReport {

	private static class Scaffold {
		private ReportPeriod reportPeriod = ReportPeriod.DAILY;
		private final Map<GroupTypeId, Set<GroupPropertyId>> clientPropertyMap = new LinkedHashMap<>();
		private final Set<GroupTypeId> allProperties = new LinkedHashSet<>();
		private ReportId reportId;
		private boolean includeNewProperties;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private Scaffold scaffold = new Scaffold();

		private Builder() {
		}

		/**
		 * Returns the GroupPropertyReport instance
		 */
		public GroupPropertyReport build() {
			try {

				return new GroupPropertyReport(scaffold);
			} finally {
				scaffold = new Scaffold();
			}
		}

		/**
		 * Sets the report period for this report
		 * 
		 * @throws ContractException
		 *             <li>{@linkplain ReportError#NULL_REPORT_PERIOD} if the
		 *             report period is null</li>
		 *             <li>if the report period END_OF_SIMULATION</li>
		 */
		public Builder setReportPeriod(ReportPeriod reportPeriod) {
			if (reportPeriod == null) {
				throw new ContractException(ReportError.NULL_REPORT_PERIOD);
			}

			if (reportPeriod == ReportPeriod.END_OF_SIMULATION) {
				throw new ContractException(ReportError.UNSUPPORTED_REPORT_PERIOD, ReportPeriod.END_OF_SIMULATION);
			}
			scaffold.reportPeriod = reportPeriod;
			return this;
		}

		/**
		 * Sets the report period for this report
		 * 
		 * @throws ContractException
		 *             <li>{@linkplain ReportError#NULL_REPORT_ID} if the report
		 *             period is null</li>
		 * 
		 */
		public Builder setReportId(ReportId reportId) {
			if (reportId == null) {
				throw new ContractException(ReportError.NULL_REPORT_ID);
			}

			scaffold.reportId = reportId;
			return this;
		}

		/**
		 * Adds all properties for the given group type id
		 * 
		 * @throws ContractException
		 *             <li>{@linkplain GroupError#NULL_GROUP_TYPE_ID} if the
		 *             group type id is null</li>
		 */
		public Builder addAllProperties(GroupTypeId groupTypeId) {
			if (groupTypeId == null) {
				throw new ContractException(GroupError.NULL_GROUP_TYPE_ID);
			}
			scaffold.allProperties.add(groupTypeId);
			return this;
		}

		public Builder includeNewProperties(boolean includeNewProperties) {
			scaffold.includeNewProperties = includeNewProperties;
			return this;
		}

		/**
		 * Adds all properties for the given group type id
		 * 
		 * @throws ContractException
		 *             <li>{@linkplain GroupError#NULL_GROUP_TYPE_ID} if the
		 *             group type id is null</li>
		 *             <li>{@linkplain GroupError#NULL_GROUP_PROPERTY_ID} if the
		 *             group property id is null</li>
		 */
		public Builder addProperty(GroupTypeId groupTypeId, GroupPropertyId groupPropertyId) {
			if (groupTypeId == null) {
				throw new ContractException(GroupError.NULL_GROUP_TYPE_ID);
			}
			if (groupPropertyId == null) {
				throw new ContractException(GroupError.NULL_GROUP_PROPERTY_ID);
			}
			Set<GroupPropertyId> set = scaffold.clientPropertyMap.get(groupTypeId);
			if (set == null) {
				set = new LinkedHashSet<>();
				scaffold.clientPropertyMap.put(groupTypeId, set);
			}
			set.add(groupPropertyId);
			return this;
		}

	}

	private final Scaffold scaffold;

	private GroupPropertyReport(Scaffold scaffold) {
		super(scaffold.reportId, scaffold.reportPeriod);
		this.scaffold = scaffold;
	}

	private static class Counter {
		int count;
	}

	/*
	 * The set of (GroupTypeId,GroupPropertyId) pairs collected from the
	 * GroupPropertyReportSettings supplied during initialization
	 */
	private final Map<GroupTypeId, Set<GroupPropertyId>> clientPropertyMap = new LinkedHashMap<>();
	private boolean isSubscribedToAllGroupPropertyUpdateEvents;
	private boolean isSubscribedToAllGroupAdditionEvents;
	private boolean isSubscribedToAllGroupRemovalEvents;
	/*
	 * For each (GroupTypeId,GroupPropertyId,property value) triplet, count the
	 * number of groups having that triplet
	 */
	private final Map<GroupTypeId, Map<GroupPropertyId, Map<Object, Counter>>> groupTypeMap = new LinkedHashMap<>();

	private ReportHeader reportHeader;

	private ReportHeader getReportHeader() {
		if (reportHeader == null) {
			ReportHeader.Builder reportHeaderBuilder = ReportHeader.builder();
			reportHeader = addTimeFieldHeaders(reportHeaderBuilder)//
																	.add("group_type")//
																	.add("property")//
																	.add("value")//
																	.add("group_count")//
																	.build();//
		}
		return reportHeader;
	}

	/*
	 * Decrement the number of groups for the given
	 * (GroupTypeId,GroupPropertyId,property value) triplet
	 */
	private void decrement(final GroupTypeId groupTypeId, final GroupPropertyId groupPropertyId, final Object groupPropertyValue) {
		getCounter(groupTypeId, groupPropertyId, groupPropertyValue).count--;
	}

	@Override
	protected void flush(ActorContext actorContext) {

		final ReportItem.Builder reportItemBuilder = ReportItem.builder();

		for (final GroupTypeId groupTypeId : groupTypeMap.keySet()) {
			final Map<GroupPropertyId, Map<Object, Counter>> propertyIdMap = groupTypeMap.get(groupTypeId);
			for (final GroupPropertyId groupPropertyId : propertyIdMap.keySet()) {
				final Map<Object, Counter> groupPropertyValueMap = propertyIdMap.get(groupPropertyId);
				for (final Object groupPropertyValue : groupPropertyValueMap.keySet()) {
					final Counter counter = groupPropertyValueMap.get(groupPropertyValue);
					if (counter.count > 0) {
						final int personCount = counter.count;
						reportItemBuilder.setReportHeader(getReportHeader());
						reportItemBuilder.setReportId(getReportId());

						fillTimeFields(reportItemBuilder);
						reportItemBuilder.addValue(groupTypeId.toString());
						reportItemBuilder.addValue(groupPropertyId.toString());
						reportItemBuilder.addValue(groupPropertyValue);
						reportItemBuilder.addValue(personCount);

						actorContext.releaseOutput(reportItemBuilder.build());
					}
				}
			}
		}
	}

	private Counter getCounter(final GroupTypeId groupTypeId, final GroupPropertyId groupPropertyId, final Object groupPropertyValue) {
		final Map<Object, Counter> propertyValueMap = groupTypeMap.get(groupTypeId).get(groupPropertyId);
		Counter counter = propertyValueMap.get(groupPropertyValue);
		if (counter == null) {
			counter = new Counter();
			propertyValueMap.put(groupPropertyValue, counter);
		}
		return counter;
	}

	/*
	 * Increment the number of groups for the given
	 * (GroupTypeId,GroupPropertyId,property value) triplet
	 */
	private void increment(final GroupTypeId groupTypeId, final GroupPropertyId groupPropertyId, final Object groupPropertyValue) {
		getCounter(groupTypeId, groupPropertyId, groupPropertyValue).count++;
	}

	private GroupsDataManager groupsDataManager;

	@Override
	public void init(final ActorContext actorContext) {
		super.init(actorContext);

		groupsDataManager = actorContext.getDataManager(GroupsDataManager.class);

		// transfer all VALID property id selections from the scaffold
		Set<GroupTypeId> groupTypeIds = groupsDataManager.getGroupTypeIds();
		for (GroupTypeId groupTypeId : groupTypeIds) {
			Set<GroupPropertyId> groupPropertyIds = new LinkedHashSet<>();
			if (scaffold.allProperties.contains(groupTypeId)) {
				groupPropertyIds.addAll(groupsDataManager.getGroupPropertyIds(groupTypeId));
			} else {
				Set<GroupPropertyId> selectedPropertyIds = scaffold.clientPropertyMap.get(groupTypeId);
				if (selectedPropertyIds != null) {
					Set<GroupPropertyId> allPropertyIds = groupsDataManager.getGroupPropertyIds(groupTypeId);
					for (GroupPropertyId groupPropertyId : allPropertyIds) {
						if (selectedPropertyIds.contains(groupPropertyId)) {
							groupPropertyIds.add(groupPropertyId);
						}
					}
				}
			}
			clientPropertyMap.put(groupTypeId, groupPropertyIds);
		}

		// determine the subscriptions for group addition
		if (clientPropertyMap.keySet().equals(groupsDataManager.getGroupTypeIds())) {
			actorContext.subscribe(GroupAdditionEvent.class, getFlushingConsumer(this::handleGroupAdditionEvent));
			isSubscribedToAllGroupAdditionEvents = true;

		} else {
			for (GroupTypeId groupTypeId : clientPropertyMap.keySet()) {
				EventLabel<GroupAdditionEvent> eventLabelByGroupType = GroupAdditionEvent.getEventLabelByGroupType(actorContext, groupTypeId);
				actorContext.subscribe(eventLabelByGroupType, getFlushingConsumer(this::handleGroupAdditionEvent));
			}
		}

		// determine the subscriptions for group removal observations
		if (clientPropertyMap.keySet().equals(groupsDataManager.getGroupTypeIds())) {
			actorContext.subscribe(GroupImminentRemovalEvent.class, getFlushingConsumer(this::handleGroupImminentRemovalEvent));
			isSubscribedToAllGroupRemovalEvents = true;
		} else {
			for (GroupTypeId groupTypeId : clientPropertyMap.keySet()) {
				EventLabel<GroupImminentRemovalEvent> eventLabelByGroupType = GroupImminentRemovalEvent.getEventLabelByGroupType(actorContext, groupTypeId);
				actorContext.subscribe(eventLabelByGroupType, getFlushingConsumer(this::handleGroupImminentRemovalEvent));
			}
		}

		// determine the subscriptions for group property changes
		boolean allPropertiesRequired = false;
		if (clientPropertyMap.keySet().equals(groupsDataManager.getGroupTypeIds())) {
			allPropertiesRequired = true;
			for (GroupTypeId groupTypeId : clientPropertyMap.keySet()) {
				if (!clientPropertyMap.get(groupTypeId).equals(groupsDataManager.getGroupPropertyIds(groupTypeId))) {
					allPropertiesRequired = false;
					break;
				}
			}
		}

		if (allPropertiesRequired) {
			isSubscribedToAllGroupPropertyUpdateEvents = true;
			actorContext.subscribe(GroupPropertyUpdateEvent.class, getFlushingConsumer(this::handleGroupPropertyUpdateEvent));
		} else {
			for (GroupTypeId groupTypeId : clientPropertyMap.keySet()) {
				if (clientPropertyMap.get(groupTypeId).equals(groupsDataManager.getGroupPropertyIds(groupTypeId))) {
					EventLabel<GroupPropertyUpdateEvent> eventLabelByGroupType = GroupPropertyUpdateEvent.getEventLabelByGroupType(actorContext, groupTypeId);
					actorContext.subscribe(eventLabelByGroupType, getFlushingConsumer(this::handleGroupPropertyUpdateEvent));
				} else {
					for (GroupPropertyId groupPropertyId : clientPropertyMap.get(groupTypeId)) {
						EventLabel<GroupPropertyUpdateEvent> eventLabelByGroupTypeAndProperty = GroupPropertyUpdateEvent.getEventLabelByGroupTypeAndProperty(actorContext, groupTypeId,
								groupPropertyId);
						actorContext.subscribe(eventLabelByGroupTypeAndProperty, getFlushingConsumer(this::handleGroupPropertyUpdateEvent));
					}
				}
			}
		}

		/*
		 * Fill the top layers of the groupTypeMap. We do not yet know the set
		 * of property values, so we leave that layer empty.
		 *
		 */

		for (GroupTypeId groupTypeId : clientPropertyMap.keySet()) {
			final Map<GroupPropertyId, Map<Object, Counter>> propertyIdMap = new LinkedHashMap<>();
			groupTypeMap.put(groupTypeId, propertyIdMap);
			Set<GroupPropertyId> groupPropertyIds = clientPropertyMap.get(groupTypeId);
			for (final GroupPropertyId groupPropertyId : groupPropertyIds) {
				final Map<Object, Counter> propertyValueMap = new LinkedHashMap<>();
				propertyIdMap.put(groupPropertyId, propertyValueMap);
			}
		}

		// group addition
		for (GroupId groupId : groupsDataManager.getGroupIds()) {
			final GroupTypeId groupTypeId = groupsDataManager.getGroupType(groupId);
			if (clientPropertyMap.containsKey(groupTypeId)) {
				for (final GroupPropertyId groupPropertyId : clientPropertyMap.get(groupTypeId)) {
					final Object groupPropertyValue = groupsDataManager.getGroupPropertyValue(groupId, groupPropertyId);
					increment(groupTypeId, groupPropertyId, groupPropertyValue);
				}
			}
		}

		if (scaffold.includeNewProperties) {
			actorContext.subscribe(GroupTypeAdditionEvent.class, getFlushingConsumer(this::handleGroupTypeAdditionEvent));
			actorContext.subscribe(GroupPropertyDefinitionEvent.class, getFlushingConsumer(this::handleGroupPropertyDefinitionEvent));
		}

	}

	private void handleGroupTypeAdditionEvent(ActorContext actorContext, GroupTypeAdditionEvent groupTypeAdditionEvent) {
		GroupTypeId groupTypeId = groupTypeAdditionEvent.getGroupTypeId();
		/*
		 * Are we subscribed to all group property assignments? if not then
		 * subscribe to all where the group type id is new one
		 */
		if (!isSubscribedToAllGroupPropertyUpdateEvents) {

			EventLabel<GroupPropertyUpdateEvent> eventLabelByGroupType = GroupPropertyUpdateEvent.getEventLabelByGroupType(actorContext, groupTypeId);
			actorContext.subscribe(eventLabelByGroupType, getFlushingConsumer(this::handleGroupPropertyUpdateEvent));
		}

		/*
		 * If we are not subscribed to all group addition events then we will need to subscribe by type
		 */
		if (isSubscribedToAllGroupAdditionEvents) {
			EventLabel<GroupAdditionEvent> eventLabelByGroupType = GroupAdditionEvent.getEventLabelByGroupType(actorContext, groupTypeId);
			actorContext.subscribe(eventLabelByGroupType, getFlushingConsumer(this::handleGroupAdditionEvent));
		}
		
		/*
		 * If we are not subscribed to all group removal events then we will need to subscribe by type
		 */
		if (isSubscribedToAllGroupRemovalEvents) {
			EventLabel<GroupImminentRemovalEvent> eventLabelByGroupType = GroupImminentRemovalEvent.getEventLabelByGroupType(actorContext, groupTypeId);
			actorContext.subscribe(eventLabelByGroupType, getFlushingConsumer(this::handleGroupImminentRemovalEvent));
		}

		/*
		 * update the client property map for the future addition of property
		 * ids associated with the new group type
		 */
		clientPropertyMap.put(groupTypeId, new LinkedHashSet<>());

		groupTypeMap.put(groupTypeId, new LinkedHashMap<>());

	}

	private void handleGroupPropertyDefinitionEvent(ActorContext actorContext, GroupPropertyDefinitionEvent groupPropertyDefinitionEvent) {
		// should we get the default value and integrate that in for all
		// existing groups of that type?

		GroupTypeId groupTypeId = groupPropertyDefinitionEvent.getGroupTypeId();
		GroupPropertyId groupPropertyId = groupPropertyDefinitionEvent.getGroupPropertyId();

		// if the group type was not previously added, then we are done
		Set<GroupPropertyId> groupPropertyIds = clientPropertyMap.get(groupTypeId);
		if (groupPropertyIds == null) {
			return;
		}
		// if the group property id was previously added, then we are done
		if (groupPropertyIds.contains(groupPropertyId)) {
			return;
		}
		groupPropertyIds.add(groupPropertyId);

		/*
		 * initialize the group type map with the initial counter of the number
		 * of groups having the default value associated with the new property
		 * id
		 */

		// build the needed structure into the groupTypeMap
		Map<GroupPropertyId, Map<Object, Counter>> map = groupTypeMap.get(groupTypeId);
		Map<Object, Counter> counterMap = new LinkedHashMap<>();
		map.put(groupPropertyId, counterMap);

		// determine how many groups of the given group type there are
		int groupCountForGroupType = groupsDataManager.getGroupCountForGroupType(groupTypeId);

		/*
		 * Rather than asking for the current value of the property for each
		 * group, we will assign the default value. We do this since there may
		 * be property value updates already performed and we want to reflect
		 * the expected conditions immediately after the property was added.
		 * 
		 */
		PropertyDefinition groupPropertyDefinition = groupsDataManager.getGroupPropertyDefinition(groupTypeId, groupPropertyId);
		Object defaultValue = groupPropertyDefinition.getDefaultValue().get();
		// create the counter with the number of groups
		Counter counter = new Counter();
		counter.count = groupCountForGroupType;

		// all groups will have the default value initially.
		counterMap.put(defaultValue, counter);

	}

	private void handleGroupPropertyUpdateEvent(ActorContext actorContext, GroupPropertyUpdateEvent groupPropertyUpdateEvent) {
		GroupId groupId = groupPropertyUpdateEvent.getGroupId();

		final GroupTypeId groupTypeId = groupsDataManager.getGroupType(groupId);
		if (clientPropertyMap.containsKey(groupTypeId)) {

			GroupPropertyId groupPropertyId = groupPropertyUpdateEvent.getGroupPropertyId();
			Object previousPropertyValue = groupPropertyUpdateEvent.getPreviousPropertyValue();
			Object currentPropertyValue = groupPropertyUpdateEvent.getCurrentPropertyValue();

			if (clientPropertyMap.get(groupTypeId).contains(groupPropertyId)) {

				increment(groupTypeId, groupPropertyId, currentPropertyValue);
				decrement(groupTypeId, groupPropertyId, previousPropertyValue);
			}
		}

	}

	private void handleGroupAdditionEvent(ActorContext actorContext, GroupAdditionEvent groupAdditionEvent) {
		GroupId groupId = groupAdditionEvent.getGroupId();
		final GroupTypeId groupTypeId = groupsDataManager.getGroupType(groupId);
		if (clientPropertyMap.containsKey(groupTypeId)) {
			for (final GroupPropertyId groupPropertyId : clientPropertyMap.get(groupTypeId)) {
				final Object groupPropertyValue = groupsDataManager.getGroupPropertyValue(groupId, groupPropertyId);
				increment(groupTypeId, groupPropertyId, groupPropertyValue);
			}
		}
	}

	private void handleGroupImminentRemovalEvent(ActorContext actorContext, GroupImminentRemovalEvent groupImminentRemovalEvent) {
		GroupId groupId = groupImminentRemovalEvent.getGroupId();
		final GroupTypeId groupTypeId = groupsDataManager.getGroupType(groupId);
		if (clientPropertyMap.containsKey(groupTypeId)) {
			Set<GroupPropertyId> groupPropertyIds = groupsDataManager.getGroupPropertyIds(groupTypeId);
			for (final GroupPropertyId groupPropertyId : groupPropertyIds) {
				if (clientPropertyMap.get(groupTypeId).contains(groupPropertyId)) {
					final Object groupPropertyValue = groupsDataManager.getGroupPropertyValue(groupId, groupPropertyId);
					decrement(groupTypeId, groupPropertyId, groupPropertyValue);
				}
			}
		}
	}
}