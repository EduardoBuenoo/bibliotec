package banco;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author kaio
 */
public class SQLiteConnection {
    
    private static final String nomeBanco = "banco_biblioteca.db";
    private static final String url = "jdbc:sqlite:" + nomeBanco;
    
    public static Connection getConexao() throws SQLException {
        return DriverManager.getConnection(url);
    }
    
    public static String conectar() {
    try(Connection conn = getConexao()) {
    return "Conexão estabelecida com sucesso!";
    } catch (SQLException e) {
    return "Falha na conexão!";
        }
    }
    
}