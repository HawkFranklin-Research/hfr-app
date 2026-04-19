import firebase_admin
from firebase_admin import credentials, firestore
import csv
import json
from datetime import datetime

# Path to your service account key
SERVICE_ACCOUNT_PATH = "service-account.json"
OUTPUT_FILE = f"clinician_responses_{datetime.now().strftime('%Y%m%d_%H%M')}.csv"

def export_responses():
    # 1. Initialize Firebase
    if not firebase_admin._apps:
        cred = credentials.Certificate(SERVICE_ACCOUNT_PATH)
        firebase_admin.initialize_app(cred)
    
    db = firestore.client()
    
    print("🛰️ Connecting to Firestore...")
    responses_ref = db.collection("responses")
    docs = responses_ref.stream()
    
    data_list = []
    all_keys = set(["uid", "userEmail", "projectId", "questionId", "questionTitle", "timestamp"])
    disease_keys = set()

    print("📊 Processing responses...")
    for doc in docs:
        row = doc.to_dict()
        row_id = doc.id
        
        # Handle the "answer" field
        answer = row.get("answer", {})
        
        # If it's the probability dictionary, flatten it
        if isinstance(answer, dict):
            for disease, prob in answer.items():
                col_name = f"prob_{disease.replace(' ', '_').lower()}"
                row[col_name] = prob
                disease_keys.add(col_name)
            # Remove the original complex answer object
            if "answer" in row: del row["answer"]
        else:
            # If it's a simple multiple choice string
            row["final_answer"] = answer
            if "answer" in row: del row["answer"]

        # Convert timestamp to string if it exists
        if "timestamp" in row and row["timestamp"]:
            try:
                row["timestamp"] = row["timestamp"].isoformat()
            except:
                pass
                
        data_list.append(row)
        for k in row.keys():
            all_keys.add(k)

    if not data_list:
        print("❌ No responses found in the database yet.")
        return

    # 2. Sort columns: Metadata first, then probability columns
    metadata_cols = ["timestamp", "userEmail", "projectId", "questionId", "questionTitle", "final_answer"]
    sorted_diseases = sorted(list(disease_keys))
    fieldnames = metadata_cols + sorted_diseases
    
    # Add any extra keys that might have appeared
    for key in all_keys:
        if key not in fieldnames:
            fieldnames.append(key)

    # 3. Write to CSV
    with open(OUTPUT_FILE, mode='w', newline='', encoding='utf-8') as f:
        writer = csv.DictWriter(f, fieldnames=fieldnames, extrasaction='ignore')
        writer.writeheader()
        writer.writerows(data_list)

    print(f"✅ SUCCESS! Exported {len(data_list)} responses to: {OUTPUT_FILE}")
    print("   You can now open this file in Excel or Google Sheets.")

if __name__ == "__main__":
    export_responses()
