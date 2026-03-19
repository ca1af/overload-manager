CREATE TABLE workout_sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    session_date DATE NOT NULL,
    notes VARCHAR(500) NULL,
    started_at TIMESTAMP NULL,
    finished_at TIMESTAMP NULL,
    duration_seconds INT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_workout_sessions_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_workout_sessions_user_date ON workout_sessions(user_id, session_date);
