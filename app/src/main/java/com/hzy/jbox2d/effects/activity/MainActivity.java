package com.hzy.jbox2d.effects.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.hzy.jbox2d.effects.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.button_simple_body)
    public void onMButtonSimpleClicked() {
        startActivity(new Intent(this, SimpleBodyActivity.class));
    }

    @OnClick(R.id.button_endless_ball)
    public void onMButtonSimpleBallClicked() {
        startActivity(new Intent(this, EndlessBallActivity.class));
    }

    @OnClick(R.id.button_mobike)
    public void onMButtonMobikeClicked() {
        startActivity(new Intent(this, MoBikeActivity.class));
    }
}
