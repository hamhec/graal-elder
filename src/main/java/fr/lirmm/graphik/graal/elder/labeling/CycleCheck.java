package fr.lirmm.graphik.graal.elder.labeling;

import java.util.HashSet;
import java.util.Iterator;

import fr.lirmm.graphik.graal.elder.core.Statement;

/**
 * @author hamhec
 *
 */
public class CycleCheck {
	private boolean isSupportCycle;
	private boolean isCycle;
	private HashSet<Statement> bin;
	
	public CycleCheck() {
		this.isSupportCycle = true;
		this.isCycle = false;
		bin = new HashSet<Statement>();
	}
	
	public boolean isSupportCycle() {
		return this.isSupportCycle;
	}
	
	/**
	 * Add a statement to the cycle check given what type of edge it was linked through
	 * @param s the statement to add
	 * @param throughSupportEdge the type of edge the statement is linked to the previous one
	 */
	public void add(Statement s, boolean throughSupportEdge) {
		if(this.isSupportCycle && !throughSupportEdge) {
			this.isSupportCycle = false;
		}
		if(this.bin.contains(s)) {
			this.isCycle = true;
		} else {
			this.bin.add(s);
		}
	}
	
	public void remove(Statement s) {
		this.bin.remove(s);
	}
	
	public boolean isCycle() {
		return this.isCycle;
	}
	
	public boolean isResolved() {
		return this.bin.isEmpty();
	}
	
	public Iterator<Statement> iterator() {
		return this.bin.iterator();
	}
}
