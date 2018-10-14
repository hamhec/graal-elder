package fr.lirmm.graphik.graal.elder.preference;

import fr.lirmm.graphik.graal.defeasible.core.DefeasibleKnowledgeBase;
import fr.lirmm.graphik.graal.defeasible.core.preferences.Preference.Status;
import fr.lirmm.graphik.graal.defeasible.core.preferences.PreferenceSet;
import fr.lirmm.graphik.graal.elder.core.Premise;
import fr.lirmm.graphik.graal.elder.core.RuleApplication;
import fr.lirmm.graphik.graal.elder.core.SGEdge;

public abstract class PreferenceFunction {
	
	private PreferenceSet rulePreferenceSet;
	
	public PreferenceFunction(PreferenceSet rulePreferenceSet) {
		this.rulePreferenceSet = rulePreferenceSet;
	}
	
	public Status preferenceStatus(String label1, String label2) {
		return this.getRulePreferenceSet().preferenceStatus(label1, label2);
	}
	
	public abstract Status preferenceStatus(SGEdge edge1, SGEdge edge2);
	public abstract Status preferenceStatus(RuleApplication ruleApplication, SGEdge attack);
	
	public PreferenceSet getRulePreferenceSet() {
		return this.rulePreferenceSet;
	}

	
	
}
