
import java.awt.Color;
import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.sql.Time;
import java.text.*;
import java.util.*;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import com.sun.java.swing.plaf.nimbus.FormattedTextFieldPainter;

public class Client implements Runnable{

public static boolean STATE_CONNECTED = false;	
	
private Socket socket;
private InputStreamReader reader;
private BufferedReader writerStream;
private BufferedWriter writer;
private String PDF_Content;
private Frame frame;
private Thread t;

public Client() throws IOException{
	frame = new Frame(this);
}

public void init(InetAddress address,int port) throws IOException, SQLException{
		socket = new Socket(address, port);
		STATE_CONNECTED = true;
		reader = new InputStreamReader(socket.getInputStream());
		writerStream = new BufferedReader(new InputStreamReader(System.in));
		writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		t = new Thread(this);
		t.start();
	
}
public void SendMessage(String Message) throws IOException{
		writer.write(Message.concat(";"));
		writer.flush();   
  
}
public void SendMessage() throws IOException{
		writer.write(writerStream.readLine().concat(";"));
		writer.flush();   
	  
}
public void CloseResources(){
	try{	
		reader.close();
		writer.close();
		writerStream.close();
		socket.close();
		STATE_CONNECTED = false;
	}catch (IOException e) {
		JOptionPane.showMessageDialog(null, "Problem closing resources: " + e.getMessage(), "Error", JOptionPane.INFORMATION_MESSAGE);
	}
}

public Thread getThread(){
	return t;
}

@Override
public void run() {
	boolean flag = false;
	int character;
	int header = 0;
	
	Database db = frame.getDatabase();
	ColorPane tArea = frame.getTextArea();
	
	StringBuilder data = new StringBuilder();
	//String[] tags = new String[db.getValues(frame.getSelectedTable()).length()];
	String[] tags = {"T", "T", "P"}; 
	String[] temp;
	String str = "";
	
	PDF_Content = "";
	Timer timer = new Timer();
	timer.schedule(
		new TimerTask(){
		@Override
		public void run() {
			try {
				SendMessage("OK");
			} catch (IOException e) {
				STATE_CONNECTED = false;
				frame.setConnectedState();
				t.interrupt();
				this.cancel();
				CloseResources();
			}
		}
		}	, 0, 500);
	while(true){
    	if(t.isInterrupted()){
    		System.out.println(PDF_Content);
    		break;
    	}
    	
    try {
    	if(!data.toString().isEmpty()){ //se resetea buffer de datos recibidos si tiene algo
    		data.delete(0, data.length());
    	}
    	if(reader.ready()){ // para que cuando te quieras desconectar no se quede trabado en reader.read()
			while((character = reader.read()) != 59) 
			 {
				     data.append((char) character);
				     flag = true;
			 }
    	}
		} catch (IOException e) {
			 e.printStackTrace();	
		}
    	 if(flag){
    		temp = data.toString().split(",");
    		flag = false;
    		System.out.println(data.toString());
    		if(temp.length == 1){
    			tArea.append(data.toString());
    			PDF_Content += tArea.getText();
    			continue;
    		}
    		if(temp[0].length() <= 2)header = Integer.valueOf(temp[0]);
    		switch(header){
    		case 1:
    			tags = data.toString().substring(13).split("    ");
    			for(int i = 0; i < tags.length; i++){
 	    			System.out.println(tags[i].charAt(0));
 	    		}
    			
    			 for(int i = 1; i < temp.length; i++){
 	    			tArea.append("\n" + temp[i]);
    				Log.log(temp[i]);
 	    		}
    			 break;
    		case 2:
    		ArrayList<String> values = new ArrayList<String>(4);
    		tArea.append("\n");
    		tArea.append(temp[1]);
    		//System.out.print(new Time(Calendar.getInstance(TimeZone.getDefault()).getTimeInMillis()).toString() + " ");
    		 for(int i = 2; i < temp.length; i++)
    		 {
    			
    			 if(tags[i-2].charAt(0) == 'P'){
    				 tArea.append("	" + temp[i] + "mbar");
    				 values.add(temp[i] + "mbar");
    				 str = data.toString().replace(temp[i], temp[i] + "mbar");
    				 
    			 }else if(tags[i-2].charAt(0) == 'T'){
    				 tArea.append("	" + temp[i] + "ºC");
    				 values.add(temp[i] + "ºC");
    				 str += data.toString().replace(temp[i], temp[i] + "ºC");
    			 }
    		 }
    		 db.updateTable(frame.getSelectedTable(), values);
					Log.log(data.toString());
    		 break;
    		case 3:    	
    			
    			 for(int i = 1; i < temp.length; i++){
    				tArea.append(Color.red, "\n" + temp[i]);
    				// tArea.appendHTML(temp[i],"color:red");
     					Log.log(temp[i]);

 	    		}
    			 break;
    		 default:
    			 for(int i = 0; i < temp.length; i++){
    	    			tArea.append("\n" + temp[i]);
        					Log.log(temp[i]);
    	    		}
    		}
    		if(!tArea.getForeground().equals(Color.black)){
    			tArea.setForeground(Color.black);
    		}
    		header = 0;
    		PDF_Content += tArea.getText();
    	 }
    }
}
public static void main(String[] args){
	String line;
	String pidInfo ="";
	try {
		
		Process p = Runtime.getRuntime().exec(System.getenv("windir") +"\\system32\\"+"tasklist.exe");
		BufferedReader input =  new BufferedReader(new InputStreamReader(p.getInputStream()));
		ProcessBuilder proc = new ProcessBuilder("C:\\xampp\\xampp-control.exe");
		
		while ((line = input.readLine()) != null) {
		    pidInfo+=line; 
		}

		input.close();
		if(!pidInfo.contains("xampp-control.exe"))
		{
			proc.start();
		
		}
		Client c = new Client();
		
	} catch (IOException e) {
		e.printStackTrace();
	}
		}
}


