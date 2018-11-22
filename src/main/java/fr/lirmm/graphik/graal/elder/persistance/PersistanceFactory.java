package fr.lirmm.graphik.graal.elder.persistance;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
	
	public Statement inflateStatement(StatementGraph sg, StatementJSONRepresentation rep) {
		List<Premise> premises = inflatePremises(sg, rep.getPremises());
		RuleApplication ruleApplication = inflateRuleApplication(rep.getRuleApplication());
		Statement statement = new Statement(ruleApplication, premises);
		statement.setLabel(rep.getLabel());
		return statement;
	}
	
	public void inflateStatements(StatementGraph sg, List<StatementJSONRepresentation> statements) {
		for(StatementJSONRepresentation rep: statements) {
			Statement s = inflateStatement(sg, rep);
			// Add the statementTo the SG
			sg.getOrCreateStatement(s);
		}
	}
	
	public void inflateQueryStatements(StatementGraph sg, List<StatementJSONRepresentation> queryStatements) {
		for(StatementJSONRepresentation rep: queryStatements) {
			Statement s = inflateStatement(sg, rep);
			// Add the statementTo the SG
			sg.getOrCreateClaimStatement(s);
		}
	}
	
	public Premise inflatePremise(StatementGraph sg, PremiseJSONRepresentation rep) {
		Premise prem = sg.getOrCreatePremiseOfAtom(rep.getAtom());
		prem.setLabel(rep.getLabel());
		return prem;
	}
	
	public List<Premise> inflatePremises(StatementGraph sg, List<PremiseJSONRepresentation> reps) {
		if (reps == null) return null;
		List<Premise> premises = new LinkedList<Premise>();
		for(PremiseJSONRepresentation rep: reps) {
			Premise p = inflatePremise(sg, rep);
			premises.add(p);
		}
		return premises;
	}
	
	public RuleApplication inflateRuleApplication(RuleApplicationJSONRepresentation rep) {
		if (rep == null) return null;
		return rep.inflate();
	}
	
	
	public SGEdge inflateEdge(StatementGraph sg, SGEdgeJSONRepresentation rep) {
		
		Statement source = sg.getStatement(rep.getSource());
		Assumption target = sg.getPremise(rep.getTargettedAssumption());
		if(null == target) { // targetting a rule application
			target = sg.getStatement(rep.getTarget()).getRuleApplication();
		}
		
		SGEdge edge = new SGEdge(source, target, rep.getType(), rep.getLabel());
		
		if(edge.isAttack()) {
			target.addAttackEdge(edge);
		} else {
			target.addSupportEdge(edge);
		}
		return edge;
	}
	
	public void inflateEdges(StatementGraph sg, List<SGEdgeJSONRepresentation> reps) {
		for(SGEdgeJSONRepresentation rep: reps) {
			inflateEdge(sg, rep);
		}
	}
	
	public RulePreference inflateRulePreference(String pref) {
		String[] params = pref.split(" ");
		return new RulePreference(params[0], params[2]);
	}
	
	public PreferenceSet inflateRulePreferences(List<String> prefs) {
		PreferenceSet prefSet = new PreferenceSet();
		for(String prefString: prefs) {
			RulePreference pref = inflateRulePreference(prefString);
			prefSet.add(pref);
		}
		return prefSet;
	}
	
	public void inflateRulePreferences(DefeasibleKnowledgeBase kb, List<String> prefs) {
		for(String prefString: prefs) {
			RulePreference pref = inflateRulePreference(prefString);
			kb.addRulePreference(pref);
		}
	}
	
	public StatementGraph inflateStatementGraph(StatementGraphJSONRepresentation rep) {
		StatementGraph sg = new StatementGraph(new DefeasibleKnowledgeBase());
		
		// Inflate Statements
		inflateStatements(sg, rep.getStatements());
		// Inflate Query Statements
		inflateQueryStatements(sg, rep.getQueryStatements());
		// Inflates Edges
		inflateEdges(sg,rep.getEdges());
		// Inflate RulePreferences
		inflateRulePreferences(sg.getKB(), rep.getRulePreferences());

		return sg;
	}
	
	public StatementGraph inflateStatementGraph(String jsonString) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		StatementGraphJSONRepresentation sgRep = mapper.readValue(jsonString, StatementGraphJSONRepresentation.class);
		return inflateStatementGraph(sgRep);
	}
	
	public String deflateStatementGraph(StatementGraph sg) {
		return sg.toJSONString();
	}
}
