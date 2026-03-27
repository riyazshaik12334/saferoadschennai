# Safe Roads Chennai - Backend Startup Guide

Your Python installation is located at a specific path that isn't fully added to your system's PATH yet. To start the server, please use these EXACT commands in your terminal:

## 1. Start the Django Server
Open a terminal in `C:\Users\user\AndroidStudioProjects\SafeRoadsChennaiSRC\backend` and run:
```powershell
& "C:\Users\user\AppData\Local\Programs\Python\Python312\python.exe" manage.py runserver 8000
```

## 2. Start the Ngrok Tunnel
Open a SECOND terminal and run:
```powershell
ngrok http 8000 --url pathwayed-uglily-tenley.ngrok-free.dev
```

---

### Why did the error happen?
The error `Python was not found` happens because Windows is trying to use the "App Execution Alias" (Microsoft Store version) instead of the one we installed. Using the full path `"C:\Users\user\AppData\Local\Programs\Python\Python312\python.exe"` bypasses this error.

### Admin Credentials
- **URL**: [https://pathwayed-uglily-tenley.ngrok-free.dev/admin/](https://pathwayed-uglily-tenley.ngrok-free.dev/admin/)
- **User**: `admin`
- **Pass**: `admin_password`
