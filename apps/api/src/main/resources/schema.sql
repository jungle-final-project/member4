CREATE TABLE IF NOT EXISTS users (
  id VARCHAR(64) PRIMARY KEY,
  email VARCHAR(255) UNIQUE NOT NULL,
  name VARCHAR(120) NOT NULL,
  role VARCHAR(20) NOT NULL,
  created_at TIMESTAMPTZ DEFAULT now()
);

CREATE TABLE IF NOT EXISTS requirements (
  id VARCHAR(64) PRIMARY KEY,
  user_id VARCHAR(64),
  raw_message TEXT,
  budget INTEGER,
  usage_tags TEXT,
  created_at TIMESTAMPTZ DEFAULT now()
);

CREATE TABLE IF NOT EXISTS builds (
  id VARCHAR(64) PRIMARY KEY,
  requirement_id VARCHAR(64),
  name VARCHAR(160) NOT NULL,
  total_price INTEGER NOT NULL,
  confidence VARCHAR(20) NOT NULL,
  warnings TEXT,
  created_at TIMESTAMPTZ DEFAULT now()
);

CREATE TABLE IF NOT EXISTS build_items (
  id VARCHAR(64) PRIMARY KEY,
  build_id VARCHAR(64),
  part_id VARCHAR(64),
  category VARCHAR(40),
  price INTEGER
);

CREATE TABLE IF NOT EXISTS parts (
  id VARCHAR(64) PRIMARY KEY,
  category VARCHAR(40) NOT NULL,
  name VARCHAR(200) NOT NULL,
  price INTEGER NOT NULL,
  status VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS tool_invocations (
  id VARCHAR(64) PRIMARY KEY,
  agent_session_id VARCHAR(64),
  tool_name VARCHAR(80),
  status VARCHAR(20),
  confidence VARCHAR(20),
  summary TEXT,
  created_at TIMESTAMPTZ DEFAULT now()
);

CREATE TABLE IF NOT EXISTS rag_evidence (
  id VARCHAR(64) PRIMARY KEY,
  source_id VARCHAR(120),
  summary TEXT,
  score NUMERIC(5, 4),
  embedding vector(1536),
  created_at TIMESTAMPTZ DEFAULT now()
);

CREATE TABLE IF NOT EXISTS agent_sessions (
  id VARCHAR(64) PRIMARY KEY,
  user_id VARCHAR(64),
  status VARCHAR(20),
  created_at TIMESTAMPTZ DEFAULT now()
);

CREATE TABLE IF NOT EXISTS agent_log_uploads (
  id VARCHAR(64) PRIMARY KEY,
  user_id VARCHAR(64),
  range_minutes INTEGER,
  status VARCHAR(20),
  summary TEXT,
  created_at TIMESTAMPTZ DEFAULT now()
);

CREATE TABLE IF NOT EXISTS as_tickets (
  id VARCHAR(64) PRIMARY KEY,
  user_id VARCHAR(64),
  symptom TEXT,
  status VARCHAR(30),
  cause_candidate TEXT,
  created_at TIMESTAMPTZ DEFAULT now()
);

CREATE TABLE IF NOT EXISTS price_snapshots (
  id VARCHAR(64) PRIMARY KEY,
  part_id VARCHAR(64),
  price INTEGER,
  source VARCHAR(80),
  collected_at TIMESTAMPTZ DEFAULT now()
);

CREATE TABLE IF NOT EXISTS price_alerts (
  id VARCHAR(64) PRIMARY KEY,
  user_id VARCHAR(64),
  part_id VARCHAR(64),
  target_price INTEGER,
  email VARCHAR(255),
  status VARCHAR(20),
  created_at TIMESTAMPTZ DEFAULT now()
);
