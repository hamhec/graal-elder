package fr.lirmm.graphik.graal.elder.labeling;

import fr.lirmm.graphik.graal.defeasible.core.DefeasibleKnowledgeBase;
import fr.lirmm.graphik.graal.elder.core.Premise;
import fr.lirmm.graphik.graal.elder.core.RuleApplication;
import fr.lirmm.graphik.graal.elder.core.SGEdge;
import fr.lirmm.graphik.graal.elder.core.Statement;
import fr.lirmm.graphik.graal.elder.preference.PreferenceFunction;
import fr.lirmm.graphik.graal.elder.preference.SimplePreferenceFunction;

public abstract class AbstractDefeasibleLogicLabelingFunction extends LabelingFunction {
	
	protected DefeasibleKnowledgeBase kb;
	protected PreferenceFunction preferenceFunction;
	
	
	public AbstractDefeasibleLogicLabelingFunction(DefeasibleKnowledgeBase kb) {
		this.kb = kb;
		this.preferenceFunction = new SimplePreferenceFunction(this.kb);
	}
	
	public AbstractDefeasibleLogicLabelingFunction(DefeasibleKnowledgeBase kb, PreferenceFunction pf) {
		this(kb);
		this.preferenceFunction = pf;
	}


	public String label(SGEdge edge) {
		if(edge.getLabel() != null) return edge.getLabel();
		Statement source = edge.getSource();
		if(source.getLabel() == null) this.label(source);
		
		edge.setLabel(source.getLabel());
		
		return edge.getLabel();
	}
	
	public PreferenceFunction getPreferenceFunction() {
		return this.preferenceFunction;
	}

}
