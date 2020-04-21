package dispatch.demo.core.context;

import dispatch.demo.core.pool.CourierPool;
import dispatch.demo.core.pool.OrderPool;
import dispatch.demo.dto.ActionNode;
import dispatch.demo.dto.Courier;
import dispatch.demo.dto.Location;
import dispatch.demo.dto.Order;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author eleme.demo
 */
@Data
public class DispatchContext {
    private String areaId;
    private CourierPool courierPool = new CourierPool();
    private OrderPool orderPool = new OrderPool();
    private long timeStamp;
    private boolean isEndOfTest = false;

    public void addOnlineCouriers(List<Courier> courierList) {
        courierPool.getCouriers().addAll(courierList);
    }

    public void addDispatchingOrders(List<Order> orders) {
        orderPool.addDispatchingOrders(orders);
    }

    public void markAllocatedOrders(List<String> orderIds) {
        orderIds.forEach(id -> orderPool.markAssignedOrder(id));
    }

    public void refresh(long refreshTime) {
        this.timeStamp = refreshTime;
        courierPool.getCouriers().forEach(c -> {
            refreshCourier(c, refreshTime);
        });
    }

    private void refreshCourier(Courier courier, long refreshTime) {
        List<ActionNode> actionNodeList = courier.getPlanRoutes();
        List<ActionNode> refreshNodeList = new ArrayList<>(0);
        for (ActionNode node : actionNodeList) {
            if (node.isSubmitted() && node.getActionTime() <= refreshTime) {
                if (node.getActionType() == 1) {
                    //到店完成
                    orderPool.markArrivalCompleteOrder(node.getOrderId());
                }
                if (node.getActionType() == 2) {
                    //取餐完成
                    orderPool.markPickCompleteOrder(node.getOrderId());
                }
                if (node.getActionType() == 3) {
                    //送达
                    orderPool.markDeliverCompleteOrder(node.getOrderId());
                }
            } else {
                refreshNodeList.add(node);
            }
        }
        List<Order> loadOrders = courier.getOrders().stream()
                .filter(o -> o.getStatus() != 4)
                .collect(Collectors.toList());
        courier.setOrders(loadOrders);
        courier.setPlanRoutes(refreshNodeList);
        if(refreshNodeList.isEmpty() && !actionNodeList.isEmpty()){
            Location latestLoc = orderPool.getOrderMap().get(actionNodeList.get(actionNodeList.size()-1).getOrderId()).getDstLoc();
            courier.setLoc(latestLoc);
        }
    }
}
