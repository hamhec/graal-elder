package fr.lirmm.graphik.graal.elder.labeling;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import fr.lirmm.graphik.graal.defeasible.core.DefeasibleKnowledgeBase;
import fr.lirmm.graphik.graal.defeasible.core.preferences.Preference;
import fr.lirmm.graphik.graal.defeasible.core.preferences.Preference.Status;
import fr.lirmm.graphik.graal.elder.core.Premise;
import fr.lirmm.graphik.graal.elder.core.RuleApplication;
import fr.lirmm.graphik.graal.elder.core.SGEdge;
import fr.lirmm.graphik.graal.elder.core.Statement;
import fr.lirmm.graphik.graal.elder.preference.PreferenceFunction;
import fr.lirmm.graphik.graal.elder.preference.SimplePreferenceFunction;



/**
 * Labeling function for ambibuity blocking with team defeat
 * @author hamhec
 *
 */
public class BDLnoTD extends AbstractDefeasibleLogicLabelingFunction {
	
	
	
	public BDLnoTD(DefeasibleKnowledgeBase kb) {
		super(kb);
	}
	
	public BDLnoTD(DefeasibleKnowledgeBase kb, PreferenceFunction pf) {
		super(kb,pf);
	}
	
	public String label(RuleApplication ruleApplication) {
		
		// Compute its label only if it does not have one already.
		if(ruleApplication.getLabel() != null) return ruleApplication.getLabel();
		
		// Start by assuming it is IN depending on its type
		if(ruleApplication.isDefeasible() || ruleApplication.isDefeater()) {
			ruleApplication.setLabel(Labels.DEFEASIBLE_IN);
		} else {
			ruleApplication.setLabel(Labels.STRICT_IN);
			return ruleApplication.getLabel(); // if we assume that strict knowledge cannot be killed
		}
		
		// Check Attacks
		HashSet<SGEdge> attacks = ruleApplication.getAttackEdges();	
		
		boolean ambiguous = false;
		for(SGEdge attack: attacks) {
			if(attack.getLabel() == null) {
				this.label(attack);
			}
			
			// do not consider non in attacks
			if(!this.isStrictIn(attack.getLabel()) && !isDefeasibleIn(attack.getLabel()) ) continue;
			
			
			if(isStrictIn(attack.getLabel())) { // Attacked by a Strict In
				ruleApplication.setLabel(Labels.STRICT_OUT); // STRICT_OUT
				return ruleApplication.getLabel();
			} else { // attacked by defeasible In 
				Status prefStatus = this.preferenceFunction.preferenceStatus(ruleApplication, attack);
				
				if(prefStatus == Status.INFERIOR) { // This attacks kills the rule application
					if(isDefeasibleIn(attack.getLabel())) {
						ruleApplication.setLabel(Labels.DEFEASIBLE_OUT);
						return ruleApplication.getLabel();
					}
					
				} else if(prefStatus == Status.EQUAL) {
					ambiguous = true;
				}
			}
		}
		
		if(ambiguous) {
			ruleApplication.setLabel(Labels.AMBIGUOUS);
		}
		return ruleApplication.getLabel();
	}
	
		
	
	public String label(Premise premise) {
		
		// Compute its label only if it does not have one already.
		if(premise.getLabel() != null) return premise.getLabel();
		
		// Check Support edge
		HashSet<SGEdge> supports = premise.getSupportEdges();
		List<SGEdge> survivingSupports = new LinkedList<SGEdge>();
		
		if(supports.isEmpty()) {// No support then OUT
			premise.setLabel(Labels.ASSUMED_OUT); // ASSUMED_OUT
			return premise.getLabel(); // No need to check attacks
		}
		
		
		/* The idea of not allowing team defeat is that every support should be able to defend itself from all attacks. */
		for(SGEdge support: supports) { // Check the label of each support edge
			// If the support has no label, then compute it.
			if(support.getLabel() == null) this.label(support);
			
			
			if(isStrictIn(support.getLabel())) { 
				// If the support is STRICT IN then label the assumption STRICT_IN.
				premise.setLabel(Labels.STRICT_IN);
				return premise.getLabel(); // there is no need to check the rest.
			} else if(isDefeasibleIn(support.getLabel()) || this.isAmbiguous(support.getLabel())) { 
				// If the support is defeasible In or ambiguous then add it
				survivingSupports.add(support);
			}
		}
		
		// Check Attack edges
		HashSet<SGEdge> attacks = premise.getAttackEdges();	
		for(SGEdge attack: attacks) {
			// Compute its label only if it does not have one already.
			if(attack.getLabel() == null) this.label(attack);
			
			// if this attack is not a strict in or a defeasible in then do not consider it
			if(!isStrictIn(attack.getLabel()) && !isDefeasibleIn(attack.getLabel())) continue;
			
			if(isStrictIn(attack.getLabel())) { // Attacked by a Strict In
				premise.setLabel(Labels.STRICT_OUT); // STRICT_OUT
				return premise.getLabel(); // No need to check other attacks
			} else { // DefeasibleIN support vs defeasibleIn attack
				Iterator<SGEdge> itSurviv = survivingSupports.iterator();
				
				// How many surviving supports does this attack kill?
				while(itSurviv.hasNext()) {
					SGEdge survivingSupport = itSurviv.next();
					
					Status pref = this.preferenceFunction.preferenceStatus(survivingSupport, attack);
					
					if(pref == Status.INFERIOR) { 
						itSurviv.remove();// This attack kills this edge
					} else if(pref == Status.EQUAL) {
						survivingSupport.isCountered(true); // This support has been countered.
					}
				}
				
				if(survivingSupports.isEmpty()) { // All supports have been killed.
					premise.setLabel(Labels.DEFEASIBLE_OUT); // DEFEASIBLE_OUT
					return premise.getLabel(); // No need to check other attacks
				}
			}
		}
		
		// If all supports have been countered then it's AMBIGUOUS
		for(SGEdge support: survivingSupports) {
			if(isDefeasibleIn(support.getLabel())) {
				if(!support.isCountered()) {
					// A defeasible support is IN and is not countred, so Defeasible IN.
					premise.setLabel(Labels.DEFEASIBLE_IN);
					return premise.getLabel();
				}
			}
		}
		
		// This premise has no surviving DEFEASIBLE_IN that are not countered
		// Therefore, it either has a ambiguous support or a countered Defeasible In support
		premise.setLabel(Labels.AMBIGUOUS);
		return premise.getLabel();
	}

	
	
	
	public String label(Statement statement) {
		
		if(statement.getLabel() != null) return statement.getLabel();
		
		boolean defeasibleOut = false;
		boolean defeasibleIn = false;
		boolean strictIn = false;
		boolean ambiguous = false;
		boolean unsupported = false;
		// Find the label for the premises
		for(Premise prem: statement.getPremises()) {
			this.label(prem);
			if(isStrictOut(prem.getLabel())) { // If a premise is strictOut then the statement is strict Out
				statement.setLabel(Labels.STRICT_OUT);
				return statement.getLabel();
			} else if(isDefeasibleOut(prem.getLabel())) {
				defeasibleOut = true;
			} else if(isAmbiguous(prem.getLabel())) {
				ambiguous = true;
			} else if(isStrictIn(prem.getLabel())) {
				strictIn = true;
			} else if(isDefeasibleIn(prem.getLabel())) {
				defeasibleIn = true;
			}
		}
		
		// If a premise is Defeasible Out then the statement is defeasible Out regardless of the label of the rule
		if(defeasibleOut) {
			statement.setLabel(Labels.DEFEASIBLE_OUT);
			return statement.getLabel();
		}
		
		// Find the Label for the rule application
		boolean ruleIsDefeasibleIn = false;
		boolean ruleIsAmbiguous = false;
		
		if(!statement.isClaimStatement()) { // If not a claim satement then take a look at it's rule application
			this.label(statement.getRuleApplication());
			
			if(isDefeasibleOut(statement.getRuleApplication().getLabel())) { // if the rule application is out then all is out
				statement.setLabel(Labels.DEFEASIBLE_OUT);
				return statement.getLabel();
			} else if(isAmbiguous(statement.getRuleApplication().getLabel())) {
				ruleIsAmbiguous = true;
			} else {
				ruleIsDefeasibleIn = true;
			}
		} 
		
		if(ambiguous || ruleIsAmbiguous) {
			statement.setLabel(Labels.AMBIGUOUS);
		} else if(defeasibleIn || ruleIsDefeasibleIn){
			statement.setLabel(Labels.DEFEASIBLE_IN);
		} else {
			statement.setLabel(Labels.STRICT_IN);
		}
		
		return statement.getLabel();
	}

	
	
	
}
