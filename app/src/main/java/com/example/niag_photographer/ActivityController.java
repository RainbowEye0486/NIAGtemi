package com.example.niag_photographer;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class ActivityController extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionbar	= getSupportActionBar();
        if	(actionbar	!=	null) {
            actionbar.hide();//統一隱藏標頭
        }
    }


}
