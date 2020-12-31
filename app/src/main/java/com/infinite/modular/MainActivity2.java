package com.infinite.modular;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.infinite.annotation.ARouter;
import com.infinite.annotation.RouterBean;
import com.infinite.arouter_api.core.ARouterLoadGroup;
import com.infinite.arouter_api.core.ARouterLoadPath;
import com.infinite.modular.test.ARouter$$Group$$guide;

import java.util.Map;

@ARouter(path = "/app/MainActivity2")
public class MainActivity2 extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void jump(View v) {

        ARouterLoadGroup group = new ARouter$$Group$$guide();
        Map<String, Class<? extends ARouterLoadPath>> map = group.loadGroup();

        Class<? extends ARouterLoadPath> clazz = map.get("guide");
        try {
            ARouterLoadPath path = clazz.newInstance();
            Map<String, RouterBean> path1 = path.loadPath();
            RouterBean bean = path1.get("/guide/GuideMainActivity");
            startActivity(new Intent(this, bean.getClazz()));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }
}
