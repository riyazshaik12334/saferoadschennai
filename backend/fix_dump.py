import os
import json

try:
    with open('data_dump_utf8.json', 'r', encoding='utf-8-sig') as f:
        data = f.read()
    print("Read UTF-8-sig successfully...")
    with open('data_dump_clean.json', 'w', encoding='utf-8') as f:
        f.write(data)
    print("Wrote pure UTF-8 successfully...")

    os.system('py manage.py loaddata data_dump_clean.json')
    print("Loaddata triggered.")
except Exception as e:
    print(e)
