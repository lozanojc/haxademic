package com.haxademic.viz.elements;

import processing.core.PApplet;
import processing.core.PConstants;
import toxi.color.TColor;
import toxi.processing.ToxiclibsSupport;

import com.haxademic.app.P;
import com.haxademic.core.audio.AudioInputWrapper;
import com.haxademic.core.util.ColorGroup;
import com.haxademic.core.util.DrawUtil;
import com.haxademic.viz.ElementBase;
import com.haxademic.viz.IVizElement;

public class BarsEQ2d
extends ElementBase 
implements IVizElement {
	
	protected float _width;
	protected float _barHeight;
	protected float _amp;
	
	protected float _cols = 32;
	protected TColor _baseColor = null;
	protected TColor _fillColor = null;


	public BarsEQ2d( PApplet p, ToxiclibsSupport toxi, AudioInputWrapper audioData ) {
		super( p, toxi, audioData );
		init();
	}

	public void init() {
		// set some defaults
		_width = p.width;
		_barHeight = p.height/8f;
	}
	
	public void setDrawProps(float width, float height) {
		_width = width;
		_barHeight = height;
	}

	public void updateColorSet( ColorGroup colors ) {
		_baseColor = colors.getRandomColor().copy();
		_fillColor = _baseColor.copy();
		_fillColor.alpha = 0.2f;
	}
	
	public void update() {
		DrawUtil.resetGlobalProps( p );
		DrawUtil.setCenter( p );
		p.pushMatrix();
		
		p.rectMode(PConstants.CORNER);
		p.noStroke();
		
		setDrawProps(p.width, p.height/4);
		p.fill( 0 );
		p.translate( 0, 0, -400f );

		// draw bars
		p.translate( 0, -p.height/2 );
		drawBars();
		p.translate( 0, p.height );
		p.rotateX( (float)(Math.PI*2f) / 2f );
		drawBars();
		
		p.popMatrix();
	}

	public void drawBars() {
		// draw bars
		float cellW = _width/_cols;
		float cellH = _barHeight * 2;
		float startX = -_width/2;
		int spectrumInterval = (int) ( 256 / _cols );	// 256 keeps it in the bottom half of the spectrum since the high ends is so overrun
		for (int i = 0; i < _cols; i++) {
			p.rect( startX + i * cellW, 0, cellW, _audioData.getFFT().spectrum[i*spectrumInterval] * cellH );
		}		
	}

	public void reset() {
		
	}

	public void dispose() {
		_audioData = null;
	}
	
}
