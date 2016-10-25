package fi.soulplayer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore.Audio;
import android.provider.MediaStore.Audio.Media;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;





public class SoulPlayerActivity extends Activity {
    
	private MediaPlayer mediaPlayer =  new MediaPlayer();
	
	private static final int MENU_MUSIC = 1;
	private static final int MENU_VIDEO = 2;
	private static final int MENU_QUIT = 3;
	
	//################
	private static final int SHOW_SOULPLAYER_ACTIVITY = 1;
	private int currentPosition = 0;	//msec from song start
    private int songPosition = 0;		//song place in playlist
    private String[] playlist;		
	private String currentSongPath = "";//Playing song path
	private boolean playing = false;	//If playing while leaving
    
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        playlist = new String[1];
        playlist[0] = "";
    }    
    
    
    ///####################################
    
    
    
    
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	switch(requestCode) {
    		case SHOW_SOULPLAYER_ACTIVITY:
    			if (resultCode == Activity.RESULT_OK) {
	    			Bundle extras = data.getExtras();
	    			currentSongPath = extras.getString("currentSongPath");
	    			currentPosition = extras.getInt("currentPosition");
	    			songPosition = extras.getInt("songPosition");
	    			playing = extras.getBoolean("playing", playing);
	    			playlist = extras.getStringArray("playlist");
	    			
	    			
	    			if (playing){
	    				playSong(currentSongPath);
	    			}	    			
	    			
    			}
    			break;
    	}
    	

    }
    
    

    ///END#################################
    
    private void playPlaylist( int index ){
    	
    	if ( playlist.length > index && index > -1 ){
			playSong(playlist[index]);
		}
    }

    
    private void playSong(String songPath) {
    	currentSongPath = songPath;
    	
    	if (songPath.length() > 2){
	        try {
	 
	        	//Display file name (without ".mp3")
	        	File file = new File (songPath);
	        	Toast.makeText(getBaseContext(), file.getName().replaceAll(".mp3", ""), Toast.LENGTH_SHORT).show();
	        	
	        	mediaPlayer.reset();
	        	mediaPlayer.setDataSource(songPath);
	        	mediaPlayer.prepare();
	        	mediaPlayer.seekTo(currentPosition);
	        	mediaPlayer.start();
	 
	        // Setup listener so next song starts automatically
	        	mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
	        		public void onCompletion(MediaPlayer arg0) {
	        			nextSong();
	                }
	            });
	        } catch (IOException e) {
	                Log.v(getString(R.string.app_name), e.getMessage());
	        }
        
	        Button play = (Button) findViewById(R.id.songPlay);
			play.setText("Pause");
    	}
    }

    
    private void nextSong() {
    	currentPosition = 0;
    	
        if (++songPosition >=  playlist.length ) {
            // Last song, just reset currentPosition
    		songPosition = 0;
    		playPlaylist( songPosition );
    		
        } else {
            // Play next song
        	playPlaylist( songPosition );
        }
	        
    }

    private void previousSong() {
    	currentPosition = 0;
    	
	    if ( songPosition == 0 ) {
	        // First song, jump to last position
			songPosition = playlist.length -1;
			playPlaylist( songPosition );
			
	    } else {
	    	// Play previous song
	    	--songPosition;
	    	playPlaylist( songPosition );
	    }
		    
    }
    
    
    
    

    /** Start playing audio from SC Card */
    public void startAudio(View view) {
    	
    	if ( mediaPlayer.isPlaying() ){
    		pauseAudio(view);
    		Button play = (Button) findViewById(R.id.songPlay);
    		play.setText("Play");
    	}else{
			playPlaylist( songPosition );
    		
    	}
    }   
    
    
    /** Pause playing audio */
    public void pauseAudio(View view) {
    	
    	if (mediaPlayer != null){
    		currentPosition = mediaPlayer.getCurrentPosition();
    		mediaPlayer.pause();
       	}
    }

    /** Stop playing audio */
    public void stopAudio(View view) {
    	
    	if (mediaPlayer != null){
    		currentPosition = 0;
    		mediaPlayer.stop();
    		Button play = (Button) findViewById(R.id.songPlay);
    		if (playlist.length > 0){
    			play.setText("Play");
    		}
    		
    	}
    }
    
    /** Activity is destroyed, stop audio if playing */
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	if (mediaPlayer != null) mediaPlayer.release();
    }
    
    public void songBack (View view){
    	previousSong();
    }
    
    public void songNext (View view){
    	nextSong();
    }
    
    public void startSongs (View view){
    	if ( mediaPlayer.isPlaying() ){
    		currentPosition = mediaPlayer.getCurrentPosition();
    		mediaPlayer.reset();
    		playing = true;
    	}else{
    		playing = false;
    	}
    	
    	final Intent intent = new Intent(SoulPlayerActivity.this, SongsActivity.class);
    	intent.putExtra("currentSongPath", currentSongPath);
    	intent.putExtra("currentPosition", currentPosition);
    	intent.putExtra("songPosition", songPosition);
    	intent.putExtra("playing", playing);
    	intent.putExtra("playlist", playlist );
    	startActivityForResult(intent,SHOW_SOULPLAYER_ACTIVITY);
    	
    }
    
    public void startArtists (View view){
    	if ( mediaPlayer.isPlaying() ){
    		currentPosition = mediaPlayer.getCurrentPosition();
    		mediaPlayer.reset();
    		playing = true;
    	}else{
    		playing = false;
    	}
    	
    	final Intent intent = new Intent(SoulPlayerActivity.this, ArtistsActivity.class);
    	intent.putExtra("currentSongPath", currentSongPath);
    	intent.putExtra("currentPosition", currentPosition);
    	intent.putExtra("songPosition", songPosition);
    	intent.putExtra("playing", playing);
    	intent.putExtra("playlist", playlist );
    	startActivityForResult(intent,SHOW_SOULPLAYER_ACTIVITY);
    }
    
    public void startAlbums (View view){
    	if ( mediaPlayer.isPlaying() ){
    		currentPosition = mediaPlayer.getCurrentPosition();
    		mediaPlayer.reset();
    		playing = true;
    	}else{
    		playing = false;
    	}
    	
    	final Intent intent = new Intent(SoulPlayerActivity.this, AlbumsActivity.class);
    	intent.putExtra("currentSongPath", currentSongPath);
    	intent.putExtra("currentPosition", currentPosition);
    	intent.putExtra("songPosition", songPosition);
    	intent.putExtra("playing", playing);
    	intent.putExtra("playlist", playlist );
    	startActivityForResult(intent,SHOW_SOULPLAYER_ACTIVITY);
    }
    
    public void startGenres (View view){
    	if ( mediaPlayer.isPlaying() ){
    		currentPosition = mediaPlayer.getCurrentPosition();
    		mediaPlayer.reset();
    		playing = true;
    	}else{
    		playing = false;
    	}
    	
    	final Intent intent = new Intent(SoulPlayerActivity.this, GenresActivity.class);
    	intent.putExtra("currentSongPath", currentSongPath);
    	intent.putExtra("currentPosition", currentPosition);
    	intent.putExtra("songPosition", songPosition);
    	intent.putExtra("playing", playing);
    	intent.putExtra("playlist", playlist );
    	startActivityForResult(intent,SHOW_SOULPLAYER_ACTIVITY);
    }
    
    public void startSearch (View view){
    	Toast.makeText(getBaseContext(), "Search", Toast.LENGTH_SHORT).show();
    }
    
    public void startNetRadio (View view){
    	if ( mediaPlayer.isPlaying() ){
    		currentPosition = mediaPlayer.getCurrentPosition();
    		mediaPlayer.reset();
    		Button play = (Button) findViewById(R.id.songPlay);
    		play.setText("Play");
    		playing = true;
    	}else{
    		playing = false;
    	}
    	
    	final Intent intent = new Intent(SoulPlayerActivity.this, NetRadioActivity.class);
    	intent.putExtra("currentSongPath", currentSongPath);
    	intent.putExtra("currentPosition", currentPosition);
    	intent.putExtra("songPosition", songPosition);
    	intent.putExtra("playing", playing);
    	intent.putExtra("playlist", playlist );
    	startActivityForResult(intent,SHOW_SOULPLAYER_ACTIVITY);
    }
    
    public boolean onCreateOptionsMenu (Menu menu){
    	// groupdId, itemId, order, title and icon (icon is optional)
        menu.add(0, MENU_MUSIC, 0, "Music").setIcon(R.drawable.music);
        menu.add(0, MENU_VIDEO, 0, "Video").setIcon(R.drawable.video);
        menu.add(0, MENU_QUIT, 0, "Quit").setIcon(R.drawable.quit);
        // return true => menu is visible
        return true;
    }
    
    /* Handles item selections */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
	        case MENU_MUSIC:
	        	Toast.makeText(getBaseContext(), "Music", Toast.LENGTH_SHORT).show();
	            return true;
	        case MENU_VIDEO:
	        	Toast.makeText(getBaseContext(), "Video", Toast.LENGTH_SHORT).show();
	            return true;
	        case MENU_QUIT:
	        	finish();
	            return true;
	        }
        return false;
    }
    
}