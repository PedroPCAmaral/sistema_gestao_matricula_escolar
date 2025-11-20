// ==================================================================================
// MÓDULO DE DADOS E PERSISTÊNCIA (Simulação de Backend com LocalStorage)
// ==================================================================================

const STORAGE_KEY = 'matricula_escolar_alunos';
const AUTH_KEY = 'matricula_escolar_auth';
const CREDENCIAIS_TESTE = { email: 'teste@escola.com', senha: '123456' };

const SERIES = ["1º Ano", "2º Ano", "3º Ano", "4º Ano", "5º Ano", "6º Ano", "7º Ano", "8º Ano", "9º Ano", "1ª Série EM", "2ª Série EM", "3ª Série EM"];
const TURNOS = ["Manhã", "Tarde", "Noite"];

/**
 * Gera um CPF aleatório válido (apenas para simulação).
 * @returns {string} CPF formatado.
 */
function gerarCPFSimulado() {
    const n = () => Math.floor(Math.random() * 10);
    const cpf = Array(9).fill(0).map(n).join('');
    return cpf.replace(/(\d{3})(\d{3})(\d{3})/, '$1.$2.$3-XX');
}

/**
 * Gera um conjunto de dados simulados de alunos.
 * @param {number} count Número de alunos a gerar.
 * @returns {Array<Object>} Lista de alunos.
 */
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

/**
 * Carrega a lista de alunos do LocalStorage ou inicializa com dados simulados.
 * @returns {Array<Object>} Lista de alunos.
 */
function carregarAlunos() {
    const dados = localStorage.getItem(STORAGE_KEY);
    if (dados) {
        return JSON.parse(dados);
    }
    const alunosIniciais = gerarDadosSimulados(35); // 35 alunos para garantir mais de 30
    salvarAlunos(alunosIniciais);
    return alunosIniciais;
}

/**
 * Salva a lista de alunos no LocalStorage.
 * @param {Array<Object>} alunos Lista de alunos a salvar.
 */
function salvarAlunos(alunos) {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(alunos));
}

/**
 * Salva o estado de autenticação (simplesmente um flag de login).
 * @param {boolean} logado
 */
function salvarAuthState(logado) {
    sessionStorage.setItem(AUTH_KEY, JSON.stringify({ logado }));
}

/**
 * Verifica se o usuário está logado.
 * @returns {boolean}
 */
function estaLogado() {
    const auth = sessionStorage.getItem(AUTH_KEY);
    return auth ? JSON.parse(auth).logado : false;
}

/**
 * Realiza a simulação de autenticação.
 * @param {string} email
 * @param {string} senha
 * @returns {boolean} Sucesso ou falha.
 */
function autenticar(email, senha) {
    return email === CREDENCIAIS_TESTE.email && senha === CREDENCIAIS_TESTE.senha;
}

// ==================================================================================
// MÓDULO DE RENDERIZAÇÃO E NAVEGAÇÃO (DOM Manipulation)
// ==================================================================================

const $ = selector => document.querySelector(selector);
const $$ = selector => document.querySelectorAll(selector);

const loginPage = $('#login-page');
const homePage = $('#home-page');
const loginForm = $('#login-form');
const loginButton = $('#login-button');
const loginErrorAlert = $('#login-error-alert');
const loginErrorMessage = $('#login-error-message');

/**
 * Alterna a visualização entre as páginas de Login e Home.
 * @param {string} page 'login' ou 'home'.
 */
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

/**
 * Manipulador de submissão do formulário de login.
 * @param {Event} e
 */
async function handleLogin(e) {
    e.preventDefault();
    loginErrorAlert.classList.add('hidden');
    loginButton.disabled = true;
    loginButton.innerHTML = '<span class="spinner"></span> Entrando...';
    loginButton.classList.add('loading');

    // Simular delay de rede
    await new Promise(resolve => setTimeout(resolve, 500));

    const email = $('#email').value;
    const senha = $('#senha').value;

    if (autenticar(email, senha)) {
        salvarAuthState(true);
        navegar('home');
    } else {
        loginErrorMessage.textContent = 'Email ou senha inválidos. Tente novamente.';
        loginErrorAlert.classList.remove('hidden');
        salvarAuthState(false);
    }

    loginButton.disabled = false;
    loginButton.innerHTML = 'Entrar';
    loginButton.classList.remove('loading');
}

/**
 * Manipulador de logout.
 */
function handleLogout() {
    salvarAuthState(false);
    sessionStorage.removeItem(AUTH_KEY);
    navegar('login');
}

// ==================================================================================
// MÓDULO HOME PAGE (Estrutura e CRUD)
// ==================================================================================

/**
 * Gera o HTML da página Home.
 * @returns {string} HTML da página Home.
 */
function gerarHomeHTML() {
    return `
        <div class="sidebar">
            <div>
                <div class="sidebar-header">Matrícula Escolar</div>
                <nav class="sidebar-nav">
                    <a href="#" class="active">
                        <svg class="icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M22 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
                        Alunos
                    </a>
                </nav>
            </div>
            <div class="sidebar-footer">
                <button id="logout-button" class="button secondary">
                    <svg class="icon icon-sm" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" x2="9" y1="12" y2="12"/></svg>
                    Sair
                </button>
            </div>
        </div>
        <div class="main-content">
            <div class="header-bar">
                <h2 class="text-2xl font-bold">Gerenciamento de Alunos</h2>
                <button id="abrir-modal-matricula" class="button primary">
                    <svg class="icon icon-sm" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><line x1="12" x2="12" y1="5" y2="19"/><line x1="5" x2="19" y1="12" y2="12"/></svg>
                    Matricular Aluno
                </button>
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

            <div id="aluno-list" class="aluno-list">
                <!-- Cards de alunos serão injetados aqui -->
            </div>
        </div>

        <!-- Modal de Matrícula/Edição -->
        <div id="modal-aluno" class="modal hidden">
            <div class="modal-content">
                <div class="modal-header">
                    <h2 id="modal-title">Matricular Novo Aluno</h2>
                    <button id="fechar-modal" class="modal-close">&times;</button>
                </div>
                <form id="form-aluno" class="form-modal space-y-4">
                    <input type="hidden" id="aluno-id" name="id" />
                    <div class="form-group">
                        <label for="nome" class="block text-sm font-medium text-gray-700">Nome Completo</label>
                        <input type="text" id="nome" name="nome" required class="input w-full" />
                    </div>
                    <div class="grid-cols-2">
                        <div class="form-group">
                            <label for="cpf" class="block text-sm font-medium text-gray-700">CPF</label>
                            <input type="text" id="cpf" name="cpf" required class="input w-full" placeholder="Ex: 000.000.000-00" />
                        </div>
                        <div class="form-group">
                            <label for="dataNascimento" class="block text-sm font-medium text-gray-700">Data de Nascimento</label>
                            <input type="date" id="dataNascimento" name="dataNascimento" required class="input w-full" />
                        </div>
                    </div>
                    <div class="grid-cols-3">
                        <div class="form-group">
                            <label for="serie" class="block text-sm font-medium text-gray-700">Série</label>
                            <div class="select-wrapper">
                                <select id="serie" name="serie" required class="input w-full">
                                    <option value="">Selecione</option>
                                    ${SERIES.map(s => `<option value="${s}">${s}</option>`).join('')}
                                </select>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="turno" class="block text-sm font-medium text-gray-700">Turno</label>
                            <div class="select-wrapper">
                                <select id="turno" name="turno" required class="input w-full">
                                    <option value="">Selecione</option>
                                    ${TURNOS.map(t => `<option value="${t}">${t}</option>`).join('')}
                                </select>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="telefone" class="block text-sm font-medium text-gray-700">Telefone</label>
                            <input type="tel" id="telefone" name="telefone" required class="input w-full" placeholder="(99) 99999-9999" />
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="responsavel" class="block text-sm font-medium text-gray-700">Nome do Responsável</label>
                        <input type="text" id="responsavel" name="responsavel" required class="input w-full" />
                    </div>
                    <button type="submit" id="salvar-aluno-button" class="button primary w-full">
                        Salvar Matrícula
                    </button>
                </form>
            </div>
        </div>
    `;
}

/**
 * Renderiza um card de aluno.
 * @param {Object} aluno
 * @returns {string} HTML do card.
 */
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
                <button class="button secondary edit-aluno" data-id="${aluno.id}">
                    <svg class="icon icon-sm" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M17 3a2.85 2.85 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5Z"/></svg>
                    Editar
                </button>
                <button class="button destructive delete-aluno" data-id="${aluno.id}">
                    <svg class="icon icon-sm" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M3 6h18"/><path d="M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6"/><path d="M8 6V4c0-1 1-2 2-2h4c1 0 2 1 2 2v2"/></svg>
                    Excluir
                </button>
            </div>
        </div>
    `;
}

/**
 * Renderiza a lista de alunos na página Home, aplicando filtros e busca.
 */
function renderizarListaAlunos() {
    const alunos = carregarAlunos();
    const busca = $('#busca-aluno').value.toLowerCase();
    const filtroSerie = $('#filtro-serie').value;
    const filtroTurno = $('#filtro-turno').value;

    const alunosFiltrados = alunos.filter(aluno => {
        const matchBusca = aluno.nome.toLowerCase().includes(busca) || aluno.cpf.includes(busca);
        const matchSerie = filtroSerie === 'todas' || aluno.serie === filtroSerie;
        const matchTurno = filtroTurno === 'todos' || aluno.turno === filtroTurno;
        return matchBusca && matchSerie && matchTurno;
    });

    const listaAlunosDiv = $('#aluno-list');
    listaAlunosDiv.innerHTML = alunosFiltrados.map(renderizarCardAluno).join('');

    // Adicionar listeners para os botões de Editar e Excluir
    $$('.edit-aluno').forEach(btn => btn.addEventListener('click', (e) => abrirModalEdicao(e.currentTarget.dataset.id)));
    $$('.delete-aluno').forEach(btn => btn.addEventListener('click', (e) => confirmarExclusao(e.currentTarget.dataset.id)));
}

/**
 * Renderiza a página Home completa e adiciona listeners.
 */
function renderizarHomePage() {
    homePage.innerHTML = gerarHomeHTML();

    // Adicionar listeners
    $('#logout-button').addEventListener('click', handleLogout);
    $('#abrir-modal-matricula').addEventListener('click', abrirModalMatricula);
    $('#fechar-modal').addEventListener('click', fecharModal);
    $('#form-aluno').addEventListener('submit', handleSalvarAluno);
    $('#busca-aluno').addEventListener('input', renderizarListaAlunos);
    $('#filtro-serie').addEventListener('change', renderizarListaAlunos);
    $('#filtro-turno').addEventListener('change', renderizarListaAlunos);

    // Renderizar a lista inicial de alunos
    renderizarListaAlunos();
}

// ==================================================================================
// MÓDULO MODAL (Matrícula e Edição)
// ==================================================================================

const modalAluno = $('#modal-aluno');
const modalTitle = $('#modal-title');
const formAluno = $('#form-aluno');

/**
 * Abre o modal para nova matrícula.
 */
function abrirModalMatricula() {
    modalTitle.textContent = 'Matricular Novo Aluno';
    formAluno.reset();
    $('#aluno-id').value = '';
    modalAluno.classList.remove('hidden');
}

/**
 * Abre o modal para edição de aluno.
 * @param {string} id ID do aluno.
 */
function abrirModalEdicao(id) {
    const alunos = carregarAlunos();
    const aluno = alunos.find(a => a.id == id);

    if (aluno) {
        modalTitle.textContent = `Editar Aluno: ${aluno.nome}`;
        $('#aluno-id').value = aluno.id;
        $('#nome').value = aluno.nome;
        $('#cpf').value = aluno.cpf;
        $('#dataNascimento').value = aluno.dataNascimento;
        $('#serie').value = aluno.serie;
        $('#turno').value = aluno.turno;
        $('#responsavel').value = aluno.responsavel;
        $('#telefone').value = aluno.telefone;
        modalAluno.classList.remove('hidden');
    }
}

/**
 * Fecha o modal.
 */
function fecharModal() {
    modalAluno.classList.add('hidden');
}

/**
 * Manipulador de submissão do formulário de aluno (Matrícula/Edição).
 * @param {Event} e
 */
function handleSalvarAluno(e) {
    e.preventDefault();

    const id = $('#aluno-id').value;
    const nome = $('#nome').value;
    const cpf = $('#cpf').value;
    const dataNascimento = $('#dataNascimento').value;
    const serie = $('#serie').value;
    const turno = $('#turno').value;
    const responsavel = $('#responsavel').value;
    const telefone = $('#telefone').value;

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
        // Edição
        alunos = alunos.map(a => a.id == id ? { ...a, ...novoAluno } : a);
    } else {
        // Nova Matrícula
        alunos.push(novoAluno);
    }

    salvarAlunos(alunos);
    fecharModal();
    renderizarListaAlunos();
}

/**
 * Confirma e realiza a exclusão de um aluno.
 * @param {string} id ID do aluno.
 */
function confirmarExclusao(id) {
    const alunos = carregarAlunos();
    const aluno = alunos.find(a => a.id == id);

    if (aluno && confirm(`Tem certeza que deseja excluir a matrícula de ${aluno.nome}?`)) {
        const alunosAtualizados = alunos.filter(a => a.id != id);
        salvarAlunos(alunosAtualizados);
        renderizarListaAlunos();
    }
}

// ==================================================================================
// INICIALIZAÇÃO
// ==================================================================================

document.addEventListener('DOMContentLoaded', () => {
    // Adicionar listener ao formulário de login
    if (loginForm) {
        loginForm.addEventListener('submit', handleLogin);
    }

    // Verificar estado de autenticação ao carregar a página
    if (estaLogado()) {
        navegar('home');
    } else {
        navegar('login');
    }
});
