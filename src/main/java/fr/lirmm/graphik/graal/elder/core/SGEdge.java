package fr.lirmm.graphik.graal.elder.core;

import org.json.simple.JSONObject;

public class SGEdge {
	private Statement source;
	private Assumption target;
	private boolean isAttack;
	
	private String label;
	
	public SGEdge(Statement source, Assumption target, boolean isAttack) {
		this.source = source;
		this.target = target;
		this.isAttack = isAttack;
	}
	
	public Statement getSource() {
		return this.source;
	}
	
	public Assumption getTarget() {
		return this.target;
	}
	
	public boolean isAttack() {
		return this.isAttack;
	}
	
	public String getLabel() {
		return this.label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public boolean targetIsRuleApplication() {
		return (this.target instanceof RuleApplication);
	}
	
	
	public String toString() {
		if(this.isAttack) return this.source.toString() + " **Attack** " + this.target.toString();
				
		return this.source.toString();
	}
	
	public int hashCode() {
        final int prime = 31;
        int result = 1;
        //result = prime * result + ((this.isAttack) ? 0 : 1);
        result = prime * result + ((this.source == null) ? 0 : this.source.hashCode());
        result = prime * result + ((this.target == null) ? 0 : this.target.hashCode());
        
        return result;
    }
	
	public int getID(Statement target) {
		final int prime = 31;
        int result = 1;
        //result = prime * result + ((this.isAttack) ? 0 : 1);
        result = prime * result + ((this.source == null) ? 0 : this.source.hashCode());
        result = prime * result + ((this.target == null) ? 0 : target.hashCode());
        
        return result;
	}
	
	/**
     * Verifies if two SGEdges are equivalent or not.
     * @param obj the object to test
     * @return true if the objects are equal, false otherwise.
     */
    public boolean equals(Object obj) {
        if (this == obj) { return true; }
        if (obj == null) { return false; }
        if (!(obj instanceof SGEdge)) { return false; }
        
        SGEdge other = (SGEdge) obj;
        // They must have the same source
        if (this.getSource() == null) {
            if (other.getSource() != null) { return false; }
        }
        else if (!this.getSource().equals(other.getSource())) { return false; }
        
        // They must have the same target
        if (this.getTarget() == null) {
            if (other.getTarget() != null) { return false; }
            else return true;
        }
        else if (!this.getTarget().equals(other.getTarget())) { return false; }
        
        return true;
    }
    
    @SuppressWarnings("unchecked")
    public JSONObject toJSON(Statement targetStatement) {
    	JSONObject json = new JSONObject();
    	// we append 'id' to avoid negative ids
    	json.put("id", "id" + this.getID(targetStatement));
    	json.put("source", this.source.getID());
    	// if it is attacking a rule application then we need to store the statement
    	// TODO: explain why do we need that?
    	String target = (this.targetIsRuleApplication()) ? targetStatement.getID() : this.target.toString();
    	json.put("target", target);
    	json.put("label", this.getLabel());
    	
    	json.put("type", this.isAttack);
    	return json;
    }
    
    @SuppressWarnings("unchecked")
    public JSONObject toViewJSON(Statement target) {
    	JSONObject json = new JSONObject();
    	// we append 'id' due to HTML no liking ids starting with '-' (if they are negative).
    	json.put("id", "id" + this.getID(target));
    	json.put("source", this.source.getID());
    	json.put("target", target.getID());

    	String label = (this.getLabel() != null) ? this.getLabel() : "";
    	json.put("label", label);
    	
    	String type = (this.isAttack == true) ? "attack" : "support";
    	json.put("type", type);
    	
    	return json;
    }
}
