package com.haxademic.app.kacheout.game;

import processing.core.PApplet;
import toxi.color.TColor;
import toxi.geom.AABB;
import toxi.geom.Sphere;
import toxi.geom.mesh.WETriangleMesh;

import com.haxademic.app.PAppletHax;
import com.haxademic.app.kacheout.KacheOut;
import com.haxademic.core.data.easing.ElasticFloat;
import com.haxademic.core.draw.color.EasingTColor;
import com.haxademic.core.draw.util.DrawMesh;
import com.haxademic.core.util.MathUtil;

public class Ball {
	
	protected KacheOut p;
	protected float _ballSize;
	protected ElasticFloat _ballSizeElastic;
	protected int BALL_RESOLUTION = 4;
	float _x, _y, _speedX, _speedY;
	protected EasingTColor _color;
	protected Sphere _sphere;
	protected float _speed;
	protected WETriangleMesh _ballMesh;
	
	public Ball() {
		p = (KacheOut)PAppletHax.getInstance();
		// TODO: convert speed to use radians
		_speed = p.stageHeight() / 80f;
		_speedX = ( MathUtil.randBoolean( p ) == true ) ? _speed : -_speed;
		_x = p.random( 0, p.gameWidth() );
		_y = p.random( p.stageHeight() / 2, p.stageHeight() );
		_color = new EasingTColor( new TColor(TColor.YELLOW), 0.05f );

		_ballSize = p.stageHeight() / 16f;
		_ballSizeElastic = new ElasticFloat( _ballSize, 0.85f, 0.25f );
		_sphere = new Sphere( _ballSize );
		
		_ballMesh = new WETriangleMesh();
		_ballMesh.addMesh( _sphere.toMesh( BALL_RESOLUTION ) );
	}
	
	public float x() { return _x; }
	public float y() { return _y; }
	public float speedX() { return _speedX; }
	public float speedY() { return _speedY; }
	public Sphere sphere() { return _sphere; }
	public float radius() { return _ballSize; }
	
	public void launch( Paddle paddle ) {
		_x = paddle.x(); 
		_y = paddle.y() - paddle.height() - _ballSize - 10;
		_speedX = ( MathUtil.randBoolean( p ) == true ) ? _speed : -_speed;
		_speedY = -_speed;
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
			_y = paddle.y() - paddle.height() - _ballSize - 10;
		} else {
			_x += _speedX;
			_y += _speedY;
		}
					
		_color.update();
		p.fill( _color.color().toARGB() );
		_sphere.x = _x;
		_sphere.y = _y;
		
		// update elastic scale
		_ballSizeElastic.update();
		float ballScale = _ballSizeElastic.val() / _ballSize;
		_ballMesh.scale( ballScale );
		
		p.pushMatrix();
		p.translate( _x, _y );
		p.rotateX( p.frameCount * 0.01f );
		p.rotateY( p.frameCount * 0.01f );
		p.rotateZ( p.frameCount * 0.01f );
		DrawMesh.drawMeshWithAudio( (PApplet)p, _ballMesh, p._audioInput, 3f, false, _color.color(), _color.color(), 0.9f );
		p.popMatrix();
		//p._toxi.sphere( _sphere, BALL_RESOLUTION );
		
		// reset elastic scale
		_ballMesh.scale( 1 / ballScale );
	}
	
	public void detectWalls( boolean leftHit, boolean topHit, boolean rightHit ) {
		boolean didHit = false;
		if( leftHit == true ) {
			_x -= _speedX;
			_speedX *= -1;
			didHit = true;
		}
		if( rightHit == true ) {
			_x -= _speedX;
			_speedX *= -1;
			didHit = true;
		}
		if( topHit == true ) {
			_y -= _speedY;
			_speedY *= -1;
			didHit = true;
		}
//		if( _y > _stageHeight ) {
//			_y -= _speedY;
//			_speedY *= -1;
//		}
		
		if( didHit == true ) {
			_color.setCurAndTargetColors( new TColor(TColor.WHITE), new TColor(TColor.YELLOW) );
			_ballSizeElastic.setValue( _ballSize * 0.7f );
			_ballSizeElastic.setTarget( _ballSize );
		}
	}

	public boolean detectBox( AABB box ) {
		if( box.intersectsSphere( _sphere ) ) return true;
		return false;
	}
	
	public void bounceOffPaddle( Paddle paddle ) {
		_speedX = ( _x - paddle.x() ) / 10;
		bounceY();
		_color.setCurAndTargetColors( new TColor(TColor.WHITE), new TColor(TColor.YELLOW) );
	}

}
