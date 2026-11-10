package br.com.avc.avc_cantina.repository;

import br.com.avc.avc_cantina.model.Produto;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ProdutoRepository {

    private final DataSource dataSource;

    public ProdutoRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Produto> findAll() {
        String sql = "SELECT id, nome, preco, estoque_disponivel FROM produtos ORDER BY id ASC";
        Connection con = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Produto> produtos = new ArrayList<>();
            while (rs.next()) {
                produtos.add(mapear(rs));
            }
            return produtos;
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao listar produtos", e);
        } finally {
            DataSourceUtils.releaseConnection(con, dataSource);
        }
    }

    public Optional<Produto> findById(long id) {
        String sql = "SELECT id, nome, preco, estoque_disponivel FROM produtos WHERE id = ?";
        Connection con = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao buscar produto por id", e);
        } finally {
            DataSourceUtils.releaseConnection(con, dataSource);
        }
    }

    public Optional<Produto> buscarPorIdComBloqueio(long id) {
        String sql = "SELECT id, nome, preco, estoque_disponivel FROM produtos WHERE id = ? FOR UPDATE";
        Connection con = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao buscar produto com bloqueio", e);
        } finally {
            DataSourceUtils.releaseConnection(con, dataSource);
        }
    }

    public Produto save(Produto produto) {
        if (produto.getId() == null) {
            return inserir(produto);
        } else {
            atualizar(produto);
            return produto;
        }
    }

    private Produto inserir(Produto produto) {
        String sql = "INSERT INTO produtos (nome, preco, estoque_disponivel) VALUES (?, ?, ?)";
        Connection con = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, produto.getNome());
            ps.setBigDecimal(2, produto.getPreco());
            ps.setInt(3, produto.getEstoqueDisponivel());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    produto.setId(rs.getLong(1));
                }
            }
            return produto;
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao inserir produto", e);
        } finally {
            DataSourceUtils.releaseConnection(con, dataSource);
        }
    }

    private void atualizar(Produto produto) {
        String sql = "UPDATE produtos SET nome = ?, preco = ?, estoque_disponivel = ? WHERE id = ?";
        Connection con = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, produto.getNome());
            ps.setBigDecimal(2, produto.getPreco());
            ps.setInt(3, produto.getEstoqueDisponivel());
            ps.setLong(4, produto.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao atualizar produto", e);
        } finally {
            DataSourceUtils.releaseConnection(con, dataSource);
        }
    }

    public void atualizarEstoque(long id, int novoEstoque) {
        String sql = "UPDATE produtos SET estoque_disponivel = ? WHERE id = ?";
        Connection con = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, novoEstoque);
            ps.setLong(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao atualizar estoque do produto", e);
        } finally {
            DataSourceUtils.releaseConnection(con, dataSource);
        }
    }

    public void deleteById(long id) {
        String sql = "DELETE FROM produtos WHERE id = ?";
        Connection con = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao excluir produto", e);
        } finally {
            DataSourceUtils.releaseConnection(con, dataSource);
        }
    }

    private Produto mapear(ResultSet rs) throws SQLException {
        Produto p = new Produto();
        p.setId(rs.getLong("id"));
        p.setNome(rs.getString("nome"));
        p.setPreco(rs.getBigDecimal("preco"));
        p.setEstoqueDisponivel(rs.getInt("estoque_disponivel"));
        return p;
    }
}