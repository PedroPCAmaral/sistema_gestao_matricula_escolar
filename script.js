// script.js - lógica principal (autenticação simulada + CRUD localStorage)
// Keys, sample credentials and constants
const STORAGE_KEY = 'matricula_escolar_alunos';
const AUTH_KEY = 'matricula_escolar_auth';
const CREDENCIAIS_TESTE = { email: 'teste@escola.com', senha: '123456' };

const SERIES = ["1º Ano", "2º Ano", "3º Ano", "4º Ano", "5º Ano", "6º Ano", "7º Ano", "8º Ano", "9º Ano", "1ª Série EM", "2ª Série EM", "3ª Série EM"];
const TURNOS = ["Manhã", "Tarde", "Noite"];

function gerarCPFSimulado() {
    const n = () => Math.floor(Math.random() * 10);
    const cpf = Array(9).fill(0).map(n).join('');
    return cpf.replace(/(\d{3})(\d{3})(\d{3})/, '$1.$2.$3-XX');
}

function gerarDadosSimulados(count = 30) {
    const alunos = [];
    const nomes = ["Ana Silva", "Bruno Costa", "Carla Santos", "Daniel Oliveira", "Eduarda Pereira", "Felipe Almeida", "Giovana Rodrigues", "Henrique Lima", "Isabela Fernandes", "João Souza", "Kátia Martins", "Lucas Rocha", "Mariana Gomes", "Nuno Barbosa", "Olívia Ribeiro", "Pedro Castro", "Quitéria Dias", "Rafael Nunes", "Sofia Melo", "Thiago Freitas", "Ursula Pires", "Vitor Dantas", "Wanessa Correia", "Xavier Mendes", "Yara Vasconcelos", "Zeca Camargo", "Alice Vieira", "Bento Ferreira", "Cecília Goncalves", "Davi Nogueira"];

    for (let i = 0; i < count; i++) {
        const nome = nomes[i % nomes.length] + (i >= nomes.length ? ` (${i + 1})` : '');
        const serie = SERIES[Math.floor(Math.random() * SERIES.length)];
        const turno = TURNOS[Math.floor(Math.random() * TURNOS.length)];
        const cpf = gerarCPFSimulado();

        alunos.push({
            id: Date.now() + i,
            nome: nome,
            cpf: cpf,
            dataNascimento: `200${Math.floor(Math.random() * 10)}-01-01`,
            serie: serie,
            turno: turno,
            responsavel: `Responsável de ${nome.split(' ')[0]}`,
            telefone: `(99) 9${Math.floor(Math.random() * 9000) + 1000}-${Math.floor(Math.random() * 9000) + 1000}`,
            dataMatricula: new Date().toISOString().split('T')[0],
        });
    }
    return alunos;
}

function carregarAlunos() {
    const dados = localStorage.getItem(STORAGE_KEY);
    if (dados) {
        try { return JSON.parse(dados); } catch(e){ localStorage.removeItem(STORAGE_KEY); }
    }
    const alunosIniciais = gerarDadosSimulados(12);
    salvarAlunos(alunosIniciais);
    return alunosIniciais;
}

function salvarAlunos(alunos) {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(alunos));
}

function salvarAuthState(logado) {
    sessionStorage.setItem(AUTH_KEY, JSON.stringify({ logado }));
}

function estaLogado() {
    const auth = sessionStorage.getItem(AUTH_KEY);
    return auth ? JSON.parse(auth).logado : false;
}

function autenticar(email, senha) {
    return email === CREDENCIAIS_TESTE.email && senha === CREDENCIAIS_TESTE.senha;
}

/* DOM helpers */
const $ = selector => document.querySelector(selector);
const $$ = selector => Array.from(document.querySelectorAll(selector));

/* Elements */
const loginPage = $('#login-page');
const homePage = $('#home-page');
const loginForm = document.getElementById('login-form');
const loginButton = document.getElementById('login-button');
const loginErrorAlert = document.getElementById('login-error-alert');
const loginErrorMessage = document.getElementById('login-error-message');

function navegar(page) {
    if (page === 'home') {
        loginPage.classList.add('hidden');
        homePage.classList.remove('hidden');
        renderizarHomePage();
    } else {
        loginPage.classList.remove('hidden');
        homePage.classList.add('hidden');
    }
}

async function handleLogin(e) {
    e.preventDefault();
    loginErrorAlert.classList.add('hidden');
    loginButton.disabled = true;
    loginButton.textContent = 'Entrando...';
    loginButton.classList.add('loading');

    await new Promise(resolve => setTimeout(resolve, 400));

    const email = document.getElementById('email').value;
    const senha = document.getElementById('senha').value;

    if (autenticar(email, senha)) {
        salvarAuthState(true);
        navegar('home');
    } else {
        loginErrorMessage.textContent = 'Email ou senha inválidos. Tente novamente.';
        loginErrorAlert.classList.remove('hidden');
        salvarAuthState(false);
    }

    loginButton.disabled = false;
    loginButton.textContent = 'Entrar';
    loginButton.classList.remove('loading');
}

function handleLogout() {
    salvarAuthState(false);
    sessionStorage.removeItem(AUTH_KEY);
    navegar('login');
}

/* Home HTML generator */
function gerarHomeHTML() {
    return `
        <div class="sidebar">
            <div>
                <div class="sidebar-header">Matrícula Escolar</div>
                <nav class="sidebar-nav">
                    <a href="#" class="active">
                        <span style="margin-right:.5rem">👥</span> Alunos
                    </a>
                </nav>
            </div>
            <div class="sidebar-footer">
                <button id="logout-button" class="button secondary">Sair</button>
            </div>
        </div>
        <div class="main-content">
            <div class="header-bar">
                <h2 class="text-2xl font-bold">Gerenciamento de Alunos</h2>
                <button id="abrir-modal-matricula" class="button primary">+ Matricular Aluno</button>
            </div>

            <div class="filter-bar">
                <input type="text" id="busca-aluno" class="input" placeholder="Buscar por nome ou CPF..." />
                <div class="select-wrapper">
                    <select id="filtro-serie" class="input">
                        <option value="todas">Todas as Séries</option>
                        ${SERIES.map(s => `<option value="${s}">${s}</option>`).join('')}
                    </select>
                </div>
                <div class="select-wrapper">
                    <select id="filtro-turno" class="input">
                        <option value="todos">Todos os Turnos</option>
                        ${TURNOS.map(t => `<option value="${t}">${t}</option>`).join('')}
                    </select>
                </div>
            </div>

            <div id="aluno-list" class="aluno-list"></div>
        </div>

        <!-- Modal -->
        <div id="modal-aluno" class="modal hidden" aria-hidden="true">
            <div class="modal-content">
                <div class="modal-header">
                    <h2 id="modal-title">Matricular Novo Aluno</h2>
                    <button id="fechar-modal" class="modal-close">&times;</button>
                </div>
                <form id="form-aluno" class="form-modal">
                    <input type="hidden" id="aluno-id" name="id" />
                    <div class="form-group">
                        <label for="nome">Nome Completo</label>
                        <input type="text" id="nome" name="nome" required class="input w-full" />
                    </div>
                    <div class="grid-cols-2">
                        <div class="form-group">
                            <label for="cpf">CPF</label>
                            <input type="text" id="cpf" name="cpf" required class="input w-full" placeholder="000.000.000-00" />
                        </div>
                        <div class="form-group">
                            <label for="dataNascimento">Data de Nascimento</label>
                            <input type="date" id="dataNascimento" name="dataNascimento" required class="input w-full" />
                        </div>
                    </div>
                    <div class="grid-cols-3">
                        <div class="form-group">
                            <label for="serie">Série</label>
                            <div class="select-wrapper">
                                <select id="serie" name="serie" required class="input w-full">
                                    <option value="">Selecione</option>
                                    ${SERIES.map(s => `<option value="${s}">${s}</option>`).join('')}
                                </select>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="turno">Turno</label>
                            <div class="select-wrapper">
                                <select id="turno" name="turno" required class="input w-full">
                                    <option value="">Selecione</option>
                                    ${TURNOS.map(t => `<option value="${t}">${t}</option>`).join('')}
                                </select>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="telefone">Telefone</label>
                            <input type="tel" id="telefone" name="telefone" required class="input w-full" placeholder="(99) 99999-9999" />
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="responsavel">Nome do Responsável</label>
                        <input type="text" id="responsavel" name="responsavel" required class="input w-full" />
                    </div>
                    <button type="submit" id="salvar-aluno-button" class="button primary w-full">Salvar Matrícula</button>
                </form>
            </div>
        </div>
    `;
}

function renderizarCardAluno(aluno) {
    return `
        <div class="aluno-card" data-id="${aluno.id}">
            <h3>${aluno.nome}</h3>
            <p><strong>CPF:</strong> ${aluno.cpf}</p>
            <p><strong>Nascimento:</strong> ${aluno.dataNascimento}</p>
            <p><strong>Responsável:</strong> ${aluno.responsavel}</p>
            <p><strong>Telefone:</strong> ${aluno.telefone}</p>
            <div class="flex gap-2 mt-2">
                <span class="tag serie">${aluno.serie}</span>
                <span class="tag turno">${aluno.turno}</span>
            </div>
            <div class="actions">
                <button class="button secondary edit-aluno" data-id="${aluno.id}">Editar</button>
                <button class="button destructive delete-aluno" data-id="${aluno.id}">Excluir</button>
            </div>
        </div>
    `;
}

function renderizarListaAlunos() {
    const alunos = carregarAlunos();
    const busca = document.getElementById('busca-aluno') ? document.getElementById('busca-aluno').value.toLowerCase() : '';
    const filtroSerie = document.getElementById('filtro-serie') ? document.getElementById('filtro-serie').value : 'todas';
    const filtroTurno = document.getElementById('filtro-turno') ? document.getElementById('filtro-turno').value : 'todos';

    const alunosFiltrados = alunos.filter(aluno => {
        const matchBusca = aluno.nome.toLowerCase().includes(busca) || aluno.cpf.includes(busca);
        const matchSerie = filtroSerie === 'todas' || aluno.serie === filtroSerie;
        const matchTurno = filtroTurno === 'todos' || aluno.turno === filtroTurno;
        return matchBusca && matchSerie && matchTurno;
    });

    const listaAlunosDiv = document.getElementById('aluno-list');
    if(!listaAlunosDiv) return;
    listaAlunosDiv.innerHTML = alunosFiltrados.map(renderizarCardAluno).join('');

    // attach listeners
    $$('.edit-aluno').forEach(btn => btn.addEventListener('click', (e) => abrirModalEdicao(e.currentTarget.dataset.id)));
    $$('.delete-aluno').forEach(btn => btn.addEventListener('click', (e) => confirmarExclusao(e.currentTarget.dataset.id)));
}

function renderizarHomePage() {
    homePage.innerHTML = gerarHomeHTML();

    document.getElementById('logout-button').addEventListener('click', handleLogout);
    document.getElementById('abrir-modal-matricula').addEventListener('click', abrirModalMatricula);
    document.getElementById('fechar-modal').addEventListener('click', fecharModal);
    document.getElementById('form-aluno').addEventListener('submit', handleSalvarAluno);
    const busca = document.getElementById('busca-aluno'); if(busca) busca.addEventListener('input', renderizarListaAlunos);
    const fs = document.getElementById('filtro-serie'); if(fs) fs.addEventListener('change', renderizarListaAlunos);
    const ft = document.getElementById('filtro-turno'); if(ft) ft.addEventListener('change', renderizarListaAlunos);

    renderizarListaAlunos();
}

/* Modal functions */
const modalAluno = {};

// use functions as in user's original code:
function abrirModalMatricula() {
    const modal = document.getElementById('modal-aluno');
    if(!modal) return;
    document.getElementById('modal-title').textContent = 'Matricular Novo Aluno';
    document.getElementById('form-aluno').reset();
    document.getElementById('aluno-id').value = '';
    modal.classList.remove('hidden');
}

function abrirModalEdicao(id) {
    const alunos = carregarAlunos();
    const aluno = alunos.find(a => a.id == id);
    if (aluno) {
        document.getElementById('modal-title').textContent = `Editar Aluno: ${aluno.nome}`;
        document.getElementById('aluno-id').value = aluno.id;
        document.getElementById('nome').value = aluno.nome;
        document.getElementById('cpf').value = aluno.cpf;
        document.getElementById('dataNascimento').value = aluno.dataNascimento;
        document.getElementById('serie').value = aluno.serie;
        document.getElementById('turno').value = aluno.turno;
        document.getElementById('responsavel').value = aluno.responsavel;
        document.getElementById('telefone').value = aluno.telefone;
        document.getElementById('modal-aluno').classList.remove('hidden');
    }
}

function fecharModal() {
    const modal = document.getElementById('modal-aluno');
    if(modal) modal.classList.add('hidden');
}

function handleSalvarAluno(e) {
    e.preventDefault();
    const id = document.getElementById('aluno-id').value;
    const nome = document.getElementById('nome').value;
    const cpf = document.getElementById('cpf').value;
    const dataNascimento = document.getElementById('dataNascimento').value;
    const serie = document.getElementById('serie').value;
    const turno = document.getElementById('turno').value;
    const responsavel = document.getElementById('responsavel').value;
    const telefone = document.getElementById('telefone').value;

    const novoAluno = {
        id: id ? parseInt(id) : Date.now(),
        nome,
        cpf,
        dataNascimento,
        serie,
        turno,
        responsavel,
        telefone,
        dataMatricula: id ? undefined : new Date().toISOString().split('T')[0],
    };

    let alunos = carregarAlunos();
    if (id) {
        alunos = alunos.map(a => a.id == id ? { ...a, ...novoAluno } : a);
    } else {
        alunos.push(novoAluno);
    }
    salvarAlunos(alunos);
    fecharModal();
    renderizarListaAlunos();
}

function confirmarExclusao(id) {
    const alunos = carregarAlunos();
    const aluno = alunos.find(a => a.id == id);
    if (aluno && confirm(`Tem certeza que deseja excluir a matrícula de ${aluno.nome}?`)) {
        const alunosAtualizados = alunos.filter(a => a.id != id);
        salvarAlunos(alunosAtualizados);
        renderizarListaAlunos();
    }
}

/* Initialization */
document.addEventListener('DOMContentLoaded', () => {
    if (loginForm) {
        loginForm.addEventListener('submit', handleLogin);
    }
    if (estaLogado()) {
        navegar('home');
    } else {
        navegar('login');
    }
});
