package fr.lirmm.graphik.graal.elder.core;

import org.json.simple.JSONObject;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.defeasible.core.LogicalObjectsFactory;
import fr.lirmm.graphik.graal.defeasible.core.rules.DefeasibleRule;
import fr.lirmm.graphik.graal.defeasible.core.rules.DefeaterRule;
import fr.lirmm.graphik.graal.defeasible.core.rules.PreferenceRule;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.IteratorException;

public class RuleApplication extends AbstractAssumption {
	// Move this to the LogicalObjectsFactory if you need to use these distinctions elsewhere
	static final String STRICT = "strict", DEFEASIBLE = "defeasible", DEFEATER = "defeater", PREFERENCE_RULE = "preference";
	
	private String ruleLabel;
	private String type;
	private String title;
	private String generatedAtom;
	
	public RuleApplication(Rule rule, AtomSet instantiatedBody, Atom generatedAtom) {
		super();
		String implication = "";
		this.title = "";
		this.ruleLabel = rule.getLabel();
		this.generatedAtom = generatedAtom.toString();
		
		// find the type of the rule
		if(rule instanceof DefeasibleRule) {
			this.type = DEFEASIBLE;
			implication = "=>";
		} else if (rule instanceof DefeaterRule) {
			this.type = DEFEATER;
			implication = "~>";
		} else if (rule instanceof PreferenceRule) {
			this.type = PREFERENCE_RULE;
			implication = "->";
		} else {
			this.type = STRICT;
			implication = "->";
		}
		if(null != instantiatedBody) {
			// create a string representation of this rule application
			CloseableIterator<Atom> it = instantiatedBody.iterator();
			
			try {
				if(it.hasNext()) this.title += it.next().toString();
			
				while(it.hasNext()) {
					this.title += ", " + it.next().toString();
				}
			} catch (IteratorException e) {
				e.printStackTrace();
			}
		}
		this.title += " " + implication + " ";
		this.title += generatedAtom;
	}
	
	public RuleApplication(Atom fact) {
		this.generatedAtom = fact.toString();
		this.title = LogicalObjectsFactory.instance().getTOPAtom().toString() + " -> " + this.generatedAtom;
		this.type = STRICT;
		this.ruleLabel = "";
	}
	
	public RuleApplication(String ruleLabel, String generatedAtom, String title, String type, String label) {
		this.ruleLabel = ruleLabel;
		this.generatedAtom = generatedAtom;
		this.title = title;
		this.type = type;
		this.setLabel(label);
	}
	
	public String getGeneratedAtom() {
		return this.generatedAtom;
	}
	
	public String getRuleLabel() {
		return this.ruleLabel;
	}
	
	public boolean isDefeater() {
		return (this.type.equals(DEFEATER));
	}
	
	public boolean isDefeasible() {
		return (this.type.equals(DEFEASIBLE));
	}
	
	public boolean isPreferenceRule() {
		return (this.type.equals(PREFERENCE_RULE));
	}
	
	public boolean isStrict() {
		return (this.type.equals(STRICT));
	}
	
	
	public String toString() {	
		return this.title;
	}
	
	public int hashCode() {
        return this.title.hashCode();
    }
	
	/**
     * Verifies if two RuleApplications are equivalent or not.
     */
    public boolean equals(Object obj) {
        if (this == obj) { return true; }
        if (obj == null) { return false; }
        if (!(obj instanceof RuleApplication)) { return false; }
        
        RuleApplication other = (RuleApplication) obj;
        // They must have the same label
        if (!this.ruleLabel.equals(other.ruleLabel)) return false;
        // They must have the same title
        // TODO error prone! the order of the body atoms might change
        if (!this.title.equals(other.title)) return false;
        
        return true;
    }
    
    
    @SuppressWarnings("unchecked")
    public JSONObject toJSON() {
    	JSONObject json = new JSONObject();
    	json.put("generatedAtom", this.generatedAtom);
    	json.put("title", this.title);
    	json.put("type", this.type);
    	json.put("ruleLabel", this.ruleLabel);
    	json.put("label", this.getLabel());
    	
    	return json;
    }
}
