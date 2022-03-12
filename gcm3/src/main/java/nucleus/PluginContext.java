package nucleus;

import java.util.Optional;
import java.util.function.Consumer;

import nucleus.util.ContractException;

/**
 * A plugin context provides plugin's the ability to add actors and data
 * managers to the initialization of each simulation instance(scenario) in an
 * experiment. It provides the set of plugin data objects gathered from the
 * plugins that compose the experiment.
 * 
 * @author Shawn Hatch
 *
 */
public interface PluginContext {

	/**
	 * 
	 * Adds a data manager to the simulation.
	 * 
	 * 
	 * @throws ContractException
	 *             <li>{@link NucleusError#PLUGIN_INITIALIZATION_CLOSED} if
	 *             plugin initialization is over</li>
	 * 
	 */
	public void addDataManager(DataManager dataManager);

	/**
	 * 
	 * Adds an actor to the simulation.
	 * 
	 * 
	 * @throws ContractException
	 *             <li>{@link NucleusError#PLUGIN_INITIALIZATION_CLOSED} if
	 *             plugin initialization is over</li>
	 * 
	 */
	public ActorId addActor(Consumer<ActorContext> init);

	/**
	 * Returns the plugin data object associated with the given class reference
	 * 
	 * @throws ContractException
	 *             <li>{@linkplain NucleusError#AMBIGUOUS_PLUGIN_DATA_CLASS} if
	 *             more than one plugin data object matches the class
	 *             reference</li>
	 */
	public <T extends PluginData> Optional<T> getPluginData(Class<T> pluginDataClass);
}
