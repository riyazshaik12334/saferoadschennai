from django.urls import path, include
from rest_framework.routers import DefaultRouter
from .views import ComplaintViewSet, NotificationViewSet, login_view, register_view, update_profile, change_password, reset_password, send_otp, verify_otp, delete_account

router = DefaultRouter()
router.register(r'complaints', ComplaintViewSet)
router.register(r'notifications', NotificationViewSet)

urlpatterns = [
    path('', include(router.urls)),
    path('login/', login_view),
    path('register/', register_view),
    path('update-profile/', update_profile),
    path('change-password/', change_password),
    path('reset-password/', reset_password),
    path('send-otp/', send_otp),
    path('verify-otp/', verify_otp),
    path('delete-account/', delete_account),
]
