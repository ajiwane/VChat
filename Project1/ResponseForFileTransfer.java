import java.io.*;
import java.net.*;

/**
*@author Ashwin,Avijit,Sumedh,Hemendra
*This thread continously checks for reply about file transfer acceptance 
*/
public class ResponseForFileTransfer implements Runnable{
	Socket s;
	Thread t;
	DataInputStream response;
	public ResponseForFileTransfer(Socket replyfromfileacceptor){
		try{
			s=replyfromfileacceptor;
			response=new DataInputStream (s.getInputStream());
			t=new Thread(this,"response");
			t.start();		
		}catch(Exception ex){}
	}

	synchronized public void run(){
		try{
			while(true)	{
				String reply=response.readUTF();
				VServer.reply=reply;
				for(int i1=0;i1<VServer.serverReadsFileThreads.size();++i1){
					if(((ServerReadsFile)VServer.serverReadsFileThreads.elementAt(i1)).t.getName().equals(VServer.nextFileTransferFrom+"readsfilefrom")){
						((ServerReadsFile)VServer.serverReadsFileThreads.elementAt(i1)).notifyit();
					}			
				}
			}
		}catch(Exception ex){}
	}
}
