package fr.lirmm.graphik.graal.elder.labeling.defeasible.logic;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import fr.lirmm.graphik.graal.defeasible.core.preferences.Preference.Status;
import fr.lirmm.graphik.graal.elder.core.Premise;
import fr.lirmm.graphik.graal.elder.core.SGEdge;
import fr.lirmm.graphik.graal.elder.labeling.Labels;
import fr.lirmm.graphik.graal.elder.preference.PreferenceFunction;

public class DefeasibleLogicLabelingHelper {
	/**
	 * Indicates if this attack should be considered as it is ambiguity blocking
	 * @param attack
	 * @return true if the label of this attack has an impact on the semantics
	 */
	public static boolean ambiguityBlocking(SGEdge attack) {
		return (isStrictIn(attack.getLabel()) || isDefeasibleIn(attack.getLabel()));
	}
	
	/**
	 * Indicates if this attack should be considered as it is ambiguity propagating
	 * @param attack
	 * @return true if the label of this attack has an impact on the semantics
	 */
	public static boolean ambiguityPropagating(SGEdge attack) {
		return (isStrictIn(attack.getLabel()) || isDefeasibleIn(attack.getLabel()) || isAmbiguous(attack.getLabel()));
	}
	
	/**
	 * Uses Team Defeat to compute label given the surviving supports and attacks
	 * @param premise a premise to be labeled
	 * @param survivingSupports the surviving supports for this premise
	 * @param survivingAttacks the surviving attacks for this premise
	 * @return the label of the premise
	 */
	public static String withTeamDefeat(Premise premise, 
			List<SGEdge> survivingSupports, List<SGEdge> survivingAttacks, PreferenceFunction preferenceFunction) {
		
		if(survivingSupports.isEmpty()) { // if no surviving support then it's unsupported
			premise.setLabel(Labels.ASSUMED_OUT);
			return premise.getLabel();
		}
						
		// List of attacks that are challenged but not defeated
		LinkedList<SGEdge> counteredAttacks = new LinkedList<SGEdge>();
		Iterator<SGEdge> itSurvivingAttacks = survivingAttacks.iterator();
		// Filter ambig attacks from In attacks
				while(itSurvivingAttacks.hasNext()) {
					SGEdge surviving = itSurvivingAttacks.next();
					if(isAmbiguous(surviving.getLabel())) {
						counteredAttacks.add(surviving); // add it to surviving ambig attacks
						itSurvivingAttacks.remove(); // remove it from surviving In attacks
					}
				}
		
		
		// Check if In supports can kill any attack
		for(SGEdge support: survivingSupports) {
			Status pref = null; // preference status in order not to declare it twice
			SGEdge surviving = null; // in order not to declare it twice
			
			if(isDefeasibleIn(support.getLabel())) { 
				// Does this support kill any countered attacks?
				Iterator<SGEdge> itCounteredAttack = counteredAttacks.iterator();
				while(itCounteredAttack.hasNext()) {
					surviving = itCounteredAttack.next();
					pref = preferenceFunction.preferenceStatus(surviving, support);
					if(pref == Status.INFERIOR) itCounteredAttack.remove();
				}
				
				// Does this support kill or 'counter' any surviving Defeasible In attack?
				itSurvivingAttacks = survivingAttacks.iterator();
				while(itSurvivingAttacks.hasNext()) {
					surviving = itSurvivingAttacks.next();
					pref = preferenceFunction.preferenceStatus(surviving, support);
					if(pref == Status.INFERIOR) { // attack rule is inferior to support rule
						itSurvivingAttacks.remove(); // this attack is killed
					} else if (pref == Status.EQUAL) { // attack has been countered (challenged but not defeated)
						counteredAttacks.add(surviving); // add it to counteredAttacks
						itSurvivingAttacks.remove(); // remove it from surviving supports
					}
				}
				
			} else if(isAmbiguous(support.getLabel())) { // redundant test, but just to make sure the support is ambig
				// Does this support any surviving Defeasible In attack?
				itSurvivingAttacks = survivingAttacks.iterator();
				while(itSurvivingAttacks.hasNext()) {
					surviving = itSurvivingAttacks.next();
					pref = preferenceFunction.preferenceStatus(surviving, support);
					if(pref != Status.SUPERIOR) { // attack rule is not superior to ambig support rule
						counteredAttacks.add(surviving); // add it to counteredAttacks
						itSurvivingAttacks.remove(); // remove it from surviving supports
					}
				}
			}
		}
		
		if(!survivingAttacks.isEmpty()) { // there is a surviving In attack
			premise.setLabel(Labels.DEFEASIBLE_OUT);
			return premise.getLabel();
		} else if(!counteredAttacks.isEmpty()) { // no surviving In but there is countered attack that survives
			premise.setLabel(Labels.AMBIGUOUS);
			return premise.getLabel();
		} else { // no surviving In or countered attacks
			for(SGEdge support: survivingSupports) {
				if(isDefeasibleIn(support.getLabel())) { // if it has a In support then it is in
					premise.setLabel(Labels.DEFEASIBLE_IN);
					return premise.getLabel();
				}
			}
			// It has only ambig supports
			premise.setLabel(Labels.AMBIGUOUS);
			return premise.getLabel();
		}
	}
	
	/**
	 * Does not consider Team Defeat to compute label given the surviving supports and attacks
	 * @param premise a premise to be labeled
	 * @param survivingSupports the surviving supports for this premise
	 * @param survivingAttacks the surviving attacks for this premise
	 * @return the label of the premise
	 */
	public static String withoutTeamDefeat(Premise premise, 
			List<SGEdge> survivingSupports, List<SGEdge> survivingAttacks, PreferenceFunction preferenceFunction) {

		if(survivingSupports.isEmpty()) { // if no surviving support then it's unsupported
			premise.setLabel(Labels.ASSUMED_OUT);
			return premise.getLabel();
		}
		
		// List of IN support that are challenged but not defeated
		LinkedList<SGEdge> ambigSupports = new LinkedList<SGEdge>();
		Iterator<SGEdge> itSurvivingSup = survivingSupports.iterator();
		// Filter ambig supports from surviving supports
		while(itSurvivingSup.hasNext()) {
			SGEdge surviving = itSurvivingSup.next();
			if(isAmbiguous(surviving.getLabel())) {
				ambigSupports.add(surviving); // add it to surviving ambig supports
				itSurvivingSup.remove(); // remove it from surviving supports
			}
		}
		
		for(SGEdge attack: survivingAttacks) {
			Status pref = null; // preference status in order not to declare it twice
			SGEdge surviving = null; // in order not to declare it twice
			
			if(isDefeasibleIn(attack.getLabel())) {
				// Does this attack kill any ambig support?
				Iterator<SGEdge> itAmbigSup = ambigSupports.iterator();
				while(itAmbigSup.hasNext()) {
					surviving = itAmbigSup.next();
					pref = preferenceFunction.preferenceStatus(surviving, attack);
					if(pref == Status.INFERIOR) itAmbigSup.remove();
				}
				
				// Does this attack kill or 'counter' any surviving Defeasible In support?
				itSurvivingSup = survivingSupports.iterator();
				while(itSurvivingSup.hasNext()) {
					surviving = itSurvivingSup.next();
					pref = preferenceFunction.preferenceStatus(surviving, attack);
					if(pref == Status.INFERIOR) { // support rule is inferior to attack rule
						itSurvivingSup.remove(); // this support is killed
					} else if (pref == Status.EQUAL) { // support has been countered (challenged but not defeated)
						ambigSupports.add(surviving); // add it to ambig supports
						itSurvivingSup.remove(); // remove it from surviving supports
					}
				}
			} else if(isAmbiguous(attack.getLabel())) { // redundant test, but just to make sure
				// Does this attack kill 'counter' any surviving Defeasible In support?
				itSurvivingSup = survivingSupports.iterator();
				while(itSurvivingSup.hasNext()) {
					surviving = itSurvivingSup.next();
					pref = preferenceFunction.preferenceStatus(surviving, attack);
					if(pref != Status.SUPERIOR) { // support is not superior to ambig attack
						ambigSupports.add(surviving); // add it to ambig supports
						itSurvivingSup.remove(); // remove it from surviving supports
					}
				}
			}
		}
		
		if(!survivingSupports.isEmpty()) { // there is a surviving In support
			premise.setLabel(Labels.DEFEASIBLE_IN);
			return premise.getLabel();
		} else if(!ambigSupports.isEmpty()) { // no surviving In but there is ambig that survives
			premise.setLabel(Labels.AMBIGUOUS);
			return premise.getLabel();
		} else { // no surviving In or Ambig
			premise.setLabel(Labels.DEFEASIBLE_OUT);
			return premise.getLabel();
		}
	}
	
	/**
	 * checks if the label is considered OUT
	 * @param lbl a label
	 * @return true if the label is defeasible out, strict out, or assumed out
	 */
	public static boolean isOut(String lbl) {
		return (isStrictOut(lbl) || isDefeasibleOut(lbl) || isAmbiguous(lbl));
	}
	
	/**
	 * checks if the label is considered Strict In
	 * @param lbl a label
	 * @return true if the label is Strict In
	 */
	public static boolean isStrictIn(String lbl) {
		return lbl.equals(Labels.STRICT_IN);
	}
	
	/**
	 * checks if the label is considered Strict Out
	 * @param lbl a label
	 * @return true if the label is Strict Out
	 */
	public static boolean isStrictOut(String lbl) {
		return lbl.equals(Labels.STRICT_OUT);
	}
	
	/**
	 * checks if the label is considered Defeasible In
	 * @param lbl a label
	 * @return true if the label is Defeasible In
	 */
	public static boolean isDefeasibleIn(String lbl) {
		return lbl.equals(Labels.DEFEASIBLE_IN);
	}
	
	/**
	 * checks if the label is considered Defeasible Out
	 * @param lbl a label
	 * @return true if the label is Defeasible Out
	 */
	public static boolean isDefeasibleOut(String lbl) {
		return lbl.equals(Labels.DEFEASIBLE_OUT);
	}
	
	/**
	 * checks if the label is considered Ambiguous
	 * @param lbl a label
	 * @return true if the label is Ambiguous
	 */
	public static boolean isAmbiguous(String lbl) {
		return lbl.equals(Labels.AMBIGUOUS);
	}
	
	/**
	 * checks if the label is considered Unsupported
	 * @param lbl a label
	 * @return true if the label is Unsupported
	 */
	public static boolean isUnsupported(String lbl) {
		return lbl.equals(Labels.ASSUMED_OUT);
	}
}
