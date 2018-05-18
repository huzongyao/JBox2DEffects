/*******************************************************************************
 * Copyright (c) 2013, Daniel Murphy
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package org.jbox2d.testbed.framework;

import org.jbox2d.callbacks.DebugDraw;
import org.jbox2d.common.IViewportTransform;

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

/**
 * Model for the testbed
 *
 * @author Daniel
 */
public class TestbedModel {

    private final TestbedSettings settings = new TestbedSettings();
    private final Vector<TestChangedListener> listeners = new Vector<TestChangedListener>();
    private final boolean[] keys = new boolean[512];
    private final boolean[] codedKeys = new boolean[512];
    private DebugDraw draw;
    private TestbedTest test;
    private float calculatedFps;
    private int currTestIndex = -1;
    private TestbedTest runningTest;
    private List<String> implSpecificHelp;
    private TestbedPanel panel;
    private WorldCreator worldCreator = new DefaultWorldCreator();
    private List<ListItem> testItems = new LinkedList<>();
    private String mCurrentCategory;

    public TestbedModel() {
    }

    public List<ListItem> getTestItems() {
        return testItems;
    }

    public WorldCreator getWorldCreator() {
        return worldCreator;
    }

    public void setWorldCreator(WorldCreator worldCreator) {
        this.worldCreator = worldCreator;
    }

    public TestbedPanel getPanel() {
        return panel;
    }

    public void setPanel(TestbedPanel panel) {
        this.panel = panel;
    }

    public List<String> getImplSpecificHelp() {
        return implSpecificHelp;
    }

    public void setImplSpecificHelp(List<String> implSpecificHelp) {
        this.implSpecificHelp = implSpecificHelp;
    }

    public float getCalculatedFps() {
        return calculatedFps;
    }

    public void setCalculatedFps(float calculatedFps) {
        this.calculatedFps = calculatedFps;
    }

    public void setViewportTransform(IViewportTransform transform) {
        draw.setViewportTransform(transform);
    }

    public DebugDraw getDebugDraw() {
        return draw;
    }

    public void setDebugDraw(DebugDraw argDraw) {
        draw = argDraw;
    }

    public TestbedTest getCurrTest() {
        return test;
    }

    public boolean[] getKeys() {
        return keys;
    }

    public boolean[] getCodedKeys() {
        return codedKeys;
    }

    public int getCurrTestIndex() {
        return currTestIndex;
    }

    public void setCurrTestIndex(int argCurrTestIndex) {
        if (argCurrTestIndex < 0) {
            throw new IllegalArgumentException("Invalid test index");
        }
        if (currTestIndex == argCurrTestIndex) {
            return;
        }
        if (!isTestAt(argCurrTestIndex)) {
            throw new IllegalArgumentException("No test at " + argCurrTestIndex);
        }
        currTestIndex = argCurrTestIndex;
        ListItem item = testItems.get(argCurrTestIndex);
        test = item.test;
        for (TestChangedListener listener : listeners) {
            listener.testChanged(test, currTestIndex);
        }
    }

    public TestbedTest getRunningTest() {
        return runningTest;
    }

    public void setRunningTest(TestbedTest runningTest) {
        this.runningTest = runningTest;
    }

    public void addTestChangeListener(TestChangedListener argListener) {
        listeners.add(argListener);
    }

    public void removeTestChangeListener(TestChangedListener argListener) {
        listeners.remove(argListener);
    }

    public void addTest(TestbedTest argTest) {
        testItems.add(new ListItem(argTest, mCurrentCategory));
    }

    public void setCategory(String argName) {
        mCurrentCategory = argName;
    }

    public TestbedTest getTestAt(int argIndex) {
        ListItem item = testItems.get(argIndex);
        if (item.isCategory()) {
            return null;
        }
        return item.test;
    }

    public boolean isTestAt(int argIndex) {
        ListItem item = testItems.get(argIndex);
        return !item.isCategory();
    }

    public void clearTestList() {
        testItems.clear();
    }

    public int getTestsSize() {
        return testItems.size();
    }

    public TestbedSettings getSettings() {
        return settings;
    }

    public interface TestChangedListener {
        void testChanged(TestbedTest test, int index);
    }

    public static class ListItem {
        public String category;
        public TestbedTest test;

        public ListItem(String argCategory) {
            category = argCategory;
        }

        public ListItem(TestbedTest argTest, String category) {
            test = argTest;
            this.category = category;
        }

        public boolean isCategory() {
            return test == null;
        }

        @Override
        public String toString() {
            return isCategory() ? category : test.getTestName();
        }
    }
}
