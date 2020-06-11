from rest_framework import serializers
from .models import Message, Confirm
from fcm_django.models import FCMDevice

class MessageSerializer(serializers.ModelSerializer):
    id = serializers.IntegerField(read_only=True)
    status = serializers.BooleanField(required=False)

    class Meta:
        model = Message
        fields = ['id', 'message_text', 'time', 'status']

class ConfirmSerializer(serializers.ModelSerializer):
    id = serializers.IntegerField(read_only=True)
    message = serializers.SlugRelatedField(queryset=Message.objects.all(), slug_field='id')
    device = serializers.SlugRelatedField(queryset=FCMDevice.objects.all(), slug_field='device_id')
    time = serializers.DateTimeField(required=False)
    
    class Meta:
        model = Confirm
        fields = ['id', 'message', 'device', 'time']