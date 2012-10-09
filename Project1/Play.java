import java.io.File;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

/**
 * @author Ashwin,Avijit,Sumedh,Hemendra
 *The Play Method is used to play the various sound alerts used in Vchat.
 *It takes the name of the sound to be played as input  in string form.
 */
public class Play extends Thread{
	AudioFormat audioFormat;
	AudioInputStream audioInputStream;
	SourceDataLine sourceDataLine;

	public Play(String s){
		try{
			File soundFile =  new File(s);
			audioInputStream = AudioSystem.getAudioInputStream(soundFile);
			audioFormat = audioInputStream.getFormat();
			DataLine.Info dataLineInfo =new DataLine.Info(SourceDataLine.class,audioFormat);
			sourceDataLine =(SourceDataLine)AudioSystem.getLine(dataLineInfo);
			this.start();
		}catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	byte tempBuffer[] = new byte[10000];

	public void run(){
		try{
			sourceDataLine.open(audioFormat);
			sourceDataLine.start();
			int cnt;
			while((cnt = audioInputStream.read(
					tempBuffer,0,tempBuffer.length)) != -1
			){
				if(cnt > 0){
					sourceDataLine.write(tempBuffer, 0, cnt);
				}
			}
			sourceDataLine.drain();
			sourceDataLine.close();
		}catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
}
