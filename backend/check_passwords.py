import os
import django
os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'backend_project.settings')
django.setup()
from django.contrib.auth.models import User
for u in User.objects.all():
    print(f"{u.username} | {u.password}")
