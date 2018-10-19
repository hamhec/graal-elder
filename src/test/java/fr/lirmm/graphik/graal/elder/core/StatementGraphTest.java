package fr.lirmm.graphik.graal.elder.core;

import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.defeasible.core.DefeasibleKnowledgeBase;
import fr.lirmm.graphik.util.stream.IteratorException;

public class StatementGraphTest {
	
	private static StatementGraph sg;
	
	@BeforeClass
	public static void setup() throws IteratorException, ChaseException, AtomSetException, HomomorphismException {
		DefeasibleKnowledgeBase kb = new DefeasibleKnowledgeBase();
		kb.add("penguin(kowalski), brokenWings(kowalski).");
		kb.add("notFly(X), bird(X) <- penguin(X).");
		kb.add("fly(X) <= bird(X).");
		kb.add("notFly(X) <~ brokenWings(X).");
		kb.add("! :- fly(X), notFly(X).");
		kb.getNegativeConstraints();
		sg = new StatementGraph(kb);
		sg.build();
	}
	@Test
	public void shouldCreateStatementsAsMuchAsThereAreRuleApplications() throws AtomSetException, IteratorException, ChaseException, HomomorphismException {	
		Assert.assertEquals(7, sg.getAllStatements().size());
	}
	
//	@Test
//	public void shouldCreateAllSupportEdges() throws AtomSetException, IteratorException, ChaseException, HomomorphismException {
//		Assert.assertEquals(6, sg.getSupportEdges().size());
//	}
//	
//	@Test 
//	public void shouldCreateAllAttackEdges() throws IteratorException, ChaseException, AtomSetException, HomomorphismException {
////		List<SGEdge> edges = sg.getAttackEdges();
////		for(SGEdge edge: edges) {
////			System.out.println(edge);
////		}
//		Assert.assertEquals(3, sg.getAttackEdges().size());
//	}
	
	
}
