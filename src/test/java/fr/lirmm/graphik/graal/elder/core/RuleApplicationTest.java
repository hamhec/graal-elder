package fr.lirmm.graphik.graal.elder.core;

import org.junit.Assert;
import org.junit.Test;

import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.io.ParseException;
import fr.lirmm.graphik.graal.defeasible.core.io.DlgpDefeasibleParser;

public class RuleApplicationTest {
	@Test
	public void shouldTheSameRuleApplicationBeEqual() throws ParseException {
		Rule r1 = DlgpDefeasibleParser.parseRule("p(X) <- q(X).");
		
		RuleApplication ra1 = new RuleApplication(r1, null, null);
		RuleApplication ra2 = new RuleApplication(r1, null, null);
		Assert.assertEquals(ra1, ra2);
	}
	
	@Test
	public void shouldReturnTheSameHashForTheSameRuleApplication() throws ParseException {
		Rule r1 = DlgpDefeasibleParser.parseRule("p(X) <- q(X).");
		
		RuleApplication ra1 = new RuleApplication(r1, null, null);
		RuleApplication ra2 = new RuleApplication(r1, null, null);
		Assert.assertEquals(ra1.hashCode(), ra2.hashCode());
	}
	
	@Test
	public void shouldReturnTheDifferentHashForTheSameRuleApplicationWithDifferentRuleTypes() throws ParseException {
		Rule r1 = DlgpDefeasibleParser.parseDefeasibleRule("p(X) <= q(X).");
		Rule r2 = DlgpDefeasibleParser.parseRule("p(X) <- q(X).");
		
		RuleApplication ra1 = new RuleApplication(r1, null, null);
		RuleApplication ra2 = new RuleApplication(r2, null, null);
		Assert.assertNotEquals(ra1.hashCode(), ra2.hashCode());
	}
}
