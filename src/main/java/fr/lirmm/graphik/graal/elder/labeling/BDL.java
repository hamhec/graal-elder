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
/**
 * @author hamhec
 *
 */
public class BDL extends LabelingFunction {
	
	private DefeasibleKnowledgeBase kb;
	private PreferenceFunction preferenceFunction;
	
	public BDL(DefeasibleKnowledgeBase kb) {
		this.kb = kb;
		this.preferenceFunction = new SimplePreferenceFunction(this.kb);
	}
	
	public BDL(DefeasibleKnowledgeBase kb, PreferenceFunction pf) {
		this(kb);
		this.preferenceFunction = pf;
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
			if(!this.isStrictIn(attack.getLabel()) && !this.isDefeasibleIn(attack.getLabel()) ) continue;
			
			
			if(this.isStrictIn(attack.getLabel())) { // Attacked by a Strict In
				ruleApplication.setLabel(Labels.STRICT_OUT); // STRICT_OUT
				return ruleApplication.getLabel();
			} else { // attacked by defeasible In 
				Status prefStatus = this.preferenceFunction.preferenceStatus(ruleApplication, attack);
				
				if(prefStatus == Status.INFERIOR) { // This attacks kills the rule application
					if(this.isDefeasibleIn(attack.getLabel())) {
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
		
		for(SGEdge support: supports) { // Check the label of each support edge
			// If the support has no label, then compute it.
			if(support.getLabel() == null) this.label(support);
			
			
			if(this.isStrictIn(support.getLabel())) { 
				// If the support is STRICT IN then label the assumption STRICT_IN.
				premise.setLabel(Labels.STRICT_IN);
				return premise.getLabel(); // there is no need to check the rest.
			} else if(this.isDefeasibleIn(support.getLabel()) || this.isAmbiguous(support.getLabel())) { 
				// If the support is not OUT then add it
				survivingSupports.add(support);
			} // TODO: continue here
		}
		
		// Check Attack edges
		HashSet<SGEdge> attacks = premise.getAttackEdges();	
		for(SGEdge attack: attacks) {
			// Compute its label only if it does not have one already.
			if(attack.getLabel() == null) this.label(attack);
			
			// if this attack is not a strict in or a defeasible in then do not consider it
			if(!attack.getLabel().equals(Labels.AMBIGUOUS)) continue;
			
			if(attack.getLabel().equals(Labels.STRICT_IN)) { // Attacked by a Strict In
				premise.setLabel(Labels.STRICT_OUT); // STRICT_OUT
				return premise.getLabel(); // No need to check other attacks
			} else { // DefeasibleIN support vs defeasibleIn attack
				Iterator<SGEdge> itSurviv = survivingSupports.iterator();
				
				// How many surviving supports does this attack kill?
				while(itSurviv.hasNext()) {
					SGEdge survivingSupport = itSurviv.next();
					
					Status pref = this.preferenceFunction.preferenceStatus(survivingSupport, attack);
					
					if(pref == Status.INFERIOR && attack.getLabel().equals(Labels.DEFEASIBLE_IN)) { 
						itSurviv.remove();// This attack kills this edge
					} else if(pref.equals(Preference.EQUAL)) {
						if(attack.getLabel().equals(Labels.DEFEASIBLE_IN)) {
							survivingSupport.isCountered(true); // This support has been countered.
						}
					} else {
						// Should remove the attack if you want team support, pay attention to the order
					}
				}
				
				if(survivingSupports.isEmpty()) { // All supports have been killed.
					premise.setLabel(Labels.OUT); // DEFEASIBLE_OUT
					return premise.getLabel(); // No need to check other attacks
				}
			}
		}
		
		boolean ambiguous = false;
		// If all supports have been countered then it's AMBIGUOUS
		for(SGEdge support: survivingSupports) {
			if(support.getLabel().equals(Labels.DEFEASIBLE_IN)) {
				if(support.isCountered()) {
					ambiguous = true;
				} else { // A defeasible support is IN and is not countred, so IN.
					premise.setLabel(Labels.DEFEASIBLE_IN);
					return premise.getLabel();
				}
			} else if(support.getLabel().equals(Labels.AMBIGUOUS)) {
				ambiguous = true;
			}
		}
		
		if(ambiguous) {
			premise.setLabel(Labels.AMBIGUOUS);
		} else {
			premise.setLabel(Labels.OUT);
		}

		return premise.getLabel();
	}

	public String label(Statement statement) {
		
		if(statement.getLabel() != null) return statement.getLabel();
		// We assume it's out
		String statementLabel = Labels.OUT;
		
		// Find the Label for the rule application
		if(!statement.isClaimStatement()) { // If not a claim satement then take a look at it's rule application
			this.label(statement.getRuleApplication());
			
			if(statement.getRuleApplication().getLabel().equals(Labels.OUT)) { // if the rule application is out then all is out
				statementLabel = Labels.OUT;
				statement.setLabel(statementLabel);
				return statement.getLabel();
			} 
		}
		boolean defeasible = false;
		boolean ambiguous = false;
		// Find the label for the premises
		for(Premise prem: statement.getPremises()) {
			this.label(prem);
			if(prem.getLabel().equals(Labels.AMBIGUOUS)) {
				ambiguous = true;
			} else if(prem.getLabel().equals(Labels.OUT)) {
				statementLabel = Labels.OUT; // need distinction between strict out and defeasible out
				statement.setLabel(statementLabel); 
				return statement.getLabel();
			} else if(prem.getLabel().equals(Labels.DEFEASIBLE_IN)) {
				defeasible = true;
			}
		}
		
		if(statement.isClaimStatement()) { // if it's a statement then only check Premise
			if(ambiguous) {
				statementLabel = Labels.AMBIGUOUS;
			} else if(defeasible){
				statementLabel = Labels.DEFEASIBLE_IN;
			} else {
				statementLabel = Labels.STRICT_IN;
			}
		} else { // Not a claim statement
			if(ambiguous || statement.getRuleApplication().getLabel().equals(Labels.AMBIGUOUS)) {
				statementLabel = Labels.AMBIGUOUS;
			} else if(defeasible || statement.getRuleApplication().getLabel().equals(Labels.DEFEASIBLE_IN)) {
				statementLabel = Labels.DEFEASIBLE_IN;
			} else {
				statementLabel = Labels.STRICT_IN;
			}
		}
		
		statement.setLabel(statementLabel);
		
		return statement.getLabel();
	}

	public String label(SGEdge edge) {
		if(edge.getLabel() != null) return edge.getLabel();
		Statement source = edge.getSource();
		if(source.getLabel() == null) this.label(source);
		
		edge.setLabel(source.getLabel());
		
		return edge.getLabel();
	}
	
	public PreferenceFunction getPreferenceFunction() {
		return this.preferenceFunction;
	}
}
