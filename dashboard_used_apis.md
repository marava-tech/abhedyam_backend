# Dashboard Used APIs

All API calls made by the Abhedyam Dashboard frontend.

---

| # | Endpoint | Method | Times Used |
|---|----------|--------|------------|
| 1 | `/api/v1/admin/owners` | GET | 2 |
| 2 | `/api/v1/admin/owners/{ownerId}` | GET | 1 |
| 3 | `/api/v1/admin/owners/{ownerId}/subscription/upgrade` | POST | 1 |
| 4 | `/api/v1/admin/owners/{ownerId}/subscription/downgrade` | POST | 1 |
| 5 | `/api/v1/admin/feedbacks` | GET | 1 |
| 6 | `/api/v1/admin/image-store` | GET | 1 |
| 7 | `/api/v1/admin/image-store` | POST | 1 |
| 8 | `/api/v1/admin/image-store/{id}` | PATCH | 1 |
| 9 | `/api/v1/admin/owner-onboarding` | GET | 1 |
| 10 | `/api/v1/admin/owner-onboarding/{id}/status` | PATCH | 1 |
| 11 | `/api/v1/files/upload` | POST | 1 |

---

## Notes

- `/api/v1/admin/owners` is called **twice** — once during login to validate the admin key (`page=0&size=1`), and once to load the owners list.
- All API calls are centralised in `src/utils/api.js`.
