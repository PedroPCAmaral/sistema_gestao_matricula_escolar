package com.matricula.repository;

import com.matricula.model.Matricula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Repository: MatriculaRepository
 * Acesso aos dados de matrículas
 */
@Repository
public interface MatriculaRepository extends JpaRepository<Matricula, Integer> {

    List<Matricula> findByAlunoIdOrderByDataMatriculaDesc(Integer alunoId);

    List<Matricula> findByTurmaIdAndStatusOrderByDataMatricula(Integer turmaId, Matricula.Status status);

    Optional<Matricula> findByAlunoIdAndStatusOrderByDataMatriculaDesc(Integer alunoId, Matricula.Status status);

    List<Matricula> findByStatusOrderByDataMatriculaDesc(Matricula.Status status);

    long countByTurmaIdAndStatus(Integer turmaId, Matricula.Status status);

    boolean existsByAlunoIdAndTurmaIdAndStatus(Integer alunoId, Integer turmaId, Matricula.Status status);
}
