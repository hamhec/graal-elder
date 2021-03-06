package fr.lirmm.graphik.graal.elder.core;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import fr.lirmm.graphik.graal.defeasible.core.Authorable;
import fr.lirmm.graphik.graal.defeasible.core.LogicalObjectsFactory;
import fr.lirmm.graphik.graal.elder.labeling.Labels;
import fr.lirmm.graphik.graal.elder.persistance.PremiseJSONRepresentation;
import fr.lirmm.graphik.graal.elder.persistance.StatementJSONRepresentation;

public class Statement implements Authorable {
	
	private RuleApplication ruleApplication;
	private List<Premise> premises;
	
	private String label;
	
	public Statement(RuleApplication ruleApplication, List<Premise> premises) {
		this.ruleApplication = ruleApplication;
		this.premises = premises;
		if(null != this.premises) {
			Collections.sort(this.premises);
		}
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
		return "ID" + this.hashCode();
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
    
    public StatementJSONRepresentation getRepresentation() {
    	StatementJSONRepresentation rep = new StatementJSONRepresentation();
    	// Add the rule application JSONObject
    	if(null == this.getRuleApplication()) {
    		rep.setRuleApplication(null);
    	} else {
    		rep.setRuleApplication(this.ruleApplication.getRepresentation());
    	}
    	// Add the premises
    	if(null == this.getPremises()) {
    		rep.setPremises(null);
    	} else {
    		
	    	List<PremiseJSONRepresentation> premises = new LinkedList<PremiseJSONRepresentation>();
	    	for(Premise p: this.getPremises()) {
	    		premises.add(p.getRepresentation());
	    	}
	    	rep.setPremises(premises);
    	}
    	
    	// Add label
    	rep.setLabel(this.getLabel());
    	rep.setLabelString(Labels.toPrettyString(this.getLabel()));
    	
    	// Add authors
    	rep.setAuthors(this.getAuthors());
    	
    	// Display information
    	rep.setId(this.getID());
    	rep.setTitle(this.toString());
    	
    	String type = "";
    	if(this.isFactStatement()) {
    		type = "fact";
    	} else if(this.isClaimStatement()) {
    		type = "claim";
    	} else {
    		type = "statement";
    	}
    	rep.setType(type);
    	
    	return rep;
    }
    
    
    public HashSet<String> getAuthors() {
    	HashSet<String> authors = new HashSet<String>();
    	if(null != this.ruleApplication && null != this.ruleApplication.getAuthors()) {
    		authors.addAll(this.ruleApplication.getAuthors());
    	}
    	if(null != this.premises) {
    		for(Premise p: this.premises) {
    			if(null != p && null != p.getAuthors()) {
    				authors.addAll(p.getAuthors());
    			}
    		}
    	}
    	return authors;
    }
    
    public void setAuthors(HashSet<String> authors) {}
}
