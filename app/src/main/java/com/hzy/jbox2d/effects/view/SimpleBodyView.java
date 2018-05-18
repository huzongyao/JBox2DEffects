package com.hzy.jbox2d.effects.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.EdgeShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by huzongyao on 2018/5/14.
 */

public class SimpleBodyView extends View {

    private Paint mPaint;
    // assume the world width is 8m
    private float mWorldWidth = 8f;
    //pixels in per meter
    private float mDpm;
    private float mWorldHeight;
    private World mWorld;
    private float mBallRadius = 0.20f;
    private int mColorFill;
    private int mColorStroke;
    private List<Body> mBallList;
    private List<Body> mCubeList;

    public SimpleBodyView(Context context) {
        this(context, null);
    }

    public SimpleBodyView(Context context, @Nullable AttributeSet attrs) {
        this(context, null, 0);
    }

    public SimpleBodyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mColorFill = Color.parseColor("#44ff4488");
        mColorStroke = Color.parseColor("#ff994488");
        mBallList = new LinkedList<>();
        mCubeList = new LinkedList<>();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mDpm = w / mWorldWidth;
        mWorldHeight = h / mDpm;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        createNewWorld();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            createMixBodies();
        }
        return super.onTouchEvent(event);
    }

    private float getScreenRotate(float radians) {
        return (float) (radians * 180f / Math.PI);
    }

    /**
     * create the world
     */
    private void createNewWorld() {
        mWorld = new World(new Vec2(0f, 9.8f));
        createBorder();
        createMixBodies();
    }

    private void createMixBodies() {
        for (int i = 0; i < 8; i++) {
            mBallList.add(createBallBody(i));
            mCubeList.add(createCubeBody(i));
        }
    }

    /**
     * create the moving ball
     */
    private Body createBallBody(int index) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DYNAMIC;
        // set original position
        bodyDef.position.set(mWorldWidth / 2 + 0.1f, 0.1f * index);
        CircleShape shape = new CircleShape();
        shape.setRadius(mBallRadius);
        FixtureDef fixture = new FixtureDef();
        fixture.shape = shape;
        // so the body will move forever
        fixture.friction = 0.1f;
        fixture.restitution = 0.7f;
        fixture.density = 0.3f;
        Body body = mWorld.createBody(bodyDef);
        body.createFixture(fixture);
        return body;
    }

    private Body createCubeBody(int index) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DYNAMIC;
        // set original position
        bodyDef.position.set(mWorldWidth / 2 + 0.1f * index, 0.1f);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(mBallRadius, mBallRadius);
        FixtureDef fixture = new FixtureDef();
        fixture.shape = shape;
        // so the body will move forever
        fixture.friction = 0.2f;
        fixture.restitution = 0.5f;
        fixture.density = 0.6f;
        Body body = mWorld.createBody(bodyDef);
        body.createFixture(fixture);
        body.applyForceToCenter(new Vec2(0.1f, 0.3f));
        return body;
    }

    /**
     * create the borders for the world
     */
    private void createBorder() {
        BodyDef bodyDef = new BodyDef();
        Body groundBody = mWorld.createBody(bodyDef);
        EdgeShape edge = new EdgeShape();
        FixtureDef boxShapeDef = new FixtureDef();
        boxShapeDef.shape = edge;
        // top
        edge.set(new Vec2(0f, 0f), new Vec2(mWorldWidth, 0f));
        groundBody.createFixture(boxShapeDef);
        // left
        edge.set(new Vec2(0f, 0f), new Vec2(0f, mWorldHeight));
        groundBody.createFixture(boxShapeDef);
        // right
        edge.set(new Vec2(mWorldWidth, 0f), new Vec2(mWorldWidth, mWorldHeight));
        groundBody.createFixture(boxShapeDef);
        // bottom
        edge.set(new Vec2(0f, mWorldHeight), new Vec2(mWorldWidth, mWorldHeight));
        groundBody.createFixture(boxShapeDef);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mWorld.step(1f / 60, 8, 12);
        // draw background
        canvas.drawColor(Color.parseColor("#cccccc"));
        for (Body body : mBallList) {
            drawBallBody(canvas, body);
        }
        for (Body body : mCubeList) {
            drawCubeBody(canvas, body);
        }
        invalidate();
    }

    private void drawCubeBody(Canvas canvas, Body body) {
        canvas.save();
        canvas.translate(body.getPosition().x * mDpm, body.getPosition().y * mDpm);
        canvas.scale(mBallRadius * mDpm, mBallRadius * mDpm);
        canvas.rotate(getScreenRotate(body.getAngle()));

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mColorFill);
        canvas.drawRect(-1f, -1f, 1f, 1f, mPaint);

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(mColorStroke);
        canvas.drawRect(-1f, -1f, 1f, 1f, mPaint);

        canvas.restore();
    }

    private void drawBallBody(Canvas canvas, Body body) {
        canvas.save();
        canvas.translate(body.getPosition().x * mDpm, body.getPosition().y * mDpm);
        canvas.scale(mBallRadius * mDpm, mBallRadius * mDpm);
        canvas.rotate(getScreenRotate(body.getAngle()));

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mColorFill);
        canvas.drawCircle(0f, 0f, 1f, mPaint);

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(mColorStroke);
        canvas.drawCircle(0f, 0f, 1f, mPaint);
        canvas.drawLine(0f, 0f, 1f, 0f, mPaint);

        canvas.restore();
    }
}
