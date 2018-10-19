package fr.lirmm.graphik.graal.elder.preference;

import org.junit.Test;
import org.junit.Assert;

import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.defeasible.core.DefeasibleKnowledgeBase;
import fr.lirmm.graphik.graal.elder.core.StatementGraph;
import fr.lirmm.graphik.graal.elder.labeling.Labels;
import fr.lirmm.graphik.util.stream.IteratorException;


public class AlternativePreferencesTest {
	@Test
	public void shouldHandleQueriesOnAlternativePreferences() throws AtomSetException, IteratorException, ChaseException, HomomorphismException {
		DefeasibleKnowledgeBase kb = new DefeasibleKnowledgeBase();
		kb.add("cool(pierre), lame(raouf).");
		kb.addPreferenceRule("X > Y <- cool(X), lame(Y).");
		StatementGraph sg = new StatementGraph(kb);
		sg.build();
		String label = sg.groundQuery("pierre > raouf .");
		Assert.assertEquals(Labels.STRICT_IN, label);
	}
	
	@Test
	public void shouldGenerateAttacksBetweenAlternativePreferences() throws IteratorException, ChaseException, AtomSetException, HomomorphismException {
		DefeasibleKnowledgeBase kb = new DefeasibleKnowledgeBase();
		kb.add("cool(pierre), lame(raouf) <= .");
		kb.add("handsome(raouf), ugly(pierre) <= .");
		
		kb.addPreferenceRule("X > Y <- cool(X), lame(Y).");
		kb.addPreferenceRule("X > Y <- handsome(X), ugly(Y).");
		
		StatementGraph sg = new StatementGraph(kb);
		sg.build();
		String label = sg.groundQuery("raouf > pierre .");
		Assert.assertEquals(Labels.AMBIGUOUS, label);
	}
	
	@Test
	public void shouldGenerateAttacksBetweenAlternativePreferencesAndCorrectlyLabelStrictOverDefeasible() throws IteratorException, ChaseException, AtomSetException, HomomorphismException {
		DefeasibleKnowledgeBase kb = new DefeasibleKnowledgeBase();
		kb.add("cool(pierre), lame(raouf) <- .");
		kb.add("handsome(raouf), ugly(pierre) <= .");
		
		kb.addPreferenceRule("X > Y <- cool(X), lame(Y).");
		kb.addPreferenceRule("X > Y <- handsome(X), ugly(Y).");
		
		StatementGraph sg = new StatementGraph(kb);
		sg.build();
		String label = sg.groundQuery("raouf > pierre .");
		Assert.assertEquals(Labels.STRICT_OUT, label);
	}
	
	
}
