package plugins.materials.events;

import net.jcip.annotations.Immutable;
import nucleus.Event;
import plugins.materials.support.StageId;

@Immutable
public record StageOfferUpdateEvent(StageId stageId, boolean previousOfferState,
									boolean currentOfferState) implements Event {
}
