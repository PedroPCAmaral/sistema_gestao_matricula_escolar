package br.com.matricula;

import java.util.List;
import java.util.regex.Pattern;

public class AlunoServico {

    private final AlunoDAO alunoDAO;
    // Listas de opções válidas para validação (case-insensitive)
    private static final List<String> SERIES_VALIDAS = List.of("PRIMEIRO", "FUND 1", "FUND 2", "MÉDIO");
    private static final List<String> TURNOS_VALIDOS = List.of("MATUTINO", "VESPERTINO");
    private static final Pattern CPF_PATTERN = Pattern.compile("^\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}$");

    public AlunoServico() {
        this.alunoDAO = new AlunoDAO();
    }

    public void cadastrarAluno(String nome, String cpf, int idade, String serie, String turno, String telefone) {
        // Validação do formato do CPF

        // Validação simples para evitar CPF duplicado
        if (alunoDAO.buscarPorCpf(cpf) != null) {
            throw new ServiceException("Já existe um aluno cadastrado com este CPF.");
        }

        // Validação de campos obrigatórios que não podem ser vazios
        if (nome == null || nome.trim().isEmpty() || cpf == null || cpf.trim().isEmpty() || serie == null || serie.trim().isEmpty() || turno == null || turno.trim().isEmpty()) {
            throw new ServiceException("Nome, CPF, Série e Turno são campos obrigatórios.");
        }

        // Validação de conteúdo para Série e Turno
        if (!SERIES_VALIDAS.contains(serie.trim().toUpperCase())) {
            throw new ServiceException("Série inválida. Opções válidas: Primeiro, Fund 1, Fund 2, Médio.");
        }

        if (!TURNOS_VALIDOS.contains(turno.trim().toUpperCase())) {
            throw new ServiceException("Turno inválido. Opções válidas: Matutino, Vespertino.");
        }

        Aluno novoAluno = new Aluno(nome, cpf, idade, serie, turno, telefone);
        alunoDAO.cadastrar(novoAluno);
        System.out.println("✅ Aluno cadastrado com sucesso!");
    }

    public void listarAlunos() {
        List<Aluno> alunos = alunoDAO.listar();
        if (alunos.isEmpty()) {
            System.out.println("⚠️ Nenhum aluno cadastrado.");
        } else {
            System.out.println("\n--- LISTA DE ALUNOS ---");
            for (Aluno aluno : alunos) {
                System.out.println(aluno); // Usa o método toString() da classe Aluno
            }
            System.out.println("-----------------------");
        }
    }

    public void buscarAlunoPorCPF(String cpf) {
        Aluno aluno = alunoDAO.buscarPorCpf(cpf);
        if (aluno != null) {
            System.out.println("--- ALUNO ENCONTRADO ---");
            System.out.println(aluno);
            System.out.println("------------------------");
        } else {
            System.out.println("⚠️ Aluno com o CPF " + cpf + " não encontrado.");
        }
    }

    public void editarAluno(String cpf, String nome, String idadeStr, String serie, String turno, String telefone) {
        Aluno aluno = alunoDAO.buscarPorCpf(cpf);
        if (aluno == null) {
            throw new ServiceException("Aluno com o CPF " + cpf + " não encontrado.");
        }

        boolean isAltered = false;
        // Lógica de atualização: só altera se um novo valor foi fornecido
        if (!nome.trim().isEmpty()) { aluno.setNome(nome); isAltered = true; }
        if (!idadeStr.trim().isEmpty()) {
            try {
                aluno.setIdade(Integer.parseInt(idadeStr));
                isAltered = true;
            } catch (NumberFormatException e) {
                throw new ServiceException("Idade inválida. Por favor, insira um número.");
            }
        }
        if (!serie.trim().isEmpty()) {
            if (!SERIES_VALIDAS.contains(serie.trim().toUpperCase())) {
                throw new ServiceException("Série inválida. Opções válidas: Primeiro, Fund 1, Fund 2, Médio.");
            }
            aluno.setSerie(serie); isAltered = true;
        }
        if (!turno.trim().isEmpty()) {
            if (!TURNOS_VALIDOS.contains(turno.trim().toUpperCase())) {
                throw new ServiceException("Turno inválido. Opções válidas: Matutino, Vespertino.");
            }
            aluno.setTurno(turno); isAltered = true;
        }
        if (!telefone.trim().isEmpty()) { aluno.setTelefone(telefone); isAltered = true; }

        if (isAltered) {
            alunoDAO.editar(aluno);
            System.out.println("✅ Aluno atualizado com sucesso!");
        } else {
            System.out.println("ℹ️ Nenhum dado foi alterado.");
        }
    }

    public void removerAluno(String cpf) {
        int removido = alunoDAO.remover(cpf);
        if (removido > 0) {
            System.out.println("✅ Aluno removido com sucesso!");
        } else {
            throw new ServiceException("Aluno com o CPF " + cpf + " não encontrado para remoção.");
        }
    }

    public static boolean isCpfValido(String cpf) {
        if (cpf == null) return false;
        // Regex para validar o formato XXX.XXX.XXX-XX
        return CPF_PATTERN.matcher(cpf).matches();
    }
}