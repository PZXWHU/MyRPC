package com.pzx.rpc.test;

import com.pzx.rpc.annotation.Service;
import com.pzx.rpc.api.ByeService;

@Service
public class ByeServiceImpl implements ByeService {

    @Override
    public void bye(String s) {
        System.out.println(s);
    }
}
