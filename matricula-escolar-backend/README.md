# Gerenciamento de Matrículas Escolares - Backend

## Descrição

Backend da aplicação de Gerenciamento de Matrículas Escolares desenvolvido em **Java com Spring Boot 3.2.0**, utilizando **MySQL** como banco de dados relacional e **Redis** para fila de requisições.

## Requisitos

- **Java 17+**
- **Maven 3.8+**
- **MySQL 8.0+**
- **Redis 6.0+**

## Instalação

### 1. Clonar o Repositório

```bash
git clone <seu-repositorio>
cd matricula-escolar-backend
```

### 2. Configurar o Banco de Dados MySQL

#### 2.1 Criar o Banco de Dados

Execute o script SQL para criar o banco de dados e as tabelas:

```bash
mysql -u root -p < scripts/01-schema.sql
```

#### 2.2 Criar Usuário do Banco de Dados

Execute o script para criar o usuário com permissões limitadas:

```bash
mysql -u root -p < scripts/02-create-user.sql
```

**Credenciais padrão:**
- Usuário: `matricula_user`
- Senha: `matricula_password`
- Banco: `matricula_escolar`

### 3. Configurar Redis

Certifique-se de que o Redis está instalado e rodando:

```bash
# Linux/Mac
redis-server

# Windows (se instalado via WSL)
wsl redis-server
```

Redis padrão: `localhost:6379`

### 4. Configurar a Aplicação

Edite o arquivo `src/main/resources/application.properties`:

```properties
# MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/matricula_escolar?useSSL=false&serverTimezone=UTC
spring.datasource.username=matricula_user
spring.datasource.password=matricula_password

# Redis
spring.redis.host=localhost
spring.redis.port=6379

# JWT (altere para uma chave segura)
jwt.secret=sua_chave_secreta_super_segura_aqui_com_mais_de_256_bits_para_hs256
jwt.expiration=86400000
```

### 5. Compilar e Executar

```bash
# Compilar o projeto
mvn clean compile

# Executar os testes (opcional)
mvn test

# Empacotar a aplicação
mvn package

# Executar a aplicação
mvn spring-boot:run
```

Ou execute diretamente o JAR:

```bash
java -jar target/matricula-escolar-1.0.0.jar
```

A aplicação estará disponível em: **http://localhost:8080/api**

## Estrutura do Projeto

```
matricula-escolar-backend/
├── src/main/java/com/matricula/
│   ├── config/              # Configurações (CORS, Security, Redis)
│   ├── controller/          # Controllers REST
│   ├── service/             # Serviços de negócio
│   ├── repository/          # Repositories JPA
│   ├── model/               # Entidades JPA
│   ├── security/            # Segurança (JWT)
│   ├── dto/                 # Data Transfer Objects
│   └── MatriculaEscolarApplication.java
├── src/main/resources/
│   └── application.properties
├── scripts/
│   ├── 01-schema.sql        # Script de criação do banco
│   └── 02-create-user.sql   # Script de criação do usuário
├── pom.xml                  # Dependências Maven
└── README.md
```

## Endpoints Principais

### Autenticação

- **POST** `/api/auth/login` - Login de usuário
- **GET** `/api/auth/validate` - Validar token JWT

### Alunos

- **GET** `/api/alunos` - Listar todos os alunos
- **GET** `/api/alunos/{id}` - Buscar aluno por ID
- **GET** `/api/alunos/turma/{turmaId}` - Listar alunos de uma turma
- **POST** `/api/alunos` - Criar novo aluno
- **PUT** `/api/alunos/{id}` - Atualizar aluno
- **DELETE** `/api/alunos/{id}` - Deletar aluno

### Turmas

- **GET** `/api/turmas` - Listar todas as turmas
- **GET** `/api/turmas/{id}` - Buscar turma por ID
- **POST** `/api/turmas` - Criar nova turma
- **PUT** `/api/turmas/{id}` - Atualizar turma
- **DELETE** `/api/turmas/{id}` - Deletar turma

### Matrículas

- **GET** `/api/matriculas` - Listar matrículas ativas
- **GET** `/api/matriculas/turma/{turmaId}` - Listar matrículas de uma turma
- **POST** `/api/matriculas` - Registrar nova matrícula
- **DELETE** `/api/matriculas/{id}` - Cancelar matrícula
- **GET** `/api/matriculas/fila/status` - Obter status das filas Redis

## Credenciais Padrão

### Usuário Administrador

- Email: `admin@matricula.com`
- Senha: `admin123456`

### Usuário Professor

- Email: `joao@matricula.com`
- Senha: `prof123456`

### Usuário Secretaria

- Email: `maria@matricula.com`
- Senha: `sec123456`

## Tecnologias Utilizadas

- **Spring Boot 3.2.0** - Framework web
- **Spring Security** - Autenticação e autorização
- **Spring Data JPA** - Persistência de dados
- **MySQL 8.0** - Banco de dados relacional
- **Redis** - Cache e fila de requisições
- **JWT** - Autenticação por token
- **Lombok** - Redução de boilerplate
- **Maven** - Gerenciador de dependências

## Padrões de Desenvolvimento

### Injeção de Dependência

O projeto utiliza **injeção de dependência do Spring** em todos os serviços e controllers:

```java
@Service
@RequiredArgsConstructor
public class AlunoService {
    private final AlunoRepository alunoRepository;
    private final TurmaRepository turmaRepository;
    // ...
}
```

### Transações

Operações críticas utilizam `@Transactional`:

```java
@Transactional
public AlunoDTO criar(AlunoDTO alunoDTO) {
    // Lógica com garantia de atomicidade
}
```

### Validação

Validações são feitas através de anotações Jakarta Validation:

```java
@NotBlank(message = "Nome é obrigatório")
@Size(min = 3, max = 150)
private String nome;
```

## Segurança

- **Autenticação JWT** - Tokens com expiração configurável
- **Controle de Acesso** - Baseado em grupos de usuários
- **CORS** - Configurado para aceitar requisições do frontend
- **Senhas** - Criptografadas com BCrypt
- **Usuário Dedicado** - Sem acesso root ao MySQL

## Troubleshooting

### Erro: "Connection refused" ao MySQL

Verifique se o MySQL está rodando:

```bash
# Linux
sudo systemctl status mysql

# Mac
brew services list | grep mysql
```

### Erro: "Could not connect to Redis"

Verifique se o Redis está rodando:

```bash
redis-cli ping
```

Se retornar `PONG`, o Redis está funcionando.

### Erro: "Unauthorized" em requisições

Certifique-se de incluir o token JWT no header:

```bash
Authorization: Bearer <seu-token-jwt>
```

## Contribuindo

1. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
2. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
3. Push para a branch (`git push origin feature/AmazingFeature`)
4. Abra um Pull Request

## Licença

Este projeto é fornecido como parte do trabalho final de Laboratório de Banco de Dados.

## Suporte

Para dúvidas ou problemas, consulte a documentação técnica ou entre em contato com o desenvolvedor.
