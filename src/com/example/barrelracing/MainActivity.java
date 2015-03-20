package com.example.barrelracing;



import java.util.Map;
import java.util.TreeMap;

import com.example.fileIO.FileOperations;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;


/*
 * Code Written By  : Rakesh Manoharan
 * Net ID 			: rxm143130
 * Course 			: CS6301
 * Class Name		: MainActivity.java
 * Date				: 11-26-2014
 * Description 		:
 * 					MainActivity is the class that extends activity, which is used to 
 * control the activity. activity_main.xml is linked to this class and all the logics to be
 * implemented on the activity is handled here.
 * 
 * 		Through this activity, I have implemented the home screen of the game where the user is 
 * allowed to start a new game and do some configuration changes like player name and speed control
 * of the horse.
 */


public class MainActivity extends Activity implements OnClickListener {
TextView speedControl=null;
EditText editTextName;
SharedPreferences.Editor editor;
ImageView startImage,helpImg;
//OnCreate Method is called at the time of activity creation
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //SharedPreferences is used to store some values unique across independent activities.
     
        SharedPreferences sharedPref = getSharedPreferences("settings", 0);
        sharedPref.edit();
        editor = sharedPref.edit();
        editor.putFloat("accelarator_reading", 8);
        
        startImage=(ImageView)findViewById(R.id.startImageButton);
        helpImg=(ImageView)findViewById(R.id.hbt);
        
        editTextName=(EditText)findViewById(R.id.linlayout);
        speedControl=(TextView)findViewById(R.id.textViewRiderSpeed);
        
        SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);
        // Following if condition helps us to retain the name of the player when the user hits on the play again button on the game_over.xml screen 
        if(sharedPref.getBoolean("NameRetain", false)==true && !sharedPref.getString("name", "Rider").equals("Rider"))
        {
        	editTextName.setText(sharedPref.getString("name", "Rider"));    
        	editor.putBoolean("NameRetain", false);
        	editor.commit();
        }
        

        TreeMap<Long,String> hScoreDetails=new TreeMap<Long,String>();
        FileOperations file=new FileOperations();
        hScoreDetails=file.read();
        file.write(hScoreDetails);
        // Following two if conditions is used to initiate the current_score and name for the first time 
        //when the application runs on any phone
        if(!sharedPref.contains("current_score"))
        {
        	editor.putLong("current_score", 0);
        	editor.commit();
        }

        if(!sharedPref.contains("name"))
        {
        	editor.putString("name", "Rider");
        	editor.commit();
        }
        
        View startImageButton=(ImageButton)findViewById(R.id.startImageButton);
        startImageButton.setOnClickListener(this);
        View helpButton=(ImageButton)findViewById(R.id.hbt);
        helpButton.setOnClickListener(this);        
        /*
         * SeekBar has been drawn on the home screen to give the user control for the speed of the horse to move.
         * I have implemented 3 controls basically which are Slow, Medium and Fast.
         * Based on the selection accelarator_reading is set to 8 or 12 or 16 respectively.
         * This accelarator_reading helps the horse to exactly jump the number of pixels assigned to it 
         * on redrawing the screen during the game. Thus giving an illusion of different speed.
         */
        seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			

			@Override
			public void onProgressChanged(SeekBar seekBar, int progresValue,
					boolean fromUser) {
				
				if(progresValue==0)
				{
					speedControl.setText("Rider Speed : Slow");
					editor.putFloat("accelarator_reading", 8);
					editor.commit();
				}
				else if(progresValue==1)
				{
					speedControl.setText("Rider Speed : Medium");
					editor.putFloat("accelarator_reading", 12);
					editor.commit();
				}
				else if(progresValue==2)
				{
					speedControl.setText("Rider Speed : High");
					editor.putFloat("accelarator_reading", 16);
					editor.commit();
				}
				
				
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// Do something here, if you want to do anything at the start of
				// touching the seekbar
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// Display the value in textview
			
			}
		});

    }

    // onCreateOptionsMenu is used to define the controls on the action bar of the application.
    // In our program, we are having one control to show the high scores.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
      
        return true;
    }
    // onOptionsItemSelected is used to handle the event generated by clicking on the controls specified on the action bar.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.highscoreslayout) {
        	Intent i=new Intent(this,HighScoreList.class);
        	startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //onClick is the method to control clicks on the buttons designed on the home page.

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId()==R.id.startImageButton)
		{
			startImage.setImageResource(R.drawable.changedplay);
			// Creates an intent for the Game Screen class, which is game page.
			Intent i=new Intent(this,GameScreen.class);
    		if(editTextName.getText().toString().trim().equals("Rider Name")||editTextName.getText().toString().trim().equals(""))
    		{
    		editor.putString("name", "Rider");
    		editor.commit();
    		}
    		else
    		{
    			editor.putString("name", editTextName.getText().toString());
    			editor.putLong("current_score", 0);
    			editor.commit();
    		}
    		//Game Page is launched through the following startActivity() method. Argument passed is an intent created.
    		startActivity(i);    		
		}
		if(v.getId()==R.id.hbt)
		{
			helpImg.setImageResource(R.drawable.changedhelp);
			AlertDialog.Builder exitAlert = new AlertDialog.Builder(
					MainActivity.this);
			exitAlert.setMessage("* Circle each barrell completely\n* Don't hit the barrell\n* Hitting Fence adds +5S penalty\n* Exit through the gate to finish");
			exitAlert.setCancelable(true);
			exitAlert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {

							helpImg.setImageResource(R.drawable.helpbt);
							
						}
					});
			exitAlert.create();
			exitAlert.show();
		}
		
	}
	
	protected void onResume(){
		super.onResume();
		startImage.setImageResource(R.drawable.playbt);
	}
	
	//verify what to do when the user presses the back button of the device
	// In our program it pops up the message "Are you sure want to exit the game?" with two options Yes and No
	// On yes, the program exits smoothly by changing all its value to default.
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		// super.onBackPressed();

		AlertDialog.Builder exitAlert = new AlertDialog.Builder(
				MainActivity.this);
		exitAlert.setMessage("Are you sure want to exit the game?");
		exitAlert.setCancelable(true);
		exitAlert.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						editor.putBoolean("NameRetain", false);
						editor.commit();
						MainActivity.this.finish();
					}
				});
		exitAlert.setNegativeButton("No",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		 exitAlert.create();
		exitAlert.show();

	}
}
