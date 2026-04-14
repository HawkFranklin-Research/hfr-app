import sys
import os
from pathlib import Path

# Add the current directory to sys.path so we can import from .app
sys.path.append(str(Path(__file__).resolve().parent))

from app.store import FirestoreStore, SEED_PROJECTS, SEED_QUESTIONNAIRES
from app.firebase_setup import get_firestore_client

def seed():
    print("Connecting to Firestore...")
    client = get_firestore_client()
    if client is None:
        print("ERROR: Could not connect to Firestore. Check your service-account.json file.")
        return

    store = FirestoreStore(client)
    
    print("Seeding projects...")
    for project in SEED_PROJECTS:
        print(f"  - Seeding project: {project['name']} ({project['id']})")
        store.projects.document(project['id']).set(project)
        
        # Seed questionnaires for each project
        questionnaire = SEED_QUESTIONNAIRES.get(project['id'], [])
        q_collection = store.projects.document(project['id']).collection('questionnaire')
        
        print(f"    - Seeding {len(questionnaire)} questions for {project['id']}")
        for q in questionnaire:
            q_collection.document(q['id']).set(q)
            
    print("\nSUCCESS: Firestore has been seeded with production data!")
    print("You can now refresh your app at https://hfr-app-6c756.web.app to see the real tiles.")

if __name__ == "__main__":
    seed()
