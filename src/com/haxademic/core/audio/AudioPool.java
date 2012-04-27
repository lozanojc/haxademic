package com.haxademic.core.audio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import processing.core.PApplet;
import ddf.minim.AudioPlayer;
import ddf.minim.Minim;

public class AudioPool {
	protected PApplet p;
	public Minim _minim;
	protected HashMap<String, ObjItem> _audioPlayers;
	
	public AudioPool( PApplet p, Minim minim ) {
		this.p = p;
		_minim = minim;
		_audioPlayers = new HashMap<String, ObjItem>();
	}
	
	public void loadAudioFile( String id, float scale, String file ) {
		_audioPlayers.put( id, new ObjItem( p, scale, file ) );
	}
	
	public AudioPlayer getSound( String id ) {
		return _audioPlayers.get( id )._sound;
	}

	public void playSound( String id ) {
		_audioPlayers.get( id )._sound.play(0);
	}

	public ArrayList<String> getIds() {
		ArrayList<String> keyList = new ArrayList<String>();
		Iterator iter = _audioPlayers.keySet().iterator();
	    while (iter.hasNext()) {
	    	keyList.add( iter.next().toString() );
	    	System.out.println("Loaded audio: "+keyList.get( keyList.size()-1 ));
	    }
		return keyList;
	}

	/**
	 * ObjItem is used to load an audio file, ready to be triggered whenever.
	 * @author cacheflowe
	 *
	 */
	public class ObjItem {
		public String _file;
		public float _vol;
		public AudioPlayer _sound;
		
		/**
		 * Initializes
		 * @param p
		 * @param scale
		 * @param file
		 */
		public ObjItem( PApplet p, float vol, String file ) {
			_file = file;
			_vol = vol;
			_sound = _minim.loadFile(file, 512);
		}
	}
}
