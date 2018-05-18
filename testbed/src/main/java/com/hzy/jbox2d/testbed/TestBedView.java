package com.hzy.jbox2d.testbed;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import org.jbox2d.testbed.framework.AbstractTestbedController;
import org.jbox2d.testbed.framework.TestList;
import org.jbox2d.testbed.framework.TestbedController;
import org.jbox2d.testbed.framework.TestbedErrorHandler;
import org.jbox2d.testbed.framework.TestbedModel;
import org.jbox2d.testbed.framework.TestbedPanel;

import java.util.List;

/**
 * Created by huzongyao on 2018/5/14.
 */

public class TestBedView extends SurfaceView
        implements SurfaceHolder.Callback, TestbedPanel {

    private TestbedModel mTestModel;
    private TestbedController mController;
    private AndroidDebugDraw mDebugDraw;
    private SurfaceHolder mSurfaceHolder;
    private boolean mEnableDraw;
    private Canvas mCanvas;

    public TestBedView(Context context) {
        this(context, null);
    }

    public TestBedView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TestBedView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        mTestModel = new TestbedModel();
        mController = new TestbedController(mTestModel,
                AbstractTestbedController.UpdateBehavior.UPDATE_CALLED,
                AbstractTestbedController.MouseBehavior.NORMAL,
                new TestbedErrorHandler() {
                    @Override
                    public void serializationError(Exception e, String message) {
                    }
                });
        mTestModel.setPanel(this);
        mDebugDraw = new AndroidDebugDraw();
        mTestModel.setDebugDraw(mDebugDraw);
        TestList.populateModel(mTestModel);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //set the screen size
        mController.updateExtents(w / 2, h / 2);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mEnableDraw = true;
        // start animation thread
        mController.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mController.stop();
        mEnableDraw = false;
    }

    @Override
    public void grabFocus() {

    }

    @Override
    public boolean render() {
        //request to draw something
        if (mEnableDraw) {
            mCanvas = mSurfaceHolder.lockCanvas();
            if (mCanvas != null) {
                mCanvas.drawColor(Color.BLACK);
                mDebugDraw.setCanvas(mCanvas);
                return true;
            }
        }
        return false;
    }

    @Override
    public void paintScreen() {
        // after all components drew
        if (mCanvas != null) {
            mSurfaceHolder.unlockCanvasAndPost(mCanvas);
        }
    }

    public List<TestbedModel.ListItem> getTestItems() {
        return mTestModel.getTestItems();
    }

    public TestbedController getTestBedController() {
        return this.mController;
    }
}
