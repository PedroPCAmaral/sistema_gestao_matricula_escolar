// script.js

// --- Estrutura de Dados e Persistência (localStorage) ---

const STORAGE_KEYS = {
    USERS: 'matricula_users',
    ALUNOS: 'matricula_alunos',
    TURMAS: 'matricula_turmas',
    LOGGED_USER: 'matricula_logged_user'
};

const MAX_ALUNOS_POR_TURMA = 30;

// Dados Iniciais (Mock Data)
const INITIAL_USERS = [
    { email: 'admin@matricula.com', password: 'admin123', role: 'Admin' },
    { email: 'secretario@matricula.com', password: 'sec123', role: 'Secretario' },
    { email: 'professor@matricula.com', password: 'prof123', role: 'Professor' },
];

const INITIAL_TURMAS = [
    { id: 'F1MA', nome: 'Fundamental 1 - Matutino - A', serie: 'Fundamental 1', turno: 'Matutino', alunos: [] },
    { id: 'F1VB', nome: 'Fundamental 1 - Vespertino - B', serie: 'Fundamental 1', turno: 'Vespertino', alunos: [] },
    { id: 'F2MA', nome: 'Fundamental 2 - Matutino - A', serie: 'Fundamental 2', turno: 'Matutino', alunos: [] },
    { id: 'F2VB', nome: 'Fundamental 2 - Vespertino - B', serie: 'Fundamental 2', turno: 'Vespertino', alunos: [] },
    { id: 'EMMA', nome: 'Ensino Médio - Matutino - A', serie: 'Ensino Médio', turno: 'Matutino', alunos: [] },
    { id: 'EMVB', nome: 'Ensino Médio - Vespertino - B', serie: 'Ensino Médio', turno: 'Vespertino', alunos: [] },
];

// Funções de Persistência
function getStorageData(key, initialData) {
    const data = localStorage.getItem(key);
    if (data) {
        return JSON.parse(data);
    }
    // Inicializa o localStorage se não houver dados
    localStorage.setItem(key, JSON.stringify(initialData));
    return initialData;
}

function setStorageData(key, data) {
    localStorage.setItem(key, JSON.stringify(data));
}

// Inicialização dos Dados
let users = getStorageData(STORAGE_KEYS.USERS, INITIAL_USERS);
let turmas = getStorageData(STORAGE_KEYS.TURMAS, INITIAL_TURMAS);
let alunos = getStorageData(STORAGE_KEYS.ALUNOS, []);
let loggedUser = getStorageData(STORAGE_KEYS.LOGGED_USER, null);

// --- Funções de Renderização ---

function renderLogin() {
    const app = document.getElementById('app');
    app.innerHTML = `
        <div class="login-container">
            <h1>Sistema de Matrícula Escolar</h1>
            <div id="login-error" class="error-message" style="display:none;"></div>
            <input type="email" id="login-email" placeholder="seu@email.com">
            <input type="password" id="login-password" placeholder="••••••••">
            <button id="login-btn">Entrar</button>
            <div class="test-credentials">
                <strong>Credenciais de Teste:</strong>
                <p>Admin: admin@matricula.com / admin123</p>
                <p>Secretário: secretario@matricula.com / sec123</p>
                <p>Professor: professor@matricula.com / prof123</p>
            </div>
        </div>
    `;

    document.getElementById('login-btn').addEventListener('click', handleLogin);
}

function renderDashboard() {
    const app = document.getElementById('app');
    const role = loggedUser.role;
    const userName = loggedUser.email.split('@')[0];

    app.innerHTML = `
        <div class="dashboard-container">
            <div class="header">
                <h1>Sistema de Matrícula Escolar</h1>
                <div>
                    <span>Bem-vindo, ${userName} (${role})</span>
                    <button id="logout-btn">Sair</button>
                </div>
            </div>

            <div class="tabs">
                <button id="tab-alunos" class="active">Alunos (${alunos.length})</button>
                <button id="tab-turmas">Turmas (${turmas.length})</button>
            </div>

            <div id="content-area">
                <!-- Conteúdo de Alunos ou Turmas será injetado aqui -->
            </div>
        </div>
    `;

    document.getElementById('logout-btn').addEventListener('click', handleLogout);
    document.getElementById('tab-alunos').addEventListener('click', () => {
        document.getElementById('tab-alunos').classList.add('active');
        document.getElementById('tab-turmas').classList.remove('active');
        renderAlunosTab();
    });
    document.getElementById('tab-turmas').addEventListener('click', () => {
        document.getElementById('tab-turmas').classList.add('active');
        document.getElementById('tab-alunos').classList.remove('active');
        renderTurmasTab();
    });

    // Renderiza a aba inicial (Alunos)
    renderAlunosTab();
}

// --- Funções de Lógica ---

function handleLogin() {
    const email = document.getElementById('login-email').value;
    const password = document.getElementById('login-password').value;
    const errorDiv = document.getElementById('login-error');

    const user = users.find(u => u.email === email && u.password === password);

    if (user) {
        loggedUser = user;
        setStorageData(STORAGE_KEYS.LOGGED_USER, user);
        errorDiv.style.display = 'none';
        renderDashboard();
    } else {
        errorDiv.textContent = 'Credenciais inválidas.';
        errorDiv.style.display = 'block';
    }
}

function handleLogout() {
    loggedUser = null;
    setStorageData(STORAGE_KEYS.LOGGED_USER, null);
    renderLogin();
}

// --- Funções da Aba Alunos ---

function renderAlunosTab() {
    const contentArea = document.getElementById('content-area');
    const series = [...new Set(turmas.map(t => t.serie))];
    const turnos = [...new Set(turmas.map(t => t.turno))];

    contentArea.innerHTML = `
        <div class="controls">
            <select id="filter-serie">
                <option value="">Todas as Séries</option>
                ${series.map(s => `<option value="${s}">${s}</option>`).join('')}
            </select>
            <select id="filter-turno">
                <option value="">Todos os Turnos</option>
                ${turnos.map(t => `<option value="${t}">${t}</option>`).join('')}
            </select>
            <input type="text" id="search-aluno" placeholder="Nome ou CPF...">
            <button id="new-aluno-btn">Novo Aluno</button>
        </div>
        <div id="alunos-list" class="list-grid">
            <!-- Cards de Alunos -->
        </div>
    `;

    document.getElementById('new-aluno-btn').addEventListener('click', () => renderAlunoModal());
    document.getElementById('filter-serie').addEventListener('change', filterAndRenderAlunos);
    document.getElementById('filter-turno').addEventListener('change', filterAndRenderAlunos);
    document.getElementById('search-aluno').addEventListener('input', filterAndRenderAlunos);

    filterAndRenderAlunos();
}

function filterAndRenderAlunos() {
    const filterSerie = document.getElementById('filter-serie').value;
    const filterTurno = document.getElementById('filter-turno').value;
    const searchTerm = document.getElementById('search-aluno').value.toLowerCase();
    const alunosListDiv = document.getElementById('alunos-list');

    const filteredAlunos = alunos.filter(aluno => {
        const turma = turmas.find(t => t.id === aluno.turmaId);
        if (!turma) return false; // Aluno sem turma válida

        const matchesSerie = !filterSerie || turma.serie === filterSerie;
        const matchesTurno = !filterTurno || turma.turno === filterTurno;
        const matchesSearch = !searchTerm || aluno.nome.toLowerCase().includes(searchTerm) || aluno.cpf.includes(searchTerm);

        return matchesSerie && matchesTurno && matchesSearch;
    });

    alunosListDiv.innerHTML = filteredAlunos.map(aluno => {
        const turma = turmas.find(t => t.id === aluno.turmaId);
        const turmaNome = turma ? turma.nome : 'Sem Turma';
        return `
            <div class="card">
                <h3>${aluno.nome}</h3>
                <p><strong>CPF:</strong> ${aluno.cpf}</p>
                <p><strong>Idade:</strong> ${aluno.idade} anos</p>
                <p><strong>Telefone:</strong> ${aluno.telefone}</p>
                <p><strong>Endereço:</strong> ${aluno.endereco}</p>
                <p><strong>Turma:</strong> ${turmaNome}</p>
                <div class="actions">
                    <button class="edit-btn" data-id="${aluno.id}">Editar</button>
                    <button class="delete-btn" data-id="${aluno.id}">Deletar</button>
                </div>
            </div>
        `;
    }).join('');

    // Adiciona listeners para os botões de Editar e Deletar
    document.querySelectorAll('.edit-btn').forEach(btn => {
        btn.addEventListener('click', (e) => renderAlunoModal(e.target.dataset.id));
    });
    document.querySelectorAll('.delete-btn').forEach(btn => {
        btn.addEventListener('click', (e) => handleDeleteAluno(e.target.dataset.id));
    });
}

function renderAlunoModal(alunoId = null) {
    const isEdit = alunoId !== null;
    const aluno = isEdit ? alunos.find(a => a.id === alunoId) : {};

    const modalHtml = `
        <div id="aluno-modal" class="modal">
            <div class="modal-content">
                <h2>${isEdit ? 'Editar Aluno' : 'Novo Aluno'}</h2>
                <div id="modal-error" class="error-message" style="display:none;"></div>
                
                <label for="aluno-nome">Nome:</label>
                <input type="text" id="aluno-nome" value="${aluno.nome || ''}" required>

                <label for="aluno-cpf">CPF:</label>
                <input type="text" id="aluno-cpf" value="${aluno.cpf || ''}" required>

                <label for="aluno-idade">Idade:</label>
                <input type="number" id="aluno-idade" value="${aluno.idade || ''}" required>

                <label for="aluno-telefone">Telefone:</label>
                <input type="text" id="aluno-telefone" value="${aluno.telefone || ''}">

                <label for="aluno-endereco">Endereço:</label>
                <input type="text" id="aluno-endereco" value="${aluno.endereco || ''}">

                <label for="aluno-turma">Turma:</label>
                <select id="aluno-turma" required>
                    <option value="">Selecione uma Turma</option>
                    ${turmas.map(t => {
                        const isSelected = t.id === aluno.turmaId ? 'selected' : '';
                        const isFull = t.alunos.length >= MAX_ALUNOS_POR_TURMA;
                        const disabled = isFull && t.id !== aluno.turmaId ? 'disabled' : '';
                        const label = isFull ? ` (LOTADA - ${t.alunos.length}/${MAX_ALUNOS_POR_TURMA})` : ` (${t.alunos.length}/${MAX_ALUNOS_POR_TURMA})`;
                        return `<option value="${t.id}" ${isSelected} ${disabled}>${t.nome}${label}</option>`;
                    }).join('')}
                </select>

                <div class="modal-actions">
                    <button class="cancel-btn" id="cancel-aluno-btn">Cancelar</button>
                    <button class="save-btn" id="save-aluno-btn" data-id="${alunoId || ''}">${isEdit ? 'Salvar' : 'Cadastrar'}</button>
                </div>
            </div>
        </div>
    `;

    document.body.insertAdjacentHTML('beforeend', modalHtml);

    document.getElementById('cancel-aluno-btn').addEventListener('click', () => document.getElementById('aluno-modal').remove());
    document.getElementById('save-aluno-btn').addEventListener('click', handleSaveAluno);
}

function handleSaveAluno(e) {
    const alunoId = e.target.dataset.id;
    const isEdit = alunoId !== '';
    const nome = document.getElementById('aluno-nome').value.trim();
    const cpf = document.getElementById('aluno-cpf').value.trim();
    const idade = parseInt(document.getElementById('aluno-idade').value);
    const telefone = document.getElementById('aluno-telefone').value.trim();
    const endereco = document.getElementById('aluno-endereco').value.trim();
    const turmaId = document.getElementById('aluno-turma').value;
    const errorDiv = document.getElementById('modal-error');

    // Validação básica
    if (!nome || !cpf || isNaN(idade) || idade <= 0 || !turmaId) {
        errorDiv.textContent = 'Por favor, preencha todos os campos obrigatórios (Nome, CPF, Idade e Turma).';
        errorDiv.style.display = 'block';
        return;
    }

    // Validação de CPF duplicado (exceto para o próprio aluno em edição)
    if (alunos.some(a => a.cpf === cpf && a.id !== alunoId)) {
        errorDiv.textContent = 'Já existe um aluno cadastrado com este CPF.';
        errorDiv.style.display = 'block';
        return;
    }

    // Validação de Lotação da Turma
    const novaTurma = turmas.find(t => t.id === turmaId);
    const alunoAtual = isEdit ? alunos.find(a => a.id === alunoId) : null;
    const turmaAntigaId = alunoAtual ? alunoAtual.turmaId : null;

    if (turmaId !== turmaAntigaId) {
        if (novaTurma.alunos.length >= MAX_ALUNOS_POR_TURMA) {
            errorDiv.textContent = `A turma selecionada (${novaTurma.nome}) está lotada (${MAX_ALUNOS_POR_TURMA} alunos).`;
            errorDiv.style.display = 'block';
            return;
        }
    }

    // Processamento dos dados
    const novoAluno = {
        id: alunoId || Date.now().toString(), // ID simples baseado em timestamp
        nome,
        cpf,
        idade,
        telefone,
        endereco,
        turmaId
    };

    if (isEdit) {
        // 1. Atualiza a lista de alunos
        alunos = alunos.map(a => a.id === alunoId ? novoAluno : a);

        // 2. Remove o aluno da turma antiga (se mudou)
        if (turmaAntigaId && turmaAntigaId !== turmaId) {
            const turmaAntiga = turmas.find(t => t.id === turmaAntigaId);
            if (turmaAntiga) {
                turmaAntiga.alunos = turmaAntiga.alunos.filter(id => id !== alunoId);
            }
        }
    } else {
        // 1. Adiciona novo aluno
        alunos.push(novoAluno);
    }

    // 3. Adiciona o aluno à nova turma
    if (turmaId) {
        const turma = turmas.find(t => t.id === turmaId);
        if (turma && !turma.alunos.includes(novoAluno.id)) {
            turma.alunos.push(novoAluno.id);
        }
    }

    // 4. Salva no localStorage
    setStorageData(STORAGE_KEYS.ALUNOS, alunos);
    setStorageData(STORAGE_KEYS.TURMAS, turmas);

    // 5. Fecha o modal e renderiza a lista
    document.getElementById('aluno-modal').remove();
    filterAndRenderAlunos();
    updateTabCounts();
}

function handleDeleteAluno(alunoId) {
    if (!confirm('Tem certeza que deseja deletar este aluno?')) return;

    const alunoToDelete = alunos.find(a => a.id === alunoId);
    if (!alunoToDelete) return;

    // 1. Remove o aluno da turma
    const turma = turmas.find(t => t.id === alunoToDelete.turmaId);
    if (turma) {
        turma.alunos = turma.alunos.filter(id => id !== alunoId);
    }

    // 2. Remove o aluno da lista de alunos
    alunos = alunos.filter(a => a.id !== alunoId);

    // 3. Salva no localStorage
    setStorageData(STORAGE_KEYS.ALUNOS, alunos);
    setStorageData(STORAGE_KEYS.TURMAS, turmas);

    // 4. Renderiza a lista
    filterAndRenderAlunos();
    updateTabCounts();
}

// --- Funções da Aba Turmas ---

function renderTurmasTab() {
    const contentArea = document.getElementById('content-area');

    contentArea.innerHTML = `
        <div class="controls">
            <button id="new-turma-btn">Nova Turma</button>
        </div>
        <div id="turmas-list" class="list-grid">
            <!-- Cards de Turmas -->
        </div>
    `;

    document.getElementById('new-turma-btn').addEventListener('click', () => renderTurmaModal());

    const turmasListDiv = document.getElementById('turmas-list');
    turmasListDiv.innerHTML = turmas.map(turma => {
        const alunosCount = turma.alunos.length;
        const isFull = alunosCount >= MAX_ALUNOS_POR_TURMA;
        const status = isFull ? 'Lotada' : 'Vagas disponíveis';
        const statusColor = isFull ? 'red' : 'green';

        return `
            <div class="card turma-card">
                <h3>${turma.nome}</h3>
                <p><strong>Série:</strong> ${turma.serie}</p>
                <p><strong>Turno:</strong> ${turma.turno}</p>
                <p class="capacity"><strong>Alunos:</strong> ${alunosCount} / ${MAX_ALUNOS_POR_TURMA}</p>
                <p style="color: ${statusColor}; font-weight: bold;">Status: ${status}</p>
                <div class="actions">
                    <button class="edit-btn" data-id="${turma.id}">Editar</button>
                    <button class="delete-btn" data-id="${turma.id}">Deletar</button>
                </div>
            </div>
        `;
    }).join('');

    // Adiciona listeners para os botões de Editar e Deletar
    document.querySelectorAll('.edit-btn').forEach(btn => {
        btn.addEventListener('click', (e) => renderTurmaModal(e.target.dataset.id));
    });
    document.querySelectorAll('.delete-btn').forEach(btn => {
        btn.addEventListener('click', (e) => handleDeleteTurma(e.target.dataset.id));
    });
}

function renderTurmaModal(turmaId = null) {
    const isEdit = turmaId !== null;
    const turma = isEdit ? turmas.find(t => t.id === turmaId) : {};

    const series = [...new Set(INITIAL_TURMAS.map(t => t.serie))];
    const turnos = [...new Set(INITIAL_TURMAS.map(t => t.turno))];

    const modalHtml = `
        <div id="turma-modal" class="modal">
            <div class="modal-content">
                <h2>${isEdit ? 'Editar Turma' : 'Nova Turma'}</h2>
                <div id="modal-error" class="error-message" style="display:none;"></div>
                
                <label for="turma-nome">Nome:</label>
                <input type="text" id="turma-nome" value="${turma.nome || ''}" required>

                <label for="turma-serie">Série:</label>
                <select id="turma-serie" required>
                    <option value="">Selecione a Série</option>
                    ${series.map(s => `<option value="${s}" ${turma.serie === s ? 'selected' : ''}>${s}</option>`).join('')}
                </select>

                <label for="turma-turno">Turno:</label>
                <select id="turma-turno" required>
                    <option value="">Selecione o Turno</option>
                    ${turnos.map(t => `<option value="${t}" ${turma.turno === t ? 'selected' : ''}>${t}</option>`).join('')}
                </select>

                <div class="modal-actions">
                    <button class="cancel-btn" id="cancel-turma-btn">Cancelar</button>
                    <button class="save-btn" id="save-turma-btn" data-id="${turmaId || ''}">${isEdit ? 'Salvar' : 'Cadastrar'}</button>
                </div>
            </div>
        </div>
    `;

    document.body.insertAdjacentHTML('beforeend', modalHtml);

    document.getElementById('cancel-turma-btn').addEventListener('click', () => document.getElementById('turma-modal').remove());
    document.getElementById('save-turma-btn').addEventListener('click', handleSaveTurma);
}

function handleSaveTurma(e) {
    const turmaId = e.target.dataset.id;
    const isEdit = turmaId !== '';
    const nome = document.getElementById('turma-nome').value.trim();
    const serie = document.getElementById('turma-serie').value;
    const turno = document.getElementById('turma-turno').value;
    const errorDiv = document.getElementById('modal-error');

    if (!nome || !serie || !turno) {
        errorDiv.textContent = 'Por favor, preencha todos os campos.';
        errorDiv.style.display = 'block';
        return;
    }

    const newTurma = {
        id: turmaId || serie.substring(0, 2) + turno.substring(0, 1) + Date.now().toString().slice(-4), // ID simples
        nome,
        serie,
        turno,
        alunos: isEdit ? turmas.find(t => t.id === turmaId).alunos : []
    };

    if (isEdit) {
        turmas = turmas.map(t => t.id === turmaId ? newTurma : t);
    } else {
        turmas.push(newTurma);
    }

    setStorageData(STORAGE_KEYS.TURMAS, turmas);
    document.getElementById('turma-modal').remove();
    renderTurmasTab();
    updateTabCounts();
}

function handleDeleteTurma(turmaId) {
    const turma = turmas.find(t => t.id === turmaId);
    if (turma.alunos.length > 0) {
        alert(`Não é possível deletar a turma "${turma.nome}" pois ela possui ${turma.alunos.length} alunos matriculados.`);
        return;
    }

    if (!confirm(`Tem certeza que deseja deletar a turma "${turma.nome}"?`)) return;

    turmas = turmas.filter(t => t.id !== turmaId);
    setStorageData(STORAGE_KEYS.TURMAS, turmas);
    renderTurmasTab();
    updateTabCounts();
}

// --- Funções de Utilidade ---

function updateTabCounts() {
    const alunosTab = document.getElementById('tab-alunos');
    const turmasTab = document.getElementById('tab-turmas');
    if (alunosTab) alunosTab.textContent = `Alunos (${alunos.length})`;
    if (turmasTab) turmasTab.textContent = `Turmas (${turmas.length})`;
}

// --- Inicialização da Aplicação ---

function initApp() {
    if (loggedUser) {
        renderDashboard();
    } else {
        renderLogin();
    }
}

// Adiciona 35 alunos iniciais para simular o estado do frontend original
function addInitialAlunos() {
    if (alunos.length === 0) {
        const initialAlunosData = [
            { nome: 'Ana Silva', cpf: '001.001.001-01', idade: 6, telefone: '(11) 90001-0001', endereco: 'Rua Ana, 100', turmaId: 'F1MA' },
            { nome: 'Bruno Costa', cpf: '002.002.002-02', idade: 6, telefone: '(11) 90002-0002', endereco: 'Rua Bruno, 101', turmaId: 'F1VB' },
            { nome: 'Carlos Santos', cpf: '003.003.003-03', idade: 6, telefone: '(11) 90003-0003', endereco: 'Rua Carlos, 102', turmaId: 'F1MA' },
            { nome: 'Diana Oliveira', cpf: '004.004.004-04', idade: 6, telefone: '(11) 90004-0004', endereco: 'Rua Diana, 103', turmaId: 'F1VB' },
            { nome: 'Eduardo Ferreira', cpf: '005.005.005-05', idade: 6, telefone: '(11) 90005-0005', endereco: 'Rua Eduardo, 104', turmaId: 'F1MA' },
            { nome: 'Fernanda Gomes', cpf: '006.006.006-06', idade: 6, telefone: '(11) 90006-0006', endereco: 'Rua Fernanda, 105', turmaId: 'F1VB' },
            { nome: 'Gabriel Martins', cpf: '007.007.007-07', idade: 6, telefone: '(11) 90007-0007', endereco: 'Rua Gabriel, 106', turmaId: 'F1MA' },
            { nome: 'Helena Rocha', cpf: '008.008.008-08', idade: 6, telefone: '(11) 90008-0008', endereco: 'Rua Helena, 107', turmaId: 'F1VB' },
            { nome: 'Igor Alves', cpf: '009.009.009-09', idade: 6, telefone: '(11) 90009-0009', endereco: 'Rua Igor, 108', turmaId: 'F1MA' },
            { nome: 'Juliana Pereira', cpf: '010.010.010-10', idade: 6, telefone: '(11) 90010-0010', endereco: 'Rua Juliana, 109', turmaId: 'F1VB' },
            { nome: 'Kevin Souza', cpf: '011.011.011-11', idade: 6, telefone: '(11) 90011-0011', endereco: 'Rua Kevin, 110', turmaId: 'F1MA' },
            { nome: 'Larissa Monteiro', cpf: '012.012.012-12', idade: 6, telefone: '(11) 90012-0012', endereco: 'Rua Larissa, 111', turmaId: 'F1VB' },
            { nome: 'Marcos Ribeiro', cpf: '013.013.013-13', idade: 7, telefone: '(11) 90013-0013', endereco: 'Rua Marcos, 112', turmaId: 'F2MA' },
            { nome: 'Natália Cardoso', cpf: '014.014.014-14', idade: 7, telefone: '(11) 90014-0014', endereco: 'Rua Natália, 113', turmaId: 'F2VB' },
            { nome: 'Otávio Mendes', cpf: '015.015.015-15', idade: 7, telefone: '(11) 90015-0015', endereco: 'Rua Otávio, 114', turmaId: 'F2MA' },
            { nome: 'Patricia Neves', cpf: '016.016.016-16', idade: 7, telefone: '(11) 90016-0016', endereco: 'Rua Patricia, 115', turmaId: 'F2VB' },
            { nome: 'Quentin Barbosa', cpf: '017.017.017-17', idade: 7, telefone: '(11) 90017-0017', endereco: 'Rua Quentin, 116', turmaId: 'F2MA' },
            { nome: 'Rafaela Teixeira', cpf: '018.018.018-18', idade: 7, telefone: '(11) 90018-0018', endereco: 'Rua Rafaela, 117', turmaId: 'F2VB' },
            { nome: 'Samuel Correia', cpf: '019.019.019-19', idade: 7, telefone: '(11) 90019-0019', endereco: 'Rua Samuel, 118', turmaId: 'F2MA' },
            { nome: 'Tania Moura', cpf: '020.020.020-20', idade: 7, telefone: '(11) 90020-0020', endereco: 'Rua Tania, 119', turmaId: 'F2VB' },
            { nome: 'Ulisses Pinto', cpf: '021.021.021-21', idade: 7, telefone: '(11) 90021-0021', endereco: 'Rua Ulisses, 120', turmaId: 'F2MA' },
            { nome: 'Vanessa Lopes', cpf: '022.022.022-22', idade: 7, telefone: '(11) 90022-0022', endereco: 'Rua Vanessa, 121', turmaId: 'F2VB' },
            { nome: 'Wagner Duarte', cpf: '023.023.023-23', idade: 7, telefone: '(11) 90023-0023', endereco: 'Rua Wagner, 122', turmaId: 'F2MA' },
            { nome: 'Ximena Campos', cpf: '024.024.024-24', idade: 7, telefone: '(11) 90024-0024', endereco: 'Rua Ximena, 123', turmaId: 'F2VB' },
            { nome: 'Yara Medeiros', cpf: '025.025.025-25', idade: 8, telefone: '(11) 90025-0025', endereco: 'Rua Yara, 124', turmaId: 'EMMA' },
            { nome: 'Zoe Machado', cpf: '026.026.026-26', idade: 8, telefone: '(11) 90026-0026', endereco: 'Rua Zoe, 125', turmaId: 'EMVB' },
            { nome: 'Adriano Reis', cpf: '027.027.027-27', idade: 8, telefone: '(11) 90027-0027', endereco: 'Rua Adriano, 126', turmaId: 'EMMA' },
            { nome: 'Beatriz Sousa', cpf: '028.028.028-28', idade: 8, telefone: '(11) 90028-0028', endereco: 'Rua Beatriz, 127', turmaId: 'EMVB' },
            { nome: 'Camila Vieira', cpf: '029.029.029-29', idade: 8, telefone: '(11) 90029-0029', endereco: 'Rua Camila, 128', turmaId: 'EMMA' },
            { nome: 'Danilo Couto', cpf: '030.030.030-30', idade: 8, telefone: '(11) 90030-0030', endereco: 'Rua Danilo, 129', turmaId: 'EMVB' },
            { nome: 'Elisa Braga', cpf: '031.031.031-31', idade: 8, telefone: '(11) 90031-0031', endereco: 'Rua Elisa, 130', turmaId: 'EMMA' },
            { nome: 'Felipe Guedes', cpf: '032.032.032-32', idade: 8, telefone: '(11) 90032-0032', endereco: 'Rua Felipe, 131', turmaId: 'EMVB' },
            { nome: 'Giselda Motta', cpf: '033.033.033-33', idade: 8, telefone: '(11) 90033-0033', endereco: 'Rua Giselda, 132', turmaId: 'EMMA' },
            { nome: 'Heitor Peixoto', cpf: '034.034.034-34', idade: 8, telefone: '(11) 90034-0034', endereco: 'Rua Heitor, 133', turmaId: 'EMVB' },
            { nome: 'Iris Marques', cpf: '035.035.035-35', idade: 8, telefone: '(11) 90035-0035', endereco: 'Rua Iris, 134', turmaId: 'EMMA' },
        ];

        alunos = initialAlunosData.map((aluno, index) => ({
            ...aluno,
            id: (index + 1).toString(),
        }));

        // Distribui os alunos nas turmas
        alunos.forEach(aluno => {
            const turma = turmas.find(t => t.id === aluno.turmaId);
            if (turma) {
                turma.alunos.push(aluno.id);
            }
        });

        setStorageData(STORAGE_KEYS.ALUNOS, alunos);
        setStorageData(STORAGE_KEYS.TURMAS, turmas);
    }
}

addInitialAlunos();
document.addEventListener('DOMContentLoaded', initApp);
