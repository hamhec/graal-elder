package fr.lirmm.graphik.graal.elder.core;

import fr.lirmm.graphik.graal.elder.labeling.Labels;
import fr.lirmm.graphik.graal.elder.persistance.PremiseJSONRepresentation;

/**
 * 
 * @author abidech
 * A Permise should be unique for each Atom. It encapsulates the Atom's label.
 */
public class Premise extends AbstractAssumption implements Comparable<Premise> {
	private String atom;

	public Premise(String atom) {
		super();
		this.atom = atom;
	}
	
	public Premise(String atom, String label) {
		this(atom);
		this.setLabel(label);
	}
	
	
	public String getAtom() {
		return this.atom;
	}
	
	
	public String toString() {
		return this.atom.toString();
	}
	
	public int hashCode() {
		if(this.getAtom() == null) {
			int i = 0;
		}
        return this.getAtom().hashCode();
    }
	
	/**
     * Verifies if two RuleApplications are equivalent or not.
     */
    public boolean equals(Object obj) {
        if (this == obj) { return true; }
        if (obj == null) { return false; }
        if (!(obj instanceof Premise)) { return false; }
        
        Premise other = (Premise) obj;
        // They must have the same Atom
        return (this.getAtom().equals(other.getAtom()));
    }
    
    
    public PremiseJSONRepresentation getRepresentation() {
    	PremiseJSONRepresentation rep = new PremiseJSONRepresentation();
    	rep.setAtom(this.atom);
    	rep.setLabel(this.getLabel());
    	rep.setLabelString(Labels.toPrettyString(this.getLabel()));
    	
    	return rep;
    }

	@Override
	public int compareTo(Premise o) {
		return this.atom.compareTo(o.getAtom());
	}
}
