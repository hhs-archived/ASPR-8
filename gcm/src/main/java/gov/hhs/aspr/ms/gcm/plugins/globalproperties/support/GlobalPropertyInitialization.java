package gov.hhs.aspr.ms.gcm.plugins.globalproperties.support;

import java.util.Optional;

import gov.hhs.aspr.ms.gcm.plugins.properties.support.PropertyDefinition;
import gov.hhs.aspr.ms.gcm.plugins.properties.support.PropertyError;
import net.jcip.annotations.Immutable;
import util.errors.ContractException;

/**
 * Immutable data class that represents the addition of a global property
 */
@Immutable
public class GlobalPropertyInitialization {

	private static class Data {
		private GlobalPropertyId globalPropertyId;
		private PropertyDefinition propertyDefinition;
		private Object value;

		public Data() {
		}

		public Data(Data data) {
			globalPropertyId = data.globalPropertyId;
			propertyDefinition = data.propertyDefinition;
			value = data.value;
		}

	}

	/**
	 * Returns a new Builder instance
	 */
	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private Data data = new Data();

		private Builder() {
		}

		private void validate() {
			if (data.propertyDefinition == null) {
				throw new ContractException(PropertyError.NULL_PROPERTY_DEFINITION);
			}
			if (data.globalPropertyId == null) {
				throw new ContractException(PropertyError.NULL_PROPERTY_ID);
			}
			if (data.value == null) {
				if (data.propertyDefinition.getDefaultValue().isEmpty()) {
					throw new ContractException(PropertyError.INSUFFICIENT_PROPERTY_VALUE_ASSIGNMENT);
				}
			} else {
				if (!data.propertyDefinition.getType().isAssignableFrom(data.value.getClass())) {
					throw new ContractException(PropertyError.INCOMPATIBLE_VALUE);
				}
			}
		}

		/**
		 * Returns the GlobalPropertyInitialization formed from the inputs.
		 * 
		 * @throws ContractException
		 *                           <ul>
		 *                           <li>{@linkplain PropertyError#NULL_PROPERTY_DEFINITION}
		 *                           if no property definition was provided</li>
		 *                           <li>{@linkplain PropertyError#NULL_PROPERTY_ID} if
		 *                           no property id was provided</li>
		 *                           <li>{@linkplain PropertyError#INSUFFICIENT_PROPERTY_VALUE_ASSIGNMENT}
		 *                           if no property value was provided and the property
		 *                           definition does not contain a default value</li>
		 *                           <li>(@linkplain PropertyError#INCOMPATIBLE_VALUE)
		 *                           if the property value type is not compatible with
		 *                           the property definition</li>
		 *                           </ul>
		 */
		public GlobalPropertyInitialization build() {
			validate();
			return new GlobalPropertyInitialization(new Data(data));
		}

		/**
		 * Sets the property id.
		 * 
		 * @throws ContractException {@linkplain PropertyError#NULL_PROPERTY_ID} if the
		 *                           property id is null
		 */
		public Builder setGlobalPropertyId(GlobalPropertyId globalPropertyId) {
			if (globalPropertyId == null) {
				throw new ContractException(PropertyError.NULL_PROPERTY_ID);
			}
			data.globalPropertyId = globalPropertyId;
			return this;
		}

		/**
		 * Sets the property definition.
		 * 
		 * @throws ContractException {@linkplain PropertyError#NULL_PROPERTY_DEFINITION}
		 *                           if the property definition is null
		 */
		public Builder setPropertyDefinition(PropertyDefinition propertyDefinition) {
			if (propertyDefinition == null) {
				throw new ContractException(PropertyError.NULL_PROPERTY_DEFINITION);
			}
			data.propertyDefinition = propertyDefinition;
			return this;
		}

		/**
		 * Sets the value of the global property that overrides any default value
		 * provided by the property definition
		 * 
		 * @throws ContractException {@linkplain PropertyError#NULL_PROPERTY_VALUE} if
		 *                           the value is null
		 */
		public Builder setValue(Object value) {
			if (value == null) {
				throw new ContractException(PropertyError.NULL_PROPERTY_VALUE);
			}
			data.value = value;
			return this;
		}
	}

	private final Data data;

	private GlobalPropertyInitialization(Data data) {
		this.data = data;
	}

	/**
	 * Returns the global property id
	 */
	public GlobalPropertyId getGlobalPropertyId() {
		return data.globalPropertyId;
	}

	/**
	 * Returns the property definition
	 */
	public PropertyDefinition getPropertyDefinition() {
		return data.propertyDefinition;
	}

	/**
	 * Returns the property value
	 */
	public Optional<Object> getValue() {
		return Optional.ofNullable(data.value);
	}
}
