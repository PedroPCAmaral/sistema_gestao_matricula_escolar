package br.com.matricula.repository;

import br.com.matricula.model.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Integer> {
    // JpaRepository já fornece todos os métodos básicos:
    // findAll(), findById(Integer id), save(Aluno aluno), deleteById(Integer id)
}
