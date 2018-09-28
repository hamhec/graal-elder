package fr.lirmm.graphik.graal.elder;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.defeasible.core.DefeasibleKnowledgeBase;
import fr.lirmm.graphik.graal.defeasible.core.preferences.RulePreference;
import fr.lirmm.graphik.graal.elder.core.StatementGraph;
import fr.lirmm.graphik.util.stream.IteratorException;

public class SetupTestClass {
	public static StatementGraph sg;
	
	private static boolean alreadySetup = false;
	
	@BeforeClass
	public static void setup() throws AtomSetException, IteratorException, ChaseException, HomomorphismException {
		if(!alreadySetup) {
			DefeasibleKnowledgeBase kb = new DefeasibleKnowledgeBase();
			kb.addFact("str1(a).");
			kb.addFact("str0(a).");
			kb.addDefeasibleRule("def1(a) <= .");
			kb.addDefeasibleRule("def0(a) <= .");
			
			kb.addStrictRule("str2(X), str3(X) <- str1(X).");
			kb.addDefeasibleRule("def2(X) <= def1(X).");
			kb.addDefeasibleRule("blocking(X) <= def1(X).");
			kb.addDefeasibleRule("ambig(X) <= def1(X).");
			kb.addDefeasibleRule("nambig(X) <= def2(X).");
			kb.addDefeasibleRule("ndefeater(X) <= def2(X).");
			kb.addDefeaterRule("defeater(X) <~ str1(X), def1(X).");
			
			kb.addStrictRule("nstr2(X) <- str3(X), def2(X).");
			
			kb.addStrictRule("nblocking(X) <- ambig(X).");
			
			kb.addDefeasibleRule("[r1] deadRule(X) <= str1(X)."); 
			kb.addDefeaterRule("[r2] ndeadRule(X) <~ def1(X)."); 
			kb.addDefeasibleRule("[r3] survivingRule(X) <= str1(X)."); 
			kb.addDefeaterRule("[r4] nsurvivingRule(X) <~ def1(X)."); 
			
			kb.addRulePreference(new RulePreference("r2", "r1"));
			kb.addRulePreference(new RulePreference("r3", "r4"));
			
			kb.addNegativeConstraint("!:- str2(X), nstr2(X).");
			kb.addNegativeConstraint("!:- defeater(X), ndefeater(X).");
			kb.addNegativeConstraint("!:- ambig(X), nambig(X).");
			kb.addNegativeConstraint("!:- blocking(X), nblocking(X).");
			kb.addNegativeConstraint("!:- deadRule(X), ndeadRule(X).");
			kb.addNegativeConstraint("!:- survivingRule(X), nsurvivingRule(X).");
			
			// Circular Conflict
			/*kb.addDefeasibleRule("[p0] p0(a) <= .");
			kb.addDefeasibleRule("[p1] p1(X) <= p0(X).");
			kb.addDefeasibleRule("[nq0] nq0(X) <= p1(X).");
			kb.addDefeasibleRule("[q0] q0(a) <= .");
			kb.addDefeasibleRule("[q1] q1(X) <= q0(X).");
			kb.addDefeasibleRule("[np0] np0(X) <= q1(X).");
			kb.addNegativeConstraint("!:- p0(X), np0(X).");
			kb.addNegativeConstraint("!:- q0(X), nq0(X).");*/
			
			sg = new StatementGraph(kb);
			sg.build();
			
			// To avoid running this before class multiple times
			alreadySetup = true;
		}
	}
}
