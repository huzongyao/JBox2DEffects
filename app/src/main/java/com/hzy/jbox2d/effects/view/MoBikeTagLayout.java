package com.hzy.jbox2d.effects.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.EdgeShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

/**
 * Created by huzongyao on 2018/5/11.
 * render by set the children view
 */

public class MoBikeTagLayout extends FrameLayout {

    private World mWorld;
    // assume the world width is 8m
    private float mWorldWidth = 8f;
    private float mWorldHeight;
    private float mDpm;

    public MoBikeTagLayout(Context context) {
        this(context, null);
    }

    public MoBikeTagLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MoBikeTagLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setWillNotDraw(false);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mDpm = w / mWorldWidth;
        mWorldHeight = h / mDpm;
    }

    private float getScreenRotate(float radians) {
        return (float) (radians * 180f / Math.PI);
    }

    private PointF getRealCenter(View view) {
        return new PointF((view.getX() + view.getWidth() / 2) / mDpm,
                (view.getY() + view.getHeight() / 2) / mDpm);
    }

    private float getRealRadius(View view) {
        return view.getWidth() / mDpm / 2;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        createNewWorld();
    }

    private void createNewWorld() {
        mWorld = new World(new Vec2(0f, 9.8f));
        createBorder();
        for (int i = 0; i < getChildCount(); i++) {
            View circleView = getChildAt(i);
            Body circleBody = createCircleBody(circleView, i);
            circleView.setTag(circleBody);
        }
    }

    private Body createCircleBody(View view, int index) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DYNAMIC;
        PointF realCenter = getRealCenter(view);
        bodyDef.position.set(realCenter.x, realCenter.y);
        CircleShape shape = new CircleShape();
        shape.setRadius(getRealRadius(view));
        FixtureDef fixture = new FixtureDef();
        fixture.shape = shape;
        fixture.friction = 0.03f;
        fixture.restitution = 0.5f;
        fixture.density = 0.3f;
        Body body = mWorld.createBody(bodyDef);
        body.createFixture(fixture);
        body.applyForceToCenter(new Vec2(index + 1, index + 2));
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
        super.onDraw(canvas);
        mWorld.step(1f / 60, 8, 8);
        for (int i = 0; i < getChildCount(); i++) {
            View circleView = getChildAt(i);
            if (circleView.getTag() != null && circleView.getTag() instanceof Body) {
                Body body = (Body) circleView.getTag();
                circleView.setX(body.getPosition().x * mDpm - circleView.getWidth() / 2);
                circleView.setY(body.getPosition().y * mDpm - circleView.getHeight() / 2);
                circleView.setRotation(getScreenRotate(body.getAngle()));
            }
        }
        invalidate();
    }

    public void onSensorChanged(float x, float y) {
        float realX = -x;
        float realY = y + 1;
        mWorld.setGravity(new Vec2(realX, realY));
    }
}
