from django.shortcuts import render, redirect
from django.http import HttpResponse, HttpRequest, JsonResponse
# Create your views here.

def index(request):
    if (not request.user.is_authenticated):
        return redirect('/login')
    return render(request, 'index.html')
    #return HttpResponse("Hello, world. You're at the index.")

def login(request):
    if (request.user.is_authenticated):
        return redirect('/')
    return render(request, 'login.html')
