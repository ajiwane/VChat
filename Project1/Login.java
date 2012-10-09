import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.sql.*;

/**
 * @author Ashwin,Avijit,Sumedh,Hemendra
 * It is the main class of the project.
 * It shows the login window and checks for identification.If the user is registered then it allows the user to login otherwise it does not.
 * After logging in, it creates connection to the main server. and creates new  vchat class 
 * It also allows to create new user account 
 */
public class Login implements ActionListener{ 
	JFrame loginFrame;
	JPanel newidPanel;
	JPanel loginPanel;
	JPanel newidBPanel;
	JButton newidButton;
	JButton signinButton;
	JButton cancelButton;
	JTextField useridField;
	JPasswordField passwordField;
	JCheckBox invisible;
	JFrame createFrame;
	JButton okButton;
	JButton cancelnewuserButton;
	JTextField firstName;
	JTextField lastName;
	JTextField newuseridField;
	JPasswordField newpasswdField;
	Connection dbConn = null;
	Statement ins = null;
	ResultSet results = null;
	Socket serverSocket ;
	Dimension scrnsize;
	
	private static final String JDBC_DRIVER = "org.postgresql.Driver";
	
	private static final String URL = "jdbc:postgresql://10.105.11.91/template1";
	
	private static final String USERNAME = "sumedh";
	
	private static final String PASSWORD = "sumedh";

	/*constuctor
	 * it creates the frame
	 */	
	public Login(){
		try{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			JFrame.setDefaultLookAndFeelDecorated(true);
			JDialog.setDefaultLookAndFeelDecorated(true);
		}catch(Exception e){
			System.out.println("Could not get look and feel");
		}

		loginFrame=new JFrame();
		loginFrame.setBackground(Color.white);
		loginFrame.setSize(new Dimension(280,350));
		loginFrame.getContentPane().setLayout(new BorderLayout());
		loginFrame.setTitle("Sign In");
		loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		loginFrame.setResizable(false);
		Font loginFont=new Font("Serif", Font.PLAIN, 20);
		try{
			loginFrame.setIconImage(ImageIO.read(new File("vlogo.jpg")));
		}
		catch(IOException e){
			System.out.println("could not set icon");
		}

		newidButton=new JButton("Get a new ID");
		newidButton.setFont(loginFont);
		newidButton.setForeground(Color.black);
		newidButton.addActionListener(this);

		signinButton=new JButton("Sign In");
		signinButton.setFont(loginFont);
		signinButton.setForeground(Color.black);
		signinButton.addActionListener(this);

		cancelButton=new JButton("Cancel");
		cancelButton.setFont(loginFont);
		cancelButton.setForeground(Color.black);
		cancelButton.addActionListener(this);		
		newidPanel = new JPanel();
		JLabel newidLabel=new JLabel("Need a new id? ");
		newidLabel.setFont(loginFont);
		newidPanel.add(newidLabel);

		newidBPanel=new JPanel();				
		newidBPanel.add(newidButton);

		loginPanel =new JPanel(new GridLayout(0,1));

		JLabel loginmssgLabel = new JLabel("Already got an id??");
		loginPanel.add(loginmssgLabel,0);

		JPanel row1 = new JPanel(new BorderLayout());
		JLabel useridLabel = new JLabel("V Chat ID: ");
		useridLabel.setFont(loginFont);
		row1.add(useridLabel, BorderLayout.WEST);
		useridField = new JTextField("",10);
		useridField.setFont(loginFont);
		row1.add(useridField, BorderLayout.CENTER);
		loginPanel.add(row1,1);

		JPanel row2 = new JPanel(new BorderLayout());
		JLabel passwordLabel = new JLabel("Password: ");
		passwordLabel.setFont(loginFont);
		row2.add(passwordLabel, BorderLayout.WEST);
		passwordField = new JPasswordField("",10);
		passwordField.setFont(loginFont);
		row2.add(passwordField, BorderLayout.CENTER);
		loginPanel.add(row2,2);

		invisible=new JCheckBox("Sign in as Invisible");
		loginPanel.add(invisible,3);
		JPanel row3= new JPanel();
		row3.add(signinButton);
		row3.add(cancelButton);
		loginPanel.add(row3,4);

		loginFrame.getContentPane().add(newidPanel,BorderLayout.PAGE_START);
		loginFrame.getContentPane().add(newidBPanel,BorderLayout.CENTER);
		loginFrame.getContentPane().add(loginPanel,BorderLayout.PAGE_END);
		Toolkit toolkit = Toolkit.getDefaultToolkit();

		scrnsize = toolkit.getScreenSize();
		loginFrame.pack();
		loginFrame.setLocation(new Point( (int)scrnsize.getWidth()-280,(int)scrnsize.getHeight()-(int)scrnsize.getHeight()/2-150));
		loginFrame.setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==newidButton){
			CreateUser();
			loginFrame.setEnabled(false);
		}
		else if(e.getSource()==okButton)	{
			String firstname=firstName.getText();
			String lastname=lastName.getText();
			String username=newuseridField.getText();
			String password=newpasswdField.getText();	
			firstName.setText(null);
			lastName.setText(null);
			newuseridField.setText(null);
			newpasswdField.setText(null);
			createFrame.dispose();
		    if(firstname == null || lastname == null|| username == null|| password == null||firstname.equals("")|| lastname.equals("")|| username.equals("")|| password .equals("")){
			CreateUser();
			JOptionPane.showMessageDialog(createFrame,"Complete all details in the form");
			return;
		    }
		    loginFrame.setEnabled(true);
			
			try{
				Class.forName(JDBC_DRIVER);
				dbConn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
			
			}
			catch (Exception ex){
				System.out.println("Unable to connect to database\n"
						+ ex+"ddddddddddddddddddddd");
			}
			try {
				ins = dbConn.createStatement();
				results=ins.executeQuery("select* from vchatlogin where username ='"+username+"'");
				if(results.next()){
					JOptionPane.showMessageDialog(loginFrame,"Username Already Exists. Give a different Username");
				}
				else{
					ins.executeUpdate("insert into vchatlogin values('"+firstname+"','"+lastname+"','"+username+"','"+password+"')");
					ins.executeUpdate("Create table "+username+" (username varchar(15),firstname varchar(15),lastname varchar(15),offlinemsg varchar(1000))");
					JOptionPane.showMessageDialog(loginFrame,"New Accout Created.Welcome to the lightening fast World of VCHAT");
				}

			} catch (SQLException exc) {
				System.out.println("Fatal database error\n" + exc);
			}
		}
		else if(e.getSource()==cancelnewuserButton){
			loginFrame.setEnabled(true);
			firstName.setText(null);
			lastName.setText(null);
			newuseridField.setText(null);
			newpasswdField.setText(null);
			createFrame.dispose();
		}
		else if(e.getSource()==signinButton){
			String initstatus;
			if(invisible.isSelected())
				initstatus="Invisible";
			else
				initstatus="Available";
			String userid=useridField.getText();
			String passwd=(String)passwordField.getText();
			try{
				Class.forName("org.postgresql.Driver");
				dbConn = DriverManager.getConnection("jdbc:postgresql://10.105.11.91/template1", "sumedh", "sumedh");
			}
			catch (Exception ex) {
				System.out.println("Unable to connect to database\n"
						+ ex);
			}
			try {
				ins = dbConn.createStatement();
				results = ins.executeQuery("select* from vchatlogin where username = '"+useridField.getText()+"'");
				if(results.next()){	
					if(passwd.equals(results.getString(4))){
						try {
							serverSocket = new Socket("10.105.11.03",5678);
							Socket filetransfersocket=new Socket("10.105.11.03",8888);
							Socket replyfromfileacceptor=new Socket("10.105.11.03",8889);
							Statement ins1 = dbConn.createStatement();
							if(userid != null)	{
								String ip=serverSocket.getInetAddress().getLocalHost().toString();
								ip=ip.substring(ip.indexOf("/")+1);
								ins1.executeUpdate("insert into currentip values('"+userid+"','"+ip+"','"+initstatus+"')");	
							}
							OutputStream s1out = serverSocket.getOutputStream();
							DataOutputStream dos1 = new DataOutputStream (s1out);
							dos1.writeUTF(userid);
							OutputStream s2out = filetransfersocket.getOutputStream();
							DataOutputStream dos2 = new DataOutputStream (s2out);
							dos2.writeUTF(userid);
							Statement ins2 = dbConn.createStatement();
							ResultSet res2=ins2.executeQuery("select* from "+userid);
							String offlinemessages="";
							int i=0;

							while(res2.next()){
								if(res2.getString(4)!=null && !res2.getString(4).equals("")){
										while(res2.getString(4).indexOf("%1@2#39$",i)!=-1){
										offlinemessages+=res2.getString(1)+":"+res2.getString(4).substring(i,res2.getString(4).indexOf("%1@2#39$",i))+"\n";
										i=res2.getString(4).indexOf("%1@2#39$",i)+8;
									}
									offlinemessages+=res2.getString(1)+":"+res2.getString(4).substring(i);
								}
							}

							if(!offlinemessages.equals("")){
								JOptionPane.showMessageDialog(loginFrame,offlinemessages);
								ins2.executeUpdate("update "+userid+" set offlinemsg=null");
							}

							loginFrame.setVisible(false);		
							VChat vchat=new VChat(userid,results.getString(1),results.getString(2),serverSocket,filetransfersocket,initstatus,replyfromfileacceptor);
							new TakesInputfromServer(serverSocket,userid,vchat.friends,filetransfersocket,replyfromfileacceptor);
							new TakesFileFromServer(vchat.vchatFrame,filetransfersocket,replyfromfileacceptor);
							new Play("pow.wav");
						}catch (Exception x) {}
					}
					else
						JOptionPane.showMessageDialog(loginFrame,"Incorrect Password. Try Again");
				}
				else{
					JOptionPane.showMessageDialog(loginFrame,"No Such Login Exists. Try Again");
				}
			} catch (SQLException exc) {
				System.out.println("Fatal database error\n");
			}

		}
		else if(e.getSource()==cancelButton)	{
			loginFrame.dispose();
		}
	}

	/*
	 * it creates the new user frame
	 */
	public void CreateUser(){
		createFrame=new JFrame();
		createFrame.getContentPane().setLayout(new GridLayout(9,0));
		createFrame.setSize(new Dimension(280,300));
		createFrame.setTitle("Create a new Account");
		createFrame.setResizable(false);
		Font loginFont=new Font("Serif", Font.PLAIN, 20);
		firstName = new JTextField();
		lastName = new JTextField();
		newuseridField = new JTextField();
		newpasswdField= new JPasswordField();

		createFrame.getContentPane().add(new JLabel("First Name :"),0);
		createFrame.getContentPane().add(firstName,1);
		createFrame.getContentPane().add(new JLabel("Last Name :"),2);
		createFrame.getContentPane().add(lastName,3);
		createFrame.getContentPane().add(new JLabel("UserID :"),4);
		createFrame.getContentPane().add(newuseridField,5);
		createFrame.getContentPane().add(new JLabel("Password :"),6);
		createFrame.getContentPane().add(newpasswdField,7);

		JPanel buttons=new JPanel();
		okButton=new JButton("OK");
		okButton.setFont(loginFont);
		okButton.setForeground(Color.black);
		okButton.addActionListener(this);
		okButton.setPreferredSize(new Dimension(90,25));

		cancelnewuserButton=new JButton("Cancel");
		cancelnewuserButton.setFont(loginFont);
		cancelnewuserButton.setForeground(Color.black);
		cancelnewuserButton.addActionListener(this);
		cancelnewuserButton.setPreferredSize(new Dimension(90,25));

		buttons.add(okButton);
		buttons.add(cancelnewuserButton);
		createFrame.getContentPane().add(buttons,8);
		createFrame.pack();
		createFrame.setVisible(true);
	}
	
	//Main function
	public static void main(String args[]){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				new Login();
			}
		});
	}
}

