# Sistema de Gestão Comercial - Software Houses

## Descrição
Este repositório contém os scripts SQL para a criação, população e manipulação do banco de dados de um Sistema de Gestão Comercial voltado para Software Houses. O sistema contempla:

- Cadastro de clientes
- Cadastro de projetos
- Gerenciamento de tarefas
- Controle de orçamentos e pagamentos
- Relacionamentos entre as entidades do banco de dados

O objetivo é demonstrar fidelidade aos diagramas, boas práticas em SQL, organização e qualidade técnica.

---

## Estrutura do Repositório
- `sistema_gestao_comercial.sql` : Arquivo único contendo:
  - Scripts de criação das tabelas
  - Scripts de inserção de dados (população do banco)
  - Scripts de manipulação de dados (SELECT, UPDATE, DELETE)

---

## Tabelas e Descrição
1. **Clientes**
   - `id_cliente` (INT, PK, AUTO_INCREMENT)
   - `nome` (VARCHAR(100), NOT NULL)
   - `email` (VARCHAR(100), UNIQUE, NOT NULL)
   - `telefone` (VARCHAR(20))
   - `endereco` (VARCHAR(200))
   - `data_cadastro` (DATE, NOT NULL, DEFAULT CURRENT_DATE)

2. **Projetos**
   - `id_projeto` (INT, PK, AUTO_INCREMENT)
   - `nome` (VARCHAR(100), NOT NULL)
   - `descricao` (TEXT)
   - `data_inicio` (DATE, NOT NULL)
   - `data_fim` (DATE)
   - `id_cliente` (INT, FK → Clientes.id_cliente)

3. **Tarefas**
   - `id_tarefa` (INT, PK, AUTO_INCREMENT)
   - `descricao` (TEXT, NOT NULL)
   - `status` (ENUM('Pendente','Em Andamento','Concluída'), DEFAULT 'Pendente')
   - `data_inicio` (DATE, NOT NULL)
   - `data_fim` (DATE)
   - `id_projeto` (INT, FK → Projetos.id_projeto)

4. **Orcamentos**
   - `id_orcamento` (INT, PK, AUTO_INCREMENT)
   - `descricao` (TEXT, NOT NULL)
   - `valor` (DECIMAL(10,2), NOT NULL)
   - `data_criacao` (DATE, NOT NULL, DEFAULT CURRENT_DATE)
   - `id_projeto` (INT, FK → Projetos.id_projeto)

5. **Pagamentos**
   - `id_pagamento` (INT, PK, AUTO_INCREMENT)
   - `valor` (DECIMAL(10,2), NOT NULL)
   - `data_pagamento` (DATE, NOT NULL, DEFAULT CURRENT_DATE)
   - `id_orcamento` (INT, FK → Orcamentos.id_orcamento)

---

## Como Utilizar
1. Criar o banco de dados no MySQL:
   ```sql

SOURCE caminho/do/arquivo/sistema_gestao_comercial.sql;
SELECT * FROM Clientes;
SELECT * FROM Projetos;
SELECT * FROM Tarefas;
SELECT * FROM Orcamentos;
SELECT * FROM Pagamentos;

UPDATE Clientes SET telefone='(11) 99999-9999' WHERE id_cliente=1;
UPDATE Projetos SET nome='Projeto Atualizado' WHERE id_projeto=1;

Colaboradores

Pedro Paulo Costa do Amaral

Winiston Alle

Yuri Natanael

Vinicius Girão

Pedro Henrique Nunes

Observações

Todos os scripts foram testados no MySQL 8.0.

As tabelas estão normalizadas e estruturadas conforme os diagramas de entidade-relacionamento do trabalho.

O arquivo SQL contém comentários explicativos para facilitar o entendimento

   
   CREATE DATABASE IF NOT EXISTS sistema_gestao_comercial;
   USE sistema_gestao_comercial;
