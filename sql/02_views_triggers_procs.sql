CREATE OR REPLACE VIEW vw_usuarios_grupos AS
SELECT u.id, u.nome, u.email, g.nome AS grupo
FROM usuarios u
LEFT JOIN grupos_usuarios g ON u.grupo = g.id;

CREATE OR REPLACE VIEW vw_total_por_grupo AS
SELECT COALESCE(grupo, 'sem_grupo') AS grupo, COUNT(*) AS total
FROM usuarios
GROUP BY grupo;

DELIMITER $$
CREATE TRIGGER trg_usuario_insert AFTER INSERT ON usuarios
FOR EACH ROW
BEGIN
  INSERT INTO log_usuarios(id_usuario, acao) VALUES(NEW.id, 'CRIADO');
END$$

CREATE TRIGGER trg_usuario_update AFTER UPDATE ON usuarios
FOR EACH ROW
BEGIN
  INSERT INTO log_usuarios(id_usuario, acao) VALUES(OLD.id, 'ATUALIZADO');
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS BuscarUsuariosPorGrupo;
DELIMITER $$
CREATE PROCEDURE BuscarUsuariosPorGrupo(IN grupo_id VARCHAR(50))
BEGIN
  SELECT * FROM usuarios WHERE grupo = grupo_id;
END$$
DELIMITER ;

DROP FUNCTION IF EXISTS ContarUsuarios;
DELIMITER $$
CREATE FUNCTION ContarUsuarios() RETURNS INT DETERMINISTIC
BEGIN
  DECLARE total INT;
  SELECT COUNT(*) INTO total FROM usuarios;
  RETURN total;
END$$
DELIMITER ;
