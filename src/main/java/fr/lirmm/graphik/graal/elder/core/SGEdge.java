package fr.lirmm.graphik.graal.elder.core;


public class SGEdge {
	private Statement source;
	private Assumption target;
	private boolean isAttack;
	
	private String label;
	
	private boolean isCountered; // if the support or attack edge is countered by another edge
	
	public SGEdge(Statement source, Assumption target, boolean isAttack) {
		this.source = source;
		this.target = target;
		this.isAttack = isAttack;
		this.isCountered = false;
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
	
	public boolean isCountered() {
		return this.isCountered;
	}
	
	public void isCountered(boolean isCountered) {
		this.isCountered = isCountered;
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
	
	/**
     * Verifies if two SGEdges are equivalent or not.
     * @param obj the object to test
     * @retrun true if the objects are equal, false otherwise.
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
}
