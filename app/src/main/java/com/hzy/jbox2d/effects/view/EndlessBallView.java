package com.hzy.jbox2d.effects.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.EdgeShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

/**
 * Created by huzongyao on 2018/5/14.
 */

public class EndlessBallView extends View {

    private Paint mPaint;
    // assume the world width is 8m
    private float mWorldWidth = 8f;
    //pixels in per meter
    private float mDpm;
    private float mWorldHeight;
    private World mWorld;
    private float mBallRadius = 0.25f;
    private Body mBallBody;
    private int mColorFill;
    private int mColorStroke;

    public EndlessBallView(Context context) {
        this(context, null);
    }

    public EndlessBallView(Context context, @Nullable AttributeSet attrs) {
        this(context, null, 0);
    }

    public EndlessBallView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mColorFill = Color.parseColor("#44ff4488");
        mColorStroke = Color.parseColor("#ff994488");
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

    private float getScreenRotate(float radians) {
        return (float) (radians * 180f / Math.PI);
    }

    private void createNewWorld() {
        mWorld = new World(new Vec2(0f, 0f));
        createBorder();
        mBallBody = createBallBody();
    }

    private Body createBallBody() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DYNAMIC;
        // set original position
        bodyDef.position.set(mWorldWidth / 2, mWorldHeight / 2);
        CircleShape shape = new CircleShape();
        shape.setRadius(mBallRadius);
        FixtureDef fixture = new FixtureDef();
        fixture.shape = shape;
        // so the body will move forever
        fixture.friction = 0f;
        fixture.restitution = 1f;
        fixture.density = 0.3f;
        Body body = mWorld.createBody(bodyDef);
        body.createFixture(fixture);
        body.applyForceToCenter(new Vec2(20f, 15f));
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
        canvas.save();
        canvas.translate(mBallBody.getPosition().x * mDpm, mBallBody.getPosition().y * mDpm);
        canvas.scale(mBallRadius * mDpm, mBallRadius * mDpm);
        canvas.rotate(getScreenRotate(mBallBody.getAngle()));

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mColorFill);
        canvas.drawCircle(0f, 0f, 1f, mPaint);

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(mColorStroke);
        canvas.drawCircle(0f, 0f, 1f, mPaint);
        canvas.drawLine(0f, 0f, 1f, 0f, mPaint);

        canvas.restore();
        invalidate();
    }
}
