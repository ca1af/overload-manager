CREATE TABLE session_exercises (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_id BIGINT NOT NULL,
    exercise_id BIGINT NOT NULL,
    order_index INT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_session_exercises_session FOREIGN KEY (session_id) REFERENCES workout_sessions(id) ON DELETE CASCADE,
    CONSTRAINT fk_session_exercises_exercise FOREIGN KEY (exercise_id) REFERENCES exercises(id) ON DELETE CASCADE
);

CREATE INDEX idx_session_exercises_session ON session_exercises(session_id);
