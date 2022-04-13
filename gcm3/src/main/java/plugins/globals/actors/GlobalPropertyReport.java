package plugins.globals.actors;

import java.util.LinkedHashSet;
import java.util.Set;

import nucleus.ActorContext;
import nucleus.EventLabel;
import nucleus.util.ContractException;
import plugins.globals.GlobalDataManager;
import plugins.globals.events.GlobalPropertyUpdateEvent;
import plugins.globals.support.GlobalError;
import plugins.globals.support.GlobalPropertyId;
import plugins.reports.support.ReportHeader;
import plugins.reports.support.ReportId;
import plugins.reports.support.ReportItem;

/**
 * A Report that displays assigned global property values over time.
 *
 *
 * Fields
 *
 * Time -- the time in days when the global property was set
 *
 * Property -- the global property identifier
 *
 * Value -- the value of the global property
 *
 * @author Shawn Hatch
 *
 */
public final class GlobalPropertyReport {

	private ReportHeader reportHeader;

	/*
	 * The constrained set of person properties that will be used in this
	 * report. They are set during init()
	 */
	private final Set<GlobalPropertyId> globalPropertyIds = new LinkedHashSet<>();

	private ReportHeader getReportHeader() {
		if (reportHeader == null) {
			reportHeader = ReportHeader	.builder()//
										.add("time")//
										.add("property")//
										.add("value")//
										.build();//
		}
		return reportHeader;
	}

	private void handleGlobalPropertyUpdateEvent(ActorContext actorContext, GlobalPropertyUpdateEvent globalPropertyUpdateEvent) {
		GlobalPropertyId globalPropertyId = globalPropertyUpdateEvent.getGlobalPropertyId();
		if (globalPropertyIds.contains(globalPropertyId)) {
			writeProperty(actorContext, globalPropertyId,globalPropertyUpdateEvent.getCurrentPropertyValue());
		}
	}

	
	private final ReportId reportId;
	
	public GlobalPropertyReport(ReportId reportId ,GlobalPropertyId... globalPropertyIds) {
		this.reportId = reportId;
		for (GlobalPropertyId globalPropertyId : globalPropertyIds) {
			this.globalPropertyIds.add(globalPropertyId);
		}
	}

	/**
	 * Initialization of the report.  Subscribes to GlobalPropertyUpdateEvent.
	 */
	public void init(final ActorContext actorContext) {

		GlobalDataManager globalDataManager = actorContext.getDataManager(GlobalDataManager.class).get();

		/*
		 * If no global properties were specified, then assume all are wanted
		 */
		if (globalPropertyIds.size() == 0) {
			globalPropertyIds.addAll(globalDataManager.getGlobalPropertyIds());
		}

		/*
		 * Ensure that every client supplied property identifier is valid
		 */
		final Set<GlobalPropertyId> validPropertyIds = globalDataManager.getGlobalPropertyIds();
		for (final GlobalPropertyId globalPropertyId : globalPropertyIds) {
			if (!validPropertyIds.contains(globalPropertyId)) {
				throw new ContractException(GlobalError.UNKNOWN_GLOBAL_PROPERTY_ID, globalPropertyId);
			}
		}

		if (globalPropertyIds.equals(globalDataManager.getGlobalPropertyIds())) {
			actorContext.subscribe(GlobalPropertyUpdateEvent.class, this::handleGlobalPropertyUpdateEvent);
		} else {
			for (GlobalPropertyId globalPropertyId : globalPropertyIds) {
				EventLabel<GlobalPropertyUpdateEvent> eventLabel = GlobalPropertyUpdateEvent.getEventLabel(actorContext, globalPropertyId);
				actorContext.subscribe(eventLabel, this::handleGlobalPropertyUpdateEvent);
			}
		}

		for (final GlobalPropertyId globalPropertyId : globalPropertyIds) {
			Object globalPropertyValue = globalDataManager.getGlobalPropertyValue(globalPropertyId);
			writeProperty(actorContext, globalPropertyId,globalPropertyValue);
		}
	}

	private void writeProperty(ActorContext actorContext, final GlobalPropertyId globalPropertyId,Object globalPropertyValue) {
		final ReportItem.Builder reportItemBuilder = ReportItem.builder();
		reportItemBuilder.setReportHeader(getReportHeader());
		reportItemBuilder.setReportId(reportId);
		reportItemBuilder.addValue(actorContext.getTime());
		reportItemBuilder.addValue(globalPropertyId.toString());
		reportItemBuilder.addValue(globalPropertyValue);
		actorContext.releaseOutput(reportItemBuilder.build());
	}

}