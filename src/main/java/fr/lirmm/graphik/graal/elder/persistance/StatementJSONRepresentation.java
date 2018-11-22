package fr.lirmm.graphik.graal.elder.persistance;

import java.io.Serializable;
import java.util.List;

public class StatementJSONRepresentation implements Serializable {
	
	private String id;
	private String title;
	private String type;
	
	private RuleApplicationJSONRepresentation ruleApplication;
	private List<PremiseJSONRepresentation> premises;
	private String label;
	private String labelString;

	public StatementJSONRepresentation() {}
	
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	
	public RuleApplicationJSONRepresentation getRuleApplication() {
		return ruleApplication;
	}

	public void setRuleApplication(RuleApplicationJSONRepresentation ruleApplication) {
		this.ruleApplication = ruleApplication;
	}

	public List<PremiseJSONRepresentation> getPremises() {
		return premises;
	}

	public void setPremises(List<PremiseJSONRepresentation> premises) {
		this.premises = premises;
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
	
	
	
}
