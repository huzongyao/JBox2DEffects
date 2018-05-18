package com.hzy.jbox2d.testbed;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import org.jbox2d.callbacks.DebugDraw;
import org.jbox2d.common.Color3f;
import org.jbox2d.common.IViewportTransform;
import org.jbox2d.common.Mat22;
import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Transform;
import org.jbox2d.common.Vec2;
import org.jbox2d.particle.ParticleColor;

/**
 * Created by huzongyao on 2018/5/14.
 * to draw debug info with android canvas
 */

public class AndroidDebugDraw extends DebugDraw {

    private Canvas mCanvas;
    private final Vec2 sp1 = new Vec2();
    private final Vec2 sp2 = new Vec2();

    private final Vec2 temp = new Vec2();
    private final Vec2 temp2 = new Vec2();
    private Paint mPaint = new Paint();

    private final Path mPath = new Path();

    public AndroidDebugDraw() {
        mPaint.setAntiAlias(true);
    }

    public void setCanvas(Canvas canvas) {
        mCanvas = canvas;
    }

    @Override
    public void setViewportTransform(IViewportTransform viewportTransform) {
        super.setViewportTransform(viewportTransform);
        viewportTransform.setYFlip(true);
    }

    @Override
    public void drawPoint(Vec2 argPoint, float argRadiusOnScreen, Color3f argColor) {
        getWorldToScreenToOut(argPoint, sp1);
        mPaint.setColor(getAndroidColor(argColor));
        mCanvas.drawPoint(sp1.x, sp1.y, mPaint);
    }

    @Override
    public void drawCircle(Vec2 center, float radius, Color3f color) {
        mPaint.setColor(getAndroidColor(color));
        mPaint.setStyle(Paint.Style.STROKE);
        getWorldToScreenToOut(center, sp1);
        Mat22 vt = viewportTransform.getMat22Representation();
        mCanvas.save();
        mCanvas.translate(sp1.x, sp1.y);
        mCanvas.scale(vt.ex.x, vt.ey.y);
        mCanvas.drawCircle(0, 0, radius, mPaint);
        mCanvas.restore();
    }

    @Override
    public void drawCircle(Vec2 center, float radius, Vec2 axis, Color3f color) {
        getWorldToScreenToOut(center, sp1);
        Mat22 vt = viewportTransform.getMat22Representation();
        mCanvas.save();
        mCanvas.translate(sp1.x, sp1.y);
        mCanvas.scale(vt.ex.x, vt.ey.y);

        mPaint.setColor(getAndroidColor(color));
        mPaint.setStyle(Paint.Style.STROKE);
        mCanvas.drawCircle(0, 0, radius, mPaint);

        if (axis != null) {
            mCanvas.rotate((float) (-MathUtils.atan2(axis.y, axis.x) * 180f / Math.PI));
            mCanvas.drawLine(0, 0, radius, 0, mPaint);
        }
        mCanvas.restore();
    }

    @Override
    public void drawSolidCircle(Vec2 center, float radius, Vec2 axis, Color3f color) {
        int f = getAndroidColor(color, 0.4f);
        int s = getAndroidColor(color, 1f);
        getWorldToScreenToOut(center, sp1);
        Mat22 vt = viewportTransform.getMat22Representation();
        mCanvas.save();
        mCanvas.translate(sp1.x, sp1.y);
        mCanvas.scale(vt.ex.x, vt.ey.y);

        mPaint.setColor(f);
        mPaint.setStyle(Paint.Style.FILL);
        mCanvas.drawCircle(0, 0, radius, mPaint);

        mPaint.setColor(s);
        mPaint.setStyle(Paint.Style.STROKE);
        mCanvas.drawCircle(0, 0, radius, mPaint);

        if (axis != null) {
            mCanvas.rotate((float) (-MathUtils.atan2(axis.y, axis.x) * 180f / Math.PI));
            mCanvas.drawLine(0, 0, radius, 0, mPaint);
        }
        mCanvas.restore();
    }

    @Override
    public void drawPolygon(Vec2[] vertices, int vertexCount, Color3f color) {
        mPath.reset();
        for (int i = 0; i < vertexCount; i++) {
            getWorldToScreenToOut(vertices[i], temp);
            if (i == 0) {
                mPath.moveTo(temp.x, temp.y);
            } else {
                mPath.lineTo(temp.x, temp.y);
            }
        }
        mPath.close();
        // draw the path
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(getAndroidColor(color));
        mCanvas.drawPath(mPath, mPaint);
    }

    @Override
    public void drawSolidPolygon(Vec2[] vertices, int vertexCount, Color3f color) {
        drawPolygon(vertices, vertexCount, color);
        int f = getAndroidColor(color, 0.4f);
        // fill the path
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(f);
        mCanvas.drawPath(mPath, mPaint);
    }

    @Override
    public void drawSegment(Vec2 p1, Vec2 p2, Color3f color) {
        getWorldToScreenToOut(p1, sp1);
        getWorldToScreenToOut(p2, sp2);
        mPaint.setColor(getAndroidColor(color));
        mPaint.setStyle(Paint.Style.STROKE);
        mCanvas.drawLine(sp1.x, sp1.y, sp2.x, sp2.y, mPaint);
    }

    @Override
    public void drawTransform(Transform xf) {
        getWorldToScreenToOut(xf.p, temp);
        temp2.setZero();
        float k_axisScale = 0.4f;
        temp2.x = xf.p.x + k_axisScale * xf.q.c;
        temp2.y = xf.p.y + k_axisScale * xf.q.s;
        getWorldToScreenToOut(temp2, temp2);
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
        mCanvas.drawLine(temp.x, temp.y, temp2.x, temp2.y, mPaint);

        temp2.x = xf.p.x + -k_axisScale * xf.q.s;
        temp2.y = xf.p.y + k_axisScale * xf.q.c;
        getWorldToScreenToOut(temp2, temp2);
        mPaint.setColor(Color.GREEN);
        mCanvas.drawLine(temp.x, temp.y, temp2.x, temp2.y, mPaint);
    }

    @Override
    public void drawString(float x, float y, String s, Color3f color) {
        mPaint.setColor(getAndroidColor(color));
        mPaint.setStyle(Paint.Style.FILL);
        mCanvas.drawText(s, x, y, mPaint);
    }

    @Override
    public void drawParticles(Vec2[] centers, float radius, ParticleColor[] colors, int count) {
        for (int i = 0; i < count; i++) {
            getWorldToScreenToOut(centers[i], temp);
            int color;
            if (colors == null) {
                color = Color.RED;
            } else {
                ParticleColor c = colors[i];
                color = getAndroidColor(c.r * 1f / 127, c.g * 1f / 127, c.b * 1f / 127,
                        c.a * 1f / 127);
            }
            mPaint.setColor(color);
            mCanvas.drawPoint(temp.x, temp.y, mPaint);
        }
    }

    @Override
    public void drawParticlesWireframe(Vec2[] centers, float radius, ParticleColor[] colors, int count) {
        for (int i = 0; i < count; i++) {
            getWorldToScreenToOut(centers[i], temp);
            int color;
            if (colors == null) {
                color = Color.RED;
            } else {
                ParticleColor c = colors[i];
                color = getAndroidColor(c.r * 1f / 127, c.g * 1f / 127, c.b * 1f / 127,
                        c.a * 1f / 127);
            }
            mPaint.setColor(color);
            mCanvas.drawPoint(temp.x, temp.y, mPaint);
        }
    }

    private static int getAndroidColor(Color3f color3f) {
        return getAndroidColor(color3f, 1.0f);
    }

    private static int getAndroidColor(Color3f color3f, float alpha) {
        return getAndroidColor(color3f.x, color3f.x, color3f.z, alpha);
    }

    private static int getAndroidColor(float r, float g, float b, float alpha) {
        return ((int) (alpha * 255.0f + 0.5f) << 24)
                | ((int) (r * 255.0f + 0.5f) << 16)
                | ((int) (g * 255.0f + 0.5f) << 8)
                | (int) (b * 255.0f + 0.5f);
    }
}
