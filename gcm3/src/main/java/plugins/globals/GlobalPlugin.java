package plugins.globals;

import nucleus.Plugin;
import plugins.reports.ReportsPluginId;

public final class GlobalPlugin {
	
	public static Plugin getPlugin(GlobalPluginData globalPluginData) {
		return Plugin.builder()
				.addPluginData(globalPluginData)
				.addPluginDependency(ReportsPluginId.PLUGIN_ID)
				.setInitializer((c)->{
					GlobalPluginData data = c.getPluginData(GlobalPluginData.class).get();
					c.addDataManager(new GlobalDataManager(data));
				})
				.setPluginId(GlobalsPluginId.PLUGIN_ID)
				.build();
	}


}
