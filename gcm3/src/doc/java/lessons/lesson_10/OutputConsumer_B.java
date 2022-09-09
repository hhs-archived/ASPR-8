package lessons.lesson_10;

import java.util.function.Consumer;

import nucleus.ExperimentContext;

 public class OutputConsumer_B implements Consumer<ExperimentContext>{
	
	@Override
	public void accept(ExperimentContext experimentContext) {
		experimentContext.subscribeToOutput(Object.class, this::handleOutput);
	}		

	private void handleOutput(ExperimentContext experimentContext, Integer scenarioId, Object output) {
		System.out.println("scenario " + scenarioId + ": " + output);
	}
 }

 
 

