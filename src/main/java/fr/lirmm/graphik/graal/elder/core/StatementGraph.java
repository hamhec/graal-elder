package fr.lirmm.graphik.graal.elder.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

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
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.defeasible.core.DefeasibleKnowledgeBase;
import fr.lirmm.graphik.graal.defeasible.core.LogicalObjectsFactory;
import fr.lirmm.graphik.graal.defeasible.core.atoms.FlexibleAtom;
import fr.lirmm.graphik.graal.defeasible.core.io.DlgpDefeasibleParser;
import fr.lirmm.graphik.graal.defeasible.core.preferences.Preference;
import fr.lirmm.graphik.graal.defeasible.core.rules.StrictRule;
import fr.lirmm.graphik.graal.elder.labeling.LabelingFunction;
import fr.lirmm.graphik.graal.elder.labeling.Labels;
import fr.lirmm.graphik.graal.elder.labeling.defeasible.logic.BDLwithTD;
import fr.lirmm.graphik.graal.elder.labeling.defeasible.logic.BDLwithoutTD;
import fr.lirmm.graphik.graal.elder.labeling.defeasible.logic.PDLwithTD;
import fr.lirmm.graphik.graal.elder.labeling.defeasible.logic.PDLwithoutTD;
import fr.lirmm.graphik.graal.elder.reasoning.SGRuleApplicationHandler;
import fr.lirmm.graphik.graal.homomorphism.SmartHomomorphism;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.CloseableIteratorWithoutException;
import fr.lirmm.graphik.util.stream.IteratorException;

public class StatementGraph {
	private HashMap<String, Statement> statements;
	private HashMap<String, Premise> premises;
	private HashMap<String, List<Statement>> statementsForAtom;
	private HashMap<String, List<String>> conflictingAtoms;
	
	
	private LabelingFunction labelingFunction;
	private DefeasibleKnowledgeBase kb;
	
	private Statement topStatement;
	public HashMap<String, Statement> statementsOfQueries;
	
	// ------------------------------------------------------------------------
	// CONSTRUCTORS
	// ------------------------------------------------------------------------	
	/**
	 * Creates an SG given a KB and a labelingFunction
	 * @param kb The defeasible Knowledge Base
	 * @param labelingFunction LabelingFunction
	 */
	public StatementGraph(DefeasibleKnowledgeBase kb, LabelingFunction labelingFunction) {
		this();
		this.labelingFunction = labelingFunction;
		this.kb = kb;
	}
	/**
	 * Creates an EDG given a KB
	 * @param kb The defeasible Knowledge Base
	 */
	public StatementGraph(DefeasibleKnowledgeBase kb) {
		this(kb, new BDLwithTD(kb.getRulePreferences()));
	}
	
	public StatementGraph() {
		this.statements = new HashMap<String,Statement>();
		this.premises = new HashMap<String, Premise>();
		this.statementsForAtom = new HashMap<String, List<Statement>>();
		
		this.conflictingAtoms = new HashMap<String, List<String>>();
		this.statementsOfQueries = new HashMap<String, Statement>();
		
		this.createTOPStatement();
	}
	
	public StatementGraph(DefeasibleKnowledgeBase kb, String labelingFunctionString) {
		this();
		this.kb = kb;
		if(labelingFunctionString.equals(LabelingFunction.BDLwithoutTD)) {
			this.labelingFunction = new BDLwithoutTD(kb.getRulePreferences());
		} else if(labelingFunctionString.equals(LabelingFunction.BDLwithTD)) {
			this.labelingFunction = new BDLwithTD(kb.getRulePreferences());
		} else if(labelingFunctionString.equals(LabelingFunction.PDLwithoutTD)) {
			this.labelingFunction = new PDLwithoutTD(kb.getRulePreferences());
		} else if(labelingFunctionString.equals(LabelingFunction.PDLwithTD)) {
			this.labelingFunction = new PDLwithTD(kb.getRulePreferences());
		} else {
			this.labelingFunction = new BDLwithTD(kb.getRulePreferences());
		}
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
	 * Return the defeasible knowledge base used to construct this SG.
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
	 * Returns a collection containing all statements of this SG
	 * @return List of all statements
	 */
	public List<Statement> getAllStatements() {
		List<Statement> statements = new LinkedList<Statement>();
		statements.addAll(this.statements.values());
		statements.addAll(this.statementsOfQueries.values());
		
		return statements;
	}
	public Collection<Premise> getAllPremises() {
		return this.premises.values();
	}

	// ------------------------------------------------------------------------
	// PUBLIC METHODS
	// ------------------------------------------------------------------------
	/**
	 * Returns the premise representing the atom, or creates it if necessary.
	 * @param atom Atom
	 * @return Premise Premise of the atom
	 */
	public Premise getOrCreatePremiseOfAtom(String atom) {
		Premise premise = this.getPremise(atom);
		if(null == premise) { // if no premise already exists for that atom then create a new one and add it.
			premise = new Premise(atom);
			this.premises.put(atom, premise);
		}
		return premise;
	}
	
	public Premise getPremise(String atom) {
		return this.premises.get(atom);
	}
	
	/**
	 * Returns the statement representing a rule application on a specific list of premises.
	 * @param ruleApplication RuleApplication
	 * @param premises Premises
	 * @return statement
	 */
	public Statement getOrCreateStatement(RuleApplication ruleApplication, List<Premise> premises) {
		return this.getOrCreateStatement(new Statement(ruleApplication, premises));
	}
	
	public Statement getOrCreateStatement(AtomSet body, Atom head, Rule rule) throws IteratorException {
		// Add the head as a Premise if it doesn't already exists
		this.getOrCreatePremiseOfAtom(head.toString());
		// Add statement
		List<Premise> premises = this.getPremisesForAtoms(body);
		if(premises.isEmpty()) { // body is top statement;
			premises.add(this.getOrCreatePremiseOfAtom(this.getTOPStatement().getRuleApplication().getGeneratedAtom()));
		}
		RuleApplication ruleApplication = new RuleApplication(rule, body, head);
		return this.getOrCreateStatement(ruleApplication, premises);
	}
	
	public Statement getOrCreateStatement(Statement s) {
		Statement statement = this.getStatement(s.getID());
		if(null == statement) {
			statement = s;
			this.addStatement(statement);
		}
		return statement;
	}
	
	public Statement getStatement(String id) {
		return this.statements.get(id);
	}
	/**
	 * Returns a list of statements that supports the atom.
	 * @param atom Atom
	 * @return list of statements that support an atom.
	 */
	public List<Statement> getStatementsForAtom(String atom) {
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
			premises.add(this.getOrCreatePremiseOfAtom(atom.toString()));
		}
		return premises;
	}
	
	
	public Statement addStatementForRuleApplication(AtomSet body, Atom head, Rule rule) throws IteratorException, AtomSetException {
		// Add the head as a Premise if it doesn't already exists
		this.getOrCreatePremiseOfAtom(head.toString());
		// Add statement
		
		if(body.isEmpty()) { // empty body, add top statement
			body.add(LogicalObjectsFactory.instance().getTOPAtom());
		}
		List<Premise> premises = this.getPremisesForAtoms(body);
		RuleApplication ruleApplication = new RuleApplication(rule, body, head);
		Statement statement = new Statement(ruleApplication, premises);
		this.addStatement(statement);
		return statement;
	}
	

	/**
	 * Constructs the SG by creating the statements and the edges
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
	 * Returns the Entailment Status of a conjunction of ground atoms represented in a string.
	 * @param atomsString Atom String
	 * @return String representing the entailment status of the ground atom.
	 * @throws IteratorException Something went wrong.
	 * @throws AtomSetException Something Went Wrong
	 */
	public String groundQuery(String atomsString) throws IteratorException, AtomSetException {
		CloseableIterator<Atom> itParsedAtoms = DlgpDefeasibleParser.parseAtomSet(atomsString);
		AtomSet atoms = new LinkedListAtomSet();
		
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
		Statement s = this.getOrCreateClaimStatement(atoms);
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
	 * Computes the label of an SGEdge
	 * @param edge Input SGEdge
	 * @return label of the edge
	 */
	public String computeLabel(SGEdge edge) {
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
		this.statements.put(statement.getID(), statement);
		// Add the link between the statement and the atom it supports.
		if(null == statement.getRuleApplication()) return;
		
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
		Atom TOPatom = LogicalObjectsFactory.instance().getTOPAtom();
		// Adds the TOP atom to the list of Premises
		this.getOrCreatePremiseOfAtom(TOPatom.toString());

		InMemoryAtomSet head = new LinkedListAtomSet();
		head.add(TOPatom);
		RuleApplication ruleApplication = new RuleApplication(new StrictRule("", null, head),
				null, TOPatom);
		
		this.topStatement = new Statement(ruleApplication, null);
		this.topStatement.setLabel(Labels.STRICT_IN); // Label Top Statement STRICT IN
		// Add the top statement to the statement for atom list
		List<Statement> list = new LinkedList<Statement>();
		list.add(this.topStatement);
		this.statementsForAtom.put(TOPatom.toString(), list);
		this.statements.put(this.topStatement.getID(), this.topStatement);
	}
	
	private void createFactStatement(Atom atom) {
		List<Premise> TOPpremise = new LinkedList<Premise>();
		TOPpremise.add(this.getOrCreatePremiseOfAtom(this.getTOPStatement()
				.getRuleApplication().getGeneratedAtom()));
		
		// Add the atom to the premise list
		this.getOrCreatePremiseOfAtom(atom.toString());
		// Create the rule application for this atom
		
		RuleApplication ruleApplication = new RuleApplication(atom);
		// Create the Statement for this fact
		this.getOrCreateStatement(ruleApplication, TOPpremise);
	}
	
	private void createFactStatements() throws IteratorException {
		CloseableIterator<Atom> itFacts = this.kb.getFacts().iterator();
		while(itFacts.hasNext()) {
			createFactStatement(itFacts.next());
		}
	}
	
	public Statement getOrCreateClaimStatement(AtomSet atoms) throws IteratorException {
		CloseableIterator<Atom> itAtoms = atoms.iterator();
		List<Premise> prems = new LinkedList<Premise>();
		while(itAtoms.hasNext()) {
			prems.add(this.getOrCreatePremiseOfAtom(itAtoms.next().toString()));
		}
		
		Statement s = new Statement(null, prems);
		// Add it to the history of queries if it does not already exists;
		if(!this.statementsOfQueries.containsKey(s.getID())) {
			this.statementsOfQueries.put(s.getID(), s);
		}
		return s; 
	}
	
	public Statement getOrCreateClaimStatement(Statement s) {
		// Create premises if not already created
		for(Premise p: s.getPremises()) {
			this.getOrCreatePremiseOfAtom(p.getAtom().toString());
		}
		// Add it to the history of queries if it does not already exists;
		if(!this.statementsOfQueries.containsKey(s.getID())) {
			this.statementsOfQueries.put(s.getID(), s);
		}
		return s; 
	}
	
	private void generateSupportEdgesForPremise(Premise prem) {
		List<Statement> statements = this.getStatementsForAtom(prem.getAtom());
		if(statements != null) {
			for(Statement s : statements) {
				if(!s.getRuleApplication().isDefeater()) {
					SGEdge edge = new SGEdge(s, prem, false);
					prem.addSupportEdge(edge);
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
	
	private void generateAttackEdges() throws AtomSetException, HomomorphismException, IteratorException {
		if(this.kb.getNegativeConstraints() == null) return;
		
		for(Rule nc: this.kb.getNegativeConstraints()) {
			CloseableIteratorWithoutException<Atom> itNcBody = nc.getBody().iterator();
			// Get the two Conflicting Atoms
			Atom firstAtom = itNcBody.next();
			Atom secondAtom = itNcBody.next();
			
			
			// Find Ground Atoms that Map to Negative Constraint
			CloseableIterator<Substitution> itSubstitutions = SmartHomomorphism.instance()
					.execute(new DefaultConjunctiveQuery(nc.getBody()), this.kb.getStaturatedFacts());
			
			if(!itSubstitutions.hasNext()) { continue; }
		
			while(itSubstitutions.hasNext()) {
				Substitution sub = itSubstitutions.next();
				Atom firstAtomImage = new FlexibleAtom(sub.createImageOf(firstAtom));
				Atom secondAtomImage = new FlexibleAtom(sub.createImageOf(secondAtom));
				
				Premise prem = this.getOrCreatePremiseOfAtom(firstAtomImage.toString());
				List<Statement> statementsForAtom = this.getStatementsForAtom(prem.getAtom());
				
				Premise prem2 = this.getOrCreatePremiseOfAtom(secondAtomImage.toString());
				List<Statement> statementsForAtom2 = this.getStatementsForAtom(prem2.getAtom());
					
				// Create Attack Links against the first Atom
				this.createAttackLinksBetweenPremiseAndStatements(prem, statementsForAtom2, statementsForAtom);
				// Create Attack Links against the second Atom
				this.createAttackLinksBetweenPremiseAndStatements(prem2, statementsForAtom, statementsForAtom2);
			}
				
		}
	}
	
	private RuleApplier<Rule, AtomSet> getRuleApplier() {
		return new SGRuleApplicationHandler(this).getRuleApplier();
	}
	
	private void createAttackLinksBetweenPremiseAndStatements(Premise prem, 
			List<Statement> statementsAgainstPremise, List<Statement> statementsForPremise) {
		
		List<String> conflicts = new LinkedList<String>();
		
		if(statementsAgainstPremise == null) {
			this.conflictingAtoms.put(prem.getAtom(), conflicts);
			return;
		}
		
		for(Statement s: statementsAgainstPremise) {
			if(!s.getRuleApplication().isDefeater()) { // If not a defeater then it attacks the premise
				SGEdge edge = new SGEdge(s, prem, true);
				prem.addAttackEdge(edge);
				conflicts.add(s.getRuleApplication().getGeneratedAtom());
			} else { // It is a defeater, it must attack the rule application
				if(statementsForPremise == null) continue;
				for(Statement sForPremise: statementsForPremise) {
					SGEdge edge = new SGEdge(s, sForPremise.getRuleApplication(), true);
					sForPremise.getRuleApplication().addAttackEdge(edge);
				}
			}
		}
		
		this.conflictingAtoms.put(prem.getAtom(), conflicts);
	}
	
	
	
	/**
     * Verifies if two StatementGraphs are equivalent or not.
     * @param obj the object to test
     * @return true if the objects are equal, false otherwise.
     */
    public boolean equals(Object obj) {
        if (this == obj) { return true; }
        if (obj == null) { return false; }
        if (!(obj instanceof StatementGraph)) { return false; }
        
        StatementGraph other = (StatementGraph) obj;
        
        // They must have the same number of statements Statements
        if(this.getAllStatements().size() != other.getAllStatements().size()) return false;
        // They must have the same Statements
        for(Statement s: this.statements.values()) {
        	if(null == other.getStatement(s.getID())) {
        		return false;
        	}
        }
        
        // They must have the same preferences
        for(Preference pref: this.getKB().getRulePreferences().values()) {
        	if(null == other.getKB().getRulePreferences().get(pref.stringify())) {
        		return false;
        	}
        }
        
        return true;
    }
	
	
	@SuppressWarnings("unchecked")
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		
		// All statements and edges to JSON
    	Collection<Statement> statementsList = this.statements.values();
    	Collection<Statement> queryList = this.statementsOfQueries.values();
    	
    	JSONArray edgesJSON = new JSONArray();
    	JSONArray statementsJSON = new JSONArray();
		JSONArray queryStatementsJSON = new JSONArray();
    	JSONArray rulePreferencesJSON = new JSONArray();
    	
    	for(Statement s: statementsList) {
    		statementsJSON.add(s.toJSON());
    		for(SGEdge e: s.getIncomingEdges()) {
    			edgesJSON.add(e.toJSON(s));
    		}
    	}
    	
    	for(Statement s: queryList) {
    		queryStatementsJSON.add(s.toJSON());
    		for(SGEdge e: s.getIncomingEdges()) {
    			edgesJSON.add(e.toJSON(s));
    		}
    	}
    	
    	
    	// Adding Preferences
    	for(Preference pref: this.getKB().getRulePreferences().values()) {
    		rulePreferencesJSON.add(pref.stringify());
    	}
    	
    	json.put("statements", statementsJSON);
    	json.put("edges", edgesJSON);
    	json.put("rulePreferences", rulePreferencesJSON);
    	json.put("queryStatements", queryStatementsJSON);
    	
		return json;
	}
	
	@SuppressWarnings("unchecked")
	public String toViewJSON() {
    	JSONObject json = new JSONObject();
    	
    	// All statements to JSON
    	List<Statement> statementsList = this.getAllStatements();

    	JSONArray edges = new JSONArray();
    	JSONArray statementsJSON = new JSONArray();
    	
    	for(Statement s: statementsList) {
    		// add statement to json string list
    		statementsJSON.add(s.toViewJSON());
    		
    		// generate incoming edges to rule application
    		Iterator<SGEdge> itAtt;
    		Iterator<SGEdge> itSupp;
    		if(s.getRuleApplication() != null) {
	    		itAtt = s.getRuleApplication().getAttackEdges().iterator();
	    		while(itAtt.hasNext()) {
	    			SGEdge e = itAtt.next();
	    			edges.add(e.toViewJSON(s));
	    		}
	    		itSupp = s.getRuleApplication().getSupportEdges().iterator();
	    		while(itSupp.hasNext()) {
	    			SGEdge e = itSupp.next();
	    			edges.add(e.toViewJSON(s));
	    		}
    		}
    		
    		if(s.getPremises() == null) continue;
    		
    		Iterator<Premise> itPremises = s.getPremises().iterator();
    		while(itPremises.hasNext()) {
    			Premise p = itPremises.next();
    			itAtt = p.getAttackEdges().iterator();
    			while(itAtt.hasNext()) {
        			SGEdge e = itAtt.next();
        			edges.add(e.toViewJSON(s));
        		}
        		itSupp = p.getSupportEdges().iterator();
        		while(itSupp.hasNext()) {
        			SGEdge e = itSupp.next();
        			edges.add(e.toViewJSON(s));
        		}
    		}	
    	}
    	
    
    	json.put("statements", statementsJSON);
    	json.put("edges", edges);
    	
    	return json.toJSONString();
    }
}
