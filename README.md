# Sistema de Matrícula Escolar - Frontend e Backend

Projeto de Cadastro de Alunos (Matrícula Escolar)

Este é um projeto simples de CRUD (Criar, Ler, Atualizar, Deletar) para um sistema de matrícula escolar, desenvolvido em Java e utilizando um banco de dados MySQL para persistência dos dados.

1-Funcionalidades (CRUD): Cadastrar Aluno (CREATE): Adiciona um novo registro de aluno ao banco de dados. Listar Alunos (READ): Consulta e exibe todos os alunos registrados. Buscar Aluno por CPF (READ): Busca um aluno específico para consulta. Editar Aluno (UPDATE): Modifica os dados de um aluno existente (ex: turno, telefone). Remover Aluno (DELETE): Remove permanentemente um aluno do banco de dados.

2-Estrutura do Projeto (Pacotes): Classe Aluno (Pacote modelo): Serve como o molde para criar objetos que representam um aluno. Atributos: id (PK), nome, cpf , idade, serie, turno, telefone. Métodos: Possui getters e setters para manipular os atributos.

Classe AlunoServico (Pacote controle): É o "cérebro" da aplicação.]Implementa toda a lógica CRUD. Responsabilidade: Recebe os dados da interface e interage com o banco de dados (via Conexao) para executar comandos SQL.

Classe Conexao (Pacote controle): Classe utilitária responsável por estabelecer e retornar a conexão com o banco de dados MySQL.

Classe Main (Pacote visao): É o ponto de entrada do programa e a interface de terminal (CLI). Responsabilidade: Exibe o menu, lê a entrada do usuário, e chama os métodos do AlunoServico

Um sistema web completo de cadastro de alunos desenvolvido em **HTML, CSS e JavaScript puro**, sem dependências externas.

## 🚀 Características

- ✅ Cadastro, edição e exclusão de alunos
- ✅ Busca em tempo real por nome ou CPF
- ✅ Armazenamento de dados em LocalStorage (sem servidor necessário)
- ✅ Interface responsiva e moderna
- ✅ Funciona offline
- ✅ Sem dependências externas

## 📂 Arquivos

- `index.html` - Estrutura HTML da aplicação
- `styles.css` - Estilização completa com design responsivo
- `script.js` - Lógica da aplicação em JavaScript puro
- `README.md` - Este arquivo

## 🎯 Como Usar Localmente

1. Clone o repositório:
```bash
git clone https://github.com/seu-usuario/sistema-matricula-frontend.git
cd sistema-matricula-frontend
```

2. Abra o arquivo `index.html` no navegador:
```bash
# No Linux/Mac
open index.html

# No Windows
start index.html

# Ou simplesmente arraste o arquivo para o navegador
```

## 🌐 Publicar no GitHub Pages

### Passo 1: Criar Repositório no GitHub

1. Acesse [github.com](https://github.com) e faça login
2. Clique em **"+"** no canto superior direito
3. Selecione **"New repository"**
4. Preencha:
   - **Repository name:** `sistema-matricula-frontend`
   - **Description:** Sistema de Matrícula Escolar
   - Deixe como **Public**
5. Clique em **"Create repository"**

### Passo 2: Instalar Git (se não tiver)

**Windows:**
- Baixe em: https://git-scm.com/download/win
- Execute o instalador

**Mac:**
```bash
brew install git
```

**Linux (Ubuntu/Debian):**
```bash
sudo apt-get install git
```

### Passo 3: Configurar Git

Abra o terminal/prompt de comando e execute:

```bash
git config --global user.name "Seu Nome"
git config --global user.email "seu-email@example.com"
```

### Passo 4: Fazer Upload para GitHub

1. Abra o terminal na pasta do projeto
2. Execute os comandos:

```bash
# Inicializar repositório
git init

# Adicionar todos os arquivos
git add .

# Fazer commit
git commit -m "Primeira versão do Sistema de Matrícula Escolar"

# Adicionar repositório remoto (substitua seu-usuario)
git remote add origin https://github.com/seu-usuario/sistema-matricula-frontend.git

# Fazer push para GitHub
git push -u origin main
```

### Passo 5: Ativar GitHub Pages

1. Acesse seu repositório no GitHub
2. Vá para **Settings** (Configurações)
3. No menu lateral, clique em **"Pages"**
4. Em **"Source"**, selecione:
   - Branch: `main`
   - Folder: `/ (root)`
5. Clique em **"Save"**

### Passo 6: Acessar o Site

Após alguns minutos, seu site estará disponível em:
```
https://seu-usuario.github.io/sistema-matricula-frontend
```

## 📝 Exemplo Passo a Passo (Resumido)

```bash
# 1. Abra o terminal na pasta do projeto
cd caminho/para/sistema-matricula-frontend

# 2. Configure Git (primeira vez apenas)
git config --global user.name "Seu Nome"
git config --global user.email "seu-email@example.com"

# 3. Inicialize o repositório
git init

# 4. Adicione os arquivos
git add .

# 5. Faça o primeiro commit
git commit -m "Primeira versão"

# 6. Adicione o repositório remoto
git remote add origin https://github.com/seu-usuario/sistema-matricula-frontend.git

# 7. Faça push
git push -u origin main

# 8. Ative GitHub Pages nas configurações do repositório
# Seu site estará em: https://seu-usuario.github.io/sistema-matricula-frontend
```

## 🔄 Fazer Atualizações

Após fazer mudanças no código:

```bash
# Adicionar mudanças
git add .

# Fazer commit
git commit -m "Descrição das mudanças"

# Fazer push
git push
```

## 💾 Dados

Os dados são armazenados no **LocalStorage** do navegador. Isso significa:
- ✅ Funciona sem servidor
- ✅ Funciona offline
- ⚠️ Dados são perdidos se o cache do navegador for limpo
- ⚠️ Dados são específicos de cada navegador/dispositivo

## 🎨 Customização

### Alterar Cores

Abra `styles.css` e procure por `:root`:

```css
:root {
    --primary-color: #2563eb;  /* Azul principal */
    --danger-color: #ef4444;   /* Vermelho para deletar */
    --success-color: #10b981;  /* Verde para sucesso */
    /* ... outras cores ... */
}
```

### Alterar Título

Abra `index.html` e procure por:

```html
<title>Sistema de Matrícula Escolar</title>
```

## 🐛 Troubleshooting

### Problema: "fatal: not a git repository"
**Solução:** Execute `git init` na pasta do projeto

### Problema: "fatal: The current branch main does not have any upstream"
**Solução:** Execute `git push -u origin main`

### Problema: Mudanças não aparecem no GitHub Pages
**Solução:** Aguarde 1-2 minutos para o GitHub atualizar o site

### Problema: Dados desaparecem
**Solução:** Os dados são armazenados localmente. Limpar cache do navegador remove os dados.

## 📚 Recursos Úteis

- [Git Documentation](https://git-scm.com/doc)
- [GitHub Pages Guide](https://pages.github.com/)
- [MDN Web Docs](https://developer.mozilla.org/)

## 📄 Licença

Projeto acadêmico - Sistema de Matrícula Escolar

---

**Desenvolvido com ❤️ em HTML, CSS e JavaScript puro**
