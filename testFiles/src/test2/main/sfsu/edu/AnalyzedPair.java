package main.sfsu.edu;

/**
 * Represents a single analysis of a pair of files - it stores the results of the comparison of a file with it's obfuscated
 * form, for clear consistent output.
 * @author anaqvi
 *
 */
public class AnalyzedPair {
	
	private String fileName;
	private String pathToFile;
	private ObfuscationType obfuscationType;
	private int methodsChanged;
	private double sizeChange;
	private int fieldsChanged;
	private int cpoolSize;
	private int attributesChanged;
	private double rating;
	String originalCallFlow;
	String finalCallFlow;

	public AnalyzedPair(AnalyzedFile originalFile, AnalyzedFile obfuscatedFile) {
		this.fileName = originalFile.getFileName();
		this.pathToFile = originalFile.getPathToFile();
		this.obfuscationType = obfuscatedFile.getObfuscationType();
		this.methodsChanged = obfuscatedFile.getNumMethods();
		this.sizeChange = obfuscatedFile.getFileSize();
		this.fieldsChanged = obfuscatedFile.getNumFields();
		this.cpoolSize = obfuscatedFile.getCpoolSize();
		this.attributesChanged = obfuscatedFile.getNumAttributes();
		this.originalCallFlow = originalFile.getCallFlow();
		this.finalCallFlow = obfuscatedFile.getCallFlow();
		
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
	public int getMethodsChanged() {
		return methodsChanged;
	}
	public void setMethodsChanged(int methodsChanged) {
		this.methodsChanged = methodsChanged;
	}
	public double getSizeChange() {
		return sizeChange;
	}
	public void setSizeChange(double sizeChange) {
		this.sizeChange = sizeChange;
	}
	public int getCpoolSize() {
		return cpoolSize;
	}
	public void setCpoolSize(int cpoolSize) {
		this.cpoolSize = cpoolSize;
	}
	public int getFieldsChanged() {
		return fieldsChanged;
	}
	public void setFieldsChanged(int fieldsChanged) {
		this.fieldsChanged = fieldsChanged;
	}
	public int getAttributesChanged() {
		return attributesChanged;
	}
	public void setAttributesChanged(int attributesChanged) {
		this.attributesChanged = attributesChanged;
	}
	public double getRating() {
		return rating;
	}
	public void setRating(double rating) {
		this.rating = rating;
	}
	public void setInstCount(double rating) {
		this.rating = rating;
	}
	public String getOriginalCallFlow() {
		return originalCallFlow;
	}
	public void setOriginalCallFlow(String originalCallFlow) {
		this.originalCallFlow = originalCallFlow;
	}
	public String getFinalCallFlow() {
		return finalCallFlow;
	}
	public void setFinalCallFlow(String finalCallFlow) {
		this.finalCallFlow = finalCallFlow;
	}
	@Override
	public String toString() {
		return obfuscationType + "\t" + fileName + "\t\t" + methodsChanged + "\t\t" + sizeChange + "\t\t" + fieldsChanged + "\t\t" + attributesChanged + "\t\t\t" + rating;  
	}


}
