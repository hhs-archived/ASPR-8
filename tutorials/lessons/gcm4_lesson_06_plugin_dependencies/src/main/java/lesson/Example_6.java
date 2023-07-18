package lesson;

import lesson.plugins.family.FamilyDataManager;
import lesson.plugins.model.ModelActor;
import lesson.plugins.people.PersonDataManager;
import lesson.plugins.vaccine.VaccinationDataManager;
import nucleus.Plugin;
import nucleus.PluginId;
import nucleus.SimplePluginId;
import nucleus.Simulation;

public final class Example_6 {

	private Example_6() {
	}

	/* start code_ref=plugin_dependencies_connecting_the_plugins */
	public static void main(String[] args) {

		PluginId peoplePluginId = new SimplePluginId("people plugin");
		Plugin peoplePlugin = Plugin.builder()//
				.setPluginId(peoplePluginId)//
				.setInitializer(pluginContext -> {
					pluginContext.addDataManager(new PersonDataManager());
				})//
				.build();

		PluginId vaccinePluginId = new SimplePluginId("vaccine plugin");
		Plugin vaccinePlugin = Plugin.builder()//
				.setPluginId(vaccinePluginId)//
				.addPluginDependency(peoplePluginId)//
				.setInitializer(pluginContext -> {
					pluginContext.addDataManager(new VaccinationDataManager());
				})//
				.build();

		PluginId familyPluginId = new SimplePluginId("family plugin");
		Plugin familyPlugin = Plugin.builder()//
				.setPluginId(familyPluginId)//
				.addPluginDependency(peoplePluginId)//
				.setInitializer(pluginContext -> {
					pluginContext.addDataManager(new FamilyDataManager());
				})//
				.build();

		PluginId modelPluginId = new SimplePluginId("model plugin");
		Plugin modelPlugin = Plugin.builder()//
				.setPluginId(modelPluginId)//
				.setInitializer(pluginContext -> {
					pluginContext.addActor(new ModelActor()::init);

				})//
				.build();

		Simulation.builder()//
				.addPlugin(vaccinePlugin)//
				.addPlugin(familyPlugin)//
				.addPlugin(peoplePlugin)//
				.addPlugin(modelPlugin)//
				.build()//
				.execute();
	}
	/* end */

}
