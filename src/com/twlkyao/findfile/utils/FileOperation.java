/**
 * @Author:		Shiyao Qi
 * @Date:		2013.11.25
 * @Function:	Operations on file
 * @Email:		qishiyao2008@126.com
 */

package com.twlkyao.findfile.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class FileOperation {
	
	/**
	 * The hex digits, used to convert the bytes to hex digits,
	 * it is used by the apache checksum
	 */
	private static char hexDigits[] = {
		'0', '1', '2', '3',
		'4', '5', '6', '7',
		'8', '9', 'a', 'b',
		'c', 'd', 'e', 'f'
		};
	
	private static MessageDigest md5digest = null; // Used to store the md5 digest information
	// Check whether the MD5 MessageDigest is supported
	static {
		try {
			md5digest = MessageDigest.getInstance("MD5");
		} catch  (NoSuchAlgorithmException nsaex) {
			System.err.println(FileOperation.class.getName()
				+ "Initialized error!, MessageDigest doesn't support MD5!");
			nsaex.printStackTrace();
		}
	}
	
	private static MessageDigest sha1digest = null; // Used to store the sha1 digest information
	// Check whether the SHA1 MessageDigest is supported
	static {
		try {
			sha1digest = MessageDigest.getInstance("SHA-1");
		} catch  (NoSuchAlgorithmException nsaex) {
			System.err.println(FileOperation.class.getName()
				+ "Initialized error!, MessageDigest doesn't support SHA1!");
			nsaex.printStackTrace();
		}
	}
	
	/**
	 * Find the specified files according to the filepath
	 * @param keyword The keyword
	 * @param filepath The filepath to start
	 * @return A list that contains the files that meet the conditions
	 */
	public ArrayList<File> findFileByName(String keyword, String filepath) {
		File files = new File(filepath); // Create a new file
		ArrayList<File> list = new ArrayList<File>(); // Used to store the search result
		for(File file : files.listFiles()) { // Recursively search the file
			
//			Log.d("ListFiles",file.getAbsolutePath()); // Log the files under the specified filepath
			
			if(file.isDirectory()) { // The variable file is a directory
				
//				Log.d("ListFile","Directory:"+file.getAbsolutePath()); // If the variable is a directory log it
				
				if(file.getName().contains(keyword)) { // If the filepath contains the keyword, add it to the list
					list.add(file);
				}
				if(file.canRead()) {  // Without this the program will collapse
					list.addAll(findFileByName(keyword, file.getAbsolutePath())); // Recursive into the filepath
				}
				
			} else { // The variable file is a file
				
//				Log.d("ListFile","File:"+file.getAbsolutePath());
				if(file.getName().contains(keyword)) { // If the file's name contains the keyword, add it to the list
					list.add(file);
				}
			}
		}
		return list;
	}
	
	/**
	 * Get the md5 value of the filepath specified file
	 * @param filePath The filepath of the file
	 * @return The md5 value
	 */
	public String fileToMD5(String filePath) {
	    InputStream inputStream = null;
	    try {
	        inputStream = new FileInputStream(filePath); // Create an FileInputStream instance according to the filepath
	        byte[] buffer = new byte[1024]; // The buffer to read the file
	        MessageDigest digest = MessageDigest.getInstance("MD5"); // Get a MD5 instance
	        int numRead = 0; // Record how many bytes have been read
	        while (numRead != -1) {
	            numRead = inputStream.read(buffer);
	            if (numRead > 0)
	                digest.update(buffer, 0, numRead); // Update the digest
	        }
	        byte [] md5Bytes = digest.digest(); // Complete the hash computing
	        return convertHashToString(md5Bytes); // Call the function to convert to hex digits
	    } catch (Exception e) {
	        return null;
	    } finally {
	        if (inputStream != null) {
	            try {
	                inputStream.close(); // Close the InputStream
	            } catch (Exception e) { }
	        }
	    }
	}
	
	/**
	 * Get the sha1 value of the filepath specified file
	 * @param filePath The filepath of the file
	 * @return The sha1 value
	 */
	public String fileToSHA1(String filePath) {
	    InputStream inputStream = null;
	    try {
	        inputStream = new FileInputStream(filePath); // Create an FileInputStream instance according to the filepath
	        byte[] buffer = new byte[1024]; // The buffer to read the file
	        MessageDigest digest = MessageDigest.getInstance("SHA-1"); // Get a SHA-1 instance
	        int numRead = 0; // Record how many bytes have been read
	        while (numRead != -1) {
	            numRead = inputStream.read(buffer);
	            if (numRead > 0)
	                digest.update(buffer, 0, numRead); // Update the digest
	        }
	        byte [] sha1Bytes = digest.digest(); // Complete the hash computing
	        return convertHashToString(sha1Bytes); // Call the function to convert to hex digits
	    } catch (Exception e) {
	        return null;
	    } finally {
	        if (inputStream != null) {
	            try {
	                inputStream.close(); // Close the InputStream
	            } catch (Exception e) { }
	        }
	    }
	}

	/**
	 * Convert the hash bytes to hex digits string
	 * @param hashBytes
	 * @return The converted hex digits string
	 */
	private static String convertHashToString(byte[] hashBytes) {
		String returnVal = "";
		for (int i = 0; i < hashBytes.length; i++) {
			returnVal += Integer.toString(( hashBytes[i] & 0xff) + 0x100, 16).substring(1);
		}
		return returnVal.toLowerCase();
	}
}
