package com.disk.controller.system;

import com.disk.common.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 * @date 2024/2/25
 */
@RestController
public class AlphaController {

//    @DubboReference
//    private ServiceAPI serviceAPI;

    @Autowired
    private RedisService redisService;


//    @Override
//    public void run(String... args) throws Exception {
//        String result = serviceAPI.sendMsg("world");
//        System.out.println("Receive result ======> " + result);
//
//        new Thread(()-> {
//            while (true) {
//                try {
//                    Thread.sleep(1000);
//                    System.out.println(new Date() + " Receive result ======> " + serviceAPI.sendMsg("world"));
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                    Thread.currentThread().interrupt();
//                }
//            }
//        }).start();
//    }
}
