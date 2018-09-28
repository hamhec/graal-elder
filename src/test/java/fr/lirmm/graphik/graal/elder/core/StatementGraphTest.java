package fr.lirmm.graphik.graal.elder.core;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.io.ParseException;
import fr.lirmm.graphik.graal.elder.SetupTestClass;

public class StatementGraphTest extends SetupTestClass {
	
	@Test
	public void shouldCreateStatementsAsMuchAsThereAreRuleApplications() throws ParseException, AtomSetException {	
		Assert.assertTrue((sg.getAllStatements().size() == 18));
	}
	
	@Test
	public void shouldCreateAllSupportEdges() {
		List<SGEdge> edges = sg.getSupportEdges();
		/*for(EDGEdge edge: edges) {
			System.out.println(edge);
		}*/
		Assert.assertTrue((edges.size() == 15));
	}
	
	@Test 
	public void shouldCreateAllAttackEdges() {
		List<SGEdge> edges = sg.getAttackEdges();
		/*for(EDGEdge edge: edges) {
			System.out.println(edge);
		}*/
		Assert.assertTrue((edges.size() == 11));
	}
}
