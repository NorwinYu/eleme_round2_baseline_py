import http

from flask import Flask, jsonify, request

import json

from demo.dto import Response

app = Flask(__name__)


@app.route('/api/v1/ping', methods=['GET'])
def ping():
    return json.dumps(dict(Response(200, "Pong", "")))


@app.route('/api/v1/dispatch', methods=['POST'])
def dispatch():
    data = request.json
    # data is in format of demo.dto.DispatchRequest
    print(data)
    return '', http.HTTPStatus.OK


if __name__ == "__main__":
    app.run(debug=True, port=8080)
