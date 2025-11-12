document.addEventListener('DOMContentLoaded', () => {
    const loginContainer = document.getElementById('login-container');
    const mainContent = document.getElementById('main-content');
    const loginForm = document.getElementById('login-form');
    const loginError = document.getElementById('login-error');
    const logoutButton = document.getElementById('logoutButton');
    const userInfo = document.getElementById('userInfo');

    const alunosTableBody = document.querySelector('#alunos-table tbody');
    const alunoForm = document.getElementById('aluno-form');
    const alunoIdInput = document.getElementById('alunoId');
    const alunoNomeInput = document.getElementById('alunoNome');
    const alunoEmailInput = document.getElementById('alunoEmail');
    const alunoDataNascimentoInput = document.getElementById('alunoDataNascimento');

    const API_URL = 'http://localhost:8080'; // URL base da sua API

    // Função para fazer requisições à API
    async function fetchAPI(endpoint, options = {}) {
        const token = localStorage.getItem('jwtToken');
        const headers = {
            'Content-Type': 'application/json',
            ...options.headers,
        };

        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }

        const response = await fetch(`${API_URL}${endpoint}`, { ...options, headers });

        if (response.status === 401 || response.status === 403) {
            logout();
            return;
        }
        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Ocorreu um erro na requisição.');
        }
        // Retorna o JSON apenas se o corpo da resposta não estiver vazio
        const contentType = response.headers.get("content-type");
        if (contentType && contentType.indexOf("application/json") !== -1) {
            return response.json();
        }
    }

    // Função de Login
    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        loginError.textContent = '';
        const login = document.getElementById('login').value;
        const password = document.getElementById('password').value;

        try {
            // ATENÇÃO: O endpoint de login pode ser diferente no seu backend. Ajuste se necessário.
            const data = await fetchAPI('/auth/login', {
                method: 'POST',
                body: JSON.stringify({ login, password })
            });

            if (data && data.token) {
                localStorage.setItem('jwtToken', data.token);
                // Você pode decodificar o token para pegar o nome do usuário se quiser
                // Por simplicidade, vamos usar o login
                document.getElementById('userName').textContent = `Bem-vindo, ${login}!`;
                showMainContent();
            } else {
                 loginError.textContent = 'Token não recebido.';
            }
        } catch (error) {
            loginError.textContent = 'Login ou senha inválidos.';
            console.error('Erro no login:', error);
        }
    });

    // Função de Logout
    function logout() {
        localStorage.removeItem('jwtToken');
        showLogin();
    }
    logoutButton.addEventListener('click', logout);

    // Carregar Alunos
    async function loadAlunos() {
        try {
            // ATENÇÃO: O endpoint para buscar alunos pode ser diferente. Ajuste se necessário.
            const alunos = await fetchAPI('/api/alunos'); 
            alunosTableBody.innerHTML = '';
            alunos.forEach(aluno => {
                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td>${aluno.idAluno}</td>
                    <td>${aluno.nome}</td>
                    <td>${aluno.email}</td>
                    <td>${aluno.dataNascimento}</td>
                    <td>
                        <button onclick="editAluno('${aluno.idAluno}', '${aluno.nome}', '${aluno.email}', '${aluno.dataNascimento}')">Editar</button>
                        <button onclick="deleteAluno('${aluno.idAluno}')">Excluir</button>
                    </td>
                `;
                alunosTableBody.appendChild(tr);
            });
        } catch (error) {
            console.error('Erro ao carregar alunos:', error);
            alert('Não foi possível carregar os alunos.');
        }
    }

    // Salvar Aluno (Criar ou Atualizar)
    alunoForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const id = alunoIdInput.value;
        const aluno = {
            nome: alunoNomeInput.value,
            email: alunoEmailInput.value,
            dataNascimento: alunoDataNascimentoInput.value,
        };

        const method = id ? 'PUT' : 'POST';
        const endpoint = id ? `/api/alunos/${id}` : '/api/alunos';

        try {
            await fetchAPI(endpoint, { method, body: JSON.stringify(aluno) });
            alunoForm.reset();
            alunoIdInput.value = '';
            loadAlunos();
        } catch (error) {
            console.error('Erro ao salvar aluno:', error);
            alert('Não foi possível salvar o aluno.');
        }
    });

    // Funções globais para os botões da tabela
    window.editAluno = (id, nome, email, dataNascimento) => {
        alunoIdInput.value = id;
        alunoNomeInput.value = nome;
        alunoEmailInput.value = email;
        alunoDataNascimentoInput.value = dataNascimento;
        window.scrollTo(0, 0); // Rola para o topo para ver o formulário
    };

    window.deleteAluno = async (id) => {
        if (confirm('Tem certeza que deseja excluir este aluno?')) {
            try {
                await fetchAPI(`/api/alunos/${id}`, { method: 'DELETE' });
                loadAlunos();
            } catch (error) {
                console.error('Erro ao excluir aluno:', error);
                alert('Não foi possível excluir o aluno.');
            }
        }
    };

    // Controle de visibilidade
    function showLogin() {
        loginContainer.classList.remove('hidden');
        mainContent.classList.add('hidden');
        userInfo.classList.add('hidden');
    }

    function showMainContent() {
        loginContainer.classList.add('hidden');
        mainContent.classList.remove('hidden');
        userInfo.classList.remove('hidden');
        loadAlunos();
    }

    // Checa se já existe um token ao carregar a página
    if (localStorage.getItem('jwtToken')) {
        // Poderia validar o token aqui, mas por simplicidade vamos direto ao conteúdo
        showMainContent();
    } else {
        showLogin();
    }
});