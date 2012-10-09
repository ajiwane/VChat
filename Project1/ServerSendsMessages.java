import java.io.DataOutputStream;
import java.net.Socket;

/**
 *@author Ashwin,Avijit,Sumedh,Hemendra
 *This thread sends message to the user(userId) whenever its get notify 
 */
public class ServerSendsMessages implements Runnable{
	Thread t;	
	static String userId;
	static Socket serverSoc;
	DataOutputStream message;
	static int check=0;
	InstantMessage im;
	static boolean stop=false;
	public ServerSendsMessages(String userId,Socket serverSoc){
		try{
			this.serverSoc=serverSoc;
			this.userId=userId;
			message=new DataOutputStream(serverSoc.getOutputStream());
			t=new Thread(this,"sendsto"+userId);
			t.start();
		}
		catch(Exception e){
			System.out.println(e);
		}
	}
	synchronized void notifyit(){
		notify();
	}
	synchronized public void stopit(){
		stop=true;
	}
	synchronized public void run(){
		try{
			while(!stop){	
				wait();
				message.writeUTF(VServer.nextMessage);
			}
		}catch(Exception e){}
	}
}
