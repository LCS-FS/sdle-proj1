from django.shortcuts import render, redirect
from django.http import HttpResponse, HttpRequest, JsonResponse
from django.contrib.auth import authenticate, login, logout
# Create your views here.

def index(request):
    lists = [
            {'hash': 1, 'title': 'list1'},
            {'hash': 2, 'title': 'list2'},
            {'hash': 3, 'title': 'list3'},
            ]
    return render(request, 'index.html', {'lists': lists})

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

