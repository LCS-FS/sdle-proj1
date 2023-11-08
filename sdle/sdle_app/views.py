from django.shortcuts import render, redirect
from django.http import HttpResponse, HttpRequest, JsonResponse
from django.contrib.auth import authenticate, login, logout
import random
from .models import List
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

def listPage(request, hash):
    l = List.objects.get(hash=hash)
    # TODO: get correct items from database
    items = [
            {'id': 1, 'title':'item1', 'count': 3},
            {'id': 2, 'title':'item2', 'count': 1},
            {'id': 3, 'title':'item3', 'count': 5},
        ]
    
    #TODO: On page load, request updated items from server 
    # using javascript
    # perhaps use pub/sub strategy with pusher
    return render(request, 'listPage.html', {'list':l, 'items':items})

