from .dto import ActionNode, Courier, Order, Location
from .context import DispatchContext
from .util import DistanceUtils
from typing import Dict, List


class TailAppendPlan():
    def plan(self, courier: Courier, order: Order, context: DispatchContext) -> List[ActionNode]:
        if len(courier.orders) == 0:
            loc = courier.loc
            planTime = context.timeStamp
        else:
            lastNode = courier.planRoutes[-1]
            lastOrder = context.orderPool.orderMap[lastNode.orderId]
            loc = lastOrder.dstLoc
            planTime = lastNode.actionTime
        tailPlans = self.planOneOrder(courier, loc, planTime, order)
        appendPlans = []
        appendPlans += courier.planRoutes
        appendPlans += tailPlans
        return appendPlans

    def planOneOrder(self, courier: Courier, loc: Location, planTime, order: Order):
        distanceUtils = DistanceUtils()
        arrivalTime = planTime + distanceUtils.timeConsuming(loc, order.srcLoc, courier.speed)
        pickTime = max(order.estimatedPrepareCompletedTime, arrivalTime)
        deliverTime = pickTime + distanceUtils.timeConsuming(order.srcLoc, order.dstLoc, courier.speed)
        arrivalNode = ActionNode(1, order.id, arrivalTime, False, planTime)
        pickNode = ActionNode(2, order.id, pickTime, False, arrivalTime)
        deliveryNode = ActionNode(3, order.id, deliverTime, False, pickTime)
        return [arrivalNode, pickNode, deliveryNode]