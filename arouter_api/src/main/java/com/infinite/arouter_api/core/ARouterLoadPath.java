package com.infinite.arouter_api.core;

import com.infinite.annotation.RouterBean;

import java.util.Map;

/**
 * 路由组group对应的详细path数据接口
 * 比如：app分组，对应有哪些类需要加载
 */
public interface ARouterLoadPath {

    Map<String, RouterBean> loadPath();
}
