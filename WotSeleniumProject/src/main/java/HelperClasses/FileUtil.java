package HelperClasses;
import java.io.File;
import java.io.IOException;
import java.time.Instant;

public class FileUtil {

	public static boolean checkFileDownload(String folderPath, String expectedFileName, 
			String expectedFileExtension, int timeOut, int threadSleepValue) throws IOException 
	{		
	    // All Files in Directory
	    File[] files;
			
	    String individualFileName;   
	    boolean fileDownloaded = false;      
	        
	    // ignore old files
	    long startTime = Instant.now().toEpochMilli() - threadSleepValue;
	        
	    // Time to wait for download to finish
	    long waitTime = startTime + timeOut;
	       
	    // waiting
	    while (Instant.now().toEpochMilli() < waitTime) 
	    {			
	    	// refresh files in the folder
	        files = new File(folderPath).listFiles();
	            
	        for (File file : files) 
	        {
	            individualFileName = file.getName().toLowerCase();
	            System.out.println("AAA" + individualFileName + expectedFileName);
	        	System.out.println("BBB" + (individualFileName.contains(expectedFileName.toLowerCase()) 
	        			&& individualFileName.contains(expectedFileExtension.toLowerCase())));
	        	System.out.println("CCC" + file.lastModified() + " " + startTime);
	        	System.out.println("DDD" + (file.lastModified() > startTime));
	            // condition 1 - Disregard old files
	            // Condition 3 - Current File name contains expected Text
	            // Condition 4 - Current File name contains expected extension
	            if (file.lastModified() > startTime
	            		&& individualFileName.contains(expectedFileName.toLowerCase()) 
	            		&& individualFileName.contains(expectedFileExtension.toLowerCase())) 
	           {
	            	System.out.println(individualFileName);
	               // File Found
	               fileDownloaded = true;
	               // delete the file if it's found
	               file.delete();
	               break;
	           }
	        }
	        // File Found Break While Loop
	         if (fileDownloaded) 
	             break;
	    }
	    return fileDownloaded;
	}
}
