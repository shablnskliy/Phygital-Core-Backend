CREATE TABLE notifications (
                               id UUID PRIMARY KEY,
                               user_id UUID NOT NULL,
                               type VARCHAR(50) NOT NULL,
                               invitation_id UUID,
                               title VARCHAR(255) NOT NULL,
                               message TEXT NOT NULL,
                               is_read BOOLEAN NOT NULL DEFAULT FALSE,
                               created_at TIMESTAMP NOT NULL
);