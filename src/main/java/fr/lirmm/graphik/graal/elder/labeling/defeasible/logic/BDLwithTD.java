package fr.lirmm.graphik.graal.elder.labeling.defeasible.logic;

import java.util.List;

import fr.lirmm.graphik.graal.defeasible.core.preferences.PreferenceSet;
import fr.lirmm.graphik.graal.elder.core.Premise;
import fr.lirmm.graphik.graal.elder.core.SGEdge;
import fr.lirmm.graphik.graal.elder.preference.PreferenceFunction;



/**
 * Labeling function for ambibuity blocking with team defeat
 * @author hamhec
 *
 */
public class BDLwithTD extends AbstractDefeasibleLogicLabelingFunction {
	
	
	
	public BDLwithTD(PreferenceSet prefs) {
		super(prefs);
	}
	
	public BDLwithTD(PreferenceFunction pf) {
		super(pf);
	}
	
	public boolean shouldAttackBeConsidered(SGEdge attack) {
		return DefeasibleLogicLabelingHelper.ambiguityBlocking(attack);
	}
	
	
	public String handleSurvivingSupportsAndAttacks(Premise premise, List<SGEdge> survivingSupports, List<SGEdge> survivingAttacks) {
		return DefeasibleLogicLabelingHelper.withTeamDefeat(premise, 
				survivingSupports, survivingAttacks, this.getPreferenceFunction());
	}
	
}
