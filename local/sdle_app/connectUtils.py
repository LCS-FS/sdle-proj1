import random
import requests
from .models import List, ItemOp

PROXY = "http://localhost:12345"
ONLINE_FLAG = True

def queryProxy(listHash):
    if not ONLINE_FLAG:
        return None, None
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

def setOnlineFlag(flag):
    global ONLINE_FLAG
    ONLINE_FLAG = flag
    return

def getList(address, port, listHash, count=0):
    if not ONLINE_FLAG:
        return False
    try:
        r = requests.get("http://" + address + ":" + str(port) + "/list/" + listHash, timeout=5)
        print(f"getList: {r.status_code}")
    
    
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
            print("GetList Error:", r.status_code)
            return False
    except Exception as e:
        print(f"getList Exception: {e}")
        address, port = queryProxy(listHash)
        if address is None or port is None:
            return False
        elif count <3:
            return getList(address, port, listHash, count=count+1)


def putList(address, port, listHash, name, items, count=0):
    if not ONLINE_FLAG:
        return False
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
        print(f"put body {r.request.body}")
        print(f"put text {r.text}")
        
        if r.status_code == 200:
            print("List updated.")
            return True
        
        else:
            print("Error:", r.status_code)
            return False
    except Exception as e:
        print(f"putList Exception: {e}")
        address, port = queryProxy(listHash)
        if address is None or port is None:
            return False
        elif count <3:
            return putList(address, port, listHash, name, items, count=count+1)
    

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

def setProxy(host):
    global PROXY
    PROXY = f"http://{host}:12345"
    print(f"Proxy set to {PROXY}")
    return

def getProxy():
    global PROXY
    proxy = PROXY
    proxy = proxy.replace("http://", "").split(":")[0]
    return proxy