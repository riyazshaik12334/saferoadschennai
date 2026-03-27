import sqlite3
import os

def sqlite_to_mysql_dump(sqlite_path, sql_output_path):
    if not os.path.exists(sqlite_path):
        print(f"Error: {sqlite_path} not found.")
        return

    conn = sqlite3.connect(sqlite_path)
    cursor = conn.cursor()

    # Get all tables
    cursor.execute("SELECT name FROM sqlite_master WHERE type='table' AND name NOT LIKE 'sqlite_%';")
    tables = [t[0] for t in cursor.fetchall()]

    with open(sql_output_path, 'w', encoding='utf-8') as f:
        f.write("-- MySQL Dump converted from SQLite\n")
        f.write("SET SQL_MODE = \"NO_AUTO_VALUE_ON_ZERO\";\n")
        f.write("SET time_zone = \"+00:00\";\n\n")

        for table in tables:
            # Get table schema
            cursor.execute(f"PRAGMA table_info({table});")
            columns = cursor.fetchall()
            
            f.write(f"-- Table structure for table `{table}`\n")
            f.write(f"CREATE TABLE IF NOT EXISTS `{table}` (\n")
            
            col_defs = []
            primary_keys = []
            for col in columns:
                name, data_type, notnull, dflt_value, pk = col[1], col[2], col[3], col[4], col[5]
                
                # Basic type mapping
                mysql_type = data_type.upper()
                if "VARCHAR" in mysql_type or "TEXT" in mysql_type:
                    mysql_type = "TEXT" if "TEXT" in mysql_type else "VARCHAR(255)"
                elif "INT" in mysql_type:
                    mysql_type = "INT(11)"
                elif mysql_type == "":
                    mysql_type = "TEXT" # Fallback for undefined types

                col_def = f"  `{name}` {mysql_type}"
                if notnull: col_def += " NOT NULL"
                if pk:
                    col_def += " AUTO_INCREMENT" if "INT" in mysql_type else ""
                    primary_keys.append(f"`{name}`")
                
                col_defs.append(col_def)
            
            if primary_keys:
                col_defs.append(f"  PRIMARY KEY ({', '.join(primary_keys)})")
                
            f.write(",\n".join(col_defs))
            f.write("\n) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;\n\n")

            # Get data
            cursor.execute(f"SELECT * FROM {table};")
            rows = cursor.fetchall()
            
            if rows:
                f.write(f"-- Dumping data for table `{table}`\n")
                col_names = ", ".join([f"`{c[1]}`" for c in columns])
                
                for row in rows:
                    values = []
                    for val in row:
                        if val is None:
                            values.append("NULL")
                        elif isinstance(val, (int, float)):
                            values.append(str(val))
                        else:
                            # Escape single quotes
                            escaped = str(val).replace("'", "''")
                            values.append(f"'{escaped}'")
                    
                    f.write(f"INSERT INTO `{table}` ({col_names}) VALUES ({', '.join(values)});\n")
                f.write("\n")

    conn.close()
    print(f"Successfully generated {sql_output_path}")

if __name__ == "__main__":
    sqlite_to_mysql_dump('db.sqlite3', 'saferoads_migration.sql')
