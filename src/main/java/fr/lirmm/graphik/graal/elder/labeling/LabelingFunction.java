package fr.lirmm.graphik.graal.elder.labeling;

import fr.lirmm.graphik.graal.elder.core.Premise;
import fr.lirmm.graphik.graal.elder.core.RuleApplication;
import fr.lirmm.graphik.graal.elder.core.SGEdge;
import fr.lirmm.graphik.graal.elder.core.Statement;
import fr.lirmm.graphik.graal.elder.preference.PreferenceFunction;

public abstract class LabelingFunction {
	
	public static final String BDLwithTD="BDLwithTD", BDLwithoutTD="BDLwithoutTD",
			PDLwithTD="PDLwithTD", PDLwithoutTD="PDLwithoutTD";
	
	/**
	 * Labels a permise
	 * @param premise a permise
	 * @return the label given to the premise
	 */
	public abstract String label(Premise premise);
	
	/**
	 * Labels a rule application
	 * @param ruleApplication a rule application
	 * @return the label given to the rule application
	 */
	public abstract String label(RuleApplication ruleApplication);
	
	/**
	 * Labels a statement
	 * @param statement a statement
	 * @return the label given to the statement
	 */
	public abstract String label(Statement statement);
	
	/**
	 * Labels an edge
	 * @param edge an edge
	 * @return the label given to the edge
	 */
	public abstract String label(SGEdge edge);
	
	
	/**
	 * returns the preference function used.
	 * @return the preference function used by the labeling
	 */
	public abstract PreferenceFunction getPreferenceFunction();
}
