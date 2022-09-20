import requests
from bs4 import BeautifulSoup
from oetsv_package.getIds import findTKNr


def getList(url):
    html_text = requests.get(url).text
    soup = BeautifulSoup(html_text, 'html.parser')
    lis=[]

    res=soup.find_all('tr')
    res.pop(0)
    res

    for e in res:
        td=e.find_all('td')
        if td[4] is not None:
            l=td[4].find('a')
            if l is not None:
                l=l.get('href')
                if 'TKNr=' in l:
                    id=findTKNr(l)
                    name=td[1].find_all('b')[0].text
                    dic={
                        'id':   id,
                        'date': td[0].text,
                        'name': name,
                    }
                    lis.append(dic)

    return lis