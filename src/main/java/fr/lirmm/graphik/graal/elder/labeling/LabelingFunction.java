package fr.lirmm.graphik.graal.elder.labeling;

import fr.lirmm.graphik.graal.elder.core.Premise;
import fr.lirmm.graphik.graal.elder.core.RuleApplication;
import fr.lirmm.graphik.graal.elder.core.SGEdge;
import fr.lirmm.graphik.graal.elder.core.Statement;
import fr.lirmm.graphik.graal.elder.preference.PreferenceFunction;

public abstract class LabelingFunction {
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
	
	/**
	 * checks if the label is considered OUT
	 * @param lbl a label
	 * @return true if the label is defeasible out, strict out, or assumed out
	 */
	public boolean isOut(String lbl) {
		return (this.isStrictOut(lbl) || this.isDefeasibleOut(lbl) || this.isAmbiguous(lbl));
	}
	
	/**
	 * checks if the label is considered Strict In
	 * @param lbl a label
	 * @return true if the label is Strict In
	 */
	public boolean isStrictIn(String lbl) {
		return lbl.equals(Labels.STRICT_IN);
	}
	
	/**
	 * checks if the label is considered Strict Out
	 * @param lbl a label
	 * @return true if the label is Strict Out
	 */
	public boolean isStrictOut(String lbl) {
		return lbl.equals(Labels.STRICT_OUT);
	}
	
	/**
	 * checks if the label is considered Defeasible In
	 * @param lbl a label
	 * @return true if the label is Defeasible In
	 */
	public boolean isDefeasibleIn(String lbl) {
		return lbl.equals(Labels.DEFEASIBLE_IN);
	}
	
	/**
	 * checks if the label is considered Defeasible Out
	 * @param lbl a label
	 * @return true if the label is Defeasible Out
	 */
	public boolean isDefeasibleOut(String lbl) {
		return lbl.equals(Labels.DEFEASIBLE_OUT);
	}
	
	/**
	 * checks if the label is considered Ambiguous
	 * @param lbl a label
	 * @return true if the label is Ambiguous
	 */
	public boolean isAmbiguous(String lbl) {
		return lbl.equals(Labels.AMBIGUOUS);
	}
	
	/**
	 * checks if the label is considered Unsupported
	 * @param lbl a label
	 * @return true if the label is Unsupported
	 */
	public boolean isUnsupported(String lbl) {
		return lbl.equals(Labels.ASSUMED_OUT);
	}
}
