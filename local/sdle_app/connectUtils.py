import random
import requests
from .models import List, ItemOp

PROXY = "http://10.227.156.30:12345"

def queryProxy(listHash):
    try:
        r = requests.get(PROXY + "/list/" + listHash, timeout=5)
        print(r.status_code)
    except:
        return None, None
    
    if r.status_code == 200:
        response_data = r.json()
        address = response_data.get("address")
        port = response_data.get("port")
        if address and port:
            return address, port
        else:
            print("Address or port not found in the response.")
            return None, None
    else:
        print("Error:", r.status_code)
        return None, None

def getList(address, port, listHash):
    try:
        r = requests.get("http://" + address + ":" + str(port) + "/list/" + listHash, timeout=5)
    except:
        return False
    
    if r.status_code == 200:
        response_data = r.json()
        name = response_data.get("name")
        items = response_data.get("commits")
        
        findList = List.objects.all().filter(hash=listHash)
        
        print(f"findList: {findList}")
        if len(findList) == 0:    
            l = List(hash=listHash, title=name)
            l.save()
        
        if name and items:
            print(name)
            l = List.objects.get(hash=listHash)
            
            for item in items:
                if item['type'] == 'ADD':
                    typ = 'Add'
                else:
                    typ = 'Rem'
                itemOp = ItemOp(hash=item['hash'], title=item['itemName'], type=typ, count=item['count'], list=l)
                
                # if itemOp not in database, add
                try:
                    itemFound = ItemOp.objects.get(itemOp)
                except:
                    itemOp.save()
            return True
        else:
            print("Name or items not found in the response.")
            return False
    else:
        print("Error:", r.status_code)
        return False


def putList(address, port, listHash, name, items):    
    itemList = []
    for item in items:
        if item.type == 'Add':
            typ = 'ADD'
        else:
            typ = 'REMOVE'
        itemList.append({
            'hash':item.hash,
            'itemName':item.title,
            'count': item.count,
            'type': typ
            })
    
    try:
        r = requests.put("http://" + address + ":" + str(port) + "/list", json={"id": listHash, "name": name, "commits": itemList}, timeout=5)
        print(r.request.body)
        print(r.text)
    except:
        return False
    if r.status_code == 200:
        print("List updated.")
        return True
    else:
        print("Error:", r.status_code)

def itemOpsFormat(listHash):
    l = List.objects.get(hash=listHash)
    items = ItemOp.objects.all().filter(list=l)
    itemMap = {}
    for item in items:
        if not item.title in itemMap:
            itemMap[item.title] = item.count
        elif item.type == 'Add':
            itemMap[item.title] += item.count
        else:
            itemMap[item.title] -= item.count

    itemList = []
    for title, cnt in itemMap.items():
        if cnt != 0:
            itemList.append({'cnt':cnt, 'title':title})
    return itemList
