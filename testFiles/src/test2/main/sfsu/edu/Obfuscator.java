package main.sfsu.edu;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
/**
 * 
 * Obfuscator.java
 * 
 * A series of helper methods which simply obfuscate a given java class and extracts the obfuscated file 
 * from the resulting jar file.
 * 
 * @author anaqvi
 *
 */

public class Obfuscator {

	/**
	 * Performs an obfuscatiwon using the JShrink library
	 * @param originalClassFile
	 * @return the path to the now-obfuscated file.
	 */
	static File createObfuscatedClassFileUsingJshrink(File originalClassFile) {
		String unobfuscatedFileName = FileUtils.getFileNameWithoutExtension(originalClassFile);
		String obfuscatedFileName = unobfuscatedFileName+".jar";
		String pathToFiles = originalClassFile.getParent()+File.separator;
		String finalObfuscatedFileName = pathToFiles+unobfuscatedFileName + ".class";
		unobfuscatedFileName = unobfuscatedFileName+".class";

		ArrayList<String> paramsExecute = new ArrayList<String>();
		String jshrinkJarFilePath = System.getProperty("AGENT_PATH").concat(File.separator+"jshrink.jar");
		paramsExecute.add("java");
		paramsExecute.add("-jar");
		paramsExecute.add(jshrinkJarFilePath);
		paramsExecute.add(pathToFiles+unobfuscatedFileName);
		paramsExecute.add("-overwrite");
		paramsExecute.add("-o");
		paramsExecute.add(pathToFiles+obfuscatedFileName);

		String pathToJar = pathToFiles+obfuscatedFileName;
		try {
			ProcessBuilder builderExecute = new ProcessBuilder(paramsExecute);
			Process process = builderExecute.start();
			process.waitFor();
			//next, extract the .class from the new .jar file
			FileUtils.extractFromJar(pathToJar, unobfuscatedFileName, finalObfuscatedFileName);

			//Finally, remove the .jar file we created
			FileUtils.delete(new File(pathToJar));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new File(finalObfuscatedFileName);
	}

	/**
	 * Performs an obfuscation using the Proguard library
	 * @param classFile
	 * @return the path to the now-obfuscated file.
	 */
	static File obfuscateClassFileUsingProguard(File classFile) {
		String unobfuscatedFileName = FileUtils.getFileNameWithoutExtension(classFile);
		String originalFileName = FileUtils.getFileNameWithoutExtension(classFile);
		String obfuscatedFileName = unobfuscatedFileName+".jar";
		String pathToFiles = classFile.getParent()+File.separator;
		String finalObfuscatedFileName = pathToFiles+unobfuscatedFileName + ".class";
		unobfuscatedFileName = unobfuscatedFileName+".class";
		ArrayList<String> paramsExecute = new ArrayList<String>();
		String proguardJarFilePath = System.getProperty("AGENT_PATH").concat(File.separator+"proguard.jar");
		String input = System.getProperty("user.dir")+File.separator+ "testFiles";
		/*
		 * We have to do something fishy here. If the java files reside in a separate project (i.e. in their own 
		 * folder) then we need to find out the name of the folder, so that we can append it to the "input" var.
		 * The only way to do this, is to remove all of the package stuff - and then see if the last folder in
		 * the hierarchy is "testfiles". If not, we need to grab that remaining bit and append accordingly. 
		 */
		String subfolderName = checkIfFilesAreInASubfolder(pathToFiles, input, finalObfuscatedFileName);
		if (!subfolderName.isEmpty()) {
			input = input.concat(subfolderName);
		}
//		String input = "C:\\Users\\maanni\\Desktop\\testFiles\\example1";
		paramsExecute.add("java");
		paramsExecute.add("-jar");
		paramsExecute.add(proguardJarFilePath);
		paramsExecute.add("-injars");
		paramsExecute.add(input);
		paramsExecute.add("-outjars");
		paramsExecute.add(pathToFiles+obfuscatedFileName);
		paramsExecute.add("-dontwarn");
		paramsExecute.add("-keep public class **" + originalFileName);
		paramsExecute.add("-keepclasseswithmembers class **" + originalFileName+"{ public static void main(java.lang.String[]); }");
		String pathToJar = pathToFiles+obfuscatedFileName;
		try {
			ProcessBuilder builderExecute = new ProcessBuilder(paramsExecute);
			Process process = builderExecute.start();
			process.waitFor();
			//next, extract the .class from the new .jar file
			FileUtils.extractFromJar(pathToJar, unobfuscatedFileName, finalObfuscatedFileName);

			//Finally, remove the .jar file we created
			FileUtils.delete(new File(pathToJar));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new File(finalObfuscatedFileName);
	}

	private static String checkIfFilesAreInASubfolder(String pathToFiles, String inputFolder, String obfuscatedFileName) {
		String packageName = "";
		try {
			ClassParser parser = new ClassParser(obfuscatedFileName);
			JavaClass javaClass = parser.parse();
			packageName = javaClass.getPackageName();
		} catch (Exception e) {
			System.out.println(e);
		}
		String pathToBasePackage = FileUtils.getBasePackagePath(pathToFiles, packageName);
		//if pathToBasePackage is not null/empty, then "subtract" inputFolder from it, and return whatever results
		if (!pathToBasePackage.isEmpty()) {
			String subfolder = pathToBasePackage.replace(inputFolder, "");
			return subfolder.isEmpty() ? "" : subfolder;
		}
		return "";
	}
}
