import requests
import pandas as pd
from bs4 import BeautifulSoup
import re

pd.set_option('display.max_colwidth', None)

def getData(id):

    #request Ausschreibungs HTML
    html_request = requests.get("https://www.tanzsportverband.at/portal/ausschreibung/ausschreibung_drucken.php?TKNr="+id+"&art=IN&conf_html=1")


    if html_request.status_code == 200:
        df=pd.read_csv("https://www.tanzsportverband.at/modules/ext-data/turniere/oetsv_turniere_"+id+".csv",sep=';',encoding='iso-8859-1')

        soup = BeautifulSoup(html_request.text, 'html.parser')

        bezeichnung=df.get('nr').where(df.get('nr').str.find('TURNIERBEZEICHNUNG')==0).dropna().to_string().split('=')[1]
        datum=df.get('nr').where(df.get('nr').str.find('DATUM')==0).dropna().to_string().split('=')[1]
        kl=df.get('bezeichnung').where(df.get('dsart').str.find('TKL')==0).dropna().values.tolist()


        table=soup.find('table').find_all('tr')[3].find_all('td')
        klassen_html=table[len(table)-1]
        klassen_bold=klassen_html.find_all('b')
        klassen_str=[]
        for i in klassen_bold:
            klassen_str.append(i.get_text())

        klassen=[]
        for i in klassen_str:
            
            klassen.append(re.sub(' \(\d+\)','',i))

        ret_dict = {
            'id':id,
            'bezeichnung': bezeichnung,
            'datum':datum,
            'klassen':klassen,
        }

        return ret_dict
    
    return 404
