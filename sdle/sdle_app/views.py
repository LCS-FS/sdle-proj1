from django.shortcuts import render, redirect
from django.http import HttpResponse, HttpRequest, JsonResponse
from django.contrib.auth import authenticate, login, logout
import random
from .models import List
# Create your views here.

def index(request):
    lists = List.objects.all()
    return render(request, 'index.html', {'lists': lists})

def createList(request):
    if(request.method != 'POST'):
        return redirect('index')
    hash = str(random.getrandbits(128))
    l = List(hash= hash, title=request.POST['title'])
    l.save()
    return redirect('index')

# def login_view(request):
#     if (request.user.is_authenticated):
#         return redirect('/')
#     return render(request, 'login.html')

# def authenticate_view(request):
#     if (request.user.is_authenticated):
#         return redirect('/')
#     if request.method == 'POST':
#         username = request.POST['username']
#         password = request.POST['pass']

#         user = authenticate(username=username, password=password)

#         if user is not None:
#             login(request, user)
#             return redirect('/')

#     return redirect('login')

