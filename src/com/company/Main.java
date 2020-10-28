package com.company;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.video.Video;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import java.awt.*;
import java.io.IOException;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    static {System.loadLibrary(Core.NATIVE_LIBRARY_NAME);}

    private CascadeClassifier faceCascade;
    private VideoCapture capture;
    private int absoluteFaceSize;
    private Timer timer;

    public static void main(String[] args) throws IOException {

        CascadeClassifier faceCascade = new CascadeClassifier();
        CascadeClassifier eyeCascade = new CascadeClassifier();
        CascadeClassifier noseCascade = new CascadeClassifier();
        CascadeClassifier mouthCascade = new CascadeClassifier();

        if(!faceCascade.load("resource/haarcascade_frontalface_alt.xml")){
            System.out.println("could not load haarcascade_frontalface_alt.xml");
            return;
        }
        if(!eyeCascade.load("resource/haarcascade_eye_tree_eyeglasses.xml")){
            System.out.println("could not load haarcascade_eye_tree_eyeglasses.xml");
            return;
        }

        if(!noseCascade.load("resource/nose.xml")){
            System.out.println("could not load nose.xml");
            return;
        }
        if(!mouthCascade.load("resource/mouth.xml")){
            System.out.println("could not load mouth.xml");
            return;
        }

        VideoCapture cap = new VideoCapture(0);
        Mat frame = new Mat();

        if(!cap.isOpened()){
            System.out.println("No cam found");
            return;
        }
        while (true) {
            cap.read(frame);

            Mat gray = new Mat();

            Imgproc.cvtColor(frame, gray, Imgproc.COLOR_BGR2GRAY);
            Imgproc.equalizeHist(gray, gray);
            MatOfRect faces = new MatOfRect();
            faceCascade.detectMultiScale(gray, faces, 1.1, 4, 0, new Size(100, 100));

            Rect[] faceRects = faces.toArray();
            if(faceRects.length>0) {
                for (Rect r : faceRects) {
                    //209, 195, 0
                    Imgproc.rectangle(frame, r, new Scalar(189, 176, 0), 3);
                    Mat face = new Mat(frame,r);

                    //EYE DETECTION CODE STARTS HERE
                    MatOfRect eyes = new MatOfRect();
                    eyeCascade.detectMultiScale(face,eyes,1.1,3,0);
                    Rect[] eyeRects = eyes.toArray();

                    if(eyeRects.length<=2 && eyeRects.length>0 ) //For 1 or 2 eye draw
                    //if(eyeRects.length>1) // For eye draw on all faces
                    {
                        for (Rect e:eyeRects) {
                            //209, 195, 0
                            Point tl = new Point(r.tl().x+e.tl().x,r.tl().y+e.tl().y);
                            Point br = new Point(r.tl().x+e.br().x,r.tl().y+e.br().y);

                            Imgproc.rectangle(frame,tl,br, new Scalar(123, 189, 0),2);
                        }
                    }
                    //EYE DETECTION CODE ENDS HERE


                    //NOSE DETECTION CODE STARTS HERE
                    MatOfRect nose = new MatOfRect();
                    noseCascade.detectMultiScale(face,nose,1.1,10,0);
                    Rect[] noseRects = nose.toArray();

                    if(noseRects.length==1)
                    {
                        for (Rect n:noseRects) {
                            //209, 195, 0
                            Point tl = new Point(r.tl().x+n.tl().x,r.tl().y+n.tl().y);
                            Point br = new Point(r.tl().x+n.br().x,r.tl().y+n.br().y);

                            Imgproc.rectangle(frame,tl,br, new Scalar(181, 119, 100),2);
                        }
                    }
                    //Nose DETECTION CODE ENDS HERE


                    //MOUTH DETECTION CODE STARTS HERE
                    MatOfRect mouth = new MatOfRect();
                    mouthCascade.detectMultiScale(face,mouth,1.5,4,0);
                    Rect[] mouthRects = mouth.toArray();

                    if(mouthRects.length==1)
                    {
                        for (Rect m:mouthRects) {
                            //209, 195, 0
                            Point tl = new Point(r.tl().x+m.tl().x,r.tl().y+m.tl().y);
                            Point br = new Point(r.tl().x+m.br().x,r.tl().y+m.br().y);

                            Imgproc.rectangle(frame,tl,br, new Scalar(100, 100, 181),2);
                        }
                    }
                    //MOUTH DETECTION CODE ENDS HERE


                }
            }
//            MatOfRect eyes = new MatOfRect();
//            eyeCascade.detectMultiScale(face,eyes,1.1,5,0);
//            Rect[] eyeRects = eyes.toArray();
//            if(eyeRects.length<=2 && eyeRects.length>0 ){
//                for (Rect e:eyeRects) {
//                    //209, 195, 0
//                    Point tl = new Point(faceRects[0].tl().x+e.tl().x,faceRects[0].tl().y+e.tl().y);
//                    Point br = new Point(faceRects[0].tl().x+e.br().x,faceRects[0].tl().y+e.br().y);
//
//                    Imgproc.rectangle(frame,tl,br, new Scalar(123, 189, 0),2);
//                }
//            }

                //Imgproc.rectangle(frame, faceRects[0], new Scalar(209, 195, 0),2);
            HighGui.imshow("Frame", frame);

            if (HighGui.waitKey(30)>=0) break;

        }

    }

}
