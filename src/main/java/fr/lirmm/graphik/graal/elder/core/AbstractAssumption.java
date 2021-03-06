package fr.lirmm.graphik.graal.elder.core;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public abstract class AbstractAssumption implements Assumption {
	private HashSet<SGEdge> supportEdges;
	private HashSet<SGEdge> attackEdges;
	private String label;
	
	private HashSet<String> authors;
	
	
	public AbstractAssumption() {
		this.supportEdges = new HashSet<SGEdge>();
		this.attackEdges = new HashSet<SGEdge>();
	}
	
	public AbstractAssumption(HashSet<String> authors) {
		this.authors = authors;
	}
	
	public String getLabel() {
		return this.label;
	}
	public void setLabel(String label) {
		this.label = label;
	}

	public HashSet<SGEdge> getSupportEdges() {
		return this.supportEdges;
	}

	public HashSet<SGEdge> getAttackEdges() {
		return this.attackEdges;
	}
	
	public void addAttackEdge(SGEdge edge) {
		if(!edge.isAttack()) {
			System.err.println("Trying to Add non Attack edge to attackingEdges list");
			return;
		}
		
		this.attackEdges.add(edge);
	}
	
	public void addSupportEdge(SGEdge edge) {
		if(edge.isAttack()) {
			System.err.println("Trying to Add Attack edge to SupportingEdges list");
			return;
		}
		this.supportEdges.add(edge);
	}
	
	public List<SGEdge> getIncomingEdges() {
		List<SGEdge> edges = new LinkedList<SGEdge>();
		edges.addAll(this.getAttackEdges());
		edges.addAll(this.getSupportEdges());
		return edges;
	}
	
	public HashSet<String> getAuthors() {
		return this.authors;
	}
	
	public void setAuthors(HashSet<String> authors) {
		this.authors = authors;
	}
}
