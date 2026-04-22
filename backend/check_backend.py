import firebase_admin
from firebase_admin import credentials, firestore, storage
import json

# Path to your service account key
SERVICE_ACCOUNT_PATH = "service-account.json"

def check_backend():
    if not firebase_admin._apps:
        cred = credentials.Certificate(SERVICE_ACCOUNT_PATH)
        # We need to specify the storage bucket to use the storage admin SDK
        firebase_admin.initialize_app(cred, {
            'storageBucket': 'hfr-app-6c756.firebasestorage.app'
        })

    db = firestore.client()
    
    print("--- 👤 CHECKING FIRESTORE (Users) ---")
    users_ref = db.collection("users")
    # Looking for our specific test emails
    query = users_ref.where("email", "in", ["final_test@hawkfranklin.in", "newtest_onboard@hawkfranklin.in"]).stream()
    
    found_users = False
    for doc in query:
        found_users = True
        data = doc.to_dict()
        print(f"UID: {doc.id}")
        print(f"Name: {data.get('fullName')}")
        print(f"Affiliation: {data.get('affiliation')}")
        print(f"Certificate URL: {data.get('certificateUrl')}")
        print("-" * 20)
        
    if not found_users:
        print("No test users found in Firestore.")

    print("\n--- 📁 CHECKING STORAGE (verifications/) ---")
    bucket = storage.bucket()
    blobs = bucket.list_blobs(prefix="verifications/")
    
    found_files = False
    for blob in blobs:
        found_files = True
        print(f"File Name: {blob.name}")
        print(f"Size: {blob.size} bytes")
        print(f"Content Type: {blob.content_type}")
        print("-" * 20)
        
    if not found_files:
        print("No files found in the verifications/ folder.")

if __name__ == "__main__":
    check_backend()
