-- Migración 003: outbox_submissions para reintentos cuando /api/submissions falla 5xx/1102
-- Ejecutar: turso db shell staffaxis < db/migrations/003_outbox_submissions.sql

CREATE TABLE IF NOT EXISTS outbox_submissions (
    id              TEXT PRIMARY KEY,
    created_at      INTEGER NOT NULL,
    status          TEXT NOT NULL DEFAULT 'pending',
    attempts        INTEGER NOT NULL DEFAULT 0,
    next_retry_at   INTEGER NOT NULL,
    last_error      TEXT,
    dedup_key       TEXT UNIQUE,
    payload_json    TEXT NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_outbox_submissions_status_next_retry
ON outbox_submissions(status, next_retry_at);
