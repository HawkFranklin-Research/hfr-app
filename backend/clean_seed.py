import firebase_admin
from firebase_admin import credentials, firestore
from app.store import SEED_PROJECTS, SEED_QUESTIONNAIRES
import os

# Path to your service account key
SERVICE_ACCOUNT_PATH = "service-account.json"

def clean_and_seed():
    if not firebase_admin._apps:
        cred = credentials.Certificate(SERVICE_ACCOUNT_PATH)
        firebase_admin.initialize_app(cred)

    db = firestore.client()
    print("🧹 Cleaning projects collection...")
    
    # 1. Delete all existing projects
    projects_ref = db.collection("projects")
    docs = projects_ref.stream()
    for doc in docs:
        print(f"  - Deleting old project: {doc.id}")
        doc.reference.delete()

    print("🚀 Seeding fresh Dermatology AI project...")
    # 2. Seed only the new projects
    for project in SEED_PROJECTS:
        project_id = project["id"]
        print(f"  - Seeding project: {project['name']} ({project_id})")
        db.collection("projects").document(project_id).set(project)

        # 3. Seed questions
        questions = SEED_QUESTIONNAIRES.get(project_id, [])
        print(f"    - Seeding {len(questions)} questions for {project_id}")
        for q in questions:
            db.collection("projects").document(project_id).collection("questionnaire").document(q["id"]).set(q)

    print("\n✨ SUCCESS: Your dashboard is now clean and branded!")

if __name__ == "__main__":
    clean_and_seed()
