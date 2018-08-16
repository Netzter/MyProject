
import java.awt.Color;
import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.util.*;

import javax.net.SocketFactory;
import javax.swing.JOptionPane;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;

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
	ArrayList<String> values = new ArrayList<String>(4);
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
    		try {
    			String path = "procesos/autoclave/serie-234/";
    			String id = "";
    			if(new File(path).exists()){
    				id = String.valueOf(new File(path).listFiles().length);
    			}
    			PDFManager pdf = new PDFManager(path + "reporte-configuracion" + id + ".pdf");
				pdf.writePdf(PDF_Content, new com.itextpdf.text.Paragraph("Reporte de configuracion"));
				//pdf.close();
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
    		tArea.append("\n");
    		tArea.append(temp[1]);
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
    		
    		 Log.log(data.toString());
    		 db.updateTable(frame.getSelectedTable(), values);
    		 values.clear();
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
    		header = 0;
    		PDF_Content += tArea.getText();
    	 }
    }
}
public static String getProcessOutput(String processName){
	String line;
	String pidInfo ="";
	try {
		
		Process p = Runtime.getRuntime().exec(processName);  // System.getenv("windir") +"\\system32\\"+"tasklist.exe"
		BufferedReader input =  new BufferedReader(new InputStreamReader(p.getInputStream()));
		
		while ((line = input.readLine()) != null) {
		    pidInfo+=line + "\n"; 
		}

		input.close();
	} catch (IOException e) {
		e.printStackTrace();
	}
	return pidInfo;
}
public static void main(String[] args){
	String netConfig = getProcessOutput("C:\\workspace\\test.bat");
	String subnet = netConfig.substring(netConfig.indexOf("255", netConfig.indexOf("Máscara de subred")), 
					netConfig.indexOf("\n", netConfig.indexOf("Máscara de subred")));
		System.out.println(subnet);
	try {
		/*if(subnet != "255.255.255.0"){
			String str1="192.168.0.201";
			String str2="255.255.255.0";
			String[] command1 = { "netsh", "interface", "ip", "set", "address",
			"name=", "Local Area Connection" ,"source=static", "addr=",str1,
			"mask=", str2};
			Process pp = Runtime.getRuntime().exec(command1);
		}*/
		Process p;
		String pList = getProcessOutput("tasklist");
		if(!pList.contains("mysqld.exe") || !pList.contains("httpd.exe"))
		{
			p = Runtime.getRuntime().exec("C:\\xampp\\mysql\\bin\\mysqld.exe");
			p = Runtime.getRuntime().exec("C:\\xampp\\apache\\bin\\httpd.exe");
			while(!getProcessOutput("tasklist").contains("mysqld.exe") || !getProcessOutput("tasklist").contains("httpd.exe"));
		}
			
		Client c = new Client();
		
	} catch (IOException e) {
		e.printStackTrace();
	}
		}
}


