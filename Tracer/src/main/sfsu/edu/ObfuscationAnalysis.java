package main.sfsu.edu;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
/**
 * 
 * ObfuscationAnalysis.java
 * 
 * The main analysis entrypoint. This class collects the files under analysis,
 * performs the obfuscation and then retreives the final files. This list of retrieved
 * files is then analyzed in pairs to collect several data points, including call depth. 
 * @author anaqvi
 *
 */
public class ObfuscationAnalysis {

	FolderTraverse fw;
	private String pathtoFilesUnderAnalysis;
	//Map of all original files
	private Map<String,AnalyzedFile> originalFiles = new HashMap<String,AnalyzedFile>();
	//Map of obfuscated files
	private Map<String,List<AnalyzedFile>> obfuscatedFiles = new HashMap<String,List<AnalyzedFile>>();
	//Set of comparisons between each original file and the resulting obfuscations
	private List<AnalyzedPair> allFilesUnderAnalysis = new ArrayList<AnalyzedPair>();
	//Cumulative Stats Tracking
	int methodsProguard = 0,methods=0,methodsJShrink=0;
	int fieldsProguard = 0,fields=0,fieldsJShrink=0;
	int attributesProguard = 0,attrs=0,attributesJShrink=0;
	int cPoolProguard = 0,cpools=0,cPoolJShrink=0;
	double sizeProguard = 0.0,size=0.0,sizeJShrink=0.0; 

	public static final String JAVA_TYPE = "java";
	public static final String CLASS_TYPE = "class";

	public ObfuscationAnalysis(String pathToFiles) {
		this.pathtoFilesUnderAnalysis = pathToFiles;
		fw = new FolderTraverse();
	}

	public void analyze() {
		analyzeOriginalFiles();
		performAnalysis(ObfuscationType.JSHRINK);
		performAnalysis(ObfuscationType.PROGUARD);

		//For each entry in the originalFilesMap, get the corresponding analysis files for each obfuscation type. Compare the values of the file pairs
		for (Map.Entry<String, AnalyzedFile> entry : originalFiles.entrySet()) {
			String key = entry.getKey();
			AnalyzedFile originalAnalyzedFile = entry.getValue();
			List<AnalyzedFile> analyzedFiles = obfuscatedFiles.get(key);
			//now we have all of the analysis objects for each obfuscation type, matching the original file's filename/path
			for (AnalyzedFile obfuscatedAnalyzedFile:analyzedFiles) {
				AnalyzedPair ap = new AnalyzedPair(originalAnalyzedFile, obfuscatedAnalyzedFile);
				allFilesUnderAnalysis.add(ap);
			}
		}
		printResults();
	}


	/**
	 * For a given obfuscation type, 
	 * 1. loop through the map of allanalyzed original files 
	 * 2. perform the specified obfuscation
	 * 3. analyze the output of (2.)
	 * 
	 * @param obfuscationType
	 */
	private void performAnalysis(ObfuscationType obfuscationType) {
		for (Map.Entry<String, AnalyzedFile> entry : originalFiles.entrySet()) {
			//get the path to that file
			String pathToOriginalFile = entry.getKey();
			File originalFile = new File (pathToOriginalFile);
			File obfuscatedClassFile = null;
			try {
				if (ObfuscationType.JSHRINK.equals(obfuscationType)) {
					//Obfuscate using JShrink
					obfuscatedClassFile = Obfuscator.createObfuscatedClassFileUsingJshrink(originalFile);
				} else if (ObfuscationType.PROGUARD.equals(obfuscationType)){
					//Rather than obfuscating file-by-file, we now obfuscate everything in one shot. This commented call can be enabled if needed
					//obfuscatedClassFile = Obfuscator.obfuscateClassFileUsingProguard(originalFile);
					obfuscatedClassFile = Obfuscator.obfuscateProjectUsingProguard(originalFile);
				}
				if (obfuscatedClassFile != null) {
					// Analyze the resulting file class file
					//CallDepthAnalysis cdAnalysis = AspectJUtils.getCallDepth(obfuscatedClassFile);
					AnalyzedFile newFileAnalysis = FileAnalysisUtils.rateSingleFile(
							originalFile, obfuscationType);
					List<AnalyzedFile> currentResults = new ArrayList<AnalyzedFile>();
					// put it into the original file map. if there is an entry, add to it's list. otherwise, create a new entry
					if (obfuscatedFiles.containsKey(pathToOriginalFile)) {
						// we already did one form of obfuscation/analysis. Add to that result set
						currentResults = obfuscatedFiles.get(pathToOriginalFile);
					} 
					currentResults.add(newFileAnalysis);
					obfuscatedFiles.put(pathToOriginalFile, currentResults);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void analyzeOriginalFiles() {
		// Grab the files that we want from the target folder.
		collectInputFiles(JAVA_TYPE);
		// now, compile the entire contents of the testfiles folder
		compileAllJavaFiles();
		// For each collected file, perform an analysis
		for (Map.Entry<String, File> entry : fw.foundFiles.entrySet()) {
			if (entry.getValue().getName().contains(".java")) {
				try {
					File originalFile = entry.getValue();
					// Compile the class file
					// Get the already-compiled classfile matching the current input file
					String ptf=originalFile.getCanonicalPath();
					int pos = ptf.lastIndexOf(".");
					if (pos > 0) {
						ptf = ptf.substring(0, pos);
					}
					// Analyze that original class file
					AnalyzedFile originalFileAnalysis = FileAnalysisUtils.rateSingleFile(
							originalFile, ObfuscationType.NONE);
					//put it into the original file map
					originalFiles.put(originalFile.getAbsolutePath(), originalFileAnalysis);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	public BufferedWriter bw=null;
	/**
	 * Outputs the results of our analysis.
	 */
	private void printResults() {
		try {
			bw = new BufferedWriter(new FileWriter(".."+ File.separator +"staticLog.txt",true));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("---Analayzing Files and Logging---");
		Set<String> seenFiles = new HashSet<String>();
		for (AnalyzedPair ap: allFilesUnderAnalysis) {
			if (seenFiles.add(ap.getPathToFile())) {
				AnalyzedFile originalFile = originalFiles.get(ap.getPathToFile());
				try {
					bw.write(String.format("%20s", " Obfuscator")	+String.format("%25s", " File Name")+String.format("%22s", "Methods")+ String.format("%18s", " Size")+String.format("%20s", " Fields")+String.format("%25s", " Attributes")+String.format("%25s", " Constant Pool") + '\n');
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
				try{
					bw.write(String.format("%20s", " Unobfuscated")	+String.format("%25s", originalFile.getFileName())+String.format("%22s", originalFile.getNumMethods())+ String.format("%18s",  originalFile.getFileSize())+String.format("%20s", originalFile.getNumFields())+String.format("%25s", originalFile.getNumAttributes())+String.format("%25s", originalFile.getCpoolSize()) + '\n');
				}catch (IOException ioe) {
					ioe.printStackTrace();
				}methods=methods+originalFile.getNumMethods();
				fields=fields+originalFile.getNumFields();
				attrs=attrs+originalFile.getNumAttributes();
				cpools=cpools+originalFile.getCpoolSize();
				size=size+originalFile.getFileSize();
				}
			try {
				bw.write(String.format("%20s", ap.getObfuscationType())	+String.format("%25s", ap.getFileName())+String.format("%22s", ap.getMethodsChanged())+ String.format("%18s", ap.getSizeChange())+String.format("%20s", ap.getFieldsChanged())+String.format("%25s", ap.getAttributesChanged())+String.format("%25s", ap.getCpoolSize()) + '\n');
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}if(ObfuscationType.JSHRINK.equals(ap.getObfuscationType())){
				methodsJShrink=methodsJShrink+ap.getMethodsChanged();
				fieldsJShrink=fieldsJShrink+ap.getFieldsChanged();
				attributesJShrink=attributesJShrink+ap.getAttributesChanged();
				cPoolJShrink=cPoolJShrink+ap.getCpoolSize();
				sizeJShrink=sizeJShrink+ap.getSizeChange();
			}
			else{
				methodsProguard=methodsProguard+ap.getMethodsChanged();
				fieldsProguard=fieldsProguard+ap.getFieldsChanged();
				attributesProguard=attributesProguard+ap.getAttributesChanged();
				cPoolProguard=cPoolProguard+ap.getCpoolSize();
				sizeProguard=sizeProguard+ap.getSizeChange();
			}
		}
		try {
			bw.write('\n' + String.format("%20s", " ")	+String.format("%25s", " Methods")+String.format("%16s", "Size")+ String.format("%22s", " Fields")+String.format("%25s", " Attributes")+String.format("%27s", " Constant Pool")+ '\n');
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		try {
			bw.write(String.format("%20s", "Total          ")	+String.format("%22s", methods)+ String.format("%20s", size)+String.format("%20s", fields)+String.format("%25s", attrs)+String.format("%25s", cpools) + '\n');
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		try {
			bw.write(String.format("%20s", "TotalJShrink          ")	+String.format("%20s", methodsJShrink)+ String.format("%20s", sizeJShrink)+String.format("%20s", fieldsJShrink)+String.format("%25s", attributesJShrink)+String.format("%25s", cPoolJShrink) + '\n');
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		try {
			bw.write(String.format("%20s", "TotalProguard          ")	+String.format("%19s", methodsProguard)+ String.format("%20s", sizeProguard)+String.format("%20s", fieldsProguard)+String.format("%25s", attributesProguard)+String.format("%25s", cPoolProguard) + '\n');
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		try{
			if(bw!=null)
				bw.close();
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
		cleanupFiles();
	}

	private void cleanupFiles() {
		try {
			for (AnalyzedPair pair: allFilesUnderAnalysis) {
				String pathToFile = pair.getPathToFile();
				String newFileName = pathToFile.replace(".java", ".class");
				File temp = new File(newFileName);
				if (temp.exists()) {
					FileUtils.delete(temp);
				}
			}
		}catch (IOException e) {
			System.out.println("Encountered a problem deleting files during cleanup: "+ e);
		}
	}

	/**
	 * Walks through the supplied folder and collects all files of the given type "typeOfFile"
	 * @param typeOfFile
	 */
	private void collectInputFiles(String typeOfFile) {
		fw = new FolderTraverse();
		fw.walk(pathtoFilesUnderAnalysis, typeOfFile);
		if (fw.foundFiles.isEmpty()) {
			System.out.println("No Files of type " + typeOfFile + " Found!");
			return;
		}
	}

	@SuppressWarnings("unused")
	@Deprecated
	private File compileAndReturnJavaFile(File inputFile) {
		if (inputFile != null) {
			String extension = FileUtils.getExtensionWithoutFileName(inputFile);
			if (extension.equals(".java")) {
				File buildFile = new File(".." + File.separator + "testFiles" + File.separator + "build.xml");
				Project p = new Project();
				p.setUserProperty("ant.file", buildFile.getAbsolutePath());
				p.init();
				ProjectHelper helper = ProjectHelper.getProjectHelper();
				p.addReference("ant.projectHelper", helper);
				helper.parse(p, buildFile);

				DefaultLogger consoleLogger = new DefaultLogger();
				consoleLogger.setErrorPrintStream(System.err);
				consoleLogger.setOutputPrintStream(System.out);
				consoleLogger.setMessageOutputLevel(Project.MSG_INFO);
				p.addBuildListener(consoleLogger);
				p.executeTarget(p.getDefaultTarget());
				//Now that we've compiled it, return the resulting class file.
				String pathToFile = inputFile.getAbsolutePath().
						substring(0,inputFile.getAbsolutePath().lastIndexOf(File.separator)+1);
				String fileNameWithoutExtension = FileUtils.getFileNameWithoutExtension(inputFile);
				return new File(pathToFile.concat(fileNameWithoutExtension).concat(".class"));
			}
		}
		return null;
	}

	private void compileAllJavaFiles() {
		File buildFile = new File(".." + File.separator + "testFiles" + File.separator + "build.xml");
		Project p = new Project();
		p.setUserProperty("ant.file", buildFile.getAbsolutePath());
		p.init();
		ProjectHelper helper = ProjectHelper.getProjectHelper();
		p.addReference("ant.projectHelper", helper);
		helper.parse(p, buildFile);

		DefaultLogger consoleLogger = new DefaultLogger();
		consoleLogger.setErrorPrintStream(System.err);
		consoleLogger.setOutputPrintStream(System.out);
		consoleLogger.setMessageOutputLevel(Project.MSG_INFO);
		p.addBuildListener(consoleLogger);
		p.executeTarget(p.getDefaultTarget());
	}
}
