package plugins.groups;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import tools.annotations.UnitTestField;

public class AT_GroupsPluginId {

	@Test
	@UnitTestField(target = GroupsPluginId.class, name = "PLUGIN_ID")
	public void testPluginId() {
		assertNotNull(GroupsPluginId.PLUGIN_ID);
	}
}
