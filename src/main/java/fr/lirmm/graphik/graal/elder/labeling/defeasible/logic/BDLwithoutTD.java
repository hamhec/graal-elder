package fr.lirmm.graphik.graal.elder.labeling.defeasible.logic;

import java.util.List;

import fr.lirmm.graphik.graal.defeasible.core.preferences.PreferenceSet;
import fr.lirmm.graphik.graal.elder.core.Premise;
import fr.lirmm.graphik.graal.elder.core.SGEdge;
import fr.lirmm.graphik.graal.elder.preference.PreferenceFunction;



/**
 * Labeling function for ambibuity blocking without team defeat
 * @author hamhec
 *
 */
public class BDLwithoutTD extends AbstractDefeasibleLogicLabelingFunction {
	
	public BDLwithoutTD(PreferenceSet prefs) {
		super(prefs);
	}
	
	public BDLwithoutTD(PreferenceFunction pf) {
		super(pf);
	}
	
	public boolean shouldAttackBeConsidered(SGEdge attack) {
		return DefeasibleLogicLabelingHelper.ambiguityBlocking(attack);
	}
	
	
	public String handleSurvivingSupportsAndAttacks(Premise premise, List<SGEdge> survivingSupports, List<SGEdge> survivingAttacks) {
		return DefeasibleLogicLabelingHelper.withoutTeamDefeat(premise, 
				survivingSupports, survivingAttacks, this.getPreferenceFunction());
	}

	
}
