package com.hmall.api.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient("trade-service")
public interface OrderClient {
    @PutMapping("/orders/{orderId}")
    void updateOrderStatus(@PathVariable("orderId") Long orderId);
}
