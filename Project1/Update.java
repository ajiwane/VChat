import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import javax.sound.sampled.SourceDataLine;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.border.LineBorder;
import javax.sound.sampled.*;
import javax.swing.*;
/**
 * @author Ashwin,Avijit,Sumedh,Hemendra
 * Update thread is used to continuosly  update the users friend list and to check if someone has come online or gone offline
 * Whenever there is a change in the list Alert is called to indicate the change to the user.
 *
 */
public class Update implements Runnable{
	Thread t;
	VChat V;
	int friendSol;
	Font loginFont=new Font("Serif", Font.PLAIN, 20);
	Connection dbConn = null;
	Statement ins = null;
	ResultSet results = null;
	Vector temp=new Vector();
	Vector latestInvisibles=new Vector();
	Vector prevInvisibles=new Vector();
	public Update (VChat v){
		V=v;
		friendSol=V.initialFriends;
		for(int i=0;i<V.friends.size();++i){
			temp.addElement(V.friends.elementAt(i));
		}
		try{//connect to the database
			Class.forName("org.postgresql.Driver");
			dbConn = DriverManager.getConnection("jdbc:postgresql://10.105.11.91/template1", "sumedh", "sumedh");
		
			t=new Thread(this);
			t.start();
		}
		catch(Exception e)	{
			System.out.println(e);
		}
	}

	synchronized public void run(){  
		int check=0;
		while(true){
			try {
				int count=0;
				V.friends.removeAllElements();
				V.friendsPanel.removeAll();
				Statement ins1 = dbConn.createStatement();
				ResultSet res=ins1.executeQuery("select* from "+V.userId);

				while(res.next()){
					String tempUserId =res.getString(1);
					//to check for any friend requests the user has received.the requestors username is stored as "#username" in the users table.
					if((res.getString(1)).charAt(0) == '#'){
						tempUserId=tempUserId.substring(1);
						// asks user if request is acceptable.
						int answer=JOptionPane.showConfirmDialog(V.vchatFrame,tempUserId+" wants to add you to his friendlist",null,JOptionPane.YES_NO_OPTION);
						if( answer == JOptionPane.YES_OPTION){
							ins1.executeUpdate("update "+V.userId+" set username='"+tempUserId+"' where username='"+res.getString(1)+"'");
							//update the requestors friendlist if accepted 
							ins1.executeUpdate("update "+tempUserId+" set username='"+ V.userId+"' where username='%"+V.userId+"'");
						}
						else{//reject the request
							ins1.executeUpdate("delete from "+V.userId+" where username='"+res.getString(1)+"'"); 
							ins1.executeUpdate("delete from "+tempUserId+" where username='%"+V.userId+"'"); 
						}
					}

					Statement ins = dbConn.createStatement();
					ResultSet results2=ins.executeQuery("select* from currentip where username='"+tempUserId+"'");
					if(results2.next()){
						V.friends.addElement(results2.getString(1));
						String status = results2.getString(3);

						if(!status.equals("Invisible")){
							count++;
							V.friendButton=new JButton(tempUserId+"  -  "+status);
							if(!status.equals("Available") && !status.equals("available"))
								V.friendButton.setIcon(new ImageIcon("busy.gif"));						
							else
								V.friendButton.setIcon(new ImageIcon("avl.jpg"));	
							V.friendButton.setFont(loginFont);
							V.friendButton.setBackground(Color.white);
							V.friendButton.setForeground(Color.black);
							V.friendButton.setBorder(new LineBorder(Color.WHITE, 0));
							V.friendButton.addMouseListener(V);
							V.friendButton.setName(tempUserId);
							V.friendsPanel.add(V.friendButton,BorderLayout.WEST);
						}
						else{					//online but invisible
							latestInvisibles.addElement(results2.getString(1));
							if(check==0)
								prevInvisibles.addElement(results2.getString(1));
							if(V.showOffline){
								V.friendButton=new JButton(tempUserId+"  -  Offline");
								V.friendButton.setIcon(new ImageIcon("offline.gif"));	
								V.friendButton.setFont(new Font("Comic Sans", Font.ITALIC, 15));
								V.friendButton.setBackground(Color.white);
								V.friendButton.setForeground(Color.black);
								V.friendButton.setBorder(new LineBorder(Color.WHITE, 0));
								V.friendButton.addMouseListener(V);
								V.friendsPanel.add(V.friendButton,BorderLayout.WEST);
								V.friendButton.setName(tempUserId);
							}
							
						}
					}
					else if(V.showOffline){//offline
						V.friendButton=new JButton(tempUserId+"  -  Offline");
						V.friendButton.setIcon(new ImageIcon("offline.gif"));	
						V.friendButton.setFont(new Font("Comic Sans", Font.ITALIC, 15));
						V.friendButton.setBackground(Color.white);
						V.friendButton.setForeground(Color.black);
						V.friendButton.setBorder(new LineBorder(Color.WHITE, 0));
						V.friendButton.addMouseListener(V);
						V.friendsPanel.add(V.friendButton,BorderLayout.WEST);
						V.friendButton.setName(tempUserId);
					}
				}
				++check;
				if(count > friendSol){//display alert to show someone has come ol
					for(int i=0; i< V.friends.size();i++){
						if(!temp.contains((String)V.friends.elementAt(i)) || (prevInvisibles.contains((String)V.friends.elementAt(i)) && !latestInvisibles.contains((String)V.friends.elementAt(i)))){
							new Play("knock.wav");
							new Alert((String)V.friends.elementAt(i) +" has signed in","avl.jpg");
							break;
						}
					}
				}

				if(friendSol > count){//display alert to show someone has gone offline
					for(int i=0; i< temp.size();i++){
						String tmp=(String)temp.elementAt(i);
						if(!V.friends.contains((String)temp.elementAt(i)) || latestInvisibles.contains((String)temp.elementAt(i))){
							new Play("door.wav");
							new Alert((String)temp.elementAt(i) +" has signed out","offline.gif");
							break;
						}
					}
				}
				temp.clear();
				prevInvisibles.clear();
				for(int i=0;i<V.friends.size();++i){
					temp.addElement(V.friends.elementAt(i));
				}
				for(int i=0;i<latestInvisibles.size();++i){
					prevInvisibles.addElement(latestInvisibles.elementAt(i));
				}
				latestInvisibles.clear();
				friendSol = count;
				V.vchatFrame.repaint();
				V.vchatFrame.pack();
				t.sleep(1000);
			} 
			catch (Exception exc) {
				System.out.println(exc);
			}
		}
	}
}
