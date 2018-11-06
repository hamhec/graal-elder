package fr.lirmm.graphik.graal.elder.core;

import java.util.HashMap;
import java.util.HashSet;

public interface Assumption {
	public HashSet<SGEdge> getSupportEdges();
	public HashSet<SGEdge> getAttackEdges();
	public void addAttackEdge(SGEdge edge);
	public void addSupportEdge(SGEdge edge);
	
	public String getLabel();
	public void setLabel(String label);
}
