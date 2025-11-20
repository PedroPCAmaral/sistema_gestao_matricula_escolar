# ROTEIRO DA APRESENTAÇÃO ORAL
## Sistema de Gerenciamento de Matrículas Escolares
### Tempo Total: 5 minutos

---

## 1. INTRODUÇÃO (0:00 - 0:30)

**Slide 1: Capa**

"Bom dia/tarde, professores e colegas. Meu nome é [seu nome] e vou apresentar o Sistema de Gerenciamento de Matrículas Escolares, desenvolvido como trabalho final da disciplina de Laboratório de Banco de Dados.

Este projeto demonstra a aplicação prática de conceitos avançados de SGBD, incluindo modelagem relacional, integração com NoSQL, autenticação segura e uma interface moderna e responsiva."

---

## 2. VISÃO GERAL DO PROJETO (0:30 - 1:00)

**Slide 2: Arquitetura do Sistema**

"O sistema foi desenvolvido com uma arquitetura em três camadas:

1. **Frontend:** HTML5, CSS3 e JavaScript puro - uma interface responsiva que funciona em desktop, tablet e mobile
2. **Backend:** Java com Spring Boot 3.2.0 - API REST com autenticação JWT e injeção de dependência
3. **Dados:** MySQL 8.0 para dados relacionais e Redis para fila de requisições

A comunicação entre camadas é feita via HTTP/REST com CORS habilitado."

---

## 3. BANCO DE DADOS RELACIONAL (1:00 - 2:00)

**Slide 3: Estrutura do MySQL**

"O banco de dados MySQL contém 6 tabelas principais:

1. **grupos_usuarios** - Define grupos com diferentes permissões
2. **usuarios** - Usuários do sistema com autenticação
3. **turmas** - Turmas com série, turno e capacidade
4. **alunos** - Alunos com informações pessoais e de contato
5. **matriculas** - Registro de matrículas com rastreamento de status
6. **logs_auditoria** - Auditoria de todas as operações

Todas as tabelas possuem índices para otimizar performance."

**Slide 4: Triggers (Obrigatório: Mínimo 2)**

"Implementei 2 triggers principais:

1. **Trigger de Timestamp:** Atualiza automaticamente a data de modificação em cada tabela
2. **Trigger de Auditoria:** Registra todas as operações (INSERT, UPDATE, DELETE) na tabela de logs

Estes triggers garantem rastreabilidade e conformidade regulatória."

**Slide 5: Views (Obrigatório: Mínimo 2)**

"Criei 2 views que simplificam consultas complexas:

1. **vw_alunos_por_turma:** Exibe alunos agrupados por turma com cálculo de ocupação e vagas disponíveis
2. **vw_matriculas_ativas:** Consolida informações de alunos, turmas e professores para matrículas ativas

As views melhoram performance e facilitam relatórios."

**Slide 6: Procedures e Functions (Obrigatório: Mínimo 2)**

"Implementei 3 procedures e 3 functions:

**Procedures:**
- sp_registrar_matricula: Registra matrícula com validações de capacidade
- sp_cancelar_matricula: Cancela matrícula com rastreamento de motivo

**Functions:**
- fn_gerar_id_matricula: Gera IDs customizados no formato YYYYMMDD-XXXXX
- fn_calcular_idade: Calcula idade baseada em data de nascimento
- fn_validar_cpf: Valida formato de CPF

Estas funções encapsulam lógica de negócio crítica."

---

## 4. BANCO DE DADOS NOSQL - REDIS (2:00 - 2:30)

**Slide 7: Justificativa do Redis**

"Redis foi escolhido como banco NoSQL por 5 razões principais:

1. **Performance:** Operações em memória com latência mínima
2. **Simplicidade:** Estrutura de dados simples e intuitiva
3. **Fila de Requisições:** Suporte nativo a operações de fila (PUSH/POP)
4. **Escalabilidade:** Suporta múltiplos clientes simultâneos
5. **Integração:** Fácil integração com Spring Data Redis

Redis é usado para armazenar fila de requisições de matrícula e cancelamento, permitindo processamento assíncrono."

**Slide 8: Estrutura de Dados**

"Redis armazena duas filas principais:

1. **fila:matriculas** - Fila de requisições de matrícula
2. **fila:cancelamentos** - Fila de cancelamentos

Quando um usuário registra uma matrícula, a requisição é adicionada à fila para processamento assíncrono. Isso melhora a responsividade da aplicação."

---

## 5. BACKEND - SPRING BOOT (2:30 - 3:30)

**Slide 9: Arquitetura do Backend**

"O backend segue uma arquitetura em camadas:

1. **Controllers:** Endpoints REST para comunicação com frontend
2. **Services:** Lógica de negócio e validações
3. **Repositories:** Acesso a dados via Spring Data JPA
4. **Models:** Entidades JPA mapeadas para tabelas MySQL

Todas as classes utilizam injeção de dependência do Spring, melhorando manutenibilidade e testabilidade."

**Slide 10: Autenticação e Segurança**

"Implementei autenticação JWT com Spring Security:

1. Usuário faz login com email e senha
2. Backend valida credenciais e gera token JWT
3. Token é armazenado no frontend (localStorage)
4. Todas as requisições incluem token no header Authorization
5. JwtAuthenticationFilter valida token em cada requisição
6. Senhas são criptografadas com BCrypt

Acesso ao banco de dados é feito com usuário dedicado (sem root)."

**Slide 11: Controle de Acesso**

"O sistema implementa controle de acesso baseado em grupos:

- **ADMINISTRADOR:** Acesso total (CRUD em todas as entidades)
- **PROFESSOR:** Acesso de leitura (READ)
- **SECRETARIA:** Acesso para gerenciar alunos e matrículas (CRUD)
- **RESPONSAVEL:** Acesso limitado (READ apenas)

Permissões são verificadas em cada endpoint com @PreAuthorize."

**Slide 12: Endpoints da API**

"Principais endpoints implementados:

- POST /auth/login - Autenticação
- GET/POST/PUT/DELETE /alunos - CRUD de alunos
- GET/POST/PUT/DELETE /turmas - CRUD de turmas
- GET/POST/DELETE /matriculas - CRUD de matrículas
- GET /matriculas/fila/status - Status das filas Redis

Todos os endpoints retornam JSON e incluem tratamento de erros."

---

## 6. FRONTEND (3:30 - 4:15)

**Slide 13: Interface do Usuário**

"O frontend é desenvolvido em HTML5, CSS3 e JavaScript puro, sem frameworks:

- **Responsivo:** Mobile-first design que funciona em todos os dispositivos
- **Moderno:** Gradientes, animações suaves e ícones FontAwesome
- **Intuitivo:** Navegação clara com navbar e modais para formulários
- **Seguro:** Autenticação JWT e validação de entrada

A interface contém 5 páginas principais: Login, Dashboard, Alunos, Turmas e Matrículas."

**Slide 14: Funcionalidades Principais**

"Funcionalidades implementadas:

1. **Login:** Autenticação segura com JWT
2. **Dashboard:** Visualização de estatísticas em tempo real
3. **Gerenciamento de Alunos:** CRUD com validação de CPF, email e telefone
4. **Gerenciamento de Turmas:** CRUD com capacidade e professor
5. **Gerenciamento de Matrículas:** Registrar, cancelar e validar capacidade
6. **Busca e Filtros:** Procurar alunos por nome ou CPF
7. **Auditoria:** Visualizar status da fila Redis

Todas as operações incluem feedback visual ao usuário."

---

## 7. DEMONSTRAÇÃO (4:15 - 4:45)

**Slide 15: Demonstração ao Vivo**

"Vou fazer uma demonstração rápida do sistema:

1. **Login:** Fazer login com credenciais de teste
2. **Dashboard:** Mostrar estatísticas e informações do sistema
3. **Criar Aluno:** Demonstrar formulário com validações
4. **Registrar Matrícula:** Mostrar validação de capacidade
5. **Visualizar Fila:** Mostrar status da fila Redis

[Executar demonstração ao vivo]"

---

## 8. CONCLUSÃO (4:45 - 5:00)

**Slide 16: Conclusão**

"Em conclusão, este projeto demonstra a aplicação prática de:

✅ Modelagem relacional com normalização
✅ Índices, triggers, views, procedures e functions
✅ Controle de acesso granular
✅ Integração com NoSQL (Redis)
✅ Autenticação segura com JWT
✅ API REST com boas práticas
✅ Interface moderna e responsiva

O sistema é funcional, profissional e pronto para produção, atendendo a todos os requisitos técnicos do trabalho final.

Obrigado pela atenção! Fico aberto a perguntas."

---

## NOTAS IMPORTANTES

### Timing
- **Introdução:** 30 segundos
- **Visão Geral:** 30 segundos
- **MySQL:** 60 segundos
- **Redis:** 30 segundos
- **Backend:** 60 segundos
- **Frontend:** 45 segundos
- **Demonstração:** 30 segundos
- **Conclusão:** 15 segundos

### Dicas de Apresentação

1. **Fale com confiança:** Você desenvolveu um sistema completo e profissional
2. **Mantenha contato visual:** Olhe para os professores, não para o slide
3. **Use exemplos concretos:** Mostre dados reais do sistema
4. **Demonstre ao vivo:** A demonstração é a melhor forma de impressionar
5. **Seja conciso:** Foque nos pontos principais, não entre em detalhes técnicos demais
6. **Prepare-se para perguntas:** Estude bem o código e a arquitetura
7. **Tenha um plano B:** Se a demonstração falhar, tenha screenshots prontos

### Possíveis Perguntas

**P: Por que usar Redis em vez de outra solução?**
R: Redis oferece performance superior, simplicidade de uso e suporte nativo a filas, sendo ideal para processamento assíncrono.

**P: Como funciona a autenticação?**
R: Usamos JWT (JSON Web Tokens) com Spring Security. O token é gerado no login e validado em cada requisição.

**P: Como garantir a integridade dos dados?**
R: Usamos triggers para auditoria, procedures para lógica crítica, constraints de chave estrangeira e transações ACID.

**P: O sistema é escalável?**
R: Sim, com índices otimizados, cache Redis e arquitetura em camadas, o sistema pode escalar horizontalmente.

**P: Como é feito o controle de acesso?**
R: Implementamos controle baseado em grupos de usuários com permissões granulares verificadas em cada endpoint.

---

**Tempo Total de Apresentação:** 5 minutos  
**Status:** Pronto para apresentação  
**Última Atualização:** Novembro de 2025
