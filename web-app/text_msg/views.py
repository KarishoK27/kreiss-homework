from django.http import HttpResponseRedirect
from django.shortcuts import render
from django.urls import reverse
from django.db.models import Exists, OuterRef

from rest_framework import viewsets
from rest_framework.response import Response

from .models import Message, Confirm
from .serializers import MessageSerializer, ConfirmSerializer

from fcm_django.models import FCMDevice



def index(req):
    return render(req, 'message/index.html')


def message(req):
    return render(req, 'message/message.html')


def send(req):
    if req.method == 'POST':
        if req.POST['message'] == '':
            return render(req, 'message/message.html',
                          {'error_message': "Please enter a text message"})

        m = Message(message_text=req.POST['message'])
        m.save()
        try:
            devices = FCMDevice.objects.all()
            devices.send_message(title="New Message", body=m.message_text)
        except:
            print("Not able to send messages to FCM:", sys.exc_info()[0])

    return HttpResponseRedirect(reverse('message'))

class MessageViewSet(viewsets.ReadOnlyModelViewSet):
    serializer_class = MessageSerializer
    
    def get_queryset(self):
        queryset = Message.objects.all().order_by('-time')
        device_id = self.request.query_params.get('device', None)
        if device_id is not None:
            device = FCMDevice.objects.get(device_id=device_id)
            subquery = Confirm.objects.filter(device=device.id, message=OuterRef('pk'))
            queryset = Message.objects.annotate(status=Exists(subquery)).order_by('-time')
        return queryset


class ConfirmViewSet(viewsets.ModelViewSet):
    serializer_class = ConfirmSerializer

    def get_queryset(self):
        queryset = Confirm.objects.all()
        device_id = self.request.query_params.get('device', None)
        message_id = self.request.query_params.get('message', None)
        if device_id is not None:
            device = FCMDevice.objects.get(device_id=device_id)
            queryset = queryset.filter(device=device.id)
        if message_id is not None:
            queryset = queryset.filter(message=message)
        return queryset
