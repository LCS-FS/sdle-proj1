import json
from django.shortcuts import render, redirect
from django.http import HttpResponse, HttpRequest, HttpResponseRedirect, JsonResponse
from django.contrib.auth import authenticate, login, logout
import random
from .models import List, ItemOp
from .connectUtils import queryProxy, getList, putList, itemOpsFormat
# Create your views here.

ADDRESS = ""
PORT = ""

def index(request):
    lists = List.objects.all()
    lists = reversed(lists)
    return render(request, 'index.html', {'lists': lists})

def createList(request):
    if(request.method != 'POST'):
        return redirect('index')
    hash = str(random.getrandbits(128))
    l = List(hash= hash, title=request.POST['title'])
    l.save()
    return redirect('listPage', hash)

def connectList(request):
    global ADDRESS, PORT
    if(request.method != 'POST'):
        return redirect('index')
    
    hash = request.POST['hash']
    try:
        l = List.objects.get(hash=hash)
        print('found list')
    except:
        address, port = queryProxy(hash)
        
        if address is None or port is None:
            return redirect('index')
        ADDRESS, PORT = address, port
        
        print(f"address: {address}, port: {port}")
        getList(address, port, hash)
    return redirect('listPage', hash)

def removeList(request, hash):
    try:
        l = List.objects.get(hash=hash)
        l.delete()
    except:
        pass
    return redirect('index')

def listPage(request, hash):
    global ADDRESS, PORT
    address, port = queryProxy(hash)
    if not (address == None or port == None):
        getList(address, port, hash)
        ADDRESS, PORT = address, port
    
    l = List.objects.get(hash=hash)
    itemList = itemOpsFormat(hash)
    
    #TODO: On page load, request updated items from server 
    # using javascript
    # perhaps use pub/sub strategy with pusher
    return render(request, 'listPage.html', {'list':l, 'items':itemList})

def newItem(request):
    if(request.method != 'POST'):
        return HttpResponseRedirect(request.META.get('HTTP_REFERER'))
    title = request.POST['title']
    hash = str(random.getrandbits(128))
    typeOfOp = 'Add'
    listHash = request.POST['listHash']

    l = List.objects.get(hash=listHash)

    itemOp = ItemOp(list=l, hash=hash, title=title, type=typeOfOp)
    itemOp.save()
    
    l = List.objects.get(hash=listHash)
    items = ItemOp.objects.all().filter(list=l)
    
    putList(ADDRESS, PORT, listHash, l.title, items)
    #getList(ADDRESS, PORT, listHash)

    return redirect("listPage", listHash)

def updateItem(request, title):
    if(request.method != 'POST'):
        return HttpResponseRedirect(request.META.get('HTTP_REFERER'))
    body_unicode = request.body.decode('utf-8')
    data = json.loads(body_unicode)
    
    count = data['count']
    #get previous op
    prevOp = ItemOp.objects.filter(title=title).last()
    l = List.objects.get(hash=prevOp.list.hash)

    #get all previous ops
    opsList = ItemOp.objects.all().filter(title=title)
    originalCnt = 0

    for op in opsList:
        if op.type == 'Add':
            originalCnt += op.count
        else:
            originalCnt -= op.count
    
    newCount = int(count) - originalCnt
    if newCount == 0: return JsonResponse({"error": "No changes", "count": originalCnt}, status=400)
    opType = 'Add'
    if newCount < 0:
        opType = 'Rem'
        newCount = -newCount

    itemOp = ItemOp(list=l, hash=str(random.getrandbits(128)), title=title, type=opType, count=newCount)
    itemOp.save()

    putList(ADDRESS, PORT, l.hash, l.title, ItemOp.objects.all().filter(list=l))
    #getList(ADDRESS, PORT, listHash)

    return JsonResponse({}, status=200)

def updateAddress(request, address, port):
    global ADDRESS, PORT
    ADDRESS, PORT = address, port
    return JsonResponse({}, status=200)

def requestAddress(request):
    return JsonResponse({"address": ADDRESS, "port": PORT}, status=200)
