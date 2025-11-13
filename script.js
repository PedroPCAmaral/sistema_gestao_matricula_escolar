// ===== VARIÁVEIS GLOBAIS =====
let alunos = [];
let alunoEmEdicao = null;
const STORAGE_KEY = 'sistema_matricula_alunos';

// ===== INICIALIZAÇÃO =====
document.addEventListener('DOMContentLoaded', function() {
    carregarAlunos();
    renderizarTabela();
    atualizarEstatisticas();
    
    // Event listeners
    document.getElementById('searchInput').addEventListener('input', function(e) {
        renderizarTabela(e.target.value);
    });

    // Fechar modal ao clicar fora
    window.addEventListener('click', function(event) {
        const modal = document.getElementById('modal');
        if (event.target === modal) {
            fecharModal();
        }
    });

    // Fechar modal com ESC
    document.addEventListener('keydown', function(event) {
        if (event.key === 'Escape') {
            fecharModal();
        }
    });
});

// ===== LOCALSTORAGE =====
function carregarAlunos() {
    const dados = localStorage.getItem(STORAGE_KEY);
    if (dados) {
        alunos = JSON.parse(dados);
    } else {
        // Dados de exemplo
        alunos = [
            {
                id: 1,
                nome: 'João Silva',
                cpf: '12345678901',
                idade: 15,
                serie: '1º Ano',
                turno: 'Matutino',
                telefone: '11999999999'
            },
            {
                id: 2,
                nome: 'Maria Santos',
                cpf: '98765432101',
                idade: 16,
                serie: '2º Ano',
                turno: 'Vespertino',
                telefone: '11988888888'
            }
        ];
        salvarAlunos();
    }
}

function salvarAlunos() {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(alunos));
}

// ===== RENDERIZAÇÃO =====
function renderizarTabela(filtro = '') {
    const tbody = document.getElementById('tabelaAlunos');
    tbody.innerHTML = '';

    let alunosFiltrados = alunos;
    
    if (filtro) {
        alunosFiltrados = alunos.filter(aluno =>
            aluno.nome.toLowerCase().includes(filtro.toLowerCase()) ||
            aluno.cpf.includes(filtro)
        );
    }

    if (alunosFiltrados.length === 0) {
        tbody.innerHTML = `
            <tr class="empty-row">
                <td colspan="7" class="empty-message">
                    Nenhum aluno encontrado.
                </td>
            </tr>
        `;
        return;
    }

    alunosFiltrados.forEach(aluno => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${aluno.nome}</td>
            <td>${formatarCPF(aluno.cpf)}</td>
            <td>${aluno.idade}</td>
            <td>${aluno.serie}</td>
            <td><span class="badge">${aluno.turno}</span></td>
            <td>${aluno.telefone || '-'}</td>
            <td>
                <div class="action-buttons">
                    <button class="btn btn-edit" onclick="editarAluno(${aluno.id})">✏️ Editar</button>
                    <button class="btn btn-danger" onclick="deletarAluno(${aluno.id})">🗑️ Deletar</button>
                </div>
            </td>
        `;
        tbody.appendChild(row);
    });
}

function atualizarEstatisticas() {
    document.getElementById('totalAlunos').textContent = alunos.length;
    document.getElementById('alunosCadastrados').textContent = alunos.length;
}

// ===== MODAL =====
function abrirFormularioCadastro() {
    alunoEmEdicao = null;
    document.getElementById('formularioAluno').reset();
    document.getElementById('modalTitle').textContent = 'Cadastrar Novo Aluno';
    document.getElementById('cpf').disabled = false;
    document.getElementById('modal').classList.add('active');
}

function fecharModal() {
    document.getElementById('modal').classList.remove('active');
    alunoEmEdicao = null;
    document.getElementById('formularioAluno').reset();
}

// ===== CRUD =====
function salvarAluno(event) {
    event.preventDefault();

    const nome = document.getElementById('nome').value.trim();
    const cpf = document.getElementById('cpf').value.trim();
    const idade = parseInt(document.getElementById('idade').value);
    const serie = document.getElementById('serie').value.trim();
    const turno = document.getElementById('turno').value;
    const telefone = document.getElementById('telefone').value.trim();

    // Validações
    if (!nome || !cpf || !idade || !serie || !turno) {
        mostrarToast('Preencha todos os campos obrigatórios!', 'error');
        return;
    }

    if (cpf.length !== 11 || !/^\d+$/.test(cpf)) {
        mostrarToast('CPF deve conter 11 dígitos!', 'error');
        return;
    }

    // Verificar CPF duplicado (exceto se estiver editando)
    const cpfJaExiste = alunos.some(a => a.cpf === cpf && (!alunoEmEdicao || a.id !== alunoEmEdicao.id));
    if (cpfJaExiste) {
        mostrarToast('CPF já cadastrado no sistema!', 'error');
        return;
    }

    if (alunoEmEdicao) {
        // Editar
        const index = alunos.findIndex(a => a.id === alunoEmEdicao.id);
        if (index !== -1) {
            alunos[index] = {
                ...alunos[index],
                nome,
                cpf,
                idade,
                serie,
                turno,
                telefone
            };
            mostrarToast('Aluno atualizado com sucesso!', 'success');
        }
    } else {
        // Criar novo
        const novoAluno = {
            id: Date.now(),
            nome,
            cpf,
            idade,
            serie,
            turno,
            telefone
        };
        alunos.push(novoAluno);
        mostrarToast('Aluno cadastrado com sucesso!', 'success');
    }

    salvarAlunos();
    renderizarTabela();
    atualizarEstatisticas();
    fecharModal();
}

function editarAluno(id) {
    const aluno = alunos.find(a => a.id === id);
    if (!aluno) return;

    alunoEmEdicao = aluno;
    document.getElementById('nome').value = aluno.nome;
    document.getElementById('cpf').value = aluno.cpf;
    document.getElementById('cpf').disabled = true;
    document.getElementById('idade').value = aluno.idade;
    document.getElementById('serie').value = aluno.serie;
    document.getElementById('turno').value = aluno.turno;
    document.getElementById('telefone').value = aluno.telefone || '';
    
    document.getElementById('modalTitle').textContent = 'Editar Aluno';
    document.getElementById('modal').classList.add('active');
}

function deletarAluno(id) {
    const aluno = alunos.find(a => a.id === id);
    if (!aluno) return;

    if (confirm(`Tem certeza que deseja remover ${aluno.nome}?`)) {
        alunos = alunos.filter(a => a.id !== id);
        salvarAlunos();
        renderizarTabela();
        atualizarEstatisticas();
        mostrarToast('Aluno removido com sucesso!', 'success');
    }
}

// ===== UTILITÁRIOS =====
function formatarCPF(cpf) {
    if (!cpf || cpf.length !== 11) return cpf;
    return `${cpf.substring(0, 3)}.${cpf.substring(3, 6)}.${cpf.substring(6, 9)}-${cpf.substring(9)}`;
}

function mostrarToast(mensagem, tipo = 'success') {
    const toast = document.getElementById('toast');
    toast.textContent = mensagem;
    toast.className = `toast show ${tipo}`;
    
    setTimeout(() => {
        toast.classList.remove('show');
    }, 3000);
}

// ===== ESTILOS ADICIONAIS PARA BADGES =====
const style = document.createElement('style');
style.textContent = `
    .badge {
        display: inline-block;
        padding: 6px 12px;
        background: #dbeafe;
        color: #1e40af;
        border-radius: 20px;
        font-size: 0.85rem;
        font-weight: 600;
    }
`;
document.head.appendChild(style);
