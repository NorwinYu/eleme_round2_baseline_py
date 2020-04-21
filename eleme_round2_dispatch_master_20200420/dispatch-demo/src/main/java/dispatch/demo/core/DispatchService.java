package dispatch.demo.core;

import dispatch.demo.core.context.DispatchContext;
import dispatch.demo.core.solver.BaseSolver;
import dispatch.demo.dto.CourierPlan;
import dispatch.demo.dto.DispatchRequest;
import dispatch.demo.dto.DispatchSolution;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author eleme.demo
 */
public class DispatchService {

    Map<String, DispatchContext> serviceContext = new HashMap<>(0);

    public DispatchSolution dispatch(DispatchRequest request) {
        String areaId = request.getAreaId();
        DispatchContext context;
        if (request.isFirstRound()) {
            context = new DispatchContext();
            context.setAreaId(areaId);
            context.setTimeStamp(request.getRequestTime());
            serviceContext.put(areaId, context);
        } else {
            context = serviceContext.get(areaId);
            if (null == context) {
                DispatchSolution emptySolution = new DispatchSolution();
                emptySolution.setCourierPlans(Collections.emptyList());
                return emptySolution;
            } else {
                if (request.isLastRound()) {
                    context.setEndOfTest(true);
                }
            }
            context.refresh(request.getRequestTime());
        }
        context.addOnlineCouriers(request.getCouriers());
        context.addDispatchingOrders(request.getOrders());
        BaseSolver solver = getSolver(context);
        List<CourierPlan> courierPlans = solver.solve();
        courierPlans.forEach(cp -> {
            cp.getPlanRoutes().forEach(a -> a.setSubmitted(true));
        });
        List<String> assignedIds = solver.getAssignedOrderIds();
        context.markAllocatedOrders(assignedIds);
        while (!context.getOrderPool().getDispatchingOrders().isEmpty() && context.isEndOfTest()) {
            long aheadTime = 10 * 60;
            context.setTimeStamp(context.getTimeStamp() + aheadTime);
            BaseSolver lastRoundSolver = getSolver(context);
            List<CourierPlan> tmpPlans = lastRoundSolver.solve();
            courierPlans.addAll(tmpPlans);
            tmpPlans.forEach(cp -> {
                cp.getPlanRoutes().forEach(a -> a.setSubmitted(true));
            });
            context.markAllocatedOrders(lastRoundSolver.getAssignedOrderIds());
        }
        DispatchSolution solution = new DispatchSolution();
        solution.setCourierPlans(courierPlans);
        return solution;
    }

    BaseSolver getSolver(DispatchContext context) {
        return new BaseSolver(context);
    }

}
