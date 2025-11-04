-- cria o banco se ainda não existir
CREATE DATABASE ${DB_NAME};

-- conecta ao banco
\connect ${DB_NAME};

-- cria o usuário da aplicação com senha
CREATE USER app_user WITH PASSWORD ${DB_PASSWORD};

-- concede permissões somente nesse banco
GRANT CONNECT ON DATABASE ${DB_NAME} TO app_user;
GRANT USAGE ON SCHEMA public TO app_user;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO app_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public
    GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO app_user;
