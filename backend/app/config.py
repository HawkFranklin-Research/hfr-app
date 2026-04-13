from pathlib import Path
import os


BACKEND_DIR = Path(__file__).resolve().parents[1]
DEFAULT_SERVICE_ACCOUNT = BACKEND_DIR / "service-account.json"

FIRESTORE_USERS = os.getenv("HFR_FIRESTORE_USERS", "users")
FIRESTORE_PROJECTS = os.getenv("HFR_FIRESTORE_PROJECTS", "projects")
FIRESTORE_CONSENTS = os.getenv("HFR_FIRESTORE_CONSENTS", "consents")
FIRESTORE_SESSIONS = os.getenv("HFR_FIRESTORE_SESSIONS", "questionnaire_sessions")
FIRESTORE_RESPONSES = os.getenv("HFR_FIRESTORE_RESPONSES", "responses")


def service_account_path() -> str | None:
    explicit = os.getenv("GOOGLE_APPLICATION_CREDENTIALS")
    if explicit:
        return explicit
    if DEFAULT_SERVICE_ACCOUNT.exists():
        return str(DEFAULT_SERVICE_ACCOUNT)
    return None
