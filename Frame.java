import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Scanner;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;


public class Frame extends JFrame implements ActionListener{
	
		private static String home_directory = "C:/workspace/";
	
		private byte[] address = {(byte)192,(byte)168,(byte)0,(byte)53};
		private ColorPane textArea = new ColorPane();
		JScrollPane scrollPane= new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	  	private JTextField[] marksField = new JTextField[2];
	  	
	  	private int port = 2000; 
	  	private Client client;
	  	private MenuBar menuBar = new MenuBar(); // first, create a MenuBar item	  	
	  	
		private Menu file = new Menu(); // our File menu
		private MenuItem openFile = new MenuItem();  // an open option
		private MenuItem saveFile = new MenuItem(); // a save option
		private MenuItem close = new MenuItem(); // and a close option!
		
		private Menu conection = new Menu();
		private MenuItem start = new MenuItem();
		private MenuItem disconnect = new MenuItem();
		private MenuItem refresh = new MenuItem();
		private MenuItem configure = new MenuItem();
		private Menu registry = new Menu();
		private MenuItem parameters = new MenuItem();
		private MenuItem connection_state = new MenuItem();
		private MenuItem selected_register = new MenuItem();
		
		private final PopupMenu popup = new PopupMenu();
		private MenuItem clear = new MenuItem();
		private Database  database = new Database("Parametros");
		private JPanel p1 = new JPanel();
		private String table_name = "autoclave_vapor_2";
	 
		public Frame(Client c) throws IOException {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (ClassNotFoundException e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
			} catch (InstantiationException e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
			} catch (IllegalAccessException e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
			} catch (UnsupportedLookAndFeelException e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
			}
			setSize(500, 300); // set the initial size of the window
			//setExtendedState(JFrame.MAXIMIZED_BOTH);
			//setState(JFrame.MAXIMIZED_BOTH);
			client = c;
			String data = Log.readFile(home_directory + "config.txt");
			
			if(data.isEmpty()){
				marksField[0] = new JTextField("198.162.0.1");
				marksField[1] = new JTextField("2000");
			}else{
				marksField[0] = new JTextField(data.substring(data.indexOf(":")+2, data.indexOf("\r\n", 60)));
				marksField[1] = new JTextField(data.substring(data.lastIndexOf(":")+2, data.lastIndexOf("\r\n")));
			}
			initFiles();
			getConfig();

			setTitle("Registro de Autoclave"); // set the title of the window
			setDefaultCloseOperation(EXIT_ON_CLOSE); // set the default close operation (exit when it gets closed)
			textArea.setFont(new Font("Arial", Font.PLAIN, 12)); // set a default font for the TextArea
			clear.setLabel("Clear Window");
			clear.addActionListener(this);
			popup.add(clear);
			textArea.add(popup);
			//textArea.setBackground(Color.WHITE);
			textArea.setEditable(false);
			textArea.addMouseListener(new MouseAdapter(){
				 public void mouseReleased(MouseEvent e) {
				        if(e.getButton() == MouseEvent.BUTTON3)popup.show(textArea, e.getX(), e.getY());
				    }
			});
			
			//this.setContentPane(scrollPane);
			setMenuBar(menuBar);
			p1.setLayout(new BorderLayout()); // the BorderLayout bit makes it fill it automatically
			p1.add(scrollPane);
			p1.setVisible(true);
			add(p1);
			menuBar.add(file); // we'll configure this later
			file.setLabel("Archivo");
			
			openFile.setLabel("Abrir..."); // set the label of the menu item
			openFile.addActionListener(this); // add an action listener (so we know when it's been clicked
			openFile.setShortcut(new MenuShortcut(KeyEvent.VK_O, false)); // set a keyboard shortcut
			file.add(openFile); // add it to the "File" menu
	 
			// and the save...
			saveFile.setLabel("Guardar");
			saveFile.addActionListener(this);
			saveFile.setShortcut(new MenuShortcut(KeyEvent.VK_S, false));
			file.add(saveFile);
	 
			// and finally, the close option
			close.setLabel("Cerrar");
			close.addActionListener(this);
			file.addSeparator();
			file.add(close);
			
			menuBar.add(conection);
			conection.setLabel("Conexion");
			
			start.setLabel("Conectar");
			start.addActionListener(this);
			start.setShortcut(new MenuShortcut(KeyEvent.VK_C, false));
			conection.add(start);
			
			disconnect.setLabel("Desconectar");
			disconnect.addActionListener(this);
			disconnect.setShortcut(new MenuShortcut(KeyEvent.VK_D, false));
			conection.add(disconnect);
			
			conection.addSeparator();
			refresh.setLabel("Actualizar");
			refresh.addActionListener(this);
			refresh.setShortcut(new MenuShortcut(KeyEvent.VK_R, false));
			conection.add(refresh);
			
			setConnectedState();
			connection_state.addActionListener(this);
			conection.add(connection_state);
			
			conection.addSeparator();
			configure.setLabel("Configuracion");
			configure.addActionListener(this);
			configure.setShortcut(new MenuShortcut(KeyEvent.VK_C, false));
			conection.add(configure);
			
			menuBar.add(registry);
			registry.setLabel("Registro");
			
			parameters.setLabel("Parametros");
			parameters.addActionListener(this);
			registry.add(parameters);
			
			selected_register.setLabel("Seleccionado: " + table_name);
			registry.add(selected_register);
			setVisible(true);
		}
		
		public void actionPerformed (ActionEvent e) {
			// if the source of the event was our "close" option
			if (e.getSource() == close)
				this.dispose(); // dispose all resources and close the application
	 
			// if the source was the "open" option
			else if (e.getSource() == openFile) {
				JFileChooser open = new JFileChooser(); // open up a file chooser (a dialog for the user to browse files to open)
				int option = open.showOpenDialog(this); // get the option that the user selected (approve or cancel)
				if (option == JFileChooser.APPROVE_OPTION) {
					this.textArea.setText(""); // clear the TextArea before applying the file contents
					try {
						// create a scanner to read the file (getSelectedFile().getPath() will get the path to the file)
						Scanner scan = new Scanner(new FileReader(open.getSelectedFile().getPath()));
						while (scan.hasNext()) // while there's still something to read
							this.textArea.append(scan.nextLine() + "\n"); // append the line to the TextArea
					} catch (Exception ex) { // catch any exceptions, and...
						System.out.println(ex.getMessage());
					}
				}
			}
			else if (e.getSource() == saveFile) {
				JFileChooser save = new JFileChooser(); // again, open a file chooser
				int option = save.showSaveDialog(this); // similar to the open file, only this time we call
				if (option == JFileChooser.APPROVE_OPTION) {
					try {
						// create a buffered writer to write to a file
						BufferedWriter out = new BufferedWriter(new FileWriter(save.getSelectedFile().getPath()));
						out.write(this.textArea.getText()); // write the contents of the TextArea to the file
						out.close(); // close the file stream
					} catch (Exception ex) { // again, catch any exceptions and...
						// ...write to the debug console
						System.out.println(ex.getMessage());
					}
				}
			}
			else if(e.getSource() == start){
				try {
					connect();
				} catch (UnknownHostException e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(null, "Error connectando al host: La direccion IP especificada es incorrecta o no existe",
                            "Error de conexion", JOptionPane.ERROR_MESSAGE);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					JOptionPane.showMessageDialog(null, "Error connectando al host: Por favor verifique que los paràmetros de la configuracion sean los correctos y que el servidor este en escucha",
                            "Error de conexion", JOptionPane.ERROR_MESSAGE);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			else if(e.getSource() == disconnect){
				disconnect();
			}
			else if(e.getSource() == refresh){
						disconnect();
						try {
							connect();
						} catch (UnknownHostException e1) {
							e1.printStackTrace();
						} catch (IOException e1) {
							e1.printStackTrace();
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}	
			}
			else if(e.getSource() == configure){
				 int selection = 0;
				try {
					selection = JOptionPane.showConfirmDialog(
					            null, getPanel(2, new String[]{"Direccion IP","Puerto"}, marksField), "Configuracion"
					                            , JOptionPane.OK_CANCEL_OPTION
					                            , JOptionPane.PLAIN_MESSAGE);
				} catch (HeadlessException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			        
			        marksField[0].addKeyListener(new KeyAdapter(){
			        	public void keyPressed(KeyEvent e){	        
			        			if(marksField[0].getText().split(":").length != 4){
			        				marksField[0].setText("192.168.0.1");
			        			}
			        		
			        	}
			        	public void keyReleased(KeyEvent e){
			        			if(marksField[0].getText().split(":").length != 4){
			        				marksField[0].setText("192.168.0.1");
			        			}
			        	}
			        });
				 if (selection == JOptionPane.OK_OPTION) 
				 	{
			            try {
			            	//System.out.println(InetAddress.getByAddress(address) + ": " + port);
			            	getConfig();
							Log.writeFile(home_directory + "config.txt", new String[]{"Direccion IP: " + marksField[0].getText(), "Puerto: " + marksField[1].getText()}, false);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
			        }
			}
			else if(e.getSource() == parameters){
				String[] temp = {"Presion","Humedad","Temperatura", "Corriente"};
				JSpinner[] sensor_parameters = new JSpinner[temp.length];
				String[] params = null;
				 int selection = 0;
				try {
					selection = JOptionPane.showConfirmDialog(
					            null, getPanel(temp.length, temp, sensor_parameters), "Parametros"
					                            , JOptionPane.OK_CANCEL_OPTION
					                            , JOptionPane.PLAIN_MESSAGE);
				
					 if (selection == JOptionPane.OK_OPTION) {
						 String s = table_name.isEmpty() ? "Ninguno" : table_name;
						 selected_register.setLabel("Seleccionado: " + s);
						 params = new String[temp.length+2];
						 int config[] = new int[temp.length];
						 params[0] = "\r\nRegistro " + "$" + table_name;
						 params[1] = "$";
						 for(int i = 0; i < temp.length; i++){
							config[i] = (Integer)(sensor_parameters[i].getValue());
							params[1] += config[i]; 
							params[i+2] = "Sensores de " + temp[i].toLowerCase() + ": " + config[i]; 
						 }
						 params[1] += "]";
						 if(!database.getTables().contains(table_name)){
							 Log.writeFile(home_directory + "params.txt", params, true);
							 database.createTable(table_name, temp.length, config, temp);
						 }	 
						  
						      }
				} catch (HeadlessException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			else if(e.getSource() == clear){
				textArea.setText(null);
			}
		}
		
		private void getConfig() throws IOException{
			String[] regex = {".", ":", "-"};
			String[] temp = {};
			
			for(int i = 0; temp.length == 0 && i < regex.length; i++){
				temp = marksField[0].getText().split(regex[i]);
			}
			for(int i = 0; i < temp.length; i++)
			{
				 address[i] = (byte)Integer.parseInt(temp[i]);
			}
	            port = Integer.parseInt(marksField[1].getText());
		}
		
		private void connect() throws IOException, SQLException{
			client.init(InetAddress.getByAddress(address),port);
			setConnectedState();
		}
		private void disconnect(){
			if(Client.STATE_CONNECTED){
				client.getThread().interrupt();
				client.CloseResources();
			}
			setConnectedState();
		}
		
		private JPanel getPanel(final int q, String[] args, final JComponent[] context) throws IOException, SQLException
	    {
	        JPanel basePanel = new JPanel();
	        basePanel.setOpaque(true);
	        boolean flag = context.getClass().equals(JSpinner[].class) ? true : false;
	        JPanel centerPanel = new JPanel();
	      
	        JLabel[] mLabel = new JLabel[q];
	        JComboBox name = null;
	        if(flag){        	
	        	centerPanel.setLayout(new GridLayout(q+1, 2, 10, 10));
	        	final String[] temp = database.getTables().split("\n");
	        	String selected = "";
	        	name = new JComboBox(temp);
	        	name.addItem(selected);
	        	name.setEditable(true);	
	        	name.setSelectedItem(table_name);
        		JLabel nLabel = new JLabel("Nombre de Registro: ");
        		for(int i = 0; i < q; i++){
        			context[i] = new JSpinner(new SpinnerNumberModel(1, 0, 10, 1));
        		}
					setRegister(temp, context, q);		
					
        		name.addItemListener(new ItemListener(){
					@Override
					public void itemStateChanged(ItemEvent e) {
						JComboBox name = (JComboBox)e.getSource();
        				table_name = name.getSelectedItem().toString();
						try {
							setRegister(temp, context, q);
							
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}       			
        		});
        		centerPanel.add(nLabel);
        		centerPanel.add(name);
	        }else{
	        	centerPanel.setLayout(new GridLayout(q, 2, 10, 10));
	        }
	        centerPanel.setBorder(
	        BorderFactory.createEmptyBorder(10, 10, 10, 10));
	        centerPanel.setOpaque(true);

	        for(int i = 0; i < q; i++){
	        	mLabel[i] = new JLabel(args[i]);
	        	centerPanel.add(mLabel[i]);
	        	centerPanel.add(context[i]);
	        }
	        basePanel.add(centerPanel);

	        return basePanel;
	    }
		
		private void setRegister(String[] temp, JComponent[] context, int length) throws IOException{
			if(table_name.isEmpty()){
				return;
			}else{
				int[][] initialValues = new int[temp.length][length];
				Scanner scanner = new Scanner(new File(home_directory + "params.txt"));
				String line = "";
	        	String[] names = new String[temp.length];
	        	String[] values = new String[temp.length];
	        	int nIndex = 0, vIndex = 0;
				while(scanner.hasNextLine()){	
					if((line = scanner.nextLine()).contains("$")){		
						if(line.length() == 5){
							values[vIndex] = line.substring(line.indexOf("$")+1);
							vIndex++;
							continue;
						}
						names[nIndex] = line.substring(line.indexOf("$")+1);
						nIndex++;
					}
				}
        		for(int i = 0; i < temp.length; i++){
        		if(table_name.equals(names[i])){
        			for(int j = 0; j < length; j++){	
    					initialValues[i][j] = values[i].charAt(j)-48;
    					((JSpinner)context[j]).setValue(initialValues[i][j]);
    					}
        			break;
        				}	
        			}
        		}
			}
		
		private void initFiles() throws IOException{
			if(!new File(home_directory + "params.txt").exists()){
				Log.writeFile(home_directory + "params.txt", 
					new String[]{
								 "Aqui se guardaran los parametros de todos los registros creados",
								 "No se recomienda editar este archivo\r\n"
								}, false);
			}
		}
		
		public String getSelectedTable(){
			return table_name;
		}
		
		public ColorPane getTextArea(){
			return textArea;
		}
		
		public void setConnectedState(){
			String state = Client.STATE_CONNECTED? "Conectado" : "Desconectado";
			connection_state.setLabel("Estado: " + state);
		}
		
		public Database getDatabase(){
			return database;
		}
		

	}
