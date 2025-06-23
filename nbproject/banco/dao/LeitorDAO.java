package banco.dao;

import banco.SQLiteConnection;
import biblioteca.Leitor;
import java.sql.*;

/**
 *
 * @author kaio
 */
public class LeitorDAO {
    private Connection connection;
    
    public LeitorDAO() throws SQLException {
     this.connection = SQLiteConnection.getConexao();
    }
    
    
    public int insertLeitor(Leitor leitor) {
        String sql = "INSERT INTO leitor(nome) VALUES (?)";
        int generatedId = -1;
        
         try {
            // Desativa auto-commit para controle transacional
            connection.setAutoCommit(false);
            
            try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, leitor.getNome());
                int affectedRows = pstmt.executeUpdate();
                
                if (affectedRows == 0) {
                    throw new SQLException("Falha ao criar leitor, nenhuma linha afetada.");
                }
                
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        generatedId = generatedKeys.getInt(1);
                        System.out.println("Leitor cadastrado com sucesso! ID: " + generatedId);
                    } else {
                        throw new SQLException("Falha ao obter o ID gerado.");
                    }
                }
                
                // Confirma a transação
                connection.commit();
            }
        } catch (SQLException e) {
            try {
                // Em caso de erro, faz rollback
                connection.rollback();
            } catch (SQLException ex) {
                System.out.println("Erro ao fazer rollback: " + ex.getMessage());
            }
            System.out.println("Erro ao cadastrar leitor: " + e.getMessage());
            generatedId = -1;
        } finally {
            try {
                // Restaura auto-commit e fecha conexão
                connection.setAutoCommit(true);
                connection.close();
            } catch (SQLException e) {
                System.out.println("Erro ao fechar conexão: " + e.getMessage());
            }
        }
        return generatedId;
    }
}
