package fr.lirmm.graphik.graal.elder.persistance;

import java.io.Serializable;

import fr.lirmm.graphik.graal.elder.core.RuleApplication;

public class RuleApplicationJSONRepresentation implements Serializable {
	
	private String ruleLabel;
	private String rule;
	private String type;
	private String title;
	private String generatedAtom;
	private String label;
	private String labelString;
	
	public RuleApplicationJSONRepresentation() {}

	public String getRuleLabel() {
		return ruleLabel;
	}

	public void setRuleLabel(String ruleLabel) {
		this.ruleLabel = ruleLabel;
	}

	public String getRule() {
		return rule;
	}

	public void setRule(String rule) {
		this.rule = rule;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getGeneratedAtom() {
		return generatedAtom;
	}

	public void setGeneratedAtom(String generatedAtom) {
		this.generatedAtom = generatedAtom;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLabelString() {
		return labelString;
	}

	public void setLabelString(String labelString) {
		this.labelString = labelString;
	}


	public RuleApplication inflate() {
		return new RuleApplication(this.getRuleLabel(), this.getRule(),
				this.getGeneratedAtom(), this.getTitle(), this.getType(), this.getLabel());
	}
	
	
}
