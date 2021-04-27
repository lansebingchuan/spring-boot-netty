package com.netty.task;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.netty.client.NettyClientServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

/**
 * <p> 启动任务类 </p>
 *
 * @author: ZHT
 * @create: 2021-04-25 16:17
 **/
@Slf4j
@Component
@Order(5)
public class StartNettyClientTask implements CommandLineRunner {

    /**
     * netty客户端端
     */
    @Autowired
    private NettyClientServer clientServer;

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
        CompletableFuture.runAsync(this::startNettyServer);

    }

    /**
     * 启动netty服务端
     */
    @Async
    public void startNettyServer() {
        // 启动netty服务端
        if (ObjectUtil.isNotEmpty(clientServer)) {
            new Thread(clientServer).start();
            @SuppressWarnings("resource")
            Scanner scanner = new Scanner(System.in);
            while(true) {
                System.out.print("请输入发送消息：");
                String msg = scanner.nextLine();
                if (StrUtil.equals(msg, "n")) {
                    break;
                }else {
                    clientServer.sendMsg(msg);
                }
                System.out.println();
            }
        }
    }


}
