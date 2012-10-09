import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import javax.swing.border.LineBorder;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.*;
import java.sql.Connection;
import java.util.*;

/**
 * @author Ashwin,Avijit,Sumedh,Hemendra
 * This thread generates separate instatnt message window for each friend in the friendlist.
 * It contains all the features for one to one chatting.
 */
public class InstantMessage implements ActionListener,Runnable ,WindowFocusListener{
	boolean lostFocus;
	Vector V;
	JFrame imFrame;
	JTextPane messageField;
	JPanel conversationPanel;
	JButton sendButton;
	JButton smileysButton;
	JButton buzzButton;
	JButton filetransferButton;
	JPanel bottomPanel;
	JPanel utilPanel;
	JPanel bottomPanel1;
	String userId;
	String friendId;

	JPanel messagePanel;
	String code="";
	Connection dbConn;
	JFrame smileysFrame;
	JPanel panel1;
	JPanel panel2;

	JButton smiley1;
	JButton smiley2;
	JButton smiley3;
	JButton smiley4;
	JButton smiley5;
	JButton smiley6;
	JButton smiley7;

	JButton smiley8;
	JButton smiley9;
	JButton smiley10;
	JButton smiley11;
	JButton smiley12;
	JButton smiley13;
	JButton smiley14;
	JButton  smiley15;
	JButton smiley16;

	Thread t;

	Socket serverSoc;
	Socket fileTransferSocket;
	Socket replyFromFileAcceptor;
	boolean open=true;
	static String message;

/*
 * Contsructor of the thread.
 * It creates the frame and waits for getting or typing the message.
 */
	public InstantMessage(String userId,String friendId,Socket s,Socket fs,Vector V,Socket replysoc){//constructor
		lostFocus= false;
		try{
			Class.forName("org.postgresql.Driver");
			dbConn = DriverManager.getConnection("jdbc:postgresql://10.105.11.91/template1", "sumedh", "sumedh");
		}
		catch (Exception ex){
			System.out.println("Unable to connect to database\n"
					+ ex);
		}
		this.V=V;
		this.userId=userId;
		this.friendId=friendId;
		serverSoc=s;
		fileTransferSocket=fs;
		replyFromFileAcceptor=replysoc;
		imFrame=new JFrame();
		imFrame.setBackground(Color.gray);
		imFrame.getContentPane().setLayout(new BorderLayout());
		imFrame.setResizable(false);
		imFrame.addWindowFocusListener(this);
		try{
			imFrame.setIconImage(ImageIO.read(new File("vlogo.jpg")));
		}catch(IOException e){
			System.out.println("could not set icon");
		}
		conversationPanel=new JPanel();
		conversationPanel.setBackground(Color.white);
		conversationPanel.setLayout(new BoxLayout(conversationPanel,BoxLayout.PAGE_AXIS));
		JScrollPane scrollPane=new JScrollPane(conversationPanel);
		scrollPane.setPreferredSize(new Dimension(300,240));
		scrollPane.setWheelScrollingEnabled(true);
		
		bottomPanel=new JPanel(new GridLayout(2,0));
		bottomPanel.setPreferredSize(new Dimension(300,60));
		messageField=new JTextPane();
		smileysButton=new JButton(new ImageIcon("online.gif"));
		smileysButton.setToolTipText("Smileys");
		buzzButton=new JButton(new ImageIcon("buzz.gif"));
		filetransferButton=new JButton(new ImageIcon("file.jpg"));
		filetransferButton.setToolTipText("Send File");
		smileysButton.addActionListener(this);
		buzzButton.addActionListener(this);
		buzzButton.setToolTipText("Buzz");
		filetransferButton.addActionListener(this);

		utilPanel=new JPanel(new FlowLayout(FlowLayout.CENTER,0,0));
		utilPanel.add(smileysButton);
		utilPanel.add(buzzButton);
		utilPanel.add(filetransferButton);
		bottomPanel.add(utilPanel,0);

		bottomPanel1=new JPanel(new BorderLayout());
		bottomPanel1.add(messageField);

		Font loginFont=new Font("Serif", Font.PLAIN, 20);
		sendButton=new JButton("SEND");
		sendButton.setFont(loginFont);
		sendButton.setForeground(Color.black);
		sendButton.addActionListener(this);
		bottomPanel1.add(sendButton,BorderLayout.EAST);
		bottomPanel1.setBorder(BorderFactory.createEtchedBorder(Color.black,Color.gray));
		bottomPanel.add(bottomPanel1,1);
		imFrame.setTitle("VChat : "+friendId + "  -  Instant Message");
		imFrame.getContentPane().add(scrollPane,BorderLayout.CENTER);
		imFrame.getContentPane().add(bottomPanel,BorderLayout.PAGE_END);
		imFrame.pack();
		imFrame.setVisible(true);
		imFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		imFrame.addWindowListener(new WinListener());
		t=new Thread(this,friendId);
		t.start();
		printing();
	}

	synchronized public void run(){}

	synchronized public void printing(){
		try{
			t.wait();
		} catch (Exception ex){}                        
	}

	synchronized public void notifyit(String m) {
		message=m;
		showMessage();
	}

	//ActionPerformed Function does things accordingly
	synchronized public void actionPerformed(ActionEvent e) {
		if(e.getSource()==sendButton) {
			String tmpmessage=messageField.getText();
			if(!tmpmessage.equals("")){
				String msg=tmpmessage;
				tmpmessage=userId+":"+tmpmessage;
				message=tmpmessage;
				tmpmessage=friendId+":"+tmpmessage;
				if(V.contains(friendId)){
					try{
						OutputStream sout = serverSoc.getOutputStream();
						DataOutputStream dos = new DataOutputStream (sout);
						dos.writeUTF(tmpmessage);
						showMessage();
					}catch(Exception ex){}
				}
				else{
					try {
						Statement ins1 = dbConn.createStatement();
						Statement ins2 = dbConn.createStatement();
						ResultSet res=ins1.executeQuery("select* from "+friendId+" where username = '"+userId+"'");

						while(res.next()){
							if(res.getString(4)==null || res.getString(4).equals(""))
								ins2.executeUpdate("update "+friendId+" set offlinemsg = '" + msg+"' where username='"+userId+"'");
							else
								ins2.executeUpdate("update "+friendId+" set offlinemsg = '"+res.getString(4) + "%1@2#39$" + msg+"' where username='"+userId+"'");

							message=userId+":"+msg;
							showMessage();
						}
					} catch (Exception exc) {
						System.out.println("Fatal database error\n" +exc);
					}
				}
				
			}
		}
		else if(e.getSource()==buzzButton){
			if(V.contains(friendId)){
				try{
					OutputStream sout = serverSoc.getOutputStream();
					DataOutputStream dos = new DataOutputStream (sout);
					message=userId + ":Buzz!!";
					dos.writeUTF(friendId+":"+message);
					showMessage();
				}
				catch(Exception ex){}
			}
			else{
				try {
					Statement ins1 = dbConn.createStatement();
					Statement ins2 = dbConn.createStatement();
					ResultSet res=ins1.executeQuery("select* from "+friendId+" where username = '"+userId+"'");
					while(res.next()){
						if(res.getString(4)==null || res.getString(4).equals(""))
							ins2.executeUpdate("update "+friendId+" set offlinemsg = 'Buzz!!' where username='"+userId+"'");
						else
							ins2.executeUpdate("update "+friendId+" set offlinemsg = '"+res.getString(4) + "%1@2#39$Buzz!!'"+" where username='"+userId+"'");
						message=userId+":Buzz!!";
						showMessage();
					}
				} catch (Exception exc) {
					System.out.println("Fatal database error\n"+exc);
					try {
						dbConn.close();
					}
					catch (SQLException x){}
				}
			}
		}
		else if(e.getSource()==filetransferButton){
			JFileChooser filechooser= new JFileChooser();
			filechooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			File file;
			int returnVal = filechooser.showOpenDialog(imFrame);
			if (returnVal == JFileChooser.APPROVE_OPTION){
				try{
					file = filechooser.getSelectedFile();

					DataOutputStream sendfilename=new DataOutputStream(fileTransferSocket.getOutputStream());
					sendfilename.writeUTF(friendId+":"+file.getName());

					DataInputStream message=new DataInputStream(replyFromFileAcceptor.getInputStream());
					String reply=new String (message.readUTF());
					if(reply.equals("YES")){
						FileInputStream fis = new FileInputStream(file);
						OutputStream sOS = fileTransferSocket.getOutputStream();
						BufferedInputStream bis = new BufferedInputStream(fis);
						int bytesRead;
						while(true){
							bytesRead = bis.read();
							if(bytesRead ==-1){
								sOS.write(-1);
								break;
							}
							sOS.write(bytesRead);
						}
						JOptionPane.showMessageDialog(imFrame, file.getName()+" was successfully transfered to "+friendId);
						sOS.flush();
					}
					else if(reply.equals("NO")){
						JOptionPane.showMessageDialog(imFrame, friendId+" refused to accept your file "+file.getName());
					}
				}catch (Exception x){}
			}

		}
		else if (e.getSource()==smileysButton){
			putSmileys();
		}
		else if(e.getSource()==smiley1){    
			String tempmess = messageField.getText();
			messageField.setText(tempmess + "[:)]");   
			smileysFrame.setVisible(false);  
		}
		else if(e.getSource()==smiley2){    
			String tempmess = messageField.getText();
			messageField.setText(tempmess + "[:(]");   
			smileysFrame.setVisible(false);  
		}
		else if(e.getSource()==smiley3){    
			String tempmess = messageField.getText();
			messageField.setText(tempmess + "[;)]");   
			smileysFrame.setVisible(false);  
		}
		else if(e.getSource()==smiley4) {    
			String tempmess = messageField.getText();
			messageField.setText(tempmess + "[:D]");   
			smileysFrame.setVisible(false);  
		}
		else if(e.getSource()==smiley5){    
			String tempmess = messageField.getText();
			messageField.setText(tempmess + "[;;)]");   
			smileysFrame.setVisible(false);  
		}
		else if(e.getSource()==smiley6){    
			String tempmess = messageField.getText();
			messageField.setText(tempmess + "[>:D<]");   
			smileysFrame.setVisible(false);  
		}
		else if(e.getSource()==smiley7){    
			String tempmess = messageField.getText();
			messageField.setText(tempmess + "[:-/]");   
			smileysFrame.setVisible(false);  
		}
		else if(e.getSource()==smiley8){    
			String tempmess = messageField.getText();
			messageField.setText(tempmess + "[:-x]");   
			smileysFrame.setVisible(false);  
		}
		else if(e.getSource()==smiley9){    
			String tempmess = messageField.getText();
			messageField.setText(tempmess + "[:>]");   
			smileysFrame.setVisible(false);  
		}
		else if(e.getSource()==smiley10){    
			String tempmess = messageField.getText();
			messageField.setText(tempmess + "[:P]");   
			smileysFrame.setVisible(false);  
		}
		else if(e.getSource()==smiley11){    
			String tempmess = messageField.getText();
			messageField.setText(tempmess + "[:*]");   
			smileysFrame.setVisible(false);  
		}
		else if(e.getSource()==smiley12){    
			String tempmess = messageField.getText();
			messageField.setText(tempmess + "[=((]");   
			smileysFrame.setVisible(false);  
		}
		else if(e.getSource()==smiley13){    
			String tempmess = messageField.getText();
			messageField.setText(tempmess + "[:O]");   
			smileysFrame.setVisible(false);  
		}
		else if(e.getSource()==smiley14){    
			String tempmess = messageField.getText();
			messageField.setText(tempmess + "[X(]");   
			smileysFrame.setVisible(false);  
		}
		else if(e.getSource()==smiley15){    
			String tempmess = messageField.getText();
			messageField.setText(tempmess + "[:->]");   
			smileysFrame.setVisible(false);  
		}
		else if(e.getSource()==smiley16){    
			String tempmess = messageField.getText();
			messageField.setText(tempmess + "[B-)]");   
			smileysFrame.setVisible(false);  
		}
	}

	/*
	 * This function shows each message in the frame 
	 */
	synchronized void showMessage(){
		if(message.equals(userId+":"+messageField.getText()))
			messageField.setText(null);
		messagePanel=new JPanel();
		messagePanel.setBackground(Color.white);
		messagePanel.setLayout(new FlowLayout(FlowLayout.LEADING,0,0));
		//JLabel log=new JLabel("");
		//log.set
		//messagePanel.add(new JLabel(""));
		if(message.substring(message.indexOf(":")+1).equals("Buzz!!")){
			new Play("doorbell.wav");
		}
		for(int i =0;i<message.length();i++){
			if(message.charAt(i) == '['){
				i++;
				while(message.charAt(i) != ']'){
					code=code+message.charAt(i);
					i++;
				}
				if(code.equals(":)")){
					messagePanel.add(new JLabel(new ImageIcon("../Smileys/1.gif")));        
				}
				if(code.equals(":(")){
					messagePanel.add(new JLabel(new ImageIcon("../Smileys/2.gif")));        
				}
				if(code.equals(";)")){
					messagePanel.add(new JLabel(new ImageIcon("../Smileys/3.gif")));        
				}
				if(code.equals(":D")){
					messagePanel.add(new JLabel(new ImageIcon("../Smileys/4.gif")));        
				}
				if(code.equals(";;)")){
					messagePanel.add(new JLabel(new ImageIcon("../Smileys/5.gif")));        
				}
				if(code.equals(">:D<")){
					messagePanel.add(new JLabel(new ImageIcon("../Smileys/6.gif")));        
				}
				if(code.equals(":-/")){
					messagePanel.add(new JLabel(new ImageIcon("../Smileys/7.gif")));        
				}
				if(code.equals(":-x")){
					messagePanel.add(new JLabel(new ImageIcon("../Smileys/8.gif")));        
				}
				if(code.equals(":>")){
					messagePanel.add(new JLabel(new ImageIcon("../Smileys/9.gif")));        
				}
				if(code.equals(":P")){
					messagePanel.add(new JLabel(new ImageIcon("../Smileys/10.gif")));        
				}
				if(code.equals(":*")){
					messagePanel.add(new JLabel(new ImageIcon("../Smileys/11.gif")));        
				}
				if(code.equals("=((")){
					messagePanel.add(new JLabel(new ImageIcon("../Smileys/12.gif")));        
				}
				if(code.equals(":O")){
					messagePanel.add(new JLabel(new ImageIcon("../Smileys/13.gif")));        
				}
				if(code.equals("X(")){
					messagePanel.add(new JLabel(new ImageIcon("../Smileys/14.gif")));        
				}
				if(code.equals(":->")){
					messagePanel.add(new JLabel(new ImageIcon("../Smileys/15.gif")));        
				}
				if(code.equals("B-)")){
					messagePanel.add(new JLabel(new ImageIcon("../Smileys/16.gif")));        
				}
				code="";
			}
			else{
				JLabel newMessage = new JLabel(message.substring(i,i+1));
				newMessage.setFont(new Font("Serif", Font.PLAIN, 12));
				messagePanel.add(newMessage);
			}
		}
		conversationPanel.add(messagePanel,BorderLayout.WEST);
		imFrame.pack();
		printing();
		if(lostFocus==true){
			new Play("chimeup.wav");
		}
	}

	//This function put smileys image in the frame
	public 	void putSmileys() {
		smileysFrame=new JFrame();
		smileysFrame.setBackground(Color.white);
		smileysFrame.setSize(new Dimension(300,100));
		smileysFrame.getContentPane().setLayout(new GridLayout(2,0));
		smileysFrame.setTitle("Smileys");
		smileysFrame.setResizable(false);
		smileysFrame.setLocation(new Point((int)imFrame.getLocation().getX(),(int)imFrame.getLocation().getY()+200));
		try{
			smileysFrame.setIconImage(ImageIO.read(new File("vlogo.jpg")));
		}catch(IOException e){
			System.out.println("could not set icon");
		}
		addButtons();

		panel1=new JPanel();
		panel1.add(smiley1);
		panel1.add(smiley2);
		panel1.add(smiley3);
		panel1.add(smiley4);
		panel1.add(smiley5);
		panel1.add(smiley6);
		panel1.add(smiley7);
		panel1.add(smiley8);
		smileysFrame.getContentPane().add(panel1,0);

		panel2=new JPanel();
		panel2.add(smiley9);
		panel2.add(smiley10);
		panel2.add(smiley11);
		panel2.add(smiley12);
		panel2.add(smiley13);
		panel2.add(smiley14);
		panel2.add(smiley15);
		panel2.add(smiley16);                
		smileysFrame.getContentPane().add(panel2,1);

		smileysFrame.pack();
		smileysFrame.setVisible(true);
	}  

	public void addButtons(){
		smiley1=new JButton(new ImageIcon("../Smileys/1.jpg"));
		smiley1.setBackground(Color.white);
		smiley1.setBorder(new LineBorder(Color.WHITE, 0));
		smiley1.setToolTipText("smile");
		smiley1.addActionListener(this);

		smiley2=new JButton(new ImageIcon("../Smileys/2.jpg"));
		smiley2.setBackground(Color.white);
		smiley2.setBorder(new LineBorder(Color.WHITE, 0));
		smiley2.setToolTipText("sad");
		smiley2.addActionListener(this);

		smiley3=new JButton(new ImageIcon("../Smileys/3.jpg"));
		smiley3.setBackground(Color.white);
		smiley3.setBorder(new LineBorder(Color.WHITE, 0));
		smiley3.setToolTipText("winking");
		smiley3.addActionListener(this);

		smiley4=new JButton(new ImageIcon("../Smileys/4.jpg"));
		smiley4.setBackground(Color.white);
		smiley4.setBorder(new LineBorder(Color.WHITE, 0));
		smiley4.setToolTipText("Big grin");
		smiley4.addActionListener(this);

		smiley5=new JButton(new ImageIcon("../Smileys/5.jpg"));
		smiley5.setBackground(Color.white);
		smiley5.setBorder(new LineBorder(Color.WHITE, 0));
		smiley5.setToolTipText("batting eyelashes");
		smiley5.addActionListener(this);

		smiley6=new JButton(new ImageIcon("../Smileys/6.jpg"));
		smiley6.setBackground(Color.white);
		smiley6.setBorder(new LineBorder(Color.WHITE, 0));
		smiley6.setToolTipText("hug");
		smiley6.addActionListener(this);

		smiley7=new JButton(new ImageIcon("../Smileys/7.jpg"));
		smiley7.setBackground(Color.white);
		smiley7.setBorder(new LineBorder(Color.WHITE, 0));
		smiley7.setToolTipText("confused");
		smiley7.addActionListener(this);

		smiley8=new JButton(new ImageIcon("../Smileys/8.jpg"));
		smiley8.setBackground(Color.white);
		smiley8.setBorder(new LineBorder(Color.WHITE, 0));
		smiley8.setToolTipText("love struck");
		smiley8.addActionListener(this);

		smiley9=new JButton(new ImageIcon("../Smileys/9.jpg"));
		smiley9.setBackground(Color.white);
		smiley9.setBorder(new LineBorder(Color.WHITE, 0));
		smiley9.setToolTipText("blushing");
		smiley9.addActionListener(this);


		smiley10=new JButton(new ImageIcon("../Smileys/10.jpg"));
		smiley10.setBackground(Color.white);
		smiley10.setBorder(new LineBorder(Color.WHITE, 0));
		smiley10.setToolTipText("tongue");
		smiley10.addActionListener(this);

		smiley11=new JButton(new ImageIcon("../Smileys/11.jpg"));
		smiley11.setBackground(Color.white);
		smiley11.setBorder(new LineBorder(Color.WHITE, 0));
		smiley11.setToolTipText("kiss");
		smiley11.addActionListener(this);

		smiley12=new JButton(new ImageIcon("../Smileys/12.jpg"));
		smiley12.setBackground(Color.white);
		smiley12.setBorder(new LineBorder(Color.WHITE, 0));
		smiley12.setToolTipText("broken heart");
		smiley12.addActionListener(this);

		smiley13=new JButton(new ImageIcon("../Smileys/13.jpg"));
		smiley13.setBackground(Color.white);
		smiley13.setBorder(new LineBorder(Color.WHITE, 0));
		smiley13.setToolTipText("surprised");
		smiley13.addActionListener(this);

		smiley14=new JButton(new ImageIcon("../Smileys/14.jpg"));
		smiley14.setBackground(Color.white);
		smiley14.setBorder(new LineBorder(Color.WHITE, 0));
		smiley14.setToolTipText("angry");
		smiley14.addActionListener(this);

		smiley15=new JButton(new ImageIcon("../Smileys/15.jpg"));
		smiley15.setBackground(Color.white);
		smiley15.setBorder(new LineBorder(Color.WHITE, 0));
		smiley15.setToolTipText("smug");
		smiley15.addActionListener(this);

		smiley16=new JButton(new ImageIcon("../Smileys/16.jpg"));
		smiley16.setBackground(Color.white);
		smiley16.setBorder(new LineBorder(Color.WHITE, 0));
		smiley16.setToolTipText("cool");
		smiley16.addActionListener(this);

	}

	public void windowGainedFocus(WindowEvent e) {
		lostFocus=false;
	}

	public void windowLostFocus(WindowEvent e) {
		lostFocus=true;
	}
	class WinListener extends WindowAdapter{
		public void windowClosing(WindowEvent event){
			for (int i=0;i<VChat.ims.size();++i){
				if(((InstantMessage)VChat.ims.elementAt(i)).t.getName().equals(t.getName())){
					VChat.ims.removeElementAt(i);
					break;
				}
			}
			imFrame.dispose();
		}
	}
}