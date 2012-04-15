package com.haxademic.app.kacheout.game;

import toxi.color.TColor;
import toxi.geom.AABB;
import toxi.geom.Sphere;

import com.haxademic.app.PAppletHax;
import com.haxademic.app.kacheout.KacheOut;
import com.haxademic.core.util.MathUtil;

public class Ball {
	
	protected KacheOut p;
	protected float BALL_SIZE = 20;
	protected int BALL_RESOLUTION = 20;
	float _x, _y, _speedX, _speedY;
	protected TColor _color;
	protected Sphere _sphere;
	protected float SPEED = 15f;
	
	public Ball() {
		p = (KacheOut)PAppletHax.getInstance();
		// convert speed to use radians
		_speedX = ( MathUtil.randBoolean( p ) == true ) ? SPEED : -SPEED;
		_x = p.random( 0, p.gameWidth() );
		_y = p.random( p.stageHeight() / 2, p.stageHeight() );
		_color = p.gameColors().getRandomColor().copy();
		_sphere = new Sphere( BALL_SIZE );
	}
	
	public float x() { return _x; }
	public float y() { return _y; }
	public Sphere sphere() { return _sphere; }
	public float radius() { return BALL_SIZE; }
	
	public void launch( Paddle paddle ) {
		_x = paddle.x(); 
		_y = paddle.y() - paddle.height() - BALL_SIZE - 10;
		_speedX = ( MathUtil.randBoolean( p ) == true ) ? SPEED : -SPEED;
		_speedY = -SPEED;
	}
	
	public void bounceX() {
		_speedX *= -1;
		_x += _speedX;
	}
	
	public void bounceY() {
		_speedY *= -1;
		_y += _speedY;
	}
	
	public void display( Paddle paddle ) {
		if( p.gameState() == p.GAME_READY ) {
			_x = paddle.x();
			_y = paddle.y() - paddle.height() - BALL_SIZE - 10;
		} else {
			_x += _speedX;
			_y += _speedY;
		}
					
		p.fill( _color.toARGB() );
		_sphere.x = _x;
		_sphere.y = _y;
		p._toxi.sphere( _sphere, BALL_RESOLUTION );
	}
	
	public void detectWalls( boolean leftHit, boolean topHit, boolean rightHit ) {
		if( leftHit == true ) {
			_x -= _speedX;
			_speedX *= -1;
		}
		if( rightHit == true ) {
			_x -= _speedX;
			_speedX *= -1;
		}
		if( topHit == true ) {
			_y -= _speedY;
			_speedY *= -1;
		}
//		if( _y > _stageHeight ) {
//			_y -= _speedY;
//			_speedY *= -1;
//		}
	}

	public boolean detectBox( AABB box ) {
		if( box.intersectsSphere( _sphere ) ) return true;
		return false;
	}
	
	public void bounceOffPaddle( Paddle paddle ) {
		p.println("bounce!");
		_speedX = ( _x - paddle.x() ) / 10;
		bounceY();
	}

}