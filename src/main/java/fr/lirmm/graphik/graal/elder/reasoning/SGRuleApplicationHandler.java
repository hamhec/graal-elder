package fr.lirmm.graphik.graal.elder.reasoning;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseHaltingCondition;
import fr.lirmm.graphik.graal.api.forward_chaining.RuleApplicationHandler;
import fr.lirmm.graphik.graal.api.forward_chaining.RuleApplier;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.defeasible.core.atoms.FlexibleAtom;
import fr.lirmm.graphik.graal.defeasible.core.preferences.AlternativePreference;
import fr.lirmm.graphik.graal.defeasible.core.rules.PreferenceRule;
import fr.lirmm.graphik.graal.elder.core.StatementGraph;
import fr.lirmm.graphik.graal.forward_chaining.halting_condition.FrontierRestrictedChaseHaltingCondition;
import fr.lirmm.graphik.graal.forward_chaining.halting_condition.HaltingConditionWithHandler;
import fr.lirmm.graphik.graal.forward_chaining.rule_applier.ExhaustiveRuleApplier;
import fr.lirmm.graphik.graal.homomorphism.SmartHomomorphism;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.CloseableIteratorWithoutException;
import fr.lirmm.graphik.util.stream.IteratorException;

public class SGRuleApplicationHandler implements RuleApplicationHandler{
	
	private StatementGraph sg;
	
	public SGRuleApplicationHandler(StatementGraph sg) {
		super();
		this.sg = sg;
	}
	
	// No pre rule application code is needed
	public boolean preRuleApplication(Rule rule, Substitution substitution,
			AtomSet data) {
		return true;
	}
	
	// After rule application code
	public CloseableIterator<Atom> postRuleApplication(Rule rule,
			Substitution substitution, AtomSet data, CloseableIterator<Atom> atomsToAdd) {
		
		// atomsToAdd is not reliable as atoms reference could be deleted if they are considered redundant
		// so we create our own image of those facts
		CloseableIteratorWithoutException<Atom> itNewFacts = substitution.createImageOf(rule.getHead()).iterator();
		CloseableIteratorWithoutException<Atom> itBody = substitution.createImageOf(rule.getBody()).iterator();
		AtomSet body = new LinkedListAtomSet();
		while(itBody.hasNext()) {
			try {
				body.add(new FlexibleAtom(itBody.next()));
			} catch (AtomSetException e) {
				e.printStackTrace();
			}
		}
		// Add a rule application (statement) for each generated Atom
		while(itNewFacts.hasNext()) {
			try {
				FlexibleAtom newAtom = null;
				// If the generated atom is a prefernce then instanticate a preference.
				if(rule instanceof PreferenceRule) {
					newAtom = new AlternativePreference(itNewFacts.next());
				} else {
					newAtom = new FlexibleAtom(itNewFacts.next());
				}
				sg.addStatementForRuleApplication(body, newAtom, rule);
			} catch (IteratorException e) {
				e.printStackTrace();
			} catch (AtomSetException e) {
				e.printStackTrace();
			} 
		}
		return atomsToAdd;
	}
	
	
	public RuleApplier<Rule, AtomSet> getRuleApplier() {
		return getRuleApplier(new FrontierRestrictedChaseHaltingCondition());
	}
	
	public RuleApplier<Rule, AtomSet> getRuleApplier(ChaseHaltingCondition chaseCondition) {
		HaltingConditionWithHandler chaseConditionHandler = new HaltingConditionWithHandler(chaseCondition, this);
		RuleApplier<Rule, AtomSet> ruleApplier = new ExhaustiveRuleApplier<AtomSet>(SmartHomomorphism.instance(), chaseConditionHandler); 
		
		return ruleApplier;
	}
	
}
