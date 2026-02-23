-- Users Table
CREATE TABLE IF NOT EXISTS users (
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'ADVOCATE', 'CLIENT', 'CLERK') NOT NULL DEFAULT 'CLIENT',
    phone VARCHAR(20),
    address TEXT,
    status ENUM('ACTIVE', 'INACTIVE', 'SUSPENDED') NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_role (role),
    INDEX idx_status (status)
) ENGINE=InnoDB;

-- Law Firms Table
CREATE TABLE IF NOT EXISTS law_firms (
    firm_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    firm_name VARCHAR(200) NOT NULL,
    address TEXT,
    email VARCHAR(100),
    phone VARCHAR(20),
    website VARCHAR(200),
    registration_number VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_firm_name (firm_name)
) ENGINE=InnoDB;

-- Advocates Table
CREATE TABLE IF NOT EXISTS advocates (
    advocate_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    firm_id BIGINT,
    bar_registration_number VARCHAR(100) UNIQUE,
    specialization VARCHAR(200),	
    experience_years INT,
    qualification VARCHAR(200),
    court_practice VARCHAR(200),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (firm_id) REFERENCES law_firms(firm_id) ON DELETE SET NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_firm_id (firm_id),
    INDEX idx_specialization (specialization)
) ENGINE=InnoDB;

-- Clients Table
CREATE TABLE IF NOT EXISTS clients (
    client_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    address TEXT,
    company_name VARCHAR(200),
    gstin VARCHAR(50),
    pan_number VARCHAR(20),
    client_type ENUM('INDIVIDUAL', 'CORPORATE', 'GOVERNMENT') DEFAULT 'INDIVIDUAL',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_client_type (client_type)
) ENGINE=InnoDB;

-- Cases Table
CREATE TABLE IF NOT EXISTS cases (
    case_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    case_number VARCHAR(100) UNIQUE NOT NULL,
    case_title VARCHAR(300) NOT NULL,
    case_type ENUM('CIVIL', 'CRIMINAL', 'FAMILY', 'CORPORATE', 'TAX', 'LABOUR', 'PROPERTY', 'OTHER') NOT NULL,
    client_id BIGINT NOT NULL,
    advocate_id BIGINT NOT NULL,
    court_name VARCHAR(200),
    court_type ENUM('SUPREME_COURT', 'HIGH_COURT', 'DISTRICT_COURT', 'TRIBUNAL', 'OTHER'),
    filing_date DATE,
    status ENUM('OPEN', 'IN_PROGRESS', 'CLOSED', 'WON', 'LOST', 'SETTLED') NOT NULL DEFAULT 'OPEN',
    priority ENUM('LOW', 'MEDIUM', 'HIGH', 'URGENT') DEFAULT 'MEDIUM',
    description TEXT,
    opposing_party VARCHAR(300),
    judge_name VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (client_id) REFERENCES clients(client_id) ON DELETE CASCADE,
    FOREIGN KEY (advocate_id) REFERENCES advocates(advocate_id) ON DELETE CASCADE,
    INDEX idx_case_number (case_number),
    INDEX idx_client_id (client_id),
    INDEX idx_advocate_id (advocate_id),
    INDEX idx_status (status),
    INDEX idx_case_type (case_type),
    INDEX idx_filing_date (filing_date)
) ENGINE=InnoDB;

-- Hearings Table
CREATE TABLE IF NOT EXISTS hearings (
    hearing_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    case_id BIGINT NOT NULL,
    hearing_date DATETIME NOT NULL,
    courtroom VARCHAR(100),
    hearing_type ENUM('PRELIMINARY', 'EVIDENCE', 'ARGUMENTS', 'JUDGMENT', 'OTHER') DEFAULT 'OTHER',
    status ENUM('SCHEDULED', 'COMPLETED', 'ADJOURNED', 'CANCELLED') NOT NULL DEFAULT 'SCHEDULED',
    remarks TEXT,
    next_hearing_date DATETIME,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (case_id) REFERENCES cases(case_id) ON DELETE CASCADE,
    INDEX idx_case_id (case_id),
    INDEX idx_hearing_date (hearing_date),
    INDEX idx_status (status)
) ENGINE=InnoDB;

-- Documents Table
CREATE TABLE IF NOT EXISTS documents (
    document_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    case_id BIGINT NOT NULL,
    document_name VARCHAR(300) NOT NULL,
    document_type ENUM('PETITION', 'EVIDENCE', 'AFFIDAVIT', 'ORDER', 'NOTICE', 'OTHER') DEFAULT 'OTHER',
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT,
    uploaded_by BIGINT NOT NULL,
    description TEXT,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (case_id) REFERENCES cases(case_id) ON DELETE CASCADE,
    FOREIGN KEY (uploaded_by) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_case_id (case_id),
    INDEX idx_uploaded_by (uploaded_by),
    INDEX idx_document_type (document_type),
    INDEX idx_uploaded_at (uploaded_at)
) ENGINE=InnoDB;

-- Invoices Table
CREATE TABLE IF NOT EXISTS invoices (
    invoice_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    invoice_number VARCHAR(100) UNIQUE NOT NULL,
    case_id BIGINT NOT NULL,
    client_id BIGINT NOT NULL,
    invoice_date DATE NOT NULL,
    due_date DATE,
    amount DECIMAL(12, 2) NOT NULL,
    tax DECIMAL(12, 2) DEFAULT 0.00,
    discount DECIMAL(12, 2) DEFAULT 0.00,
    total_amount DECIMAL(12, 2) NOT NULL,
    status ENUM('DRAFT', 'SENT', 'PAID', 'OVERDUE', 'CANCELLED') NOT NULL DEFAULT 'DRAFT',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (case_id) REFERENCES cases(case_id) ON DELETE CASCADE,
    FOREIGN KEY (client_id) REFERENCES clients(client_id) ON DELETE CASCADE,
    INDEX idx_invoice_number (invoice_number),
    INDEX idx_case_id (case_id),
    INDEX idx_client_id (client_id),
    INDEX idx_status (status),
    INDEX idx_invoice_date (invoice_date)
) ENGINE=InnoDB;

-- Payments Table
CREATE TABLE IF NOT EXISTS payments (
    payment_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    invoice_id BIGINT NOT NULL,
    payment_date DATE NOT NULL,
    amount DECIMAL(12, 2) NOT NULL,
    payment_mode ENUM('CASH', 'CHEQUE', 'ONLINE', 'UPI', 'CARD', 'BANK_TRANSFER') NOT NULL,
    transaction_id VARCHAR(200),
    status ENUM('SUCCESS', 'PENDING', 'FAILED') NOT NULL DEFAULT 'SUCCESS',
    remarks TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (invoice_id) REFERENCES invoices(invoice_id) ON DELETE CASCADE,
    INDEX idx_invoice_id (invoice_id),
    INDEX idx_payment_date (payment_date),
    INDEX idx_status (status)
) ENGINE=InnoDB;

-- Legal Notices Table
CREATE TABLE IF NOT EXISTS legal_notices (
    notice_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    case_id BIGINT NOT NULL,
    notice_type ENUM('DEMAND', 'TERMINATION', 'CEASE_DESIST', 'EVICTION', 'OTHER') DEFAULT 'OTHER',
    recipient_name VARCHAR(200) NOT NULL,
    recipient_address TEXT,
    content TEXT NOT NULL,
    sent_date DATE,
    status ENUM('DRAFT', 'SENT', 'ACKNOWLEDGED', 'RESPONDED') DEFAULT 'DRAFT',
    generated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (case_id) REFERENCES cases(case_id) ON DELETE CASCADE,
    INDEX idx_case_id (case_id),
    INDEX idx_status (status),
    INDEX idx_sent_date (sent_date)
) ENGINE=InnoDB;

-- Audit Logs Table
CREATE TABLE IF NOT EXISTS audit_logs (
    log_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(50),
    entity_id BIGINT,
    ip_address VARCHAR(45),
    user_agent TEXT,
    description TEXT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_action (action),
    INDEX idx_timestamp (timestamp),
    INDEX idx_entity (entity_type, entity_id)
) ENGINE=InnoDB;

-- Case Notes Table
CREATE TABLE IF NOT EXISTS case_notes (
    note_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    case_id BIGINT NOT NULL,
    created_by BIGINT NOT NULL,
    note_text TEXT NOT NULL,
    is_private BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (case_id) REFERENCES cases(case_id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_case_id (case_id),
    INDEX idx_created_by (created_by)
) ENGINE=InnoDB;

-- Tasks Table
CREATE TABLE IF NOT EXISTS tasks (
    task_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    case_id BIGINT NOT NULL,
    assigned_to BIGINT,
    task_title VARCHAR(300) NOT NULL,
    description TEXT,
    due_date DATETIME,
    priority ENUM('LOW', 'MEDIUM', 'HIGH', 'URGENT') DEFAULT 'MEDIUM',
    status ENUM('PENDING', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED') DEFAULT 'PENDING',
    created_by BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (case_id) REFERENCES cases(case_id) ON DELETE CASCADE,
    FOREIGN KEY (assigned_to) REFERENCES users(user_id) ON DELETE SET NULL,
    FOREIGN KEY (created_by) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_case_id (case_id),
    INDEX idx_assigned_to (assigned_to),
    INDEX idx_status (status),
    INDEX idx_due_date (due_date)
) ENGINE=InnoDB;

-- Messages Table
CREATE TABLE IF NOT EXISTS messages (
    message_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    case_id BIGINT NULL,
    sender_id BIGINT NOT NULL,
    recipient_id BIGINT NOT NULL,
    subject VARCHAR(300),
    message_text TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    read_at TIMESTAMP NULL,
    FOREIGN KEY (case_id) REFERENCES cases(case_id) ON DELETE CASCADE,
    FOREIGN KEY (sender_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (recipient_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_case_id (case_id),
    INDEX idx_sender_id (sender_id),
    INDEX idx_recipient_id (recipient_id),
    INDEX idx_is_read (is_read),
    INDEX idx_sent_at (sent_at)
) ENGINE=InnoDB;

-- Set AUTO_INCREMENT values
ALTER TABLE users AUTO_INCREMENT = 1001;
ALTER TABLE law_firms AUTO_INCREMENT = 2001;
ALTER TABLE advocates AUTO_INCREMENT = 3001;
ALTER TABLE clients AUTO_INCREMENT = 4001;
ALTER TABLE cases AUTO_INCREMENT = 5001;
ALTER TABLE hearings AUTO_INCREMENT = 6001;
ALTER TABLE documents AUTO_INCREMENT = 7001;
ALTER TABLE invoices AUTO_INCREMENT = 8001;
ALTER TABLE payments AUTO_INCREMENT = 9001;
ALTER TABLE legal_notices AUTO_INCREMENT = 10001;
ALTER TABLE audit_logs AUTO_INCREMENT = 11001;
ALTER TABLE case_notes AUTO_INCREMENT = 12001;
ALTER TABLE tasks AUTO_INCREMENT = 13001;
ALTER TABLE messages AUTO_INCREMENT = 14001;
