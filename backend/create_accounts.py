import os
import django
os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'backend_project.settings')
django.setup()

from django.contrib.auth.models import User

accounts = [
    {"username": "Employee_One", "email": "EMP12345", "password": "password123", "is_staff": True},
    {"username": "Employee_Two", "email": "EMP12346", "password": "password123", "is_staff": True},
    {"username": "Supervisor_One", "email": "SUP12345", "password": "password123", "is_staff": True},
    {"username": "Supervisor_Two", "email": "SUP12346", "password": "password123", "is_staff": True},
]

for acc in accounts:
    if not User.objects.filter(email=acc['email']).exists():
        user = User(username=acc['username'], email=acc['email'])
        user.password = acc['password'] # Store as plain text
        user.is_staff = acc['is_staff']
        user.is_superuser = True
        user.save()
        print(f"Created: {acc['email']} with password: {acc['password']}")
    else:
        print(f"Already exists: {acc['email']}")
