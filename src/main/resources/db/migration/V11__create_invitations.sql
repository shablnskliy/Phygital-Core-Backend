CREATE TABLE invitations (
                             id UUID PRIMARY KEY,
                             team_id UUID NOT NULL,
                             sender_id UUID NOT NULL,
                             recipient_id UUID NOT NULL,
                             status VARCHAR(50) NOT NULL DEFAULT 'pending',
                             created_at TIMESTAMP NOT NULL,
                             responded_at TIMESTAMP
);