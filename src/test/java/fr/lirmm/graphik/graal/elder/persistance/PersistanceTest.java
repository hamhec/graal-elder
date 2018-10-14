package fr.lirmm.graphik.graal.elder.persistance;

import org.json.simple.parser.ParseException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.defeasible.core.DefeasibleKnowledgeBase;
import fr.lirmm.graphik.graal.elder.core.StatementGraph;
import fr.lirmm.graphik.util.stream.IteratorException;

public class PersistanceTest {
		
	@Test
	public void shouldDeflateAndInflateTheSameSGWithoutLabels() throws AtomSetException, IteratorException, ChaseException, HomomorphismException, ParseException {	
		DefeasibleKnowledgeBase kb = new DefeasibleKnowledgeBase();
		kb = new DefeasibleKnowledgeBase();
		
		// buying example
		kb.add("go(vacation) <= .");
		kb.add("price(phone,cheap), reviews(phone,good), eco(phone,detrimental), delivery(phone,slow).");
		
		kb.add("[rcheap] buy(X) <= price(X,cheap).");
		kb.add("[rreviews] buy(X) <= reviews(X,good).");
		
		kb.add("[recho] notBuy(X) <= eco(X,detrimental).");
		kb.add("[rdelivery] notBuy(X) <= delivery(X,slow).");
		
		kb.add("take(loan) <= buy(X).");
		kb.add("notGo(vacation) <= buy(X).");
		kb.add("take(loan) <= go(vacation).");
		
		kb.add("! :- notBuy(X), buy(X).");
		kb.add("! :- go(X), notGo(X).");
		
		kb.add(" rcheap >> recho .");
		kb.add(" rreviews >> rdelivery .");
		
		// legal example
		kb.add("innocent(alice) <= .");
		kb.add("female(alice) <= .");
		kb.add("testimony(raouf, incriminating, alice) <= .");
		kb.add("testimony(hecham, absolving, alice) <= .");
		
		kb.add("sentenced(X) <- guilty(X).");
		kb.add("guilty(X) <- responsible(X).");
		kb.add("responsible(Y) <- testimony(X, incriminating, Y).");
		kb.add("notResponsible(Y) <- testimony(X, absolving, Y).");
		
		
		kb.add("! :- guilty(X), innocent(X).");
		kb.add("! :- responsible(X), notResponsible(X).");
		
		// Penguin and Birds
		kb.add("penguin(kowalski), bird(tweety), brokenWings(tweety).");
		kb.add("animal(kowalski) <= .");
		kb.add("animal(tweety) <= .");
		kb.add("happy(tweety) <= .");
		kb.add("beautiful(X) <- penguin(X).");
		kb.add("sad(X) <- brokenWings(X).");
		
		kb.add("[rpenguin] notFly(X), bird(X) <= penguin(X).");
		kb.add("[rfly] fly(X) <= bird(X).");
		kb.add("[rbroken] notFly(X) <~ brokenWings(X) .");
		
		kb.add("! :- fly(X), notFly(X).");
		kb.add("! :- happy(X), sad(X).");
		
		kb.add("rpenguin >> rfly .");
		kb.add("rbroken >> rfly .");
		
		StatementGraph sg = new StatementGraph(kb);
		sg.build();
		
		
		String sg1String = PersistanceFactory.instance().deflateStatementGraph(sg);
		StatementGraph sg2 = PersistanceFactory.instance().inflateStatementGraph(sg1String);
	
		Assert.assertTrue(sg.equals(sg2));
	}
	
	@Test
	public void shouldDeflateAndInflateTheSameSGWithLabels() throws AtomSetException, IteratorException, ChaseException, HomomorphismException, ParseException {	
		DefeasibleKnowledgeBase kb = new DefeasibleKnowledgeBase();
		kb = new DefeasibleKnowledgeBase();
		
		// buying example
		kb.add("go(vacation) <= .");
		kb.add("price(phone,cheap), reviews(phone,good), eco(phone,detrimental), delivery(phone,slow).");
		
		kb.add("[rcheap] buy(X) <= price(X,cheap).");
		kb.add("[rreviews] buy(X) <= reviews(X,good).");
		
		kb.add("[recho] notBuy(X) <= eco(X,detrimental).");
		kb.add("[rdelivery] notBuy(X) <= delivery(X,slow).");
		
		kb.add("take(loan) <= buy(X).");
		kb.add("notGo(vacation) <= buy(X).");
		kb.add("take(loan) <= go(vacation).");
		
		kb.add("! :- notBuy(X), buy(X).");
		kb.add("! :- go(X), notGo(X).");
		
		kb.add(" rcheap >> recho .");
		kb.add(" rreviews >> rdelivery .");
		
		// legal example
		kb.add("innocent(alice) <= .");
		kb.add("female(alice) <= .");
		kb.add("testimony(raouf, incriminating, alice) <= .");
		kb.add("testimony(hecham, absolving, alice) <= .");
		
		kb.add("sentenced(X) <- guilty(X).");
		kb.add("guilty(X) <- responsible(X).");
		kb.add("responsible(Y) <- testimony(X, incriminating, Y).");
		kb.add("notResponsible(Y) <- testimony(X, absolving, Y).");
		
		
		kb.add("! :- guilty(X), innocent(X).");
		kb.add("! :- responsible(X), notResponsible(X).");
		
		// Penguin and Birds
		kb.add("penguin(kowalski), bird(tweety), brokenWings(tweety).");
		kb.add("animal(kowalski) <= .");
		kb.add("animal(tweety) <= .");
		kb.add("happy(tweety) <= .");
		kb.add("beautiful(X) <- penguin(X).");
		kb.add("sad(X) <- brokenWings(X).");
		
		kb.add("[rpenguin] notFly(X), bird(X) <= penguin(X).");
		kb.add("[rfly] fly(X) <= bird(X).");
		kb.add("[rbroken] notFly(X) <~ brokenWings(X) .");
		
		kb.add("! :- fly(X), notFly(X).");
		kb.add("! :- happy(X), sad(X).");
		
		kb.add("rpenguin >> rfly .");
		kb.add("rbroken >> rfly .");
		
		StatementGraph sg = new StatementGraph(kb);
		sg.build();
		
		sg.groundQuery("take(loan).");
		String sg1String = PersistanceFactory.instance().deflateStatementGraph(sg);
		StatementGraph sg2 = PersistanceFactory.instance().inflateStatementGraph(sg1String);
		
		Assert.assertTrue(sg.equals(sg2));
	}
}
