from __future__ import annotations

from dataclasses import dataclass, field
from datetime import datetime, timezone
from typing import Any
from uuid import uuid4

from . import config


def utc_now() -> str:
    return datetime.now(timezone.utc).isoformat()


SEED_PROJECTS = [
    {
        "id": "derma_ai",
        "name": "Derma AI",
        "shortDescription": "Case-based dermatology labeling and review feed.",
        "longDescription": "Structured dermoscopy and lesion review cases for clinicians. This stream is intended for annotation quality checks, differential capture, and model evaluation against real-world review behavior.",
        "consentText": "By continuing, you agree that your answers and any case-level interpretation you submit may be used for internal research, model evaluation, and dataset quality improvement. No patient identifiers should be entered into the response fields.",
        "accentHex": "#C9722B",
        "logoKey": "derma",
        "questionCount": 3,
        "liveStatus": "Live dataset collection",
    },
    {
        "id": "telemedicine",
        "name": "Telemedicine",
        "shortDescription": "Remote triage reasoning and clinician handoff practice.",
        "longDescription": "Telemedicine scenarios focused on response urgency, remote escalation, and concise clinician-to-clinician summaries. This stream is designed for quality review on digital-first workflows.",
        "consentText": "By continuing, you agree that your triage choices, response timing estimates, and handoff notes may be collected for internal workflow research and operations analysis. Do not include direct patient identifiers in your answers.",
        "accentHex": "#2F6CE5",
        "logoKey": "telemedicine",
        "questionCount": 3,
        "liveStatus": "Pilot workflow live",
    },
]

SEED_QUESTIONNAIRES = {
    "derma_ai": [
        {
            "id": "derma-1",
            "title": "Primary pattern match",
            "prompt": "Which option best matches the visible lesion pattern shown in the case images?",
            "helper": "This mirrors the structured case collection used by the Android app.",
            "type": "multiple_choice",
            "options": [
                "A. Atopic dermatitis / eczema flare",
                "B. Psoriatic plaque morphology",
                "C. Tinea corporis ring pattern",
                "D. Herpes zoster distribution",
            ],
        },
        {
            "id": "derma-2",
            "title": "Estimated duration",
            "prompt": "How many days has this lesion pattern likely been active?",
            "helper": "Use a whole number of days.",
            "type": "numeric",
            "options": [],
        },
        {
            "id": "derma-3",
            "title": "Differential note",
            "prompt": "Write the concise differential note you would attach for downstream review.",
            "helper": "Use one or two sentences.",
            "type": "text",
            "options": [],
        },
    ],
    "telemedicine": [
        {
            "id": "tm-1",
            "title": "Referral urgency",
            "prompt": "How urgent is this telemedicine case based on the symptom timing and summary?",
            "helper": "Choose the level that best reflects the next clinical action.",
            "type": "multiple_choice",
            "options": [
                "A. Immediate same-day clinician response",
                "B. Response within 24 hours",
                "C. Routine follow-up within 72 hours",
                "D. Educational guidance only",
            ],
        },
        {
            "id": "tm-2",
            "title": "Expected callback window",
            "prompt": "Enter the maximum callback window in hours before the case becomes overdue.",
            "helper": "Use a numeric value.",
            "type": "numeric",
            "options": [],
        },
        {
            "id": "tm-3",
            "title": "Consult note",
            "prompt": "Write the handoff note another clinician should read before opening the chart.",
            "helper": "Focus on triage logic and what should happen next.",
            "type": "text",
            "options": [],
        },
    ],
}


@dataclass
class InMemoryStore:
    users: dict[str, dict[str, Any]] = field(default_factory=dict)
    projects: dict[str, dict[str, Any]] = field(default_factory=lambda: {p["id"]: dict(p) for p in SEED_PROJECTS})
    consents: list[dict[str, Any]] = field(default_factory=list)
    sessions: dict[str, dict[str, Any]] = field(default_factory=dict)
    responses: list[dict[str, Any]] = field(default_factory=list)

    def get_or_create_user(self, uid: str, email: str, name: str) -> dict[str, Any]:
        existing = self.users.get(uid)
        if existing:
            return existing
        created = {
            "uid": uid,
            "displayName": name,
            "email": email,
            "institution": "",
            "specialty": "",
            "onboardingComplete": False,
            "createdAt": utc_now(),
        }
        self.users[uid] = created
        return created

    def list_projects(self) -> list[dict[str, Any]]:
        return list(self.projects.values())

    def get_project(self, project_id: str) -> dict[str, Any] | None:
        return self.projects.get(project_id)

    def get_questionnaire(self, project_id: str) -> list[dict[str, Any]]:
        return SEED_QUESTIONNAIRES.get(project_id, [])

    def record_consent(self, uid: str, project_id: str, project_name: str) -> dict[str, Any]:
        entry = {
            "uid": uid,
            "projectId": project_id,
            "projectName": project_name,
            "acceptedAt": utc_now(),
        }
        self.consents.append(entry)
        return entry

    def submit_responses(
        self,
        uid: str,
        project_id: str,
        project_name: str,
        display_name: str,
        institution: str,
        specialty: str,
        answers: list[dict[str, Any]],
    ) -> dict[str, Any]:
        session_id = str(uuid4())
        session = {
            "id": session_id,
            "uid": uid,
            "projectId": project_id,
            "projectName": project_name,
            "displayName": display_name,
            "institution": institution,
            "specialty": specialty,
            "answerCount": len(answers),
            "createdAt": utc_now(),
        }
        self.sessions[session_id] = session
        for answer in answers:
            self.responses.append(
                {
                    "id": str(uuid4()),
                    "uid": uid,
                    "sessionId": session_id,
                    "projectId": project_id,
                    **answer,
                    "createdAt": utc_now(),
                }
            )
        return session

    def metrics(self, uid: str) -> dict[str, int]:
        return {
            "completedResponses": len([item for item in self.responses if item["uid"] == uid]),
            "activeConsents": len([item for item in self.consents if item["uid"] == uid]),
        }


class FirestoreStore:
    def __init__(self, client):
        self.client = client
        self.users = client.collection(config.FIRESTORE_USERS)
        self.projects = client.collection(config.FIRESTORE_PROJECTS)
        self.consents = client.collection(config.FIRESTORE_CONSENTS)
        self.sessions = client.collection(config.FIRESTORE_SESSIONS)
        self.responses = client.collection(config.FIRESTORE_RESPONSES)

    def get_or_create_user(self, uid: str, email: str, name: str) -> dict[str, Any]:
        doc = self.users.document(uid).get()
        if doc.exists:
            return {"uid": uid, **doc.to_dict()}
        payload = {
            "displayName": name,
            "email": email,
            "institution": "",
            "specialty": "",
            "onboardingComplete": False,
            "createdAt": utc_now(),
        }
        self.users.document(uid).set(payload)
        return {"uid": uid, **payload}

    def list_projects(self) -> list[dict[str, Any]]:
        docs = list(self.projects.stream())
        if not docs:
            for project in SEED_PROJECTS:
                self.projects.document(project["id"]).set(project)
            return SEED_PROJECTS
        return [{"id": doc.id, **doc.to_dict()} for doc in docs]

    def get_project(self, project_id: str) -> dict[str, Any] | None:
        doc = self.projects.document(project_id).get()
        if doc.exists:
            return {"id": doc.id, **doc.to_dict()}
        return next((project for project in SEED_PROJECTS if project["id"] == project_id), None)

    def get_questionnaire(self, project_id: str) -> list[dict[str, Any]]:
        return SEED_QUESTIONNAIRES.get(project_id, [])

    def record_consent(self, uid: str, project_id: str, project_name: str) -> dict[str, Any]:
        payload = {
            "uid": uid,
            "projectId": project_id,
            "projectName": project_name,
            "acceptedAt": utc_now(),
        }
        self.consents.document(f"{uid}_{project_id}").set(payload)
        return payload

    def submit_responses(
        self,
        uid: str,
        project_id: str,
        project_name: str,
        display_name: str,
        institution: str,
        specialty: str,
        answers: list[dict[str, Any]],
    ) -> dict[str, Any]:
        session_id = str(uuid4())
        session = {
            "uid": uid,
            "projectId": project_id,
            "projectName": project_name,
            "displayName": display_name,
            "institution": institution,
            "specialty": specialty,
            "answerCount": len(answers),
            "createdAt": utc_now(),
        }
        self.sessions.document(session_id).set(session)
        batch = self.client.batch()
        for answer in answers:
            response_id = str(uuid4())
            batch.set(
                self.responses.document(response_id),
                {
                    "uid": uid,
                    "sessionId": session_id,
                    "projectId": project_id,
                    **answer,
                    "createdAt": utc_now(),
                },
            )
        batch.update(
            self.users.document(uid),
            {
                "latestProjectId": project_id,
                "lastSubmittedAt": utc_now(),
            },
        )
        batch.commit()
        return {"id": session_id, **session}

    def metrics(self, uid: str) -> dict[str, int]:
        responses = self.responses.where("uid", "==", uid).stream()
        consents = self.consents.where("uid", "==", uid).stream()
        return {
            "completedResponses": sum(1 for _ in responses),
            "activeConsents": sum(1 for _ in consents),
        }
