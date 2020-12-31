package com.infinite.guide;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.infinite.annotation.ARouter;

@ARouter(path="/guide/GuideMainActivity")
public class GuideMainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_main);
        findViewById(R.id.textv)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        Class<?> clazz=GuideMainActivity$$ARouter.findTargetClass("app/MainActivity");
//                        startActivity(new Intent(GuideMainActivity.this,clazz));
                    }
                });
    }


}
