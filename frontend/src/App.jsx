
import { useEffect, useState } from 'react'
import './App.css'

function App() {
    const [clientes, setClientes] = useState([])
    const [produtos, setProdutos] = useState([])
    const [pedidos, setPedidos] = useState([])

    const [nome, setNome] = useState('')
    const [email, setEmail] = useState('')
    const [idBusca, setIdBusca] = useState('')
    const [clienteEncontrado, setClienteEncontrado] = useState(null)

    const [nomeProduto, setNomeProduto] = useState('')
    const [precoProduto, setPrecoProduto] = useState('')
    const [estoqueProduto, setEstoqueProduto] = useState('')
    const [idBuscaProduto, setIdBuscaProduto] = useState('')
    const [produtoEncontrado, setProdutoEncontrado] = useState(null)

    const [clientePedido, setClientePedido] = useState('')
    const [produtoPedido, setProdutoPedido] = useState('')

    const [filtroSituacao, setFiltroSituacao] = useState('TODOS')

    const [mensagem, setMensagem] = useState('')
    const [erro, setErro] = useState('')
    const [carregando, setCarregando] = useState(true)
    const [salvando, setSalvando] = useState(false)
    const [cancelando, setCancelando] = useState(null)

    async function carregarClientes() {
        const resposta = await fetch('/api/clientes')
        if (!resposta.ok) throw new Error('Erro ao carregar clientes.')
        setClientes(await resposta.json())
    }

    async function carregarProdutos() {
        const resposta = await fetch('/api/produtos')
        if (!resposta.ok) throw new Error('Erro ao carregar produtos.')
        setProdutos(await resposta.json())
    }

    async function carregarPedidos(situacao = '') {
        const url = situacao
            ? `/api/pedidos?situacao=${situacao}`
            : '/api/pedidos'

        const resposta = await fetch(url)

        if (!resposta.ok) throw new Error('Erro ao carregar pedidos.')

        setPedidos(await resposta.json())
    }

    async function carregarDados() {
        try {
            setCarregando(true)
            setErro('')

            await Promise.all([
                carregarClientes(),
                carregarProdutos(),
                carregarPedidos()
            ])
        } catch (erro) {
            setErro(erro.message)
        } finally {
            setCarregando(false)
        }
    }

    async function cadastrarCliente(evento) {
        evento.preventDefault()
        setMensagem('')
        setErro('')

        try {
            setSalvando(true)

            const resposta = await fetch('/api/clientes', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ nome, email })
            })

            const dados = await resposta.json().catch(() => null)

            if (!resposta.ok) {
                throw new Error(dados?.mensagem || 'Erro ao cadastrar cliente.')
            }

            setMensagem('Cliente cadastrado com sucesso!')
            setNome('')
            setEmail('')
            await carregarClientes()
        } catch (erro) {
            setErro(erro.message)
        } finally {
            setSalvando(false)
        }
    }

    async function buscarCliente(evento) {
        evento.preventDefault()
        setMensagem('')
        setErro('')
        setClienteEncontrado(null)

        try {
            const resposta = await fetch(`/api/clientes/${idBusca}`)

            if (!resposta.ok) throw new Error('Cliente não encontrado.')

            setClienteEncontrado(await resposta.json())
        } catch (erro) {
            setErro(erro.message)
        }
    }

    async function cadastrarProduto(evento) {
        evento.preventDefault()
        setMensagem('')
        setErro('')

        try {
            setSalvando(true)

            const resposta = await fetch('/api/produtos', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    nome: nomeProduto,
                    preco: Number(precoProduto),
                    estoqueDisponivel: Number(estoqueProduto)
                })
            })

            const dados = await resposta.json().catch(() => null)

            if (!resposta.ok) {
                throw new Error(dados?.mensagem || 'Erro ao cadastrar produto.')
            }

            setMensagem('Produto cadastrado com sucesso!')
            setNomeProduto('')
            setPrecoProduto('')
            setEstoqueProduto('')
            await carregarProdutos()
        } catch (erro) {
            setErro(erro.message)
        } finally {
            setSalvando(false)
        }
    }

    async function buscarProduto(evento) {
        evento.preventDefault()
        setMensagem('')
        setErro('')
        setProdutoEncontrado(null)

        try {
            const resposta = await fetch(`/api/produtos/${idBuscaProduto}`)

            if (!resposta.ok) throw new Error('Produto não encontrado.')

            setProdutoEncontrado(await resposta.json())
        } catch (erro) {
            setErro(erro.message)
        }
    }

    async function cadastrarPedido(evento) {
        evento.preventDefault()
        setMensagem('')
        setErro('')

        try {
            setSalvando(true)

            const resposta = await fetch('/api/pedidos', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    clienteId: Number(clientePedido),
                    produtoId: Number(produtoPedido)
                })
            })

            const dados = await resposta.json().catch(() => null)

            if (!resposta.ok) {
                throw new Error(dados?.mensagem || 'Erro ao registrar pedido.')
            }

            setMensagem('Pedido registrado com sucesso!')
            setClientePedido('')
            setProdutoPedido('')

            await Promise.all([
                carregarPedidos(),
                carregarProdutos()
            ])
        } catch (erro) {
            setErro(erro.message)
        } finally {
            setSalvando(false)
        }
    }

    async function cancelarPedido(id) {
        const confirmar = window.confirm(
            `Tem certeza que deseja cancelar o pedido ${id}?`
        )

        if (!confirmar) return

        setMensagem('')
        setErro('')
        setCancelando(id)

        try {
            const resposta = await fetch(`/api/pedidos/${id}/cancelamento`, {
                method: 'PATCH'
            })

            const dados = await resposta.json().catch(() => null)

            if (!resposta.ok) {
                throw new Error(
                    dados?.mensagem || dados?.message || 'Erro ao cancelar pedido.'
                )
            }

            await Promise.all([
                carregarPedidos(),
                carregarProdutos()
            ])

            setMensagem(
                `Pedido ${id} cancelado com sucesso! O estoque foi atualizado.`
            )
        } catch (erro) {
            setErro(erro.message)
        } finally {
            setCancelando(null)
        }
    }

    const pedidosFiltrados = pedidos.filter(pedido => {
        if (filtroSituacao === 'TODOS') return true
        return pedido.situacao === filtroSituacao
    })

    useEffect(() => {
        carregarDados()
    }, [])

    return (
        <div className="container">
            <header>
                <h1>Sistema de Pedidos - Cantina</h1>
                <p>Gerenciamento de clientes, produtos e pedidos</p>
            </header>

            {mensagem && <p className="mensagem">{mensagem}</p>}
            {erro && <p className="erro">{erro}</p>}

            <div className="grupo-secoes">
                <section>
                    <h2>Cadastrar cliente</h2>

                    <form onSubmit={cadastrarCliente}>
                        <label>Nome</label>
                        <input
                            value={nome}
                            onChange={e => setNome(e.target.value)}
                            placeholder="Digite o nome"
                            required
                        />

                        <label>E-mail</label>
                        <input
                            type="email"
                            value={email}
                            onChange={e => setEmail(e.target.value)}
                            placeholder="Digite o e-mail"
                            required
                        />

                        <button disabled={salvando}>Cadastrar cliente</button>
                    </form>
                </section>

                <section>
                    <h2>Buscar cliente por ID</h2>

                    <form onSubmit={buscarCliente}>
                        <label>ID do cliente</label>
                        <input
                            type="number"
                            min="1"
                            value={idBusca}
                            onChange={e => setIdBusca(e.target.value)}
                            placeholder="Digite o ID"
                            required
                        />

                        <button>Buscar cliente</button>
                    </form>

                    {clienteEncontrado && (
                        <div className="resultado">
                            <p><strong>Nome:</strong> {clienteEncontrado.nome}</p>
                            <p><strong>E-mail:</strong> {clienteEncontrado.email}</p>
                        </div>
                    )}
                </section>
            </div>

            <section>
                <h2>Clientes cadastrados</h2>

                {carregando ? <p>Carregando...</p> : (
                    <table>
                        <thead>
                        <tr>
                            <th>ID</th>
                            <th>Nome</th>
                            <th>E-mail</th>
                        </tr>
                        </thead>
                        <tbody>
                        {clientes.map(cliente => (
                            <tr key={cliente.id}>
                                <td>{cliente.id}</td>
                                <td>{cliente.nome}</td>
                                <td>{cliente.email}</td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                )}
            </section>

            <div className="grupo-secoes">
                <section>
                    <h2>Cadastrar produto</h2>

                    <form onSubmit={cadastrarProduto}>
                        <label>Nome</label>
                        <input
                            value={nomeProduto}
                            onChange={e => setNomeProduto(e.target.value)}
                            placeholder="Nome do produto"
                            required
                        />

                        <label>Preço (R$)</label>
                        <input
                            type="number"
                            min="0"
                            step="0.01"
                            value={precoProduto}
                            onChange={e => setPrecoProduto(e.target.value)}
                            placeholder="Ex.: 6.50"
                            required
                        />

                        <label>Estoque disponível</label>
                        <input
                            type="number"
                            min="0"
                            step="1"
                            value={estoqueProduto}
                            onChange={e => setEstoqueProduto(e.target.value)}
                            placeholder="Quantidade"
                            required
                        />

                        <button disabled={salvando}>Cadastrar produto</button>
                    </form>
                </section>

                <section>
                    <h2>Buscar produto por ID</h2>

                    <form onSubmit={buscarProduto}>
                        <label>ID do produto</label>
                        <input
                            type="number"
                            min="1"
                            value={idBuscaProduto}
                            onChange={e => setIdBuscaProduto(e.target.value)}
                            placeholder="Digite o ID"
                            required
                        />

                        <button>Buscar produto</button>
                    </form>

                    {produtoEncontrado && (
                        <div className="resultado">
                            <p><strong>Nome:</strong> {produtoEncontrado.nome}</p>
                            <p>
                                <strong>Preço:</strong> R$ {Number(produtoEncontrado.preco).toFixed(2)}
                            </p>
                            <p>
                                <strong>Estoque:</strong> {produtoEncontrado.estoqueDisponivel}
                            </p>
                        </div>
                    )}
                </section>
            </div>

            <section>
                <h2>Produtos cadastrados</h2>

                {carregando ? <p>Carregando...</p> : (
                    <table>
                        <thead>
                        <tr>
                            <th>ID</th>
                            <th>Nome</th>
                            <th>Preço</th>
                            <th>Estoque</th>
                        </tr>
                        </thead>
                        <tbody>
                        {produtos.map(produto => (
                            <tr key={produto.id}>
                                <td>{produto.id}</td>
                                <td>{produto.nome}</td>
                                <td>R$ {Number(produto.preco).toFixed(2)}</td>
                                <td>{produto.estoqueDisponivel}</td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                )}
            </section>

            <section>
                <h2>Registrar pedido</h2>

                <form onSubmit={cadastrarPedido}>
                    <label>Cliente</label>
                    <select
                        value={clientePedido}
                        onChange={e => setClientePedido(e.target.value)}
                        required
                    >
                        <option value="">Selecione um cliente</option>
                        {clientes.map(cliente => (
                            <option key={cliente.id} value={cliente.id}>
                                {cliente.nome}
                            </option>
                        ))}
                    </select>

                    <label>Produto</label>
                    <select
                        value={produtoPedido}
                        onChange={e => setProdutoPedido(e.target.value)}
                        required
                    >
                        <option value="">Selecione um produto</option>
                        {produtos.map(produto => (
                            <option
                                key={produto.id}
                                value={produto.id}
                                disabled={produto.estoqueDisponivel <= 0}
                            >
                                {produto.nome} - R$ {Number(produto.preco).toFixed(2)}
                                {produto.estoqueDisponivel <= 0 ? ' (Sem estoque)' : ''}
                            </option>
                        ))}
                    </select>

                    <button disabled={salvando}>
                        {salvando ? 'Registrando...' : 'Registrar pedido'}
                    </button>
                </form>
            </section>

            <section>
                <h2>Pedidos registrados</h2>

                <label>Filtrar pedidos:</label>
                <select
                    value={filtroSituacao}
                    onChange={e => setFiltroSituacao(e.target.value)}
                >
                    <option value="TODOS">Todos os pedidos</option>
                    <option value="ATIVO">Apenas ativos</option>
                    <option value="CANCELADO">Apenas cancelados</option>
                </select>

                {carregando ? <p>Carregando...</p> : (
                    <table>
                        <thead>
                        <tr>
                            <th>ID</th>
                            <th>Cliente</th>
                            <th>Produto</th>
                            <th>Preço</th>
                            <th>Situação</th>
                            <th>Ação</th>
                        </tr>
                        </thead>
                        <tbody>
                        {pedidosFiltrados.map(pedido => (
                            <tr key={pedido.id}>
                                <td>{pedido.id}</td>
                                <td>{pedido.cliente?.nome}</td>
                                <td>{pedido.produto?.nome}</td>
                                <td>
                                    R$ {Number(pedido.precoRegistrado).toFixed(2)}
                                </td>
                                <td>{pedido.situacao}</td>
                                <td>
                                    {pedido.situacao === 'ATIVO' ? (
                                        <button
                                            onClick={() => cancelarPedido(pedido.id)}
                                            disabled={cancelando !== null}
                                        >
                                            {cancelando === pedido.id
                                                ? 'Cancelando...'
                                                : 'Cancelar'}
                                        </button>
                                    ) : (
                                        '—'
                                    )}
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                )}

                {!carregando && pedidosFiltrados.length === 0 && (
                    <p>Nenhum pedido encontrado nessa situação.</p>
                )}
            </section>
        </div>
    )
}

export default App
