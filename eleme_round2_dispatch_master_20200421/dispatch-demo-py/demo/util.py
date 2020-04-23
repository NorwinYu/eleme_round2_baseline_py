from .dto import Location
import math


class DistanceUtils:
    def __init__(self):
        # 地球半径
        self.RADIUS = 6367000.0
        # 导航距离/路面距离 经验系数
        self.COEFFICIENT = 1.4

    def timeConsuming(self, fromL: Location, toL: Location, speed):
        return math.ceil(self.getDistance(fromL, toL) / speed)

    # 经验路面距离 = 球面距离 * 经验系数(1.4)
    def getDistance(self, fromL: Location, toL: Location):
        return self.greatCircleDistance(fromL.longitude, fromL.latitude, toL.longitude, toL.latitude) * self.COEFFICIENT

    # 简化版球面距离
    def greatCircleDistance(self, lng1, lat1, lng2, lat2):
        # 经度差值
        deltaLng = lng2 - lng1
        # 纬度差值
        deltaLat = lat2 - lat1
        # 平均纬度
        b = (lat1 + lat2) / 2.0
        # 东西距离
        x = math.radians(deltaLng) * self.RADIUS * math.cos(math.radians(b))
        # 南北距离
        y = self.RADIUS * math.radians(deltaLat)
        # 用平面的矩形对角距离公式计算总距离
        return math.sqrt(x * x + y * y)