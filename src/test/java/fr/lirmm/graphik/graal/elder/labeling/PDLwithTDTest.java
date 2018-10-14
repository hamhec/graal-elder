package fr.lirmm.graphik.graal.elder.labeling;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.elder.core.StatementGraph;
import fr.lirmm.graphik.graal.elder.labeling.defeasible.logic.PDLwithTD;
import fr.lirmm.graphik.util.stream.IteratorException;

public class PDLwithTDTest extends KnowledgeBaseForLabelingTesting {
	
	public static StatementGraph sg;
	
	@BeforeClass
	public static void setupSG() throws IteratorException, ChaseException, AtomSetException, HomomorphismException {
		sg = new StatementGraph(kb, new PDLwithTD(kb.getRulePreferences()));
		sg.build();
	}
	
	// ------------------------------------------------------------------------
	// 	GENERAL BEHAVIOR TEST
	// ------------------------------------------------------------------------
	@Test
	public void shouldLabelTopStatementStrictIN() {
		Assert.assertEquals(Labels.STRICT_IN, sg.getTOPStatement().getLabel());
	}
	
	@Test
	public void shouldLabelSupportedByAmgbiguousAmbiguous() throws IteratorException, AtomSetException {
		String query = "sentenced(alice).";
		String label = sg.groundQuery(query);
		
		Assert.assertEquals(Labels.AMBIGUOUS, label);
	}
	
	@Test
	public void shouldLabelConclusionOfInferiorRuleOUT() throws IteratorException, AtomSetException {
		String query = "fly(kowalski).";
		String label = sg.groundQuery(query);
		
		Assert.assertEquals(Labels.DEFEASIBLE_OUT, label);
	}
	
	@Test
	public void shouldLabelConclusionOfInferiorRuleKilledByDefeaterUnsupported() throws IteratorException, AtomSetException {
		String query = "fly(tweety).";
		String label = sg.groundQuery(query);
		
		Assert.assertEquals(Labels.ASSUMED_OUT, label);
	}
	
	@Test
	public void shouldLabelConclusionOfSuperiorRuleIN() throws IteratorException, AtomSetException {
		String query = "notFly(kowalski).";
		String label = sg.groundQuery(query);
		
		Assert.assertEquals(Labels.DEFEASIBLE_IN, label);
	}
	
	@Test
	public void shouldLabelStrictFactStrictIN() throws IteratorException, AtomSetException {
		String query = "penguin(kowalski).";
		String label = sg.groundQuery(query);
		
		Assert.assertEquals(Labels.STRICT_IN, label);
	}
	
	@Test
	public void shouldLabelStrictDerivableAtomStrictIN() throws IteratorException, AtomSetException {
		String query = "beautiful(kowalski).";
		String label = sg.groundQuery(query);
		
		Assert.assertEquals(Labels.STRICT_IN, label);
	}
	
	@Test
	public void shouldLabelUnattackedDefeasibleFactDefeasibleIN() throws IteratorException, AtomSetException {
		String query = "testimony(raouf,incriminating,alice).";
		String label = sg.groundQuery(query);
		
		Assert.assertEquals(Labels.DEFEASIBLE_IN, label);
	}
	
	@Test
	public void shouldLabelUnattackedDerivableDefeasibleAtomDefeasibleIN() throws IteratorException, AtomSetException {
		String query = "bird(kowalski).";
		String label = sg.groundQuery(query);
		
		Assert.assertEquals(Labels.DEFEASIBLE_IN, label);
	}
	
	@Test
	public void shouldLabelStrictlyKilledAtomsOUT() throws IteratorException, AtomSetException {
		String query = "happy(tweety).";
		String label = sg.groundQuery(query);
		
		Assert.assertEquals(Labels.STRICT_OUT, label);
	}
	
	@Test
	public void shouldLabelOUTIfNoSupportIsPresentForAtom() throws IteratorException, AtomSetException {
		String query = "something(unknown).";
		String label = sg.groundQuery(query);
		
		Assert.assertEquals(Labels.ASSUMED_OUT, label);
	}
	
	@Test
	public void shouldLabelConjunctiveQueryOnStrictFactsStrictIN() throws IteratorException, AtomSetException {
		String query = "bird(tweety), brokenWings(tweety).";
		String label = sg.groundQuery(query);
		
		Assert.assertEquals(Labels.STRICT_IN, label);
	}
	
	@Test
	public void shouldLabelConjunctiveQueryOnDefeasibleFactsDefeasibleIN() throws IteratorException, AtomSetException {
		String query = "animal(kowalski), animal(tweety).";
		String label = sg.groundQuery(query);
		
		Assert.assertEquals(Labels.DEFEASIBLE_IN, label);
	}
	
	@Test
	public void shouldLabelConjunctiveQueryOnDefeasibleAndStrictFactsDefeasibleIN() throws IteratorException, AtomSetException {
		String query = "penguin(kowalski), animal(kowalski).";
		String label = sg.groundQuery(query);
		
		Assert.assertEquals(Labels.DEFEASIBLE_IN, label);
	}
	
	@Test
	public void shouldLabelConjunctiveQueryOnAmbiguousAndStrictInAmbiguous() throws IteratorException, AtomSetException {
		String query = "guilty(alice), penguin(kowalski).";
		String label = sg.groundQuery(query);
		
		Assert.assertEquals(Labels.AMBIGUOUS, label);
	}
	
	@Test
	public void shouldLabelConjunctiveQueryOnAmbiguousAndDefeasibleInAmbiguous() throws IteratorException, AtomSetException {
		String query = "guilty(alice), female(alice).";
		String label = sg.groundQuery(query);
		
		Assert.assertEquals(Labels.AMBIGUOUS, label);
	}
	
	@Test
	public void shouldLabelConjunctiveQueryOnOUTAndOtherOUT() throws IteratorException, AtomSetException {
		String query = "happy(tweety), bird(tweety).";
		String label = sg.groundQuery(query);
		
		Assert.assertEquals(Labels.STRICT_OUT, label);
	}
	
	@Test
	public void shouldLabelOUTIfNoSupportIsPresentForAtomInConjunction() throws IteratorException, AtomSetException {
		String query = "something(unknown), bird(tweety).";
		String label = sg.groundQuery(query);
		
		Assert.assertEquals(Labels.ASSUMED_OUT, label);
	}
	
	// ------------------------------------------------------------------------
	// 	AMBIGUITY BEHAVIOR TEST
	// ------------------------------------------------------------------------
	@Test
	public void shouldLabelAttackedByAmgbiguousAmbiguous() throws IteratorException, AtomSetException {
		String query = "innocent(alice).";
		String label = sg.groundQuery(query);
		
		Assert.assertEquals(Labels.AMBIGUOUS, label);
	}
	
	// ------------------------------------------------------------------------
	// 	TEAM DEFEAT BEHAVIOR TEST
	// ------------------------------------------------------------------------
	@Test
	public void shouldBeWithTeamDefeat() throws IteratorException, AtomSetException {
		String query = "buy(phone).";
		String label = sg.groundQuery(query);
		
		Assert.assertEquals(Labels.DEFEASIBLE_IN, label);
	}
	
	// ------------------------------------------------------------------------
	// 	AMBIGUITY WITH TEAM DEFEAT BEHAVIOR TEST
	// ------------------------------------------------------------------------
	@Test
	public void shouldBeAmbiguityPropagatingWithTeamDefeat() throws IteratorException, AtomSetException {
		String query = "go(vacation).";
		String label = sg.groundQuery(query);
		
		Assert.assertEquals(Labels.AMBIGUOUS, label);
	}	
	
	// ------------------------------------------------------------------------
	// Circular Conflict
	// ------------------------------------------------------------------------
	/*@Test
	public void shouldLoop() throws IteratorException, AtomSetException {
		String query = "p1(a).";
		String label = edg.groundQuery(query);
		
		Assert.assertEquals(Labels.DEFEASIBLE_IN, label);
	}*/

}
