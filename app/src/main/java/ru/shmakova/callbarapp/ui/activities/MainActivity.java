package ru.shmakova.callbarapp.ui.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;
import android.widget.ToggleButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import ru.shmakova.callbarapp.R;
import ru.shmakova.callbarapp.services.CallService;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.toggle_service_button)
    ToggleButton toggleServiceButton;

    private Intent callService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnCheckedChanged(R.id.toggle_service_button)
    public void onCheckedChange(boolean isChecked) {
        if (isChecked) {
            callService = new Intent(this, CallService.class);
            startService(callService);
        } else {
            stopService(callService);
        }

    }
}
