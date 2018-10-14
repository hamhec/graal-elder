package fr.lirmm.graphik.graal.elder.core;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.defeasible.core.DefeasibleKnowledgeBase;
import fr.lirmm.graphik.util.stream.IteratorException;

public class StatementGraphPlayground {
	
	private static StatementGraph sg;
	
	@BeforeClass
	public static void setup() throws IteratorException, ChaseException, AtomSetException, HomomorphismException {
		DefeasibleKnowledgeBase kb = new DefeasibleKnowledgeBase();
		kb.add("X > Y <- reduceArtificialFertil(X), notReduceArtificialFertil(Y).");
		kb.add("X > Y <- mandatory(X), notMandatory(Y).");
		kb.add("X > Y <- easyImplementation(X), notEasyImplementation(Y).");
		kb.add("mandatory(distillery), notMandatory(distillery), notReduceArtificialFertil(distillery) <= .");
		kb.add("reduceArtificialFertil(fertilization), notMandatory(fertilization) <= .");
		kb.add("! :- mandatory(X), notMandatory(X).");
		kb.add("! :- reduceArtificialFertil(X), notReduceArtificialFertil(X).");
		kb.add("! :- easyImplementation(X), notEasyImplementation(X).");
		
		sg = new StatementGraph(kb);
		sg.build();
	}
	
	@Test
	public void shouldJSONTheShitOutOfIt() throws AtomSetException, IteratorException, ChaseException, HomomorphismException {	
		System.out.println(sg.toJSON());
		Assert.assertTrue(true);
	}
	
}
