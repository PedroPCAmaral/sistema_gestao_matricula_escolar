package br.com.matricula;

import java.util.Scanner;
public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        AlunoServico servico = new AlunoServico();
        int opcao;

        do {
            System.out.println("\n===== MENU MATRÍCULA ESCOLAR =====");
            System.out.println("1 - Cadastrar aluno");
            System.out.println("2 - Listar alunos");
            System.out.println("3 - Buscar aluno por CPF");
            System.out.println("4 - Editar aluno");
            System.out.println("5 - Remover aluno");
            System.out.println("6 - Sair");
            System.out.print("Escolha uma opção: ");

            try {
                opcao = sc.nextInt();
            } catch (java.util.InputMismatchException e) {
                System.out.println("❌ Erro: Por favor, digite um número válido.");
                opcao = 0; // Define uma opção inválida para repetir o loop
            } finally {
                sc.nextLine(); // Limpa o buffer do scanner, crucial para evitar erros de leitura
            }

            try {
                switch (opcao) {
                    case 1:
                        handleCadastrarAluno(sc, servico);
                        break;
                    case 2:
                        servico.listarAlunos();
                        break;
                    case 3:
                        handleBuscarAluno(sc, servico);
                        break;
                    case 4:
                        handleEditarAluno(sc, servico);
                        break;
                    case 5:
                        handleRemoverAluno(sc, servico);
                        break;
                    case 6:
                        System.out.println("Saindo...");
                        break;
                    default:
                        if (opcao != 0) System.out.println("❌ Opção inválida!");
                }
            } catch (DataAccessException | ServiceException e) {
                System.out.println("❌ " + e.getMessage());
            }

        } while (opcao != 6);

        sc.close();
    }

    private static void handleCadastrarAluno(Scanner sc, AlunoServico servico) {
        System.out.print("Nome completo: ");
        String nome = sc.nextLine();
        System.out.print("CPF (formato XXX.XXX.XXX-XX): ");
        String cpf = sc.nextLine();
        // Validação imediata do formato do CPF
        if (!AlunoServico.isCpfValido(cpf)) {
            System.out.println("❌ Erro: Formato de CPF inválido. Use o formato XXX.XXX.XXX-XX.");
            return;
        }
        System.out.print("Idade: ");
        int idade;
        try {
            idade = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("❌ Erro: Idade inválida. Por favor, insira um número.");
            return;
        }

        System.out.print("Série (Primeiro, Fund 1, Fund 2, Médio): ");
        String serie = sc.nextLine();
        System.out.print("Turno (Matutino, Vespertino): ");
        String turno = sc.nextLine();
        System.out.print("Telefone: ");
        String telefone = sc.nextLine();

        // Validação de campos obrigatórios
        if (nome.isBlank() || cpf.isBlank() || serie.isBlank() || turno.isBlank()) {
            System.out.println("❌ Erro: Nome, CPF, Série e Turno são obrigatórios.");
            return;
        }

        servico.cadastrarAluno(nome, cpf, idade, serie, turno, telefone);
    }

    private static void handleBuscarAluno(Scanner sc, AlunoServico servico) {
        System.out.print("Digite o CPF do aluno para busca (formato XXX.XXX.XXX-XX): ");
        String cpfBusca = sc.nextLine();
        if (cpfBusca.isBlank()) {
            System.out.println("❌ Erro: CPF não pode ser vazio.");
            return;
        }
        // Validação imediata do formato do CPF
        if (!AlunoServico.isCpfValido(cpfBusca)) {
            System.out.println("❌ Erro: Formato de CPF inválido. Use o formato XXX.XXX.XXX-XX.");
            return;
        }
        servico.buscarAlunoPorCPF(cpfBusca);
    }

    private static void handleEditarAluno(Scanner sc, AlunoServico servico) {
        System.out.print("Digite o CPF do aluno que deseja editar (formato XXX.XXX.XXX-XX): ");
        String cpfEdit = sc.nextLine();
        if (cpfEdit.isBlank()) {
            System.out.println("❌ Erro: CPF não pode ser vazio.");
            return;
        }
        // Validação imediata do formato do CPF
        if (!AlunoServico.isCpfValido(cpfEdit)) {
            System.out.println("❌ Erro: Formato de CPF inválido. Use o formato XXX.XXX.XXX-XX.");
            return;
        }

        System.out.println("Digite os novos dados (deixe em branco para não alterar):");
        System.out.print("Novo nome: ");
        String novoNome = sc.nextLine();
        System.out.print("Nova idade (apenas números): ");
        String novaIdadeStr = sc.nextLine();
        System.out.print("Nova série (Primeiro, Fund 1, Fund 2, Médio): ");
        String novaSerie = sc.nextLine();
        System.out.print("Novo turno (Matutino, Vespertino): ");
        String novoTurno = sc.nextLine();
        System.out.print("Novo telefone: ");
        String novoTelefone = sc.nextLine();

        servico.editarAluno(cpfEdit, novoNome, novaIdadeStr, novaSerie, novoTurno, novoTelefone);
    }

    private static void handleRemoverAluno(Scanner sc, AlunoServico servico) {
        System.out.print("Digite o CPF do aluno para remover (formato XXX.XXX.XXX-XX): ");
        String cpfRemove = sc.nextLine();
        if (cpfRemove.isBlank()) {
            System.out.println("❌ Erro: CPF não pode ser vazio.");
            return;
        }
        // Validação imediata do formato do CPF
        if (!AlunoServico.isCpfValido(cpfRemove)) {
            System.out.println("❌ Erro: Formato de CPF inválido. Use o formato XXX.XXX.XXX-XX.");
            return;
        }

        System.out.print("Tem certeza que deseja remover este aluno? (S/N): ");
        String confirmacao = sc.nextLine();
        if (confirmacao.equalsIgnoreCase("S")) {
            servico.removerAluno(cpfRemove);
        } else {
            System.out.println("ℹ️ Remoção cancelada.");
        }
    }
}
