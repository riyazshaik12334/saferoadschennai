import os
import django
import sys

# Setup Django environment
sys.path.append('c:/Users/user/AndroidStudioProjects/SafeRoadsChennaiSRC/backend')
os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'backend_project.settings')
django.setup()

from api.models import Complaint

def migrate():
    complaints = list(Complaint.objects.all())
    print(f"DEBUG: Found {len(complaints)} complaints to migrate.")
    
    for c in complaints:
        old_id = c.report_id
        # Standardize ID: "src - [number]"
        clean = old_id
        low = clean.lower()
        if low.startswith("src - "): clean = clean[6:]
        elif low.startswith("src-"): clean = clean[4:]
        elif low.startswith("src "): clean = clean[4:]
        elif low.startswith("src"): clean = clean[3:]
        
        new_id = f"src - {clean}"
        
        if old_id == new_id:
            print(f"DEBUG: Skipping {old_id} (already formatted)")
            continue
            
        print(f"DEBUG: Migrating {old_id} -> {new_id}")
        
        # Check if new_id already exists (shouldn't happen with unique numbers)
        if Complaint.objects.filter(report_id=new_id).exists():
            print(f"WARNING: ID {new_id} already exists. Skipping.")
            continue
            
        # Create new instance with all fields copied
        new_c = Complaint()
        for field in c._meta.fields:
            value = getattr(c, field.name)
            if field.name == 'report_id':
                setattr(new_c, 'report_id', new_id)
            else:
                setattr(new_c, field.name, value)
        
        try:
            # Save new record
            new_c.save()
            # Delete old record
            c.delete()
        except Exception as e:
            print(f"ERROR migrating {old_id}: {str(e)}")

if __name__ == "__main__":
    migrate()
