package filehandler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.nio.file.Files;
import java.nio.file.Path;

import gui.Logger;
import gui.Main;
import gui.MessageOrigin;
import gui.MessageType;
import settings.SettingHandler;


public class Manager {
	
	public static final String SEPERATOR = File.separator;
	public static final String PATH = System.getProperty("user.home") + Manager.SEPERATOR + "Interface Oasis"; //Base program folder path
	private static int fID = 0;
	private static ArrayList<File> files = new ArrayList<File>();
	
	public static int newFile(String path) {
		File file = new File(path);
		int fileID = newFileID();
		if (!file.exists()) {
			try {
				file.createNewFile();
				file.setWritable(true);
				file.setReadable(true);
			} catch (Exception e) {
				reportError("File Creator", "Unable to create file with path " + path, e.getMessage(), true);
				Logger.reportException("Manager", "newFile", e);
				return -1;
			}
		}
		Manager.files.add(file);
		return fileID;
	}
	
	public static boolean writeFile(int fileID, String input, boolean append) {
		File file = files.get(fileID);
		
		FileWriter fileWriter;
		BufferedWriter writer;
		
		try {
			fileWriter = new FileWriter(file, append);
			writer = new BufferedWriter(fileWriter);
			writer.write(input);
			writer.flush();
			writer.close();			
		} catch (Exception e) {
			reportError("File Writer", "Unable to write to file with path " + files.get(fileID).getPath(), e.getMessage(), true);
			Logger.reportException("Manager", "writeFile", e);

			return false;
		}
		return true;
	}
	
	public static String readFile(int fileID) {
		File file = files.get(fileID);
		
		FileReader fileReader;
		BufferedReader reader;
		
		try {
			fileReader = new FileReader(file);
			reader = new BufferedReader(fileReader);
			String result = "";
			String line;
			while ((line = reader.readLine()) != null) {
				result += line + "\n";
			}
			reader.close();
			fileReader.close();
			return result;
		} catch (Exception e) {
			reportError("File Reader", "Unable to read from file with path " + files.get(fileID).getPath(), e.getMessage(), true);
			Logger.reportException("Manager", "readFile", e);
			return null;
		}
	}
	
	public static String copyFile(int fileID) {
		String oldContent = readFile(fileID);
		String oldPath = getDirPath(fileID);
		String newPath = oldPath + Manager.SEPERATOR + "Setting Backup";
		int i = 1;
		for (boolean unique = false; !unique; ) {
			String addon = " (" + i + ").xml";
			try {
				if (!Files.exists(Path.of(newPath + addon))) {
					newPath += addon;
					unique = true;
				} else {
					i++;
				}
			} catch (Exception e) {
				reportError("File Copyer", "Unable to copy file with path " + files.get(fileID).getPath(), e.getMessage(), true);
				Logger.reportException("Manager", "copyFile", e);
				return null;
			}
			
		}
		int copyTo = newFile(newPath);
		Manager.writeFile(copyTo, oldContent, false);
		return newPath;
	}
	
	public static boolean delFile(int fileID) {
		return files.get(fileID).delete();
	}
	
	private static int newFileID() {
		return fID++;
	}
	
	public static String checkPath(String path) { //Only folder paths not files
		if (!Files.exists(Path.of(path))) {
			try {
				Files.createDirectories(Path.of(path));
			} catch (IOException e) {
				reportError("Path Checker", "Unable to create path for setting file", e.getMessage(), true);
				Logger.reportException("Manager", "checkPath", e);
			}		
		}

		return path;
	}
	
	public static String getDirPath(int fileID) {
		return Manager.files.get(fileID).getAbsoluteFile().getParent();
	}
	
	public static String getPath(int fileID) {
		return Manager.files.get(fileID).getAbsolutePath();
	}
	
	public String getFileName(int fileID) {
		return Manager.files.get(fileID).getName();
	}
	
	private static void reportError(String source, String causes, String errorMessage, boolean isFatal) {
		String message = source + " in the File Handler reported " + causes + " caused by " + errorMessage;
		String[] elements = {"ID", "Origin", "Source", "Causes", "ErrorMessage"};
		String[] values = {SettingHandler.FILEHANDLERID, MessageOrigin.FileHandler.name(), source, causes, errorMessage};
		Logger.addMessage(MessageType.Error, MessageOrigin.FileHandler, message, SettingHandler.FILEHANDLERID, elements, values, isFatal);
	}
}
