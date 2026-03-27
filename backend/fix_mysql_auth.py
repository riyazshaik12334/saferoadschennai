import pymysql
import sys

def restore_auth_columns():
    try:
        connection = pymysql.connect(
            host='localhost',
            user='root',
            password='',
            database='saferoads_db'
        )
        with connection.cursor() as cursor:
            print("Adding missing columns to auth_user...")
            try:
                cursor.execute("ALTER TABLE auth_user ADD COLUMN first_name varchar(150) NOT NULL DEFAULT ''")
                print("Added first_name")
            except Exception as e:
                print(f"first_name might already exist: {e}")
                
            try:
                cursor.execute("ALTER TABLE auth_user ADD COLUMN last_name varchar(150) NOT NULL DEFAULT ''")
                print("Added last_name")
            except Exception as e:
                print(f"last_name might already exist: {e}")

            connection.commit()
            print("Columns restored successfully.")
            
    except Exception as e:
        print(f"Error connecting to MySQL: {e}")
    finally:
        if 'connection' in locals():
            connection.close()

if __name__ == "__main__":
    restore_auth_columns()
