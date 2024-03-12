
-- CREATE EXTENSIONS --
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
--ddl-end --

CREATE TABLE public.user (
	id serial NOT NULL,
	email varchar(255) NOT NULL UNIQUE,
	password varchar(255) NOT NULL,
    name varchar(255) DEFAULT null,
    is_active boolean NOT NULL DEFAULT false,
	created_at timestamp NOT NULL DEFAULT current_timestamp,
	updated_at timestamp DEFAULT current_timestamp,
	CONSTRAINT "user_id_key" PRIMARY KEY (id)
);
ALTER TABLE public.user OWNER TO postgres;

CREATE TABLE public.chat (
	id serial NOT NULL,
	user_id integer NOT NULL,
	name varchar(255) NOT NULL,
	created_at timestamp NOT NULL DEFAULT current_timestamp,
	updated_at timestamp DEFAULT current_timestamp,
	CONSTRAINT "chat_id_key" PRIMARY KEY (id),
	CONSTRAINT "fk_chat_user_id" FOREIGN KEY (user_id) REFERENCES public.user(id)
);
ALTER TABLE public.chat OWNER TO postgres;

CREATE TABLE public.history (
	id serial NOT NULL,
    chat_id integer NOT NULL,
    user_msg boolean NOT NULL DEFAULT false,
    value varchar NOT NULL,
	created_at timestamp NOT NULL DEFAULT current_timestamp,
	updated_at timestamp DEFAULT current_timestamp,
	CONSTRAINT "history_id_key" PRIMARY KEY (id),
	CONSTRAINT "fk_history_chat_id" FOREIGN KEY (chat_id) REFERENCES public.chat(id)
);
ALTER TABLE public.history OWNER TO postgres;


