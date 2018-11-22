package fr.lirmm.graphik.graal.elder.persistance;

import java.io.Serializable;
import java.util.List;

public class StatementGraphJSONRepresentation implements Serializable {

	private List<StatementJSONRepresentation> statements;
	private List<SGEdgeJSONRepresentation> edges;
	private List<String> rulePreferences;
	private List<StatementJSONRepresentation> queryStatements;
	
	public StatementGraphJSONRepresentation() {}
	
	
	public List<StatementJSONRepresentation> getStatements() {
		return statements;
	}


	public void setStatements(List<StatementJSONRepresentation> statements) {
		this.statements = statements;
	}


	public List<SGEdgeJSONRepresentation> getEdges() {
		return edges;
	}


	public void setEdges(List<SGEdgeJSONRepresentation> edges) {
		this.edges = edges;
	}


	public List<String> getRulePreferences() {
		return rulePreferences;
	}


	public void setRulePreferences(List<String> rulePreferences) {
		this.rulePreferences = rulePreferences;
	}


	public List<StatementJSONRepresentation> getQueryStatements() {
		return queryStatements;
	}


	public void setQueryStatements(List<StatementJSONRepresentation> queryStatements) {
		this.queryStatements = queryStatements;
	}


}
