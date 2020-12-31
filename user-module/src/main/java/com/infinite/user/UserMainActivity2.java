package com.infinite.user;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.infinite.annotation.ARouter;
import com.infinite.annotation.Parameter;
import com.infinite.arouter_api.ARouterManager;
import com.infinite.arouter_api.BundleManager;
import com.infinite.arouter_api.ParameterManager;

@ARouter(path = "/user/UserMainActivity2")
public class UserMainActivity2 extends AppCompatActivity {

    @Parameter
    String name;

    @Parameter(name = "agex")
    int age;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user__main);
//        new Parameter$$UserMainActivity2().loadParameter(this);
        ParameterManager.getInstance().loadParameter(this);
        findViewById(R.id.text)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ARouterManager
                                .getInstance()
                                .build("/user/UserMainActivity2")
                                .withInt("age", 50)
                                .withString("name", "ff")
                                .isResult(true)
                                .navigate(UserMainActivity2.this, RESULT_OK)
                        ;
                    }
                });

        Toast.makeText(this, "UserMainActivity2:name=" + name + ",age=" + age, Toast.LENGTH_LONG).show();
    }


}
