-- Script criado para o trabalho: criar schema, funções, triggers, views, procedures e usuários.
-- 1. Criar o banco de dados se ele não existir
-- =====================================================================
-- TRABALHO FINAL - LABORATÓRIO DE BANCO DE DADOS
-- Script Completo: Schema, Tabelas, Funções, Procedures, Views, Triggers e Usuários
-- =====================================================================

-- 1. CRIAÇÃO DO BANCO DE DADOS
CREATE DATABASE IF NOT EXISTS matricula_escolar CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE matricula_escolar;

-- 2. TABELA DE CONTROLE PARA GERAÇÃO DE IDs CUSTOMIZADOS
-- Justificativa: Permite a criação de IDs não sequenciais e com prefixos,
-- atendendo ao requisito de não usar AUTO_INCREMENT para dados críticos.
CREATE TABLE IF NOT EXISTS controle_ids (
    nome_seq VARCHAR(100) PRIMARY KEY,
    ultimo_valor INT NOT NULL
);
INSERT IGNORE INTO controle_ids (nome_seq, ultimo_valor) VALUES ('usuario_seq', 0), ('aluno_seq', 0);

-- 3. FUNÇÕES PARA GERAÇÃO DE IDs (Requisito Obrigatório)
DELIMITER //
DROP FUNCTION IF EXISTS fnc_gerar_id_usuario;
//
CREATE FUNCTION fnc_gerar_id_usuario() RETURNS VARCHAR(20)
DETERMINISTIC
BEGIN
    UPDATE controle_ids SET ultimo_valor = ultimo_valor + 1 WHERE nome_seq='usuario_seq';
    RETURN CONCAT('USR', LPAD((SELECT ultimo_valor FROM controle_ids WHERE nome_seq='usuario_seq'), 6, '0'));
END;
//

DROP FUNCTION IF EXISTS fnc_gerar_id_aluno;
//
CREATE FUNCTION fnc_gerar_id_aluno() RETURNS VARCHAR(20)
DETERMINISTIC
BEGIN
    UPDATE controle_ids SET ultimo_valor = ultimo_valor + 1 WHERE nome_seq='aluno_seq';
    RETURN CONCAT('ALU', LPAD((SELECT ultimo_valor FROM controle_ids WHERE nome_seq='aluno_seq'), 6, '0'));
END;
//
DELIMITER ;

-- Apaga as tabelas na ordem correta para evitar erros de chave estrangeira
DROP TABLE IF EXISTS matriculas;
DROP TABLE IF EXISTS usuarios;
DROP TABLE IF EXISTS alunos;
DROP TABLE IF EXISTS turmas;
DROP TABLE IF EXISTS grupos_usuarios;
-- 4. TABELAS PRINCIPAIS (Requisito Obrigatório)
CREATE TABLE IF NOT EXISTS grupos_usuarios (
    id INT PRIMARY KEY,
    nome VARCHAR(50) NOT NULL UNIQUE,
    descricao VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS alunos (
    id_aluno VARCHAR(20) PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    data_nascimento DATE,
    email VARCHAR(120) UNIQUE
);

CREATE TABLE IF NOT EXISTS usuarios (
    id_usuario VARCHAR(20) PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    login VARCHAR(50) NOT NULL UNIQUE,
    senha_hash VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    id_grupo INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_grupo) REFERENCES grupos_usuarios(id)
);

CREATE TABLE IF NOT EXISTS turmas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    capacidade INT NOT NULL,
    vagas_ocupadas INT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS matriculas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_aluno VARCHAR(20),
    id_turma INT,
    data_matricula TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_aluno) REFERENCES alunos(id_aluno) ON DELETE CASCADE,
    FOREIGN KEY (id_turma) REFERENCES turmas(id)
);

-- 5. TRIGGERS (Requisito Obrigatório)
-- Trigger 1: Atualiza a contagem de vagas na tabela 'turmas' após uma matrícula.
DELIMITER //
DROP TRIGGER IF EXISTS trg_after_insert_matricula;
//
CREATE TRIGGER trg_after_insert_matricula
AFTER INSERT ON matriculas
FOR EACH ROW
BEGIN
    UPDATE turmas SET vagas_ocupadas = vagas_ocupadas + 1 WHERE id = NEW.id_turma;
END;
//

DROP TRIGGER IF EXISTS trg_before_insert_matricula;
//
-- Trigger 2: Impede a inserção de uma matrícula se a turma estiver lotada.
CREATE TRIGGER trg_before_insert_matricula
BEFORE INSERT ON matriculas
FOR EACH ROW
BEGIN
    DECLARE vagas_restantes INT;
    SELECT (capacidade - vagas_ocupadas) INTO vagas_restantes FROM turmas WHERE id = NEW.id_turma;
    IF vagas_restantes <= 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Não há vagas disponíveis nesta turma.';
    END IF;
END;
//
DELIMITER ;

-- 6. VIEWS (Requisito Obrigatório)
-- View 1: Simplifica a consulta de informações dos alunos e suas respectivas turmas.
CREATE OR REPLACE VIEW vw_alunos_turmas AS
SELECT a.id_aluno, a.nome, a.email, t.id AS id_turma, t.nome AS nome_turma
FROM alunos a
LEFT JOIN matriculas m ON a.id_aluno = m.id_aluno
LEFT JOIN turmas t ON m.id_turma = t.id;

-- View 2: Mostra o status de vagas de cada turma.
CREATE OR REPLACE VIEW vw_status_vagas_turmas AS
SELECT id, nome, capacidade, vagas_ocupadas, (capacidade - vagas_ocupadas) AS vagas_disponiveis
FROM turmas;

-- 7. PROCEDURES E FUNCTIONS (Requisito Obrigatório)
-- Procedure: Cancela uma matrícula e atualiza o número de vagas.
DELIMITER //
DROP PROCEDURE IF EXISTS sp_cancelar_matricula;
//
CREATE PROCEDURE sp_cancelar_matricula(IN p_id_matricula INT)
BEGIN
    DECLARE v_id_turma INT;
    SELECT id_turma INTO v_id_turma FROM matriculas WHERE id = p_id_matricula;
    DELETE FROM matriculas WHERE id = p_id_matricula;
    UPDATE turmas SET vagas_ocupadas = vagas_ocupadas - 1 WHERE id = v_id_turma;
END;
//
DELIMITER ;

-- 8. DADOS INICIAIS
INSERT IGNORE INTO grupos_usuarios (id, nome, descricao) VALUES (1, 'ADMIN', 'Acesso total'), (2, 'USER', 'Acesso de consulta');
INSERT IGNORE INTO usuarios (id_usuario, nome, login, senha_hash, email, id_grupo) VALUES (fnc_gerar_id_usuario(), 'Administrador', 'admin', '$2a$10$3g5v1C.PzL3g5v1C.PzL3uI0k3j4l5m6n7o8p9q0r1s2t3u4v', 'admin@escola.com', 1);
INSERT IGNORE INTO alunos (id_aluno, nome, data_nascimento, email) VALUES (fnc_gerar_id_aluno(), 'João da Silva', '2005-04-10', 'joao.silva@email.com');
INSERT IGNORE INTO turmas (id, nome, capacidade, vagas_ocupadas) VALUES (1, '3º Ano A - Manhã', 30, 0), (2, '1º Ano B - Tarde', 25, 0);

-- 9. USUÁRIOS DO BANCO (Requisito Obrigatório)
CREATE USER IF NOT EXISTS 'admin_app'@'localhost' IDENTIFIED BY 'SenhaAdmin123!';
GRANT ALL PRIVILEGES ON matricula_escolar.* TO 'admin_app'@'localhost';

CREATE USER IF NOT EXISTS 'backend_app'@'localhost' IDENTIFIED BY 'SenhaBack123!';
GRANT SELECT, INSERT, UPDATE, DELETE, EXECUTE ON matricula_escolar.* TO 'backend_app'@'localhost';

CREATE USER IF NOT EXISTS 'relatorio_app'@'localhost' IDENTIFIED BY 'SenhaRelatorio!';
GRANT SELECT ON matricula_escolar.* TO 'relatorio_app'@'localhost';

FLUSH PRIVILEGES;
