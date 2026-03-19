CREATE TABLE workout_sets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_exercise_id BIGINT NOT NULL,
    set_number INT NOT NULL,
    weight DECIMAL(7,2) NOT NULL DEFAULT 0.00,
    reps INT NOT NULL DEFAULT 0,
    completed BOOLEAN NOT NULL DEFAULT FALSE,
    rest_seconds INT NULL,
    completed_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_workout_sets_session_exercise FOREIGN KEY (session_exercise_id) REFERENCES session_exercises(id) ON DELETE CASCADE
);

CREATE INDEX idx_workout_sets_session_exercise ON workout_sets(session_exercise_id);
