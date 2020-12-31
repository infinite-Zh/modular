package com.infinite.modular.test;

import com.infinite.arouter_api.core.ARouterLoadGroup;
import com.infinite.arouter_api.core.ARouterLoadPath;

import java.util.HashMap;
import java.util.Map;

public class ARouter$$Group$$guide implements ARouterLoadGroup {
    @Override
    public Map<String, Class<? extends ARouterLoadPath>> loadGroup() {
        Map<String,Class<? extends ARouterLoadPath>> groupMap=new HashMap<>();
        groupMap.put("guide",ARouter$$Path$$guide.class);
        return groupMap;
    }
}
