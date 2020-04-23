#!/usr/bin/python

from flask import Flask, jsonify, request
from .demo.service import DispatchService
from .demo.dto import DispatchRequest, Location, Order, Courier, Response
import json

app = Flask(__name__)


@app.route('/api/v1/ping', methods=['GET'])
def ping():
    pong_result = '{"code":200,"result":"PONG"}'
    return pong_result


@app.route('/api/v1/score', methods=['GET'])
def score():
    score_result = '{"code":200,"result":"PONG"}'
    return score_result


def toLocation(dct):
    latitude = dct['latitude']
    longitude = dct['longitude']
    return Location(float(latitude), float(longitude))


def toDispatchRequest(dct):
    courierList = dct['couriers']
    courierobjs = []
    for courier in courierList:
        areaId = courier['areaId']
        id = courier['id']
        loc = courier['loc']
        maxLoads = courier['maxLoads']
        speed = courier['speed']
        courierobj = Courier(id, areaId, toLocation(loc), float(speed), int(maxLoads))
        courierobjs.append(courierobj)
    orderList = dct['orders']
    orderobjs = []
    for order in orderList:
        areaId = order['areaId']
        createTime = order['createTime']
        dstLoc = order['dstLoc']
        estimatedPrepareCompletedTime = order['estimatedPrepareCompletedTime']
        id = order['id']
        promiseDeliverTime = order['promiseDeliverTime']
        srcLoc = order['srcLoc']
        orderobj = Order(areaId, id, toLocation(srcLoc), toLocation(dstLoc), 0, int(createTime),
                         int(promiseDeliverTime), int(estimatedPrepareCompletedTime))
        orderobjs.append(orderobj)
    request = DispatchRequest(int(dct['requestTime']), dct['areaId'], bool(dct['firstRound']), bool(dct['lastRound']),
                               courierobjs, orderobjs)
    return request


dispatchService = DispatchService()


@app.route('/api/v1/dispatch', methods=['POST'])
def dispatch():
    data = request.json
    # data is in format of demo.dto.DispatchRequest
    print(data)
    # empty_result = """{
    #     "code":200,
    #     "result":{
    #         "courierPlans":[]
    #     }
    # }"""
    resultobj = dispatchService.dispatch(toDispatchRequest(data))

    def outputFilter(o):
        is_find = False
        res: dict = o.__dict__
        res = res.copy()
        for k in res.keys():
            if k == "isSubmitted" or k == "needSubmitTime":
                is_find = True
                break
        if is_find:
            del res["isSubmitted"]
            del res["needSubmitTime"]
        return res

    result = json.dumps(resultobj.__dict__, default=outputFilter, sort_keys=False)
    if result is not None:
        print(result.replace(" ", ""))
    responseobj = Response(200, resultobj, "")
    response = json.dumps(responseobj.__dict__, default=outputFilter, sort_keys=False)

    return response.replace(" ", "")


def local_start(port=8080):
    app.run(debug=True, port=port)


if __name__ == "__main__":
    app.run(debug=True, port=8080)
