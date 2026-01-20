CREATE TABLE games (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    genre VARCHAR(100),
    min_players INTEGER DEFAULT 1,
    max_players INTEGER DEFAULT 1,
    image_url VARCHAR(500),
    active BOOLEAN DEFAULT true,
    created_by VARCHAR(255),
    modified_by VARCHAR(255)
);

CREATE INDEX idx_games_name ON games(name);
CREATE INDEX idx_games_genre ON games(genre);
CREATE INDEX idx_games_active ON games(active);
