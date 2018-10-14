package fr.lirmm.graphik.graal.elder.core;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import fr.lirmm.graphik.graal.defeasible.core.LogicalObjectsFactory;

public class Statement {
	
	private RuleApplication ruleApplication;
	private List<Premise> premises;
	
	private String label;
	
	public Statement(RuleApplication ruleApplication, List<Premise> premises) {
		this.ruleApplication = ruleApplication;
		this.premises = premises;
		this.label = null;
	}
	
	public String getLabel() {
		return this.label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	
	public RuleApplication getRuleApplication() {
		return this.ruleApplication;
	}
	public List<Premise> getPremises() {
		return this.premises;
	}
	
	
	public String toString() {
		String str = "";
		if(null != this.getRuleApplication()) {
			return this.getRuleApplication().toString();
		} else {
			Iterator<Premise> it = this.getPremises().iterator();
			if(it.hasNext()) str += it.next().toString();
			while(it.hasNext()) {
				str += ", " + it.next().toString();
			}
		}
		return str;
	}
	
	public String getID() {
		return String.valueOf(this.hashCode());
	}
	
	public int hashCode() {
        final int prime = 31;
        int result = 2;
        result = prime * result + ((this.getRuleApplication() == null) ? 0 : this.getRuleApplication().hashCode());
        
        if(this.getPremises() == null) {
        	return prime * result;
        }
        
        for(Premise prem : this.getPremises()) {
        	result = prime * result + ((prem == null) ? 0 : prem.hashCode());
        }
        return result;
    }
	
	/**
     * Verifies if two Statements are equivalent or not.
     */
    public boolean equals(Object obj) {
        if (this == obj) { return true; }
        if (obj == null) { return false; }
        if (!(obj instanceof Statement)) { return false; }
        
        Statement other = (Statement) obj;
        // They must have the same Rule
        if (this.getRuleApplication() == null) {
            if (other.getRuleApplication() != null) { return false; }
        }
        else if (!this.getRuleApplication().equals(other.getRuleApplication())) { return false; }
        
        // They must have the same Premises
        if (this.getPremises().isEmpty()) {
            if (!other.getPremises().isEmpty()) { return false; }
            else return true;
        }
        boolean exists = false;
        Iterator<Premise> itPrems = this.getPremises().iterator();
        while(itPrems.hasNext()) {
        	Premise prem1 = itPrems.next();
        	for(Premise prem : other.getPremises()) {
        		if(prem.equals(prem1)) {
        			exists = true; break;
        		}
        	}
        	if(!exists) return false;
        	else exists = false;
        }
        return true;
    }
    
    /**
     * Checks if this statement is the TOP statement
     * @return true if this statement is TOP statement
     */
    public boolean isTopStatement() {
    	return (this.premises == null);
    }
    /**
     * Checks if this statement is a fact statement
     * @return true if this statement is a fact statement
     */
    public boolean isFactStatement() {
    	if(this.premises == null) return false;
    	return (this.premises.iterator().next().getAtom().equals(LogicalObjectsFactory.instance().getTOPAtom().toString()));
    }
    /**
     * Checks if this statement is a Claim statement
     * @return true if this statement is a claim statement
     */
    public boolean isClaimStatement() {
    	return (this.ruleApplication == null);
    }
    
    
    public List<SGEdge> getIncomingEdges() {
    	List<SGEdge> edges = new LinkedList<SGEdge>();
    	
    	if(this.getRuleApplication() != null) {
    		edges.addAll(this.getRuleApplication().getIncomingEdges());
		}
		
		if(this.getPremises() != null) {
			for(Premise p : this.getPremises()) {
				edges.addAll(p.getIncomingEdges());
			}	
		}
    	
    	return edges;
    }
    
    @SuppressWarnings("unchecked")
    public JSONObject toJSON() {
    	JSONObject json = new JSONObject();
    	// Add the rule application JSONObject
    	if(null == this.getRuleApplication()) {
    		json.put("ruleApplication", null);
    	} else {
    		json.put("ruleApplication", this.getRuleApplication().toJSON());
    	}
    	// Add the premises
    	if(null == this.getPremises()) {
    		json.put("premises", null);
    	} else {
	    	JSONArray premises = new JSONArray();
	    	for(Premise p: this.getPremises()) {
	    		premises.add(p.toJSON());
	    	}
	    	json.put("premises", premises);
    	}
    	
    	// Add label
    	json.put("label", this.getLabel());
    	
    	return json;
    }
    
    @SuppressWarnings("unchecked")
    public JSONObject toViewJSON() {
    	JSONObject json = new JSONObject();
    	json.put("id", "" + this.hashCode());
    	json.put("title", this.toString());
    	
    	String type = "";
    	if(this.isFactStatement()) {
    		type = "fact";
    	} else if(this.isClaimStatement()) {
    		type = "claim";
    	} else {
    		type = "statement";
    	}
    	json.put("type", type);
    	// Add label
    	String label = (this.getLabel() != null) ? this.getLabel() : "";
    	json.put("label", label);
    	
    	return json;
    }
}
