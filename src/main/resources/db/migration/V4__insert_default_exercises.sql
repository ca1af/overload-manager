-- Chest exercises
INSERT INTO exercises (name_ko, name_en, category, exercise_type, equipment, primary_muscle, secondary_muscles, default_sets_min, default_sets_max, default_reps_min, default_reps_max, is_custom) VALUES
('벤치 프레스', 'Bench Press', 'CHEST', 'COMPOUND', 'BARBELL', 'Pectoralis Major', 'Anterior Deltoid,Triceps', 3, 5, 5, 8, FALSE),
('인클라인 벤치 프레스', 'Incline Bench Press', 'CHEST', 'COMPOUND', 'BARBELL', 'Upper Pectoralis', 'Anterior Deltoid,Triceps', 3, 4, 6, 10, FALSE),
('딥스', 'Dips', 'CHEST', 'COMPOUND', 'BODYWEIGHT', 'Pectoralis Major', 'Triceps,Anterior Deltoid', 3, 4, 8, 12, FALSE),
('덤벨 플라이', 'Dumbbell Fly', 'CHEST', 'ISOLATION', 'DUMBBELL', 'Pectoralis Major', 'Anterior Deltoid', 3, 4, 10, 15, FALSE),
('케이블 플라이', 'Cable Fly', 'CHEST', 'ISOLATION', 'CABLE', 'Pectoralis Major', 'Anterior Deltoid', 3, 4, 10, 15, FALSE),
('펙 덱 머신', 'Pec Deck Machine', 'CHEST', 'ISOLATION', 'MACHINE', 'Pectoralis Major', '', 3, 4, 10, 15, FALSE);

-- Back exercises
INSERT INTO exercises (name_ko, name_en, category, exercise_type, equipment, primary_muscle, secondary_muscles, default_sets_min, default_sets_max, default_reps_min, default_reps_max, is_custom) VALUES
('데드리프트', 'Deadlift', 'BACK', 'COMPOUND', 'BARBELL', 'Erector Spinae', 'Gluteus Maximus,Hamstrings,Trapezius', 3, 5, 3, 6, FALSE),
('바벨 로우', 'Barbell Row', 'BACK', 'COMPOUND', 'BARBELL', 'Latissimus Dorsi', 'Rhomboids,Biceps,Rear Deltoid', 3, 4, 6, 10, FALSE),
('풀업', 'Pull-up', 'BACK', 'COMPOUND', 'BODYWEIGHT', 'Latissimus Dorsi', 'Biceps,Rhomboids', 3, 4, 6, 12, FALSE),
('친업', 'Chin-up', 'BACK', 'COMPOUND', 'BODYWEIGHT', 'Latissimus Dorsi', 'Biceps,Brachialis', 3, 4, 6, 12, FALSE),
('시티드 케이블 로우', 'Seated Cable Row', 'BACK', 'COMPOUND', 'CABLE', 'Latissimus Dorsi', 'Rhomboids,Biceps', 3, 4, 8, 12, FALSE),
('랫 풀다운', 'Lat Pulldown', 'BACK', 'COMPOUND', 'CABLE', 'Latissimus Dorsi', 'Biceps,Rhomboids', 3, 4, 8, 12, FALSE),
('원암 덤벨 로우', 'One-Arm Dumbbell Row', 'BACK', 'COMPOUND', 'DUMBBELL', 'Latissimus Dorsi', 'Rhomboids,Biceps', 3, 4, 8, 12, FALSE),
('페이스 풀', 'Face Pull', 'BACK', 'ISOLATION', 'CABLE', 'Rear Deltoid', 'Rhomboids,Trapezius', 3, 4, 12, 20, FALSE);

-- Legs exercises
INSERT INTO exercises (name_ko, name_en, category, exercise_type, equipment, primary_muscle, secondary_muscles, default_sets_min, default_sets_max, default_reps_min, default_reps_max, is_custom) VALUES
('스쿼트', 'Squat', 'LEGS', 'COMPOUND', 'BARBELL', 'Quadriceps', 'Gluteus Maximus,Hamstrings,Erector Spinae', 3, 5, 5, 8, FALSE),
('레그 프레스', 'Leg Press', 'LEGS', 'COMPOUND', 'MACHINE', 'Quadriceps', 'Gluteus Maximus,Hamstrings', 3, 4, 8, 12, FALSE),
('루마니안 데드리프트', 'Romanian Deadlift', 'LEGS', 'COMPOUND', 'BARBELL', 'Hamstrings', 'Gluteus Maximus,Erector Spinae', 3, 4, 8, 12, FALSE),
('런지', 'Lunge', 'LEGS', 'COMPOUND', 'DUMBBELL', 'Quadriceps', 'Gluteus Maximus,Hamstrings', 3, 4, 10, 12, FALSE),
('레그 익스텐션', 'Leg Extension', 'LEGS', 'ISOLATION', 'MACHINE', 'Quadriceps', '', 3, 4, 10, 15, FALSE),
('레그 컬', 'Leg Curl', 'LEGS', 'ISOLATION', 'MACHINE', 'Hamstrings', '', 3, 4, 10, 15, FALSE),
('힙 어브덕션', 'Hip Abduction', 'LEGS', 'ISOLATION', 'MACHINE', 'Gluteus Medius', 'Gluteus Minimus', 3, 4, 12, 15, FALSE),
('카프 레이즈', 'Calf Raise', 'LEGS', 'ISOLATION', 'MACHINE', 'Gastrocnemius', 'Soleus', 3, 4, 12, 20, FALSE);

-- Shoulders exercises
INSERT INTO exercises (name_ko, name_en, category, exercise_type, equipment, primary_muscle, secondary_muscles, default_sets_min, default_sets_max, default_reps_min, default_reps_max, is_custom) VALUES
('오버헤드 프레스(바벨)', 'Overhead Press (Barbell)', 'SHOULDERS', 'COMPOUND', 'BARBELL', 'Anterior Deltoid', 'Lateral Deltoid,Triceps', 3, 5, 5, 8, FALSE),
('오버헤드 프레스(덤벨)', 'Overhead Press (Dumbbell)', 'SHOULDERS', 'COMPOUND', 'DUMBBELL', 'Anterior Deltoid', 'Lateral Deltoid,Triceps', 3, 4, 8, 12, FALSE),
('사이드 레터럴 레이즈', 'Side Lateral Raise', 'SHOULDERS', 'ISOLATION', 'DUMBBELL', 'Lateral Deltoid', '', 3, 4, 12, 20, FALSE),
('프론트 레이즈', 'Front Raise', 'SHOULDERS', 'ISOLATION', 'DUMBBELL', 'Anterior Deltoid', '', 3, 4, 12, 15, FALSE),
('리어 델트 플라이', 'Rear Delt Fly', 'SHOULDERS', 'ISOLATION', 'DUMBBELL', 'Rear Deltoid', 'Rhomboids', 3, 4, 12, 20, FALSE);

-- Biceps exercises
INSERT INTO exercises (name_ko, name_en, category, exercise_type, equipment, primary_muscle, secondary_muscles, default_sets_min, default_sets_max, default_reps_min, default_reps_max, is_custom) VALUES
('바벨 컬', 'Barbell Curl', 'BICEPS', 'ISOLATION', 'BARBELL', 'Biceps Brachii', 'Brachialis', 3, 4, 8, 12, FALSE),
('덤벨 컬', 'Dumbbell Curl', 'BICEPS', 'ISOLATION', 'DUMBBELL', 'Biceps Brachii', 'Brachialis', 3, 4, 8, 12, FALSE),
('해머 컬', 'Hammer Curl', 'BICEPS', 'ISOLATION', 'DUMBBELL', 'Brachioradialis', 'Biceps Brachii', 3, 4, 8, 12, FALSE),
('케이블 컬', 'Cable Curl', 'BICEPS', 'ISOLATION', 'CABLE', 'Biceps Brachii', 'Brachialis', 3, 4, 10, 15, FALSE);

-- Triceps exercises
INSERT INTO exercises (name_ko, name_en, category, exercise_type, equipment, primary_muscle, secondary_muscles, default_sets_min, default_sets_max, default_reps_min, default_reps_max, is_custom) VALUES
('트라이셉스 푸시다운', 'Triceps Pushdown', 'TRICEPS', 'ISOLATION', 'CABLE', 'Triceps', '', 3, 4, 10, 15, FALSE),
('오버헤드 익스텐션', 'Overhead Extension', 'TRICEPS', 'ISOLATION', 'DUMBBELL', 'Triceps Long Head', '', 3, 4, 10, 15, FALSE),
('스컬크러셔', 'Skull Crusher', 'TRICEPS', 'ISOLATION', 'BARBELL', 'Triceps', '', 3, 4, 8, 12, FALSE);

-- Core exercises
INSERT INTO exercises (name_ko, name_en, category, exercise_type, equipment, primary_muscle, secondary_muscles, default_sets_min, default_sets_max, default_reps_min, default_reps_max, is_custom) VALUES
('플랭크', 'Plank', 'CORE', 'ISOLATION', 'BODYWEIGHT', 'Rectus Abdominis', 'Transverse Abdominis,Obliques', 3, 4, 30, 60, FALSE),
('크런치', 'Crunch', 'CORE', 'ISOLATION', 'BODYWEIGHT', 'Rectus Abdominis', 'Obliques', 3, 4, 15, 25, FALSE),
('레그 레이즈', 'Leg Raise', 'CORE', 'ISOLATION', 'BODYWEIGHT', 'Lower Rectus Abdominis', 'Hip Flexors', 3, 4, 10, 20, FALSE),
('케이블 크런치', 'Cable Crunch', 'CORE', 'ISOLATION', 'CABLE', 'Rectus Abdominis', 'Obliques', 3, 4, 12, 20, FALSE);
