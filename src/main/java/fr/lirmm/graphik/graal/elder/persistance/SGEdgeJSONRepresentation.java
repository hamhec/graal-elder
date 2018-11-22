package fr.lirmm.graphik.graal.elder.persistance;

import java.io.Serializable;

import fr.lirmm.graphik.graal.elder.core.SGEdge;

public class SGEdgeJSONRepresentation implements Serializable {
	
	private String id;
	private String source;
	private String target;
	private String targettedAssumption;
	private String type;
	private String label;
	private String labelString;
	
	public SGEdgeJSONRepresentation() {}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}
	
	public String getTargettedAssumption() {
		return targettedAssumption;
	}

	public void setTargettedAssumption(String targettedAssumption) {
		this.targettedAssumption = targettedAssumption;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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
