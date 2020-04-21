package dispatch.demo.core.route;

import dispatch.demo.core.context.DispatchContext;
import dispatch.demo.dto.ActionNode;
import dispatch.demo.dto.Courier;
import dispatch.demo.dto.Order;

import java.util.List;

/**
 * @author eleme.demo
 * route planner interface
 */
public interface Planner {

    /**
     * 路径规划
     *
     * @param courier   给定骑手
     * @param order     待规划的单
     * @param context   上下文信息
     * @return 骑手路径
     */
    public List<ActionNode> plan(Courier courier, Order order, DispatchContext context);
}
