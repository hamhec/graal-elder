package fr.lirmm.graphik.graal.elder.core;

import org.junit.Assert;
import org.junit.Test;

import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.RuleSet;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.defeasible.core.DefeasibleKnowledgeBase;
import fr.lirmm.graphik.graal.defeasible.core.DefeasibleKnowledgeBaseCollection;
import fr.lirmm.graphik.graal.elder.persistance.StatementJSONRepresentation;
import fr.lirmm.graphik.util.stream.IteratorException;

public class AuthorshipTest {
	@Test
	public void shouldGiveAllGeneratedStatementSingleAuthorship() throws AtomSetException, IteratorException, ChaseException, HomomorphismException {
		DefeasibleKnowledgeBase kb = new DefeasibleKnowledgeBase("Test");
		kb.add("p(a). q(X) <= p(X). q(X) <- p(X). q(X) <~ p(X).");
		
		StatementGraph sg = new StatementGraph(kb);
		sg.build();
		
		for(Statement s: sg.getAllStatements()) {
			if(!s.getAuthors().contains("Test") && !s.isTopStatement()) {
				Assert.fail(s.toString() + " Has not the correct authorship!");
			}
		}
	}
	
	
	@Test
	public void shouldGiveAllGeneratedStatementMultipleAuthorship1() throws AtomSetException, IteratorException, ChaseException, HomomorphismException {
		DefeasibleKnowledgeBase kb1 = new DefeasibleKnowledgeBase("Raouf");
		DefeasibleKnowledgeBase kb2 = new DefeasibleKnowledgeBase("Pierre");
		DefeasibleKnowledgeBaseCollection kbs = new DefeasibleKnowledgeBaseCollection();
		kbs.add(kb1);
		kbs.add(kb2);
		
		kb1.add("p(a) <= . q(a).");
		kb2.add("t(X) <= p(X), q(X).");
		
		StatementGraph sg = new StatementGraph(kbs);
		sg.build();
		sg.groundQuery("t(a).");
		
		Statement q = sg.statementsOfQueries.values().iterator().next();
		
		Assert.assertTrue(q.getAuthors().contains("Raouf"));
		Assert.assertTrue(q.getAuthors().contains("Pierre"));
	}
	
	@Test
	public void shouldGiveAllGeneratedStatementMultipleAuthorship2() throws AtomSetException, IteratorException, ChaseException, HomomorphismException {
		DefeasibleKnowledgeBase kb1 = new DefeasibleKnowledgeBase("Raouf");
		DefeasibleKnowledgeBase kb2 = new DefeasibleKnowledgeBase("Pierre");
		DefeasibleKnowledgeBase kb3 = new DefeasibleKnowledgeBase("Bruno");
		
		DefeasibleKnowledgeBaseCollection kbs = new DefeasibleKnowledgeBaseCollection();
		kbs.add(kb1);
		kbs.add(kb2);
		kbs.add(kb3);
		
		kb1.add("p(a) <= . q(a).");
		kb2.add("t(X) <= p(X), q(X).");
		kb3.add("t(X) <= p(X), q(X).");
		
		StatementGraph sg = new StatementGraph(kbs);
		sg.build();
		sg.groundQuery("t(a).");
		
		Statement q = sg.statementsOfQueries.values().iterator().next();
		Assert.assertTrue(q.getAuthors().contains("Raouf"));
		Assert.assertTrue(q.getAuthors().contains("Pierre"));
		Assert.assertTrue(q.getAuthors().contains("Bruno"));
	}
	
	@Test
	public void shouldGiveAllGeneratedStatementMultipleAuthorship3() throws AtomSetException, IteratorException, ChaseException, HomomorphismException {
		DefeasibleKnowledgeBase kb1 = new DefeasibleKnowledgeBase("Raouf");
		DefeasibleKnowledgeBase kb2 = new DefeasibleKnowledgeBase("Pierre");
		DefeasibleKnowledgeBase kb3 = new DefeasibleKnowledgeBase("Bruno");
		
		DefeasibleKnowledgeBaseCollection kbs = new DefeasibleKnowledgeBaseCollection();
		kbs.add(kb1);
		kbs.add(kb2);
		kbs.add(kb3);
		
		kb1.add("p(a) <= . q(a).");
		kb2.add("p(a) <= . q(a).");
		kb3.add("t(X) <= p(X), q(X).");
		
		StatementGraph sg = new StatementGraph(kbs);
		sg.build();
		sg.groundQuery("t(a).");
		
		Statement q = sg.statementsOfQueries.values().iterator().next();
		Assert.assertTrue(q.getAuthors().contains("Raouf"));
		Assert.assertTrue(q.getAuthors().contains("Pierre"));
		Assert.assertTrue(q.getAuthors().contains("Bruno"));
	}
	
	@Test
	public void shouldGiveAllGeneratedStatementMultipleAuthorship4() throws AtomSetException, IteratorException, ChaseException, HomomorphismException {
		DefeasibleKnowledgeBase kb1 = new DefeasibleKnowledgeBase("Raouf");
		DefeasibleKnowledgeBase kb2 = new DefeasibleKnowledgeBase("Pierre");
		DefeasibleKnowledgeBase kb3 = new DefeasibleKnowledgeBase("Bruno");
		
		DefeasibleKnowledgeBaseCollection kbs = new DefeasibleKnowledgeBaseCollection();
		kbs.add(kb1);
		kbs.add(kb2);
		kbs.add(kb3);
		
		kb1.add("p(a) <= . q(a).");
		kb2.add("p(b).");
		kb3.add("t(X) <= p(X), q(X).");
		
		StatementGraph sg = new StatementGraph(kbs);
		sg.build();
		sg.groundQuery("t(a).");
		
		Statement q = sg.statementsOfQueries.values().iterator().next();
		Assert.assertTrue(q.getAuthors().contains("Raouf"));
		Assert.assertFalse(q.getAuthors().contains("Pierre"));
		Assert.assertTrue(q.getAuthors().contains("Bruno"));
	}
	
	@Test
	public void shouldNotPropagateAuthorshipIfRuleIsDefeater() throws AtomSetException, IteratorException, ChaseException, HomomorphismException {
		DefeasibleKnowledgeBase kb = new DefeasibleKnowledgeBase("Test");
		kb.add("p(a). q(X) <~ p(X).");
		
		StatementGraph sg = new StatementGraph(kb);
		sg.build();
		sg.groundQuery("q(a).");
		Statement q = sg.statementsOfQueries.values().iterator().next();
		System.out.println(q.getAuthors());
		Assert.assertFalse(q.getAuthors().contains("Test"));
	}
	
	@Test
	public void shouldAddAuthorsToStatementJSONRepresentation() throws AtomSetException, IteratorException, ChaseException, HomomorphismException {
		DefeasibleKnowledgeBase kb1 = new DefeasibleKnowledgeBase("Raouf");
		DefeasibleKnowledgeBase kb2 = new DefeasibleKnowledgeBase("Pierre");
		DefeasibleKnowledgeBaseCollection kbs = new DefeasibleKnowledgeBaseCollection();
		kbs.add(kb1);
		kbs.add(kb2);
		
		kb1.add("p(a) <= . q(a).");
		kb2.add("t(X) <= p(X), q(X).");
		
		StatementGraph sg = new StatementGraph(kbs);
		sg.build();
		sg.groundQuery("t(a).");
		
		Statement q = sg.statementsOfQueries.values().iterator().next();
		StatementJSONRepresentation rep = q.getRepresentation();
		Assert.assertTrue(rep.getAuthors().contains("Raouf"));
		Assert.assertTrue(rep.getAuthors().contains("Pierre"));
	}
}	
