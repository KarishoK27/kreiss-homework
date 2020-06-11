from django.db import models
from django.utils import timezone

from fcm_django.models import FCMDevice


class Message(models.Model):
    message_text = models.CharField(max_length=200)
    time = models.DateTimeField(default=timezone.now)

    def __str__(self):
        return self.message_text

class Confirm(models.Model):
    message = models.ForeignKey(Message, on_delete=models.CASCADE)
    device = models.ForeignKey(FCMDevice, on_delete=models.DO_NOTHING)
    time = models.DateTimeField(default=timezone.now)