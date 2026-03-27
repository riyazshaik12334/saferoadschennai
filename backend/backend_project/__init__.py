import pymysql

# Spoof mysqlclient version BEFORE any Django imports
pymysql.version_info = (2, 2, 1, "final", 0)
pymysql.install_as_MySQLdb()

# Now we can safely import Django's database components
from django.db.backends.mysql.base import DatabaseWrapper
from django.db.backends.mysql.features import DatabaseFeatures

# Spoof MariaDB server version to pass the check
@property
def patched_server_info(self):
    return "10.6.0-MariaDB"

DatabaseWrapper.mysql_server_info = patched_server_info

# Explicitly disable 'RETURNING' clause support (for MariaDB < 10.5)
@property
def patched_can_return_columns_from_insert(self):
    return False

DatabaseFeatures.can_return_columns_from_insert = patched_can_return_columns_from_insert
DatabaseFeatures.can_return_rows_from_bulk_insert = patched_can_return_columns_from_insert
