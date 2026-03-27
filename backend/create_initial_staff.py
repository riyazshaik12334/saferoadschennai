import os
import django
import sys

# Setup Django Environment
sys.path.append('c:\\Users\\user\\AndroidStudioProjects\\SafeRoadsChennaiSRC\\backend')
os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'backend_project.settings')
django.setup()

from api.models import Authority, Supervisor

def create_staff():
    # Create Authority
    if not Authority.objects.filter(officer_id="auth12345").exists():
        auth = Authority(
            officer_id="auth12345",
            name="Principal Officer",
            email="auth12345@chennai.gov",
            password="password123"
        )
        auth.save()
        print("Created Authority: auth12345")
    else:
        print("Authority auth12345 already exists.")

    # Create Supervisor
    if not Supervisor.objects.filter(supervisor_id="sup12345").exists():
        sup = Supervisor(
            supervisor_id="sup12345",
            name="District Supervisor",
            email="sup12345@chennai.gov",
            password="password123"
        )
        sup.save()
        print("Created Supervisor: sup12345")
    else:
        print("Supervisor sup12345 already exists.")

if __name__ == '__main__':
    create_staff()
