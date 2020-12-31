package com.infinite.arouter_api;

import android.app.Activity;
import android.util.LruCache;

import com.infinite.arouter_api.core.ParameterLoad;

public class ParameterManager {

    private static final String FILE_NAME_SUFFIX ="$$Parameter";
    private static ParameterManager instance;

    private LruCache<String, ParameterLoad> lruCache;

    private ParameterManager(){
        lruCache =new LruCache<>(200);
    }

    public static ParameterManager getInstance(){
        if (instance==null){
            synchronized (ParameterManager.class){
                if (instance==null){
                    instance=new ParameterManager();
                }
            }
        }
        return instance;
    }

    public void loadParameter(Object target){
        if (target instanceof Activity){
            String className=((Activity) target).getClass().getName();
            ParameterLoad parameterLoad= lruCache.get(className);
            if (parameterLoad==null){
                try {
                    Class<?> clazz=Class.forName(className+ FILE_NAME_SUFFIX);
                    parameterLoad= (ParameterLoad) clazz.newInstance();
                    lruCache.put(className, parameterLoad);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                }

            }
            parameterLoad.loadParameter(target);
        }

    }
}
