from .dto import Order, Courier, ActionNode
from .pool import OrderPool, CourierPool
from typing import Dict, List


class DispatchContext:
    def __init__(self, areaId, timeStamp, isEndOfTest=False):
        self.areaId = areaId
        self.timeStamp = timeStamp
        self.isEndOfTest = isEndOfTest

        self.courierPool = CourierPool()
        self.orderPool = OrderPool()

    def setTimeStamp(self, timeStamp):
        self.timeStamp = timeStamp

    def setIsEndOfTest(self, isEndOfTest):
        self.isEndOfTest = isEndOfTest

    def addOnlineCouriers(self, courierList):
        self.courierPool.addOnlineCouriers(courierList)

    def addDispatchingOrders(self, orders):
        self.orderPool.addDispatchingOrders(orders)

    def markAllocatedOrders(self, orderIds):
        for orderId in orderIds:
            self.orderPool.markAssignedOrder(orderId)

    def refresh(self, refreshTime):
        self.timeStamp = refreshTime
        for courier in self.courierPool.couriers:
            self.refreshCourier(courier, refreshTime)

    def refreshCourier(self, courier: Courier, refreshTime):
        actionNodeList = courier.planRoutes
        refreshNodeList: List[ActionNode] = []
        for node in actionNodeList:
            if node.isSubmitted and node.actionTime <= refreshTime:
                if node.actionType == 1:
                    self.orderPool.markArrivalCompleteOrder(node.orderId)
                elif node.actionType == 2:
                    self.orderPool.markPickCompleteOrder(node.orderId)
                elif node.actionType == 3:
                    self.orderPool.markDeliverCompleteOrder(node.orderId)
            else:
                refreshNodeList.append(node)
        loadOrders = [order for order in courier.orders if order.status != 4]
        courier.orders = loadOrders
        courier.planRoutes = refreshNodeList
        if len(refreshNodeList) == 0 and len(actionNodeList) != 0:
            latestOrder: Order = self.orderPool.getOrder(actionNodeList[-1].orderId)
            courier.setLoc(latestOrder.dstLoc)
