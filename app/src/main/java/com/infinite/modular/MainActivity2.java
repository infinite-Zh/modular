package com.infinite.modular;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.infinite.annotation.ARouter;
import com.infinite.annotation.RouterBean;
import com.infinite.arouter_api.ARouterManager;
import com.infinite.arouter_api.core.ARouterLoadGroup;
import com.infinite.arouter_api.core.ARouterLoadPath;
import com.infinite.user.UserMainActivity2;

import java.util.Map;

@ARouter(path = "/app/MainActivity2")
public class MainActivity2 extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void jump(View v) {

        ARouterManager
                .getInstance()
                .build("/user/UserMainActivity2")
                .withInt("age", 50)
                .withString("name", "ff")
                .isResult(true)
                .navigate(MainActivity2.this, RESULT_OK)
        ;
    }
}
