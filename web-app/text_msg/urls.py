from django.urls import include, path

from . import views

urlpatterns = [
    path('', views.message, name='message'),
    path('send/', views.send, name='send')
]