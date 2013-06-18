package opencv;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Scalar;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.KeyPoint;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;

import util.ImageViewer;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class CornerDetector
{
	private JFrame videoCamara;
	private JFrame videoCamaraResult;
	
	private ImageViewer viewerVideoCamara;
	private ImageViewer viewerVideoCamaraResult;
	
	private Mat src;
	private Mat frame_gray;
	VideoCapture video;
	Random number = new Random(12345);
	
	private CornerDetector initFrames()
	{
		videoCamara = new JFrame("Video Camara");
		videoCamara.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		 viewerVideoCamara = new ImageViewer();
		 videoCamara.setContentPane(viewerVideoCamara.getGui());
		 
		 videoCamara.pack();
		 videoCamara.setLocationByPlatform(true);
		 videoCamara.setVisible(true);
		 
		 
		 videoCamaraResult = new JFrame("Video Camara Result");
		 videoCamaraResult.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
		 viewerVideoCamaraResult = new ImageViewer();
		 videoCamaraResult.setContentPane(viewerVideoCamaraResult.getGui());
			 
		 videoCamaraResult.pack();
		 videoCamaraResult.setLocationByPlatform(true);
		 videoCamaraResult.setVisible(true);
		 
		 return this;
	}
	
	
	private void run()
	{
		if (video != null) {
            VideoCapture camera = video;
            video = null; // Make it null before releasing...
            camera.release();
        }
		
		video = new VideoCapture(0);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		if(!video.isOpened())
			JOptionPane.showMessageDialog(null, "Cam can not found", "Error", JOptionPane.ERROR_MESSAGE);
		
		src = new Mat();
		frame_gray = new Mat();
		
		boolean process = true;
		
		int counter = 0;
		
		while(process && video != null)
		{
			boolean grabbed = video.grab();
			if(grabbed)
			{
				video.retrieve(src, Highgui.CV_CAP_ANDROID_COLOR_FRAME_RGB);
				
				//Gray color
				Imgproc.cvtColor(src, frame_gray, Imgproc.COLOR_BGRA2GRAY);
				
				//Init fast
				MatOfKeyPoint matkp = new MatOfKeyPoint();
				FeatureDetector fast = FeatureDetector.create(FeatureDetector.FAST);
				fast.detect(frame_gray, matkp);
				
				//draw circles
				for(KeyPoint kp: matkp.toList())
				{
					Core.circle(frame_gray, kp.pt, 4, new Scalar( number.nextInt(255), number.nextInt(255), number.nextInt(255) ));
				}
				
		        //Video Camara
		        MatOfByte matOfByte = new MatOfByte();
				Highgui.imencode(".png", src, matOfByte); 
				byte[] byteArray = matOfByte.toArray();
			    BufferedImage bufImage = null;
			    try {
			        InputStream in = new ByteArrayInputStream(byteArray);
			        bufImage = ImageIO.read(in);
			        viewerVideoCamara.setImage(bufImage);
			    } catch (Exception e) {
			        e.printStackTrace();
			    }
		        
		        //Video Camara Result
				MatOfByte matOfByteResult = new MatOfByte();
				Highgui.imencode(".png", frame_gray, matOfByteResult); 
				byte[] byteArrayResult = matOfByteResult.toArray();
			    BufferedImage bufImageResult = null;
			    try {
			        InputStream in = new ByteArrayInputStream(byteArrayResult);
			        bufImageResult = ImageIO.read(in);
			        viewerVideoCamaraResult.setImage(bufImageResult);
			    } catch (Exception e) {
			        e.printStackTrace();
			    }
				if(counter == 500)
					process = false;
			    counter++;
			}
		}
		
		if (video != null) {
			video.release();
			video = null;
        }
		
		/*src = Highgui.imread(getClass().getResource("/frame_0.png").getPath());
		src_aux = src.clone();
		
		MatOfKeyPoint matkp = new MatOfKeyPoint();
		FeatureDetector fast = FeatureDetector.create(FeatureDetector.FAST);
		fast.detect(frame_gray, matkp);
		
		for(KeyPoint kp: matkp.toList())
		{
			Core.circle(frame_gray, kp.pt, 4, new Scalar( number.nextInt(255), number.nextInt(255), number.nextInt(255) ));
		}
		
		Highgui.imwrite("frame_0-cornerDetection.png", src_aux);*/
	}
	
	public static void main (String args[])
	{
		// Load the native library.
	    System.loadLibrary("opencv_java245");
		new CornerDetector().initFrames().run();
	}
}