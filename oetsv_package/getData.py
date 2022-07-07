import requests
import pandas as pd
from bs4 import BeautifulSoup
import re
import json

pd.set_option('display.max_colwidth', None)

def getData(id):

    #request Ausschreibungs HTML
    html_request = requests.get("https://www.tanzsportverband.at/portal/ausschreibung/ausschreibung_drucken.php?TKNr="+str(id)+"&art=IN&conf_html=1")


    if html_request.status_code == 200:


        df=pd.read_csv("https://www.tanzsportverband.at/modules/ext-data/turniere/oetsv_turniere_"+str(id)+".csv",sep=';',encoding='iso-8859-1')

        soup = BeautifulSoup(html_request.text, 'html.parser')

        bezeichnung=df.get('nr').where(df.get('nr').str.find('TURNIERBEZEICHNUNG')==0).dropna().to_string().split('=')[1]

        datum=df.get('nr').where(df.get('nr').str.find('DATUM')==0).dropna().to_string().split('=')[1]
        zeit=re.sub(' Uhr','',re.search("\d\d?(|:\d\d) Uhr",soup.text).group(0))+":00"

        nenngeld_suche=re.search("(g|G)(eld|gebühr)\s*\d+(,(\d{2}|-))\s*€?",soup.text)
        if nenngeld_suche:
            nenngeld=re.sub('\s+','',re.sub('(G|g)(eld|gebühr) ','',nenngeld_suche.group(0)))
        else:
            nenngeld=0
        #turnierklassen
        table=soup.find('table').find_all('tr')[3].find_all('td')
        klassen_html=table[len(table)-1]
        klassen_bold=klassen_html.find_all('b')
        klassen_str=[]
        for i in klassen_bold:
            klassen_str.append(i.get_text())

        klassen=[]
        for i in klassen_str:
            klassen.append(re.sub(' \(\d+\)','',i))

        a_tags=soup.find_all('a')
        links=[]
        for i in a_tags:
            links.append(i['href'])

        link_str='-'.join([str(elem) for elem in links])
        search_term=re.sub('q=','',re.sub('\s+|\+','%20',re.search("q=(\w|\s|\+)+",link_str).group(0)))
        adr_response=json.loads(requests.get("https://nominatim.openstreetmap.org/search?q="+search_term+"&format=json").text)
        if len(adr_response)!=0:
            adr=adr_response[0]['display_name']
        else:
            adr=re.sub('%20',' ',search_term)

        #in Dict für JSON
        ret_dict = {
            'id':id,
            'bezeichnung': bezeichnung,
            'datum':datum+'T'+zeit,
            'klassen':klassen,
            'nenngeld':nenngeld,
            'adresse':adr,
        }

        return ret_dict
    
    return 404
