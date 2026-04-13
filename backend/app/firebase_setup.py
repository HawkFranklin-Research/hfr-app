from __future__ import annotations

from pathlib import Path

import firebase_admin
from firebase_admin import credentials, firestore

from .config import service_account_path


def get_firestore_client():
    try:
        app = firebase_admin.get_app()
    except ValueError:
        cred_path = service_account_path()
        if cred_path and Path(cred_path).exists():
            app = firebase_admin.initialize_app(credentials.Certificate(cred_path))
        else:
            return None
    return firestore.client(app=app)
