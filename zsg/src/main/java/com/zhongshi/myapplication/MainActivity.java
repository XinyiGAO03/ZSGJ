package com.zhongshi.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private LinearLayout tabHome, tabFriends, tabAdd, tabMessage, tabMine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_interface);

        tabHome = findViewById(R.id.tabHome);
        tabFriends = findViewById(R.id.tabFriends);
        tabAdd = findViewById(R.id.tabAdd);
        tabMessage = findViewById(R.id.tabMessage);
        tabMine = findViewById(R.id.tabMine);

        tabHome.setOnClickListener(v -> Toast.makeText(this, "主页", Toast.LENGTH_SHORT).show());
        tabFriends.setOnClickListener(v -> Toast.makeText(this, "朋友", Toast.LENGTH_SHORT).show());
        tabMessage.setOnClickListener(v -> Toast.makeText(this, "消息", Toast.LENGTH_SHORT).show());
        tabMine.setOnClickListener(v -> Toast.makeText(this, "我的", Toast.LENGTH_SHORT).show());

        // 关键修复：不要在主页面直接找不存在的相机控件；点击 + 跳转到发布商品页。
        tabAdd.setOnClickListener(v -> startActivity(new Intent(this, PostProductActivity.class)));
    }
}
