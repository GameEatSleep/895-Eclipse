package main.sfsu.edu;

import java.util.List;

public class CallFlowComparison {

	String originalCallFlow;
	List<String> obfuscatedCallFlows;

	public CallFlowComparison(String originalCallFlow, List<String> obfuscatedCallFlows) {
		super();
		this.originalCallFlow = originalCallFlow;
		this.obfuscatedCallFlows = obfuscatedCallFlows;
	}

	public String getOriginalCallFlow() {
		return originalCallFlow;
	}
	public void setOriginalCallFlow(String originalCallFlow) {
		this.originalCallFlow = originalCallFlow;
	}
	public List<String> getObfuscatedCallFlows() {
		return obfuscatedCallFlows;
	}
	public void setObfuscatedCallFlows(List<String> obfuscatedCallFlows) {
		this.obfuscatedCallFlows = obfuscatedCallFlows;
	}
	
	
}
