package main.sfsu.edu;

/**
 * Represents the analysis of a single file. The metrics contained here will be compared to other such
 * files to judge the effectiveness of different obfuscation methods
 * @author anaqvi
 *
 */
public class AnalyzedFile {

	private String fileName;
	private String pathToFile;
	private ObfuscationType obfuscationType;
	private int numMethods;
	private double fileSize;
	private int numFields;
	private int cpoolSize;
	private int numAttributes;
	private int callDepth;
	private String callFlow;

	
	public AnalyzedFile(String fileName, String pathToFile, ObfuscationType obfuscationType, int numMethods, double fileSize,
			int numFields, int cpoolSize, int numAttributes, int callDepth, String callFlow) {
		super();
		this.fileName = fileName;
		this.pathToFile = pathToFile;
		this.obfuscationType = obfuscationType;
		this.numMethods = numMethods;
		this.fileSize = fileSize;
		this.numFields = numFields;
		this.cpoolSize = cpoolSize;
		this.numAttributes = numAttributes;
		this.callDepth = callDepth;
		this.callFlow = callFlow;
	}

	public AnalyzedFile() {
	}

	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getPathToFile() {
		return pathToFile;
	}
	public void setPathToFile(String pathToFile) {
		this.pathToFile = pathToFile;
	}
	public ObfuscationType getObfuscationType() {
		return obfuscationType;
	}
	public void setObfuscationType(ObfuscationType obfuscationType) {
		this.obfuscationType = obfuscationType;
	}
	public int getNumMethods() {
		return numMethods;
	}
	public void setNumMethods(int numMethods) {
		this.numMethods = numMethods;
	}
	public double getFileSize() {
		return fileSize;
	}
	public void setFileSize(double fileSize) {
		this.fileSize = fileSize;
	}
	public int getNumFields() {
		return numFields;
	}
	public void setNumFields(int numFields) {
		this.numFields = numFields;
	}
	public int getCpoolSize() {
		return cpoolSize;
	}
	public void setCpoolSize(int cpoolSize) {
		this.cpoolSize = cpoolSize;
	}
	public int getNumAttributes() {
		return numAttributes;
	}
	public void setNumAttributes(int numAttributes) {
		this.numAttributes = numAttributes;
	}
	public int getCallDepth() {
		return callDepth;
	}
	public void setCallDepth(int callDepth) {
		this.callDepth = callDepth;
	}
	public String getCallFlow() {
		return callFlow;
	}
	public void setCallFlow(String callFlow) {
		this.callFlow = callFlow;
	}
}
