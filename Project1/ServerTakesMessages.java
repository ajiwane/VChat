import java.io.DataInputStream;
import java.net.Socket;
import java.util.Hashtable;

/**
 *@author Ashwin,Avijit,Sumedh,Hemendra
 *This thread continously checks for message from the user(userId) 
 *and notifys the message sending thread which corresponds to the user(VServer.nextMessageTo) 
 *whom userId wants to send the message.
 */
public class ServerTakesMessages implements Runnable{
	Thread t;
	DataInputStream message;
	static String userId;
	static Socket clientSoc;
	static boolean stop=false;
	ServerTakesMessages(String userId,Socket clientSoc){
		try{
			this.clientSoc=clientSoc;
			this.userId=userId;
			message=new DataInputStream(clientSoc.getInputStream());
			t=new Thread(this,"from"+userId);
			t.start();
		}
		catch(Exception e)	{
			System.out.println(e);
		}
	}

	synchronized public void stopit(){
		stop=true;
	}
	synchronized public void run(){
		try{
			while(!stop){
				synchronized (this){
					String nextMessage=new String (message.readUTF());
					int i=nextMessage.indexOf(":");
					/*if(nextMessage.substring(i+1).equals("[[[###SIGNOUT"))/*{
						for(int i1=0;i1<VServer.sendingThreads.size();++i1){
							if(((ServerSendsMessages)VServer.sendingThreads.elementAt(i1)).t.getName().equals("sendsto"+userId)){
								((ServerSendsMessages)VServer.sendingThreads.elementAt(i1)).stopit();
								VServer.sendingThreads.remove(i1);
							}	
						}
						//stopit();
					}		*/				
					//else{	
						{VServer.nextMessageBy=userId;
						VServer.nextMessageTo=nextMessage.substring(0,i);
						VServer.nextMessage=nextMessage.substring(i+1);
						for(int i1=0;i1<VServer.sendingThreads.size();++i1){
							if(((ServerSendsMessages)VServer.sendingThreads.elementAt(i1)).t.getName().equals("sendsto"+VServer.nextMessageTo)){
								((ServerSendsMessages)VServer.sendingThreads.elementAt(i1)).notifyit();
							}	
						}
					}			
				}
			}
		}catch(Exception e){}
	}

}
