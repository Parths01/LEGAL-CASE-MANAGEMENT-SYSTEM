-- ============================================================
-- Legal Case Management System – Demo Seed Data
-- Passwords are BCrypt hashes of "Admin123!"
-- ============================================================

-- ► USERS
-- Admin
INSERT IGNORE INTO users (user_id, name, email, password, role, phone, address, status)
VALUES (1001, 'Admin User', 'admin@legal.com',
        '$2a$12$Ow3l8A3KNlkV93IOnfEOMeUjXF1o3JjivpX2TGO/y5YFxjEJVF1xe',
        'ADMIN', '9000000001', '1 Admin Lane, Mumbai', 'ACTIVE');

-- Advocates
INSERT IGNORE INTO users (user_id, name, email, password, role, phone, address, status)
VALUES (1002, 'John Advocate', 'john.advocate@legal.com',
        '$2a$12$Ow3l8A3KNlkV93IOnfEOMeUjXF1o3JjivpX2TGO/y5YFxjEJVF1xe',
        'ADVOCATE', '9000000002', '2 Barrister St, Delhi', 'ACTIVE');

INSERT IGNORE INTO users (user_id, name, email, password, role, phone, address, status)
VALUES (1003, 'Jane Advocate', 'jane.advocate@legal.com',
        '$2a$12$Ow3l8A3KNlkV93IOnfEOMeUjXF1o3JjivpX2TGO/y5YFxjEJVF1xe',
        'ADVOCATE', '9000000003', '3 Counsel Rd, Bangalore', 'ACTIVE');

-- Clients
INSERT IGNORE INTO users (user_id, name, email, password, role, phone, address, status)
VALUES (1004, 'Ravi Client', 'ravi.client@email.com',
        '$2a$12$Ow3l8A3KNlkV93IOnfEOMeUjXF1o3JjivpX2TGO/y5YFxjEJVF1xe',
        'CLIENT', '9000000004', '4 Client Ave, Pune', 'ACTIVE');

INSERT IGNORE INTO users (user_id, name, email, password, role, phone, address, status)
VALUES (1005, 'Priya Client', 'priya.client@email.com',
        '$2a$12$Ow3l8A3KNlkV93IOnfEOMeUjXF1o3JjivpX2TGO/y5YFxjEJVF1xe',
        'CLIENT', '9000000005', '5 Resident Blvd, Chennai', 'ACTIVE');

INSERT IGNORE INTO users (user_id, name, email, password, role, phone, address, status)
VALUES (1006, 'Suresh Corp', 'suresh.corp@company.com',
        '$2a$12$Ow3l8A3KNlkV93IOnfEOMeUjXF1o3JjivpX2TGO/y5YFxjEJVF1xe',
        'CLIENT', '9000000006', '6 Corporate Park, Hyderabad', 'ACTIVE');

-- Clerks
INSERT IGNORE INTO users (user_id, name, email, password, role, phone, address, status)
VALUES (1007, 'Anjali Clerk', 'anjali.clerk@legal.com',
        '$2a$12$Ow3l8A3KNlkV93IOnfEOMeUjXF1o3JjivpX2TGO/y5YFxjEJVF1xe',
        'CLERK', '9000000007', '7 Office Lane, Mumbai', 'ACTIVE');

INSERT IGNORE INTO users (user_id, name, email, password, role, phone, address, status)
VALUES (1008, 'Vikram Clerk', 'vikram.clerk@legal.com',
        '$2a$12$Ow3l8A3KNlkV93IOnfEOMeUjXF1o3JjivpX2TGO/y5YFxjEJVF1xe',
        'CLERK', '9000000008', '8 Registry Rd, Delhi', 'ACTIVE');

-- ► LAW FIRM
INSERT IGNORE INTO law_firms (firm_id, firm_name, address, email, phone, registration_number)
VALUES (2001, 'SmartLegal Associates', '10 Law St, Mumbai', 'info@smartlegal.com',
        '0222000001', 'MH-LF-2001-001');

-- ► ADVOCATES PROFILE
INSERT IGNORE INTO advocates (advocate_id, user_id, firm_id, bar_registration_number,
                               specialization, experience_years, qualification, court_practice)
VALUES (3001, 1002, 2001, 'BAR-MH-001', 'Civil & Property Law', 8,
        'LLB, LLM (Mumbai University)', 'Bombay High Court, District Courts');

INSERT IGNORE INTO advocates (advocate_id, user_id, firm_id, bar_registration_number,
                               specialization, experience_years, qualification, court_practice)
VALUES (3002, 1003, 2001, 'BAR-DL-002', 'Criminal & Family Law', 6,
        'LLB (Delhi University)', 'Delhi High Court, Family Courts');

-- ► CLIENTS PROFILE
INSERT IGNORE INTO clients (client_id, user_id, address, company_name, gstin, pan_number, client_type)
VALUES (4001, 1004, '4 Client Ave, Pune', NULL, NULL, 'ABCDE1234F', 'INDIVIDUAL');

INSERT IGNORE INTO clients (client_id, user_id, address, company_name, gstin, pan_number, client_type)
VALUES (4002, 1005, '5 Resident Blvd, Chennai', NULL, NULL, 'PQRST5678G', 'INDIVIDUAL');

INSERT IGNORE INTO clients (client_id, user_id, address, company_name, gstin, pan_number, client_type)
VALUES (4003, 1006, '6 Corporate Park, Hyderabad', 'Suresh Corp Pvt Ltd',
        '29ABCDE1234F1Z5', 'FGHIJ9012H', 'CORPORATE');

-- ► CASES
INSERT IGNORE INTO cases (case_id, case_number, case_title, case_type, client_id, advocate_id,
                           court_name, court_type, filing_date, status, priority,
                           description, opposing_party, judge_name)
VALUES (5001, 'LC-2024-001',
        'Ravi vs State – Property Dispute',
        'PROPERTY', 4001, 3001,
        'Bombay High Court', 'HIGH_COURT', '2024-03-10',
        'IN_PROGRESS', 'HIGH',
        'Disputed ownership of agricultural land in Pune district.',
        'State of Maharashtra', 'Justice A.K. Sharma');

INSERT IGNORE INTO cases (case_id, case_number, case_title, case_type, client_id, advocate_id,
                           court_name, court_type, filing_date, status, priority,
                           description, opposing_party, judge_name)
VALUES (5002, 'LC-2024-002',
        'Priya Custody Case',
        'FAMILY', 4002, 3002,
        'Family Court, Chennai', 'OTHER', '2024-04-15',
        'OPEN', 'MEDIUM',
        'Child custody dispute following divorce proceedings.',
        'Anand Kumar', 'Judge R. Meenakshi');

INSERT IGNORE INTO cases (case_id, case_number, case_title, case_type, client_id, advocate_id,
                           court_name, court_type, filing_date, status, priority,
                           description, opposing_party, judge_name)
VALUES (5003, 'LC-2024-003',
        'Suresh Corp Tax Dispute',
        'TAX', 4003, 3001,
        'Income Tax Appellate Tribunal, Hyderabad', 'TRIBUNAL', '2024-05-01',
        'IN_PROGRESS', 'URGENT',
        'Appeal against income tax assessment for FY 2022-23.',
        'Income Tax Department', 'ITAT Member B. Rao');

INSERT IGNORE INTO cases (case_id, case_number, case_title, case_type, client_id, advocate_id,
                           court_name, court_type, filing_date, status, priority,
                           description, opposing_party, judge_name)
VALUES (5004, 'LC-2024-004',
        'Ravi Employment Termination',
        'LABOUR', 4001, 3002,
        'Labour Court, Pune', 'OTHER', '2024-06-20',
        'OPEN', 'MEDIUM',
        'Wrongful termination claim against former employer.',
        'ABC Pvt Ltd', 'Labour Court Judge P. Singh');

INSERT IGNORE INTO cases (case_id, case_number, case_title, case_type, client_id, advocate_id,
                           court_name, court_type, filing_date, status, priority,
                           description, opposing_party, judge_name)
VALUES (5005, 'LC-2023-045',
        'Priya Accident Compensation',
        'CIVIL', 4002, 3001,
        'District Court, Chennai', 'DISTRICT_COURT', '2023-11-05',
        'CLOSED', 'LOW',
        'Motor accident compensation claim – settled out of court.',
        'XYZ Insurance Co', 'District Judge V. Kumar');

-- ► HEARINGS
INSERT IGNORE INTO hearings (hearing_id, case_id, hearing_date, courtroom,
                              hearing_type, status, remarks)
VALUES (6001, 5001, '2026-03-05 10:30:00', 'Court Room 3',
        'EVIDENCE', 'SCHEDULED', 'Evidence submission hearing');

INSERT IGNORE INTO hearings (hearing_id, case_id, hearing_date, courtroom,
                              hearing_type, status, remarks)
VALUES (6002, 5001, '2025-12-10 11:00:00', 'Court Room 3',
        'PRELIMINARY', 'COMPLETED', 'Initial hearing completed');

INSERT IGNORE INTO hearings (hearing_id, case_id, hearing_date, courtroom,
                              hearing_type, status, remarks)
VALUES (6003, 5002, '2026-02-28 09:30:00', 'Family Court Room 1',
        'ARGUMENTS', 'SCHEDULED', 'Final arguments');

INSERT IGNORE INTO hearings (hearing_id, case_id, hearing_date, courtroom,
                              hearing_type, status, remarks)
VALUES (6004, 5003, '2026-03-12 14:00:00', 'ITAT Hall 2',
        'EVIDENCE', 'SCHEDULED', 'Document submission before tribunal');

INSERT IGNORE INTO hearings (hearing_id, case_id, hearing_date, courtroom,
                              hearing_type, status, remarks)
VALUES (6005, 5004, '2026-04-01 10:00:00', 'Labour Court Room 2',
        'PRELIMINARY', 'SCHEDULED', 'First hearing – framing of issues');

INSERT IGNORE INTO hearings (hearing_id, case_id, hearing_date, courtroom,
                              hearing_type, status, remarks)
VALUES (6006, 5005, '2023-12-20 09:00:00', 'DCR 5',
        'JUDGMENT', 'COMPLETED', 'Case settled – compensation awarded ₹2,50,000');

-- ► DOCUMENTS
INSERT IGNORE INTO documents (document_id, case_id, document_name, document_type,
                               file_path, file_size, uploaded_by, description)
VALUES (7001, 5001, 'Land Title Deed.pdf', 'EVIDENCE',
        '/documents/5001/land_title_deed.pdf', 512000, 1002, 'Original land title document');

INSERT IGNORE INTO documents (document_id, case_id, document_name, document_type,
                               file_path, file_size, uploaded_by, description)
VALUES (7002, 5001, 'Petition.pdf', 'PETITION',
        '/documents/5001/petition.pdf', 204800, 1002, 'Initial writ petition');

INSERT IGNORE INTO documents (document_id, case_id, document_name, document_type,
                               file_path, file_size, uploaded_by, description)
VALUES (7003, 5002, 'Marriage Certificate.pdf', 'EVIDENCE',
        '/documents/5002/marriage_cert.pdf', 102400, 1003, 'Marriage certificate copy');

INSERT IGNORE INTO documents (document_id, case_id, document_name, document_type,
                               file_path, file_size, uploaded_by, description)
VALUES (7004, 5003, 'Tax Assessment Order.pdf', 'ORDER',
        '/documents/5003/tax_order.pdf', 307200, 1002, 'IT assessment order for FY 2022-23');

-- ► TASKS
INSERT IGNORE INTO tasks (task_id, case_id, assigned_to, task_title, description,
                           due_date, priority, status, created_by)
VALUES (13001, 5001, 1002, 'Prepare evidence affidavit',
        'Draft and notarize the affidavit for evidence submission',
        '2026-02-28 18:00:00', 'HIGH', 'IN_PROGRESS', 1001);

INSERT IGNORE INTO tasks (task_id, case_id, assigned_to, task_title, description,
                           due_date, priority, status, created_by)
VALUES (13002, 5001, 1007, 'File court fee payment',
        'Process court fee payment for next hearing',
        '2026-03-01 12:00:00', 'MEDIUM', 'PENDING', 1001);

INSERT IGNORE INTO tasks (task_id, case_id, assigned_to, task_title, description,
                           due_date, priority, status, created_by)
VALUES (13003, 5002, 1003, 'Obtain school records for custody',
        "Request children's school records as supporting evidence",
        '2026-02-25 18:00:00', 'HIGH', 'PENDING', 1001);

INSERT IGNORE INTO tasks (task_id, case_id, assigned_to, task_title, description,
                           due_date, priority, status, created_by)
VALUES (13004, 5003, 1002, 'Compile financial statements',
        'Gather FY 2022-23 audited financial statements for tribunal',
        '2026-03-05 18:00:00', 'URGENT', 'IN_PROGRESS', 1001);

INSERT IGNORE INTO tasks (task_id, case_id, assigned_to, task_title, description,
                           due_date, priority, status, created_by)
VALUES (13005, 5003, 1008, 'Draft reply to tax notice',
        'Prepare written reply to departmental show-cause notice',
        '2026-02-20 18:00:00', 'URGENT', 'COMPLETED', 1001);

-- ► INVOICES
INSERT IGNORE INTO invoices (invoice_id, invoice_number, case_id, client_id,
                              invoice_date, due_date, amount, tax, discount, total_amount, status, notes)
VALUES (8001, 'INV-2024-0001', 5001, 4001,
        '2024-04-01', '2024-04-30',
        25000.00, 4500.00, 0.00, 29500.00, 'PAID',
        'Initial retainer fee for property dispute case');

INSERT IGNORE INTO invoices (invoice_id, invoice_number, case_id, client_id,
                              invoice_date, due_date, amount, tax, discount, total_amount, status, notes)
VALUES (8002, 'INV-2024-0002', 5003, 4003,
        '2024-05-15', '2024-06-15',
        50000.00, 9000.00, 2500.00, 56500.00, 'SENT',
        'Consultation and filing fees for tax dispute');

INSERT IGNORE INTO invoices (invoice_id, invoice_number, case_id, client_id,
                              invoice_date, due_date, amount, tax, discount, total_amount, status, notes)
VALUES (8003, 'INV-2025-0001', 5001, 4001,
        '2025-01-10', '2025-01-31',
        15000.00, 2700.00, 0.00, 17700.00, 'OVERDUE',
        'Quarterly billing – Q4 2024');

-- ► LEGAL NOTICES
INSERT IGNORE INTO legal_notices (notice_id, case_id, notice_type, recipient_name,
                                   recipient_address, content, sent_date, status)
VALUES (10001, 5001,
        'DEMAND', 'State of Maharashtra',
        'Revenue Department, Mantralaya, Mumbai',
        'This is a legal notice demanding the restoration of title rights to the disputed property located at Survey No. 45/A, Pune district, as per the registered title deed dated 15 January 2018.',
        '2024-03-20', 'SENT');

INSERT IGNORE INTO legal_notices (notice_id, case_id, notice_type, recipient_name,
                                   recipient_address, content, sent_date, status)
VALUES (10002, 5004,
        'CEASE_DESIST', 'ABC Pvt Ltd',
        'ABC Pvt Ltd, MG Road, Pune 411001',
        'You are hereby notified to cease and desist any adverse actions against our client Mr. Ravi and to provide the required relieving letter and full and final settlement within 15 days from the date of this notice.',
        '2024-07-01', 'ACKNOWLEDGED');

-- ► CASE NOTES
INSERT IGNORE INTO case_notes (note_id, case_id, created_by, note_text, is_private)
VALUES (12001, 5001, 1002,
        'Obtained original title deed from client. Revenue records verified. Strong case for reclaim.',
        FALSE);

INSERT IGNORE INTO case_notes (note_id, case_id, created_by, note_text, is_private)
VALUES (12002, 5001, 1002,
        'Attended preliminary hearing. Court fixed next date for evidence. Client informed.',
        FALSE);

INSERT IGNORE INTO case_notes (note_id, case_id, created_by, note_text, is_private)
VALUES (12003, 5003, 1002,
        'Tax assessment order received. Grounds for appeal are non-application of mind and jurisdictional error.',
        TRUE);

-- ► MESSAGES
INSERT IGNORE INTO messages (message_id, case_id, sender_id, recipient_id,
                              subject, message_text, is_read)
VALUES (14001, 5001, 1002, 1004,
        'Update on Property Dispute Case',
        'Dear Mr. Ravi, the next hearing is scheduled for 5 March 2026 at 10:30 AM in Court Room 3. Please ensure you are available. Regards, John Advocate.',
        FALSE);

INSERT IGNORE INTO messages (message_id, case_id, sender_id, recipient_id,
                              subject, message_text, is_read)
VALUES (14002, 5003, 1002, 1006,
        'Tax Dispute – Document Request',
        'Dear Suresh Corp team, please share the audited financial statements for FY 2022-23 and all ITR filings at the earliest so we can prepare our case effectively.',
        TRUE);