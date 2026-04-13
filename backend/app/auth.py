from __future__ import annotations

from typing import Annotated

from fastapi import Header, HTTPException
from pydantic import BaseModel

import firebase_admin
from firebase_admin import auth as firebase_auth


class CurrentUser(BaseModel):
    uid: str
    email: str
    name: str


def get_current_user(
    authorization: Annotated[str | None, Header()] = None,
    x_demo_uid: Annotated[str | None, Header()] = None,
    x_demo_email: Annotated[str | None, Header()] = None,
) -> CurrentUser:
    if authorization and authorization.startswith("Bearer "):
        token = authorization.removeprefix("Bearer ").strip()
        try:
            decoded = firebase_auth.verify_id_token(token)
            return CurrentUser(
                uid=decoded["uid"],
                email=decoded.get("email", ""),
                name=decoded.get("name", decoded.get("email", "HawkFranklin user")),
            )
        except ValueError:
            pass
        except firebase_admin.exceptions.FirebaseError as exc:
            raise HTTPException(status_code=401, detail=f"Invalid Firebase token: {exc}") from exc

    return CurrentUser(
        uid=x_demo_uid or "demo-clinician",
        email=x_demo_email or "demo@hawkfranklin.internal",
        name="Demo Clinician",
    )
