package lesson.plugins.vaccine.datamanagers;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import lesson.plugins.family.datamanagers.FamilyDataManager;
import lesson.plugins.family.support.FamilyId;
import lesson.plugins.person.datamanagers.PersonDataManager;
import lesson.plugins.person.events.PersonRemovalEvent;
import lesson.plugins.person.support.PersonId;
import lesson.plugins.vaccine.events.VaccinationEvent;
import nucleus.DataManager;
import nucleus.DataManagerContext;
import nucleus.Event;

public final class VaccinationDataManager extends DataManager {

	private Set<PersonId> vaccinatedPeople = new LinkedHashSet<>();

	private PersonDataManager personDataManager;
	private FamilyDataManager familyDataManager;
	private DataManagerContext dataManagerContext;

	@Override
	public void init(DataManagerContext dataManagerContext) {
		super.init(dataManagerContext);
		dataManagerContext.subscribe(PersonRemovalEvent.class, this::handlePersonRemovalEvent);
		personDataManager = dataManagerContext.getDataManager(PersonDataManager.class);
		familyDataManager = dataManagerContext.getDataManager(FamilyDataManager.class);
		this.dataManagerContext = dataManagerContext;

		dataManagerContext.subscribe(VaccinationMutationEvent.class, this::handleVaccinationMutationEvent);

	}

	private void handlePersonRemovalEvent(DataManagerContext dataManagerContext,
			PersonRemovalEvent personRemovalEvent) {
		PersonId personId = personRemovalEvent.getPersonId();
		vaccinatedPeople.remove(personId);
	}

	public Set<PersonId> getVaccinatedPeople() {
		return new LinkedHashSet<>(vaccinatedPeople);
	}

	public Set<PersonId> getUnvaccinatedPeople() {
		Set<PersonId> people = personDataManager.getPeople();
		people.removeAll(vaccinatedPeople);
		return people;
	}

	public boolean isPersonVaccinated(PersonId personId) {
		if (!personDataManager.personExists(personId)) {
			throw new RuntimeException("unknown person " + personId);
		}
		return vaccinatedPeople.contains(personId);
	}

	private static record VaccinationMutationEvent(PersonId personId) implements Event {
	}

	public void vaccinatePerson(PersonId personId) {
		dataManagerContext.releaseMutationEvent(new VaccinationMutationEvent(personId));
	}

	private void handleVaccinationMutationEvent(DataManagerContext dataManagerContext,
			VaccinationMutationEvent vaccinationMutationEvent) {
		PersonId personId = vaccinationMutationEvent.personId();
		if (!personDataManager.personExists(personId)) {
			throw new RuntimeException("unknown person " + personId);
		}

		vaccinatedPeople.add(personId);
		dataManagerContext.releaseObservationEvent(new VaccinationEvent(personId));

	}

	public List<PersonId> getUnvaccinatedFamilyMembers(PersonId personId) {
		if (!personDataManager.personExists(personId)) {
			throw new RuntimeException("unknown person " + personId);
		}
		List<PersonId> result = new ArrayList<>();
		Optional<FamilyId> optional = familyDataManager.getFamilyId(personId);
		if (optional.isPresent()) {
			FamilyId familyId = optional.get();
			List<PersonId> familyMembers = familyDataManager.getFamilyMembers(familyId);
			for (PersonId familyMemeberId : familyMembers) {
				if (!isPersonVaccinated(familyMemeberId)) {
					result.add(personId);
				}
			}
		}
		return result;
	}

}
