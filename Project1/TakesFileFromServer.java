import javax.swing.*;
import java.io.*;
import java.net.*;

/**
*@author Ashwin,Avijit,Sumedh,Hemendra
*This thread continously checks if serveroffiletransfer is writing file to it.  
*If yes then it pops up the window which asks for confirmation.
*If user accepts it, then it write that file otherwise it does not.
*/
public class TakesFileFromServer implements Runnable{

	JFrame vchatFrame;
	Socket fileTransferSocket;
	Thread t;
	Socket replyFromFileAcceptor;
	JFileChooser fileChooser;
	public TakesFileFromServer(JFrame vchatFrame,Socket fileTransferSocket,Socket s)	{
		this.vchatFrame=vchatFrame;
		this.fileTransferSocket=fileTransferSocket;
		replyFromFileAcceptor=s;
		fileChooser = new JFileChooser();
		t=new Thread(this,"getfile");
		t.start();
	}
	
	synchronized public void run(){
		try{
			while(true)	{
				DataInputStream message=new DataInputStream(fileTransferSocket.getInputStream());
				String nextMessage=new String (message.readUTF());
				String filefrom=nextMessage.substring(0,nextMessage.indexOf(":"));
				String filename=nextMessage.substring(nextMessage.indexOf(":")+1);
				int result=JOptionPane.showConfirmDialog(vchatFrame,"You are about to recieve file "+filename+" from "+filefrom+"\n Do u want to accept the file?");
				DataOutputStream sendsreply=new DataOutputStream(replyFromFileAcceptor.getOutputStream());

				if (result == JOptionPane.YES_OPTION){
					int returnval = fileChooser.showSaveDialog(vchatFrame);
					if(returnval == JFileChooser.APPROVE_OPTION){
						File newfile = fileChooser.getSelectedFile();
						sendsreply.writeUTF("YES");
						InputStream s1In = fileTransferSocket.getInputStream();
						FileOutputStream fos = new FileOutputStream(newfile);
						int bytesRead;
						while((bytesRead=s1In.read())!=255){
							fos.write(bytesRead);
						}
                                                JOtionPane.showMessageDialog(vchatFrame, filename+" was successfully transferred from "+filefrom+" and saved as "+newfile.getName());
						fos.flush();
					}
				}
				else{
					sendsreply.writeUTF("NO");
				}
			}
		}catch(Exception e){}
	}
}
