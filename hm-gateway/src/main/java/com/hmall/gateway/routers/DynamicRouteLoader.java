package com.hmall.gateway.routers;

import cn.hutool.json.JSONUtil;
import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;

@Slf4j
@Component
@RequiredArgsConstructor
public class DynamicRouteLoader {

    //nacos配置管理
    private final NacosConfigManager nacosConfigManager;

    //路由发布者，用于更新路由配置
    private final RouteDefinitionWriter routeDefinitionWriter;

    //nacos中的配置文件名称
    private final String dataId = "gateway-routes.json";

    //nacos的路由配置文件所在的组名
    private final String group = "DEFAULT_GROUP";

    //路由id集合，使用set是为了保证id的唯一性
    private final Set<String> routeIds = new HashSet<>();

    //在项目初始化后执行
    @PostConstruct
    public void initRouteConfigListener() throws NacosException {
        //项目启动，先拉取一次配置，并添加配置监听器
        String configInfo = nacosConfigManager.getConfigService()//使用配置管理器获取配置服务，再为特定配置注册一个监听器
                .getConfigAndSignListener(dataId, group, 5000, new Listener() {
                    @Override
                    public Executor getExecutor() {
                        return null;
                    }

                    @Override
                    public void receiveConfigInfo(String configInfo) {
                        // 2.监听到配置变更，需要去更新路由表
                        updateConfigInfo(configInfo);
                    }
                });
        // 3.第一次读取到配置，也需要更新到路由表
        updateConfigInfo(configInfo);
    }

    public void updateConfigInfo(String configInfo) {
        log.debug("监听到路由配置信息：{}", configInfo);
        //1.解析配置信息（json格式），转为RouteDefinition
        List<RouteDefinition> routeDefinitions = JSONUtil.toList(configInfo, RouteDefinition.class);

        //2.删除旧路由表
        for (String routeId : routeIds) {
            routeDefinitionWriter.delete(Mono.just(routeId));
        }
        //清空以便后续写入
        routeIds.clear();

        //3.写入新的路由表
        for(RouteDefinition routeDefinition : routeDefinitions) {
            //更新路由表
            routeDefinitionWriter.save(Mono.just(routeDefinition)).subscribe();
            //记录路由id，便于下一次更新时删除
            routeIds.add(routeDefinition.getId());
        }
    }
}
