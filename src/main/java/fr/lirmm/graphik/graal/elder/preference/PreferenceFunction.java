package fr.lirmm.graphik.graal.elder.preference;

import fr.lirmm.graphik.graal.defeasible.core.DefeasibleKnowledgeBase;
import fr.lirmm.graphik.graal.defeasible.core.preferences.Preference.Status;
import fr.lirmm.graphik.graal.elder.core.Premise;
import fr.lirmm.graphik.graal.elder.core.RuleApplication;
import fr.lirmm.graphik.graal.elder.core.SGEdge;

public abstract class PreferenceFunction {
	
	private DefeasibleKnowledgeBase kb;
	
	public PreferenceFunction(DefeasibleKnowledgeBase kb) {
		this.kb = kb;
	}
	
	public Status preferenceStatus(String label1, String label2) {
		return this.getKB().getRulePreferences().preferenceStatus(label1, label2);
	}
	
	public abstract Status preferenceStatus(SGEdge edge1, SGEdge edge2);
	public abstract Status preferenceStatus(RuleApplication ruleApplication, SGEdge attack);
	public abstract Status preferenceStatus(Premise premise, SGEdge attack);
	
	public DefeasibleKnowledgeBase getKB() {
		return this.kb;
	}

	
	
}
