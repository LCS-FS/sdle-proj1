# Generated by Django 4.2.5 on 2023-11-10 12:07

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('sdle_app', '0002_itemop'),
    ]

    operations = [
        migrations.AlterField(
            model_name='itemop',
            name='count',
            field=models.IntegerField(default=1),
        ),
    ]