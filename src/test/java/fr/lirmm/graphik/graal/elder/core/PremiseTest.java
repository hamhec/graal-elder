package fr.lirmm.graphik.graal.elder.core;

import org.junit.Assert;
import org.junit.Test;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.io.ParseException;
import fr.lirmm.graphik.graal.defeasible.core.io.DlgpDefeasibleParser;

public class PremiseTest {
	@Test
	public void shouldTheSamePremiseBeEqual() throws ParseException {
		Atom a = DlgpDefeasibleParser.parseAtom("p(a).");
		Atom a2 = DlgpDefeasibleParser.parseAtom("p(a).");
		
		Premise prem1 = new Premise(a.toString());
		Premise prem2 = new Premise(a2.toString());
		Assert.assertEquals(prem1, prem2);
	}
	
	@Test
	public void shouldReturnTheSameHashForPremisesForTheSameAtom() throws ParseException {
		Atom a = DlgpDefeasibleParser.parseAtom("p(a).");
		Atom a2 = DlgpDefeasibleParser.parseAtom("p(a).");
		
		Premise prem1 = new Premise(a.toString());
		Premise prem2 = new Premise(a2.toString());
		Assert.assertEquals(prem1.hashCode(), prem2.hashCode());
	}
}
