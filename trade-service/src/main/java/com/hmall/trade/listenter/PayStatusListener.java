package com.hmall.trade.listenter;

import com.hmall.trade.domain.po.Order;
import com.hmall.trade.service.IOrderService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PayStatusListener {

    private static final Logger log = LoggerFactory.getLogger(PayStatusListener.class);
    private final IOrderService orderService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "trade.pay.success.queue", durable = "true"),
            exchange = @Exchange(name = "pay.direct"),
            key = "pay.success"
    ))
    public void listenPaySuccess(Long orderId){
        //1.查询订单状态
        Order order = orderService.getById(orderId);
        //2.判断订单状态，是否为未支付
        if (order == null || order.getStatus() != 1) {
            //不做处理
            return;
        }
        log.info("接收支付状态通知成功，订单id：{}", orderId);
        orderService.markOrderPaySuccess(orderId);
    }
}
