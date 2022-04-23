package plugins.globalproperties.testsupport;

import java.util.function.Consumer;

import nucleus.ActorContext;
import nucleus.Plugin;
import nucleus.Simulation;
import nucleus.testsupport.testplugin.ScenarioPlanCompletionObserver;
import nucleus.testsupport.testplugin.TestActorPlan;
import nucleus.testsupport.testplugin.TestError;
import nucleus.testsupport.testplugin.TestPlugin;
import nucleus.testsupport.testplugin.TestPluginData;
import nucleus.util.ContractException;
import plugins.globalproperties.GlobalPropertiesPlugin;
import plugins.globalproperties.GlobalPropertiesPluginData;
import plugins.reports.ReportsPlugin;
import plugins.reports.ReportsPluginData;

/**
 * A static test support class for the globals plugin. Provides convenience
 * methods for integrating a test plugin into a global-properties simulation
 * test harness.
 * 
 * 
 * @author Shawn Hatch
 *
 */

public class GlobalsPropertiesActionSupport {

	/**
	 * Creates the test plugin containing a test actor initialized by the given
	 * consumer. Executes the simulation via the
	 * {@linkplain GlobalsPropertiesActionSupport#testConsumers(Plugin)} method
	 *
	 */

	public static void testConsumer(Consumer<ActorContext> consumer) {

		TestPluginData testPluginData = TestPluginData	.builder()//
														.addTestActorPlan("actor", new TestActorPlan(0, consumer))//
														.build();

		Plugin plugin = TestPlugin.getTestPlugin(testPluginData);
		testConsumers(plugin);
	}

	/**
	 * Executes a simulation composed of the given test plugin and the global
	 * plugin initialized with the {@linkplain TestGlobalPropertyId} properties.
	 */
	public static void testConsumers(Plugin testPlugin) {

		GlobalPropertiesPluginData.Builder globalsPluginBuilder = GlobalPropertiesPluginData.builder();
		for (TestGlobalPropertyId testGlobalPropertyId : TestGlobalPropertyId.values()) {
			globalsPluginBuilder.defineGlobalProperty(testGlobalPropertyId, testGlobalPropertyId.getPropertyDefinition());
		}
		GlobalPropertiesPluginData globalPropertiesPluginData = globalsPluginBuilder.build();
		Plugin globalsPlugin = GlobalPropertiesPlugin.getPlugin(globalPropertiesPluginData);

		ScenarioPlanCompletionObserver scenarioPlanCompletionObserver = new ScenarioPlanCompletionObserver();
		Simulation	.builder()//
					.addPlugin(ReportsPlugin.getReportPlugin(ReportsPluginData.builder().build()))//
					.addPlugin(globalsPlugin)//
					.setOutputConsumer(scenarioPlanCompletionObserver::handleOutput).addPlugin(testPlugin)//
					.build()//
					.execute();//

		// show that all actions were executed
		if (!scenarioPlanCompletionObserver.allPlansExecuted()) {
			throw new ContractException(TestError.TEST_EXECUTION_FAILURE);
		}
	}
}
