package com.infinite.modular;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.infinite.annotation.ARouter;
import com.infinite.annotation.RouterBean;
import com.infinite.arouter_api.ARouterManager;
import com.infinite.arouter_api.core.ARouterLoadGroup;
import com.infinite.arouter_api.core.ARouterLoadPath;
import com.infinite.modular.apt.ARouter$$Group$$guide;
import com.infinite.modular.apt.ARouter$$Group$$user;

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

    }

    public void jump(View v) {
        ARouterManager.getInstance()
                .build("/user/UserMainActivity2")
                .withString("name","lf")
                .withInt("agex",20)
                .navigate(this,100);
//        ARouterLoadGroup group = new ARouter$$Group$$user();
//        Map<String, Class<? extends ARouterLoadPath>> map = group.loadGroup();

//        Class<? extends ARouterLoadPath> clazz = map.get("user");
//        try {
//            ARouterLoadPath path = clazz.newInstance();
//            Map<String, RouterBean> path1 = path.loadPath();
//            RouterBean bean = path1.get("/user/UserMainActivity2");
//            Intent intent = new Intent(this, bean.getClazz());
//            intent.putExtra("agex", 10);
//            intent.putExtra("name", "lf");
//            startActivity(intent);
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InstantiationException e) {
//            e.printStackTrace();
//        }
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
