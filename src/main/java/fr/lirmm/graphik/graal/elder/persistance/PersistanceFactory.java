package fr.lirmm.graphik.graal.elder.persistance;

import java.util.LinkedList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import fr.lirmm.graphik.graal.defeasible.core.DefeasibleKnowledgeBase;
import fr.lirmm.graphik.graal.defeasible.core.preferences.PreferenceSet;
import fr.lirmm.graphik.graal.defeasible.core.preferences.RulePreference;
import fr.lirmm.graphik.graal.elder.core.Assumption;
import fr.lirmm.graphik.graal.elder.core.Premise;
import fr.lirmm.graphik.graal.elder.core.RuleApplication;
import fr.lirmm.graphik.graal.elder.core.SGEdge;
import fr.lirmm.graphik.graal.elder.core.Statement;
import fr.lirmm.graphik.graal.elder.core.StatementGraph;

public class PersistanceFactory {
	private static final PersistanceFactory INSTANCE = new PersistanceFactory();
	
	public static PersistanceFactory instance() {
		return INSTANCE;
	}
	
	public Statement inflateStatement(StatementGraph sg, JSONObject s) {
		JSONObject ra = (JSONObject)s.get("ruleApplication");
		JSONArray ps = (JSONArray)s.get("premises");
		String label = (String)s.get("label");
		RuleApplication ruleApplication= inflateRuleApplication(sg, ra);
		List<Premise> premises = inflatePremises(sg, ps);
		Statement statement = new Statement(ruleApplication, premises);
		statement.setLabel(label);
		return statement;
	}
	
	public void inflateStatements(StatementGraph sg, JSONArray statements) {
		for(Object o: statements) {
			Statement s = inflateStatement(sg, (JSONObject) o);
			// Add the statementTo the SG
			sg.getOrCreateStatement(s);
		}
	}
	
	public void inflateQueryStatements(StatementGraph sg, JSONArray queryStatements) {
		for(Object o: queryStatements) {
			Statement s = inflateStatement(sg, (JSONObject) o);
			// Add the statementTo the SG
			sg.getOrCreateClaimStatement(s);
		}
	}
	
	public Premise inflatePremise(StatementGraph sg, JSONObject p) {
		String atom = (String)p.get("atom");
		String label = (String)p.get("label");
		Premise prem = sg.getOrCreatePremiseOfAtom(atom);
		prem.setLabel(label);
		return prem;
	}
	
	public List<Premise> inflatePremises(StatementGraph sg, JSONArray ps) {
		if (ps == null) return null;
		List<Premise> premises = new LinkedList<Premise>();
		for(Object o: ps) {
			Premise p = inflatePremise(sg, (JSONObject) o);
			premises.add(p);
		}
		return premises;
	}
	
	public RuleApplication inflateRuleApplication(StatementGraph sg, JSONObject ra) {
		if (ra == null) return null;
		String ruleLabel = (String)ra.get("ruleLabel");
		String generatedAtom = (String)ra.get("generatedAtom");
		String title = (String)ra.get("title");
		String type = (String)ra.get("type");
		String label = (String)ra.get("label");
		return new RuleApplication(ruleLabel, generatedAtom, title, type, label);
	}
	
	
	public SGEdge inflateEdge(StatementGraph sg, JSONObject edgeJSON) {
		// Given that HTML does not like negative ids, we added 'id' before each one,
		// thus these two characters need to be removed for the hash to coincide.
		String sourceID = (String) edgeJSON.get("source");
		String targetID = ((String) edgeJSON.get("target"));
		
		boolean type = (boolean) edgeJSON.get("type");
		String label = (String) edgeJSON.get("label");
		
		Statement source = sg.getStatement(sourceID);
		Assumption target = null;
		if(null != sg.getPremise(targetID)) { // targetting a premise
			target = sg.getPremise(targetID);
		} else { // targetting a rule application
			target = sg.getStatement(targetID).getRuleApplication();
		}
		
		SGEdge edge = new SGEdge(source, target, type);
		edge.setLabel(label);
		
		if(edge.isAttack()) {
			target.addAttackEdge(edge);
		} else {
			target.addSupportEdge(edge);
		}
		return edge;
	}
	
	public void inflateEdges(StatementGraph sg, JSONArray edgesJSON) {
		for(Object o: edgesJSON) {
			inflateEdge(sg, (JSONObject) o);
		}
	}
	
	public RulePreference inflateRulePreference(String pref) {
		String[] params = pref.split(" ");
		return new RulePreference(params[0], params[2]);
	}
	
	public PreferenceSet inflateRulePreferences(JSONArray prefs) {
		PreferenceSet prefSet = new PreferenceSet();
		for(Object o: prefs) {
			RulePreference pref = inflateRulePreference((String)o);
			prefSet.add(pref);
		}
		return prefSet;
	}
	
	public void inflateRulePreferences(DefeasibleKnowledgeBase kb, JSONArray prefs) {
		for(Object o: prefs) {
			RulePreference pref = inflateRulePreference((String)o);
			kb.addRulePreference(pref);
		}
	}
	
	public StatementGraph inflateStatementGraph(JSONObject sgJSON) {
		StatementGraph sg = new StatementGraph(new DefeasibleKnowledgeBase());
		
		JSONArray statements = (JSONArray)sgJSON.get("statements");
		JSONArray edges = (JSONArray)sgJSON.get("edges");
		JSONArray rulePreferences = (JSONArray)sgJSON.get("rulePreferences");
		JSONArray queryStatements = (JSONArray)sgJSON.get("queryStatements");
		
		// Inflate Statements
		inflateStatements(sg, statements);
		// Inflate Query Statements
		inflateQueryStatements(sg, queryStatements);
		// Inflates Edges
		inflateEdges(sg,edges);
		// Inflate RulePreferences
		inflateRulePreferences(sg.getKB(), rulePreferences);

		
		return sg;
	}
	
	public StatementGraph inflateStatementGraph(String jsonString) throws ParseException {
		JSONParser parser = new JSONParser();
		JSONObject json = (JSONObject) parser.parse(jsonString);
		return inflateStatementGraph(json);
	}
	
	public String deflateStatementGraph(StatementGraph sg) {
		return sg.toJSON().toJSONString();
	}
}
