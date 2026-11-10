package br.com.avc.avc_cantina.repository;

import br.com.avc.avc_cantina.model.Cliente;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ClienteRepository {

    private final DataSource dataSource;

    public ClienteRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Cliente> findAll() {
        String sql = "SELECT id, nome, email FROM clientes ORDER BY id ASC";
        Connection con = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Cliente> clientes = new ArrayList<>();
            while (rs.next()) {
                clientes.add(mapear(rs));
            }
            return clientes;
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao listar clientes", e);
        } finally {
            DataSourceUtils.releaseConnection(con, dataSource);
        }
    }

    public Optional<Cliente> findById(long id) {
        String sql = "SELECT id, nome, email FROM clientes WHERE id = ?";
        Connection con = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao buscar cliente por id", e);
        } finally {
            DataSourceUtils.releaseConnection(con, dataSource);
        }
    }

    public boolean existsByEmail(String email) {
        String sql = "SELECT 1 FROM clientes WHERE LOWER(email) = LOWER(?) LIMIT 1";
        Connection con = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao verificar email de cliente", e);
        } finally {
            DataSourceUtils.releaseConnection(con, dataSource);
        }
    }

    public boolean existsByEmailAndIdNot(String email, long id) {
        String sql = "SELECT 1 FROM clientes WHERE LOWER(email) = LOWER(?) AND id <> ? LIMIT 1";
        Connection con = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setLong(2, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao verificar duplicidade de email", e);
        } finally {
            DataSourceUtils.releaseConnection(con, dataSource);
        }
    }

    public Cliente save(Cliente cliente) {
        if (cliente.getId() == null) {
            return inserir(cliente);
        } else {
            atualizar(cliente);
            return cliente;
        }
    }

    private Cliente inserir(Cliente cliente) {
        String sql = "INSERT INTO clientes (nome, email) VALUES (?, ?)";
        Connection con = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, cliente.getNome());
            ps.setString(2, cliente.getEmail());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    cliente.setId(rs.getLong(1));
                }
            }
            return cliente;
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao inserir cliente", e);
        } finally {
            DataSourceUtils.releaseConnection(con, dataSource);
        }
    }

    private void atualizar(Cliente cliente) {
        String sql = "UPDATE clientes SET nome = ?, email = ? WHERE id = ?";
        Connection con = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cliente.getNome());
            ps.setString(2, cliente.getEmail());
            ps.setLong(3, cliente.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao atualizar cliente", e);
        } finally {
            DataSourceUtils.releaseConnection(con, dataSource);
        }
    }

    public void deleteById(long id) {
        String sql = "DELETE FROM clientes WHERE id = ?";
        Connection con = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao excluir cliente", e);
        } finally {
            DataSourceUtils.releaseConnection(con, dataSource);
        }
    }

    private Cliente mapear(ResultSet rs) throws SQLException {
        Cliente c = new Cliente();
        c.setId(rs.getLong("id"));
        c.setNome(rs.getString("nome"));
        c.setEmail(rs.getString("email"));
        return c;
    }
}