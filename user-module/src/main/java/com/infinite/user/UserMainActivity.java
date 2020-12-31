package com.infinite.user;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.infinite.annotation.ARouter;

@ARouter(path="/user/UserMainActivity")
public class UserMainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user__main);
        findViewById(R.id.text)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        Class<?> clazz=GuideMainActivity$$ARouter.findTargetClass("app/MainActivity");
//                        startActivity(new Intent(GuideMainActivity.this,clazz));
                    }
                });
    }


}
