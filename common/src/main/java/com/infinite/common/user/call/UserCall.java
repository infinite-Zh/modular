package com.infinite.common.user.call;

import com.infinite.arouter_api.core.Call;
import com.infinite.common.user.bean.User;

public interface UserCall extends Call {
    User getUserInfo(String id);
}
