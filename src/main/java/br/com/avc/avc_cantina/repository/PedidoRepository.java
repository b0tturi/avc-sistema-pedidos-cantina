package br.com.avc.avc_cantina.repository;

import br.com.avc.avc_cantina.model.Cliente;
import br.com.avc.avc_cantina.model.Pedido;
import br.com.avc.avc_cantina.model.Produto;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class PedidoRepository {

    private final DataSource dataSource;

    public PedidoRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Pedido> findAll() {
        String sql = "SELECT p.id, p.preco_registrado, p.data_registro, p.situacao, " +
                     "c.id AS cliente_id, c.nome AS cliente_nome, c.email AS cliente_email, " +
                     "pr.id AS produto_id, pr.nome AS produto_nome, pr.preco AS produto_preco, pr.estoque_disponivel AS produto_estoque " +
                     "FROM pedidos p " +
                     "INNER JOIN clientes c ON c.id = p.cliente_id " +
                     "INNER JOIN produtos pr ON pr.id = p.produto_id " +
                     "ORDER BY p.id ASC";
        Connection con = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Pedido> pedidos = new ArrayList<>();
            while (rs.next()) {
                pedidos.add(mapear(rs));
            }
            return pedidos;
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao listar pedidos", e);
        } finally {
            DataSourceUtils.releaseConnection(con, dataSource);
        }
    }

    public List<Pedido> findBySituacao(String situacao) {
        String sql = "SELECT p.id, p.preco_registrado, p.data_registro, p.situacao, " +
                     "c.id AS cliente_id, c.nome AS cliente_nome, c.email AS cliente_email, " +
                     "pr.id AS produto_id, pr.nome AS produto_nome, pr.preco AS produto_preco, pr.estoque_disponivel AS produto_estoque " +
                     "FROM pedidos p " +
                     "INNER JOIN clientes c ON c.id = p.cliente_id " +
                     "INNER JOIN produtos pr ON pr.id = p.produto_id " +
                     "WHERE p.situacao = ? " +
                     "ORDER BY p.id ASC";
        Connection con = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, situacao);
            try (ResultSet rs = ps.executeQuery()) {
                List<Pedido> pedidos = new ArrayList<>();
                while (rs.next()) {
                    pedidos.add(mapear(rs));
                }
                return pedidos;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao listar pedidos por situação", e);
        } finally {
            DataSourceUtils.releaseConnection(con, dataSource);
        }
    }

    public Optional<Pedido> findById(long id) {
        String sql = "SELECT p.id, p.preco_registrado, p.data_registro, p.situacao, " +
                     "c.id AS cliente_id, c.nome AS cliente_nome, c.email AS cliente_email, " +
                     "pr.id AS produto_id, pr.nome AS produto_nome, pr.preco AS produto_preco, pr.estoque_disponivel AS produto_estoque " +
                     "FROM pedidos p " +
                     "INNER JOIN clientes c ON c.id = p.cliente_id " +
                     "INNER JOIN produtos pr ON pr.id = p.produto_id " +
                     "WHERE p.id = ?";
        Connection con = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao buscar pedido por id", e);
        } finally {
            DataSourceUtils.releaseConnection(con, dataSource);
        }
    }

    public Optional<Pedido> buscarPorIdComBloqueio(long id) {
        String sql = "SELECT p.id, p.preco_registrado, p.data_registro, p.situacao, " +
                     "c.id AS cliente_id, c.nome AS cliente_nome, c.email AS cliente_email, " +
                     "pr.id AS produto_id, pr.nome AS produto_nome, pr.preco AS produto_preco, pr.estoque_disponivel AS produto_estoque " +
                     "FROM pedidos p " +
                     "INNER JOIN clientes c ON c.id = p.cliente_id " +
                     "INNER JOIN produtos pr ON pr.id = p.produto_id " +
                     "WHERE p.id = ? FOR UPDATE";
        Connection con = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao buscar pedido com bloqueio", e);
        } finally {
            DataSourceUtils.releaseConnection(con, dataSource);
        }
    }

    public boolean existsByProdutoId(long produtoId) {
        String sql = "SELECT 1 FROM pedidos WHERE produto_id = ? LIMIT 1";
        Connection con = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, produtoId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao verificar pedidos para o produto", e);
        } finally {
            DataSourceUtils.releaseConnection(con, dataSource);
        }
    }

    public Pedido save(Pedido pedido) {
        if (pedido.getId() == null) {
            return inserir(pedido);
        } else {
            atualizar(pedido);
            return pedido;
        }
    }

    private Pedido inserir(Pedido pedido) {
        String sql = "INSERT INTO pedidos (cliente_id, produto_id, preco_registrado, data_registro, situacao) " +
                     "VALUES (?, ?, ?, ?, ?)";
        Connection con = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, pedido.getCliente().getId());
            ps.setLong(2, pedido.getProduto().getId());
            ps.setBigDecimal(3, pedido.getPrecoRegistrado());
            ps.setTimestamp(4, Timestamp.valueOf(pedido.getDataRegistro()));
            ps.setString(5, pedido.getSituacao() != null ? pedido.getSituacao() : "ATIVO");
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    pedido.setId(rs.getLong(1));
                }
            }
            return pedido;
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao inserir pedido", e);
        } finally {
            DataSourceUtils.releaseConnection(con, dataSource);
        }
    }

    private void atualizar(Pedido pedido) {
        String sql = "UPDATE pedidos SET situacao = ? WHERE id = ?";
        Connection con = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, pedido.getSituacao());
            ps.setLong(2, pedido.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao atualizar pedido", e);
        } finally {
            DataSourceUtils.releaseConnection(con, dataSource);
        }
    }

    public void atualizarSituacao(long id, String situacao) {
        String sql = "UPDATE pedidos SET situacao = ? WHERE id = ?";
        Connection con = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, situacao);
            ps.setLong(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao atualizar situação do pedido", e);
        } finally {
            DataSourceUtils.releaseConnection(con, dataSource);
        }
    }

    private Pedido mapear(ResultSet rs) throws SQLException {
        Cliente c = new Cliente();
        c.setId(rs.getLong("cliente_id"));
        c.setNome(rs.getString("cliente_nome"));
        c.setEmail(rs.getString("cliente_email"));

        Produto pr = new Produto();
        pr.setId(rs.getLong("produto_id"));
        pr.setNome(rs.getString("produto_nome"));
        pr.setPreco(rs.getBigDecimal("produto_preco"));
        pr.setEstoqueDisponivel(rs.getInt("produto_estoque"));

        Pedido p = new Pedido();
        p.setId(rs.getLong("id"));
        p.setCliente(c);
        p.setProduto(pr);
        p.setPrecoRegistrado(rs.getBigDecimal("preco_registrado"));
        Timestamp ts = rs.getTimestamp("data_registro");
        if (ts != null) {
            p.setDataRegistro(ts.toLocalDateTime());
        }
        p.setSituacao(rs.getString("situacao"));
        return p;
    }
}