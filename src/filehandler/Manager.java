package filehandler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Manager {
	
	public static final String SEPERATOR = File.pathSeparator;
	private static int fID = 0;
	private static ArrayList<File> files = new ArrayList<File>();
	private static ArrayList<String> errorMessages = new ArrayList<String>();
	
	public static int newFile(String path) {
		File file = new File(path);
		int fileID = newFileID();
		if (!file.exists()) {
			try {
				
			} catch (Exception e) {
				setError(fileID, e.getMessage());
				return -1;
			}
		}
		Manager.files.add(file);
		Manager.errorMessages.add("");
		return fileID;
	}
	
	public static boolean writeFile(int fileID, String input, boolean append) {
		File file = files.get(fileID);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (Exception e) {
				setError(fileID, e.getMessage());
				return false;
			}
		}
		
		FileWriter fileWriter;
		BufferedWriter writer;
		
		try {
			fileWriter = new FileWriter(file, append);
			writer = new BufferedWriter(fileWriter);
			writer.write(input);
			writer.flush();
		} catch (Exception e) {
			setError(fileID, e.getMessage());
			return false;
		}
		try {
			writer.close();			
		} catch (Exception e) {
			setError(fileID, e.getMessage());
			return false;
		}
		return true;
	}
	
	public static String readFile(int fileID) {
		File file = files.get(fileID);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				setError(fileID, e.getMessage());
				return null;
			}
		}
		
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
			setError(fileID, e.getMessage());
			return null;
		}
	}
	
	public static boolean delFile(int fileID) {
		return files.get(fileID).delete();
	}
	
	private static void setError(int fileID, String errorMessage) {
		errorMessages.set(fileID, errorMessage);
	}
	
	private static int newFileID() {
		return fID++;
	}

}
