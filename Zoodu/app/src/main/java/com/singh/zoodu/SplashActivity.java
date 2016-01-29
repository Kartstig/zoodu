package com.singh.zoodu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Runnable launch_main = new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    goHome();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        launch_main.run();

    }

    private void goHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
