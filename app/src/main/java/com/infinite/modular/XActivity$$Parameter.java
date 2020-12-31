package com.infinite.modular;

import com.infinite.arouter_api.core.ParameterLoad;

class XActivity$$Parameter implements ParameterLoad {
    @Override
    public void loadParameter(Object target) {
        MainActivity t = (MainActivity) target;

        t.name = t.getIntent().getStringExtra("name");
        t.age = t.getIntent().getIntExtra("age", t.age);
    }
}
