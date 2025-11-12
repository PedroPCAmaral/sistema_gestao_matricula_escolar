package br.com.matricula.service;

import br.com.matricula.model.Aluno;
import br.com.matricula.repository.AlunoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AlunoService {

    @Autowired
    private AlunoRepository alunoRepository;

    // Listar todos os alunos
    public List<Aluno> listarTodos() {
        return alunoRepository.findAll();
    }

    // Buscar aluno por ID
    public Optional<Aluno> buscarPorId(Integer idAluno) {
        return alunoRepository.findById(idAluno); // <-- Integer direto
    }

    // Salvar novo aluno
    public Aluno salvar(Aluno aluno) {
        return alunoRepository.save(aluno);
    }

    // Atualizar aluno existente
    public Aluno atualizar(Integer idAluno, Aluno aluno) {
        return alunoRepository.findById(idAluno)
                .map(a -> {
                    a.setNome(aluno.getNome());
                    a.setEmail(aluno.getEmail());
                    a.setDataNascimento(aluno.getDataNascimento());
                    return alunoRepository.save(a);
                }).orElse(null);
    }

    // Deletar aluno
    public void deletar(Integer idAluno) {
        alunoRepository.deleteById(idAluno); // <-- Integer direto
    }
}
