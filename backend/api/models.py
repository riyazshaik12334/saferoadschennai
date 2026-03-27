from django.db import models
from django.db.models.signals import post_delete, post_save
from django.dispatch import receiver
import os

class Complaint(models.Model):
    report_id = models.CharField(max_length=50, unique=True, primary_key=True)
    title = models.CharField(max_length=255)
    zone = models.CharField(max_length=100)
    reporter = models.CharField(max_length=100)
    status = models.CharField(max_length=50, default='Pending')
    date = models.CharField(max_length=100)
    priority = models.CharField(max_length=20)
    image_res_id = models.IntegerField(default=0)
    image = models.ImageField(upload_to='complaints/', null=True, blank=True)
    description = models.TextField(null=True, blank=True)
    latitude = models.FloatField()
    longitude = models.FloatField()
    reporter_mobile = models.CharField(max_length=20)
    reporter_email = models.CharField(max_length=100, default='unknown@link.com')
    created_at = models.DateTimeField(auto_now_add=True, null=True, blank=True)
    
    # Supervisor Evidence Architecture
    supervisor_name = models.CharField(max_length=150, null=True, blank=True)
    supervisor_image_res_id = models.IntegerField(default=0)
    supervisor_image = models.ImageField(upload_to='supervisor_evidence/', null=True, blank=True)
    proof = models.ImageField(upload_to='proof_evidence/', null=True, blank=True)
    supervisor_updated_at = models.DateTimeField(null=True, blank=True)
    
    # Authority attribution and Citizen feedback
    authority_name = models.CharField(max_length=150, null=True, blank=True)
    rating = models.IntegerField(null=True, blank=True)
    rating_description = models.TextField(null=True, blank=True)

    def __str__(self):
        return f"{self.report_id} - {self.title}"

@receiver(post_delete, sender=Complaint)
def auto_delete_file_on_delete(sender, instance, **kwargs):
    if instance.image:
        if os.path.isfile(instance.image.path):
            os.remove(instance.image.path)

class Notification(models.Model):
    NOTIFICATION_TYPES = (
        ('CITIZEN', 'Citizen'),
        ('AUTHORITY', 'Authority'),
        ('SUPERVISOR', 'Supervisor'),
    )
    
    title = models.CharField(max_length=200)
    message = models.TextField()
    timestamp = models.DateTimeField(auto_now_add=True)
    is_read = models.BooleanField(default=False)
    type = models.CharField(max_length=20, choices=NOTIFICATION_TYPES)
    user_email = models.EmailField(null=True, blank=True) # To target specific citizens

    def __str__(self):
        return f"{self.title} ({self.type})"

class OTP(models.Model):
    email = models.EmailField()
    code = models.CharField(max_length=4)
    created_at = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return f"{self.email} - {self.code}"
