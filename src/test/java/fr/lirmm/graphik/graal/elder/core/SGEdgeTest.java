package fr.lirmm.graphik.graal.elder.core;

import java.util.HashSet;
import java.util.List;

import org.junit.Test;

import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.defeasible.core.DefeasibleKnowledgeBase;
import fr.lirmm.graphik.util.stream.IteratorException;
import org.junit.Assert;

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
}
