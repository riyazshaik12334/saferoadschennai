import urllib.request
import json

URL = "https://pathwayed-uglily-tenley.ngrok-free.dev/api/send-otp/"
DATA = {"email": "riyazshaik.lg2@gmail.com"}

def test_otp():
    try:
        req = urllib.request.Request(URL)
        req.add_header('Content-Type', 'application/json')
        req.add_header('ngrok-skip-browser-warning', 'true')
        
        jsondata = json.dumps(DATA).encode('utf-8')
        
        print(f"Requesting {URL}...")
        with urllib.request.urlopen(req, data=jsondata) as response:
            print(f"Status: {response.status}")
            print(f"Body: {response.read().decode('utf-8')}")
    except urllib.error.HTTPError as e:
        print(f"HTTP Error: {e.code}")
        print(f"Reason: {e.read().decode('utf-8')}")
    except Exception as e:
        print(f"Error: {str(e)}")

if __name__ == "__main__":
    test_otp()
