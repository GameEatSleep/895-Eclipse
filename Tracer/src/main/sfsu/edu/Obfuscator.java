package main.sfsu.edu;

import java.io.File;
import java.io.FileNotFoundException;
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
		String pathToFiles = originalClassFile.getParent()+File.separator;
		String finalObfuscatedFileName = pathToFiles+unobfuscatedFileName + ".class";
		unobfuscatedFileName = unobfuscatedFileName+".class";

		String pathToJar = System.getProperty("user.dir")+File.separator+".."+File.separator+"snapshotj.jar";
		try {
			//next, extract the .class from the new .jar file
			FileUtils.extractFromJar(pathToJar, unobfuscatedFileName, finalObfuscatedFileName);

			} catch (IOException e) {
			e.printStackTrace();
		} 
		return new File(finalObfuscatedFileName);
	}

	/**
	 * Performs an obfuscation using the Proguard library
	 * @param classFile
	 * @return the path to the now-obfuscated file.
	 * @throws IOException
	 * Since the final decision for this project was to verify the Jbox2D library, we will leave this out for the moment.
	 * However, it does permit users to perform dynamic weaving so I will not remove it altogether 
	 */
	@Deprecated
	static File obfuscateClassFileUsingProguard(File classFile) throws IOException {
		String unobfuscatedFileName = FileUtils.getFileNameWithoutExtension(classFile);
		String originalFileName = FileUtils.getFileNameWithoutExtension(classFile);
		String obfuscatedFileName = unobfuscatedFileName+".jar";

		String pathToFiles = classFile.getCanonicalPath().substring(0, classFile.getCanonicalPath().lastIndexOf(File.separator)) +File.separator; //need to get the parent path
		String finalObfuscatedFileName = pathToFiles+unobfuscatedFileName + ".class";
		unobfuscatedFileName = unobfuscatedFileName+".class";
		ArrayList<String> paramsExecute = new ArrayList<String>();
		String proguardJarFilePath = System.getProperty("user.dir")+File.separator+"proguard.jar";
		File tempFile = new File (System.getProperty("user.dir")+File.separator+".."+File.separator+ "testFiles" + File.separator+"src");
		String input = tempFile.getCanonicalPath(); //since we want to remove any relative pathing (eg ".." characters)
		/*
		 * We have to do something tricky here. If the java files reside in a separate project (i.e. in their own 
		 * folder) then we need to find out the name of the folder, so that we can append it to the "input" var.
		 * The only way to do this, is to remove all of the package stuff - and then see if the last folder in
		 * the hierarchy is "testfiles". If not, we need to grab that remaining bit and append accordingly. 
		 */
		String subfolderName = checkIfFilesAreInASubfolder(pathToFiles, input, finalObfuscatedFileName);
		if (!subfolderName.isEmpty()) {
			input = input.concat(subfolderName);
		}
		paramsExecute.add("java");
		paramsExecute.add("-jar");
		paramsExecute.add(proguardJarFilePath);
		paramsExecute.add("@myconfig.pro");
		String pathToJar = pathToFiles+obfuscatedFileName;
		try {
			ProcessBuilder builderExecute = new ProcessBuilder(paramsExecute);
			builderExecute.inheritIO();

			Process process = builderExecute.start();
			process.waitFor();
			//next, extract the .class from the new .jar file
			FileUtils.extractFromJar(pathToJar, unobfuscatedFileName, finalObfuscatedFileName);

			//Finally, remove the .jar file we created
			FileUtils.delete(new File(pathToJar));
		} catch (FileNotFoundException fnfe){
			//Do nothing - if the file is not there, we obviously cant analyze it.
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return new File(finalObfuscatedFileName);
	}

	static File obfuscateProjectUsingProguard(File classFile) throws IOException {
		String unobfuscatedFileName = FileUtils.getFileNameWithoutExtension(classFile);
		String pathToFiles = classFile.getCanonicalPath().substring(0, classFile.getCanonicalPath().lastIndexOf(File.separator)) +File.separator; //need to get the parent path
		String finalObfuscatedFileName = pathToFiles+unobfuscatedFileName + ".class";
		unobfuscatedFileName = unobfuscatedFileName+".class";
		String pathToJar= System.getProperty("user.dir")+File.separator+".."+File.separator+"snapshotp.jar";
		FileUtils.extractFromJar(pathToJar, unobfuscatedFileName, finalObfuscatedFileName);
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
