from rest_framework import viewsets, status
from rest_framework.decorators import api_view, action
from rest_framework.response import Response
from .models import Complaint, Notification, OTP
from .serializers import ComplaintSerializer, NotificationSerializer
from django.contrib.auth import authenticate
from django.contrib.auth.models import User
from django.core.mail import send_mail
from django.utils import timezone
import random
import datetime

class ComplaintViewSet(viewsets.ModelViewSet):
    queryset = Complaint.objects.all().order_by('-created_at')
    serializer_class = ComplaintSerializer
    lookup_field = 'report_id'

    def get_queryset(self):
        queryset = super().get_queryset()
        status_param = self.request.query_params.get('status')
        if status_param:
            queryset = queryset.filter(status__iexact=status_param)
        return queryset

    def create(self, request, *args, **kwargs):
        print(f"DEBUG: Incoming POST to /api/complaints/")
        print(f"DEBUG: request.data keys: {list(request.data.keys())}")
        print(f"DEBUG: request.FILES keys: {list(request.FILES.keys())}")
        
        serializer = self.get_serializer(data=request.data)
        if not serializer.is_valid():
            print(f"DEBUG: Serializer Errors: {serializer.errors}")
        
        response = super().create(request, *args, **kwargs)
        
        # Create notification for Authority
        if response.status_code == status.HTTP_201_CREATED:
            complaint_data = response.data
            Notification.objects.create(
                title="New Complaint Reported",
                message=f"A new complaint '{complaint_data.get('title')}' has been reported in {complaint_data.get('zone')}.",
                type='AUTHORITY'
            )
        return response
    
    @action(detail=True, methods=['post'])
    def delete_evidence(self, request, report_id=None):
        complaint = self.get_object()
        if complaint.status == 'Resolved':
            return Response({"error": "Cannot delete evidence of a resolved complaint"}, status=status.HTTP_400_BAD_REQUEST)
        
        # Clear evidence fields
        complaint.supervisor_name = None
        complaint.supervisor_image = None
        complaint.proof = None
        complaint.save()
        return Response({"message": "Evidence deleted successfully"})

    def perform_update(self, serializer):
        original_instance = self.get_object()
        original_status = original_instance.status
        
        # Check if supervisor evidence is being added/updated
        is_evidence_update = False
        if 'proof' in self.request.data or 'supervisor_image' in self.request.data:
            is_evidence_update = True
            
        instance = serializer.save()
        
        if is_evidence_update:
            instance.supervisor_updated_at = timezone.now()
            instance.save(update_fields=['supervisor_updated_at'])
        
        # Create notifications strictly based on Data and Status transitions
        if original_status != instance.status:
            if instance.status == 'Processing' or instance.status == 'In Progress':
                Notification.objects.create(
                    title="Action Required: Processing Started",
                    message=f"Authority has marked '{instance.title}' in {instance.zone} as Processing. Please investigate and submit evidence.",
                    type='SUPERVISOR'
                )
            elif instance.status == 'Resolved':
                Notification.objects.create(
                    title="Complaint Resolved",
                    message=f"Your complaint '{instance.title}' has been successfully resolved.",
                    type='CITIZEN',
                    user_email=instance.reporter_email
                )
            else:
                Notification.objects.create(
                    title="Complaint Status Updated",
                    message=f"Your complaint '{instance.title}' is now '{instance.status}'.",
                    type='CITIZEN',
                    user_email=instance.reporter_email
                )
        
        # Notify Authority when Supervisor submits evidence
        if is_evidence_update:
            Notification.objects.create(
                title="Evidence Submitted",
                message=f"Supervisor {instance.supervisor_name or 'Unknown'} has submitted evidence for '{instance.title}' in {instance.zone}.",
                type='AUTHORITY'
            )
            
        # Notify Authority when Citizen submits rating/feedback
        if 'rating' in self.request.data:
            Notification.objects.create(
                title="Citizen Feedback Received",
                message=f"A citizen has submitted a {instance.rating}-star rating for complaint '{instance.title}'.",
                type='AUTHORITY'
            )

class NotificationViewSet(viewsets.ModelViewSet):
    queryset = Notification.objects.all().order_by('-timestamp')
    serializer_class = NotificationSerializer

    def get_queryset(self):
        queryset = super().get_queryset().filter(is_read=False)
        ntype = self.request.query_params.get('type')
        email = self.request.query_params.get('email')
        
        if ntype:
            queryset = queryset.filter(type=ntype)
        if email:
            queryset = queryset.filter(user_email=email)
            
        return queryset

    @action(detail=False, methods=['post'])
    def mark_all_as_read(self, request):
        ntype = request.data.get('type')
        email = request.data.get('email')
        
        queryset = Notification.objects.filter(is_read=False)
        if ntype:
            queryset = queryset.filter(type=ntype)
        if email:
            queryset = queryset.filter(user_email=email)
            
        queryset.update(is_read=True)
        return Response({'message': 'All notifications marked as read'})

@api_view(['POST'])
def login_view(request):
    email = request.data.get('email')
    password = request.data.get('password')
    requested_role = request.data.get('role')
    
    try:
        user = User.objects.get(email=email)
        # Direct plain text comparison (Security Bypass)
        if user.password == password:
            
            # Fetch injected 'role_id' Raw column directly from auth_user
            from django.db import connection
            with connection.cursor() as cursor:
                cursor.execute("SELECT role_id FROM auth_user WHERE id = %s;", [user.id])
                row = cursor.fetchone()
                db_role_id = row[0] if row else 1
                
            actual_role = "CITIZEN"
            if db_role_id == 3:
                actual_role = "SUPERVISOR"
            elif db_role_id == 2:
                actual_role = "AUTHORITY"
                
            if requested_role and requested_role.upper() != actual_role:
                return Response({"error": "Unauthorized role"}, status=status.HTTP_403_FORBIDDEN)
                
            name = user.username
            mobile = getattr(user, 'mobile', '') # Initial fallback
            return Response({
                "token": "mock_token_123",
                "name": name,
                "email": user.email,
                "role": actual_role,
                "mobile": mobile
            })
        else:
            return Response({"error": "Incorrect password"}, status=status.HTTP_401_UNAUTHORIZED)
    except User.DoesNotExist:
        return Response({"error": "User not found"}, status=status.HTTP_404_NOT_FOUND)

@api_view(['POST'])
def update_profile(request):
    email = request.data.get('email')
    new_name = request.data.get('name')

    try:
        user = User.objects.get(email=email)
        
        if new_name:
            if User.objects.filter(username=new_name).exclude(email=email).exists():
                return Response({"error": "Username already exists"}, status=status.HTTP_400_BAD_REQUEST)
                
            user.username = new_name
            user.save()
            
            # Sync the new username globally to past reports
            Complaint.objects.filter(reporter_email=email).update(reporter=new_name)
            
        return Response({"message": "Profile updated successfully", "name": user.username})
    except User.DoesNotExist:
        return Response({"error": "User not found"}, status=status.HTTP_404_NOT_FOUND)

@api_view(['POST'])
def register_view(request):
    name = request.data.get('name')
    email = request.data.get('email')
    password = request.data.get('password')
    mobile = request.data.get('mobile')

    if not email or '@' not in email:
        return Response({"error": "Valid email is required"}, status=status.HTTP_400_BAD_REQUEST)

    if User.objects.filter(email=email).exists():
        return Response({"error": "This email is already registered"}, status=status.HTTP_400_BAD_REQUEST)

    # Derive username from email (full prefix before @)
    derived_username = email.split('@')[0]

    # Handle collision for derived username
    base_username = derived_username
    counter = 1
    while User.objects.filter(username=derived_username).exists():
        derived_username = f"{base_username}{counter}"
        counter += 1
    
    user = User(username=derived_username, email=email)
    user.is_superuser = True  # Granted native SuperUser capability
    user.password = password # Store as plain text (security bypass)
    user.save()
    
    # Store mobile if needed - for now we'll just return it in success
    return Response({"message": "User created successfully", "username": derived_username, "mobile": mobile}, status=status.HTTP_201_CREATED)

@api_view(['POST'])
def change_password(request):
    email = request.data.get('email')
    current_password = request.data.get('current_password')
    new_password = request.data.get('new_password')
    
    if not email or not current_password or not new_password:
        return Response({"error": "Missing required fields"}, status=status.HTTP_400_BAD_REQUEST)
        
    try:
        user = User.objects.get(email=email)
        # Direct comparison and direct update (Security Bypass)
        if user.password != current_password:
            return Response({"error": "Incorrect current password"}, status=status.HTTP_400_BAD_REQUEST)
        
        user.password = new_password
        user.save()
        return Response({"message": "Password updated successfully"})
    except User.DoesNotExist:
        return Response({"error": "User not found"}, status=status.HTTP_404_NOT_FOUND)
@api_view(['POST'])
def reset_password(request):
    email = request.data.get('email')
    new_password = request.data.get('new_password')
    
    if not email or not new_password:
        return Response({"error": "Missing email or new password"}, status=status.HTTP_400_BAD_REQUEST)
        
    try:
        from django.db.models import Q
        user = User.objects.get(Q(email=email) | Q(username=email))
        user.set_password(new_password)
        user.save()
        return Response({"message": "Password updated successfully"}, status=status.HTTP_200_OK)
    except User.DoesNotExist:
        return Response({"error": "User no longer exists"}, status=status.HTTP_404_NOT_FOUND)

@api_view(['POST'])
def send_otp(request):
    print(f"DEBUG: Incoming POST to /api/send-otp/ with data: {request.data}")
    email = request.data.get('email')
    if not email:
        return Response({"error": "Email/Username is required"}, status=status.HTTP_400_BAD_REQUEST)
    
    # Check if user exists by email or username
    from django.db.models import Q
    user = User.objects.filter(Q(email=email) | Q(username=email)).first()
    
    if not user:
        print(f"DEBUG: User not found for identifier: {email}")
        return Response({"error": "User with this email/ID does not exist"}, status=status.HTTP_404_NOT_FOUND)
    
    # Ensure user has an email for delivery, even if they logged in with ID
    recipient_email = user.email
    if not recipient_email:
        return Response({"error": "User account has no secondary email associated for recovery"}, status=status.HTTP_400_BAD_REQUEST)

    # Generate 4-digit OTP
    otp_code = str(random.randint(1000, 9999))
    print(f"\n{'='*50}\n!!! DEBUG: OTP FOR {recipient_email} IS: {otp_code} !!!\n{'='*50}\n")
    
    # Save to DB using the actual user's email
    OTP.objects.create(email=recipient_email, code=otp_code)
    
    # Send Email
    subject = 'Safe Roads Chennai - Password Reset OTP'
    message = f'Your OTP for password reset is: {otp_code}. It is valid for 5 minutes.'
    from_email = 'Safe Roads Chennai <chennaisaferoads@gmail.com>'
    
    try:
        print(f"DEBUG: Attempting to send email to {recipient_email}...")
        res = send_mail(subject, message, from_email, [recipient_email], fail_silently=False)
        print(f"DEBUG: send_mail returned: {res}")
        return Response({"message": "OTP sent successfully"})
    except Exception as e:
        print(f"DEBUG: Email error: {str(e)}")
        import traceback
        traceback.print_exc()
        return Response({"error": f"Failed to send email: {str(e)}"}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)

@api_view(['POST'])
def verify_otp(request):
    email = request.data.get('email')
    code = request.data.get('code')
    
    if not email or not code:
        return Response({"error": "Email and code are required"}, status=status.HTTP_400_BAD_REQUEST)
    
    # Get user to see if the input was a username/ID
    from django.db.models import Q
    user = User.objects.filter(Q(email=email) | Q(username=email)).first()
    actual_email = user.email if user else email

    # Get latest OTP for this actual email within last 5 minutes
    five_minutes_ago = timezone.now() - datetime.timedelta(minutes=5)
    otp_record = OTP.objects.filter(email=actual_email, code=code, created_at__gte=five_minutes_ago).order_by('-created_at').first()
    
    if otp_record:
        # Success - OTP is valid
        # Optionally delete OTP after use
        # otp_record.delete()
        return Response({"message": "OTP verified successfully"})
    else:
        return Response({"error": "Invalid or expired OTP"}, status=status.HTTP_400_BAD_REQUEST)
@api_view(['POST'])
def delete_account(request):
    email = request.data.get('email')
    if not email:
        return Response({"error": "Email is required"}, status=status.HTTP_400_BAD_REQUEST)

    try:
        user = User.objects.get(email=email)
        
        # Mark complaints as deleted
        complaints = Complaint.objects.filter(reporter_email=email)
        for complaint in complaints:
            if not complaint.reporter.endswith("(deleted)"):
                complaint.reporter = f"{complaint.reporter} (deleted)"
                complaint.save()
        
        user.delete()
        return Response({"message": "Account deleted successfully"})
    except User.DoesNotExist:
        return Response({"error": "User not found"}, status=status.HTTP_404_NOT_FOUND)
