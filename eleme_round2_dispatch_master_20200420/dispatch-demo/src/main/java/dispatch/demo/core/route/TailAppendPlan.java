package dispatch.demo.core.route;

import dispatch.demo.core.context.DispatchContext;
import dispatch.demo.dto.ActionNode;
import dispatch.demo.dto.Courier;
import dispatch.demo.dto.Location;
import dispatch.demo.dto.Order;
import dispatch.demo.utils.DistanceUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author eleme.demo
 * the courier always append new order to tail
 */
public class TailAppendPlan implements Planner {

    @Override
    public List<ActionNode> plan(Courier courier, Order order, DispatchContext context) {
        Location loc;
        long planTime;
        if (courier.getOrders().isEmpty()) {
            loc = courier.getLoc();
            planTime = context.getTimeStamp();
        } else {
            int size = courier.getPlanRoutes().size();
            ActionNode lastNode = courier.getPlanRoutes().get(size - 1);
            Order lastOrder = context.getOrderPool().getOrderMap().get(lastNode.getOrderId());
            loc = lastOrder.getDstLoc();
            planTime = lastNode.getActionTime();
        }
        List<ActionNode> tailPlans = planOneOrder(courier, loc, planTime, order);
        List<ActionNode> appendPlans = new ArrayList<>(courier.getPlanRoutes().size() + tailPlans.size());
        appendPlans.addAll(courier.getPlanRoutes());
        appendPlans.addAll(tailPlans);
        return appendPlans;
    }

    private List<ActionNode> planOneOrder(Courier courier, Location loc, long planTime, Order order) {
        long arrivalTime = planTime + DistanceUtils.timeConsuming(loc, order.getSrcLoc(), courier.getSpeed());
        long pickTime = Math.max(order.getEstimatedPrepareCompletedTime(), arrivalTime);
        long deliverTime = pickTime + DistanceUtils.timeConsuming(order.getSrcLoc(), order.getDstLoc(), courier.getSpeed());
        ActionNode arrivalNode = new ActionNode(order.getId(), 1, arrivalTime, false, planTime);
        ActionNode pickNode = new ActionNode(order.getId(), 2, pickTime, false, arrivalTime);
        ActionNode deliveryNode = new ActionNode(order.getId(), 3, deliverTime, false, pickTime);
        return Arrays.asList(arrivalNode, pickNode, deliveryNode);
    }
}
