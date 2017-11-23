package main.sfsu.edu;
//Dynamic Analysis now done using Post-Compile Time Weaving
@SuppressWarnings("unused")
@Deprecated
public class CallDepthAnalysis {

	int callDepth;
	String callFlow;

	public CallDepthAnalysis(int callDepth,String callFlow) {
		this.callDepth = callDepth;
		this.callFlow = callFlow;
	}

	int getCallDepth() {
		return this.callDepth;
	}

	String getCallFlow() {
		String completeCallFlow = callFlow.replace("[", "").replace("]", "").replaceAll(" ", "");
		String[] callFlowPieces = completeCallFlow.split(",");
		StringBuilder output = new StringBuilder();
		for (String s:callFlowPieces) {
			output.append(s + "\n");
		}
		return output.toString();
	}
}
