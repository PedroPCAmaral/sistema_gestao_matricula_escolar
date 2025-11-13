# 📚 Guia Completo: Como Colocar o Projeto no GitHub Pages

Este guia passo a passo vai te ensinar como publicar seu Sistema de Matrícula Escolar no GitHub Pages.

## 🎯 Objetivo Final

Seu site funcionando em: `https://seu-usuario.github.io/sistema-matricula-frontend`

---

## ✅ PASSO 1: Criar Conta no GitHub (Se não tiver)

1. Acesse [github.com](https://github.com)
2. Clique em **"Sign up"** (Inscrever-se)
3. Preencha:
   - Email
   - Senha
   - Nome de usuário (ex: `seu-usuario`)
4. Complete a verificação
5. Pronto! Sua conta está criada

---

## ✅ PASSO 2: Criar Novo Repositório

1. Faça login no GitHub
2. Clique no ícone **"+"** no canto superior direito
3. Selecione **"New repository"**

![Criar repositório](https://docs.github.com/assets/cb-25528/images/help/repository/repo-create.png)

4. Preencha os campos:
   - **Repository name:** `sistema-matricula-frontend`
   - **Description:** `Sistema de Matrícula Escolar`
   - **Visibility:** Selecione **Public** (para que fique acessível)

5. Clique em **"Create repository"**

---

## ✅ PASSO 3: Instalar Git

### Windows:
1. Acesse [git-scm.com](https://git-scm.com/download/win)
2. Baixe o instalador
3. Execute e siga as instruções padrão
4. Reinicie o computador

### Mac:
```bash
brew install git
```

### Linux (Ubuntu/Debian):
```bash
sudo apt-get update
sudo apt-get install git
```

### Verificar se Git está instalado:
Abra o terminal/prompt e execute:
```bash
git --version
```

Deve aparecer algo como: `git version 2.40.0`

---

## ✅ PASSO 4: Configurar Git

Abra o terminal/prompt de comando e execute:

```bash
git config --global user.name "Seu Nome Aqui"
git config --global user.email "seu-email@example.com"
```

**Exemplo:**
```bash
git config --global user.name "João Silva"
git config --global user.email "joao@example.com"
```

---

## ✅ PASSO 5: Preparar Pasta do Projeto

1. Crie uma pasta para o projeto:
   - **Windows:** `C:\Users\SeuUsuario\Documents\sistema-matricula-frontend`
   - **Mac/Linux:** `~/sistema-matricula-frontend`

2. Coloque os 4 arquivos nesta pasta:
   - `index.html`
   - `styles.css`
   - `script.js`
   - `README.md`

---

## ✅ PASSO 6: Abrir Terminal na Pasta do Projeto

### Windows:
1. Abra a pasta do projeto no Explorador
2. Clique na barra de endereço
3. Digite `cmd` e pressione Enter
4. Pronto! Terminal aberto na pasta

### Mac:
1. Abra o Terminal (Cmd + Espaço, digite "Terminal")
2. Digite: `cd ~/sistema-matricula-frontend`
3. Pressione Enter

### Linux:
1. Abra o Terminal
2. Digite: `cd ~/sistema-matricula-frontend`
3. Pressione Enter

---

## ✅ PASSO 7: Inicializar Repositório Git

No terminal, execute:

```bash
git init
```

Você verá: `Initialized empty Git repository in ...`

---

## ✅ PASSO 8: Adicionar Arquivos

No terminal, execute:

```bash
git add .
```

Este comando adiciona todos os arquivos da pasta.

---

## ✅ PASSO 9: Fazer Primeiro Commit

No terminal, execute:

```bash
git commit -m "Primeira versão do Sistema de Matrícula Escolar"
```

Você verá informações sobre os arquivos adicionados.

---

## ✅ PASSO 10: Conectar ao Repositório GitHub

No terminal, execute (substitua `seu-usuario` pelo seu nome de usuário GitHub):

```bash
git remote add origin https://github.com/seu-usuario/sistema-matricula-frontend.git
```

**Exemplo:**
```bash
git remote add origin https://github.com/joaosilva/sistema-matricula-frontend.git
```

---

## ✅ PASSO 11: Fazer Push para GitHub

No terminal, execute:

```bash
git push -u origin main
```

Se pedir autenticação:
1. Clique em "Sign in with your browser"
2. Autorize o acesso
3. Volte para o terminal

---

## ✅ PASSO 12: Ativar GitHub Pages

1. Acesse seu repositório no GitHub
2. Clique em **"Settings"** (Configurações)
3. No menu lateral esquerdo, clique em **"Pages"**
4. Em **"Source"**, selecione:
   - **Branch:** `main`
   - **Folder:** `/ (root)`
5. Clique em **"Save"**

Aguarde 1-2 minutos...

---

## ✅ PASSO 13: Acessar Seu Site

Seu site estará disponível em:

```
https://seu-usuario.github.io/sistema-matricula-frontend
```

**Exemplo:**
```
https://joaosilva.github.io/sistema-matricula-frontend
```

---

## 🔄 Como Fazer Atualizações

Depois que o projeto está no GitHub, para fazer atualizações:

1. Edite os arquivos (`index.html`, `styles.css`, `script.js`)
2. Abra o terminal na pasta do projeto
3. Execute:

```bash
git add .
git commit -m "Descrição das mudanças"
git push
```

**Exemplo:**
```bash
git add .
git commit -m "Adicionada validação de CPF"
git push
```

O site será atualizado automaticamente em poucos minutos!

---

## 🆘 Problemas Comuns

### ❌ "fatal: not a git repository"

**Solução:**
```bash
git init
```

### ❌ "fatal: The current branch main does not have any upstream"

**Solução:**
```bash
git push -u origin main
```

### ❌ Mudanças não aparecem no site

**Solução:** Aguarde 1-2 minutos e limpe o cache do navegador (Ctrl+Shift+Delete)

### ❌ "Permission denied (publickey)"

**Solução:** Configure SSH ou use HTTPS. Tente novamente com:
```bash
git remote set-url origin https://github.com/seu-usuario/sistema-matricula-frontend.git
```

### ❌ Não consigo fazer login

**Solução:** Use um Personal Access Token:
1. Vá para GitHub Settings → Developer settings → Personal access tokens
2. Crie um novo token com permissão `repo`
3. Use o token como senha ao fazer push

---

## 📝 Checklist Final

- [ ] Conta no GitHub criada
- [ ] Repositório criado
- [ ] Git instalado
- [ ] Git configurado com nome e email
- [ ] Pasta do projeto criada
- [ ] Arquivos colocados na pasta
- [ ] `git init` executado
- [ ] `git add .` executado
- [ ] `git commit` executado
- [ ] `git remote add origin` executado
- [ ] `git push -u origin main` executado
- [ ] GitHub Pages ativado
- [ ] Site acessível em `https://seu-usuario.github.io/sistema-matricula-frontend`

---

## 🎉 Parabéns!

Seu Sistema de Matrícula Escolar está online e acessível para o mundo!

---

## 📚 Recursos Adicionais

- [Git Documentation](https://git-scm.com/doc)
- [GitHub Pages Documentation](https://docs.github.com/en/pages)
- [GitHub Hello World](https://guides.github.com/activities/hello-world/)

---

**Dúvidas? Consulte a documentação oficial do GitHub ou procure por tutoriais no YouTube!**
