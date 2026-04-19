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
        "name": "Dermatology AI",
        "shortDescription": "Official clinical evaluation for disease differentials (Nov13 set).",
        "longDescription": "Professional clinical evaluation set using high-resolution images. Clinicians are asked to provide probability weights across 10 common skin conditions based on three distinct photographic views.",
        "consentText": "By continuing, you agree to provide your professional medical opinion for these cases. This data will be used to benchmark clinical reasoning and model performance. All data is anonymized.",
        "accentHex": "#D9A441",
        "logoKey": "derma",
        "questionCount": 10,
        "liveStatus": "Official Clinical Test",
    }
]

# Mapping the 10 real cases to the new project ID 'derma_ai'
SEED_QUESTIONNAIRES = {
    "derma_ai": [
        {
            "id": "clin-nov13-1",
            "title": "Case #1",
            "prompt": "Assign probability percentages to the following differentials based on the 3 views provided.",
            "helper": "Total should ideally be 100%, but we record each value independently.",
            "type": "probability_grid",
            "images": [
                "https://firebasestorage.googleapis.com/v0/b/hfr-app-6c756.firebasestorage.app/o/cases%2Fcase_1_folliculitis%2Fview_1.png?alt=media",
                "https://firebasestorage.googleapis.com/v0/b/hfr-app-6c756.firebasestorage.app/o/cases%2Fcase_1_folliculitis%2Fview_2.png?alt=media",
                "https://firebasestorage.googleapis.com/v0/b/hfr-app-6c756.firebasestorage.app/o/cases%2Fcase_1_folliculitis%2Fview_3.png?alt=media"
            ],
            "options": ["Folliculitis", "Psoriasis", "Tinea", "Urticaria", "Herpes Zoster", "Drug Rash", "Insect Bite", "Irritant Contact Dermatitis", "Allergic Contact Dermatitis", "Eczema"],
            "patientCode": "-1499518133606453111"
        },
        {
            "id": "clin-nov13-2",
            "title": "Case #2",
            "prompt": "Assign probability percentages to the following differentials based on the 3 views provided.",
            "helper": "Total should ideally be 100%, but we record each value independently.",
            "type": "probability_grid",
            "images": [
                "https://firebasestorage.googleapis.com/v0/b/hfr-app-6c756.firebasestorage.app/o/cases%2Fcase_2_psoriasis%2Fview_1.png?alt=media",
                "https://firebasestorage.googleapis.com/v0/b/hfr-app-6c756.firebasestorage.app/o/cases%2Fcase_2_psoriasis%2Fview_2.png?alt=media",
                "https://firebasestorage.googleapis.com/v0/b/hfr-app-6c756.firebasestorage.app/o/cases%2Fcase_2_psoriasis%2Fview_3.png?alt=media"
            ],
            "options": ["Folliculitis", "Psoriasis", "Tinea", "Urticaria", "Herpes Zoster", "Drug Rash", "Insect Bite", "Irritant Contact Dermatitis", "Allergic Contact Dermatitis", "Eczema"],
            "patientCode": "-1101392520794914783"
        },
        {
            "id": "clin-nov13-3",
            "title": "Case #3",
            "prompt": "Assign probability percentages to the following differentials based on the 3 views provided.",
            "helper": "Total should ideally be 100%, but we record each value independently.",
            "type": "probability_grid",
            "images": [
                "https://firebasestorage.googleapis.com/v0/b/hfr-app-6c756.firebasestorage.app/o/cases%2Fcase_3_tinea%2Fview_1.png?alt=media",
                "https://firebasestorage.googleapis.com/v0/b/hfr-app-6c756.firebasestorage.app/o/cases%2Fcase_3_tinea%2Fview_2.png?alt=media",
                "https://firebasestorage.googleapis.com/v0/b/hfr-app-6c756.firebasestorage.app/o/cases%2Fcase_3_tinea%2Fview_3.png?alt=media"
            ],
            "options": ["Folliculitis", "Psoriasis", "Tinea", "Urticaria", "Herpes Zoster", "Drug Rash", "Insect Bite", "Irritant Contact Dermatitis", "Allergic Contact Dermatitis", "Eczema"],
            "patientCode": "-460018212599407689"
        },
        {
            "id": "clin-nov13-4",
            "title": "Case #4",
            "prompt": "Assign probability percentages to the following differentials based on the 3 views provided.",
            "helper": "Total should ideally be 100%, but we record each value independently.",
            "type": "probability_grid",
            "images": [
                "https://firebasestorage.googleapis.com/v0/b/hfr-app-6c756.firebasestorage.app/o/cases%2Fcase_4_urticaria%2Fview_1.png?alt=media",
                "https://firebasestorage.googleapis.com/v0/b/hfr-app-6c756.firebasestorage.app/o/cases%2Fcase_4_urticaria%2Fview_2.png?alt=media",
                "https://firebasestorage.googleapis.com/v0/b/hfr-app-6c756.firebasestorage.app/o/cases%2Fcase_4_urticaria%2Fview_3.png?alt=media"
            ],
            "options": ["Folliculitis", "Psoriasis", "Tinea", "Urticaria", "Herpes Zoster", "Drug Rash", "Insect Bite", "Irritant Contact Dermatitis", "Allergic Contact Dermatitis", "Eczema"],
            "patientCode": "-1073544188024944010"
        },
        {
            "id": "clin-nov13-5",
            "title": "Case #5",
            "prompt": "Assign probability percentages to the following differentials based on the 3 views provided.",
            "helper": "Total should ideally be 100%, but we record each value independently.",
            "type": "probability_grid",
            "images": [
                "https://firebasestorage.googleapis.com/v0/b/hfr-app-6c756.firebasestorage.app/o/cases%2Fcase_5_herpes_zoster%2Fview_1.png?alt=media",
                "https://firebasestorage.googleapis.com/v0/b/hfr-app-6c756.firebasestorage.app/o/cases%2Fcase_5_herpes_zoster%2Fview_2.png?alt=media",
                "https://firebasestorage.googleapis.com/v0/b/hfr-app-6c756.firebasestorage.app/o/cases%2Fcase_5_herpes_zoster%2Fview_3.png?alt=media"
            ],
            "options": ["Folliculitis", "Psoriasis", "Tinea", "Urticaria", "Herpes Zoster", "Drug Rash", "Insect Bite", "Irritant Contact Dermatitis", "Allergic Contact Dermatitis", "Eczema"],
            "patientCode": "-2994712363562385732"
        },
        {
            "id": "clin-nov13-6",
            "title": "Case #6",
            "prompt": "Assign probability percentages to the following differentials based on the 3 views provided.",
            "helper": "Total should ideally be 100%, but we record each value independently.",
            "type": "probability_grid",
            "images": [
                "https://firebasestorage.googleapis.com/v0/b/hfr-app-6c756.firebasestorage.app/o/cases%2Fcase_6_folliculitis%2Fview_1.png?alt=media",
                "https://firebasestorage.googleapis.com/v0/b/hfr-app-6c756.firebasestorage.app/o/cases%2Fcase_6_folliculitis%2Fview_2.png?alt=media",
                "https://firebasestorage.googleapis.com/v0/b/hfr-app-6c756.firebasestorage.app/o/cases%2Fcase_6_folliculitis%2Fview_3.png?alt=media"
            ],
            "options": ["Folliculitis", "Psoriasis", "Tinea", "Urticaria", "Herpes Zoster", "Drug Rash", "Insect Bite", "Irritant Contact Dermatitis", "Allergic Contact Dermatitis", "Eczema"],
            "patientCode": "-1679661288171171796"
        },
        {
            "id": "clin-nov13-7",
            "title": "Case #7",
            "prompt": "Assign probability percentages to the following differentials based on the 3 views provided.",
            "helper": "Total should ideally be 100%, but we record each value independently.",
            "type": "probability_grid",
            "images": [
                "https://firebasestorage.googleapis.com/v0/b/hfr-app-6c756.firebasestorage.app/o/cases%2Fcase_7_drug_rash%2Fview_1.png?alt=media",
                "https://firebasestorage.googleapis.com/v0/b/hfr-app-6c756.firebasestorage.app/o/cases%2Fcase_7_drug_rash%2Fview_2.png?alt=media",
                "https://firebasestorage.googleapis.com/v0/b/hfr-app-6c756.firebasestorage.app/o/cases%2Fcase_7_drug_rash%2Fview_3.png?alt=media"
            ],
            "options": ["Folliculitis", "Psoriasis", "Tinea", "Urticaria", "Herpes Zoster", "Drug Rash", "Insect Bite", "Irritant Contact Dermatitis", "Allergic Contact Dermatitis", "Eczema"],
            "patientCode": "-194017562508969044"
        },
        {
            "id": "clin-nov13-8",
            "title": "Case #8",
            "prompt": "Assign probability percentages to the following differentials based on the 3 views provided.",
            "helper": "Total should ideally be 100%, but we record each value independently.",
            "type": "probability_grid",
            "images": [
                "https://firebasestorage.googleapis.com/v0/b/hfr-app-6c756.firebasestorage.app/o/cases%2Fcase_8_insect_bite%2Fview_1.png?alt=media",
                "https://firebasestorage.googleapis.com/v0/b/hfr-app-6c756.firebasestorage.app/o/cases%2Fcase_8_insect_bite%2Fview_2.png?alt=media",
                "https://firebasestorage.googleapis.com/v0/b/hfr-app-6c756.firebasestorage.app/o/cases%2Fcase_8_insect_bite%2Fview_3.png?alt=media"
            ],
            "options": ["Folliculitis", "Psoriasis", "Tinea", "Urticaria", "Herpes Zoster", "Drug Rash", "Insect Bite", "Irritant Contact Dermatitis", "Allergic Contact Dermatitis", "Eczema"],
            "patientCode": "-1248246033945054233"
        },
        {
            "id": "clin-nov13-9",
            "title": "Case #9",
            "prompt": "Assign probability percentages to the following differentials based on the 3 views provided.",
            "helper": "Total should ideally be 100%, but we record each value independently.",
            "type": "probability_grid",
            "images": [
                "https://firebasestorage.googleapis.com/v0/b/hfr-app-6c756.firebasestorage.app/o/cases%2Fcase_9_psoriasis%2Fview_1.png?alt=media",
                "https://firebasestorage.googleapis.com/v0/b/hfr-app-6c756.firebasestorage.app/o/cases%2Fcase_9_psoriasis%2Fview_2.png?alt=media",
                "https://firebasestorage.googleapis.com/v0/b/hfr-app-6c756.firebasestorage.app/o/cases%2Fcase_9_psoriasis%2Fview_3.png?alt=media"
            ],
            "options": ["Folliculitis", "Psoriasis", "Tinea", "Urticaria", "Herpes Zoster", "Drug Rash", "Insect Bite", "Irritant Contact Dermatitis", "Allergic Contact Dermatitis", "Eczema"],
            "patientCode": "-1964801097019886136"
        },
        {
            "id": "clin-nov13-10",
            "title": "Case #10",
            "prompt": "Assign probability percentages to the following differentials based on the 3 views provided.",
            "helper": "Total should ideally be 100%, but we record each value independently.",
            "type": "probability_grid",
            "images": [
                "https://firebasestorage.googleapis.com/v0/b/hfr-app-6c756.firebasestorage.app/o/cases%2Fcase_10_irritant_contact_dermatitis%2Fview_1.png?alt=media",
                "https://firebasestorage.googleapis.com/v0/b/hfr-app-6c756.firebasestorage.app/o/cases%2Fcase_10_irritant_contact_dermatitis%2Fview_2.png?alt=media",
                "https://firebasestorage.googleapis.com/v0/b/hfr-app-6c756.firebasestorage.app/o/cases%2Fcase_10_irritant_contact_dermatitis%2Fview_3.png?alt=media"
            ],
            "options": ["Folliculitis", "Psoriasis", "Tinea", "Urticaria", "Herpes Zoster", "Drug Rash", "Insect Bite", "Irritant Contact Dermatitis", "Allergic Contact Dermatitis", "Eczema"],
            "patientCode": "-4920504467922873434"
        }
    ]
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
        # Clear projects in DB first or just overwrite? Let's assume we want to sync
        docs = list(self.projects.stream())
        # To strictly enforce our NEW set of projects, we should ideally clear the old ones
        # but for safety, we just push our current SEED_PROJECTS
        for project in SEED_PROJECTS:
            self.projects.document(project["id"]).set(project)
        return SEED_PROJECTS

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
