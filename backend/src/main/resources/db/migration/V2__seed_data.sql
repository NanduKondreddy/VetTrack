-- V2__seed_data.sql
-- Seed baseline data for VetTrack

-- Default users (admin / admin123, staff / staff123)
INSERT INTO users (username, password_hash, role, created_at, updated_at) VALUES
('admin', '$2a$12$C0q7/Otigqqe5GAKML5QXuKArf6KXleRdbMXXsNG2icYU.AzOcGPe', 'ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('staff', '$2a$12$ny/PAHYVIVPC48WUqMnN4uHnOWpDhakjVW8nGRUagAtRTiqIN8HcS', 'STAFF', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Vets
INSERT INTO vets (name, license_no, created_at, updated_at) VALUES
('Dr. Sarah Jenkins', 'VET-2018-0941', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Dr. Marcus Vance', 'VET-2020-1422', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Dr. Priya Patel', 'VET-2021-3810', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Vaccines
INSERT INTO vaccines (name, species_target, interval_days, created_at, updated_at) VALUES
('Rabies Core', 'Dog, Cat, Cattle', 365, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('DHPP 5-in-1', 'Dog', 365, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('FVRCP Feline Core', 'Cat', 365, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Bovine Resp Shield 9', 'Cattle', 180, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Newcastle-Bronchitis Avian', 'Poultry', 90, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Vaccine Batches
INSERT INTO vaccine_batches (vaccine_id, batch_no, expiry_date, quantity_remaining, created_at, updated_at) VALUES
(1, 'RAB-2026-A1', '2027-06-30', 45, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'RAB-2025-X9', '2026-11-15', 8, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP), -- Expiring soon (within 30 days)
(2, 'DHPP-2026-B4', '2027-08-20', 30, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'DHPP-2025-C2', '2026-10-10', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP), -- Expired & zero stock
(3, 'FVRCP-2026-F1', '2027-04-12', 25, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'BOV-2026-V3', '2027-01-30', 80, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'NEWC-2026-P8', '2026-12-01', 120, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Sample Owners
INSERT INTO owners (name, phone, email, city, created_at, updated_at) VALUES
('Elena Rostova', '+1-555-019-2834', 'elena.rostova@farmfresh.org', 'Austin', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('James Miller', '+1-555-014-9981', 'jmiller84@vetmail.com', 'Round Rock', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Amina Al-Mansoor', '+1-555-012-3344', 'amina.am@dairyvalley.com', 'Georgetown', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Carlos Mendez', '+1-555-018-7762', 'cmendez@pastures.net', 'San Marcos', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Animals
INSERT INTO animals (owner_id, name, species, breed, birth_date, sex, created_at, updated_at) VALUES
(1, 'Barnaby', 'Dog', 'Golden Retriever', '2022-03-15', 'Male', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'Cleo', 'Cat', 'Domestic Shorthair', '2023-05-20', 'Female', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Daisy', 'Cattle', 'Holstein Friesian', '2021-08-10', 'Female', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Bella', 'Cattle', 'Angus', '2022-01-14', 'Female', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'Flock Alpha', 'Poultry', 'Leghorn', '2024-02-01', 'Mixed', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'Thor', 'Dog', 'German Shepherd', '2020-11-05', 'Male', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Visits
INSERT INTO visits (animal_id, vet_id, visit_date, reason, notes, created_at, updated_at) VALUES
(1, 1, '2025-11-01', 'Annual Wellness Checkup', 'Healthy weight, clean coat, heart sounds normal.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 2, '2025-10-15', 'Routine Vaccination', 'No adverse reactions noted.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 3, '2026-05-10', 'Herd Herd-wide Inspection', 'Lactation cycle check, good rumen motility.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 1, '2025-10-20', 'Limping on right front leg', 'Mild sprain, prescribed rest for 1 week.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Vaccinations (covering overdue, due soon, and up to date)
INSERT INTO vaccinations (animal_id, vaccine_id, batch_id, given_on, next_due_on, visit_id, created_at, updated_at) VALUES
-- Overdue vaccination (next_due_on was 2026-10-15)
(1, 1, 1, '2025-10-15', '2026-10-15', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
-- Due within 14 days (next_due_on is 2026-11-05)
(2, 3, 5, '2025-11-05', '2026-11-05', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
-- Future vaccination (next_due_on in 2027)
(3, 4, 6, '2026-05-10', '2026-11-10', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 1, 1, '2025-11-12', '2026-11-12', 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
