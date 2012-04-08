package com.haxademic.app.kacheout.game;

import toxi.color.TColor;
import toxi.geom.AABB;
import toxi.geom.Sphere;
import toxi.geom.Vec3D;

import com.haxademic.app.PAppletHax;
import com.haxademic.app.kacheout.KacheOut;
import com.haxademic.core.data.easing.EasingFloat;

public class Paddle {
	protected EasingFloat _x, _y, _z;
	protected int STAGE_H_PADDING = 40;
	protected float HEIGHT = 20;
	protected float _width = 0;
	protected float _easing = 1.5f;
	protected float _center;
	protected float _xPosPercent;
	protected TColor _color;
	protected AABB _box;
	protected KacheOut p;

	public Paddle() {
		p = (KacheOut)PAppletHax.getInstance();
		_center = ( p.gameWidth() + _width) / 2;
		_xPosPercent = 0.5f;
		_width = (float)p.gameWidth() / 5f;
		_x = new EasingFloat( _center, _easing );
		_y = new EasingFloat( p.stageHeight() - STAGE_H_PADDING, _easing );
		_color = p.gameColors().getRandomColor().copy();
		_box = new AABB( 1 );
		_box.setExtent( new Vec3D( _width, HEIGHT, HEIGHT ) );
	} 
	
	public float x() { return _x.value(); }
	public float y() { return _y.value(); }
	public float height() { return HEIGHT; }
	public float xPosPercent() { return _xPosPercent; }

	public AABB box() {
		return _box;
	}

	public void setTargetXByPercent( float percent ) {
		_xPosPercent = percent;
		percent = 1 - percent;
		_x.setTarget( _width + percent * (p.gameWidth() - _width*2f) );
	}

	public boolean detectSphere( Sphere sphere ) {
		if( _box.intersectsSphere( sphere ) ) return true;
		return false;
	}

	public void display() {
		_x.update();
		_y.update();
		
		_box.set( _x.value(), _y.value(), 0 );
		_box.rotateX( p.frameCount / 30f );
		_color.alpha = 0.5f + p._audioInput.getFFT().averages[1];
		p.fill( _color.toARGB() );
		p.noStroke();
		p._toxi.box( _box ); 
	}
}
