package fr.lirmm.graphik.graal.elder.core;

import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.api.io.ParseException;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.defeasible.core.DefeasibleKnowledgeBase;
import fr.lirmm.graphik.graal.defeasible.core.io.DlgpDefeasibleParser;
import fr.lirmm.graphik.util.stream.IteratorException;

public class RedundancyTest {
	
	@Test
	public void shouldStatementsBeEqual() throws ParseException, AtomSetException {
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
		
		List<Premise> premises1 = new LinkedList<Premise>();
		premises1.add(new Premise("q(a)"));
		premises1.add(new Premise("t(a)"));
		
		List<Premise> premises2 = new LinkedList<Premise>();
		premises2.add(new Premise("t(a)"));
		premises2.add(new Premise("q(a)"));
		
		Statement s1 = new Statement(ra1, premises1);
		Statement s2 = new Statement(ra2, premises2);
		
		Assert.assertTrue(s1.equals(s2) && s1.hashCode() == s2.hashCode());
	}
	
	@Test
	public void shouldGiveLabelToAllStatements() throws IteratorException, ChaseException, AtomSetException, HomomorphismException {
		DefeasibleKnowledgeBase kb = new DefeasibleKnowledgeBase();		
		kb.add(" cheap(indian)<= .\n" + 
				" expensive(entrecote) <= .\n");
		
		kb.add(" X > Y <- cheap(X), expensive(Y).\n" + 
				" cheap(indian), expensive(entrecote) <= .");
		
		StatementGraph sg = new StatementGraph(kb);
		sg.build();
		sg.groundQuery("indian > entrecote.");
		
		for(Statement s: sg.getAllStatements()) {
			if(s.getLabel() == null || s.getLabel().equals("") || s.getLabel().isEmpty()) {
				Assert.assertTrue(false);
				return;
			}
		}
		Assert.assertTrue(true);
		
	}
	
}
