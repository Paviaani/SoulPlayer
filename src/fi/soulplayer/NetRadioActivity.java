package fi.soulplayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class NetRadioActivity  extends ListActivity{
	
	private MediaPlayer mediaPlayer = new MediaPlayer();
	private EditText url; 
	private List<String> songs = new ArrayList<String>();
	
	private int currentPosition = 0;
    private int songPosition = 0;
    private String[] playlist;
    private String currentSongPath = "";
	private boolean playing = false;

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.netradio);
        
        //Add stuff to list
        addUrl("http://nayuki.animenfo.com:8000/");
        addUrl("http://stream.radiorock.fi/");
        
        //Default text
		setUrl("http://");
		
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
    
    public void updatePlaylist(){;
    	currentPosition = 0;
    	
    	playlist = new String[songs.size()];
    	
    	for ( int i=0; i < songs.size(); i++  ){
    		String path2 = songs.get(i);
    		playlist[i] = path2;
    	}
    	sendData();

    }
	
	// get string from EditBox
	public String getUrl(){
		url = (EditText)findViewById(R.id.url);
		return url.getText().toString();
		
	}
	
	// set string to EditBox
	public void setUrl(String path){
		url = (EditText)findViewById(R.id.url);
		url.setText(path);
	}
	
	public void addUrl( String path ) {
		
		Boolean allReadyExits = false;
		
		for (int i=0; i < songs.size(); i++ ){
			if ( songs.get(i).toString() == path ){
				allReadyExits = true;
			}
		}
		
		if ( !allReadyExits ){
			songs.add(path);
		}
        
        ArrayAdapter<String> songList = new ArrayAdapter<String>(this, R.layout.radio_list_item, songs);
        setListAdapter(songList);
        songPosition = songs.size()-1;
        updatePlaylist();
        
    }
	
	
	private void playUrl(String songPath) {
		
		currentSongPath = songPath;
		
        try {
        	
        	mediaPlayer.reset();
        	mediaPlayer.setDataSource(songPath);
        	mediaPlayer.prepare();
        	
        	addUrl(songPath);
        	setUrl(songPath);
        	
        	mediaPlayer.start();
        	playing = true;
 
        } catch (IOException e) {
        	
        	Toast.makeText(getBaseContext(), "Error - Can't play url", Toast.LENGTH_SHORT).show();
            Log.v(getString(R.string.app_name), e.getMessage());
        }
        
        Toast.makeText(getBaseContext(), songPath, Toast.LENGTH_SHORT).show();
    }
	
	private void playSong(String songPath) {

    	currentSongPath = songPath;
    	
    	if (songPath.length() > 2){
	        try {
	   	
	        	mediaPlayer.reset();
	        	mediaPlayer.setDataSource(songPath);
	        	mediaPlayer.prepare();
	        	mediaPlayer.seekTo(currentPosition);
	        	mediaPlayer.start();
	        	playing = true;
	 
	        // Setup listener so next song starts automatically
	        	mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
	        		public void onCompletion(MediaPlayer arg0) {
	        			nextSong();
	                }
	            });
	        } catch (IOException e) {
	                Log.v(getString(R.string.app_name), e.getMessage());
	        }
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

    
    private void playPlaylist( int index ){
    	
    	if ( playlist.length > index && index > -1 ){
    		playSong(playlist[index]);
    	}
    }
	
	
	
	@Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
			playUrl(songs.get(position));
			updatePlaylist();
			songPosition = position;
			
    }
	
	
	
	/** Called when the activity is destroyed */
    @Override
    public void onDestroy() {
    	sendData();
    	super.onDestroy();
    	if (mediaPlayer.isPlaying()) mediaPlayer.reset();
    }
	
	
	
    public void startAudio(View view) {    	
    	updatePlaylist();
    	playUrl( getUrl() );
    }   
    

    public void stopAudio(View view) {
    	if (mediaPlayer != null){
    		mediaPlayer.stop();
    	}
    	playing = false;
    	updatePlaylist();
    }
    
}
