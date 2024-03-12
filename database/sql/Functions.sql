
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION generate_alphanumeric_id()
RETURNS TRIGGER AS
$$
BEGIN
    NEW.identifier := substr(replace(uuid_generate_v4()::text, '-', ''), 1, 6);
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;