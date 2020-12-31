package com.infinite.arouter_api.core;

import java.util.Map;

public interface ARouterLoadGroup {
    Map<String, Class<? extends ARouterLoadPath>> loadGroup();
}
