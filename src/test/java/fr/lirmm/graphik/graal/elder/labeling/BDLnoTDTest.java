package fr.lirmm.graphik.graal.elder.labeling;

import org.junit.Assert;
import org.junit.Test;

import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.elder.SetupTestClass;
import fr.lirmm.graphik.util.stream.IteratorException;

public class BDLnoTDTest extends SetupTestClass {
	@Test
	public void shouldLabelTopStatementStrictIN() {
		Assert.assertEquals(Labels.STRICT_IN, sg.getTOPStatement().getLabel());
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
	
	// ------------------------------------------------------------------------
	// 	AMBIGUITY BEHAVIOR TEST
	// ------------------------------------------------------------------------
	@Test
	public void shouldLabelAttackedByAmgbiguousIN() throws IteratorException, AtomSetException {
		String query = "blocking(a).";
		String label = sg.groundQuery(query);
		
		Assert.assertEquals(Labels.DEFEASIBLE_IN, label);
	}
	@Test
	public void shouldLabelSupportedByAmgbiguousAmbiguous() throws IteratorException, AtomSetException {
		String query = "nblocking(a).";
		String label = sg.groundQuery(query);
		
		Assert.assertEquals(Labels.AMBIGUOUS, label);
	}
	
	@Test
	public void shouldLabelKilledRulesOUT() throws IteratorException, AtomSetException {
		String query = "deadRule(a).";
		String label = sg.groundQuery(query);
		
		Assert.assertEquals(Labels.DEFEASIBLE_OUT, label);
	}
	@Test
	public void shouldLabelSurvivingRulesIN() throws IteratorException, AtomSetException {
		String query = "survivingRule(a).";
		String label = sg.groundQuery(query);
		
		Assert.assertEquals(Labels.DEFEASIBLE_IN, label);
	}
	
	// ------------------------------------------------------------------------
	// 	ATOMIC GROUND QUERIES
	// ------------------------------------------------------------------------
	@Test
	public void shouldLabelStrictFactStrictIN() throws IteratorException, AtomSetException {
		String query = "str1(a).";
		String label = sg.groundQuery(query);
		
		Assert.assertEquals(Labels.STRICT_IN, label);
	}
	
	@Test
	public void shouldLabelStrictDerivableAtomStrictIN() throws IteratorException, AtomSetException {
		String query = "str3(a).";
		String label = sg.groundQuery(query);
		
		Assert.assertEquals(Labels.STRICT_IN, label);
	}
	
	@Test
	public void shouldLabelStrictAttackedAtomStrictIN() throws IteratorException, AtomSetException {
		String query = "str2(a).";
		String label = sg.groundQuery(query);
		
		Assert.assertEquals(Labels.STRICT_IN, label);
	}
	
	@Test

	public void shouldLabelUnattackedDefeasibleFactDefeasibleIN() throws IteratorException, AtomSetException {
		String query = "def1(a).";
		String label = sg.groundQuery(query);
		
		Assert.assertEquals(Labels.DEFEASIBLE_IN, label);
	}
	
	@Test
	public void shouldLabelUnattackedDerivableDefeasibleAtomDefeasibleIN() throws IteratorException, AtomSetException {
		String query = "def2(a).";
		String label = sg.groundQuery(query);
		
		Assert.assertEquals(Labels.DEFEASIBLE_IN, label);
	}
	
	@Test
	public void shouldLabelDefeasiblyCountredDefeasibleAtomAmbiguous() throws IteratorException, AtomSetException {
		String query = "ambig(a).";
		String label = sg.groundQuery(query);
		
		Assert.assertEquals(Labels.AMBIGUOUS, label);
	}
	
	@Test
	public void shouldLabelStrictlyKilledDefeasibleAtomOUT() throws IteratorException, AtomSetException {
		String query = "nstr2(a).";
		String label = sg.groundQuery(query);
		
		Assert.assertEquals(Labels.STRICT_OUT, label);
	}
	
	@Test
	public void shouldLabelOUTIfNoSupportIsPresentForAtom() {
		//TODO
	}
	
	// ------------------------------------------------------------------------
	// 	CONJUNCTIVE GROUND QUERIES
	// ------------------------------------------------------------------------
	@Test
	public void shouldLabelConjunctiveQueryOnStrictFactsStrictIN() throws IteratorException, AtomSetException {
		String query = "str1(a), str0(a).";
		String label = sg.groundQuery(query);
		
		Assert.assertEquals(Labels.STRICT_IN, label);
	}
	
	@Test
	public void shouldLabelConjunctiveQueryOnDefeasibleFactsDefeasibleIN() throws IteratorException, AtomSetException {
		String query = "def1(a), def0(a).";
		String label = sg.groundQuery(query);
		
		Assert.assertEquals(Labels.DEFEASIBLE_IN, label);
	}
	
	@Test
	public void shouldLabelConjunctiveQueryOnDefeasibleAndStrictFactsDefeasibleIN() throws IteratorException, AtomSetException {
		String query = "str0(a), def0(a).";
		String label = sg.groundQuery(query);
		
		Assert.assertEquals(Labels.DEFEASIBLE_IN, label);
	}
	
	@Test
	public void shouldLabelConjunctiveQueryOnAmbiguousAndStrictInAmbiguous() throws IteratorException, AtomSetException {
		String query = "ambig(a), str1(a).";
		String label = sg.groundQuery(query);
		
		Assert.assertEquals(Labels.AMBIGUOUS, label);
	}
	
	@Test
	public void shouldLabelConjunctiveQueryOnAmbiguousAndDefeasibleInAmbiguous() throws IteratorException, AtomSetException {
		String query = "ambig(a), def1(a).";
		String label = sg.groundQuery(query);
		
		Assert.assertEquals(Labels.AMBIGUOUS, label);
	}
	
	@Test
	public void shouldLabelConjunctiveQueryOnOUTAndOtherOUT() throws IteratorException, AtomSetException {
		String query = "ambig(a), def1(a), str1(a), nstr2(a).";
		String label = sg.groundQuery(query);
		
		Assert.assertEquals(Labels.DEFEASIBLE_OUT, label);
	}
	
	@Test
	public void shouldLabelOUTIfNoSupportIsPresentForAtomInConjunction() {
		//TODO
	}
}
