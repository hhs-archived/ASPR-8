package plugins.people.events;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import plugins.people.support.PersonError;
import plugins.people.support.PersonId;
import tools.annotations.UnitTest;
import tools.annotations.UnitTestConstructor;
import util.errors.ContractException;

@UnitTest(target = PersonImminentRemovalEvent.class)
public class AT_PersonImminentRemovalEvent {

	@Test
	@UnitTestConstructor(args = { PersonId.class })
	public void testConstructor() {

		// precondition tests
		ContractException contractException = assertThrows(ContractException.class, () -> new PersonImminentRemovalEvent(null));
		assertEquals(PersonError.NULL_PERSON_ID, contractException.getErrorType());

	}
}
