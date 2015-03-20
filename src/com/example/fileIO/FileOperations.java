package com.example.fileIO;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.TreeMap;
import android.os.Environment;

/*
 * Code Written By  : Rakesh Manoharan & Pavan Trinath
 * Net ID 			: rxm143130 & BXV131230
 * Course 			: CS6301
 * Class Name		: FileOperations.java
 * Date				: 12-30-2014
 * Description 		:
 * 						FileOperations is the class used to do all the functions related
 * to the file. All the user scores are stored into the text file through this class.
 *  
 * Write function is used to write the data onto the text file. Input to this function is
 * TreeMap with key, value pairs. Key contains the time taken by the user to complete
 * a game successfully. Value consists the name of the player. One advantage of TreeMap is that it allows us 
 * to sort the time taken on the ascending order.
 */



public class FileOperations {
   public FileOperations() {
      }
   /*
   * Code Written By  	: Rakesh Manoharan
   * Net ID 			: rxm143130
   * Course 			: CS6301
   * Function Name		: Boolean write(TreeMap<Long,String> userData)
   * Date				: 12-30-2014
   * Description 		:
   *	Write function is used to write all the data from the TreeMap onto the text file
   *	Returns the boolean value saying whether the write function is executed successfully.
   */
   public Boolean write(TreeMap<Long,String> userData){
      try {
    	  
        String fpath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()+"/HighScoresBarrellRacing.txt";
        
        // If file does not exists, then create it

        File file = new File(fpath);
        
        if (!file.exists()) {
        	file.createNewFile();
		}
        FileWriter fileWriter = null;
		fileWriter = new FileWriter(file);
		String strLine="";
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        
        for(Map.Entry<Long, String> Iterator:userData.entrySet())
        {
        	if(Iterator.getKey()!=null)
        	{
        		strLine=strLine.concat(Long.toString(Iterator.getKey())).concat(",").concat(Iterator.getValue()).concat(":");
           	}
		
        }
        
    	bufferedWriter.write(strLine);
		bufferedWriter.close();
	return true;

        
        
      } catch (IOException e) {
        e.printStackTrace();
        return false;
      }
   }

   
   /*
   * Code Written By  	: Pavan Trinath
   * Net ID 			: BXV131230
   * Course 			: CS6301
   * Function Name		: TreeMap<Long,String> read()
   * Date				: 12-30-2014
   * Description 		:
   *	Read function is used to read all the data from the text file into the TreeMap.
   *	Returns the tree map it constructs from the text file.
   */

   
   
   public TreeMap<Long,String> read(){
	     BufferedReader br = null;
	     try {
	        String fpath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()+"/HighScoresBarrellRacing.txt";
	        
	        File file = new File(fpath);
	        if (!file.exists()) {
	    		file.createNewFile();
	    	}    
	      FileInputStream in = new FileInputStream(file.getAbsoluteFile());
	      br = new BufferedReader(new InputStreamReader(in));
	      String strLine;
	      Map<Long,String> allUserData=new TreeMap<Long,String>();

	      strLine=br.readLine();
	      if(strLine!=null)
	      {
	    	  String[] game=strLine.split(":");
	    	  for(int i=0;i<game.length;i++)
	    	  {
	    		  String[] player=game[i].split(",");
	    		  String scoreString=player[0];
	    		  long score=Long.parseLong(scoreString);
	    		  allUserData.put(score, player[1]);
	    		  
	    	  }
	    	 
	      }
	      br.close();
	    return (TreeMap<Long, String>) allUserData;
	     
	        
	      } catch (IOException e) {
	        e.printStackTrace();
	        return null;
	      }
	      
	   }
}