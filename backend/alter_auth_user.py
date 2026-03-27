import os
import django
from django.db import connection

os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'backend_project.settings')
django.setup()

with connection.cursor() as cursor:
    # Safely inject columns into native auth_user framework
    try:
        cursor.execute("ALTER TABLE auth_user ADD COLUMN is_authority BOOLEAN DEFAULT 0;")
        print("Injected 'is_authority' boolean column successfully.")
    except Exception as e:
        print("is_authority may already exist:", e)

    try:
        cursor.execute("ALTER TABLE auth_user ADD COLUMN is_supervisor BOOLEAN DEFAULT 0;")
        print("Injected 'is_supervisor' boolean column successfully.")
    except Exception as e:
        print("is_supervisor may already exist:", e)
        
    try:
        cursor.execute("ALTER TABLE auth_user ADD COLUMN role_id INT DEFAULT 1;")
        print("Injected 'role_id' integer column successfully.")
    except Exception as e:
        print("role_id may already exist:", e)

    # Sync existing user strings over to formal ID maps
    cursor.execute("UPDATE auth_user SET role_id = 1;") # Default Citizen fallback
    cursor.execute("UPDATE auth_user SET role_id = 2, is_authority = 1 WHERE email LIKE 'EMP%' OR email LIKE 'auth_%' OR (is_staff = 1 AND email NOT LIKE 'SUP%');")
    cursor.execute("UPDATE auth_user SET role_id = 3, is_supervisor = 1 WHERE email LIKE 'SUP%';")

    print("\nDatabase sync completed! Successfully fully mapped Citizen (1), Authority (2), and Supervisor (3) values.")
