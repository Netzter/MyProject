import java.io.*;
import java.text.*;
import java.util.*;

     public abstract class Log {
    	 private static boolean isFirstLog = true;
         public static void log(String s){
         TimeZone tz = TimeZone.getDefault(); // or TimeZone.getTimeZone("EST"),PST, MID, etc ...
         Date now = new Date();
         DateFormat df = new SimpleDateFormat ("yyyy.MM.dd hh:mm:ss ");
         df.setTimeZone(tz);
         String currentTime = df.format(now);
         final String path = "C:/Documents and Settings/Gonzalo/workspace/log.txt";
         FileWriter aWriter;
         try{
         if(new File(path).exists()){
        	 aWriter = new FileWriter(path, true);
         }
         else{
        	 File f = new File(path);
          	 aWriter = new FileWriter(f, true); 
         }
         if(isFirstLog){
        	 aWriter.write("\r\n");
        	 isFirstLog = false;
         }
         aWriter.write("\r\n");
         aWriter.write(currentTime + " " + s);
         aWriter.flush();
         aWriter.close();
         }catch(IOException e){
			 e.printStackTrace();
		 }
     }
 		public static String readFile(String path) throws IOException{
			StringBuilder data;
			FileReader fReader;
			int character;
			if(new File(path).exists()){
				fReader = new FileReader(path);
				data = new StringBuilder();
				while ((character = fReader.read()) != -1) 
				 {
					     data.append((char) character);
				 }
				fReader.close();
				return data.toString();
	         }
	         else{
	        	 File f = new File(path);
	        	 return "";
	         }
		}
		public static void writeFile(String path, String[] params, boolean append){
			 FileWriter fWriter;
			 try{
		         if(new File(path).exists()){
		        	 fWriter = new FileWriter(path, append);
		         }
		         else{
		        	 File f = new File(path);
		          	 fWriter = new FileWriter(f, append); 
		         }
		         if(!append)fWriter.write("-------------------Configuration File-------------------\r\n \r\n");
		         if(params != null){
			         for(int i = 0; i < params.length; i++){
			        	 fWriter.write(params[i] + "\r\n");
			         } 
		         }

		         fWriter.flush();
		         fWriter.close();
			 }catch(IOException e){
				 e.printStackTrace();
			 }

	         
		}
 }
