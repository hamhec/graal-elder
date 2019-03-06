package fr.lirmm.graphik.graal.elder.core;

import java.util.HashSet;

import fr.lirmm.graphik.graal.defeasible.core.Authorable;

public interface Assumption extends Authorable {
	public HashSet<SGEdge> getSupportEdges();
	public HashSet<SGEdge> getAttackEdges();
	public void addAttackEdge(SGEdge edge);
	public void addSupportEdge(SGEdge edge);
	
	public String getLabel();
	public void setLabel(String label);
}
