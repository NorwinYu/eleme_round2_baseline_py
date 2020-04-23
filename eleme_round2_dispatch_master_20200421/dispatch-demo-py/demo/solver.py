from .context import DispatchContext
from .dto import Courier, Order, CourierPlan
from typing import Dict, List
from .plan import TailAppendPlan
import sys


class Cost:
    def __init__(self, i, j, couriers: List[Courier], orders: List[Order], context: DispatchContext):
        self.i = i
        self.j = j
        self.courier = couriers[i]
        self.order = orders[j]
        tailAppendPlan = TailAppendPlan()
        self.planActionNodes = tailAppendPlan.plan(self.courier, self.order, context)
        self.cost = self.calCost()

    def isValid(self):
        maxLoad = self.courier.maxLoads
        cr = sum([1 for order in self.courier.orders if order.status == 3])
        for node in self.planActionNodes:
            if node.actionType == 2:
                cr += 1
                if cr > maxLoad:
                    return False
            if node.actionType == 3:
                cr -= 1
        return True

    def calCost(self):
        cost = sys.float_info.max
        if not self.isValid():
            return sys.float_info.max
        for node in self.planActionNodes:
            if node.actionType == 3 and node.orderId == self.order.id:
                cost = node.actionTime
                break
        return cost


class BaseSolver:
    def __init__(self, context: DispatchContext):
        self.context = context
        self.orders = self.getCandidateOrders(context)
        self.couriers = self.getCandidateCouriers(context)
        self.ordersAssigned = [False for i in self.orders]
        self.costTable = []
        self.MINIMUM_INTERVAL_SECONDS = 60

    def getCandidateCouriers(self, dispatchContext: DispatchContext):
        return dispatchContext.courierPool.couriers

    def getCandidateOrders(self, dispatchContext: DispatchContext):
        return dispatchContext.orderPool.getDispatchingOrders()

    def getAssignedOrderIds(self):
        return [order.id for i, order in enumerate(self.orders) if self.ordersAssigned[i]]

    def initTable(self):
        courierSize = len(self.couriers)
        orderSize = len(self.orders)
        for i in range(courierSize):
            costTableRow = []
            for j in range(orderSize):
                costTableRow.append(self.getCost(i, j))
            self.costTable.append(costTableRow)

    def solve(self) -> List[CourierPlan]:
        self.initTable()
        while True:
            cost = self.getBest()
            if cost is None:
                break
            self.dealWithCost(cost)
        results: List[CourierPlan] = []
        for courier in self.couriers:
            submitPlan = self.getSubmitPlan(courier)
            if len(submitPlan.planRoutes) != 0:
                results.append(submitPlan)
        return results

    def getSubmitPlan(self, courier: Courier):
        submitThresholdTime = self.context.timeStamp + self.MINIMUM_INTERVAL_SECONDS
        submittedNodes = [node for node in courier.planRoutes if (not node.isSubmitted) and (node.needSubmitTime <= submitThresholdTime or self.context.isEndOfTest)]
        plan = CourierPlan(courier.id, submittedNodes)
        return plan

    def dealWithCost(self, cost: Cost):
        cost.courier.setsetPlanRoutes(cost.planActionNodes)
        cost.courier.orders.append(cost.order)
        self.ordersAssigned[cost.j] = True
        self.updateWeightRow(cost.i)
        self.updateWeightCol(cost.j)

    def updateWeightRow(self, i):
        for j in range(len(self.orders)):
            self.costTable[i][j] = self.getCost(i, j)

    def updateWeightCol(self, j):
        for i in range(len(self.couriers)):
            self.costTable[i][j] = self.getCost(i, j)

    def getBest(self):
        best = None
        courierSize = len(self.couriers)
        orderSize = len(self.orders)
        for i in range(courierSize):
            for j in range(orderSize):
                tmpC = self.costTable[i][j]
                if tmpC is None:
                    continue
                if best is None:
                    best = tmpC
                    continue
                if self.costLess(tmpC, best):
                    best = tmpC
        return best

    def costLess(self, c1: Cost, c2: Cost):
        return c1.cost <= c2.cost

    def getCost(self, i, j):
        if self.ordersAssigned[j]:
            return None
        cost = Cost(i, j, self.couriers, self.orders, self.context)
        if not cost.isValid():
            return None
        return cost
