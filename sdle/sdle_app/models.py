from django.db import models

# Create your models here.

class List(models.Model):
    hash = models.CharField(max_length=256, primary_key=True)
    title = models.CharField(max_length=256)