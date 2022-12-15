package plugins.materials.actors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.util.FastMath;
import org.junit.jupiter.api.Test;

import nucleus.ActorContext;
import nucleus.Plugin;
import nucleus.testsupport.testplugin.TestActorPlan;
import nucleus.testsupport.testplugin.TestPlugin;
import nucleus.testsupport.testplugin.TestPluginData;
import plugins.materials.datamangers.MaterialsDataManager;
import plugins.materials.support.MaterialsProducerConstructionData;
import plugins.materials.support.MaterialsProducerId;
import plugins.materials.support.StageId;
import plugins.materials.testsupport.MaterialsActionSupport;
import plugins.materials.testsupport.TestMaterialsProducerId;
import plugins.materials.testsupport.TestMaterialsProducerPropertyId;
import plugins.regions.testsupport.TestRegionId;
import plugins.reports.support.ReportHeader;
import plugins.reports.support.ReportId;
import plugins.reports.support.ReportItem;
import plugins.reports.support.ReportItem.Builder;
import plugins.reports.support.SimpleReportId;
import plugins.resources.datamanagers.ResourcesDataManager;
import plugins.resources.support.ResourceId;
import plugins.resources.testsupport.TestResourceId;
import plugins.stochastics.StochasticsDataManager;
import plugins.util.properties.TimeTrackingPolicy;
import tools.annotations.UnitTag;
import tools.annotations.UnitTest;
import tools.annotations.UnitTestConstructor;
import tools.annotations.UnitTestMethod;

@UnitTest(target = MaterialsProducerResourceReport.class)
public final class AT_MaterialsProducerResourceReport {

	private ReportItem getReportItemFromResourceId(ActorContext agentContext, MaterialsProducerId materialsProducerId,
			ResourceId resourceId, long amount) {

		String actionName;
		if (amount < 0) {
			actionName = "Removed";
		} else {
			actionName = "Added";
		}

		long reportedAmount = FastMath.abs(amount);

		return getReportItem(agentContext.getTime(), resourceId, materialsProducerId, actionName, reportedAmount);

	}

	@Test
	@UnitTestConstructor(args = { ReportId.class })
	public void testConstructor() {
		MaterialsProducerResourceReport report = new MaterialsProducerResourceReport(REPORT_ID);
		assertNotNull(report);
	}

	@Test
	@UnitTestMethod(name = "init", args = { ActorContext.class }, tags = { UnitTag.INCOMPLETE })
	public void testInit() {
		Set<ReportItem> expectedReportItems = new LinkedHashSet<>();

		TestPluginData.Builder pluginBuilder = TestPluginData.builder();

		MaterialsProducerId newMaterialsProducerId = TestMaterialsProducerId.getUnknownMaterialsProducerId();

		double actionTime = 0;
		pluginBuilder.addTestActorPlan("actor", new TestActorPlan(actionTime++, (c) -> {
			for (TestMaterialsProducerId testMaterialsProducerId : TestMaterialsProducerId.values()) {
				for (TestResourceId testResourceId : TestResourceId.values()) {
					expectedReportItems
							.add(getReportItemFromResourceId(c, testMaterialsProducerId, testResourceId, 0L));
				}
			}

			ResourceId newResourceId = TestResourceId.getUnknownResourceId();
			ResourcesDataManager resourcesDataManager = c.getDataManager(ResourcesDataManager.class);
			resourcesDataManager.addResourceId(newResourceId, TimeTrackingPolicy.TRACK_TIME);

			for (TestMaterialsProducerId testMaterialsProducerId : TestMaterialsProducerId.values()) {
				expectedReportItems.add(getReportItemFromResourceId(c, testMaterialsProducerId, newResourceId, 0L));
			}

		}));

		// add a new materials producer just before time 20
		pluginBuilder.addTestActorPlan("actor", new TestActorPlan(19.5, (c) -> {
			StochasticsDataManager stochasticsDataManager = c.getDataManager(StochasticsDataManager.class);
			RandomGenerator randomGenerator = stochasticsDataManager.getRandomGenerator();
			MaterialsDataManager materialsDataManager = c.getDataManager(MaterialsDataManager.class);
			MaterialsProducerConstructionData.Builder builder = //
					MaterialsProducerConstructionData.builder()//
							.setMaterialsProducerId(newMaterialsProducerId);//
			for (TestMaterialsProducerPropertyId testMaterialsProducerPropertyId : TestMaterialsProducerPropertyId
					.getPropertiesWithoutDefaultValues()) {
				Object randomPropertyValue = testMaterialsProducerPropertyId.getRandomPropertyValue(randomGenerator);
				builder.setMaterialsProducerPropertyValue(testMaterialsProducerPropertyId, randomPropertyValue);
			}

			MaterialsProducerConstructionData materialsProducerConstructionData = builder.build();

			materialsDataManager.addMaterialsProducer(materialsProducerConstructionData);
			ResourcesDataManager resourcesDataManager = c.getDataManager(ResourcesDataManager.class);
			for (ResourceId resourceId : resourcesDataManager.getResourceIds()) {
				expectedReportItems.add(getReportItemFromResourceId(c, newMaterialsProducerId, resourceId, 0L));
			}

		}));

		for (int i = 0; i < 100; i++) {

			// set a resource value
			pluginBuilder.addTestActorPlan("actor", new TestActorPlan(actionTime++, (c) -> {
				MaterialsDataManager materialsDataManager = c.getDataManager(MaterialsDataManager.class);
				List<MaterialsProducerId> mats = new ArrayList<>(materialsDataManager.getMaterialsProducerIds());

				StochasticsDataManager stochasticsDataManager = c.getDataManager(StochasticsDataManager.class);
				RandomGenerator randomGenerator = stochasticsDataManager.getRandomGenerator();
				MaterialsProducerId materialsProducerId = mats.get(randomGenerator.nextInt(mats.size()));
				ResourcesDataManager resourcesDataManager = c.getDataManager(ResourcesDataManager.class);
				List<ResourceId> resourceIds = new ArrayList<>(resourcesDataManager.getResourceIds());
				Random random = new Random(randomGenerator.nextLong());

				ResourceId resourceId = resourceIds.get(random.nextInt(resourceIds.size()));

				if (randomGenerator.nextBoolean()) {
					long amount = randomGenerator.nextInt(100) + 1;
					StageId stageId = materialsDataManager.addStage(materialsProducerId);
					materialsDataManager.convertStageToResource(stageId, resourceId, amount);
					expectedReportItems.add(getReportItemFromResourceId(c, materialsProducerId, resourceId, amount));
				} else {
					long resourceLevel = materialsDataManager.getMaterialsProducerResourceLevel(materialsProducerId,
							resourceId);
					if (resourceLevel > 0) {
						long amount = randomGenerator.nextInt((int) resourceLevel) + 1;
						TestRegionId testRegionId = TestRegionId.getRandomRegionId(randomGenerator);
						materialsDataManager.transferResourceToRegion(materialsProducerId, resourceId, testRegionId,
								amount);
						expectedReportItems
								.add(getReportItemFromResourceId(c, materialsProducerId, resourceId, -amount));
					}
				}
			}));
		}

		TestPluginData testPluginData = pluginBuilder.build();
		Plugin testPlugin = TestPlugin.getTestPlugin(testPluginData);
		Set<ReportItem> actualReportItems = MaterialsActionSupport.testConsumers(6081341958178733565L, testPlugin,
				new MaterialsProducerResourceReport(REPORT_ID)::init);

		assertEquals(expectedReportItems, actualReportItems);
	}

	private static ReportItem getReportItem(Object... values) {
		Builder builder = ReportItem.builder().setReportId(REPORT_ID).setReportHeader(REPORT_HEADER);
		for (Object value : values) {
			builder.addValue(value);
		}
		return builder.build();
	}

	private static final ReportId REPORT_ID = new SimpleReportId("report");

	private static final ReportHeader REPORT_HEADER = getReportHeader();

	private static ReportHeader getReportHeader() {
		return ReportHeader.builder()//
				.add("time")//
				.add("resource")//
				.add("materials_producer")//
				.add("action")//
				.add("amount")//
				.build();

	}

}
