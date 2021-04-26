package com.netty.task;

import cn.hutool.core.util.ObjectUtil;
import com.netty.server.NettyServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * <p> 启动任务类 </p>
 *
 * @author: ZHT
 * @create: 2021-04-25 16:17
 **/
@Component
@Order(5)
public class StartTask implements CommandLineRunner {

    /**
     * netty服务端
     */
    @Autowired
    private NettyServer nettyServer;

    @Override
    public void run(String... args) throws Exception {
        // 重启服务
        reStart();
    }

    /**
     * 重启线程服务
     */
    private void reStart() {
        // 启动netty服务端
        startNettyServer();
    }

    /**
     * 启动netty服务端
     */
    private void startNettyServer() {
        // 启动netty服务端
        if (ObjectUtil.isNotEmpty(nettyServer)) {
            new Thread(nettyServer).start();
        }
    }


}
