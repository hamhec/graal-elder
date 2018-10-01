package fr.lirmm.graphik.graal.elder.core;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.defeasible.core.rules.DefeasibleRule;
import fr.lirmm.graphik.graal.defeasible.core.rules.DefeaterRule;
import fr.lirmm.graphik.graal.defeasible.core.rules.PreferenceRule;

public class RuleApplication extends AbstractAssumption {
	private Rule rule;
	private Substitution substitution;
	private Atom generatedAtom;
	
	public RuleApplication(Rule rule, Substitution substitution, Atom generatedAtom) {
		super();
		this.rule = rule;
		this.substitution = substitution;
		this.generatedAtom = generatedAtom;
	}
	
	public Atom getGeneratedAtom() {
		return this.generatedAtom;
	}
	
	public Rule getRule() {
		return this.rule;
	}
	
	public Substitution getSubstitution() {
		return this.substitution;
	}
	
	public boolean isDefeater() {
		return (this.rule instanceof DefeaterRule);
	}
	public boolean isDefeasible() {
		return (this.rule instanceof DefeasibleRule);
	}
	public boolean isPreferenceRule() {
		return (this.rule instanceof PreferenceRule);
	}
	public boolean isStrict() {
		return (!this.isDefeasible() && !this.isDefeater() && !this.isPreferenceRule());
	}
	
	
	public String toString() {
		String str = "[";
		if (this.getRule() != null) {
			str += this.getRule().toString();
		}
		
		if (this.getGeneratedAtom() != null)
			str += "] ==> " + this.getGeneratedAtom().toString();
		
		return str;
	}
	
	public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.getRule() == null) ? 0 : this.getRule().hashCode());
        result = prime * result + ((this.getSubstitution() == null) ? 0 : this.getSubstitution().hashCode());
        result = prime * result + ((this.getGeneratedAtom() == null) ? 0 : this.getGeneratedAtom().hashCode());
        
        if(this.isDefeasible()) {
        	result = prime * result + 1;
        } else if(this.isDefeater()) {
        	result = prime * result + 2;
        } else {
        	result = prime * result + 3;
        }
        return result;
    }
	
	/**
     * Verifies if two RuleApplications are equivalent or not.
     */
    public boolean equals(Object obj) {
        if (this == obj) { return true; }
        if (obj == null) { return false; }
        if (!(obj instanceof RuleApplication)) { return false; }
        
        RuleApplication other = (RuleApplication) obj;
        // They must have the same Rule
        if (this.getRule() == null) {
            if (other.getRule() != null) { return false; }
        }
        else if (!this.getRule().equals(other.getRule())) { return false; }
        else if (this.isDefeasible() && !other.isDefeasible()) { return false; }
        else if (this.isDefeater() && !other.isDefeater()) { return false; }
        
        // They must use the same Homomorphism
        if (this.getSubstitution() == null) {
            if (other.getSubstitution() != null) { return false; }
        } else if (!this.getSubstitution().equals(other.getSubstitution())) { return false; }
        
        return true;
    }
}
