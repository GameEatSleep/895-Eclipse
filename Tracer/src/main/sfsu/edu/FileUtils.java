package main.sfsu.edu;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipException;

/**
 * 
 * FileUtils.java
 * 
 * A set of helper methods used for file operations, such as file name retrieval, copying and deletion. 
 * 
 * @author anaqvi
 *
 */

public class FileUtils {

	/**
	 * Used to get the name of a file, without the associated extension. Useful for 
	 * ensuring that we can build an accompanying obfuscated file with the same name.
	 * @param f1 the file for which we want to get the filename
	 * @return the name of the file f1 without it's extension
	 */
	public static String getFileNameWithoutExtension(File f1) {
		String fileName = f1.getName();
		int pos = fileName.lastIndexOf(".");
		if (pos > 0) {
			fileName = fileName.substring(0, pos);
		}
		return fileName;
	}

	/**
	 * Performs a binary copy of the given file "source" into the desired output file "dest"
	 * @param source the original file
	 * @param dest the target file into which we wish to copy
	 * @throws IOException
	 */
	static void copyFileUsingFileStreams(File source, File dest)
			throws IOException {
		InputStream input = null;
		OutputStream output = null;
		try {
			input = new FileInputStream(source);
			output = new FileOutputStream(dest);
			byte[] buf = new byte[1024];
			int bytesRead;
			while ((bytesRead = input.read(buf)) > 0) {
				output.write(buf, 0, bytesRead);
			}
		} finally {
			input.close();
			output.close();
		}
	}

	/**
	 * Gets the extension of a given file
	 * @param f1 the file from which we want the extension
	 * @return the extension for the given file
	 */
	public static String getExtensionWithoutFileName(File f1) {
		String extension = f1.getName();
		int pos = extension.lastIndexOf(".");
		if (pos > 0) {
			extension = extension.substring(pos);
		}
		return extension;
	}

	/**
	 * Deletes a file or folder
	 * @param f the file or folder to delete.
	 * @throws IOException
	 */
	public static void delete(File f) throws IOException {
		if (f.isDirectory()) {
			for (File c : f.listFiles())
				delete(c);
		}
		if (!f.delete())
			throw new FileNotFoundException("Failed to delete file: " + f);
	}

	/**
	 * Extracts the compiled class from a jar. The obfuscation toolsets usually output in .jar format, requiring the use of this function
	 * @param jarFile the jar file containing the class we wish to analyze
	 * @param archivedFileName the name of the class file within the jar
	 * @param outputFileName the file name to use for the extracted file
	 * @throws IOException
	 */
	public static void extractFromJar (String jarFile, String archivedFileName, String outputFileName) throws IOException {
		JarFile jar = null;
		try {
			jar = new JarFile(jarFile);
			JarEntry foundEntry = null;
			for (Enumeration<JarEntry> entries = jar.entries(); entries.hasMoreElements(); ) {
				JarEntry entry = entries.nextElement();
				//get the last bit of the name - that means, the last slash to end of string
				int positionOfLastSlash = entry.getName().lastIndexOf("/");

				String entryName = entry!= null ? entry.getName().substring(1 + positionOfLastSlash ) : "";
				if (entryName.equals(archivedFileName) || archivedFileName.equals(entry.getName())) {
					foundEntry = entry;
					break;
				}
			}
			if (foundEntry != null) {
				InputStream input = jar.getInputStream(foundEntry);
				OutputStream output = new FileOutputStream(outputFileName);
				try {
					byte[] buffer = new byte[input.available()];
					for (int i = 0; i != -1; i = input.read(buffer)) {
						output.write(buffer, 0, i);
					}
				} finally {
					jar.close();
					input.close();
					output.close();
				}
			} else {
				System.out.println("Could not find class " + archivedFileName + " - skipping...");
				//				System.exit(0);
			}
		} catch (ZipException ze) {
			// Sometimes, if proguard encounters a failure, we cannot get a compressed JAR archive. Proceed without and do nothing.
		}
	}

	/**
	 * Given a file in a package, gets the root package for that file so that we can execute it dynamically.
	 * 
	 * For example, if the file has the package declaration "main.hello", and the file resides in "test/example/main/hello",
	 * we have to strip off the "main/hello" so that we execute it from the base package folder. The packagename will be set
	 * during classloading.
	 * @param pathToFile
	 * @param packageName
	 * @return
	 */
	static String getBasePackagePath(String pathToFile, String packageName) {
		if (packageName == null || packageName.length() == 0) {
			return pathToFile;
		}
		String temp = pathToFile.replace(File.separatorChar, '.');
		String tempValue = temp.substring(0,temp.indexOf(packageName));
		//put the slashes back in and return it
		return tempValue.replace('.', File.separatorChar);
	}
}
