# HawkFranklin backend

This backend is the service counterpart to the Android app's Firebase-backed clinician workflow.

It exposes:

- `GET /health`
- `GET /me`
- `GET /projects`
- `GET /projects/{project_id}/questionnaire`
- `POST /projects/{project_id}/consent`
- `POST /projects/{project_id}/responses`
- `GET /metrics`

## Local run

```bash
cd backend
python -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
uvicorn app.main:app --reload
```

## Firebase mode

Place a service account file at `backend/service-account.json` or set `GOOGLE_APPLICATION_CREDENTIALS`.

Without credentials, the backend runs in in-memory demo mode so the API surface is still testable.
