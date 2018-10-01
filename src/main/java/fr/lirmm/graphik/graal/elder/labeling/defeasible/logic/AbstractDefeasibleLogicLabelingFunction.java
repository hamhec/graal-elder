package fr.lirmm.graphik.graal.elder.labeling.defeasible.logic;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import fr.lirmm.graphik.graal.defeasible.core.DefeasibleKnowledgeBase;
import fr.lirmm.graphik.graal.defeasible.core.preferences.Preference.Status;
import fr.lirmm.graphik.graal.elder.core.Premise;
import fr.lirmm.graphik.graal.elder.core.RuleApplication;
import fr.lirmm.graphik.graal.elder.core.SGEdge;
import fr.lirmm.graphik.graal.elder.core.Statement;
import fr.lirmm.graphik.graal.elder.labeling.LabelingFunction;
import fr.lirmm.graphik.graal.elder.labeling.Labels;
import fr.lirmm.graphik.graal.elder.preference.PreferenceFunction;
import fr.lirmm.graphik.graal.elder.preference.SimplePreferenceFunction;

public abstract class AbstractDefeasibleLogicLabelingFunction extends LabelingFunction {
	
	protected DefeasibleKnowledgeBase kb;
	protected PreferenceFunction preferenceFunction;
	
	
	public AbstractDefeasibleLogicLabelingFunction() {
	}
	
	public AbstractDefeasibleLogicLabelingFunction(PreferenceFunction pf) {
		this.preferenceFunction = pf;
	}
	
	public void setKnowledgeBase(DefeasibleKnowledgeBase kb) {
		this.kb = kb;
		this.preferenceFunction = new SimplePreferenceFunction(this.kb);
	}

	/**
	 * Indicates if this attack should be considered for the semantics
	 * @param attack an SGEdge representing the attack
	 * @return true if the label of this attack has an impact on the semantics
	 */
	public abstract boolean shouldAttackBeConsidered(SGEdge attack);
	
	
	/**
	 * Handles the semantics given the surviving supports and attacks
	 * @param premise a premise to be labeled
	 * @param survivingSupports the surviving supports for this premise
	 * @param survivingAttacks the surviving attacks for this premise
	 * @return the label of the premise
	 */
	public abstract String handleSurvivingSupportsAndAttacks(Premise premise, 
			List<SGEdge> survivingSupports, List<SGEdge> survivingAttacks);
	
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
			if(!shouldAttackBeConsidered(attack)) continue;
			
			
			if(DefeasibleLogicLabelingHelper.isStrictIn(attack.getLabel())) { // Attacked by a Strict In
				ruleApplication.setLabel(Labels.STRICT_OUT); // STRICT_OUT
				return ruleApplication.getLabel();
			} else { // attacked by defeasible In 
				Status prefStatus = this.preferenceFunction.preferenceStatus(ruleApplication, attack);
				
				if(prefStatus == Status.INFERIOR) { // This attacks kills the rule application
					if(DefeasibleLogicLabelingHelper.isDefeasibleIn(attack.getLabel())) {
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
		
		// Keep only supports that should be considered i.e. DefeasibleIn, Ambiguous
		for(SGEdge support: supports) { // Check the label of each support edge
			// If the support has no label, then compute it.
			if(support.getLabel() == null) this.label(support);
			
			if(DefeasibleLogicLabelingHelper.isStrictIn(support.getLabel())) { 
				// If the support is STRICT IN then label the assumption STRICT_IN.
				premise.setLabel(Labels.STRICT_IN);
				return premise.getLabel(); // there is no need to check the rest.
			} else if(DefeasibleLogicLabelingHelper.isDefeasibleIn(support.getLabel()) || 
					DefeasibleLogicLabelingHelper.isAmbiguous(support.getLabel())) { 
				// If the support is defeasible In or ambiguous then add it
				survivingSupports.add(support);
			}
		}
		
		// Check Attack edges
		HashSet<SGEdge> attacks = premise.getAttackEdges();	
		LinkedList<SGEdge> survivingAttacks = new LinkedList<SGEdge>();
		
		// Keep only attacks that should be considered DefeasibleIn (or ambig depending on semantics)
		for(SGEdge attack: attacks) {
			// Compute its label only if it does not have one already.
			if(attack.getLabel() == null) this.label(attack);
			
			// if this attack is not a strict in or a defeasible in then do not consider it
			if(!shouldAttackBeConsidered(attack)) continue;
			
			if(DefeasibleLogicLabelingHelper.isStrictIn(attack.getLabel())) { // Attacked by a Strict In
				premise.setLabel(Labels.STRICT_OUT); // STRICT_OUT
				return premise.getLabel(); // No need to check other attacks
			} else { // DefeasibleIN support vs defeasibleIn attack
				survivingAttacks.add(attack);
			}
		}
		
		
		return this.handleSurvivingSupportsAndAttacks(premise, survivingSupports, survivingAttacks);
	}
	
	
	public String label(Statement statement) {
		
		if(statement.getLabel() != null) return statement.getLabel();
		
		boolean defeasibleOut = false;
		boolean defeasibleIn = false;
		boolean ambiguous = false;
		boolean unsupported = false;
		// Find the label for the premises
		for(Premise prem: statement.getPremises()) {
			this.label(prem);
			if(DefeasibleLogicLabelingHelper.isStrictOut(prem.getLabel())) { // If a premise is strictOut then the statement is strict Out
				statement.setLabel(Labels.STRICT_OUT);
				return statement.getLabel();
			} else if(DefeasibleLogicLabelingHelper.isDefeasibleOut(prem.getLabel())) {
				defeasibleOut = true;
			} else if(DefeasibleLogicLabelingHelper.isAmbiguous(prem.getLabel())) {
				ambiguous = true;
			} else if(DefeasibleLogicLabelingHelper.isDefeasibleIn(prem.getLabel())) {
				defeasibleIn = true;
			} else if(DefeasibleLogicLabelingHelper.isUnsupported(prem.getLabel())) {
				unsupported = true;
			}
		}
		
		// If a premise is Defeasible Out then the statement is defeasible Out regardless of the label of the rule
		if(unsupported) {
			statement.setLabel(Labels.ASSUMED_OUT);
			return statement.getLabel();
		} else if(defeasibleOut) {
			statement.setLabel(Labels.DEFEASIBLE_OUT);
			return statement.getLabel();
		}
		
		// Find the Label for the rule application
		boolean ruleIsDefeasibleIn = false;
		boolean ruleIsAmbiguous = false;
		
		if(!statement.isClaimStatement()) { // If not a claim satement then take a look at it's rule application
			if(null == statement.getRuleApplication().getLabel())
				this.label(statement.getRuleApplication());
			
			if(DefeasibleLogicLabelingHelper.isDefeasibleOut(statement.getRuleApplication().getLabel())) { // if the rule application is out then all is out
				statement.setLabel(Labels.DEFEASIBLE_OUT);
				return statement.getLabel();
			} else if(DefeasibleLogicLabelingHelper.isAmbiguous(statement.getRuleApplication().getLabel())) {
				ruleIsAmbiguous = true;
			} else if(DefeasibleLogicLabelingHelper.isDefeasibleIn(statement.getRuleApplication().getLabel())) {
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
