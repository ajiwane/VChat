import java.net.*;
import java.io.*;
import java.util.*;

/**
 * @author Ashwin,Avijit,Sumedh,Hemendra
 *This is main Server of the VChat.
 *Its create the server for message transfer, file transfer and reply of file transfer,and continously check for clients to join the server
 *Whenever one gets join,it creates ServerSendsMessages,ServerTakesMessages and ServerForFileTransfer threads separately for each user
 */

public class VServer{

	static VChat vchat;
	static String nextMessageBy;
	static String nextMessageTo;
	static DataInputStream readMessage;
	static Vector threads=new Vector();
	static String nextMessage; 
	Socket clientSoc;
	static ServerSocket ser;
	static ServerSocket fileTransferSocket;
	Socket fileClientSocket;
	ServerSocket fileClientReponseSocket;
	Socket replyFromFileAcceptor;
	static Vector sendingThreads=new Vector();
	static Vector serverReadsFileThreads=new Vector();
	static String reply="NO";
	static String nextFileTransferFrom;

	VServer(){
		try{
			ser = new ServerSocket(5678);
			fileTransferSocket=new ServerSocket(8888);
			fileClientReponseSocket=new ServerSocket(8889); 
		}
		catch(Exception ex){}

		try{
			while((clientSoc=ser.accept())!=null)	{
				readMessage = new DataInputStream(clientSoc.getInputStream());
				String userid=new String (readMessage.readUTF());
								
				ServerSendsMessages s=new ServerSendsMessages(userid,clientSoc);
				sendingThreads.add(s);
				new ServerTakesMessages(userid,clientSoc);
				fileClientSocket=fileTransferSocket.accept();
				replyFromFileAcceptor=fileClientReponseSocket.accept();
				new ServerForFileTransfer(fileClientSocket,replyFromFileAcceptor);
			}	
		}
		catch (Exception ex){}
	}

	public  static void main(String args[]) throws Exception{
		new VServer();
	}
}


