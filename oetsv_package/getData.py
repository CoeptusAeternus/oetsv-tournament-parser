import requests
import pandas as pd
from bs4 import BeautifulSoup
import re
import io

pd.set_option('display.max_colwidth', None)

def getData(id):

    #request Ausschreibungs HTML&CSV
    html_response = requests.get("https://www.tanzsportverband.at/portal/ausschreibung/ausschreibung_drucken.php?TKNr="+str(id)+"&art=IN&conf_html=1")

    if html_response.status_code == 200:

        soup = BeautifulSoup(html_response.text, 'html.parser')

        bezeichnung=soup.find('table').find_all('tr')[3].find_all('td')[4].getText()

        datetimeField=soup.find('table').find_all('tr')[3].find_all('td')[8].getText()
        datetimeField=re.sub(' Uhr','',datetimeField)
        datetimeField=re.sub('[a-zA-Z]{2}., ',"", datetimeField)
        datetimeField=re.sub(', ',"T", datetimeField)
        datetimeField=re.sub('\A\d{1}\.',addLeading0Day, datetimeField)
        datetimeField=re.sub('\. {1}\d\.',addLeading0Month, datetimeField)
        datetimeField=re.sub(' \d{2,4}',formatYear, datetimeField)
        datetimeField=datetimeField+":00"
        datetimeField



        nenngeld_suche=re.search("Nenngeld:(\s|\w|€)*\d+(,(\d{2}|-)|)",soup.text)
        if nenngeld_suche:
            nenngeld=re.search('\d+(,(\d{2}|-)|)',nenngeld_suche.group(0)).group(0)
        else:
            nenngeld='0'
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

        adr=table[6].text.split('  ->')[0]

        wr_search=re.sub('(.|\\n)*Wertungsrichter','\n ',soup.text)
        wr_match=re.finditer("\\n([A-Z]\s+-)?((\s|-)+(\w|[äöüÄÖÜ])+){2,}\s*(,|/)\s+(\w|[äöüÄÖÜ])+\\n",wr_search)
        wr_str=[]
        for x in wr_match:
            wr_str.append(re.sub(' /',',',re.sub('\n\s*([A-Z]\s+-)?','',x.group(0))))

        #in Dict für JSON
        ret_dict = {
            'id':id,
            'bezeichnung': bezeichnung,
            'datum':datetimeField,
            'klassen':klassen,
            'nenngeld':nenngeld,
            'adresse':adr,
            'wr':wr_str,
        }

        return ret_dict
    
    return 404



def addLeading0Day(param):
    if(len(param.group(0)) == 2):
        return "0"+param.group(0)
    else:
        return param.group(0)

def addLeading0Month(param):
    if(len(param.group(0)) == 4):
        return re.sub(' ','0',param.group(0))
    else:
        return param.group(0)

def formatYear(param):
    if(len(param.group(0)) == 3):
        return re.sub(' ','20',param.group(0))
    elif(len(param.group(0)) == 5):
        return re.sub(' ','',param.group(0))
    return param.group(0)
