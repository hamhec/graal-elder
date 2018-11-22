package fr.lirmm.graphik.graal.elder.persistance;

import java.io.Serializable;

import fr.lirmm.graphik.graal.elder.core.Premise;

public class PremiseJSONRepresentation implements Serializable {
	
	private String atom;
	private String label;
	private String labelString;
	
	public PremiseJSONRepresentation() {}

	public String getAtom() {
		return atom;
	}

	public void setAtom(String atom) {
		this.atom = atom;
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

	
	public Premise inflate() {
		return new Premise(this.getAtom(), this.getLabel());
	}
	
}
