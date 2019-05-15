package com.liuchen.update;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.diuchen.updateutil.UpdateUtil;

public class MainActivity extends AppCompatActivity {
    private Button updateBtn;
    private TextView logTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        updateBtn = findViewById(R.id.updateBtn);
        logTv = findViewById(R.id.logTv);

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new UpdateUtil.Builder()
                        .setActivity(MainActivity.this)
                        .setUrl("https://my-test-1253832037.cos.ap-chengdu.myqcloud.com/app-release.apk")
                        .setNewVersionCode(2)
                        .setUpdateListener(new UpdateUtil.UpdateListener() {
                            @Override
                            public void upDateStart(long max) {
                                logTv.setText("开始下载");
                            }

                            @Override
                            public void upDateProgress(int progress) {
                                logTv.setText("下载进度:" + progress);
                            }

                            @Override
                            public void upDateComplete(String path, boolean checkSuccess) {
                                logTv.setText("下载完成 路径:" + path);
                            }

                            @Override
                            public void upDateFail(String message) {
                                logTv.setText("下载失败:" + message);
                            }
                        })
                        .build()
                        .start();
            }
        });

    }
}
