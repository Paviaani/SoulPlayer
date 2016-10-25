package fi.soulplayer;

import java.io.File;
import java.io.IOException;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.provider.MediaStore.Audio;
import android.provider.MediaStore.Audio.Media;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class SongsActivity  extends ListActivity{
	

    private MediaPlayer mediaPlayer = new MediaPlayer();
    private int currentPosition = 0;
    private int songPosition = 0;
    
    
    private static final int PLAY = 0;
	private static final int EDIT = 1;
	
	private String id;
	
	private EditText editArtist;
	private EditText editAlbum;
	private EditText editTitle;
	
	private String[] playlist;
	private String currentSongPath = "";
	private boolean playing = false;
	
	

	

    
    @Override
    public void onCreate(Bundle icicle) {
            super.onCreate(icicle);
            setContentView(R.layout.songs);
            
            // What I want to query
            String[] projection = new String[] { Media._ID,
            		Media.TITLE,
            		Media.ARTIST,                
                    Media.ALBUM};
            
            // What I want to show
            String[] displayFields = new String[] {Media.TITLE, 
                    Media.ARTIST
                    };
            
            // Where I want to show them
            int[] displayViews = new int[] { R.id.title, R.id.artist };

            // Using cursor to query android media provider
            Cursor cur2 = managedQuery(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, 
                           projection, null, null, null);

            // ListAdapter to set queried things to a ListView
            setListAdapter(new SimpleCursorAdapter(this, R.layout.list_item, cur2, displayFields, displayViews));
            
            
         // get data from calling intent
            Bundle extras = getIntent().getExtras();
			currentSongPath = extras.getString("currentSongPath");
			currentPosition = extras.getInt("currentPosition");
			songPosition = extras.getInt("songPosition");
			playing = extras.getBoolean("playing", playing);
			playlist = extras.getStringArray("playlist");
			if (playing){
				playSong(currentSongPath);
			}
                 
    }
    
    
    public void sendData( ) {
    
    	// create a new intent to pass data
    	Intent intent = new Intent();
    	// add result to intent
    	intent.putExtra("currentSongPath", currentSongPath);
    	intent.putExtra("currentPosition", currentPosition);
    	intent.putExtra("songPosition", songPosition);
    	intent.putExtra("playing", playing);
    	intent.putExtra("playlist", playlist );
    	
    	
    	// all ok here, result is set
    	setResult(RESULT_OK,intent);
    	
    }
    
    
    // Read tag data to the dialogs EditText fields
    public void readTagData(long i)
    {
    	String[] str = { Media.ARTIST, Media.ALBUM, Media.TITLE, Media._ID };
    	Cursor cur = managedQuery(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, 
                str, null, null, null);    	
    	cur.moveToPosition((int)i - 1);
    	String artist = cur.getString(cur.getColumnIndex(Audio.Media.ARTIST));
    	String album = "";//cur.getString(cur.getColumnIndex(Audio.Media.ALBUM));
    	String title = cur.getString(cur.getColumnIndex(Audio.Media.TITLE));
    	id = cur.getString(cur.getColumnIndex(Audio.Media._ID));
    	
    	editArtist.setText(artist);
    	editAlbum.setText(album);
    	editTitle.setText(title);    	
    }
    
    // Plays the selected song
    public void playSelected(long i)
    {
    	String[] str = { Media.DATA };
    	Cursor cur = managedQuery(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, 
                str, null, null, null);    	
    	cur.moveToPosition((int)i - 1);
    	String path = cur.getString(cur.getColumnIndex(Audio.Media.DATA)); 
    	
    	playSong(path);
    }
    
    public void updatePlaylist(){
    	String[] str = { Media.DATA };
    	Cursor cur = managedQuery(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, 
                str, null, null, null);
    	
    	playlist = new String[cur.getCount()];
    	
    	for ( int i=0; i < cur.getCount(); i++  ){
    		cur.moveToPosition( i );
    		String path = cur.getString(cur.getColumnIndex(Audio.Media.DATA));
    		
    		playlist[i] = path;
    	}

    }
    
    //##################################################################
    
    

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
    		songPosition = position;
    		currentPosition = 0;
    		
    		updatePlaylist();
    		playPlaylist( songPosition );
    }
    
    
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

    /** Called when the activity is destroyed */
    @Override
    public void onDestroy() {
    	currentPosition = mediaPlayer.getCurrentPosition();
    	sendData();
    	mediaPlayer.reset();
    	
    	super.onDestroy();
    	//if (mediaPlayer.isPlaying()) mediaPlayer.reset();
    }
    
    public void startAudio(View view) {
    	
    	if ( mediaPlayer.isPlaying() ){
    		if (mediaPlayer != null){
        		currentPosition = mediaPlayer.getCurrentPosition();
        		mediaPlayer.pause();
        		Button play = (Button) findViewById(R.id.songPlay);
        		play.setText("Play");
           	}
    	}else{
    		playPlaylist( songPosition );
    	}
    }   
    
    public void pauseAudio(View view) {
    	if ( mediaPlayer.isPlaying() ){
    		currentPosition = mediaPlayer.getCurrentPosition();
    		mediaPlayer.reset();
    		playing = true;
    	}else{
    		playing = false;
    	}
    	
    	sendData();
    	mediaPlayer.reset();
    	finish();
    	
    }

    public void stopAudio(View view) {
    	
    	if (mediaPlayer != null){
    		currentPosition = 0;
    		mediaPlayer.stop();
    		Button play = (Button) findViewById(R.id.songPlay);
    		play.setText("Play");
    	}
    }
    
    public void songBack (View view){
    	previousSong();
    }
    
    public void songNext (View view){
		nextSong();
    }
}