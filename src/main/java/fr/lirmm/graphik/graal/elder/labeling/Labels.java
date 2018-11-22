package fr.lirmm.graphik.graal.elder.labeling;

public abstract class Labels {
	public static final String STRICT_IN = "INstr", 
			DEFEASIBLE_IN = "INdef", 
			AMBIGUOUS = "AMBIG",
			STRICT_OUT = "OUTstr",
			DEFEASIBLE_OUT = "OUTdef",
			ASSUMED_OUT = "UNSUP";
	
	public static String toPrettyString(String label) {
		if(null == label) return null;
		
		String prettyLabel = "";
		if(label.equals(Labels.DEFEASIBLE_IN)) {
			prettyLabel = "Accepted Generally";
		} else if(label.equals(Labels.DEFEASIBLE_OUT)) {
			prettyLabel = "Rejected Generally";
		} else if(label.equals(Labels.STRICT_IN)) {
			prettyLabel = "Accepted Strictly";
		} else if(label.equals(Labels.STRICT_OUT)) {
			prettyLabel = "Rejected Strictly";
		} else if(label.equals(Labels.AMBIGUOUS)) {
			prettyLabel = "Ambiguous";
		} else if(label.equals(Labels.ASSUMED_OUT)) {
			prettyLabel = "Unsupported";
		}
		return prettyLabel;
	}
}
