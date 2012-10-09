import java.io.*;
import java.net.*;

/**
*@author Ashwin,Avijit,Sumedh,Hemendra
*This thread mainly gets the file from filesending user and write it to filereceiving user, if both mutualy agrees
*/
public class ServerReadsFile implements Runnable{
	Socket mySocket;
	InputStream nextInFileByte;
	DataInputStream writeFileFromUser;
	String userId;
	Thread t;
	int nextByte;
	Socket replyFromFileAcceptor;
	public ServerReadsFile(String userId,Socket s,Socket replysoc){
		try{
			replyFromFileAcceptor=replysoc;
			this.userId=userId;
			mySocket=s;
			nextInFileByte= mySocket.getInputStream();
			writeFileFromUser=new DataInputStream(mySocket.getInputStream());
			t=new Thread(this,userId+"readsfilefrom");
			t.start();
		}
		catch(Exception ex){}
	}

	synchronized void notifyit(){
		notify();
	}
	synchronized public void run(){
		try{
			while(true)	{
				int i=0;
				String next=new String (writeFileFromUser.readUTF());
				String nextuser=next.substring(0,next.indexOf(":"));
				VServer.nextFileTransferFrom=userId;
				String nextfilename=next.substring(next.indexOf(":")+1);
				Socket nextsoc= (Socket)ServerForFileTransfer.users.get(nextuser);
				DataOutputStream sendfilename=new DataOutputStream(nextsoc.getOutputStream());
				sendfilename.writeUTF(userId+":"+nextfilename);
				wait();
				DataOutputStream sendconfirmation=new DataOutputStream(replyFromFileAcceptor.getOutputStream());
				sendconfirmation.writeUTF(VServer.reply);
				if(VServer.reply.equals("YES")){	
					OutputStream nextoutfilebyte=nextsoc.getOutputStream();
					while(true){
						nextByte=nextInFileByte.read();
						if(nextByte==255){
							nextoutfilebyte.write(-1);
							break;
						}
						nextoutfilebyte.write(nextByte);
					}
				}
			}
		}catch(Exception ex){}
	}
}
