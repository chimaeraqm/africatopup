package com.crazydwarf.africatopup.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import com.crazydwarf.africatopup.R;
import com.crazydwarf.africatopup.view.SimpleToolBar;

public class TxtDisplayActivity extends AppCompatActivity
{
    private TextView tvInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_txt_display);
        SimpleToolBar toolBar = findViewById(R.id.top_menu);
        toolBar.setBackIconClickListener(new SimpleToolBar.BackIconClickListener() {
            @Override
            public void OnClick() {
                finish();
            }
        });

        Intent intent = getIntent();
        String info = intent.getStringExtra("OPERATOR_INFO");
        if(info == null)
        {
            info = "NULL";
        }
        tvInfo = findViewById(R.id.tv_info);
        tvInfo.setText(info);
        tvInfo.setMovementMethod(ScrollingMovementMethod.getInstance());
    }
}
