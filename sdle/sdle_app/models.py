from django.db import models

# Create your models here.

class List(models.Model):
    hash = models.CharField(max_length=256, primary_key=True)
    title = models.CharField(max_length=256)

typeOfOps={
    ('Add','Add'),
    ('Rem','Rem')
}

class ItemOp(models.Model):
    hash = models.CharField(max_length=256, primary_key=True)
    title = models.CharField(max_length=256)
    type = models.CharField(max_length=3, choices=typeOfOps)
    count = models.IntegerField(default=1)
    list = models.ForeignKey(List, on_delete=models.CASCADE)


    #Banana-Add(<hash random>, 1)             Banana-Add(<hash-random>, 1)  Banana-Add(<hash-random>, 1)
    #Banana-Add(<hash2>, 1)                                                 
    #                                         Banana-Add(<hash3>, 2)
    #                                                                       Banana-Add<hash2, 1>
    #                                                                       Banana-Add<hash3, 2>
    #Banana-Remove(hash4, 1)
    #                                                                       Banana-Remove<hash4, 1>
