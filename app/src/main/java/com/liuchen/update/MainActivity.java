package com.liuchen.update;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.diuchen.updateutil.UpdateUtil;

public class MainActivity extends AppCompatActivity {
    private Button updateBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        updateBtn = findViewById(R.id.updateBtn);
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new UpdateUtil.Builder()
                        .setActivity(MainActivity.this)
                        .setUrl("https://send-message-1253832037.cos.ap-chengdu.myqcloud.com/MessageToHer/%E5%B0%8F%E5%8F%AF%E7%88%B1%E7%9A%84%E4%B8%93%E5%B1%9E%E6%97%A5%E5%8E%86.apk")
                        //.setUrl("https://my-test-1253832037.cos.ap-chengdu.myqcloud.com/%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20190416155556.jpg")
                        .build()
                        .start();
            }
        });
    }
}
