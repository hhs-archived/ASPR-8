package plugins.personproperties.support;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import nucleus.NucleusError;
import nucleus.SimulationContext;
import plugins.partitions.support.Equality;
import plugins.partitions.support.Filter;
import plugins.partitions.support.FilterSensitivity;
import plugins.partitions.support.PartitionError;
import plugins.people.support.PersonId;
import plugins.personproperties.datamanagers.PersonPropertiesDataManager;
import plugins.personproperties.events.PersonPropertyUpdateEvent;
import plugins.util.properties.PropertyDefinition;
import plugins.util.properties.PropertyError;
import util.errors.ContractException;

public final class PropertyFilter extends Filter {

	private final PersonPropertyId personPropertyId;
	private final Object personPropertyValue;
	private final Equality equality;
	private PersonPropertiesDataManager personPropertiesDataManager;

	private void validatePersonPropertyId(SimulationContext simulationContext, final PersonPropertyId personPropertyId) {
		
		if (personPropertiesDataManager == null) {
			personPropertiesDataManager = simulationContext.getDataManager(PersonPropertiesDataManager.class);
		}
		
		if (personPropertyId == null) {
			throw new ContractException(PropertyError.NULL_PROPERTY_ID);
		}

		if (!personPropertiesDataManager.personPropertyIdExists(personPropertyId)) {
			throw new ContractException(PropertyError.UNKNOWN_PROPERTY_ID, personPropertyId);
		}
	}

	private void validateEquality(SimulationContext simulationContext, final Equality equality) {
		if (equality == null) {
			throw new ContractException(PartitionError.NULL_EQUALITY_OPERATOR);
		}
	}

	private void validatePersonPropertyValueNotNull(SimulationContext simulationContext, final Object propertyValue) {
		if (propertyValue == null) {
			throw new ContractException(PropertyError.NULL_PROPERTY_VALUE);
		}
	}

	private void validateValueCompatibility(SimulationContext simulationContext, final Object propertyId, final PropertyDefinition propertyDefinition, final Object propertyValue) {
		if (!propertyDefinition.getType().isAssignableFrom(propertyValue.getClass())) {
			throw new ContractException(PropertyError.INCOMPATIBLE_VALUE, "Property value " + propertyValue + " is not of type " + propertyDefinition.getType().getName() + " and does not match definition of " + propertyId);
		}
	}

	private void validateEqualityCompatibility(SimulationContext simulationContext, final Object propertyId, final PropertyDefinition propertyDefinition, final Equality equality) {

		if (equality == Equality.EQUAL) {
			return;
		}
		if (equality == Equality.NOT_EQUAL) {
			return;
		}

		if (!Comparable.class.isAssignableFrom(propertyDefinition.getType())) {
			throw new ContractException(PartitionError.NON_COMPARABLE_ATTRIBUTE, "Property values for " + propertyId + " are not comparable via " + equality);
		}
	}

	public PropertyFilter(final PersonPropertyId personPropertyId, final Equality equality, final Object personPropertyValue) {
		this.personPropertyId = personPropertyId;
		this.personPropertyValue = personPropertyValue;
		this.equality = equality;
	}

	@Override
	public void validate(SimulationContext simulationContext) {
		if(simulationContext == null) {
			throw new ContractException(NucleusError.NULL_SIMULATION_CONTEXT);
		}
		if (personPropertiesDataManager == null) {
			personPropertiesDataManager = simulationContext.getDataManager(PersonPropertiesDataManager.class);
		}
		
		validatePersonPropertyId(simulationContext, personPropertyId);
		validateEquality(simulationContext, equality);
		validatePersonPropertyValueNotNull(simulationContext, personPropertyValue);		
		final PropertyDefinition propertyDefinition = personPropertiesDataManager.getPersonPropertyDefinition(personPropertyId);
		validateValueCompatibility(simulationContext, personPropertyId, propertyDefinition, personPropertyValue);
		validateEqualityCompatibility(simulationContext, personPropertyId, propertyDefinition, equality);
	}

	@Override
	public boolean evaluate(SimulationContext simulationContext, PersonId personId) {
		
		if(simulationContext == null) {
			throw new ContractException(NucleusError.NULL_SIMULATION_CONTEXT);
		}
		if (personPropertiesDataManager == null) {
			personPropertiesDataManager = simulationContext.getDataManager(PersonPropertiesDataManager.class);
		}
		
		// we do not assume that the returned property value is
		// comparable unless we are forced to.
		final Object propVal = personPropertiesDataManager.getPersonPropertyValue(personId, personPropertyId);

		return evaluate(propVal);

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private boolean evaluate(Object propVal) {
		if (equality.equals(Equality.EQUAL)) {
			return propVal.equals(personPropertyValue);
		} else if (equality.equals(Equality.NOT_EQUAL)) {
			return !propVal.equals(personPropertyValue);
		} else {
			Comparable comparablePropertyValue = (Comparable) propVal;
			int evaluation = comparablePropertyValue.compareTo(personPropertyValue);
			return equality.isCompatibleComparisonValue(evaluation);
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PropertyFilter [personPropertyId=");
		builder.append(personPropertyId);
		builder.append(", personPropertyValue=");
		builder.append(personPropertyValue);
		builder.append(", equality=");
		builder.append(equality);
		builder.append("]");
		return builder.toString();
	}

	private Optional<PersonId> requiresRefresh(SimulationContext simulationContext, PersonPropertyUpdateEvent event) {
		if (event.getPersonPropertyId().equals(personPropertyId)) {
			if (evaluate(event.getPreviousPropertyValue()) != evaluate(event.getCurrentPropertyValue())) {
				return Optional.of(event.getPersonId());
			}
		}
		return Optional.empty();
	}

	@Override
	public Set<FilterSensitivity<?>> getFilterSensitivities() {
		Set<FilterSensitivity<?>> result = new LinkedHashSet<>();
		result.add(new FilterSensitivity<PersonPropertyUpdateEvent>(PersonPropertyUpdateEvent.class, this::requiresRefresh));
		return result;
	}

}
