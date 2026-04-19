import firebase_admin
from firebase_admin import credentials, auth
import os

# Path to your service account key
SERVICE_ACCOUNT_PATH = "/Users/vatsal1/Projects/git2/hawk-app/hfr-app/backend/service-account.json"

def create_test_user():
    cred = credentials.Certificate(SERVICE_ACCOUNT_PATH)
    firebase_admin.initialize_app(cred)

    email = "test@hawkfranklin.in"
    password = "HawkTest2026!"

    try:
        user = auth.create_user(
            email=email,
            password=password,
            display_name="Test Clinician"
        )
        print(f"Successfully created new test user: {user.uid}")
        print(f"Login: {email}")
        print(f"Password: {password}")
    except Exception as e:
        if "EMAIL_EXISTS" in str(e) or "already exists" in str(e).lower():
            # If user exists, update the password to the known one
            user = auth.get_user_by_email(email)
            auth.update_user(user.uid, password=password)
            print(f"User already existed. Password has been reset to: {password}")
        else:
            print(f"Error: {e}")

if __name__ == "__main__":
    create_test_user()
