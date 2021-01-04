package com.infinite.user.impl;

import com.infinite.annotation.ARouter;
import com.infinite.common.user.bean.User;
import com.infinite.common.user.call.UserCall;

@ARouter(path = "/user/UserCallImpl")
public class UserCallImpl implements UserCall {
    @Override
    public User getUserInfo(String id) {
        return new User("1", "lf", 24);
    }
}
