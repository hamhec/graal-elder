package fr.lirmm.graphik.graal.elder.core;

import org.junit.Assert;
import org.junit.Test;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.io.ParseException;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.defeasible.core.io.DlgpDefeasibleParser;

public class RuleApplicationTest {
	@Test
	public void shouldTheSameRuleApplicationBeEqual() throws ParseException, AtomSetException {
		Rule r1 = DlgpDefeasibleParser.parseRule("p(X) <- q(X).");
		Atom a = DlgpDefeasibleParser.parseAtom("p(a).");
		AtomSet body = new LinkedListAtomSet();
		body.add(DlgpDefeasibleParser.parseAtom("q(a)."));
		RuleApplication ra1 = new RuleApplication(r1, body, a);
		RuleApplication ra2 = new RuleApplication(r1, body, a);
		Assert.assertEquals(ra1, ra2);
	}
	
	@Test
	public void shouldReturnTheSameHashForTheSameRuleApplication() throws ParseException, AtomSetException {
		Rule r1 = DlgpDefeasibleParser.parseRule("p(X) <- q(X).");
		Atom a = DlgpDefeasibleParser.parseAtom("p(a).");
		AtomSet body = new LinkedListAtomSet();
		body.add(DlgpDefeasibleParser.parseAtom("q(a)."));
		RuleApplication ra1 = new RuleApplication(r1, body, a);
		RuleApplication ra2 = new RuleApplication(r1, body, a);
		Assert.assertEquals(ra1.hashCode(), ra2.hashCode());
	}
	
	@Test
	public void shouldReturnTheDifferentHashForTheSameRuleApplicationWithDifferentRuleTypes() throws ParseException, AtomSetException {
		Rule r1 = DlgpDefeasibleParser.parseDefeasibleRule("p(X) <= q(X).");
		Rule r2 = DlgpDefeasibleParser.parseRule("p(X) <- q(X).");
		Atom a = DlgpDefeasibleParser.parseAtom("p(a).");
		AtomSet body = new LinkedListAtomSet();
		body.add(DlgpDefeasibleParser.parseAtom("q(a)."));
		RuleApplication ra1 = new RuleApplication(r1, body, a);
		RuleApplication ra2 = new RuleApplication(r2, body, a);
		Assert.assertNotEquals(ra1.hashCode(), ra2.hashCode());
	}
	
	@Test
	public void shouldBeSameIfSameRuleDifferentBodyHeadOrder() throws ParseException, AtomSetException {
		Rule r1 = DlgpDefeasibleParser.parseRule("p(X) <- q(X), t(X).");
		Atom a = DlgpDefeasibleParser.parseAtom("p(a).");
		AtomSet body1 = new LinkedListAtomSet();
		body1.add(DlgpDefeasibleParser.parseAtom("q(a)."));
		body1.add(DlgpDefeasibleParser.parseAtom("t(a)."));
		
		Rule r2 = DlgpDefeasibleParser.parseRule("p(X) <- t(X), q(X).");
		AtomSet body2 = new LinkedListAtomSet();
		body2.add(DlgpDefeasibleParser.parseAtom("t(a)."));
		body2.add(DlgpDefeasibleParser.parseAtom("q(a)."));
		
		RuleApplication ra1 = new RuleApplication(r1, body1, a);
		RuleApplication ra2 = new RuleApplication(r2, body2, a);
		
		Assert.assertEquals(ra1, ra2);
	}
}
