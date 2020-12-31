package com.infinite.modular.test;

import com.infinite.annotation.RouterBean;
import com.infinite.arouter_api.core.ARouterLoadPath;
import com.infinite.guide.GuideMainActivity;

import java.util.HashMap;
import java.util.Map;

public class ARouter$$Path$$guide implements ARouterLoadPath {
    @Override
    public Map<String, RouterBean> loadPath() {
        Map<String ,RouterBean> pathMap=new HashMap<>();
         pathMap.put("guide/GuideMainActivity", RouterBean.create(RouterBean.Type.ACTIVITY,
                 GuideMainActivity.class,
                 "guide",
                 "guide/GuideMainActivity"));
        return pathMap;
    }
}
