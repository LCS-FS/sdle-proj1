import random
import requests
from .models import List, ItemOp

PROXY = "http://localhost:12345"

def queryProxy(listHash):
    try:
        r = requests.get(PROXY + "/list/" + listHash, timeout=5)
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
    else:
        print("Error:", r.status_code)

def getList(address, port, listHash):
    try:
        r = requests.get("http://" + address + ":" + str(port) + "/list/" + listHash, timeout=5)
    except:
        return
    
    if r.status_code == 200:
        response_data = r.json()
        name = response_data.get("name")
        items = response_data.get("items")
        
        findList = List.objects.all().filter(hash=listHash)
        if len(findList) == 0:    
            l = List(hash=listHash, title=name)
            l.save()
        
        if name and items:
            print(name)
            l = List.objects.get(hash=listHash)
            
            itemList = itemOpsFormat(listHash)
            itemMap = {}
            for item in itemList:
                itemMap[item.title] = item.cnt
            
            for item in items:
                # set received items count to 0
                if item['name'] in itemMap.keys():
                    if itemMap[item['name']] < 0:
                        type = 'Add'
                    else:
                        type = 'Del'
                    itemOpDel = ItemOp(list=l, hash=str(random.getrandbits(128)), title=item['name'], type=type, count=itemMap[item['name']])
                    itemOpDel.save()
                    
                # set received items count to quantity
                itemOpAdd = ItemOp(list=l, hash=str(random.getrandbits(128)), title=item['name'], type='Add', count=item['quantity'])
                itemOpAdd.save()
        else:
            print("Name or items not found in the response.")
    else:
        print("Error:", r.status_code)


def putList(address, port, listHash, name, items):
    r = requests.put("http://" + address + ":" + str(port) + "/list/" + listHash, json={"name": name, "items": items})
    if r.status_code == 200:
        print("List updated.")
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