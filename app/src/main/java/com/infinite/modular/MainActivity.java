package com.infinite.modular;

import android.content.Intent;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.infinite.annotation.ARouter;
import com.infinite.annotation.RouterBean;
import com.infinite.arouter_api.ARouterManager;
import com.infinite.arouter_api.core.ARouterLoadGroup;
import com.infinite.arouter_api.core.ARouterLoadPath;
import com.infinite.common.user.call.UserCall;
import com.infinite.common.view.BigView;
import com.infinite.modular.apt.ARouter$$Group$$guide;
import com.infinite.modular.apt.ARouter$$Group$$user;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@ARouter(path = "/app/MainActivity")
public class MainActivity extends AppCompatActivity {
    String name;

    int age = 9;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name = getIntent().getStringExtra("name");
        age = getIntent().getIntExtra("age", age);

        try {
//            InputStream is=getAssets().open("long.jpeg");
            InputStream is=getAssets().open("world.jpg");
            ((BigView)findViewById(R.id.bigView)).setImageStream(is);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void jump(View v) {
        ARouterManager.getInstance()
                .build("/user/UserMainActivity2")
                .withString("name","lf")
                .withInt("agex",2000)
                .navigate(this,100);

        UserCall userCall = (UserCall) ARouterManager.getInstance()
                .build("/user/UserCallImpl")
                .navigate(this);

        Log.e("user", userCall.getUserInfo("0").toString());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==100){
            Bundle bundle=data.getExtras();
            Log.e("name", bundle.getString("name"));
            Log.e("age", bundle.getInt("age")+"");
        }
    }
}
