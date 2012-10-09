import java.awt.*;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * @author Ashwin,Avijit,Sumedh,Hemendra
 *The alert thread is used  to display alert dialogue boxes at the bottom right corner of the screen
 *whenever a friend goes offline or comes on-line.
 *A corrponding image to the status of the friend is also displayed in the alert.
 */
public class Alert implements Runnable{
	Thread t;
	JFrame alertFrame;
	JLabel info; 
	Dimension scrnsize;
	JButton Message;
	public Alert(String message, String image){
		Message = new JButton(message);
		Message.setIcon(new ImageIcon(image));		
		alertFrame=new JFrame();
		alertFrame.setTitle("Velox  Chat");
		alertFrame.setBackground(Color.white);
		alertFrame.setSize(new Dimension(200,50));
		alertFrame.getContentPane().setLayout(new BorderLayout());
		alertFrame.setResizable(false);
		alertFrame.setFocusableWindowState(false);

		try{
			alertFrame.setIconImage(ImageIO.read(new File("vlogo.jpg")));
			alertFrame.getContentPane().add(Message);
			Toolkit toolkit = Toolkit.getDefaultToolkit();
			scrnsize = toolkit.getScreenSize();
			t=new Thread(this);
			t.start();
		}catch(Exception e){
			System.out.println(e);
		}
	}


	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 *while running the thread a new Jframe is opened.This jframe is set to always on top condition 
	 *so that the alert is seen  even if the user is currently using some other programme.
	 * the thread is made to sleep for 3 seconds so that the frame is seen for 3 seconds after that it is disposed and the thread is killed
	 */
	synchronized public void run(){
		try{
			alertFrame.pack();
			//alertFrame.setAlwaysOnTop(true);
			alertFrame.setLocation(new Point( (int)scrnsize.getWidth()-200,(int)scrnsize.getHeight()-100));
			alertFrame.setVisible(true);
			t.sleep(3000);
			alertFrame.dispose();
		}catch(Exception e){}
	}
}
