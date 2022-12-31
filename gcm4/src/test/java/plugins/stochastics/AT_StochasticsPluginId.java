package plugins.stochastics;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import tools.annotations.UnitTestField;

public class AT_StochasticsPluginId {

	@Test
	@UnitTestField(target = StochasticsPluginId.class, name = "PLUGIN_ID")
	public void testPluginId() {
		assertNotNull(StochasticsPluginId.PLUGIN_ID);
	}

}
