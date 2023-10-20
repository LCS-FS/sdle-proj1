from django.shortcuts import render, redirect
from django.http import HttpResponse, HttpRequest, JsonResponse
# Create your views here.

def index(request):
    return render(request, 'index.html')
    #return HttpResponse("Hello, world. You're at the index.")
