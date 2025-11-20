package com.matricula.repository;

import com.matricula.model.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Repository: AlunoRepository
 * Acesso aos dados de alunos
 */
@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Integer> {

    Optional<Aluno> findByCpf(String cpf);

    Optional<Aluno> findByEmail(String email);

    List<Aluno> findByTurmaIdAndStatusOrderByNome(Integer turmaId, Aluno.Status status);

    List<Aluno> findByTurnoAndStatusOrderByNome(Aluno.Turno turno, Aluno.Status status);

    @Query("SELECT a FROM Aluno a WHERE a.status = 'ATIVO' ORDER BY a.nome")
    List<Aluno> findAllAtivos();

    long countByTurmaIdAndStatus(Integer turmaId, Aluno.Status status);
}
