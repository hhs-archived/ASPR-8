package plugins.groups.events;

import org.junit.jupiter.api.Test;

import plugins.groups.support.GroupId;
import tools.annotations.UnitTestConstructor;
import tools.annotations.UnitTestMethod;

public class AT_GroupImminentRemovalEvent {

	@Test
	@UnitTestConstructor(target = GroupImminentRemovalEvent.class, args = { GroupId.class })
	public void testConstructor() {
		// nothing to test
	}

	@Test
	@UnitTestMethod(target = GroupImminentRemovalEvent.class, name = "equals", args = { Object.class })
	public void testEquals() {
		// nothing to test
	}

	@Test
	@UnitTestMethod(target = GroupImminentRemovalEvent.class, name = "toString", args = {})
	public void testToString() {
		// nothing to test
	}
	
	@Test
	@UnitTestMethod(target = GroupImminentRemovalEvent.class, name = "groupId", args = {})
	public void testGroupId() {
		// nothing to test
	}
	

	@Test
	@UnitTestMethod(target = GroupImminentRemovalEvent.class, name = "hashCode", args = {})
	public void testHashCode() {
		// nothing to test
	}
}
