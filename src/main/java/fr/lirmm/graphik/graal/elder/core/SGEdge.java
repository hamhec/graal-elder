package fr.lirmm.graphik.graal.elder.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.lirmm.graphik.graal.elder.labeling.Labels;
import fr.lirmm.graphik.graal.elder.persistance.SGEdgeJSONRepresentation;

public class SGEdge {
	public static String ATTACK = "attack", SUPPORT = "support";
	
	private Statement source;
	private Assumption target;
	private String type;
	
	private String label;
	
	public SGEdge(Statement source, Assumption target, String type) {
		this.source = source;
		this.target = target;
		this.type = type;
	}
	
	public SGEdge(Statement source, Assumption target, String type, String label) {
		this(source, target, type);
		this.setLabel(label);
	}
	
	public Statement getSource() {
		return this.source;
	}
	
	public Assumption getTarget() {
		return this.target;
	}
	
	public boolean isAttack() {
		return this.type.equals(ATTACK);
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
		if(isAttack()) return this.source.toString() + " **Attack** " + this.target.toString();
				
		return this.source.toString();
	}
	
	public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.type == null) ? 0 : this.type.hashCode());
        result = prime * result + ((this.source == null) ? 0 : this.source.hashCode());
        result = prime * result + ((this.target == null) ? 0 : this.target.hashCode());
        
        return result;
    }
	
	public String getID(Statement target) {
        // we append 'id' due to HTML no liking ids starting with '-' (if they are negative).
		
		final int prime = 31;
        int result = 1;
        result = prime * result + ((this.type == null) ? 0 : this.type.hashCode());
        result = prime * result + ((this.source == null) ? 0 : this.source.hashCode());
        result = prime * result + ((target == null) ? 0 : target.hashCode());
        
        return "ID" + result;
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
    
    public SGEdgeJSONRepresentation getRepresentation(Statement targetStatement) {
    	SGEdgeJSONRepresentation rep = new SGEdgeJSONRepresentation();
    	rep.setId(this.getID(targetStatement));
    	rep.setSource(this.source.getID());
    	rep.setTarget(targetStatement.getID());
    	rep.setTargettedAssumption(this.target.toString());
    	rep.setType(this.type);
    	rep.setLabel(this.getLabel());
    	rep.setLabelString(Labels.toPrettyString(this.getLabel()));
    	return rep;
    }
    
  
    public String toJSONString(Statement targetStatement) {
    	ObjectMapper mapper = new ObjectMapper();
    	String json = "";
		try {
			json = mapper.writeValueAsString(this.getRepresentation(targetStatement));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
    	return json;
    }
}
