-- ============================================================================
-- SCRIPT DE CRIAÇÃO DE USUÁRIO E PERMISSÕES
-- ============================================================================
-- Este script cria um usuário específico para a aplicação com permissões
-- limitadas, seguindo o princípio de menor privilégio.
-- Justificativa: Segurança - evitar acesso root ao banco de dados
-- ============================================================================

-- Criar usuário para a aplicação
CREATE USER IF NOT EXISTS 'matricula_user'@'localhost' IDENTIFIED BY 'matricula_password';

-- Conceder permissões específicas ao banco de dados
GRANT SELECT, INSERT, UPDATE, DELETE ON matricula_escolar.* TO 'matricula_user'@'localhost';
GRANT EXECUTE ON matricula_escolar.* TO 'matricula_user'@'localhost';

-- Aplicar as mudanças
FLUSH PRIVILEGES;

-- Verificação
SELECT User, Host FROM mysql.user WHERE User = 'matricula_user';
SHOW GRANTS FOR 'matricula_user'@'localhost';
