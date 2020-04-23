from .dto import DispatchRequest, DispatchSolution, ActionNode, CourierPlan
from .context import DispatchContext
from typing import Dict, List
from .solver import BaseSolver

class DispatchService:
    def __init__(self):
        self.serviceContext: Dict[str, DispatchContext] = {}

    def dispatch(self, request: DispatchRequest):
        areaId = request.areaId
        if request.isFirstRound:
            context = DispatchContext(areaId, request.requestTimestamp)
            self.serviceContext[areaId] = context
        else:
            context = self.serviceContext.get(areaId)
            if context is None:
                emptySolution = DispatchSolution([])
                return emptySolution
            else:
                if request.isLastRound:
                    context.setIsEndOfTest(True)
            context.refresh(request.requestTimestamp)

        context.addOnlineCouriers(request.couriers)
        context.addDispatchingOrders(request.orders)
        solver = self.getSolver(context)
        courierPlans = solver.solve()
        for cp in courierPlans:
            for a in cp.planRoutes:
                a.setSubmitted(True)
        assignedIds = solver.getAssignedOrderIds()
        context.markAllocatedOrders(assignedIds)
        while len(context.orderPool.getDispatchingOrders()) != 0 and context.isEndOfTest:
            aheadTime = 10 * 60
            context.setTimeStamp(context.timeStamp + aheadTime)
            lastRoundSolver = self.getSolver(context)
            tmpPlans = lastRoundSolver.solve()
            for cp in tmpPlans:
                for a in cp.planRoutes:
                    a.setSubmitted(True)
            context.markAllocatedOrders(lastRoundSolver.getAssignedOrderIds())
        solution = DispatchSolution(courierPlans)
        return solution

    def getSolver(self, context: DispatchContext) -> BaseSolver:
        return BaseSolver(context)



