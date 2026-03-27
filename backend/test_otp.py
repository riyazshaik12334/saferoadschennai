import requests
import json

BASE_URL = "https://pathwayed-uglily-tenley.ngrok-free.dev/api/"
EMAIL = "tabrez@example.com" # Replace with a real test email if needed

def test_send_otp():
    url = BASE_URL + "send-otp/"
    data = {"email": EMAIL}
    headers = {"Content-Type": "application/json", "ngrok-skip-browser-warning": "true"}
    
    print(f"Testing POST {url} with {data}")
    try:
        response = requests.post(url, data=json.dumps(data), headers=headers)
        print(f"Status Code: {response.status_code}")
        print(f"Response: {response.text}")
    except Exception as e:
        print(f"Error: {str(e)}")

if __name__ == "__main__":
    test_send_otp()
