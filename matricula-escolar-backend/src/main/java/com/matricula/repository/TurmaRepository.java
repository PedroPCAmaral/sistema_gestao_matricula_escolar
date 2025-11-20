package com.matricula.repository;

import com.matricula.model.Turma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository: TurmaRepository
 * Acesso aos dados de turmas
 */
@Repository
public interface TurmaRepository extends JpaRepository<Turma, Integer> {

    List<Turma> findBySerieAndAtivoTrueOrderByNome(String serie);

    List<Turma> findByTurnoAndAtivoTrueOrderByNome(Turma.Turno turno);

    List<Turma> findByAtivoTrueOrderBySerieAscTurnoAsc();

    List<Turma> findByProfessorIdAndAtivoTrue(Integer professorId);
}
