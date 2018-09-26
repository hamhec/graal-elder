package fr.lirmm.graphik.graal.elder.core;

import java.util.Iterator;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.Term;

/**
 * 
 * @author abidech
 * A Permise should be unique for each Atom. It encapsulates the Atom's label.
 */
public class Premise extends AbstractAssumption {
	private Atom atom;

	public Premise(Atom atom) {
		super();
		this.atom = atom;
	}
	
	public Premise(Atom atom, String label) {
		this(atom);
		this.setLabel(label);
	}
	
	
	public Atom getAtom() {
		return this.atom;
	}
	
	
	public String toString() {
		return this.atom.toString();
	}
	
	public String toPrettyString() {
		return prettyPrint(this.atom);
	}
	
	public int hashCode() {
        return this.getAtom().hashCode();
    }
	
	public static String prettyPrint(Atom atom) {
		String str = atom.getPredicate().getIdentifier().toString();
		Iterator<Term> it = atom.getTerms().iterator();
		str += "(" + it.next();
		while(it.hasNext()) {
			str += ", " + it.next();
		}
		return str + ")";
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
}
