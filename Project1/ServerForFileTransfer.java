import java.io.*;
import java.net.*;
import java.util.Hashtable;
import java.util.Vector;

/**
 *@author Ashwin,Avijit,Sumedh,Hemendra
 *This thread creates two new threads (ServerReadsFile and ResponseForFileTransfer) and gets kill
 */
public class ServerForFileTransfer implements Runnable{
	Thread t;
	Socket clientSoc;
	static DataInputStream read;
	static Hashtable users = new Hashtable();
	Socket replyFromFileAcceptor;
	public ServerForFileTransfer(Socket fileclientsocket,Socket replyFromFileAcceptor){
		try{
			this.replyFromFileAcceptor=replyFromFileAcceptor;
			clientSoc=fileclientsocket;
			t=new Thread(this,"file");
			t.start();
		}
		catch(Exception ex){}
	}

	synchronized public void run(){
		try{
			read = new DataInputStream(clientSoc.getInputStream());
			String userId=new String (read.readUTF());
			users.put(userId, clientSoc);
			VServer.serverReadsFileThreads.add(new ServerReadsFile(userId,clientSoc,replyFromFileAcceptor));
			new ResponseForFileTransfer(replyFromFileAcceptor);
		}
		catch(Exception ex){}
	}
}
