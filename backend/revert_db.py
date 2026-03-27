import os

os.system('py drop_db.py')

migration_dir = 'api/migrations'
if os.path.exists(migration_dir):
    for f in os.listdir(migration_dir):
        if f.startswith('0') and f.endswith('.py'):
            os.remove(os.path.join(migration_dir, f))
            print(f"Deleted migration: {f}")

try:
    with open('data_dump.json', 'r', encoding='utf-16le') as f:
        data = f.read()
    with open('data_dump_utf8.json', 'w', encoding='utf-8') as f:
        f.write(data)
    print("Converted data_dump.json to UTF-8.")
except Exception as e:
    print("Error converting file:", e)

os.system('py manage.py makemigrations api')
os.system('py manage.py migrate')
os.system('py manage.py loaddata data_dump_utf8.json')
print("Restore completely processed.")
