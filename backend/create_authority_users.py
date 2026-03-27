from django.contrib.auth.models import User
import sys

def create_authority_users():
    users_to_create = [
        ('Officer3', 'EMP33333@src.com', 'admin123'),
        ('Officer4', 'EMP44444@src.com', 'admin123'),
        ('AuthSpecial', 'auth_special@src.com', 'admin123'),
    ]
    
    for username, email, password in users_to_create:
        if not User.objects.filter(email=email).exists():
            user = User.objects.create_user(username=username, email=email, password=password)
            # The current backend logic uses plain text password comparison in login_view, 
            # but User.create_user hashes it. 
            # However, looking at login_view in views.py:
            # 91: if user.password == password:
            # This means the project is using plain text passwords!
            # So I should set the password directly to the attribute.
            user.password = password
            user.save()
            print(f"Created authority user: {username} ({email})")
        else:
            print(f"User with email {email} already exists.")

if __name__ == "__main__":
    create_authority_users()
