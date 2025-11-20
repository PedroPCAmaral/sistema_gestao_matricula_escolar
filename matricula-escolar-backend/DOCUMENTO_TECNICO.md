# SISTEMA DE GERENCIAMENTO DE MATRÍCULAS ESCOLARES
## Documento Técnico - Trabalho Final de Laboratório de Banco de Dados

---

## 1. INTRODUÇÃO

O presente documento descreve a implementação de um **Sistema Completo de Gerenciamento de Matrículas Escolares**, desenvolvido como trabalho final da disciplina de Laboratório de Banco de Dados. O sistema integra um banco de dados relacional (MySQL), um banco NoSQL (Redis), um backend em Java com Spring Boot e um frontend em HTML/CSS/JavaScript puro.

### 1.1 Objetivo

Desenvolver uma aplicação funcional que demonstre o domínio dos principais conceitos e recursos de um Sistema Gerenciador de Banco de Dados (SGBD), incluindo:

- Modelagem de dados relacional
- Implementação de índices, triggers, views e procedures
- Controle de acesso e segurança
- Integração com banco de dados NoSQL
- Autenticação e autorização
- API REST com boas práticas

### 1.2 Escopo

O sistema permite o gerenciamento completo de:
- Usuários com diferentes níveis de acesso
- Alunos com informações pessoais e de contato
- Turmas com capacidade e professor responsável
- Matrículas com rastreamento de status
- Fila de requisições para processamento assíncrono

---

## 2. ARQUITETURA DO SISTEMA

### 2.1 Visão Geral

```
┌─────────────────────────────────────────────────────────────┐
│                    CAMADA DE APRESENTAÇÃO                   │
│         Frontend (HTML5/CSS3/JavaScript Puro)              │
│              Interface Responsiva e Intuitiva              │
└────────────────────────┬────────────────────────────────────┘
                         │ HTTP/REST
┌────────────────────────▼────────────────────────────────────┐
│                    CAMADA DE NEGÓCIO                        │
│         Backend (Java Spring Boot 3.2.0)                   │
│    Controllers → Services → Repositories → Models          │
└────────────────────────┬────────────────────────────────────┘
                         │ JDBC/JPA
┌────────────────────────▼────────────────────────────────────┐
│                    CAMADA DE DADOS                          │
│  ┌──────────────────────┐      ┌──────────────────────┐    │
│  │   MySQL 8.0          │      │   Redis 6.0+         │    │
│  │  (Dados Relacionais) │      │  (Fila de Requisições)│   │
│  └──────────────────────┘      └──────────────────────┘    │
└─────────────────────────────────────────────────────────────┘
```

### 2.2 Componentes Principais

#### 2.2.1 Frontend
- **Tecnologia:** HTML5, CSS3, JavaScript Vanilla
- **Responsividade:** Mobile-first design
- **Autenticação:** JWT (tokens armazenados em localStorage)
- **Comunicação:** Fetch API com tratamento de CORS

#### 2.2.2 Backend
- **Framework:** Spring Boot 3.2.0
- **Segurança:** Spring Security com JWT
- **Persistência:** Spring Data JPA com Hibernate
- **Cache/Fila:** Spring Data Redis
- **Validação:** Jakarta Validation

#### 2.2.3 Banco de Dados Relacional
- **SGBD:** MySQL 8.0
- **Tipo:** Relacional com InnoDB
- **Características:** Triggers, Views, Procedures, Functions, Índices

#### 2.2.4 Banco de Dados NoSQL
- **SGBD:** Redis
- **Uso:** Fila de requisições de matrícula e cancelamento
- **Justificativa:** Performance, simplicidade e suporte a operações assíncronas

---

## 3. MODELAGEM DE DADOS

### 3.1 Banco de Dados Relacional (MySQL)

#### 3.1.1 Tabelas Obrigatórias

**Tabela: grupos_usuarios**
```sql
CREATE TABLE grupos_usuarios (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL UNIQUE,
    descricao TEXT,
    permissoes JSON NOT NULL DEFAULT '[]',
    ativo BOOLEAN DEFAULT TRUE,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

**Justificativa:** Implementa controle granular de acesso baseado em grupos, permitindo diferentes níveis de permissão (ADMINISTRADOR, PROFESSOR, SECRETARIA, RESPONSAVEL).

**Tabela: usuarios**
```sql
CREATE TABLE usuarios (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    cpf VARCHAR(14) UNIQUE,
    telefone VARCHAR(20),
    endereco TEXT,
    idade INT,
    senha_hash VARCHAR(255) NOT NULL,
    ativo BOOLEAN DEFAULT TRUE,
    grupo_usuario_id INT NOT NULL,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (grupo_usuario_id) REFERENCES grupos_usuarios(id)
);
```

**Justificativa:** Tabela obrigatória para autenticação e controle de acesso. Armazena informações de usuários com validação de email e CPF únicos.

#### 3.1.2 Tabelas Adicionais

**Tabela: turmas**
- Armazena informações das turmas (série, turno, capacidade, professor)
- Índices em: serie, turno, ativo

**Tabela: alunos**
- Armazena informações dos alunos (nome, CPF, telefone, endereço, idade, turma, turno)
- Índices em: cpf, email, turma_id, turno, status, data_matricula

**Tabela: matriculas**
- Registra as matrículas dos alunos em turmas
- Índices em: aluno_id, turma_id, status, data_matricula
- Constraint único: (aluno_id, turma_id)

**Tabela: logs_auditoria**
- Registra todas as operações para conformidade e rastreamento
- Índices em: usuario_id, tabela_afetada, operacao, data_operacao

#### 3.1.3 Índices

| Tabela | Coluna(s) | Tipo | Justificativa |
|--------|-----------|------|---------------|
| usuarios | email | Simples | Busca rápida durante login |
| usuarios | cpf | Simples | Validação de CPF único |
| alunos | cpf | Simples | Busca por CPF |
| alunos | turma_id | Simples | Listagem de alunos por turma |
| alunos | turno | Simples | Filtros por turno |
| matriculas | aluno_id | Simples | Histórico de matrículas |
| matriculas | turma_id | Simples | Matrículas por turma |
| matriculas | status | Simples | Matrículas ativas |
| logs_auditoria | data_operacao | Simples | Consultas de auditoria |

**Justificativa:** Índices otimizam as consultas mais frequentes, melhorando significativamente a performance em operações de leitura.

#### 3.1.4 Triggers (Obrigatório: Mínimo 2)

**TRIGGER 1: Atualizar data_atualizacao automaticamente**

```sql
CREATE TRIGGER tr_usuarios_update_timestamp
BEFORE UPDATE ON usuarios
FOR EACH ROW
BEGIN
    SET NEW.data_atualizacao = CURRENT_TIMESTAMP;
END
```

**Função:** Atualiza automaticamente o timestamp de modificação sempre que um registro é alterado.

**Justificativa:** Garante rastreabilidade de quando cada registro foi modificado, sem necessidade de lógica na aplicação.

**TRIGGER 2: Registrar operações em logs_auditoria**

```sql
CREATE TRIGGER tr_alunos_audit_insert
AFTER INSERT ON alunos
FOR EACH ROW
BEGIN
    INSERT INTO logs_auditoria (tabela_afetada, operacao, registro_id, dados_novos)
    VALUES ('alunos', 'INSERT', NEW.id, JSON_OBJECT(...));
END
```

**Função:** Cria registro de auditoria sempre que um aluno é inserido, atualizado ou deletado.

**Justificativa:** Conformidade regulatória e rastreamento de alterações críticas.

#### 3.1.5 Views (Obrigatório: Mínimo 2)

**VIEW 1: vw_alunos_por_turma**

```sql
CREATE OR REPLACE VIEW vw_alunos_por_turma AS
SELECT 
    t.id AS turma_id,
    t.nome AS turma_nome,
    t.serie,
    t.turno,
    t.capacidade,
    COUNT(a.id) AS total_alunos,
    (t.capacidade - COUNT(a.id)) AS vagas_disponiveis,
    ROUND((COUNT(a.id) / t.capacidade) * 100, 2) AS percentual_ocupacao
FROM turmas t
LEFT JOIN alunos a ON t.id = a.turma_id AND a.status = 'ATIVO'
GROUP BY t.id, t.nome, t.serie, t.turno, t.capacidade
ORDER BY t.serie, t.turno;
```

**Justificativa:** Simplifica consultas para relatórios de ocupação de turmas, evitando cálculos complexos na aplicação.

**VIEW 2: vw_matriculas_ativas**

```sql
CREATE OR REPLACE VIEW vw_matriculas_ativas AS
SELECT 
    m.id AS matricula_id,
    a.id AS aluno_id,
    a.nome AS aluno_nome,
    a.cpf,
    t.id AS turma_id,
    t.nome AS turma_nome,
    t.serie,
    m.turno,
    u.nome AS professor_nome,
    m.data_matricula,
    m.status
FROM matriculas m
INNER JOIN alunos a ON m.aluno_id = a.id
INNER JOIN turmas t ON m.turma_id = t.id
LEFT JOIN usuarios u ON t.professor_id = u.id
WHERE m.status = 'ATIVA' AND a.status = 'ATIVO'
ORDER BY t.serie, m.turno, a.nome;
```

**Justificativa:** Facilita consultas para dashboard e relatórios administrativos com dados consolidados.

#### 3.1.6 Procedures (Obrigatório: Mínimo 2)

**PROCEDURE 1: sp_registrar_matricula**

```sql
CREATE PROCEDURE sp_registrar_matricula(
    IN p_aluno_id INT,
    IN p_turma_id INT,
    IN p_turno VARCHAR(20),
    OUT p_matricula_id INT,
    OUT p_mensagem VARCHAR(255)
)
BEGIN
    -- Validações
    -- Verificar capacidade da turma
    -- Registrar matrícula com transação
    -- Atualizar aluno com turma
END
```

**Função:** Encapsula a lógica complexa de registrar uma matrícula, incluindo validações e transações.

**Justificativa:** Garante integridade dos dados e reutilização de lógica de negócio.

**PROCEDURE 2: sp_cancelar_matricula**

```sql
CREATE PROCEDURE sp_cancelar_matricula(
    IN p_matricula_id INT,
    IN p_motivo VARCHAR(255),
    OUT p_sucesso BOOLEAN,
    OUT p_mensagem VARCHAR(255)
)
BEGIN
    -- Validar matrícula
    -- Cancelar matrícula
    -- Atualizar status do aluno
    -- Registrar em auditoria
END
```

**Função:** Cancela uma matrícula com rastreamento do motivo.

**Justificativa:** Operação crítica que requer validações e rastreamento.

#### 3.1.7 Functions (Obrigatório: Mínimo 2)

**FUNCTION 1: fn_gerar_id_matricula()**

```sql
CREATE FUNCTION fn_gerar_id_matricula()
RETURNS VARCHAR(20)
DETERMINISTIC
READS SQL DATA
BEGIN
    DECLARE v_count INT;
    DECLARE v_id VARCHAR(20);
    
    SELECT COUNT(*) + 1 INTO v_count 
    FROM matriculas 
    WHERE DATE(data_matricula) = CURDATE();
    
    SET v_id = CONCAT(
        DATE_FORMAT(CURDATE(), '%Y%m%d'),
        '-',
        LPAD(v_count, 5, '0')
    );
    
    RETURN v_id;
END
```

**Justificativa:** Gera IDs customizados para matrículas no formato YYYYMMDD-XXXXX, melhorando rastreabilidade.

**FUNCTION 2: fn_calcular_idade()**

```sql
CREATE FUNCTION fn_calcular_idade(data_nascimento DATE)
RETURNS INT
DETERMINISTIC
READS SQL DATA
BEGIN
    DECLARE v_idade INT;
    SET v_idade = YEAR(CURDATE()) - YEAR(data_nascimento);
    
    IF MONTH(CURDATE()) < MONTH(data_nascimento) OR 
       (MONTH(CURDATE()) = MONTH(data_nascimento) AND DAY(CURDATE()) < DAY(data_nascimento)) THEN
        SET v_idade = v_idade - 1;
    END IF;
    
    RETURN v_idade;
END
```

**Justificativa:** Calcula idade automaticamente baseada na data de nascimento, simplificando queries.

**FUNCTION 3: fn_validar_cpf()**

```sql
CREATE FUNCTION fn_validar_cpf(cpf_valor VARCHAR(14))
RETURNS BOOLEAN
DETERMINISTIC
READS SQL DATA
BEGIN
    DECLARE v_cpf_limpo VARCHAR(11);
    
    SET v_cpf_limpo = REPLACE(REPLACE(REPLACE(cpf_valor, '.', ''), '-', ''), ' ', '');
    
    IF LENGTH(v_cpf_limpo) = 11 AND v_cpf_limpo REGEXP '^[0-9]{11}$' THEN
        RETURN TRUE;
    ELSE
        RETURN FALSE;
    END IF;
END
```

**Justificativa:** Valida formato de CPF, garantindo integridade dos dados.

### 3.2 Banco de Dados NoSQL (Redis)

#### 3.2.1 Justificativa da Escolha

**Redis foi escolhido porque:**

1. **Performance:** Operações em memória com latência mínima
2. **Simplicidade:** Estrutura de dados simples (listas, strings)
3. **Fila de Requisições:** Suporte nativo a operações de fila (PUSH/POP)
4. **Escalabilidade:** Suporta múltiplos clientes simultâneos
5. **Persistência:** Opção de persistência em disco
6. **Integração:** Fácil integração com Spring Data Redis

#### 3.2.2 Estrutura de Dados

**Fila de Matrículas:**
```
Key: fila:matriculas
Type: List (FIFO)
Estrutura: [
    "Matrícula ID: 1, Aluno: João Silva, Turma: 6º A",
    "Matrícula ID: 2, Aluno: Ana Santos, Turma: 6º B",
    ...
]
```

**Fila de Cancelamentos:**
```
Key: fila:cancelamentos
Type: List (FIFO)
Estrutura: [
    "Cancelamento ID: 1, Aluno: Pedro Oliveira, Motivo: Mudança de escola",
    ...
]
```

#### 3.2.3 Operações Principais

| Operação | Comando | Descrição |
|----------|---------|-----------|
| Adicionar | RPUSH | Adiciona elemento ao final da fila |
| Remover | LPOP | Remove elemento do início da fila |
| Tamanho | LLEN | Retorna tamanho da fila |
| Listar | LRANGE | Lista elementos da fila |
| Limpar | DEL | Deleta a fila |

#### 3.2.4 Aplicação

**Fluxo de Processamento:**

```
1. Usuário registra matrícula via frontend
   ↓
2. Backend valida dados
   ↓
3. Backend registra matrícula no MySQL
   ↓
4. Backend adiciona mensagem à fila Redis
   ↓
5. Sistema assíncrono processa fila
   ↓
6. Notificações são enviadas
```

---

## 4. IMPLEMENTAÇÃO DO BACKEND

### 4.1 Arquitetura em Camadas

```
┌─────────────────────────────────────┐
│      Controllers (REST API)         │
│  AuthController, AlunoController    │
│  TurmaController, MatriculaController
└────────────────┬────────────────────┘
                 │
┌────────────────▼────────────────────┐
│      Services (Lógica de Negócio)   │
│  AuthService, AlunoService          │
│  TurmaService, MatriculaService     │
└────────────────┬────────────────────┘
                 │
┌────────────────▼────────────────────┐
│    Repositories (Acesso a Dados)    │
│  UsuarioRepository, AlunoRepository │
│  TurmaRepository, MatriculaRepository
└────────────────┬────────────────────┘
                 │
┌────────────────▼────────────────────┐
│      Models (Entidades JPA)         │
│  Usuario, Aluno, Turma, Matricula   │
└─────────────────────────────────────┘
```

### 4.2 Injeção de Dependência

O projeto utiliza **injeção de dependência do Spring** em todos os serviços:

```java
@Service
@RequiredArgsConstructor
public class AlunoService {
    private final AlunoRepository alunoRepository;
    private final TurmaRepository turmaRepository;
    
    // Dependências injetadas automaticamente
}
```

**Benefícios:**
- Desacoplamento de componentes
- Facilita testes unitários
- Melhor manutenibilidade
- Configuração centralizada

### 4.3 Autenticação e Segurança

#### 4.3.1 Fluxo de Autenticação

```
1. Usuário faz POST /auth/login com email e senha
   ↓
2. AuthService valida credenciais
   ↓
3. Se válido, gera token JWT com JwtTokenProvider
   ↓
4. Token é retornado ao frontend
   ↓
5. Frontend armazena token em localStorage
   ↓
6. Todas as requisições incluem token no header Authorization
   ↓
7. JwtAuthenticationFilter valida token em cada requisição
```

#### 4.3.2 Implementação JWT

```java
public String generateToken(String username) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

    return Jwts.builder()
            .subject(username)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
            .compact();
}
```

**Segurança:**
- Algoritmo: HS256 (HMAC com SHA-256)
- Expiração: 24 horas (configurável)
- Chave secreta: Mínimo 256 bits

#### 4.3.3 Controle de Acesso

```java
@PreAuthorize("isAuthenticated()")
@GetMapping("/alunos")
public ResponseEntity<List<AlunoDTO>> listarTodos() {
    // Apenas usuários autenticados
}
```

### 4.4 Validações

**Validações de Entrada:**

```java
@NotBlank(message = "Nome é obrigatório")
@Size(min = 3, max = 150, message = "Nome deve ter entre 3 e 150 caracteres")
@Pattern(regexp = "^[a-zA-ZáéíóúàâêôãõçÁÉÍÓÚÀÂÊÔÃÕÇ\\s]+$", 
         message = "Nome deve conter apenas letras e espaços")
private String nome;

@Email(message = "Email deve ser válido")
private String email;

@Pattern(regexp = "^\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}$|^\\d{11}$", 
         message = "CPF deve estar no formato XXX.XXX.XXX-XX ou 11 dígitos")
private String cpf;
```

**Validações de Negócio:**

- Verificar se aluno já existe (CPF único)
- Verificar se turma tem vagas disponíveis
- Verificar se aluno já possui matrícula ativa
- Verificar integridade referencial

### 4.5 Integração com Redis

```java
@Service
@RequiredArgsConstructor
public class MatriculaService {
    private final RedisTemplate<String, Object> redisTemplate;
    
    private void adicionarFilaMatricula(Matricula matricula) {
        String mensagem = String.format("Matrícula ID: %d, Aluno: %s, Turma: %s",
                matricula.getId(),
                matricula.getAluno().getNome(),
                matricula.getTurma().getNome());
        
        redisTemplate.opsForList().rightPush("fila:matriculas", mensagem);
    }
}
```

**Operações:**
- RPUSH: Adicionar à fila
- LPOP: Remover da fila
- LLEN: Obter tamanho da fila
- LRANGE: Listar elementos

---

## 5. IMPLEMENTAÇÃO DO FRONTEND

### 5.1 Estrutura HTML

O frontend é estruturado em uma única página (SPA - Single Page Application) com múltiplas "páginas" que são mostradas/escondidas dinamicamente.

**Páginas:**
1. Login Page
2. Dashboard Page
3. Alunos Page
4. Turmas Page
5. Matrículas Page

### 5.2 Estilo e Design

**Paleta de Cores:**
- Primária: #667eea (Roxo)
- Secundária: #764ba2 (Roxo escuro)
- Sucesso: #48bb78 (Verde)
- Erro: #f56565 (Vermelho)

**Tipografia:**
- Font: Segoe UI, Tahoma, Geneva, Verdana, sans-serif
- Tamanhos: 12px-32px conforme hierarquia

**Responsividade:**
- Desktop: Layout completo
- Tablet: Layout adaptado
- Mobile: Layout colapsado

### 5.3 Comunicação com Backend

**Fetch API:**

```javascript
async function apiRequest(endpoint, method = 'GET', body = null) {
    const options = {
        method: method,
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${authToken}`
        }
    };
    
    if (body) {
        options.body = JSON.stringify(body);
    }
    
    const response = await fetch(`${API_BASE_URL}${endpoint}`, options);
    return await response.json();
}
```

**Tratamento de Erros:**
- 401 Unauthorized: Redirecionar para login
- 400 Bad Request: Mostrar mensagem de erro
- 500 Internal Server Error: Mostrar mensagem genérica

---

## 6. CONTROLE DE ACESSO

### 6.1 Tabela de Permissões

| Grupo | Alunos | Turmas | Matrículas | Usuários |
|-------|--------|--------|-----------|----------|
| ADMINISTRADOR | CRUD | CRUD | CRUD | CRUD |
| PROFESSOR | READ | READ | READ | - |
| SECRETARIA | CRUD | READ | CRUD | - |
| RESPONSAVEL | READ | - | READ | - |

### 6.2 Implementação

**No Backend:**

```java
@PreAuthorize("hasRole('ADMIN') or hasRole('SECRETARIA')")
@PostMapping
public ResponseEntity<?> criar(@Valid @RequestBody AlunoDTO alunoDTO) {
    // Apenas ADMIN ou SECRETARIA podem criar alunos
}
```

**No Frontend:**

```javascript
// Verificar permissões antes de mostrar botões
if (userGroup === 'SECRETARIA' || userGroup === 'ADMINISTRADOR') {
    showButton('Novo Aluno');
}
```

---

## 7. TESTES E VALIDAÇÃO

### 7.1 Dados de Teste

**Usuários Padrão:**

| Email | Senha | Grupo |
|-------|-------|-------|
| admin@matricula.com | admin123456 | ADMINISTRADOR |
| joao@matricula.com | prof123456 | PROFESSOR |
| maria@matricula.com | sec123456 | SECRETARIA |

**Alunos de Teste:**

| Nome | CPF | Turma | Turno |
|------|-----|-------|-------|
| João Silva | 11111111111 | 6º A | MATUTINO |
| Ana Santos | 22222222222 | 6º A | MATUTINO |
| Pedro Oliveira | 33333333333 | 6º B | VESPERTINO |

### 7.2 Cenários de Teste

1. **Login:** Testar com credenciais válidas e inválidas
2. **CRUD de Alunos:** Criar, ler, atualizar, deletar
3. **CRUD de Turmas:** Criar, ler, atualizar, deletar
4. **Matrículas:** Registrar, cancelar, validar capacidade
5. **Fila Redis:** Verificar se requisições são adicionadas à fila
6. **Segurança:** Testar acesso sem autenticação

---

## 8. INSTRUÇÕES DE INSTALAÇÃO E USO

### 8.1 Requisitos

- Java 17+
- Maven 3.8+
- MySQL 8.0+
- Redis 6.0+
- Navegador moderno (Chrome, Firefox, Safari, Edge)

### 8.2 Instalação do Backend

```bash
# 1. Clonar repositório
git clone <seu-repositorio>
cd matricula-escolar-backend

# 2. Criar banco de dados
mysql -u root -p < scripts/01-schema.sql
mysql -u root -p < scripts/02-create-user.sql

# 3. Compilar
mvn clean compile

# 4. Executar
mvn spring-boot:run
```

### 8.3 Instalação do Frontend

```bash
# 1. Navegar para pasta do frontend
cd matricula-escolar-frontend

# 2. Servir com Python
python -m http.server 3000

# 3. Acessar em http://localhost:3000
```

---

## 9. CONCLUSÃO

O **Sistema de Gerenciamento de Matrículas Escolares** demonstra a aplicação prática de conceitos avançados de banco de dados, incluindo:

- ✅ Modelagem relacional com normalização
- ✅ Índices para otimização de performance
- ✅ Triggers para automação e auditoria
- ✅ Views para simplificação de consultas
- ✅ Procedures e Functions para lógica de negócio
- ✅ Controle de acesso granular
- ✅ Integração com NoSQL (Redis)
- ✅ Autenticação e segurança com JWT
- ✅ API REST com boas práticas
- ✅ Interface responsiva e intuitiva

O sistema é **funcional, profissional e pronto para produção**, atendendo a todos os requisitos técnicos do trabalho final.

---

## 10. REFERÊNCIAS

1. MySQL 8.0 Documentation: https://dev.mysql.com/doc/
2. Spring Boot Documentation: https://spring.io/projects/spring-boot
3. Redis Documentation: https://redis.io/documentation
4. JWT Introduction: https://jwt.io/introduction
5. REST API Best Practices: https://restfulapi.net/

---

**Autor:** Manus AI  
**Data:** Novembro de 2025  
**Versão:** 1.0.0
