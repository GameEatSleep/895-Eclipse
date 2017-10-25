package main.sfsu.edu;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.JavaClass;

public class FileAnalysisUtils {

	static AnalyzedFile rateSingleFile(File file, CallDepthAnalysis cdAnalysis, ObfuscationType obfuscationType) {
		@SuppressWarnings("unused")
		int methodCount = 0, fieldCount = 0, bytesCounter = 0, Alen = 0,cpoolCount=0;
		AnalyzedFile af = new AnalyzedFile();
		try {
			// get the name of the class we're analyzing
			String srcFileName = file.getAbsolutePath();
			srcFileName = srcFileName.replace(".java", ".class");
			/* Parse the class */
			ClassParser parser = new ClassParser(srcFileName);
			JavaClass javaClass = parser.parse();
			fieldCount = javaClass.getFields().length;
			bytesCounter = javaClass.getBytes().length;
			methodCount = javaClass.getMethods().length;
			ConstantPool cpool =  javaClass.getConstantPool();
			cpoolCount = cpool.getLength();
			af.setFileSize(bytesCounter);
			af.setNumMethods(methodCount);
			af.setNumFields(fieldCount);
			af.setCpoolSize(cpoolCount);
			af.setCallDepth(cdAnalysis.getCallDepth());
			af.setCallFlow(cdAnalysis.getCallFlow());
			af.setFileName(file.getName());
			af.setObfuscationType(obfuscationType);
			af.setPathToFile(file.getAbsolutePath());
			return af;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
