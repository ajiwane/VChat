import java.io.*;
import java.net.*;
import java.util.*;

/**
 *@author Ashwin,Avijit,Sumedh,Hemendra
 *This thread continously checks for message from the server 
 *if instantmessage window of the messagesendinguser is open then it does not open it otherwise it opens a new window
 *and displys the message received in the window 
 */
public class TakesInputfromServer  implements Runnable{

	Thread t;
	Socket serverSoc;
	DataInputStream message;
	int check=0;
	String userId;
	Vector V;
	Socket fileTransferSocket;
	Socket replyFromFileAcceptor;
	
	public TakesInputfromServer(Socket s,String userId,Vector v,Socket fs,Socket replysoc){
		try{
			V=v;
			this.serverSoc=s;		
			this.userId=userId;
			fileTransferSocket=fs;
			replyFromFileAcceptor=replysoc;
			message=new DataInputStream(serverSoc.getInputStream());
			t=new Thread(this,"takesinput");
			t.start();
		}
		catch(Exception e){}
	}
	synchronized public void run(){

		try{
			while(true){
				String nextMessage=new String (message.readUTF());
				String messageFrom=nextMessage.substring(0,nextMessage.indexOf(":"));
				for (int i=0;i<VChat.ims.size();++i){
					if(((InstantMessage)VChat.ims.elementAt(i)).t.getName().equals(messageFrom)){
						((InstantMessage)VChat.ims.elementAt(i)).notifyit(nextMessage);
						check=1;
					}
				}
				if(check==0)	{
					InstantMessage im=new InstantMessage(userId,messageFrom,serverSoc,fileTransferSocket,V,replyFromFileAcceptor);
					im.notifyit(nextMessage);
					new Play("chimeup.wav");
					VChat.ims.addElement(im);
				}
				if(check==1)
					check=0;
			} 
		}
		catch(Exception e){}
	}
}
