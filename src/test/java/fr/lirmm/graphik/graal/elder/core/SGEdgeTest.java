package fr.lirmm.graphik.graal.elder.core;

import java.util.HashSet;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.api.io.ParseException;
import fr.lirmm.graphik.graal.defeasible.core.DefeasibleKnowledgeBase;
import fr.lirmm.graphik.graal.elder.persistance.SGEdgeJSONRepresentation;
import fr.lirmm.graphik.graal.elder.persistance.StatementGraphJSONRepresentation;
import fr.lirmm.graphik.util.stream.IteratorException;

public class SGEdgeTest {
	@Test
	public void shouldBeSameIfGeneratedByEqualRuleApplication() throws AtomSetException, IteratorException, ChaseException, HomomorphismException {
		DefeasibleKnowledgeBase kb = new DefeasibleKnowledgeBase();
		kb.add("cheap(a) <= .");
		kb.add("cheap(a) <= .");
		kb.add("beau(X) <= cheap(X).");
		
		StatementGraph sg = new StatementGraph(kb);
		sg.build();
		
		List<Statement> ss = sg.getAllStatements();
		HashSet<SGEdge> es = new HashSet<SGEdge>();
		
		for(Statement s: ss) {
			es.addAll(s.getIncomingEdges());
		}
		
		Assert.assertEquals(2, es.size());
		
	}
	
	@Test
	public void shouldHaveDifferentIDifGoingToDifferentTargetStatement() throws AtomSetException, IteratorException, ChaseException, HomomorphismException {
		DefeasibleKnowledgeBase kb = new DefeasibleKnowledgeBase();
		kb.add("penguin(a) <= .");
		kb.add("bird(X) <= penguin(X).");
		kb.add("notFly(X) <= penguin(X).");
		
		StatementGraph sg = new StatementGraph(kb);
		sg.build();
		
		StatementGraphJSONRepresentation rep = sg.getRepresentation();
		
		for(SGEdgeJSONRepresentation e: rep.getEdges()) {
			int count = -1;
			for(SGEdgeJSONRepresentation e2: rep.getEdges()) {
				if(e.getId().equals(e2.getId())) {
					count++;
					if(count > 0) {
						Assert.fail();
						return;
					}
				}
				
			}
		}
	}
	
	@Test
	public void shouldGiveDifferentHashesIfTheConstantsChangePositions() throws AtomSetException, IteratorException, ChaseException, HomomorphismException {
		DefeasibleKnowledgeBase kb = new DefeasibleKnowledgeBase();
		kb.add("pref(t,d), pref(d,t).");
		kb.add("eq(X,Y) <- pref(X,Y), pref(Y,X).");
		
		StatementGraph sg = new StatementGraph(kb);
		sg.build();
		
		StatementGraphJSONRepresentation rep = sg.getRepresentation();
		
		for(SGEdgeJSONRepresentation e: rep.getEdges()) {
			int count = -1;
			for(SGEdgeJSONRepresentation e2: rep.getEdges()) {
				if(e.getId().equals(e2.getId())) {
					count++;
					if(count > 0) {
						Assert.fail();
						return;
					}
				}
				
			}
		}
	}
	
}
