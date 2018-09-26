package fr.lirmm.graphik.graal.elder.core;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import fr.lirmm.graphik.graal.defeasible.core.LogicalObjectsFactory;

public class Statement {
	private RuleApplication ruleApplication;
	private List<Premise> premises;
	
	private String label;
	
	private HashSet<SGEdge> outgoingAttackEdges;
	private HashSet<SGEdge> outgoingSupportEdges;
	
	public Statement(RuleApplication ruleApplication, List<Premise> premises, HashSet<SGEdge> outgoingAttackEdges, HashSet<SGEdge> outgoingSupportEdges) {
		this.ruleApplication = ruleApplication;
		this.premises = premises;
		this.outgoingAttackEdges = outgoingAttackEdges;
		this.outgoingSupportEdges = outgoingSupportEdges;
		this.label = null;
	}
	
	public Statement(RuleApplication ruleApplication, List<Premise> premises) {
		this(ruleApplication, premises, new HashSet<SGEdge>(), new HashSet<SGEdge>());
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
	
	public void addOutgoingAttackEdge(SGEdge edge) {
		this.outgoingAttackEdges.add(edge);
	}
	public HashSet<SGEdge> getOutgoingAttackEdges() {
		return this.outgoingAttackEdges;
	}
	public void addOutgoingSupportEdge(SGEdge edge) {
		this.outgoingSupportEdges.add(edge);
	}
	public HashSet<SGEdge> getOutgoingSupportEdges() {
		return this.outgoingSupportEdges;
	}
	
	
	
	
	public String toString() {
		String str = "[";
		if(this.getPremises() != null) {
			for(Premise prem : this.getPremises()) {
				str += prem.toString() + ",";
			}
		}
		str += "] ";
		if(this.getRuleApplication() == null) return str;
		if(this.getRuleApplication().isStrict()) {
			str += "-> ";
		} else if(this.getRuleApplication().isDefeasible()) {
			str += "=> ";
		} else {
			str += "~> ";
		}
		str += this.getRuleApplication().getGeneratedAtom();
		return str;
	}
	
	public String toPrettyString() {
		String str = "";
		if(this.getPremises() != null) {
			Iterator<Premise> it = this.getPremises().iterator();
			if(it.hasNext()) str += it.next().toPrettyString();
			while(it.hasNext()) {
				str += ", " + it.next().toPrettyString();
			}
		}
		if(this.getRuleApplication() == null) return str;
		if(this.getRuleApplication().isStrict()) {
			str += " -> ";
		} else if(this.getRuleApplication().isDefeasible()) {
			str += " => ";
		} else {
			str += " ~> ";
		}
		str += Premise.prettyPrint(this.getRuleApplication().getGeneratedAtom());
		return str;
	}
	
	public int hashCode() {
        final int prime = 31;
        int result = 2;
        result = prime * result + ((this.getRuleApplication() == null) ? 0 : this.getRuleApplication().hashCode());
        
        if(this.getPremises() == null) {
        	return prime * result;
        }
        
        if(this.getPremises().isEmpty()) return result;
        
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
    	
    	return (this.premises.iterator().next().getAtom().equals(LogicalObjectsFactory.instance().getTOPAtom()));
    }
    /**
     * Checks if this statement is a Claim statement
     * @return true if this statement is a claim statement
     */
    public boolean isClaimStatement() {
    	
    	return (this.ruleApplication == null);
    }
}
