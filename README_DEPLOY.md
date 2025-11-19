# Backend - gestao_matricula (Node.js)

Arquivos incluídos:
- index.js
- package.json
- .env.example
- sql/ (scripts para criar tabelas, views, triggers, procedures e usuário)

## Passos rápidos (pelo terminal VS Code)

### 1) Descompacte o ZIP e entre na pasta
```
cd sistema_matricula_backend
```

### 2) Instale dependências
```
npm install
```

### 3) Configure variáveis de ambiente
- Crie um arquivo `.env` baseado no `.env.example`
- Se for usar PlanetScale, cole as credenciais e defina DB_SSL=1

Exemplo `.env`:
```
DB_HOST=us-east.connect.psdb.cloud
DB_USER=your_user
DB_PASS=your_password
DB_NAME=gestao_matricula
DB_SSL=1
REDIS_HOST=127.0.0.1
REDIS_PORT=6379
PORT=3000
```

### 4) Rodar local
```
npm start
```

### 5) Testar
- Health: http://localhost:3000/health
- List users: http://localhost:3000/usuarios

### 6) Subir scripts SQL no banco (use MySQL Workbench / TablePlus / DBeaver)
- sql/01_tables.sql
- sql/02_views_triggers_procs.sql
- sql/03_create_user.sql

### 7) Subir para o GitHub (branch backend-producao)
```
git init
git checkout -b backend-producao
git add .
git commit -m "Backend funcional com PlanetScale"
git remote add origin https://github.com/PedroPCAmaral/sistema_gestao_matricula_escolar.git
git push -u origin backend-producao
```

### 8) Deploy no Render (ou Railway)
- Crie conta no Render.com
- New -> Web Service -> conecte ao GitHub e escolha este repo/branch
- Build command: npm install
- Start command: npm start
- Adicione variáveis de ambiente no painel (DB_HOST, DB_USER, DB_PASS, DB_NAME, DB_SSL, REDIS_HOST, REDIS_PORT)
- Deploy

### 9) Atualizar frontend (GitHub Pages)
- No seu JS do front, defina:
```
const API_URL = "https://SEU_BACKEND_RENDER.onrender.com";
```

### 10) Gerar executável (opcional)
```
npm install -g pkg
pkg . --targets node18-win-x64 --output sistemaMatricula.exe
```

## Notas sobre PlanetScale
- PlanetScale exige, em muitos casos, conexões TLS. Use DB_SSL=1 e certifique-se de que suas credenciais e host estão corretos.
- Se PlanetScale exigir o uso do "Serverless" connection string, consulte a docs PlanetScale para configurar o driver `mysql2`.

## Segurança (leia antes de ir a produção)
- Nunca comite `.env` com credenciais.
- Use hashing (bcrypt) para senhas em produção.
- Ative regras de CORS e autenticação apropriadas.
