#!/usr/bin/python

from flask import Flask, jsonify, request

app = Flask(__name__)


@app.route('/api/v1/ping', methods=['GET'])
def ping():
    pong_result = '{"code":200,"result":"PONG"}'
    return pong_result


@app.route('/api/v1/dispatch', methods=['POST'])
def dispatch():
    data = request.json
    # data is in format of demo.dto.DispatchRequest
    print(data)
    empty_result = """{
        "code":200,
        "result":{
            "courierPlans":[]
        }
    }"""
    return empty_result


def local_start(port=8080):
    app.run(debug=True, port=port)


if __name__ == "__main__":
    app.run(debug=True, port=8080)
