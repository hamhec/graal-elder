package fr.lirmm.graphik.graal.elder.labeling.defeasible.logic;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import fr.lirmm.graphik.graal.defeasible.core.DefeasibleKnowledgeBase;
import fr.lirmm.graphik.graal.defeasible.core.preferences.Preference;
import fr.lirmm.graphik.graal.defeasible.core.preferences.PreferenceSet;
import fr.lirmm.graphik.graal.defeasible.core.preferences.Preference.Status;
import fr.lirmm.graphik.graal.elder.core.Premise;
import fr.lirmm.graphik.graal.elder.core.RuleApplication;
import fr.lirmm.graphik.graal.elder.core.SGEdge;
import fr.lirmm.graphik.graal.elder.core.Statement;
import fr.lirmm.graphik.graal.elder.preference.PreferenceFunction;
import fr.lirmm.graphik.graal.elder.preference.SimplePreferenceFunction;



/**
 * Labeling function for ambibuity propagating with team defeat
 * @author hamhec
 *
 */
public class PDLwithTD extends AbstractDefeasibleLogicLabelingFunction {
	
	
	
	public PDLwithTD(PreferenceSet prefs) {
		super(prefs);
	}
	
	public PDLwithTD(PreferenceFunction pf) {
		super(pf);
	}
	
	public boolean shouldAttackBeConsidered(SGEdge attack) {
		return DefeasibleLogicLabelingHelper.ambiguityPropagating(attack);
	}
	
	
	public String handleSurvivingSupportsAndAttacks(Premise premise, List<SGEdge> survivingSupports, List<SGEdge> survivingAttacks) {
		return DefeasibleLogicLabelingHelper.withTeamDefeat(premise, 
				survivingSupports, survivingAttacks, this.getPreferenceFunction());
	}
	
}
