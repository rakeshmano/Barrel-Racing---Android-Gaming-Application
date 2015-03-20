package com.example.barrelracing;

import java.util.Map;
import java.util.TreeMap;

import com.example.fileIO.FileOperations;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


/*
* Code Written By  	: Pavan Trinath
* Net ID 			: BXV131230
* Course 			: CS6301
* Class Name		: GameOver.java
* Date				: 12-30-2014
* Description 		:if the current score is 0, then it says gameover, when the current score is greater than 0, and less than high score, 
                            it shows well done,nd displays the current score and high score.if the current score is the high score, the new high score image is called.
                            the score is calculated using the shared preferences. A play again button is placed on
                                every condition.				
* 		
*/


public class GameOver extends Activity implements OnClickListener {

	SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_over);
        
        ImageView gameOver=(ImageView)findViewById(R.id.imageView1);
        
        TextView your_score=(TextView)findViewById(R.id.textview01);
        TextView your_score_name=(TextView)findViewById(R.id.textViewRiderSpeed);
        SharedPreferences sharedPref = getSharedPreferences("settings", 0);
        long score=sharedPref.getLong("current_score", 0);
        String playerName=sharedPref.getString("name", "Rider");
        String scoreText=Long.toString(score);
        //check to see if the game is incomplete
        if(score==0){
        	gameOver.setImageResource(R.drawable.gameover);
        	your_score.setText("----------");
        	your_score_name.setText(playerName);
        }
        else{
        long diffMilliSecs=(score%1000)/10;
		long diffSeconds = score / 1000 % 60;
		long diffMinutes = score / (60 * 1000) % 60;
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



		your_score_name.setText(playerName);
        your_score.setText(Timer);
        gameOver.setImageResource(R.drawable.done);
        }
        View startImageButton=(ImageButton)findViewById(R.id.playagain);
        startImageButton.setOnClickListener(this);
        
        TreeMap<Long,String> hScoreDetails=new TreeMap<Long,String>();
        FileOperations file=new FileOperations();
        hScoreDetails=file.read();

        

        //checking to see if the game is completed, and if it is high score. If yes well done, if its high score, displaying new high score.
        if(sharedPref.getLong("current_score", 0)!=0)
        {
        	hScoreDetails.put(sharedPref.getLong("current_score", 0), sharedPref.getString("name", "Rider"));
        }
        long diff=0;
        int counter=1;
        String highScoreName="";
        for(Map.Entry<Long, String> Iterator:hScoreDetails.entrySet())
        {
        	if(counter>1)
        	{
        		break;
        	}
        	if(Iterator.getKey()!=null)
        	{
        		counter++;
        		diff=Iterator.getKey();
        		highScoreName=Iterator.getValue();
           	}
		
        }

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

		if(score==diff)
		{
			gameOver.setImageResource(R.drawable.newhighscore);
		}
        TextView high_score=(TextView)findViewById(R.id.textview02);
        TextView high_score_name=(TextView)findViewById(R.id.textView2);
        high_score.setText(Timer);
        high_score_name.setText("".concat(highScoreName));
        file.write(hScoreDetails);
        sharedPref.edit();
        editor = sharedPref.edit();
        editor.putLong("current_score", 0);
        editor.putBoolean("NameRetain", true);
        editor.commit();
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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		if(v.getId()==R.id.playagain)
		{
			Intent i=new Intent(this,MainActivity.class);
    		
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
    		startActivity(i);    		
    		
    		this.finish();
		}
		
	}
}
