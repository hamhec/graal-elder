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
	private String rule;
	private String type;
	private String title;
	private String generatedAtom;
	
	public RuleApplication(Rule rule, AtomSet instantiatedBody, Atom generatedAtom) {
		super();
		String implication = "";
		this.title = "";
		this.ruleLabel = rule.getLabel();
		this.rule = rule.toString(); //TODO implement your own toString on rules
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
		this.rule = this.title;
	}
	
	public RuleApplication(String ruleLabel, String rule, String generatedAtom, String title, String type, String label) {
		this.ruleLabel = ruleLabel;
		this.rule = rule;
		this.generatedAtom = generatedAtom;
		this.title = title;
		this.type = type;
		this.setLabel(label);
	}
	
	
	
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
        //if (!this.ruleLabel.equals(other.ruleLabel)) return false;
        // They must have the same title

        if(!other.getType().equals(this.type)) return false;
        String spliter = "";
        if(this.type.equals(STRICT) || this.type.equals(PREFERENCE_RULE)) {
        	spliter = "->";
        } else if (this.type.equals(DEFEASIBLE)) {
        	spliter = "=>";
        } else {
        	spliter = "~>";
        }
        
        String otherTitle = other.getTitle().replace('.', ' ').trim();
        String[] otherBH = otherTitle.split(spliter);
        String[] otherB = otherBH[0].split(",");
        String[] otherH = otherBH[1].split(",");
        
        String title = this.getTitle().replace('.', ' ').trim();
        String[] meBH = title.split(spliter);
        String[] meB = meBH[0].split(",");
        String[] meH = meBH[1].split(",");
        
        
        // Test body is same
        if(!this.contains(meB, otherB)) return false;
        if(!this.contains(otherB, meB)) return false;
        // Test head is same
        if(!this.contains(meH, otherH)) return false;
        if(!this.contains(otherH, meH)) return false;
        
        return true;
    }
    
    
    @SuppressWarnings("unchecked")
    public JSONObject toJSON() {
    	JSONObject json = new JSONObject();
    	json.put("generatedAtom", this.generatedAtom);
    	json.put("title", this.title);
    	json.put("type", this.type);
    	json.put("ruleLabel", this.ruleLabel);
    	json.put("rule", this.rule);
    	json.put("label", this.getLabel());
    	
    	
    	return json;
    }
    
    private boolean contains(String[] child, String[] parent) {
    	for(int i = 0; i < child.length; i++) {
        	boolean exists = false;
        	String a = child[i].trim();
        	for(int j = 0; j < parent.length; j++) {
        		String b = parent[j].trim();
        		if(b.equals(a)) {
        			exists = true;
        			break;
        		}
        	}
        	if(!exists) {
        		return false;
        	}
        }
    	return true;
    }
}
