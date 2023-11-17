from django.shortcuts import render, redirect
from django.http import HttpResponse, HttpRequest, HttpResponseRedirect, JsonResponse
from django.contrib.auth import authenticate, login, logout
import random
from .models import List, ItemOp
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
        title = "PLACEHOLDER"
        l = List(hash=hash, title=title)
        l.save()
    
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
    
    items = ItemOp.objects.all().filter(list=l)
    itemMap = {}
    titleMap = {}
    for item in items:
        print(item.count)
        if not item.hash in titleMap:
            titleMap[item.hash] = item.title
        if not item.hash in itemMap:
            itemMap[item.hash] = item.count
        elif item.type == 'Add':
            itemMap[item.hash] += item.count
        else:
            itemMap[item.hash] -= item.count


    itemList = []
    for hash, cnt in itemMap.items():
        itemList.append({'hash':hash, 'cnt':cnt, 'title':titleMap[hash]})

    print(itemList)
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

