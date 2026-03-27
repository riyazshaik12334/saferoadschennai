import os
import django

os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'backend_project.settings')
django.setup()

from api.models import OTP

otps = OTP.objects.all().order_by('-created_at')[:5]
print(f"Total OTPs in DB: {OTP.objects.count()}")
for o in otps:
    print(f"Email: {o.email}, Code: {o.code}, Created: {o.created_at}")
