from flask import Flask, jsonify
from oetsv_package.getIds import getIds
from oetsv_package.getCSVData import getCSVData

app = Flask(__name__)

@app.route('/')
def hello_world():
    return "<p>api at /oetsv_kalender</p>"

@app.route('/oetsv_kalender')
def oetsv_json():
    url = 'https://www.tanzsportverband.at/kalender/daten.html'
    json_data = []
    for t in getIds(url):
        d=getCSVData(t)
        if d!=404:
            json_data.append(d)

    return jsonify(json_data)
