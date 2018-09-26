package fr.lirmm.graphik.graal.elder.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseException;
import fr.lirmm.graphik.graal.api.forward_chaining.RuleApplier;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.core.DefaultConjunctiveQuery;
import fr.lirmm.graphik.graal.core.DefaultRule;
import fr.lirmm.graphik.graal.core.atomset.graph.DefaultInMemoryGraphStore;
import fr.lirmm.graphik.graal.defeasible.core.DefeasibleKnowledgeBase;
import fr.lirmm.graphik.graal.defeasible.core.io.DlgpDefeasibleParser;
import fr.lirmm.graphik.graal.homomorphism.SmartHomomorphism;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.CloseableIteratorWithoutException;
import fr.lirmm.graphik.util.stream.IteratorException;

public class StatementGraph {
	private HashMap<Statement, Statement> statements;
	private HashMap<Atom, Premise> premises;
	private HashMap<Atom, List<Statement>> statementsForAtom;
	private HashMap<Atom, List<Atom>> conflictingAtoms;
	
	
	private LabelingFunction labelingFunction;
	private DefeasibleKnowledgeBase kb;
	
	private Statement topStatement;
	private HashMap<Statement, Statement> statementsOfQueries;
	
	// ------------------------------------------------------------------------
	// CONSTRUCTORS
	// ------------------------------------------------------------------------
	/**
	 * Creates an EDG given a KB and a labelingFunction
	 * @param kb The defeasible Knowledge Base
	 * @param labelingFunction LabelingFunction
	 */
	public StatementGraph(DefeasibleKnowledgeBase kb, LabelingFunction labelingFunction) {
		this.labelingFunction = labelingFunction;
		this.kb = kb;
		this.statements = new HashMap<Statement, Statement>();
		this.premises = new HashMap<Atom, Premise>();
		this.statementsForAtom = new HashMap<Atom, List<Statement>>();
		
		this.conflictingAtoms = new HashMap<Atom, List<Atom>>();
		
		this.statements = new HashMap<Statement, Statement>();
		this.statementsOfQueries = new HashMap<Statement, Statement>();
		
		this.createTOPStatement();
	}
	/**
	 * Creates an EDG given a KB
	 * @param kb The defeasible Knowledge Base
	 */
	public StatementGraph(DefeasibleKnowledgeBase kb) {
		this(kb, new BDL(kb));
	}
	
	// ------------------------------------------------------------------------
	// GETTERS & SETTERS
	// ------------------------------------------------------------------------
	public LabelingFunction getLabelingFunction() {
		return this.labelingFunction;
	}
	public void setLabelingFunction(LabelingFunction labelingFunction) {
		this.labelingFunction = labelingFunction;
	}
	/**
	 * Return the defeasible knowledge base used to construct this EDG.
	 * @return The defeasible Knowledge Base
	 */
	public DefeasibleKnowledgeBase getKB() {
		return this.kb;
	}
	/**
	 * Returns the TOP Statement.
	 * @return the Top statement.
	 */
	public Statement getTOPStatement() {
		return this.topStatement;
	}
	/**
	 * Returns a collection containing all statements of this EDG
	 * @return List of all statements
	 */
	public List<Statement> getAllStatements() {
		List<Statement> statements = new LinkedList<Statement>();
		statements.addAll(this.statements.values());
		statements.add(this.topStatement);
		statements.addAll(this.statementsOfQueries.values());
		
		return statements;
	}
	public Collection<Premise> getAllPremises() {
		return this.premises.values();
	}
	/**
	 * Returns All support edges in this EDG
	 * @return List of support edges in this EDG
	 */
	public List<EDGEdge> getSupportEdges() {
		List<EDGEdge> edges = new LinkedList<EDGEdge>();
		Iterator<Statement> itStatements = this.getAllStatements().iterator();
		while(itStatements.hasNext()) {
			Statement statement = itStatements.next();
			edges.addAll(statement.getOutgoingSupportEdges());
		}
		return edges;
	}
	/**
	 * Returns All attack edges in this EDG
	 * @return List of all attack edges in this EDG
	 */
	public List<EDGEdge> getAttackEdges() {
		List<EDGEdge> edges = new LinkedList<EDGEdge>();
		Iterator<Statement> itStatements = this.getAllStatements().iterator();
		while(itStatements.hasNext()) {
			Statement statement = itStatements.next();
			edges.addAll(statement.getOutgoingAttackEdges());
		}
		return edges;
	}
	
	// ------------------------------------------------------------------------
	// PUBLIC METHODS
	// ------------------------------------------------------------------------
	/**
	 * Returns the premise representing the atom, or creates it if necessary.
	 * @param atom Atom
	 * @return Premise Premise of the atom
	 */
	public Premise getOrCreatePremiseOfAtom(Atom atom) {
		Premise premise = this.premises.get(atom);
		if(null == premise) { // if no premise already exists for that atom then create a new one and add it.
			premise = new Premise(atom);
			this.premises.put(atom, premise);
		}
		return premise;
	}
	/**
	 * Returns the statement representing a rule application on a specific list of premises.
	 * @param ruleApplication RuleApplication
	 * @param premises Premises
	 * @return statement
	 */
	public Statement getStatement(RuleApplication ruleApplication, List<Premise> premises) {
		Statement s1 = new Statement(ruleApplication, premises);
		Statement statement = this.statements.get(s1);
		if(null == statement) {
			statement = s1;
			this.addStatement(statement);
		}
		return statement;
	}
	/**
	 * Returns a list of statements that supports the atom.
	 * @param atom Atom
	 * @return list of statements that support an atom.
	 */
	public List<Statement> getStatementsForAtom(Atom atom) {
		return this.statementsForAtom.get(atom);
	}
	/**
	 * Returns a list Premises representing a set of atoms. 
	 * @param atoms Atoms
	 * @return List of All premises
	 * @throws IteratorException Something went wrong
	 */
	public List<Premise> getPremisesForAtoms(AtomSet atoms) throws IteratorException {
		List<Premise> premises = new LinkedList<Premise>();
		CloseableIterator<Atom> itAtoms = atoms.iterator();
		while(itAtoms.hasNext()) {
			Atom atom = itAtoms.next();
			premises.add(this.getOrCreatePremiseOfAtom(atom));
		}
		return premises;
	}
	
	
	public void addStatementForRuleApplication(AtomSet body, Atom head, Rule rule, Substitution substitution) throws IteratorException {
		// Add the head as a Premise if it doesn't already exists
		this.getOrCreatePremiseOfAtom(head);
		// Add statement
		List<Premise> premises = this.getPremisesForAtoms(body);
		if(premises.isEmpty()) { // body is top statement;
			premises.add(this.getOrCreatePremiseOfAtom(this.getTOPStatement().getRuleApplication().getGeneratedAtom()));
		}
		RuleApplication ruleApplication = new RuleApplication(rule, substitution, head);
		Statement statement = new Statement(ruleApplication, premises);
		this.addStatement(statement);
	}
	/**
	 * Constructs the EDG by creating the statements and the edges
	 * @throws IteratorException Something Went Wrong
	 * @throws ChaseException Something Went Wrong
	 * @throws AtomSetException Something Went Wrong
	 * @throws HomomorphismException Something Went Wrong
	 */
	public void build() throws IteratorException, ChaseException, AtomSetException, HomomorphismException {
		// Build Statements for Fact (they are Strict Facts by default)
		this.createFactStatements();
		// Build Statements for each rule application
		this.kb.staturate(this.getRuleApplier());
		// Add the Support links between Statements
		this.generateSupportEdges();
		// Add the Attack links between Statements
		this.generateAttackEdges();
	}
	
	/**
	 * Reinitializes the labels on the statements and premises,
	 * needs to be done if when using a new labeling function.
	 */
	public void reinitializeLabels() {
		// Remove Query history
		this.statementsOfQueries.clear();
		// reinitialize Statements' labels
		Iterator<Statement> itStatements = this.statements.values().iterator();
		while(itStatements.hasNext()) {
			itStatements.next().setLabel(null);
		}
		// reinitialize Premises' Labels
		Iterator<Premise> itPremises = this.premises.values().iterator();
		while(itPremises.hasNext()) {
			itPremises.next().setLabel(null);
		}
	}
	
	/**
	 * Returns the Entailment Status of a Ground atom.
	 * @param atom Ground atom
	 * @return String representing the entailment status of the ground atom.
	 */
	public String groundAtomicQuery(Atom atom) {
		// TODO
		this.getOrCreatePremiseOfAtom(atom);
		return null;
	}

	/**
	 * Returns the Entailment Status of a conjunction of ground atoms represented in a string.
	 * @param atomsString Atom String
	 * @return String representing the entailment status of the ground atom.
	 * @throws IteratorException Something went wrong.
	 * @throws AtomSetException Something Went Wrong
	 */
	public String groundQuery(String atomsString) throws IteratorException, AtomSetException {
		CloseableIterator<Atom> itParsedAtoms = DlgpDefeasibleParser.parseAtomSet(atomsString);
		AtomSet atoms = new DefaultInMemoryGraphStore();
		
		while(itParsedAtoms.hasNext()) {
			atoms.add(itParsedAtoms.next());
		}
		return this.groundQuery(atoms);
	}
	/**
	 * Returns the Entailment Status of a set of atoms (considered as a conjunctive query).
	 * @param atoms Atoms
	 * @return String representing the entailment status of the ground atom.
	 * @throws IteratorException Something Went wrong
	 */
	public String groundQuery(AtomSet atoms) throws IteratorException {
		Statement s = this.createClaimStatement(atoms);
		return this.computeLabel(s);
	}
	
	/**
	 * Computes the label of a Premise
	 * @param premise Input premise
	 * @return label of the premise
	 */
	public String computeLabel(Premise premise) {
		if(premise.getLabel() == null) 
			this.getLabelingFunction().label(premise);
		return premise.getLabel();
	}
	/**
	 * Computes the label of an EDGEdge
	 * @param edge Input EDGEdge
	 * @return label of the edge
	 */
	public String computeLabel(EDGEdge edge) {
		if(edge.getLabel() == null) 
			this.getLabelingFunction().label(edge);
		return edge.getLabel();
	}
	/**
	 * Computes the label of a statement
	 * @param s Input statement
	 * @return label of the statement
	 */
	public String computeLabel(Statement s) {
		if(s.getLabel() == null) 
			this.getLabelingFunction().label(s);
		return s.getLabel();
	}
	
	/**
	 * Computes the label for a set of atoms
	 * @param atomSet conjunctive query
	 * @return Label of the claim statement for the conjunctive query
	 * @throws IteratorException something went wrong
	 */
	public String computeLabelForClaim(AtomSet atomSet) throws IteratorException {
		Statement s = this.createClaimStatement(atomSet);
		return computeLabel(s);
	}
	
	/**
	 * Compute the label of all statements
	 */
	public void computeLabels() {
		// TODO compute labels
		Collection<Statement> states = this.statements.values();

		for(Statement s: states) {
			this.computeLabel(s);
		}
	}
	/**
	 * Computes the label of all statements using a new labelingFunction
	 * @param labelingFunction The new labeling function.
	 */
	public void computeLabels(LabelingFunction labelingFunction) {
		this.setLabelingFunction(labelingFunction);
		this.reinitializeLabels();
		this.computeLabels();
	}
	
	// ------------------------------------------------------------------------
	// PRIVATE METHODS
	// ------------------------------------------------------------------------
	private void addStatement(Statement statement) {
		this.statements.put(statement, statement);
		// Add the link between the statement and the atom it supports.
		List<Statement> list = this.statementsForAtom.get(statement.getRuleApplication().getGeneratedAtom());
		if(null == list) {
			list = new LinkedList<Statement>();
			this.statementsForAtom.put(statement.getRuleApplication().getGeneratedAtom(), list);
		}
		list.add(statement);
	}
	
	/**
	 * Creates the Top Statement.
	 */
	private void createTOPStatement() {
		Atom TOPatom = DefeasibleKnowledgeBase.getTOPAtom();
		// Adds the TOP atom to the list of Premises
		this.getOrCreatePremiseOfAtom(TOPatom);

		InMemoryAtomSet head = new DefaultInMemoryGraphStore();
		head.add(TOPatom);
		RuleApplication ruleApplication = new RuleApplication(new DefaultRule("", null, head), null, TOPatom);
		
		this.topStatement = new Statement(ruleApplication, null);
		this.topStatement.setLabel(Labels.STRICT_IN); // Label Top Statement STRICT IN
		
		List<Statement> list = new LinkedList<Statement>();
		list.add(this.topStatement);
		this.statementsForAtom.put(TOPatom, list);
	}
	
	private void createFactStatements() throws IteratorException {
		CloseableIterator<Atom> itFacts = this.kb.getFacts().iterator();
		
		List<Premise> TOPpremise = new LinkedList<Premise>();
		TOPpremise.add(this.getOrCreatePremiseOfAtom(this.getTOPStatement()
				.getRuleApplication().getGeneratedAtom()));
		
		InMemoryAtomSet body = new DefaultInMemoryGraphStore();
		body.add(this.getTOPStatement().getRuleApplication().getGeneratedAtom());
		
		while(itFacts.hasNext()) {
			Atom atom = itFacts.next();
			// Add the atom to the premise list
			this.getOrCreatePremiseOfAtom(atom);
			// Create the rule application for this atom
			InMemoryAtomSet head = new DefaultInMemoryGraphStore();
			head.add(atom);
			
			RuleApplication ruleApplication = new RuleApplication(new DefaultRule("", body, head), null, atom);
			// Create the Statement for this fact
			this.addStatement(new Statement(ruleApplication, TOPpremise));
		}
	}
	
	private Statement createClaimStatement(AtomSet atoms) throws IteratorException {
		CloseableIterator<Atom> itAtoms = atoms.iterator();
		List<Premise> prems = new LinkedList<Premise>();
		while(itAtoms.hasNext()) {
			Atom atom = itAtoms.next();
			prems.add(this.getOrCreatePremiseOfAtom(atom));
		}
		
		Statement s = new Statement(null, prems);
		// Add it to the history of queries if it does not already exists;
		if(this.statementsOfQueries.containsKey(s)) {
			s = this.statementsOfQueries.get(s);
		} else {
			this.statementsOfQueries.put(s, s);
		}
		return s; 
	}
	
	private void generateSupportandAttackEdgesForClaimStatement(Statement s) {
		//TODO
	}
	
	private void generateSupportEdgesForPremise(Premise prem) {
		List<Statement> statements = this.getStatementsForAtom(prem.getAtom());
		if(statements != null) {
			for(Statement s : statements) {
				if(!s.getRuleApplication().isDefeater()) {
					EDGEdge edge = new EDGEdge(s, prem, false);
					prem.addSupportEdge(edge);
					s.addOutgoingSupportEdge(edge);
				}
			}
		}
	}
	private void generateSupportEdges() {
		// For each Premise Add support links comming for Statement generating its atom
		for(Premise prem: this.premises.values()) {
			generateSupportEdgesForPremise(prem);
		}
	}
	
	private void generateAttackEdgesForPremise(Premise prem) {
		// TODO
	}
	
	private void generateAttackEdges() throws AtomSetException, HomomorphismException, IteratorException {
		if(this.kb.getNegativeConstraints() == null) return;
		
		for(Rule nc: this.kb.getNegativeConstraints()) {
			CloseableIteratorWithoutException<Atom> itNcBody = nc.getBody().iterator();
			// Get the two Conflicting Atoms
			Atom firstAtom = itNcBody.next();
			Atom secondAtom = itNcBody.next();
			
			
			// Find Ground Atoms that Map to Negative Constraint
			CloseableIterator<Substitution> itSubstitutions = SmartHomomorphism.instance()
					.execute(new DefaultConjunctiveQuery(nc.getBody()), this.kb.getSatruatedFacts());
			
			if(!itSubstitutions.hasNext()) { continue; }
		
			AtomSet firstAtoms = new DefaultInMemoryGraphStore();		
			AtomSet secondAtoms = new DefaultInMemoryGraphStore();
			
			while(itSubstitutions.hasNext()) {
				Substitution sub = itSubstitutions.next();
				firstAtoms.add(sub.createImageOf(firstAtom));
				secondAtoms.add(sub.createImageOf(secondAtom));
			}
			
			CloseableIterator<Atom> itFirstAtoms = firstAtoms.iterator();
			while(itFirstAtoms.hasNext()) {
				Atom atom = itFirstAtoms.next();
				Premise prem = this.getOrCreatePremiseOfAtom(atom);
				List<Statement> statementsForAtom = this.getStatementsForAtom(atom);
				
				CloseableIterator<Atom> itSecondAtoms = secondAtoms.iterator();
				while(itSecondAtoms.hasNext()) {
					Atom atom2 = itSecondAtoms.next();
					Premise prem2 = this.getOrCreatePremiseOfAtom(atom2);
					List<Statement> statementsForAtom2 = this.getStatementsForAtom(atom2);
					
					// Create Attack Links against the first Atom
					this.createAttackLinksBetweenPremiseAndStatements(prem, statementsForAtom2, statementsForAtom);
					// Create Attack Links against the second Atom
					this.createAttackLinksBetweenPremiseAndStatements(prem2, statementsForAtom, statementsForAtom2);
				}
				
			}
		}
	}
	
	private RuleApplier<Rule, AtomSet> getRuleApplier() {
		return new EDGRuleApplicationHandler(this).getRuleApplier();
	}
	
	private void createAttackLinksBetweenPremiseAndStatements(Premise prem, 
			List<Statement> statementsAgainstPremise, List<Statement> statementsForPremise) {
		
		List<Atom> conflicts = new LinkedList<Atom>();
		
		if(statementsAgainstPremise == null) {
			this.conflictingAtoms.put(prem.getAtom(), conflicts);
			return;
		}
		
		for(Statement s: statementsAgainstPremise) {
			if(!s.getRuleApplication().isDefeater()) { // If not a defeater then it attacks the premise
				EDGEdge edge = new EDGEdge(s, prem, true);
				prem.addAttackEdge(edge);
				s.addOutgoingAttackEdge(edge);
				conflicts.add(s.getRuleApplication().getGeneratedAtom());
			} else { // It is a defeater, it must attack the rule application
				if(statementsForPremise == null) continue;
				for(Statement sForPremise: statementsForPremise) {
					EDGEdge edge = new EDGEdge(s, sForPremise.getRuleApplication(), true);
					sForPremise.getRuleApplication().addAttackEdge(edge);
					s.addOutgoingAttackEdge(edge);
				}
			}
		}
		
		this.conflictingAtoms.put(prem.getAtom(), conflicts);
	}
}
