package main.sfsu.edu;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class FolderTraverse {
	public Map<String, File> foundFiles = new HashMap<String, File>();

	public void walk(String path, String desiredExtension) {
		File root = new File(path);
		File[] list = root.listFiles();

		if (list == null)
			return;

		for (File f : list) {
			if (f.isDirectory() && !f.getName().equals("obftmp")) {
				walk(f.getAbsolutePath(), desiredExtension);
			} else {
				String fileNameOnly = f.getName().replaceFirst("[.][^.]+$", "");
				if (f.getName().endsWith(desiredExtension)) {
					foundFiles.put(fileNameOnly, f);
				}
			}
		}
	}
}