package gov.hhs.aspr.ms.gcm.simulation.plugins.people.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.math3.random.RandomGenerator;
import org.junit.jupiter.api.Test;

import gov.hhs.aspr.ms.util.annotations.UnitTestConstructor;
import gov.hhs.aspr.ms.util.annotations.UnitTestMethod;
import gov.hhs.aspr.ms.util.errors.ContractException;
import gov.hhs.aspr.ms.util.random.RandomGeneratorProvider;

public class AT_PersonRange {

	@Test
	@UnitTestConstructor(target = PersonRange.class, args = { int.class, int.class })
	public void testPersonRange() {

		// precondition test: illegal person range
		ContractException illegalContractException = assertThrows(ContractException.class,
				() -> new PersonRange(10, 7));
		assertEquals(PersonError.ILLEGAL_PERSON_RANGE, illegalContractException.getErrorType());

		// precondition test: negative person if
		ContractException negativeContractException = assertThrows(ContractException.class,
				() -> new PersonRange(-5, 20));
		assertEquals(PersonError.NEGATIVE_PERSON_ID, negativeContractException.getErrorType());
	}

	@Test
	@UnitTestMethod(target = PersonRange.class, name = "getLowPersonId", args = {})
	public void testGetLowPersonId() {
		RandomGenerator randomGenerator = RandomGeneratorProvider.getRandomGenerator(7817347436220081509L);
		for (int i = 0; i < 10; i++) {
			int lowId = randomGenerator.nextInt(50);
			int highId = lowId + 1 + randomGenerator.nextInt(50);
			PersonRange personRange = new PersonRange(lowId, highId);
			int actualId = personRange.getLowPersonId();
			assertEquals(lowId, actualId);
		}
	}

	@Test
	@UnitTestMethod(target = PersonRange.class, name = "getHighPersonId", args = {})
	public void testGetHighId() {
		RandomGenerator randomGenerator = RandomGeneratorProvider.getRandomGenerator(2964674930895304415L);
		for (int i = 0; i < 10; i++) {
			int lowId = randomGenerator.nextInt(50);
			int highId = lowId + 1 + randomGenerator.nextInt(50);
			PersonRange personRange = new PersonRange(lowId, highId);
			int actualId = personRange.getHighPersonId();
			assertEquals(highId, actualId);
		}
	}

	@Test
	@UnitTestMethod(target = PersonRange.class, name = "compareTo", args = { PersonRange.class })
	public void testCompareTo() {
		RandomGenerator randomGenerator = RandomGeneratorProvider.getRandomGenerator(2964674930895304415L);
		for (int i = 0; i < 30; i++) {
			int lowId = randomGenerator.nextInt(5);
			int highId = lowId + 1 + randomGenerator.nextInt(50);
			int lowId2 = randomGenerator.nextInt(5);
			int highId2 = lowId + 1 + randomGenerator.nextInt(50);
			PersonRange personRange = new PersonRange(lowId, highId);
			PersonRange personRange2 = new PersonRange(lowId2, highId2);
			if (lowId == lowId2) {
				if (highId == highId2) {
					assertEquals(0, personRange.compareTo(personRange2));
				} else if (highId < highId2) {
					assertTrue(personRange.compareTo(personRange2) < 0);
				} else {
					assertTrue(personRange.compareTo(personRange2) > 0);
				}
			} else if (lowId < lowId2) {
				assertTrue(personRange.compareTo(personRange2) < 0);
			} else {
				assertTrue(personRange.compareTo(personRange2) > 0);
			}
		}
	}

	@Test
	@UnitTestMethod(target = PersonRange.class, name = "hashCode", args = {})
	public void testHashCode() {
		RandomGenerator randomGenerator = RandomGeneratorProvider.getRandomGenerator(8720935725310593369L);
		// show that equal person ranges have equal hashcodes
		for (int i = 0; i < 10; i++) {
			long seed = randomGenerator.nextLong();			
			PersonRange personRange = getRandomPersonRange(seed);
			PersonRange duplicatePersonRange = getRandomPersonRange(seed);		
			assertEquals(personRange, duplicatePersonRange);
			assertEquals(personRange.hashCode(), duplicatePersonRange.hashCode());
		}

		// show that hash codes are reasonably distributed
		Set<Integer> hashCodes = new LinkedHashSet<>();
		for (int i = 0; i < 100; i++) {
			PersonRange personRange = getRandomPersonRange(randomGenerator.nextLong());
			hashCodes.add(personRange.hashCode());
		}
		assertEquals(100, hashCodes.size());
	}

	private PersonRange getRandomPersonRange(long seed) {
		RandomGenerator randomGenerator = RandomGeneratorProvider.getRandomGenerator(seed);
		int lowId = randomGenerator.nextInt(1000);
		int highId = lowId + 1 + randomGenerator.nextInt(1000);
		return new PersonRange(lowId, highId);
	}

	@Test
	@UnitTestMethod(target = PersonRange.class, name = "equals", args = { Object.class })
	public void testEquals() {
		RandomGenerator randomGenerator = RandomGeneratorProvider.getRandomGenerator(4375612914105986895L);
		// show that not equal to null
		for (int i = 0; i < 30; i++) {
			PersonRange personRange = getRandomPersonRange(randomGenerator.nextLong());
			assertFalse(personRange.equals(null));
		}

		// show that not equal to another type
		for (int i = 0; i < 30; i++) {
			PersonRange personRange = getRandomPersonRange(randomGenerator.nextLong());
			assertFalse(personRange.equals(new Object()));
		}

		// reflexive
		for (int i = 0; i < 30; i++) {
			PersonRange personRange = getRandomPersonRange(randomGenerator.nextLong());
			assertTrue(personRange.equals(personRange));
		}

		// symmetric, transitive, consistent
		for (int i = 0; i < 30; i++) {
			long seed = randomGenerator.nextLong();
			PersonRange personRange = getRandomPersonRange(seed);
			PersonRange duplicatePersonRange = getRandomPersonRange(seed);
			assertFalse(personRange == duplicatePersonRange);
			for (int j = 0; j < 10; j++) {		
				assertTrue(personRange.equals(duplicatePersonRange));
				assertTrue(duplicatePersonRange.equals(personRange));
			}
		}

		// different inputs yield unequal person ranges
		Set<PersonRange> set = new LinkedHashSet<>();
		for (int i = 0; i < 100; i++) {
			PersonRange personRange = getRandomPersonRange(randomGenerator.nextLong());
			set.add(personRange);
		}
		assertEquals(100, set.size());
	}

	@Test
	@UnitTestMethod(target = PersonRange.class, name = "toString", args = {})
	public void testToString() {
		assertEquals(new PersonRange(2, 100).toString(), "PersonRange [lowPersonId=2, highPersonId=100]");
		assertEquals(new PersonRange(4, 10).toString(), "PersonRange [lowPersonId=4, highPersonId=10]");
		assertEquals(new PersonRange(5, 5).toString(), "PersonRange [lowPersonId=5, highPersonId=5]");
		assertEquals(new PersonRange(12, 19).toString(), "PersonRange [lowPersonId=12, highPersonId=19]");
	}
}
