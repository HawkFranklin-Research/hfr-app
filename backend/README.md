# HawkFranklin backend

This backend is the service counterpart to the Android app's Firebase-backed clinician workflow.

## 📊 Data Export (The "Gold")
To download the clinician responses for your research, use the provided export script. This connects to the live production database and flattens the 10-disease probability data into a clean CSV file.

### How to export:
1. Ensure `service-account.json` is present in the `backend/` folder.
2. Run the export script:
   ```bash
   cd hfr-app/backend
   python export_results.py
   ```
3. A new file named `clinician_responses_YYYYMMDD_HHMM.csv` will be created. You can open this directly in **Excel or Google Sheets**.

---

## 🚀 Seeding Data
To update the projects or questionnaires on the live website:
1. Edit `hfr-app/backend/app/store.py` with your new cases/projects.
2. Run the seeding script:
   ```bash
   python seed_firestore.py
   ```

---

## 🛠️ Local API Run (Optional)
If you need to run the local FastAPI server for metrics or API testing:
```bash
cd hfr-app/backend
python -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
uvicorn app.main:app --reload
```

## 🔐 Firebase Authentication
The backend uses the service account file at `backend/service-account.json`. Ensure this file is never committed to version control.
