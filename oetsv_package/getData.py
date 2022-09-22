import requests
import pandas as pd
from bs4 import BeautifulSoup
import re
import io

pd.set_option('display.max_colwidth', None)

def getData(id):

    #request Ausschreibungs HTML&CSV
    html_response = requests.get("https://www.tanzsportverband.at/portal/ausschreibung/ausschreibung_drucken.php?TKNr="+str(id)+"&art=IN&conf_html=1")
    csv_response = requests.get("https://www.tanzsportverband.at/modules/ext-data/turniere/oetsv_turniere_"+str(id)+".csv")

    if html_response.status_code == 200 and csv_response.status_code == 200:

        df=pd.read_csv(io.StringIO(csv_response.text),sep=';',encoding='iso-8859-1')

        soup = BeautifulSoup(html_response.text, 'html.parser')

        bezeichnung=df.get('nr').where(df.get('nr').str.find('TURNIERBEZEICHNUNG')==0).dropna().to_string().split('=')[1]

        datum=df.get('nr').where(df.get('nr').str.find('DATUM')==0).dropna().to_string().split('=')[1]
        #format date fpr iso
        date_comp=datum.split('-')
        t1 = date_comp[1]
        t2 = date_comp[2]
        if int(t1) < 10:
            date_comp[1]='0'+date_comp[1]
        if int(t2) < 10:
            date_comp[2]='0'+date_comp[2]
        datum=date_comp[0]+"-"+date_comp[1]+"-"+date_comp[2]

        zeit=re.sub(' Uhr','',re.search("\d\d?(|:\d\d) Uhr",soup.text).group(0))+":00"

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
            'datum':datum+'T'+zeit,
            'klassen':klassen,
            'nenngeld':nenngeld,
            'adresse':adr,
            'wr':wr_str,
        }

        return ret_dict
    
    return 404
