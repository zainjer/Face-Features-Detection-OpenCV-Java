package com.company;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

public class Main {

    static {System.loadLibrary(Core.NATIVE_LIBRARY_NAME);}

    public static void main(String[] args) {

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


        VideoCapture cap = new VideoCapture(1);
        Mat frame = new Mat();


        Scalar col1 = new Scalar(111, 71, 239);
        Scalar col2 = new Scalar(102, 209, 255);
        Scalar col3 = new Scalar(160, 214, 6);
        Scalar col4 = new Scalar(178, 138, 17);
        Scalar col5 = new Scalar(76, 59, 7);

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
                    Imgproc.rectangle(frame, r, col1, 2);
                    int font = Imgproc.FONT_HERSHEY_DUPLEX;

                    Point startingPoint = new Point(r.tl().x-1,r.br().y);
                    Point endingPoint = new Point(r.br().x,r.br().y+25);
                    Imgproc.rectangle(frame,startingPoint,endingPoint,col1,-1);

                    double fontScale = (r.br().x/r.tl().x)/3;
                    //System.out.println(fontScale);

                    Imgproc.putText(frame,"Person Detected",new Point(r.tl().x+5,r.br().y+18),font,fontScale,new Scalar(255, 255, 255),1);

                    Mat face = new Mat(frame,r);

                    //EYE DETECTION CODE STARTS HERE
                    MatOfRect eyes = new MatOfRect();
                    eyeCascade.detectMultiScale(face,eyes,1.1,3,0);
                    Rect[] eyeRects = eyes.toArray();


                    //if(eyeRects.length<=2 && eyeRects.length>0 ) //For 1 or 2 eye draw
                    if(eyeRects.length<3 && eyeRects.length>0) // For eye draw on all faces
                    {
                        for (Rect e : eyeRects) {
                            Point tl = new Point(r.tl().x + e.tl().x, r.tl().y + e.tl().y);
                            Point br = new Point(r.tl().x + e.br().x, r.tl().y + e.br().y);

                            //Imgproc.rectangle(frame,tl,br, new Scalar(123, 189, 0),2);
                            Imgproc.rectangle(frame, tl, br, col2, 2);
                            double centerX = ((br.x - tl.x) / 2) + tl.x;
                            double centerY = ((br.y - tl.y) / 2) + tl.y;

                            Point center = new Point(centerX, centerY);
                            double radius = (br.x - tl.x) / 2;

                            Imgproc.circle(frame, center, (int) radius, col2);
                            Imgproc.line(frame, br, new Point(frame.width() - 145, br.y), col2, 2);


                            //Imgproc.putText(frame,"Eye",new Point(centerX,br.y-10),font,0.5,new Scalar(255, 255, 255),1);
                            Imgproc.putText(frame, "Analyzing Eyes..", new Point(frame.width() - 140, br.y), font, 0.5, new Scalar(255, 255, 255), 1);

                        }
                    }
                    //EYE DETECTION CODE ENDS HERE


                    //NOSE DETECTION CODE STARTS HERE
                    MatOfRect nose = new MatOfRect();
                    noseCascade.detectMultiScale(face,nose,1.1,5,0);
                    Rect[] noseRects = nose.toArray();

                    if(noseRects.length==1) {
                        for (Rect n : noseRects) {
                            //209, 195, 0
                            Point tl = new Point(r.tl().x + n.tl().x, r.tl().y + n.tl().y);
                            Point br = new Point(r.tl().x + n.br().x, r.tl().y + n.br().y);

                            Imgproc.rectangle(frame, tl, br, col3, 2);
                            Imgproc.line(frame, br, new Point(frame.width() - 205, br.y), col3, 2);

                            //Imgproc.putText(frame,"Nose",new Point(tl.x, br.y),font,0.5,new Scalar(255, 255, 255),1);
                            Imgproc.putText(frame, "Determining Nose type", new Point(frame.width() - 200, br.y), font, 0.5, new Scalar(255, 255, 255), 1);

                        }
                    }
                    //Nose DETECTION CODE ENDS HERE


                    //MOUTH DETECTION CODE STARTS HERE
                    MatOfRect mouth = new MatOfRect();
                    mouthCascade.detectMultiScale(face,mouth,1.5,5,0);
                    Rect[] mouthRects = mouth.toArray();

                    if(mouthRects.length==1) {
                        for (Rect m : mouthRects) {
                            //209, 195, 0
                            Point tl = new Point(r.tl().x + m.tl().x, r.tl().y + m.tl().y);
                            Point br = new Point(r.tl().x + m.br().x, r.tl().y + m.br().y);

                            Imgproc.rectangle(frame, tl, br, col4, 2);
                            //Imgproc.putText(frame,"Mouth",new Point(tl.x, br.y),font,0.5,new Scalar(255, 255, 255),1);
                            Imgproc.line(frame, br, new Point(frame.width() - 155, br.y), col4, 2);
                            Imgproc.putText(frame, "Detecting lips...", new Point(frame.width() - 150, br.y), font, 0.5, new Scalar(255, 255, 255), 1);

                        }
                    }
                    //MOUTH DETECTION CODE ENDS HERE
                }
            }
            HighGui.imshow("Feature Detection",frame);
            if (HighGui.waitKey(30)>=0) break;
        }
    }
}
