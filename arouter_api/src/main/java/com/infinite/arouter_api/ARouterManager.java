package com.infinite.arouter_api;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.LruCache;

import com.infinite.annotation.RouterBean;
import com.infinite.arouter_api.core.ARouterLoadGroup;
import com.infinite.arouter_api.core.ARouterLoadPath;

public class ARouterManager {

    private static final String GROUP_FILE_PREFIX = "ARouter$$Group$$";
    private static ARouterManager instance;

    private String path;
    private String group;

    private LruCache<String, ARouterLoadGroup> groupLruCache;

    private LruCache<String, ARouterLoadPath> pathLruCache;

    private ARouterManager() {
        groupLruCache = new LruCache<>(50);
        pathLruCache = new LruCache<>(50);
    }

    public static ARouterManager getInstance() {
        if (instance == null) {
            synchronized (ARouterManager.class) {
                if (instance == null) {
                    instance = new ARouterManager();
                }
            }
        }
        return instance;
    }

    public BundleManager build(String path) {
        if (TextUtils.isEmpty(path) || !path.startsWith("/")) {
            throw new IllegalArgumentException("未按规范配置，如：/app/MainActivity");
        }
        this.path = path;
        group = getGroup(path);
        return new BundleManager();
    }

    public Object navigate(Context context, BundleManager bundleManager, int code) {
        String groupClassName = context.getPackageName() + ".apt." + GROUP_FILE_PREFIX + group;
        try {
            ARouterLoadGroup loadGroup = groupLruCache.get(group);
            if (loadGroup == null) {
                Class<?> groupClass = Class.forName(groupClassName);
                loadGroup = (ARouterLoadGroup) groupClass.newInstance();
                groupLruCache.put(group, loadGroup);
            }
            ARouterLoadPath loadPath = pathLruCache.get(path);
            if (loadPath == null) {
                Class<? extends ARouterLoadPath> pathClass = loadGroup.loadGroup().get(group);
                loadPath = pathClass.newInstance();
                pathLruCache.put(group, loadPath);
            }
            RouterBean routerBean = loadPath.loadPath().get(path);
            if (routerBean != null) {
                switch (routerBean.getType()) {
                    case ACTIVITY:
                        Intent intent = new Intent(context, routerBean.getClazz());
                        intent.putExtras(bundleManager.getBundle());
                        if (bundleManager.isResult()) {
                            ((Activity) context).setResult(code, intent);
                            ((Activity) context).finish();
                        } else {
                            ((Activity) context).startActivityForResult(intent, code);
                        }

                }
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String getGroup(String path) {

        if (TextUtils.isEmpty(path)) {
            throw new IllegalArgumentException("path 不能为null");
        }

        if (!path.startsWith("/")) {
            throw new IllegalArgumentException("path 不合法");
        }

        if (path.lastIndexOf("/", 1) < 0) {
            throw new IllegalArgumentException("path 不合法");
        }

        String group = path.substring(1, path.indexOf("/", 1));
        if (TextUtils.isEmpty(group)) {
            throw new IllegalArgumentException("group 不能为空");
        }
        return group;
    }
}
