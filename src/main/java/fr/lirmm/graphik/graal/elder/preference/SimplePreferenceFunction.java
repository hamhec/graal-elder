package fr.lirmm.graphik.graal.elder.preference;

import fr.lirmm.graphik.graal.defeasible.core.preferences.Preference.Status;
import fr.lirmm.graphik.graal.defeasible.core.preferences.PreferenceSet;
import fr.lirmm.graphik.graal.elder.core.RuleApplication;
import fr.lirmm.graphik.graal.elder.core.SGEdge;

public class SimplePreferenceFunction extends PreferenceFunction {

	public SimplePreferenceFunction(PreferenceSet rulePreferenceSet) {
		super(rulePreferenceSet);
	}
	
	public Status preferenceStatus(SGEdge edge1, SGEdge edge2) {
		String label1 = edge1.getSource().getRuleApplication().getRuleLabel();
		String label2 = edge2.getSource().getRuleApplication().getRuleLabel();
		
		return this.preferenceStatus(label1, label2);
	}

	public Status preferenceStatus(RuleApplication ruleApplication, SGEdge attack) {
		String label1 = ruleApplication.getRuleLabel();
		String label2 = attack.getSource().getRuleApplication().getRuleLabel();
		
		return this.preferenceStatus(label1, label2);
	}

	
}
