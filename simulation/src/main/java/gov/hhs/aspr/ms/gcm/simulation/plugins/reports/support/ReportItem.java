package gov.hhs.aspr.ms.gcm.simulation.plugins.reports.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import gov.hhs.aspr.ms.util.errors.ContractException;
import net.jcip.annotations.NotThreadSafe;
import net.jcip.annotations.ThreadSafe;

/**
 * A thread safe(immutable), container that supports output lines for multiple
 * reports. The values contained in a report item should be immutable and
 * support toString().
 */
@ThreadSafe
public final class ReportItem {

	/**
	 * Returns a new Builder instance.
	 */
	public static Builder builder() {
		return new Builder(new Data());
	}

	@NotThreadSafe
	public final static class Builder {
		private Data data;
		
		private Builder(Data data) {
			this.data = data;
		}

		/**
		 * Adds an entry's string value to the report item. Order should follow the
		 * order in the {@link ReportHeader}
		 * 
		 * @throws ContractException if the entry is null
		 */
		public Builder addValue(final Object entry) {
			ensureDataMutability();
			if (entry == null) {
				throw new ContractException(ReportError.NULL_REPORT_ITEM_ENTRY);
			}
			data.values.add(entry.toString());
			return this;
		}

		/*
		 * Null checks for the various fields.
		 */
		private void validateData() {

			if (data.reportLabel == null) {
				throw new ContractException(ReportError.NULL_REPORT_LABEL);
			}

		}

		/**
		 * Builds the {@link ReportItem} from the collected data.
		 * 
		 * @throws ContractException
		 *                           <ul>
		 *                           <li>{@linkplain ReportError#NULL_REPORT_HEADER} if
		 *                           the collected report header is null</li>
		 *                           <li>{@linkplain ReportError#NULL_REPORT_LABEL} if
		 *                           the collected report label is null</li>
		 *                           </ul>
		 */
		public ReportItem build() {
			if (!data.locked) {
				validateData();
			}
			ensureImmutability();
			return new ReportItem(new Data(data));
		}

		/**
		 * Sets the report type for this {@link ReportItem}. The report type should be
		 * the class type of the report that authors the report item.
		 */
		public Builder setReportLabel(ReportLabel reportLabel) {
			ensureDataMutability();
			if (reportLabel == null) {
				throw new ContractException(ReportError.NULL_REPORT_LABEL);
			}
			data.reportLabel = reportLabel;
			return this;
		}

		private void ensureDataMutability() {
			if (data.locked) {
				data = new Data(data);
				data.locked = false;
			}
		}

		private void ensureImmutability() {
			if (!data.locked) {
				data.locked = true;
			}
		}

	}

	private static class Data {
		private ReportLabel reportLabel;
		private final List<String> values = new ArrayList<>();
		private boolean locked;

		private Data() {
		}

		private Data(Data data) {
			reportLabel = data.reportLabel;
			values.addAll(data.values);
			locked = data.locked;
		}

		/**
    	 * Standard implementation consistent with the {@link #equals(Object)} method
    	 */
		@Override
		public int hashCode() {
			return Objects.hash(reportLabel, values);
		}

		/**
    	 * Two {@link Data} instances are equal if and only if
    	 * their inputs are equal.
    	 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Data other = (Data) obj;
			return Objects.equals(reportLabel, other.reportLabel) && Objects.equals(values, other.values);
		}
	}

	private final Data data;

	private ReportItem(final Data data) {
		this.data = data;
	}

	/**
	 * Returns the report label for this report item
	 */
	public ReportLabel getReportLabel() {
		return data.reportLabel;
	}

	/**
	 * Returns the string value stored at the given index
	 *
	 * @throws IndexOutOfBoundsException
	 *                                   <ul>
	 *                                   <li>if the index &lt; 0</li>
	 *                                   <li>if the index &gt;= size()</li>
	 *                                   </ul>
	 */
	public String getValue(final int index) {
		return data.values.get(index);
	}

	/**
	 * Returns the number of values stored in this report item
	 *
	 * @return
	 */
	public int size() {
		return data.values.size();
	}

	/**
	 * A string listing the values as added to this ReportItem delimited by commas
	 * in the form: ReportItem
	 * [reportLabel=reportLabel,values=[value1, value2...]]
	 */
	@Override
	public String toString() {
		StringBuilder builder2 = new StringBuilder();
		builder2.append("ReportItem [reportLabel=");
		builder2.append(data.reportLabel);
		builder2.append(", values=");
		builder2.append(data.values);
		builder2.append("]");
		return builder2.toString();
	}

	/**
	 * Returns the values in the form [value[0], value[1], ... ,value[N-1]]
	 */
	public String toValueString() {
		return data.values.toString();
	}

	/**
     * Standard implementation consistent with the {@link #equals(Object)} method
     */
	@Override
	public int hashCode() {
		return Objects.hash(data);
	}

	/**
     * Two {@link ReportItem} instances are equal if and only if
     * their inputs are equal.
     */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ReportItem other = (ReportItem) obj;
		return Objects.equals(data, other.data);
	}

	public Builder toBuilder() {
		return new Builder(data);
	}

}
