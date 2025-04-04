package gov.hhs.aspr.ms.gcm.lessons;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.util.Pair;

import gov.hhs.aspr.ms.gcm.lessons.plugins.model.ModelPlugin;
import gov.hhs.aspr.ms.gcm.lessons.plugins.model.ModelReportLabel;
import gov.hhs.aspr.ms.gcm.lessons.plugins.model.support.GlobalProperty;
import gov.hhs.aspr.ms.gcm.simulation.nucleus.Dimension;
import gov.hhs.aspr.ms.gcm.simulation.nucleus.Experiment;
import gov.hhs.aspr.ms.gcm.simulation.nucleus.FunctionalDimension;
import gov.hhs.aspr.ms.gcm.simulation.nucleus.FunctionalDimensionData;
import gov.hhs.aspr.ms.gcm.simulation.nucleus.Plugin;
import gov.hhs.aspr.ms.gcm.simulation.plugins.globalproperties.GlobalPropertiesPlugin;
import gov.hhs.aspr.ms.gcm.simulation.plugins.globalproperties.datamanagers.GlobalPropertiesPluginData;
import gov.hhs.aspr.ms.gcm.simulation.plugins.globalproperties.reports.GlobalPropertyReportPluginData;
import gov.hhs.aspr.ms.gcm.simulation.plugins.properties.support.PropertyDefinition;
import gov.hhs.aspr.ms.gcm.simulation.plugins.reports.support.NIOReportItemHandler;


public final class Example_13 {

	private Example_13() {
	}

	/* start code_ref= global_proerties_plugin_get_property_data|code_cap=Initializing the global properties plugin data with three property definitions.*/
	private static GlobalPropertiesPluginData getGlobalPropertiesPluginData() {
		GlobalPropertiesPluginData.Builder builder = GlobalPropertiesPluginData.builder();//

		PropertyDefinition propertyDefinition = PropertyDefinition.builder()//
				.setType(Double.class)//
				.setDefaultValue(2.0)//
				.setPropertyValueMutability(false)//
				.build();
		builder.defineGlobalProperty(GlobalProperty.ALPHA, propertyDefinition, 0);

		propertyDefinition = PropertyDefinition.builder()//
				.setType(Double.class)//
				.setDefaultValue(5.0)//
				.setPropertyValueMutability(false)//
				.build();

		builder.defineGlobalProperty(GlobalProperty.BETA, propertyDefinition, 0);

		propertyDefinition = PropertyDefinition.builder()//
				.setType(Double.class)//
				.setDefaultValue(1.0)//
				.setPropertyValueMutability(true)//
				.build();

		builder.defineGlobalProperty(GlobalProperty.GAMMA, propertyDefinition, 0);

		return builder.build();
	}
	/* end */

	/* start code_ref= global_proerties_plugin_alpha_beta_dimension|code_cap=A dimension is created that adds five pairs of values over the ALPHA and BETA global properties. */
	private static Dimension getAlphaBetaDimension() {
		List<Pair<Double, Double>> alphaBetaPairs = new ArrayList<>();
		alphaBetaPairs.add(new Pair<>(3.0, 10.0));
		alphaBetaPairs.add(new Pair<>(12.0, 25.0));
		alphaBetaPairs.add(new Pair<>(30.0, 40.0));
		alphaBetaPairs.add(new Pair<>(45.0, 70.0));
		alphaBetaPairs.add(new Pair<>(80.0, 100.0));

		FunctionalDimensionData.Builder dimensionDataBuilder = FunctionalDimensionData.builder();

		for (int i = 0; i < alphaBetaPairs.size(); i++) {
			Pair<Double, Double> pair = alphaBetaPairs.get(i);
			dimensionDataBuilder.addValue("Level_" + i, (c) -> {
				List<String> result = new ArrayList<>();
				GlobalPropertiesPluginData.Builder builder = c
						.getPluginDataBuilder(GlobalPropertiesPluginData.Builder.class);
				builder.setGlobalPropertyValue(GlobalProperty.ALPHA, pair.getFirst(), 0);
				builder.setGlobalPropertyValue(GlobalProperty.BETA, pair.getSecond(), 0);
				result.add(pair.getFirst().toString());
				result.add(pair.getSecond().toString());
				return result;
			});
		}

		dimensionDataBuilder.addMetaDatum(GlobalProperty.ALPHA.toString());
		dimensionDataBuilder.addMetaDatum(GlobalProperty.BETA.toString());

		FunctionalDimensionData functionalDimensionData = dimensionDataBuilder.build();
		return new FunctionalDimension(functionalDimensionData);
	}

	/* end */

	/* start code_ref= global_proerties_plugin_example_13|code_cap= Using the global properties plugin to add three global properties. */
	public static void main(String[] args) throws IOException {
		if (args.length == 0) {
			throw new RuntimeException("One output directory argument is required");
		}
		Path outputDirectory = Paths.get(args[0]);
		if (!Files.exists(outputDirectory)) {
			Files.createDirectory(outputDirectory);
		} else {
			if (!Files.isDirectory(outputDirectory)) {
				throw new IOException("Provided path is not a directory");
			}
		}

		GlobalPropertiesPluginData globalPropertiesPluginData = getGlobalPropertiesPluginData();

		GlobalPropertyReportPluginData globalPropertyReportPluginData = GlobalPropertyReportPluginData.builder()//
				.setReportLabel(ModelReportLabel.GLOBAL_PROPERTY_REPORT)//
				.setDefaultInclusion(true)//
				.build();

		Plugin globalPropertiesPlugin = GlobalPropertiesPlugin.builder()
				.setGlobalPropertiesPluginData(globalPropertiesPluginData)
				.setGlobalPropertyReportPluginData(globalPropertyReportPluginData)//
				.getGlobalPropertiesPlugin();

		Plugin modelPlugin = ModelPlugin.getModelPlugin();

		NIOReportItemHandler nioReportItemHandler = //
				NIOReportItemHandler.builder()//
						.addReport(ModelReportLabel.GLOBAL_PROPERTY_REPORT, //
								outputDirectory.resolve("global property report.xls"))//
						.build();

		Dimension alphaBetaDimension = getAlphaBetaDimension();

		Experiment.builder()//
				.addPlugin(globalPropertiesPlugin)//
				.addPlugin(modelPlugin)//
				.addExperimentContextConsumer(nioReportItemHandler)//
				.addDimension(alphaBetaDimension)//
				.build()//
				.execute();//

	}
	/* end */

}
