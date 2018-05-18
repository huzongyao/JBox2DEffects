package com.hzy.jbox2d.testbed;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.blankj.utilcode.util.SPUtils;

import org.jbox2d.testbed.framework.TestbedController;
import org.jbox2d.testbed.framework.TestbedModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TestBedActivity extends AppCompatActivity {

    private static final String PREF_CUR_TEST_INDEX = "PREF_CUR_TEST_INDEX";

    @BindView(R.id.test_bed_view)
    TestBedView mTestBedView;
    @BindView(R.id.test_case_spinner)
    Spinner mTestCaseSpinner;
    @BindView(R.id.test_label)
    TextView mTestLabel;

    private TestbedController mTestBedController;
    private List<TestbedModel.ListItem> mTestItemList;
    private int mCurTestIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_bed);
        ButterKnife.bind(this);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mTestBedController = mTestBedView.getTestBedController();
        mTestItemList = mTestBedView.getTestItems();
        initSpinner();
        mCurTestIndex = SPUtils.getInstance().getInt(PREF_CUR_TEST_INDEX, 0);
        mTestCaseSpinner.setSelection(mCurTestIndex);
    }

    /**
     * init the spinner
     */
    private void initSpinner() {
        mTestCaseSpinner.setAdapter(new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, mTestItemList));
        mTestCaseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCurTestIndex = position;
                mTestBedController.playTest(position);
                TestbedModel.ListItem curItem = mTestItemList.get(position);
                setTitle(curItem.test.getTestName());
                mTestLabel.setText(String.format("%s:%s", curItem.category, curItem.test.getTestName()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // save the selected index
        SPUtils.getInstance().put(PREF_CUR_TEST_INDEX, mCurTestIndex);
    }
}
