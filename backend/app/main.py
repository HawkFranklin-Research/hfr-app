from __future__ import annotations

from functools import lru_cache
from typing import Annotated

from fastapi import Depends, FastAPI, HTTPException
from pydantic import BaseModel, Field

from .auth import CurrentUser, get_current_user
from .firebase_setup import get_firestore_client
from .store import FirestoreStore, InMemoryStore


class AnswerPayload(BaseModel):
    questionId: str
    questionTitle: str
    value: str


class ResponseSubmitPayload(BaseModel):
    displayName: str
    institution: str
    specialty: str
    answers: list[AnswerPayload] = Field(default_factory=list)


@lru_cache
def get_store():
    client = get_firestore_client()
    if client is not None:
        return FirestoreStore(client)
    return InMemoryStore()


app = FastAPI(title="HawkFranklin Backend", version="0.1.0")


@app.get("/health")
def health():
    store = get_store()
    return {
        "status": "ok",
        "storage": "firestore" if isinstance(store, FirestoreStore) else "memory",
    }


@app.get("/me")
def me(current_user: Annotated[CurrentUser, Depends(get_current_user)]):
    store = get_store()
    return store.get_or_create_user(
        uid=current_user.uid,
        email=current_user.email,
        name=current_user.name,
    )


@app.get("/projects")
def list_projects():
    return {"projects": get_store().list_projects()}


@app.get("/projects/{project_id}/questionnaire")
def questionnaire(project_id: str):
    project = get_store().get_project(project_id)
    if project is None:
        raise HTTPException(status_code=404, detail="Project not found")
    return {
        "project": project,
        "questions": get_store().get_questionnaire(project_id),
    }


@app.post("/projects/{project_id}/consent")
def consent(
    project_id: str,
    current_user: Annotated[CurrentUser, Depends(get_current_user)],
):
    store = get_store()
    project = store.get_project(project_id)
    if project is None:
        raise HTTPException(status_code=404, detail="Project not found")
    return store.record_consent(
        uid=current_user.uid,
        project_id=project_id,
        project_name=project["name"],
    )


@app.post("/projects/{project_id}/responses")
def submit_responses(
    project_id: str,
    payload: ResponseSubmitPayload,
    current_user: Annotated[CurrentUser, Depends(get_current_user)],
):
    store = get_store()
    project = store.get_project(project_id)
    if project is None:
      raise HTTPException(status_code=404, detail="Project not found")
    if not payload.answers:
      raise HTTPException(status_code=400, detail="At least one answer is required")
    return store.submit_responses(
        uid=current_user.uid,
        project_id=project_id,
        project_name=project["name"],
        display_name=payload.displayName,
        institution=payload.institution,
        specialty=payload.specialty,
        answers=[answer.model_dump() for answer in payload.answers],
    )


@app.get("/metrics")
def metrics(current_user: Annotated[CurrentUser, Depends(get_current_user)]):
    return get_store().metrics(current_user.uid)
