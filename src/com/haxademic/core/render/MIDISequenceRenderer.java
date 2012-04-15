package com.haxademic.core.render;

// help from: http://stackoverflow.com/questions/3850688/reading-midi-files-in-java
// and: 	  http://stackoverflow.com/questions/2038313/midi-ticks-to-actual-playback-seconds-midi-music
// and: 	  http://stackoverflow.com/questions/7063437/midi-timestamp-in-seconds

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

import processing.core.PApplet;

public class MidiSequenceRenderer {
	protected PApplet p;

    public static final int NOTE_ON = 0x90;
    public static final int NOTE_OFF = 0x80;
    public static final String[] NOTE_NAMES = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
    
    protected Vector<MidiSequenceEvent> _messages;
    protected float _bpm = 120;
    protected float _ticksPerMin = 0;
    protected float _ticksPerSecond = 0;
    protected float _tickInMilliseconds = 0;
    
    // helps align audio and midi render timing
    protected float _frameOffset = 0;
    protected float _renderFPS = 30;
    
    public MidiSequenceRenderer( PApplet p5 ) {
    	p = p5;
    } 
    
    public void loadMIDIFile( String midiFile, float midiBpm, float renderFPS, float frameOffset ) throws InvalidMidiDataException, IOException {
    	// load file
        Sequence sequence = MidiSystem.getSequence(new File(midiFile));
        p.println("sequence.getMicrosecondLength() = " + sequence.getMicrosecondLength());
        p.println("sequence.getTickLength() = " + sequence.getTickLength());
        p.println("sequence.getResolution() = " + sequence.getResolution());
        p.println("sequence.getDivisionType() = " + sequence.getDivisionType());
        
        // calculate midi event timing
        _frameOffset = frameOffset;
        _renderFPS = renderFPS;
        _bpm = midiBpm;
    	_ticksPerMin = _bpm * sequence.getResolution();
    	_ticksPerSecond = _ticksPerMin / 60f;
    	_tickInMilliseconds = _ticksPerSecond / 1000.f;
        p.println("_bpm = " + _bpm);
        p.println("_ticksPerMin = " + _ticksPerMin);
        p.println("_ticksPerSecond = " + _ticksPerSecond);
        p.println("_tickInMilliseconds = " + _tickInMilliseconds);

        int trackNumber = 0;
        _messages = new Vector<MidiSequenceEvent>();
        for (Track track :  sequence.getTracks()) {
            trackNumber++;
            p.println("Track " + trackNumber + ": size = " + track.size() + ": ticks = " + track.ticks());
            p.println();
            for (int i=0; i < track.size(); i++) { 
                MidiEvent event = track.get(i);
                System.out.print("@" + event.getTick() + " ");
                MidiMessage message = event.getMessage();
                if (message instanceof ShortMessage) {
                    ShortMessage sm = (ShortMessage) message;
                    System.out.print("Channel: " + sm.getChannel() + " ");
                    float tickTime = event.getTick() / _ticksPerSecond;
                    if (sm.getCommand() == NOTE_ON) {
                    	_messages.add( new MidiSequenceEvent( event, sm, tickTime ) );
                        int key = sm.getData1();
                        int octave = (key / 12)-1;
                        int note = key % 12;
                        String noteName = NOTE_NAMES[note];
                        int velocity = sm.getData2();
                        p.println("Note on, " + noteName + octave + " key=" + key + " velocity: " + velocity + " tick: " + event.getTick() + " tickTime: " + tickTime);
                    } else if (sm.getCommand() == NOTE_OFF) {
                    	_messages.add( new MidiSequenceEvent( event, sm, tickTime ) );
                    	int key = sm.getData1();
                        int octave = (key / 12)-1;
                        int note = key % 12;
                        String noteName = NOTE_NAMES[note];
                        int velocity = sm.getData2();
                        p.println("Note on, " + noteName + octave + " key=" + key + " velocity: " + velocity + " tick: " + event.getTick() + " tickTime: " + tickTime);
                    } else {
                        p.println("Command:" + sm.getCommand());
                    }
                } else {
                    p.println("Other message: " + message.getClass());
                }
            }
            p.println();
        }
    }
    
    // return current midi pitch of it's an NOTE_ON message. shift the event off the from of the vector to get ready for the next event
    public int checkCurrentNoteEvent() {
    	// get current time and add offset
    	float curAppletSeconds = (float)p.frameCount / _renderFPS;
    	curAppletSeconds += (_frameOffset * 1f/_renderFPS);

    	if( _messages.size() > 0 ) {
    		MidiSequenceEvent curEvent = _messages.firstElement();
    		if( curEvent != null ) {
    			if( curEvent.getSeconds() < curAppletSeconds ) {
    				_messages.remove( 0 );
    				if( curEvent.getMidiCommand() == NOTE_ON )
    					return curEvent.getMidiNotePitch();
    			}
    		} 
    	}
    	return( -1 );
    }
    
    public class MidiSequenceEvent {
    	protected ShortMessage _sm;
    	protected MidiEvent _event;
    	protected float _tickTimeSeconds;
    	
    	public MidiSequenceEvent( MidiEvent event, ShortMessage sm, float tickTime ) {
    		_sm = sm;
    		_event = event;
    		_tickTimeSeconds = tickTime;
    	}
    	
    	public float getMidiCommand() {
    		return _sm.getCommand();
    	}
    	
    	public float getSeconds() {
    		return _tickTimeSeconds;
    	}
    	
    	public int getMidiNotePitch() {
    		return _sm.getData1();
    	}
    	
    }
    
}