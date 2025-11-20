-- ============================================================================
-- SCRIPT DE CRIAÇÃO DO BANCO DE DADOS - GERENCIAMENTO DE MATRÍCULAS ESCOLARES
-- ============================================================================
-- Este script cria a estrutura completa do banco de dados relacional (MySQL)
-- com todas as tabelas, índices, triggers, views, procedures e functions
-- obrigatórias para o trabalho final de Laboratório de Banco de Dados.
-- ============================================================================

-- Criar o banco de dados
CREATE DATABASE IF NOT EXISTS matricula_escolar
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE matricula_escolar;

-- ============================================================================
-- TABELAS OBRIGATÓRIAS
-- ============================================================================

-- Tabela: grupos_usuarios
-- Descrição: Define os grupos de usuários e suas permissões
-- Justificativa: Controle de acesso granular baseado em grupos
CREATE TABLE IF NOT EXISTS grupos_usuarios (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL UNIQUE,
    descricao TEXT,
    permissoes JSON NOT NULL DEFAULT '[]',
    ativo BOOLEAN DEFAULT TRUE,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_nome (nome),
    INDEX idx_ativo (ativo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabela: usuarios
-- Descrição: Armazena informações dos usuários do sistema
-- Justificativa: Tabela obrigatória para autenticação e controle de acesso
CREATE TABLE IF NOT EXISTS usuarios (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    cpf VARCHAR(14) UNIQUE,
    telefone VARCHAR(20),
    endereco TEXT,
    idade INT,
    senha_hash VARCHAR(255) NOT NULL,
    ativo BOOLEAN DEFAULT TRUE,
    grupo_usuario_id INT NOT NULL,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (grupo_usuario_id) REFERENCES grupos_usuarios(id) ON DELETE RESTRICT,
    INDEX idx_email (email),
    INDEX idx_cpf (cpf),
    INDEX idx_ativo (ativo),
    INDEX idx_grupo_usuario_id (grupo_usuario_id),
    UNIQUE KEY uk_email_cpf (email, cpf)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabela: turmas
-- Descrição: Define as turmas disponíveis na escola
CREATE TABLE IF NOT EXISTS turmas (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    serie VARCHAR(50) NOT NULL,
    turno ENUM('MATUTINO', 'VESPERTINO', 'NOTURNO') NOT NULL,
    capacidade INT NOT NULL DEFAULT 30,
    professor_id INT,
    ativo BOOLEAN DEFAULT TRUE,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (professor_id) REFERENCES usuarios(id) ON DELETE SET NULL,
    INDEX idx_serie (serie),
    INDEX idx_turno (turno),
    INDEX idx_ativo (ativo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabela: alunos
-- Descrição: Armazena informações dos alunos
CREATE TABLE IF NOT EXISTS alunos (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(150) NOT NULL,
    cpf VARCHAR(14) UNIQUE,
    email VARCHAR(150),
    telefone VARCHAR(20),
    endereco TEXT,
    idade INT,
    data_nascimento DATE,
    responsavel_nome VARCHAR(150),
    responsavel_telefone VARCHAR(20),
    responsavel_email VARCHAR(150),
    turma_id INT,
    turno ENUM('MATUTINO', 'VESPERTINO', 'NOTURNO') NOT NULL,
    status ENUM('ATIVO', 'INATIVO', 'CANCELADO') DEFAULT 'ATIVO',
    data_matricula TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (turma_id) REFERENCES turmas(id) ON DELETE SET NULL,
    INDEX idx_cpf (cpf),
    INDEX idx_email (email),
    INDEX idx_turma_id (turma_id),
    INDEX idx_turno (turno),
    INDEX idx_status (status),
    INDEX idx_data_matricula (data_matricula)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabela: matriculas
-- Descrição: Registra as matrículas dos alunos
CREATE TABLE IF NOT EXISTS matriculas (
    id INT PRIMARY KEY AUTO_INCREMENT,
    aluno_id INT NOT NULL,
    turma_id INT NOT NULL,
    turno ENUM('MATUTINO', 'VESPERTINO', 'NOTURNO') NOT NULL,
    data_matricula TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_cancelamento TIMESTAMP NULL,
    status ENUM('ATIVA', 'CANCELADA', 'SUSPENSA') DEFAULT 'ATIVA',
    motivo_cancelamento TEXT,
    usuario_responsavel_id INT,
    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (aluno_id) REFERENCES alunos(id) ON DELETE CASCADE,
    FOREIGN KEY (turma_id) REFERENCES turmas(id) ON DELETE RESTRICT,
    FOREIGN KEY (usuario_responsavel_id) REFERENCES usuarios(id) ON DELETE SET NULL,
    UNIQUE KEY uk_aluno_turma (aluno_id, turma_id),
    INDEX idx_aluno_id (aluno_id),
    INDEX idx_turma_id (turma_id),
    INDEX idx_status (status),
    INDEX idx_data_matricula (data_matricula)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabela: logs_auditoria
-- Descrição: Registra todas as operações no sistema para auditoria
CREATE TABLE IF NOT EXISTS logs_auditoria (
    id INT PRIMARY KEY AUTO_INCREMENT,
    usuario_id INT,
    tabela_afetada VARCHAR(100) NOT NULL,
    operacao ENUM('INSERT', 'UPDATE', 'DELETE') NOT NULL,
    registro_id INT NOT NULL,
    dados_anteriores JSON,
    dados_novos JSON,
    ip_address VARCHAR(45),
    user_agent TEXT,
    data_operacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE SET NULL,
    INDEX idx_usuario_id (usuario_id),
    INDEX idx_tabela_afetada (tabela_afetada),
    INDEX idx_operacao (operacao),
    INDEX idx_data_operacao (data_operacao)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- ÍNDICES ADICIONAIS (JUSTIFICATIVA)
-- ============================================================================
-- Índices criados para otimizar as consultas mais frequentes:
-- 1. Busca de usuários por email e CPF (autenticação)
-- 2. Listagem de alunos por turma e turno (relatórios)
-- 3. Consulta de matrículas ativas (dashboard)
-- 4. Auditoria por data (conformidade)
-- ============================================================================

-- ============================================================================
-- TRIGGERS (OBRIGATÓRIO: MÍNIMO 2)
-- ============================================================================

-- TRIGGER 1: Atualizar data_atualizacao automaticamente
-- Descrição: Atualiza o timestamp de modificação sempre que um registro é alterado
-- Função: Manter rastreabilidade de quando cada registro foi modificado
DELIMITER $$

CREATE TRIGGER tr_usuarios_update_timestamp
BEFORE UPDATE ON usuarios
FOR EACH ROW
BEGIN
    SET NEW.data_atualizacao = CURRENT_TIMESTAMP;
END$$

CREATE TRIGGER tr_alunos_update_timestamp
BEFORE UPDATE ON alunos
FOR EACH ROW
BEGIN
    SET NEW.data_atualizacao = CURRENT_TIMESTAMP;
END$$

CREATE TRIGGER tr_turmas_update_timestamp
BEFORE UPDATE ON turmas
FOR EACH ROW
BEGIN
    SET NEW.data_atualizacao = CURRENT_TIMESTAMP;
END$$

CREATE TRIGGER tr_matriculas_update_timestamp
BEFORE UPDATE ON matriculas
FOR EACH ROW
BEGIN
    SET NEW.data_atualizacao = CURRENT_TIMESTAMP;
END$$

-- TRIGGER 2: Registrar operações em logs_auditoria
-- Descrição: Cria um registro de auditoria sempre que um aluno é modificado
-- Função: Conformidade e rastreabilidade de alterações críticas
CREATE TRIGGER tr_alunos_audit_insert
AFTER INSERT ON alunos
FOR EACH ROW
BEGIN
    INSERT INTO logs_auditoria (usuario_id, tabela_afetada, operacao, registro_id, dados_novos)
    VALUES (NULL, 'alunos', 'INSERT', NEW.id, JSON_OBJECT(
        'nome', NEW.nome,
        'cpf', NEW.cpf,
        'email', NEW.email,
        'turma_id', NEW.turma_id,
        'status', NEW.status
    ));
END$$

CREATE TRIGGER tr_alunos_audit_update
AFTER UPDATE ON alunos
FOR EACH ROW
BEGIN
    INSERT INTO logs_auditoria (usuario_id, tabela_afetada, operacao, registro_id, dados_anteriores, dados_novos)
    VALUES (NULL, 'alunos', 'UPDATE', NEW.id, 
        JSON_OBJECT('nome', OLD.nome, 'email', OLD.email, 'status', OLD.status),
        JSON_OBJECT('nome', NEW.nome, 'email', NEW.email, 'status', NEW.status)
    );
END$$

CREATE TRIGGER tr_alunos_audit_delete
AFTER DELETE ON alunos
FOR EACH ROW
BEGIN
    INSERT INTO logs_auditoria (usuario_id, tabela_afetada, operacao, registro_id, dados_anteriores)
    VALUES (NULL, 'alunos', 'DELETE', OLD.id, JSON_OBJECT(
        'nome', OLD.nome,
        'cpf', OLD.cpf,
        'email', OLD.email,
        'status', OLD.status
    ));
END$$

DELIMITER ;

-- ============================================================================
-- VIEWS (OBRIGATÓRIO: MÍNIMO 2)
-- ============================================================================

-- VIEW 1: Relatório de Alunos por Turma
-- Descrição: Exibe todos os alunos agrupados por turma e turno
-- Justificativa: Facilita consultas para relatórios de ocupação de turmas
CREATE OR REPLACE VIEW vw_alunos_por_turma AS
SELECT 
    t.id AS turma_id,
    t.nome AS turma_nome,
    t.serie,
    t.turno,
    t.capacidade,
    COUNT(a.id) AS total_alunos,
    (t.capacidade - COUNT(a.id)) AS vagas_disponiveis,
    ROUND((COUNT(a.id) / t.capacidade) * 100, 2) AS percentual_ocupacao,
    GROUP_CONCAT(a.nome SEPARATOR ', ') AS nomes_alunos
FROM turmas t
LEFT JOIN alunos a ON t.id = a.turma_id AND a.status = 'ATIVO'
GROUP BY t.id, t.nome, t.serie, t.turno, t.capacidade
ORDER BY t.serie, t.turno;

-- VIEW 2: Relatório de Matrículas Ativas
-- Descrição: Exibe todas as matrículas ativas com informações do aluno e turma
-- Justificativa: Simplifica consultas para dashboard e relatórios administrativos
CREATE OR REPLACE VIEW vw_matriculas_ativas AS
SELECT 
    m.id AS matricula_id,
    a.id AS aluno_id,
    a.nome AS aluno_nome,
    a.cpf,
    a.email,
    a.telefone,
    t.id AS turma_id,
    t.nome AS turma_nome,
    t.serie,
    m.turno,
    u.nome AS professor_nome,
    m.data_matricula,
    m.status,
    DATEDIFF(CURDATE(), a.data_nascimento) DIV 365 AS idade_aluno
FROM matriculas m
INNER JOIN alunos a ON m.aluno_id = a.id
INNER JOIN turmas t ON m.turma_id = t.id
LEFT JOIN usuarios u ON t.professor_id = u.id
WHERE m.status = 'ATIVA' AND a.status = 'ATIVO'
ORDER BY t.serie, m.turno, a.nome;

-- VIEW 3: Auditoria de Operações Recentes
-- Descrição: Exibe as últimas operações realizadas no sistema
-- Justificativa: Facilita rastreamento e conformidade
CREATE OR REPLACE VIEW vw_auditoria_recente AS
SELECT 
    l.id,
    l.usuario_id,
    u.nome AS usuario_nome,
    l.tabela_afetada,
    l.operacao,
    l.registro_id,
    l.data_operacao,
    TIMESTAMPDIFF(MINUTE, l.data_operacao, CURRENT_TIMESTAMP) AS minutos_atras
FROM logs_auditoria l
LEFT JOIN usuarios u ON l.usuario_id = u.id
ORDER BY l.data_operacao DESC
LIMIT 100;

-- ============================================================================
-- FUNCTIONS (OBRIGATÓRIO: MÍNIMO 2)
-- ============================================================================

-- FUNCTION 1: Gerar ID customizado para matrículas
-- Descrição: Gera um ID único para matrículas no formato YYYYMMDD-XXXXX
-- Justificativa: Melhor rastreabilidade e conformidade com padrões de negócio
DELIMITER $$

CREATE FUNCTION fn_gerar_id_matricula()
RETURNS VARCHAR(20)
DETERMINISTIC
READS SQL DATA
BEGIN
    DECLARE v_count INT;
    DECLARE v_id VARCHAR(20);
    
    SELECT COUNT(*) + 1 INTO v_count 
    FROM matriculas 
    WHERE DATE(data_matricula) = CURDATE();
    
    SET v_id = CONCAT(
        DATE_FORMAT(CURDATE(), '%Y%m%d'),
        '-',
        LPAD(v_count, 5, '0')
    );
    
    RETURN v_id;
END$$

-- FUNCTION 2: Calcular idade do aluno
-- Descrição: Calcula a idade baseada na data de nascimento
-- Justificativa: Simplifica cálculos em queries e procedures
CREATE FUNCTION fn_calcular_idade(data_nascimento DATE)
RETURNS INT
DETERMINISTIC
READS SQL DATA
BEGIN
    DECLARE v_idade INT;
    SET v_idade = YEAR(CURDATE()) - YEAR(data_nascimento);
    
    IF MONTH(CURDATE()) < MONTH(data_nascimento) OR 
       (MONTH(CURDATE()) = MONTH(data_nascimento) AND DAY(CURDATE()) < DAY(data_nascimento)) THEN
        SET v_idade = v_idade - 1;
    END IF;
    
    RETURN v_idade;
END$$

-- FUNCTION 3: Validar CPF (formato básico)
-- Descrição: Valida se o CPF tem o formato correto
-- Justificativa: Garantir integridade dos dados de entrada
CREATE FUNCTION fn_validar_cpf(cpf_valor VARCHAR(14))
RETURNS BOOLEAN
DETERMINISTIC
READS SQL DATA
BEGIN
    DECLARE v_cpf_limpo VARCHAR(11);
    
    SET v_cpf_limpo = REPLACE(REPLACE(REPLACE(cpf_valor, '.', ''), '-', ''), ' ', '');
    
    IF LENGTH(v_cpf_limpo) = 11 AND v_cpf_limpo REGEXP '^[0-9]{11}$' THEN
        RETURN TRUE;
    ELSE
        RETURN FALSE;
    END IF;
END$$

DELIMITER ;

-- ============================================================================
-- PROCEDURES (OBRIGATÓRIO: MÍNIMO 2)
-- ============================================================================

-- PROCEDURE 1: Registrar nova matrícula
-- Descrição: Registra uma nova matrícula de um aluno em uma turma
-- Justificativa: Encapsula lógica de negócio complexa e garante integridade
DELIMITER $$

CREATE PROCEDURE sp_registrar_matricula(
    IN p_aluno_id INT,
    IN p_turma_id INT,
    IN p_turno VARCHAR(20),
    OUT p_matricula_id INT,
    OUT p_mensagem VARCHAR(255)
)
BEGIN
    DECLARE v_capacidade INT;
    DECLARE v_ocupacao INT;
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        SET p_mensagem = 'Erro ao registrar matrícula';
        SET p_matricula_id = -1;
    END;
    
    START TRANSACTION;
    
    -- Verificar se aluno existe
    IF NOT EXISTS (SELECT 1 FROM alunos WHERE id = p_aluno_id) THEN
        SET p_mensagem = 'Aluno não encontrado';
        SET p_matricula_id = -1;
        ROLLBACK;
    ELSE
        -- Verificar se turma existe
        IF NOT EXISTS (SELECT 1 FROM turmas WHERE id = p_turma_id) THEN
            SET p_mensagem = 'Turma não encontrada';
            SET p_matricula_id = -1;
            ROLLBACK;
        ELSE
            -- Verificar capacidade da turma
            SELECT capacidade INTO v_capacidade FROM turmas WHERE id = p_turma_id;
            SELECT COUNT(*) INTO v_ocupacao FROM alunos WHERE turma_id = p_turma_id AND status = 'ATIVO';
            
            IF v_ocupacao >= v_capacidade THEN
                SET p_mensagem = 'Turma sem vagas disponíveis';
                SET p_matricula_id = -1;
                ROLLBACK;
            ELSE
                -- Verificar se já existe matrícula ativa
                IF EXISTS (SELECT 1 FROM matriculas WHERE aluno_id = p_aluno_id AND status = 'ATIVA') THEN
                    SET p_mensagem = 'Aluno já possui matrícula ativa';
                    SET p_matricula_id = -1;
                    ROLLBACK;
                ELSE
                    -- Registrar matrícula
                    INSERT INTO matriculas (aluno_id, turma_id, turno, status)
                    VALUES (p_aluno_id, p_turma_id, p_turno, 'ATIVA');
                    
                    SET p_matricula_id = LAST_INSERT_ID();
                    SET p_mensagem = 'Matrícula registrada com sucesso';
                    
                    -- Atualizar aluno com turma
                    UPDATE alunos SET turma_id = p_turma_id, turno = p_turno WHERE id = p_aluno_id;
                    
                    COMMIT;
                END IF;
            END IF;
        END IF;
    END IF;
END$$

-- PROCEDURE 2: Cancelar matrícula
-- Descrição: Cancela uma matrícula existente com motivo
-- Justificativa: Garante que o cancelamento seja registrado corretamente
CREATE PROCEDURE sp_cancelar_matricula(
    IN p_matricula_id INT,
    IN p_motivo VARCHAR(255),
    OUT p_sucesso BOOLEAN,
    OUT p_mensagem VARCHAR(255)
)
BEGIN
    DECLARE v_aluno_id INT;
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        SET p_sucesso = FALSE;
        SET p_mensagem = 'Erro ao cancelar matrícula';
    END;
    
    START TRANSACTION;
    
    -- Verificar se matrícula existe e está ativa
    IF NOT EXISTS (SELECT 1 FROM matriculas WHERE id = p_matricula_id AND status = 'ATIVA') THEN
        SET p_sucesso = FALSE;
        SET p_mensagem = 'Matrícula não encontrada ou já foi cancelada';
        ROLLBACK;
    ELSE
        -- Obter ID do aluno
        SELECT aluno_id INTO v_aluno_id FROM matriculas WHERE id = p_matricula_id;
        
        -- Cancelar matrícula
        UPDATE matriculas 
        SET status = 'CANCELADA', 
            data_cancelamento = CURRENT_TIMESTAMP,
            motivo_cancelamento = p_motivo
        WHERE id = p_matricula_id;
        
        -- Atualizar status do aluno
        UPDATE alunos 
        SET status = 'INATIVO', turma_id = NULL
        WHERE id = v_aluno_id;
        
        SET p_sucesso = TRUE;
        SET p_mensagem = 'Matrícula cancelada com sucesso';
        
        COMMIT;
    END IF;
END$$

-- PROCEDURE 3: Listar alunos de uma turma
-- Descrição: Lista todos os alunos de uma turma específica
-- Justificativa: Simplifica consultas frequentes
CREATE PROCEDURE sp_listar_alunos_turma(
    IN p_turma_id INT
)
BEGIN
    SELECT 
        a.id,
        a.nome,
        a.cpf,
        a.email,
        a.telefone,
        a.idade,
        a.data_nascimento,
        a.responsavel_nome,
        a.responsavel_telefone,
        a.status,
        a.data_matricula
    FROM alunos a
    WHERE a.turma_id = p_turma_id AND a.status = 'ATIVO'
    ORDER BY a.nome;
END$$

DELIMITER ;

-- ============================================================================
-- DADOS INICIAIS
-- ============================================================================

-- Inserir grupos de usuários
INSERT INTO grupos_usuarios (nome, descricao, permissoes, ativo)
VALUES 
    ('ADMINISTRADOR', 'Acesso total ao sistema', JSON_ARRAY('READ', 'WRITE', 'DELETE', 'ADMIN'), TRUE),
    ('PROFESSOR', 'Acesso para gerenciar turmas e alunos', JSON_ARRAY('READ', 'WRITE'), TRUE),
    ('SECRETARIA', 'Acesso para registrar matrículas', JSON_ARRAY('READ', 'WRITE'), TRUE),
    ('RESPONSAVEL', 'Acesso limitado para consultar dados do aluno', JSON_ARRAY('READ'), TRUE);

-- Inserir usuários padrão (senhas são hashes bcrypt de exemplo)
INSERT INTO usuarios (nome, email, cpf, telefone, endereco, idade, senha_hash, ativo, grupo_usuario_id)
VALUES 
    ('Administrador', 'admin@matricula.com', '12345678900', '11999999999', 'Rua Admin, 100', 40, '$2a$10$slYQmyNdGzin7olVN3p5Be7DkH0dO0jvMrEg5Z5Z5Z5Z5Z5Z5Z5Z5', TRUE, 1),
    ('Professor João', 'joao@matricula.com', '98765432100', '11988888888', 'Rua Professor, 200', 35, '$2a$10$slYQmyNdGzin7olVN3p5Be7DkH0dO0jvMrEg5Z5Z5Z5Z5Z5Z5Z5Z5', TRUE, 2),
    ('Secretária Maria', 'maria@matricula.com', '55555555555', '11977777777', 'Rua Secretaria, 300', 30, '$2a$10$slYQmyNdGzin7olVN3p5Be7DkH0dO0jvMrEg5Z5Z5Z5Z5Z5Z5Z5Z5', TRUE, 3);

-- Inserir turmas
INSERT INTO turmas (nome, serie, turno, capacidade, professor_id, ativo)
VALUES 
    ('6º A', '6º Ano', 'MATUTINO', 30, 2, TRUE),
    ('6º B', '6º Ano', 'VESPERTINO', 30, 2, TRUE),
    ('7º A', '7º Ano', 'MATUTINO', 30, 2, TRUE),
    ('8º A', '8º Ano', 'NOTURNO', 25, 2, TRUE);

-- Inserir alunos de exemplo
INSERT INTO alunos (nome, cpf, email, telefone, endereco, idade, data_nascimento, responsavel_nome, responsavel_telefone, responsavel_email, turma_id, turno, status)
VALUES 
    ('João Silva', '11111111111', 'joao.silva@email.com', '11987654321', 'Rua A, 100', 12, '2011-05-15', 'Maria Silva', '11987654321', 'maria@email.com', 1, 'MATUTINO', 'ATIVO'),
    ('Ana Santos', '22222222222', 'ana.santos@email.com', '11987654322', 'Rua B, 200', 12, '2011-08-20', 'Carlos Santos', '11987654322', 'carlos@email.com', 1, 'MATUTINO', 'ATIVO'),
    ('Pedro Oliveira', '33333333333', 'pedro.oliveira@email.com', '11987654323', 'Rua C, 300', 13, '2010-03-10', 'Lucia Oliveira', '11987654323', 'lucia@email.com', 2, 'VESPERTINO', 'ATIVO');

-- ============================================================================
-- CONFIRMAÇÃO
-- ============================================================================
SELECT 'Banco de dados criado com sucesso!' AS status;
SELECT COUNT(*) AS total_usuarios FROM usuarios;
SELECT COUNT(*) AS total_alunos FROM alunos;
SELECT COUNT(*) AS total_turmas FROM turmas;
