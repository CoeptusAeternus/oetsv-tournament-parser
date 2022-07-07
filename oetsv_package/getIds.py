import requests
from bs4 import BeautifulSoup
import re


def findTKNr(l):
    id = re.search('TKNr=\d+',l)
    if id:
        return id.group(0).split('=')[1]

def getIds(url):
    html_text = requests.get(url).text
    soup = BeautifulSoup(html_text, 'html.parser')
    ids=[]

    for link in soup.find_all('a'):
        l=link.get('href')
        if 'TKNr=' in l:
            id=findTKNr(l)
            ids.append(id)
    
    return ids