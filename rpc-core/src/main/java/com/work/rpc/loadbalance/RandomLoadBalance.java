package com.work.rpc.loadbalance;

import cn.hutool.core.util.RandomUtil;

import java.util.List;

public class RandomLoadBalance implements LoadBalance {

    @Override
    public String select(List<String> list) {
        return RandomUtil.randomEle(list);
    }
}
