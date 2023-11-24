import json
from django.shortcuts import render, redirect
from django.http import HttpResponse, HttpRequest, HttpResponseRedirect, JsonResponse
from django.contrib.auth import authenticate, login, logout
import random
from .models import List, ItemOp
from .connectUtils import queryProxy, getList, putList, itemOpsFormat
# Create your views here.

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
    if(request.method != 'POST'):
        return redirect('index')
    
    hash = request.POST['hash']
    try:
        l = List.objects.get(hash=hash)
    except:
        # TODO: Replace with query to main server
        address, port = queryProxy(hash)
        
        if address == None or port == None:
            return redirect('index')
        
        getList(address, port, hash)
    
    # TODO: redirect to index if list not found
    return redirect('listPage', hash)

def removeList(request, hash):
    try:
        l = List.objects.get(hash=hash)
        l.delete()
    except:
        pass
    return redirect('index')

def listPage(request, hash):
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

    return redirect("listPage", listHash)

def updateItem(request, title):
    if(request.method != 'POST'):
        return HttpResponseRedirect(request.META.get('HTTP_REFERER'))
    body_unicode = request.body.decode('utf-8')
    data = json.loads(body_unicode)
    
    count = data['count']
    print(count)
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
    print(newCount)
    if newCount == 0: return JsonResponse({"error": "No changes", "count": originalCnt}, status=400)
    opType = 'Add'
    if newCount < 0:
        opType = 'Rem'
        newCount = -newCount

    itemOp = ItemOp(list=l, hash=str(random.getrandbits(128)), title=title, type=opType, count=newCount)
    itemOp.save()

    return JsonResponse({}, status=200)

