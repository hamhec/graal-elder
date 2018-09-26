package fr.lirmm.graphik.graal.elder.preference;

import fr.lirmm.graphik.graal.defeasible.core.DefeasibleKnowledgeBase;
import fr.lirmm.graphik.graal.defeasible.core.preferences.Preference.Status;
import fr.lirmm.graphik.graal.elder.core.Premise;
import fr.lirmm.graphik.graal.elder.core.RuleApplication;
import fr.lirmm.graphik.graal.elder.core.SGEdge;

public class SimplePreferenceFunction extends PreferenceFunction {

	public SimplePreferenceFunction(DefeasibleKnowledgeBase kb) {
		super(kb);
	}
	
	public Status preferenceStatus(SGEdge edge1, SGEdge edge2) {
		String label1 = edge1.getSource().getRuleApplication().getRule().getLabel();
		String label2 = edge2.getSource().getRuleApplication().getRule().getLabel();
		
		return this.preferenceStatus(label1, label2);
	}

	@Override
	public Status preferenceStatus(RuleApplication ruleApplication, SGEdge attack) {
		String label1 = ruleApplication.getRule().getLabel();
		String label2 = attack.getSource().getRuleApplication().getRule().getLabel();
		
		return this.preferenceStatus(label1, label2);
	}

	@Override
	public Status preferenceStatus(Premise ruleApplication, SGEdge attack) {
		// TODO Auto-generated method stub
		return null;
	}

	
}
