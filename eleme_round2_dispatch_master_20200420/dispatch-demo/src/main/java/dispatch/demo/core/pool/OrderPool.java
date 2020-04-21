package dispatch.demo.core.pool;

import dispatch.demo.dto.Order;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author eleme.demo
 */

public class OrderPool {

    private List<Order> orders;

    @Getter
    private HashMap<String, Order> orderMap;

    public OrderPool() {
        orders = new ArrayList<>(0);
        orderMap = new HashMap<>(0);
    }

    public void addDispatchingOrders(List<Order> dispatchingOrders) {
        this.orders.addAll(dispatchingOrders);
        for (Order order : orders) {
            orderMap.put(order.getId(), order);
        }
    }

    public List<Order> getDispatchingOrders() {
        return orders.stream().filter(o -> o.getStatus() == 0).collect(Collectors.toList());
    }

    public void markAssignedOrder(String orderId){
        orderMap.get(orderId).setStatus(1);
    }

    public void markArrivalCompleteOrder(String orderId){
        orderMap.get(orderId).setStatus(2);
    }

    public void markPickCompleteOrder(String orderId){
        orderMap.get(orderId).setStatus(3);
    }

    public void markDeliverCompleteOrder(String orderId){
        orderMap.get(orderId).setStatus(4);
    }

}
