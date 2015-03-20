package com.example.barrelracing;



import java.util.Date;
import java.util.HashMap;

import com.example.barrelracing.R.drawable;
import com.example.service.PixelCoOrdinates;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.format.Time;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;


/*
 * Code Written By  : Rakesh Manoharan & Pavan Trinath
 * Net ID 			: rxm143130 & BXV131230
 * Course 			: CS6301
 * Class Name		: GameScreen.java
 * Date				: 12-30-2014
 * Description 		:
 * 		GameScreen class extends the Activity and where it implements all the logics related to game.
 * We are mainly taking advantage of the onDraw() function which redraws the screen constantly. 
 */
public class GameScreen extends Activity  implements SensorEventListener  {
	static Sensor accelerometerSensor;
	static SensorManager sensorManager;
	public static Vibrator vibrate;
	public static float rodeoX=495;
	public static float rodeoY=45;
	public static float accelerometer_reading=8;
	static float height;
	static float width;
	static boolean onTouched=false;
	static boolean leftAvisited=false,rightAvisited=false,topAvisited=false;
	static boolean leftBvisited=false,bottomBvisited=false,rightBvisited=false;
	static boolean rightCvisited=false,topCvisited=false,bottomCvisited=false;
	static String visitedA="";
	static String firstVisitedA="";
	static boolean removeBarrellA=false;
	static String visitedB="";
	static String firstVisitedB="";
	static boolean removeBarrellB=false;
	static String visitedC="";
	static String firstVisitedC="";
	static boolean removeBarrellC=false;		
	static Date startDate,endDate;
	static long startTime=0, endTime, diff;
	static long penalty=0;
	static int warningCounter=101;
	long pauseTime=0;
	static long pauseValue=0;
	static boolean penaltyEnabled=false;
	static int penaltyEnableCounter=1;
	SharedPreferences.Editor editor;
	static boolean waitGame=false;
	static int waitGameCounter=0;
	static boolean backPressed=false;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
 		requestWindowFeature(Window.FEATURE_NO_TITLE); // Title bar is removed from the game screen.

 		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
 		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
 		accelerometerSensor = sensorManager
 				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
 		sensorManager.registerListener(this, accelerometerSensor,
 				SensorManager.SENSOR_DELAY_NORMAL); // ACCELEROMETER sensor is registered with this statement.
 		vibrate = (Vibrator) getSystemService(VIBRATOR_SERVICE);
 		SharedPreferences sharedPref = getSharedPreferences("settings", 0);
 		accelerometer_reading=sharedPref.getFloat("accelarator_reading", 8);

        sharedPref.edit();
        editor = sharedPref.edit();
 		setContentView(new GameView(this));
 		
    }
    
    private static class GameView extends View{

		Display display;
		
		Canvas canvas = new Canvas();
		
		SharedPreferences sharedP;;
		
		Point point=new Point();
		
		
		Bitmap barrel1 = BitmapFactory.decodeResource(getResources(),
				drawable.barrell);
		Bitmap stadiumGround = BitmapFactory.decodeResource(getResources(),
				drawable.back);
		Bitmap rodeo = BitmapFactory.decodeResource(getResources(),
				drawable.rodeo);    
		Bitmap play = BitmapFactory.decodeResource(getResources(),
				drawable.play);
		Bitmap timer = BitmapFactory.decodeResource(getResources(),
				drawable.timer);
		Bitmap tBarrell = BitmapFactory.decodeResource(getResources(),
				drawable.tickedbarrel);
		Bitmap three = BitmapFactory.decodeResource(getResources(),
				drawable.three);
		Bitmap two = BitmapFactory.decodeResource(getResources(),
				drawable.two);
		Bitmap one = BitmapFactory.decodeResource(getResources(),
				drawable.one);
		Bitmap go = BitmapFactory.decodeResource(getResources(),
				drawable.go);
		
		public GameView(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
			WindowManager wm = (WindowManager) context
					.getSystemService(Context.WINDOW_SERVICE);
			display = wm.getDefaultDisplay();
			sharedP=context.getSharedPreferences("settings", 0);
			setFocusable(true);
		
		}
		// onDraw function is called repeatedly whenever the code interprets the this.invalidate() function statement.
		// Thus the screen is drawn repeatedly for almost every milli second, which depends upon the processor speed.
		@Override
		protected void onDraw(final Canvas canvas) {
			

			super.onDraw(canvas);
			// Following three lines of code is used to get the width and the height of the screen.
			display.getSize(point);
			height = point.y;
			width=point.x;
			
			if(penaltyEnableCounter>=1000)
			{
				penaltyEnabled=true;
			}
			else
			{
				penaltyEnableCounter++;
			}
			// HashMap collection of pixels for each barrel	
			HashMap<Integer,PixelCoOrdinates> barrellAPixCol=bufferPixel((width*0.25f)-20f,(height*0.2f)-20f,48);
			HashMap<Integer,PixelCoOrdinates> barrellBPixCol=bufferPixel((width*0.25f)-20f,(height*0.6f)-20f,48);
			HashMap<Integer,PixelCoOrdinates> barrellCPixCol=bufferPixel((width*0.6f)-20f,(height*0.4f)-20f,48);
			
			
			//On four sides of the each barrell, put all the pixels which are at a distance of 30 pixels into the SparseArray collections.
			SparseArray<PixelCoOrdinates> leftA=linebufferPixel(20,width*0.25f,height*0.2f,(height*0.2f)+64f);
			SparseArray<PixelCoOrdinates> topA=linebufferPixel(width*0.25f,(width*0.25f)+64f,20,height*0.2f);
			SparseArray<PixelCoOrdinates> rightA=linebufferPixel((width*0.25f)+64f,width-20f,height*0.2f,(height*0.2f)+64f);
			SparseArray<PixelCoOrdinates> bottomA=linebufferPixel(width*0.25f,(width*0.25f)+64f,(height*0.2f)+64f,height*0.6f);
			
			SparseArray<PixelCoOrdinates> leftB=linebufferPixel(20,width*0.25f,height*0.6f,(height*0.6f)+64f);
			SparseArray<PixelCoOrdinates> bottomB=linebufferPixel(width*0.25f,(width*0.25f)+64f,height*0.6f+64f,(height*0.8f)-20f);
			SparseArray<PixelCoOrdinates> rightB=linebufferPixel((width*0.25f)+64f,width-20f,height*0.6f,(height*0.6f)+64f);
			SparseArray<PixelCoOrdinates> topB=linebufferPixel(width*0.25f,(width*0.25f)+64f,(height*0.2f)+64f,height*0.6f);

			SparseArray<PixelCoOrdinates> topC=linebufferPixel(width*0.6f,(width*0.6f)+64f,20,height*0.4f);
			SparseArray<PixelCoOrdinates> bottomC=linebufferPixel(width*0.6f,(width*0.6f)+64f,(height*0.4f)+64f,height-20f);
			SparseArray<PixelCoOrdinates> rightC=linebufferPixel((width*0.6f)+64f,width-20f,height*0.4f,(height*0.4f)+64f);			
			SparseArray<PixelCoOrdinates> leftC=linebufferPixel(20,width*0.6f,height*0.4f,(height*0.4f)+64f);

			// Following lines of code is used to change the horse left and top pixel in the multiples of 30.
			int xPos=(int) rodeoX;
			int yPos=(int) rodeoY;
			if(xPos<30)
			{
				xPos+=30;
			}
			if(yPos<30)
			{
				yPos+=30;
			}
			if(xPos>width)
			{
				xPos-=20;
			}
			if(yPos>=(int)(height*0.4f))
			{
				yPos-=20;
			}			

			xPos=xPos+(30-(xPos%30));
			yPos=yPos+(30-(yPos%30));
			

/*
 * Code Written By  : Rakesh Manoharan
 * Net ID 			: rxm143130
 * Course 			: CS6301
 * Class Name		: GameScreen.java
 * Date				: 11-28-2014
 * Description 		:
 * 		Following lines of code is used to check whether the horse circles each barrell completely or not.
 * Once it circles successfully, it will set the "removeBarrell" boolean variable to true.
 * 		As the pixels on four sides of barrell are collected onto the collections at the distance of 30 pixels. 
 * This code checks the horse left and top pixels to match atleast one of the pixel on the collection, if it matches then the 
 * particular side is marked to be visited by the horse.
 * 		To complete the circle, horse has to visit all the four sides of the barrell and then visit the side where it starts first.
 * Once it visits any side, that value is set to the firstVisited String and when it completes the circle it compares the last visited
 * side with the first visited and if it matches then the barrell is said to be circled with the horse.
 */			
			if(!removeBarrellA)
			{
				if(!visitedA.contains("lA")){
					for(int counter=1;counter<=leftA.size();counter++)
					{
						if(xPos==leftA.get(counter).getX()&&yPos==leftA.get(counter).getY())
						{
							if(!visitedA.contains("lA"))
								visitedA=visitedA.concat("lA");
							break;
						}

					}
				}
				
				if(!visitedA.contains("rA")){
					for(int counter=1;counter<=rightA.size();counter++)
					{
						if(xPos==rightA.get(counter).getX()&&yPos==rightA.get(counter).getY())
						{
							if(!visitedA.contains("rA"))
								visitedA=visitedA.concat("rA");
							break;
						}

					}
				}
				
				if(!visitedA.contains("tA")){
					for(int counter=1;counter<=topA.size();counter++)
					{
						if(xPos==topA.get(counter).getX()&&yPos==topA.get(counter).getY())
						{
							if(!visitedA.contains("tA"))
								visitedA=visitedA.concat("tA");
							break;
						}

					}
				}
				
				if(!visitedA.contains("bA")){
					for(int counter=1;counter<=bottomA.size();counter++)
					{
						if(xPos==bottomA.get(counter).getX()&&yPos==bottomA.get(counter).getY())
						{
							if(!visitedA.contains("bA"))
								visitedA=visitedA.concat("bA");
							break;
						}

					}
				}
			
				if(visitedA.contains("lA")&&visitedA.contains("tA")&&visitedA.contains("rA")&&visitedA.contains("bA"))
				{
					if(firstVisitedA.equals(""))
					{
						firstVisitedA=visitedA.substring(0, 2);
						visitedA=visitedA.replace(firstVisitedA, "");
					}
					else
					{
						if(firstVisitedA.equals(visitedA.substring(visitedA.length()-2, visitedA.length())))
						{
							removeBarrellA=true;
							if(!removeBarrellB){
								visitedB="";
								firstVisitedB="";
							}
							
							if(!removeBarrellC)
							{
								visitedC="";
								firstVisitedC="";
							}
						}
					}
				
				}
			}

			if(!removeBarrellB)
			{	
				if(!visitedB.contains("lB")){
					for(int counter=1;counter<=leftB.size();counter++)
					{
						if(xPos==leftB.get(counter).getX()&&yPos==leftB.get(counter).getY())
						{
							if(!visitedB.contains("lB"))
								visitedB=visitedB.concat("lB");
							break;
						}

					}
				}
				
				if(!visitedB.contains("rB")){
					for(int counter=1;counter<=rightB.size();counter++)
					{
						if(xPos==rightB.get(counter).getX()&&yPos==rightB.get(counter).getY())
						{
							if(!visitedB.contains("rB"))
							visitedB=visitedB.concat("rB");
							break;
						}
					}
				}

				if(!visitedB.contains("tB")){
					for(int counter=1;counter<=topB.size();counter++)
					{
						if(xPos==topB.get(counter).getX()&&yPos==topB.get(counter).getY())
						{
							if(!visitedB.contains("tB"))
								visitedB=visitedB.concat("tB");
							break;
						}
					}
				}
				
				if(!visitedB.contains("bB")){
					for(int counter=1;counter<=bottomB.size();counter++)
					{
						if(xPos==bottomB.get(counter).getX()&&yPos==bottomB.get(counter).getY())
						{
							if(!visitedB.contains("bB"))
								visitedB=visitedB.concat("bB");
							break;
						}
					}				
				}
					
				if(visitedB.contains("lB")&&visitedB.contains("tB")&&visitedB.contains("rB")&&visitedB.contains("bB"))
				{
					if(firstVisitedB.equals(""))
					{
						firstVisitedB=visitedB.substring(0, 2);
						visitedB=visitedB.replace(firstVisitedB, "");
					}
					else
					{
						if(firstVisitedB.equals(visitedB.substring(visitedB.length()-2, visitedB.length())))
						{
							removeBarrellB=true;
							if(!removeBarrellA){
								visitedA="";
								firstVisitedA="";
							}
							
							if(!removeBarrellC)
							{
								visitedC="";
								firstVisitedC="";
							}

						}
					}
				
				}
			}

			if(!removeBarrellC)
			{	
				if(!visitedC.contains("lC")){
					for(int counter=1;counter<=leftC.size();counter++)
					{
						if(xPos==leftC.get(counter).getX()&&yPos==leftC.get(counter).getY())
						{
							if(!visitedC.contains("lC"))
								visitedC=visitedC.concat("lC");
							break;
						}

					}
				}
				
				if(!visitedC.contains("rC")){
					for(int counter=1;counter<=rightC.size();counter++)
					{
						if(xPos==rightC.get(counter).getX()&&yPos==rightC.get(counter).getY())
						{
							if(!visitedC.contains("rC"))
							visitedC=visitedC.concat("rC");
							break;
						}
					}
				}

				if(!visitedC.contains("tC")){
					for(int counter=1;counter<=topC.size();counter++)
					{
						if(xPos==topC.get(counter).getX()&&yPos==topC.get(counter).getY())
						{
							if(!visitedC.contains("tC"))
								visitedC=visitedC.concat("tC");
							break;
						}
					}
				}
				
				if(!visitedC.contains("bC")){
					for(int counter=1;counter<=bottomC.size();counter++)
					{
						if(xPos==bottomC.get(counter).getX()&&yPos==bottomC.get(counter).getY())
						{
							if(!visitedC.contains("bC"))
								visitedC=visitedC.concat("bC");
							break;
						}
					}				
				}
					
				if(visitedC.contains("lC")&&visitedC.contains("tC")&&visitedC.contains("rC")&&visitedC.contains("bC"))
				{
					if(firstVisitedC.equals(""))
					{
						firstVisitedC=visitedC.substring(0, 2);
						visitedC=visitedC.replace(firstVisitedC, "");
					}
					else
					{
						if(firstVisitedC.equals(visitedC.substring(visitedC.length()-2, visitedC.length())))
						{
							removeBarrellC=true;
							if(!removeBarrellB){
								visitedB="";
								firstVisitedB="";
							}
							
							if(!removeBarrellA)
							{
								visitedA="";
								firstVisitedA="";
							}

						}
					}
				
				}
			}

			Rect rectTimer=new Rect();
			int rectwidth=(Integer)Math.round(width);
			int rectheight=(Integer)Math.round(height);
			rectTimer.set(0, 0, rectwidth, rectheight);
			canvas.drawBitmap(timer,  rectTimer,rectTimer,  null);
			Rect rect=new Rect();
			int iwidth=(Integer)Math.round(width);
			int iheight=(Integer)Math.round(height*0.8f);
			rect.set(0, 0, iwidth, iheight);
			
			canvas.drawBitmap(stadiumGround, rect,rect,null);
			Paint stadiumPaint = new Paint();
			
			stadiumPaint.setAntiAlias(true);
			stadiumPaint.setColor(Color.rgb(102, 51, 0));
			stadiumPaint.setStyle(Style.STROKE);
			stadiumPaint.setStrokeWidth(20f);

			
			Paint exitGate=new Paint();
			exitGate.setAntiAlias(true);
			exitGate.setColor(Color.WHITE);
			exitGate.setStyle(Style.STROKE);
			exitGate.setStrokeWidth(5);
			exitGate.setTextSize(28);
			
			Paint timerPaint=new Paint();
			timerPaint.setAntiAlias(true);
			timerPaint.setColor(Color.rgb(170, 63, 0));
			timerPaint.setStyle(Style.STROKE);
			timerPaint.setStrokeWidth(6);
			timerPaint.setTextSize(56);
			
			Paint timerPaintSec=new Paint();
			timerPaintSec.setAntiAlias(true);
			timerPaintSec.setColor(Color.YELLOW);
			timerPaintSec.setStyle(Style.STROKE);
			timerPaintSec.setStrokeWidth(6);
			timerPaintSec.setTextSize(56);
			
			Paint exitGateText=new Paint();
			exitGateText.setAntiAlias(true);
			exitGateText.setColor(Color.WHITE);
			//exitGateText.setStyle(Style.STROKE);
			exitGateText.setStrokeWidth(1);
			exitGateText.setTextSize(35);
			
			Paint paintA = new Paint();
			paintA.setAntiAlias(true);
			paintA.setColor(Color.rgb(0, 128, 128));
			paintA.setStyle(Style.STROKE);
			paintA.setStrokeWidth(20.0f);

			if(removeBarrellA&&removeBarrellB&&removeBarrellC)
			{	
				canvas.drawText("Exit Here -->", width-350, 60, exitGateText);
				canvas.drawCircle(width, 0, 120, exitGate);
				if(rodeoX<=width&&rodeoX>=width-170&&rodeoY<=70&&rodeoY>=0)
				{
					Context cx = getContext();
					Context mC=getContext();
					Intent mainClass = new Intent(mC, MainActivity.class);
					mainClass.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
					mC.startActivity(mainClass);
					Intent n = new Intent(cx, GameOver.class);

					SharedPreferences.Editor sendScores = sharedP.edit();
					sendScores.putLong("current_score", diff);
					sendScores.commit();
					cx.startActivity(n);
					android.os.Process.killProcess(android.os.Process.myPid());					
				}
			}

			if(!removeBarrellA)
			{
				canvas.drawBitmap(barrel1,  width*0.25f,height*0.2f,  null);
			}
			else
			{
				canvas.drawBitmap(tBarrell,  width*0.25f,height*0.2f,  null);
			}
			if(!removeBarrellB)
			{
			canvas.drawBitmap(barrel1,  width*0.25f,height*0.6f,  null);
			}
			else
			{
				canvas.drawBitmap(tBarrell,  width*0.25f,height*0.6f,  null);
			}
			if(!removeBarrellC)
			{			
			canvas.drawBitmap(barrel1,  width*0.6f,height*0.4f,  null);
			}
			else
			{
				canvas.drawBitmap(tBarrell,  width*0.6f,height*0.4f,  null);
			}			   
			   /*
			   * Code Written By  	: Pavan Trinath
			   * Net ID 			: BXV131230
			   * Course 			: CS6301
			   * Function Name		: bufferPixel
			   * Date				: 11-27-2014
			   * Description 		:
			   *	Following lines of code is used to implement the game logic of horse touching the barrell, thats when the
			   *game should end with the game over message.
			   *	rodeoPixCol is the collection of all the pixels at the border of the horse. Pixels are put into the
			   *collection at the distance of 20 pixels.
			   *	Barrells border pixels also noted on the barrellAPixCol collection.
			   *	Every time the loop is used to check does any of the pixel matches on both collection. If it matches then
			   *the barrell is considered to be touched by the horse. So the game ends and goes to the game over screen.
			   */

			   
			HashMap<Integer,PixelCoOrdinates> rodeoPixCol=bufferPixel(rodeoX-20f,rodeoY-20f,96);
						
			for(int rodeoCounter=1;rodeoCounter<=16;rodeoCounter++)
			{
				for(int barCounter=1;barCounter<=8;barCounter++)
				{
					
					if((rodeoPixCol.get(rodeoCounter).getX()==barrellAPixCol.get(barCounter).getX()&&rodeoPixCol.get(rodeoCounter).getY()==barrellAPixCol.get(barCounter).getY()))
					{
						vibrate.vibrate(100);
						Context mC=getContext();
						Intent mainClass = new Intent(mC, MainActivity.class);
						mainClass.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
						mC.startActivity(mainClass);
						Context cx = getContext();
						Intent n = new Intent(cx, GameOver.class);

						SharedPreferences.Editor sendScores = sharedP.edit();
						sendScores.putLong("current_score", 0);
						sendScores.commit();
						cx.startActivity(n);
						android.os.Process.killProcess(android.os.Process.myPid());
						break;
					}
					
					if((rodeoPixCol.get(rodeoCounter).getX()==barrellBPixCol.get(barCounter).getX()&&rodeoPixCol.get(rodeoCounter).getY()==barrellBPixCol.get(barCounter).getY()))
					{
						vibrate.vibrate(100);
						Context mC=getContext();
						Intent mainClass = new Intent(mC, MainActivity.class);
						mainClass.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
						mC.startActivity(mainClass);
						Context cx = getContext();
						Intent n = new Intent(cx, GameOver.class);

						SharedPreferences.Editor sendScores = sharedP.edit();
						sendScores.putLong("current_score", 0);
						sendScores.commit();
						cx.startActivity(n);
						android.os.Process.killProcess(android.os.Process.myPid());						
						break;
						
					}
					if((rodeoPixCol.get(rodeoCounter).getX()==barrellCPixCol.get(barCounter).getX()&&rodeoPixCol.get(rodeoCounter).getY()==barrellCPixCol.get(barCounter).getY()))
					{
						vibrate.vibrate(100);
						Context mC=getContext();
						Intent mainClass = new Intent(mC, MainActivity.class);
						mainClass.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
						mC.startActivity(mainClass);
						Context cx = getContext();
						SharedPreferences.Editor sendScores = sharedP.edit();
						sendScores.putLong("current_score", 0);
						sendScores.commit();
						Intent n = new Intent(cx, GameOver.class);
						
						cx.startActivity(n);
						android.os.Process.killProcess(android.os.Process.myPid());						
						break;
					}
					
					
				}
			}

			if(onTouched)
			{
			
/*				MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.AudioFile1);
				mp.start();*/
				startDate=new Date();
				if(startTime==0&&pauseValue!=0){
					penalty=penalty+pauseValue;
					pauseValue=0;
				}
				if(waitGame==true&& waitGameCounter<=60&&waitGameCounter>0&&!backPressed)
						canvas.drawBitmap(three,  0,0,  null);
				if(waitGame==true&& waitGameCounter<=120&&waitGameCounter>60&&!backPressed)
					canvas.drawBitmap(two,  0,0,  null);
				if(waitGame==true&& waitGameCounter<=180&&waitGameCounter>120&&!backPressed)
					canvas.drawBitmap(one,  0,0,  null);
				if(waitGame==true&& waitGameCounter<=240&&waitGameCounter>180&&!backPressed)
					canvas.drawBitmap(go,  0,0,  null);
				if(!backPressed)
				waitGameCounter++;
					
				
				if(waitGameCounter>240)
				{	waitGameCounter=0;
					waitGame=false;
				}
				
				canvas.drawBitmap(rodeo, rodeoX,rodeoY,  null);
				if(!waitGame)
				{
				
				if(startTime==0)
				startTime=startDate.getTime();
				endDate=new Date();
				endTime=endDate.getTime();
				
				if(warningCounter<=65&&warningCounter>0)
				canvas.drawText("+ 5 Sec", width*0.7f, (height*0.8f)+110f-warningCounter, timerPaintSec);
				warningCounter++;
				
				if(warningCounter>=1000)
					warningCounter=66;
				}
				diff=(endTime-startTime)+penalty;
				long diffMilliSecs=(diff%1000)/10;
				long diffSeconds = diff / 1000 % 60;
				long diffMinutes = diff / (60 * 1000) % 60;
        		String milliSec=Long.toString(diffMilliSecs);
        		String diffSec=Long.toString(diffSeconds);
        		String min=Long.toString(diffMinutes);
        		if(milliSec.length()<2)
        		{
        			milliSec="0".concat(milliSec);
        		}
        		if(diffSec.length()<2)
        		{
        			diffSec="0".concat(diffSec);
        		}
        		if(min.length()<2)
        		{
        			min="0".concat(min);
        		}
        		
        		
        		String Timer="Time : ".concat(min).concat(" : ").concat(diffSec).concat(" : ").concat(milliSec).concat("         ");


				canvas.drawText(Timer, width*0.08f, (height*0.8f)+110f, timerPaint);

				this.invalidate();
			
			}
			else
			{
				canvas.drawBitmap(rodeo, width-110, 15, null);
				canvas.drawCircle(width, 0, 140, exitGate);
				canvas.drawBitmap(play,  rectTimer,rectTimer,  null);
				
			}
			canvas.drawRect(0, 0, width, height*4/5, stadiumPaint);				
			this.invalidate();
		}    	
		
		// On touching the screen, the boolean values of onTouched is set to true indicating the game starts.
		// The horse will not move till this value is set to true.

		public boolean onTouchEvent(MotionEvent event) {
			// TODO Auto-generated method stub
			if (event.getAction() == MotionEvent.ACTION_DOWN)
			{
				onTouched = true;
			}
			return super.onTouchEvent(event);
		}
		

/*
 * Code Written By  : Rakesh Manoharan
 * Net ID 			: rxm143130
 * Course 			: CS6301
 * Function Name	: linebufferPixel
 * Date				: 11-28-2014
 * Description 		:
 * 		linebufferPixel function is used to collect all the pixels divisible by 30 in the rectange to the collection.
 * Argument passed should be the four sides of the rectangle left, right, top and bottom.
 */			
	
	public SparseArray<PixelCoOrdinates> linebufferPixel(float leftX,float rightX, float topY, float bottomY)
	{
		int lx=(int) leftX;
		int rx=(int) rightX;
		int ty=(int) topY;
		int by=(int) bottomY;
		lx=lx+(30-(lx%30));
		rx=rx-(rx%30);
		ty=ty+(30-ty%30);
		by=by-(by%30);
		int xwidth=(rx-lx)/30;
		int ywidth=(by-ty)/30;
		SparseArray<PixelCoOrdinates> pixCol=new SparseArray<PixelCoOrdinates>(xwidth*ywidth);
		
		for(int x=0;x<xwidth*ywidth;x+=ywidth,lx=lx+30)
		{
			int yt=ty;
			for(int y=1;y<=ywidth;y++,yt+=30)
			{
				PixelCoOrdinates pixPos=new PixelCoOrdinates();
				pixPos.setX(lx);
				pixPos.setY(yt);
				pixCol.put(x+y, pixPos);
			}
			
		}
				
		return pixCol;
		
	}
	   /*
	   * Code Written By  	: Pavan Trinath
	   * Net ID 			: BXV131230
	   * Course 			: CS6301
	   * Function Name		: bufferPixel
	   * Date				: 11-27-2014
	   * Description 		:
	   *	bufferPixel is used to collect all the pixels at the border of the image which is divisible by 30.
	   * Arguments passed are the X & Y position of the top left most pixel and the size of the image.
	   *
	   */
		
    public HashMap<Integer,PixelCoOrdinates> bufferPixel(float PosX,float PosY,int size)
    {
    	int xPos=(int) PosX;
    	int yPos=(int) PosY;
    	int rem=xPos%20;
    	int remY=yPos%20;
    	xPos=xPos+(20-rem);
    	yPos=yPos+(20-remY);
    	int imageSize=size-(size%20);
		HashMap<Integer,PixelCoOrdinates> rodeoPixCol=new HashMap<Integer,PixelCoOrdinates>();
		
		float topleftX=xPos;
		float topleftY=yPos;
		float toprightX=xPos+imageSize;
		float toprightY=yPos;
		float botleftX=xPos;
		float botleftY=yPos+imageSize;
		
		int xNumPix=(int)(toprightX-topleftX)/20;

		float change;
		change=topleftY;
		//Copy left border pixels into the collection
		for(int x=1;x<=xNumPix;x++)
		{
			PixelCoOrdinates pixPos=new PixelCoOrdinates();
			pixPos.setX(topleftX);
			pixPos.setY(change);
			rodeoPixCol.put(x, pixPos);
			change=change+20;
		}
		change=topleftX;
		//Copy top border pixels into the collection
		for(int x=xNumPix+1;x<=xNumPix*2;x++)
		{
			PixelCoOrdinates pixPos=new PixelCoOrdinates();
			pixPos.setX(change);
			pixPos.setY(topleftY);
			rodeoPixCol.put(x, pixPos);
			change=change+20;
		}
		//Copy right border pixels into the collection
		change=toprightY;
		for(int x=(xNumPix*2)+1;x<=xNumPix*3;x++)
		{
			PixelCoOrdinates pixPos=new PixelCoOrdinates();
			pixPos.setX(toprightX);
			pixPos.setY(change);
			rodeoPixCol.put(x, pixPos);
			change=change+20;
		}
		//Copy bottom border pixels into the collection
		change=botleftX;
		for(int x=(xNumPix*3)+1;x<=xNumPix*4;x++)
		{
			PixelCoOrdinates pixPos=new PixelCoOrdinates();
			pixPos.setX(change);
			pixPos.setY(botleftY);
			rodeoPixCol.put(x, pixPos);
			change=change+20;
		}
		return rodeoPixCol;
    }
    }
    
	@Override
	   /*
	   * Code Written By  	: Pavan Trinath
	   * Net ID 			: BXV131230
	   * Course 			: CS6301
	   * Function Name		: bufferPixel
	   * Date				: 11-27-2014
	   * function 			: onSensorChanged(SensorEvent event) :
	   * Description 		:	  
	   * created to control the position and motion of the rodeo with the sensor readings from accelerometer, each condition points to a direction.
	   * */
	 
	public void onSensorChanged(SensorEvent event) {
		
		float xAxis=event.values[0];
		float yAxis=event.values[1];

		if(onTouched&&!waitGame)
		{
		// TODO Auto-generated method stub
		// Moving South 
		if (xAxis <= 1 && xAxis>=-1 && yAxis >= 1 && rodeoX < width && rodeoX>0 && rodeoY > 0 && rodeoY <  height*0.8f-96) {
			rodeoY += accelerometer_reading;
		}
		//  Moving North 
		else if (xAxis <= 1 && xAxis>=-1 && yAxis <0 && rodeoX < width && rodeoX>0 && rodeoY > 0 && rodeoY <  height*0.8f) {
			rodeoY -= accelerometer_reading;
		}
		// Moving East 
		else if (yAxis <= 1 && yAxis>=-1 && xAxis <0 && rodeoX < width-96 && rodeoX>0 && rodeoY > 0 && rodeoY < height*0.8f) {
			rodeoX += accelerometer_reading;
		}
		// Moving West 
		else if (yAxis <= 1 && yAxis>=-1 && xAxis >1 && rodeoX < width && rodeoX>0 && rodeoY > 0 && rodeoY < height*0.8f) {
			rodeoX -= accelerometer_reading;
		}

		// Moving NorthEast 
		else if (yAxis <0  && xAxis <0 && rodeoX < width-96 && rodeoX>0 && rodeoY > 0 && rodeoY < height*0.8f) {
			rodeoX += accelerometer_reading;
			rodeoY -= accelerometer_reading;	
		}

		//  Moving NorthWest 
		else if (xAxis >1 && yAxis <0 && rodeoX < width && rodeoX>0 && rodeoY > 0 && rodeoY < height*0.8f) {
			rodeoY -= accelerometer_reading;
			rodeoX -= accelerometer_reading;
		}
		// Moving SouthWest 
		else if (xAxis >1 && yAxis >= 1 && rodeoX < width && rodeoX>0 && rodeoY > 0 && rodeoY < height*0.8f-96) {
			rodeoY += accelerometer_reading;
			rodeoX -= accelerometer_reading;
		}
		// Moving SouthEast 
		else if (yAxis >= 1 && xAxis <0 && rodeoX < width-96 && rodeoX>0 && rodeoY > 0 && rodeoY < height*0.8f-96) {
			rodeoX += accelerometer_reading;
			rodeoY += accelerometer_reading;
		}
		else
		{
		
			if(xAxis>=0 && xAxis<=1 && rodeoX < width-96 && rodeoX>0 && rodeoY > 0 && rodeoY < height*0.8f)
			{
				rodeoX-=accelerometer_reading;
			}
			else if(yAxis<=1 && yAxis>=0 && rodeoX < width && rodeoX>0 && rodeoY > 0 && rodeoY <  height*0.8f-96)
			{
				rodeoY += accelerometer_reading;			
			}
		}
			// Move 40 paces back of the border
			if(rodeoY>=height*0.8f-96f)
			{
				rodeoY-=40;
				rodeoY-=accelerometer_reading;
				penalty=penalty+5000;
				vibrate.vibrate(10);
				warningCounter=1;
			}
			// Move 40 paces back of the border	
			else if(rodeoY<=0)
			{
				rodeoY+=40;
				rodeoY+=accelerometer_reading;
				penalty=penalty+5000;
				vibrate.vibrate(10);
				warningCounter=1;
			}
			// Move 40 paces back of the border
			else if(rodeoX<=0)
			{
				rodeoX+=40;
				rodeoY+=accelerometer_reading;
				penalty=penalty+5000;
				vibrate.vibrate(10);
				warningCounter=1;
			}
			// Move 40 paces back of the border
			else if(rodeoX>=width-96f)
			{
				rodeoX-=40;
				rodeoX-=accelerometer_reading;
				penalty=penalty+5000;
				vibrate.vibrate(10);
				warningCounter=1;
			}
		}	


	}
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
	
	/*
	 * Code Written By  : Rakesh Manoharan
	 * Net ID 			: rxm143130
	 * Course 			: CS6301
	 * Function Name	: onPause() & onResume()
	 * Date				: 11-29-2014
	 * Description 		:
	 * 			
	 */	
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		sensorManager.unregisterListener(this);
		pauseTime=(endTime-startTime)+penalty;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(penaltyEnabled&&pauseTime==0){
		penalty=penalty+5000;
		warningCounter=1;
		}
		if(pauseTime!=0)
		{
			pauseValue=pauseTime;
			pauseTime=0;
			startTime=0;
			endTime=0;
			penalty=0;
			//warningCounter=1;
			waitGameCounter=1;
			waitGame=true;
		}
		sensorManager.registerListener(this, accelerometerSensor,
				SensorManager.SENSOR_DELAY_NORMAL);
		
	}
	   /*
	   * Code Written By  	: Pavan Trinath
	   * Net ID 			: BXV131230
	   * Course 			: CS6301
	   * Date				: 11-27-2014
	   * function 			: onBackPressed()
	   * Description 		:	  
	   * 				verify what to do when the user presses the back button of the device
	   * */

	
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		// super.onBackPressed();
		waitGame=true;
		backPressed=true;
		pauseTime=(endTime-startTime)+penalty;
		AlertDialog.Builder exitAlert = new AlertDialog.Builder(
				GameScreen.this);
		exitAlert.setMessage("Back to home screen?");
		exitAlert.setCancelable(true);
		exitAlert.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						android.os.Process.killProcess(android.os.Process.myPid());
						
					}
				});
		exitAlert.setNegativeButton("No",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
						waitGameCounter=1;
						backPressed=false;
						if(pauseTime!=0)
						{
							pauseValue=pauseTime;
							pauseTime=0;
							startTime=0;
							endTime=0;
							penalty=0;
							//warningCounter=1;
							waitGameCounter=1;
							waitGame=true;
						}

					}
				});
		 exitAlert.create();
		exitAlert.show();
		


	}
}
