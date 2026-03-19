CREATE TABLE exercises (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_by BIGINT NULL,
    name_ko VARCHAR(100) NOT NULL,
    name_en VARCHAR(100) NOT NULL,
    category VARCHAR(20) NOT NULL,
    exercise_type VARCHAR(20) NOT NULL,
    equipment VARCHAR(20) NOT NULL,
    primary_muscle VARCHAR(50) NOT NULL,
    secondary_muscles VARCHAR(500) NULL,
    default_sets_min INT NOT NULL DEFAULT 3,
    default_sets_max INT NOT NULL DEFAULT 5,
    default_reps_min INT NOT NULL DEFAULT 8,
    default_reps_max INT NOT NULL DEFAULT 12,
    is_custom BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_exercises_user FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL
);

CREATE INDEX idx_exercises_category ON exercises(category);
CREATE INDEX idx_exercises_is_custom ON exercises(is_custom);
