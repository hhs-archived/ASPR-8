package nucleus;

import java.util.function.BiFunction;

import net.jcip.annotations.NotThreadSafe;

/**
 * A convenience class that implements an event labeler
 *
 * @author Shawn Hatch
 *
 */
@NotThreadSafe
public class SimpleEventLabeler<T extends Event> implements EventLabeler<T> {
	private final Class<T> eventClass;
	private final BiFunction<SimulationContext, T, EventLabel<T>> labelMaker;
	private final EventLabelerId id;

	/**
	 * Constructs the event labeler from the given labeler id, event class and
	 * function for producing a label from an event.
	 */
	public SimpleEventLabeler(final EventLabelerId id, final Class<T> eventClass, BiFunction<SimulationContext, T, EventLabel<T>> labelMaker) {
		this.eventClass = eventClass;
		this.labelMaker = labelMaker;
		this.id = id;
	}

	@Override
	public final Class<T> getEventClass() {
		return eventClass;
	}

	@Override
	public EventLabel<T> getEventLabel(SimulationContext simulationContext, T event) {
		return labelMaker.apply(simulationContext, event);
	}
//	@SuppressWarnings("unchecked")
//	public EventLabel<T> getEventLabel2(SimulationContext simulationContext, Event event) {
//		return labelMaker.apply(simulationContext, (T)event);
//	}

	@Override
	public EventLabelerId getId() {
		return id;
	}

}
