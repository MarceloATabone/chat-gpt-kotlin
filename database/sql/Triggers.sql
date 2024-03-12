CREATE TRIGGER update_user_modtime
BEFORE UPDATE ON public.user
FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_chat_modtime
BEFORE UPDATE ON public.chat
FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_history_modtime
BEFORE UPDATE ON public.history
FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();