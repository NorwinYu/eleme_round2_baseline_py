package dispatch.demo.core.solver;

import dispatch.demo.core.context.DispatchContext;
import dispatch.demo.core.route.Planner;
import dispatch.demo.core.route.TailAppendPlan;
import dispatch.demo.dto.ActionNode;
import dispatch.demo.dto.Courier;
import dispatch.demo.dto.CourierPlan;
import dispatch.demo.dto.Order;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author eleme.demo
 */

public class BaseSolver {

    /** 两次调度最短间隔时间 */
    private static final long MINIMUM_INTERVAL_SECONDS = 60;

    List<Order> orders;
    List<Courier> couriers;
    DispatchContext context;

    boolean[] ordersAssigned;
    Cost[][] costTable;

    /** 路径规划:新单永远规划在末尾 */
    Planner planner = new TailAppendPlan();

    public BaseSolver(DispatchContext context) {
        this.context = context;
        this.orders = getCandidateOrders(context);
        this.couriers = getCandidateCouriers(context);
        this.ordersAssigned = new boolean[this.orders.size()];
    }

    protected List<Courier> getCandidateCouriers(DispatchContext dispatchContext) {
        return dispatchContext.getCourierPool().getCouriers();
    }

    protected List<Order> getCandidateOrders(DispatchContext dispatchContext) {
        return dispatchContext.getOrderPool().getDispatchingOrders();
    }

    public List<String> getAssignedOrderIds() {
        return IntStream.range(0, this.orders.size())
                .filter(i -> this.ordersAssigned[i])
                .mapToObj(j -> this.orders.get(j).getId()).collect(Collectors.toList());
    }

    private void initTable() {
        int courierSize = couriers.size();
        int orderSize = orders.size();
        costTable = new Cost[courierSize][];
        for (int i = 0; i < courierSize; i++) {
            costTable[i] = new Cost[orderSize];
            for (int j = 0; j < orderSize; j++) {
                costTable[i][j] = getCost(i, j);
            }
        }
    }

    public List<CourierPlan> solve() {
        initTable();
        while (true) {
            Cost cost = getBest();
            if (null == cost) {
                break;
            }
            dealWithCost(cost);
        }
        List<CourierPlan> results = new ArrayList<>(0);
        for (Courier courier : couriers) {
            CourierPlan submitPlan = getSubmitPlan(courier);
            if (!submitPlan.getPlanRoutes().isEmpty()) {
                results.add(submitPlan);
            }
        }
        return results;
    }


    private CourierPlan getSubmitPlan(Courier courier) {
        long submitThresholdTime = this.context.getTimeStamp() + MINIMUM_INTERVAL_SECONDS;
        List<ActionNode> submittedNodes = courier.getPlanRoutes().stream()
                .filter(node -> !node.isSubmitted())
                .filter(node -> node.getNeedSubmitTime() <= submitThresholdTime || context.isEndOfTest())
                .collect(Collectors.toList());
        CourierPlan plan = new CourierPlan();
        plan.setCourierId(courier.getId());
        plan.setPlanRoutes(submittedNodes);
        return plan;
    }

    private void dealWithCost(Cost cost) {
        cost.getCourier().setPlanRoutes(cost.getPlanActionNodes());
        cost.getCourier().getOrders().add(cost.getOrder());
        ordersAssigned[cost.j] = true;
        updateWeightRow(cost.i);
        updateWeightCol(cost.j);
    }

    private void updateWeightRow(int i) {
        for (int j = 0; j < orders.size(); ++j) {
            costTable[i][j] = getCost(i, j);
        }
    }

    private void updateWeightCol(int j) {
        for (int i = 0; i < couriers.size(); ++i) {
            costTable[i][j] = getCost(i, j);
        }
    }

    Cost getBest() {
        Cost best = null;
        int courierSize = couriers.size();
        int orderSize = orders.size();
        for (int i = 0; i < courierSize; i++) {
            for (int j = 0; j < orderSize; j++) {
                Cost tmpC = costTable[i][j];
                if (null == tmpC) {
                    continue;
                }
                if (null == best) {
                    best = tmpC;
                    continue;
                }
                if (costLess(tmpC, best)) {
                    best = tmpC;
                }
            }
        }
        return best;
    }

    protected boolean costLess(Cost c1, Cost c2) {
        return c1.getCost() <= c2.getCost();
    }

    protected Cost getCost(int i, int j) {
        if (ordersAssigned[j]) {
            return null;
        }
        Cost cost = new Cost(i, j);
        if (!cost.isValid()) {
            return null;
        }
        return cost;
    }

    @Data
    public class Cost {
        int i, j;
        Courier courier;
        Order order;
        double cost;
        List<ActionNode> planActionNodes;

        Cost(int i, int j) {
            this.i = i;
            this.j = j;
            this.courier = couriers.get(i);
            this.order = orders.get(j);
            this.planActionNodes = planner.plan(courier, order, context);
            this.cost = calCost();
        }

        boolean isValid() {
            int maxLoad = courier.getMaxLoads();
            int cr = (int) courier.getOrders().stream().filter(c -> c.getStatus() == 3).count();
            for (ActionNode node : planActionNodes) {
                if (node.getActionType() == 2) {
                    if (++cr > maxLoad) {
                        return false;
                    }
                }
                if (node.getActionType() == 3) {
                    --cr;
                }
            }
            return true;
        }

        /**
         * 此处是一个最简单的cost函数例子，谁能最快送达就给谁
         */
        protected double calCost() {
            double cost = Double.MAX_VALUE;
            if (!isValid()) {
                return Double.MAX_VALUE;
            }
            for (ActionNode node : this.planActionNodes) {
                if (node.getActionType() == 3 && node.getOrderId().equals(order.getId())) {
                    cost = node.getActionTime();
                    break;
                }
            }
            return cost;
        }

        double getCost() {
            return cost;
        }
    }
}
