package com.infinite.arouter_api;

import android.content.Context;
import android.os.Bundle;

public class BundleManager {

    private Bundle bundle = new Bundle();
    private boolean isResult;

    public boolean isResult() {
        return isResult;
    }

    public BundleManager isResult(boolean result) {
        isResult = result;
        return this;
    }

    public Bundle getBundle() {
        return bundle;
    }

    public BundleManager withString(String name, String value) {
        bundle.putString(name, value);
        return this;
    }

    public BundleManager withInt(String name, int value) {
        bundle.putInt(name, value);
        return this;
    }

    public BundleManager withBoolean(String name, boolean value) {
        bundle.putBoolean(name, value);
        return this;
    }

    public Object navigate(Context context, int code) {
        return ARouterManager.getInstance().navigate(context, this, code);
    }

    public Object navigate(Context context) {
        return navigate(context, -1);
    }
}
