import os
import django
from django.db import connection

os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'backend_project.settings')
django.setup()

with connection.cursor() as cursor:
    cursor.execute("SET FOREIGN_KEY_CHECKS = 0;")
    cursor.execute("SELECT table_name FROM information_schema.tables WHERE table_schema = 'saferoads_db';")
    tables = cursor.fetchall()
    for table in tables:
        cursor.execute(f"DROP TABLE IF EXISTS `{table[0]}`;")
    cursor.execute("SET FOREIGN_KEY_CHECKS = 1;")
    print("All tables dropped successfully.")
