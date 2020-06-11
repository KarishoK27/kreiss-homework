"""webapp URL Configuration

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/3.0/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  path('', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  path('', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.urls import include, path
    2. Add a URL to urlpatterns:  path('blog/', include('blog.urls'))
"""
from django.contrib import admin
from django.urls import include, path
from rest_framework import routers
from text_msg import views
from fcm_django.api.rest_framework import FCMDeviceViewSet


router = routers.DefaultRouter()
router.register('messages', views.MessageViewSet, basename='messages')
router.register('confirm', views.ConfirmViewSet, basename='confirm')
router.register('devices', FCMDeviceViewSet)

urlpatterns = [
    path('admin/', admin.site.urls),
    path('message/', include('text_msg.urls')),
    path('', views.index, name='index'),
    path('api/', include(router.urls))
]
