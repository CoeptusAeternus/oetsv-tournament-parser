from flask import Flask, jsonify, abort
from oetsv_package.getIds import getIds
from oetsv_package.getData import getData
from oetsv_package.getList import getList

app = Flask(__name__)

@app.route('/')
def landing():
    return "<p>api at <a href=\"/oetsv_kalender\" title=\"api\">/oetsv_kalender</a></p>"

@app.route('/oetsv_kalender')
def api_overview():
    return "<a href=\"/oetsv_kalender/all\" title=\"app\">all</a><br><a href=\"/oetsv_kalender/list\" title=\"api\">list</a><br>"

@app.route('/oetsv_kalender/all')
def oetsv_json():
    url = 'https://www.tanzsportverband.at/kalender/daten.html'
    json_data = []
    for t in getIds(url):
        d=getData(t)
        if d!=404:
            json_data.append(d)

    return jsonify(json_data)

@app.route('/oetsv_kalender/list')
def list():
    url = 'https://www.tanzsportverband.at/kalender/daten.html'
    return jsonify(getList(url))

@app.route('/oetsv_kalender/<id>')
def tournament(id):
    data=getData(id)
    if data != 404:
        return jsonify(getData(id))
    abort(404)

@app.errorhandler(404)
def not_found(e):
    return {'error':404, 'message':'id not found',},404