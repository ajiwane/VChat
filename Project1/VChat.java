import java.awt.*;
import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.event.*;
import java.net.*;
import java.io.*;
import java.util.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
/**
 * @author Ashwin,Avijit,Sumedh,Hemendra
 * It is the main class of the project.
 * It shows the friendlist of the user.Vchat get repainted by the update thread which
 *  continuosly checks for updates in the currentip table  
 *  
 */
public class VChat implements ActionListener,MouseListener{
	
	JFrame vchatFrame;
	JTextField friendsField;
	JMenuBar menuBar;
	//JMenuItem menuItem;
	JMenuItem SignOut;
	JMenuItem Addstatus;
	JMenuItem SO;
	JMenuItem BRB;
	JMenuItem NAMD;
	JMenuItem Busy;
	JMenuItem Invisible;
	JMenuItem Available;
	JMenuItem Addfriend;
	JMenuItem Removefriend;
	JMenuItem Help;
	JMenuItem About;
	JMenuItem ShowOffline;
	JMenuItem DeleteAccount;
	JLabel statusLabel;
	JPanel statusPanel; 
	Vector friends=new Vector();
	JScrollPane scrollPane;
	JButton friendButton;
	JPanel friendsPanel;
	InstantMessage im;
	String userId;
	int initialFriends;
	boolean showOffline=false;

	String firstName,lastName;
	Connection dbConn = null;
	Statement ins = null;
	ResultSet results = null;
	Socket serverSocket;
	Socket fileTransferSocket;
	Socket replyFromFileAcceptor;
	static Vector ims=new Vector();
	Dimension scrnsize;

	public VChat(String u,String n,String l,Socket s,Socket files,String initstatus,Socket replysoc){
		try{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

			initialFriends=0;
			userId=u;
			firstName=n;
			lastName =l;
			serverSocket=s;
			fileTransferSocket=files;
			replyFromFileAcceptor=replysoc;
			vchatFrame=new JFrame(userId);
			vchatFrame.setResizable(false);
			vchatFrame.getContentPane().setLayout(new BorderLayout());
			vchatFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			vchatFrame.setTitle("Velox Chat");
			vchatFrame.setIconImage(ImageIO.read(new File("vlogo.jpg")));

			menuBar = new JMenuBar();
			JMenu menu1 = new JMenu("VChat");
			menuBar.add(menu1);
			JMenu submenu1 = new JMenu("My Status");
			Available=new JMenuItem("Available");
			submenu1.add(Available);
			Busy=new JMenuItem("Busy");
			submenu1.add(Busy);
			NAMD=new JMenuItem("Not At My Desk");
			submenu1.add(NAMD);
			BRB=new JMenuItem("Be Right Back");
			submenu1.add(BRB);
			SO=new JMenuItem("Stepped Out");
			submenu1.add(SO);
			Invisible=new JMenuItem("Invisible");
			submenu1.add(Invisible);
			menu1.add(submenu1);

			Addstatus=new JMenuItem("Add a new status");
			menu1.add(Addstatus);
			SignOut=new JMenuItem("Sign Out");
			menu1.add(SignOut);

			Addstatus.addActionListener(this);
			SignOut.addActionListener(this);
			SO.addActionListener(this);
			BRB.addActionListener(this);
			NAMD.addActionListener(this);
			Busy.addActionListener(this);
			Available.addActionListener(this);
			Invisible.addActionListener(this);

			JMenu menu2 = new JMenu("Action");
			menuBar.add(menu2);
			Addfriend=new JMenuItem("Add friend to list");
			menu2.add(Addfriend);
			Removefriend=new JMenuItem("Remove a friend");
			menu2.add(Removefriend);
			ShowOffline=new JMenuItem("Show Offline Friends");
			menu2.add(ShowOffline);
			DeleteAccount=new JMenuItem("Delete Account");
			menu2.add(DeleteAccount);
			DeleteAccount.addActionListener(this);
			Addfriend.addActionListener(this);
			Removefriend.addActionListener(this);
			ShowOffline.addActionListener(this);

			JMenu menu3 = new JMenu("Help");
			menuBar.add(menu3);
			Help=new JMenuItem("Help");
			menu3.add(Help);
			About=new JMenuItem("About Velox Chat");
			menu3.add(About);
			Help.addActionListener(this);
			About.addActionListener(this);

			vchatFrame.setJMenuBar(menuBar);

			statusPanel=new JPanel(new BorderLayout(3,3));
			statusLabel = new JLabel(userId+": "+initstatus);
			statusLabel.setFont(new Font("Serif", Font.BOLD, 17));
			statusLabel.setForeground(Color.BLUE);
			statusPanel.add(statusLabel,BorderLayout.WEST);
			vchatFrame.getContentPane().add(statusPanel,BorderLayout.PAGE_START);

			friendsPanel=new JPanel();
			friendsPanel.setBackground(Color.white);
			friendsPanel.setLayout(new BoxLayout(friendsPanel, BoxLayout.PAGE_AXIS));

			Font loginFont=new Font("Serif", Font.PLAIN, 20);
			scrollPane=new JScrollPane(friendsPanel);
			scrollPane.setPreferredSize(new Dimension(300,600));
			scrollPane.setWheelScrollingEnabled(true);
			vchatFrame.getContentPane().add(scrollPane,BorderLayout.CENTER);
			Toolkit toolkit = Toolkit.getDefaultToolkit();

			scrnsize = toolkit.getScreenSize();
			vchatFrame.pack();
			vchatFrame.setLocation(new Point( (int)scrnsize.getWidth()-300,0));
			vchatFrame.setVisible(true);
			vchatFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			vchatFrame.addWindowListener(new WinListener());
		}catch(Exception e){
			System.out.println("Could not get look and feel");
		}
		try{
			Class.forName("org.postgresql.Driver");
			dbConn = DriverManager.getConnection("jdbc:postgresql://10.105.11.91/template1", "sumedh", "sumedh");
		
		}catch (Exception ex){
			System.out.println("Unable to connect to database\n"
					+ ex.getMessage());
		}
		try{
			Statement ins1 = dbConn.createStatement();
			ResultSet res=ins1.executeQuery("select* from "+userId);
			while(res.next()){
				Statement ins = dbConn.createStatement();
				ResultSet results2=ins.executeQuery("select* from currentip where username='"+res.getString(1)+"'");
				while(results2.next()){
					initialFriends++;
				}
			}
		} catch (Exception exc){
			System.out.println("Fatal database error\n");
		}
		try{
			new Update(this);
		}catch (Exception ex){}
	}

	public void showStatus(String status){
		statusLabel.setText(userId+": "+status);
		try {
			ins = dbConn.createStatement();
			ins.executeUpdate("update currentip set status='"+status+"' where username="+"'"+userId+"'");	
		} catch (SQLException exc) {
			System.out.println("Fatal database error\n");
		}
	}
	public void actionPerformed(ActionEvent e) {

		if(e.getSource()==Invisible){
			showStatus("Invisible");
		}
		else if(e.getSource()==Available)	{
			showStatus("Available");
		}
		else if(e.getSource()==Busy){
			showStatus("Busy");
		}
		else if(e.getSource()==NAMD)	{
			showStatus("Not At My Desk");			
		}
		else if(e.getSource()==BRB)	{
			showStatus("Be Right Back");
		}
		else if(e.getSource()==SO){
			showStatus("Stepped Out");
		}
		else if(e.getSource()==Addstatus){
			String mess=JOptionPane.showInputDialog(vchatFrame,"Add ur status message");
			if(mess!=null)
				showStatus(mess);
		}
		else if(e.getSource()==SignOut){
			//if a user signs out remove him from the currrentip table and close application
			try {
				ins = dbConn.createStatement();
				ins.executeUpdate("delete from currentip where username="+"'"+userId+"'");
				try{
					DataOutputStream signingOut=new DataOutputStream (serverSocket.getOutputStream());
					signingOut.writeUTF(userId+":[[[###SIGNOUT");
				}catch (Exception exc){}
				
			} catch (SQLException exc) {
				System.out.println("Fatal database error\n");
				try {
					dbConn.close();
				} catch (SQLException x) {
				}
			}
			System.exit(0);
		}
		else if(e.getSource()==Addfriend){
			//while adding a new friend the friends id is stored in the users table as %friendid
			//while the users id is stored in the friends list as #userid  untill the request is pending
			String newfriend=JOptionPane.showInputDialog(vchatFrame,"Add vchat id of ur friend");
			try {
				ins = dbConn.createStatement();
				Statement ins1=dbConn.createStatement();
				results=ins.executeQuery("select* from vchatlogin where username ='"+newfriend+"'");
				ResultSet results2=ins1.executeQuery("select* from "+userId+" where username ='"+newfriend+"'");
				if(results2.next()){
					JOptionPane.showMessageDialog(vchatFrame,newfriend+" already exits in your friend list");
				}
				else{
					if(results.next()){
						ins.executeUpdate("insert into "+ userId+ " values('"+"%"+newfriend+"','"+results.getString(1)+"','"+results.getString(2)+"',"+null+")");
						results2=ins1.executeQuery("select* from "+newfriend+" where username ='"+userId+"'");
						if(results2.next()){
							ins.executeUpdate("update "+ newfriend+ " set username='#"+userId+"' where username='"+userId+"'");
						}
						else
							ins.executeUpdate("insert into "+ newfriend+ " values('"+"#"+userId+"','"+firstName+"','"+lastName+"',"+null+")");
					}
					else{
						JOptionPane.showMessageDialog(vchatFrame,"No such Username exists");

					}
				}
			} catch (SQLException exc) {
				System.out.println("Fatal database error\n");
				try {
					dbConn.close();
				} catch (SQLException x) {
				} 
			}

		}
		else if(e.getSource()==Removefriend){
			//remove a friend removes the friend from the users list but the user still remains 
			//in the friends list
			String removefriend=JOptionPane.showInputDialog(vchatFrame,"Add vchat id of ur friend to remove him/her from ur list");
			try {
				ins = dbConn.createStatement();
				results=ins.executeQuery("select* from "+ userId+" where username ='"+removefriend+"'");
				if(results.next()){
					ins.executeUpdate("delete from "+ userId + " where username ='"+ removefriend+"'");	
					JOptionPane.showMessageDialog(vchatFrame,removefriend+" has been deleted from your friend list");
				}
				else{
					JOptionPane.showMessageDialog(vchatFrame,"No such Username exists in Your Friendlist");
				}
			} catch (SQLException exc) {
				System.out.println("Fatal database error\n");
				try {
					dbConn.close();
				} catch (SQLException x) {
				} 
			}
		}

		else if(e.getSource()==Help){
			
			JFrame helpWindow = new JFrame("Help");
			helpWindow.setSize(new Dimension(430,700));
			helpWindow.setResizable(false);
			JTextPane helpPanel = new JTextPane();
            	helpPanel.setBackground(Color.white);
            	helpPanel.setLayout(new BoxLayout(helpPanel,BoxLayout.PAGE_AXIS));
            	helpPanel.disable();
            helpPanel.add(new JLabel("1.New User"));
            helpPanel.add(new JLabel(" A new user can register into VeLoX ChAt using the relevant button"));
            helpPanel.add(new JLabel(" on the window and then providing the required information to"));
            helpPanel.add(new JLabel(" create a new account."));            
            
            helpPanel.add(new JLabel("2.Log In"));
            helpPanel.add(new JLabel(" A user once registered can then use his username and password to"));
            helpPanel.add(new JLabel(" login to VeLoX ChAt either as available to his friends or"));
            helpPanel.add(new JLabel(" invisible.An invisible user can send and receive messages to "));
            helpPanel.add(new JLabel(" and from his friends although he is seen as offline by his friends."));
            
            helpPanel.add(new JLabel("3.VChAt Window"));
            helpPanel.add(new JLabel(" Once logged in, VChAt window opens up with a sound displaying his"));
            helpPanel.add(new JLabel(" currently online friends with their status messages.He can choose"));
            helpPanel.add(new JLabel(" to see his offline friends by selecting the corresponding"));
            helpPanel.add(new JLabel(" option in the Actions menu."));            
            
            helpPanel.add(new JLabel("4.Menu Bar"));
            helpPanel.add(new JLabel(" The Menu contains various options like Add A Friend,Remove Friend,"));            
            helpPanel.add(new JLabel(" Set Status Message,Set Custom Status Message,"));
            helpPanel.add(new JLabel("Show/Hide Offline Friends and Sign Out."));            
            helpPanel.add(new JLabel(" The current status and the user's username is displayed on the window."));            
            
            helpPanel.add(new JLabel("5.Start Messaging"));
            helpPanel.add(new JLabel(" To start a conversation with a friend,he can double-click on that"));            
            helpPanel.add(new JLabel(" friend's name.An instant messaging window opens up which is used"));
            helpPanel.add(new JLabel(" for the conversation.The user has to input the text in the "));
            helpPanel.add(new JLabel(" text bar and click on the send button to send"));
            helpPanel.add(new JLabel(" his message to his friend.He can insert smileys in his"));
            helpPanel.add(new JLabel(" message by choosing from a list of smileys displayed on"));
            helpPanel.add(new JLabel(" clicking on the smiley button on the window.He can"));
            helpPanel.add(new JLabel(" also buzz his friend.A log of their conversation is"));
            helpPanel.add(new JLabel(" displayed in the messaging window."));
            
            
            helpPanel.add(new JLabel("6.Receive Messages"));
            helpPanel.add(new JLabel("If a user receives a message from his friend a messaging window"));            
            helpPanel.add(new JLabel(" pops up with an alert sound.If a message is received in an"));
            helpPanel.add(new JLabel(" already open window which is not currently focussed,"));
            helpPanel.add(new JLabel(" an alert sound is played."));            
            
            helpPanel.add(new JLabel("7.Offline Messages"));
            helpPanel.add(new JLabel(" Any message sent to a user who is currently offline is seen by"));            
            helpPanel.add(new JLabel(" that user when he logs in to VeLoX ChAt."));           
            
            helpPanel.add(new JLabel("8.Alerts"));
            helpPanel.add(new JLabel(" Whenever any friend comes online or goes offline, a corresponding"));            
            helpPanel.add(new JLabel(" alert with sound is displayed at the bottom right of the screen."));            
            
            helpWindow.getContentPane().add(helpPanel,BorderLayout.CENTER);
            helpWindow.pack();
            helpWindow.setVisible(true);
		}
		else if(e.getSource()==About){
			JFrame aboutWindow = new JFrame("About Velox Chat");
			aboutWindow.setSize(new Dimension(190,230));
			aboutWindow.setResizable(false);
			JTextPane aboutPanel = new JTextPane();
            aboutPanel.setBackground(Color.white);
            aboutPanel.setLayout(new BoxLayout(aboutPanel,BoxLayout.PAGE_AXIS));
            aboutPanel.disable();
            
            aboutPanel.add(new JLabel("            Velox Chat             "));
            aboutPanel.add(new JLabel("            Version 1.0             "));
            aboutPanel.add(new JLabel("          30 April 2007          "));
            aboutPanel.add(new JLabel("                                   "));
            aboutPanel.add(new JLabel("          Developed By:            "));
            aboutPanel.add(new JLabel("          Ashwin Jiwane            "));
            aboutPanel.add(new JLabel("         Avijit Satoskar           "));
            aboutPanel.add(new JLabel("        Sumedh Ambokar            "));
            aboutPanel.add(new JLabel("      Hemendra Srivastava         "));
            aboutPanel.add(new JLabel("                                   "));
            aboutPanel.add(new JLabel("      Under The Guidance Of:       "));
            aboutPanel.add(new JLabel("        Prof. S.Sudarshan          "));
            
            
            
            
            aboutWindow.getContentPane().add(aboutPanel,BorderLayout.CENTER);
            aboutWindow.pack();
            aboutWindow.setVisible(true);            
		}		


		else if(e.getSource()==ShowOffline){
			showOffline=!showOffline;
			if(showOffline)
				ShowOffline.setLabel("Hide Offline friends");
			else
				ShowOffline.setLabel("Show Offline friends");
		}
		
		else if(e.getSource()==DeleteAccount){
			//delete account removes all data related to the user.
			int answer=JOptionPane.showConfirmDialog(vchatFrame," Are you sure you want to delete your account?",null,JOptionPane.YES_NO_OPTION);
			if(answer == JOptionPane.YES_OPTION){
				try {
					ins = dbConn.createStatement();
					results=ins.executeQuery("select username  from vchatlogin ");
					while(results.next()){
						Statement ins1=dbConn.createStatement();
						ins1.executeUpdate("delete from "+ results.getString(1)+ " where username ='"+ userId+"'");	
						
					}
					
					
					ins.executeUpdate("delete from currentip where username="+"'"+userId+"'");				
					ins.executeUpdate("delete from vchatlogin where username="+"'"+userId+"'");				
					ins.executeUpdate("drop table "+userId);
					
				} catch (SQLException exc) {
					System.out.println("Fatal database error\n"+exc);
					try {
						dbConn.close();
					} catch (SQLException x) {
					} 
				}
				System.exit(0);
			}
			}
			
	}
	public void mouseClicked(MouseEvent e) {
		ResultSet res;
		if( e.getClickCount()==2){ 
			System.out.println(e.getPoint().x);
			System.out.println(e.getPoint().y);
			System.out.println(((JButton)e.getSource()).getName());
			int check=-1;
			for (int i=0;i<ims.size();++i)	{
				if(((InstantMessage)ims.elementAt(i)).friendId.equals(((JButton)e.getSource()).getName()))
					check=i;
			}
			if(check==-1){
				im=new InstantMessage(userId,((JButton)e.getSource()).getName(),serverSocket,fileTransferSocket,friends,replyFromFileAcceptor);
				ims.add(im);}
			else{
				((InstantMessage)ims.elementAt(check)).imFrame.nextFocus();
			}
		}
	}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}

	class WinListener extends WindowAdapter{
		public void windowClosing(WindowEvent event){
			JOptionPane.showMessageDialog(vchatFrame,"Sign out first.");
		}
	}
}