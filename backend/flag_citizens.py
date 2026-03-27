import os
import django
os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'backend_project.settings')
django.setup()

from django.contrib.auth.models import User

updated_count = 0
for user in User.objects.all():
    # Evaluate if they are categorized as a Citizen
    if not (user.is_staff or user.email.startswith("SUP") or user.email.startswith("sup_") or 
            user.email.startswith("EMP") or "EMP" in user.email or user.email.startswith("auth_")):
        user.is_superuser = True
        user.save()
        updated_count += 1

print(f"Successfully flagged {updated_count} existing Citizens as 'is_superuser = 1' in the Database.")
