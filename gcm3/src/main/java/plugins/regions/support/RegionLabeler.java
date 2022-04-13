package plugins.regions.support;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import nucleus.Event;
import nucleus.NucleusError;
import nucleus.SimulationContext;
import nucleus.util.ContractException;
import plugins.partitions.support.Labeler;
import plugins.partitions.support.LabelerSensitivity;
import plugins.people.support.PersonId;
import plugins.regions.datamanagers.RegionDataManager;
import plugins.regions.events.PersonRegionChangeObservationEvent;

/**
 * A labeler for regions. The dimension of the labeler is the
 * {@linkplain RegionId} class, the event that stimulates a label update is
 * {@linkplain PersonRegionChangeObservationEvent} and the labeling function is
 * composed from the given Function.
 * 
 * @author Shawn Hatch
 *
 */
public final class RegionLabeler implements Labeler {

	private final Function<RegionId, Object> regionLabelingFunction;

	private RegionDataManager regionDataManager;

	/**
	 * Creates the Region labeler from the given labeling function
	 * 
	 * 
	 */
	public RegionLabeler(Function<RegionId, Object> regionLabelingFunction) {
		this.regionLabelingFunction = regionLabelingFunction;
	}

	private Optional<PersonId> getPersonId(PersonRegionChangeObservationEvent personRegionChangeObservationEvent) {
		return Optional.of(personRegionChangeObservationEvent.getPersonId());
	}

	@Override
	public Set<LabelerSensitivity<?>> getLabelerSensitivities() {
		Set<LabelerSensitivity<?>> result = new LinkedHashSet<>();
		result.add(new LabelerSensitivity<PersonRegionChangeObservationEvent>(PersonRegionChangeObservationEvent.class, this::getPersonId));
		return result;
	}

	@Override
	public Object getLabel(SimulationContext simulationContext, PersonId personId) {
		if (simulationContext == null) {
			throw new ContractException(NucleusError.NULL_SIMULATION_CONTEXT);
		}
		if (regionDataManager == null) {
			regionDataManager = simulationContext.getDataManager(RegionDataManager.class).get();
		}
		RegionId regionId = regionDataManager.getPersonRegion(personId);
		return regionLabelingFunction.apply(regionId);
	}

	@Override
	public Object getDimension() {
		return RegionId.class;
	}

	@Override
	public Object getPastLabel(SimulationContext simulationContext, Event event) {
		PersonRegionChangeObservationEvent personRegionChangeObservationEvent = (PersonRegionChangeObservationEvent) event;
		return regionLabelingFunction.apply(personRegionChangeObservationEvent.getPreviousRegionId());
	}

}
