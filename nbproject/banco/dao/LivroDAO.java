package banco.dao;

import biblioteca.Livro;
import biblioteca.Autor;
import banco.SQLiteConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LivroDAO {
    
    public void insertLivro(Livro livro) throws SQLException {
        String sql = "INSERT INTO livro(titulo, isbn, quantidade_disponivel) VALUES (?, ?, ?)";
        
        try (Connection conn = SQLiteConnection.getConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, livro.getTitulo());
            pstmt.setString(2, livro.getIsbn());
            pstmt.setInt(3, livro.getQntDisponivel());
            pstmt.executeUpdate();
            
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    livro.setId(rs.getInt(1));
                }
            }
            
            // Cadastrar os autores
            cadastrarAutores(livro);
        }
    }
    
    public List<Livro> listarTodosLivros() throws SQLException {
    List<Livro> livros = new ArrayList<>();
    String sql = "SELECT * FROM livro";
    
    try (Connection conn = SQLiteConnection.getConexao();
         PreparedStatement pstmt = conn.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {
        
        while (rs.next()) {
            Livro livro = new Livro(
                rs.getInt("id"),
                rs.getString("titulo"),
                buscarAutoresDoLivro(conn, rs.getInt("id")),
                rs.getString("isbn"),
                rs.getInt("quantidade_disponivel")
            );
            livros.add(livro);
        }
    }
    return livros;
}
    
    private void cadastrarAutores(Livro livro) throws SQLException {
        String sql = "INSERT INTO livro_autor(livro_id, autor_id) VALUES (?, ?)";
        
        try (Connection conn = SQLiteConnection.getConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            for (Autor autor : livro.getAutor()) {
                // Primeiro insere o autor se não existir
                int autorId = inserirAutorSeNaoExistir(conn, autor);
                
                pstmt.setInt(1, livro.getId());
                pstmt.setInt(2, autorId);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }
    
    private int inserirAutorSeNaoExistir(Connection conn, Autor autor) throws SQLException {
        // Verifica se o autor já existe
        String sqlBusca = "SELECT id FROM autor WHERE nome = ? AND nacionalidade = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sqlBusca)) {
            pstmt.setString(1, autor.getNome());
            pstmt.setString(2, autor.getNacionalidade());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        
        // Se não existir, insere
        String sqlInsere = "INSERT INTO autor(nome, nacionalidade) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sqlInsere, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, autor.getNome());
            pstmt.setString(2, autor.getNacionalidade());
            pstmt.executeUpdate();
            
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("Falha ao inserir autor");
    }
    
    public Livro buscarPorIsbn(String isbn) throws SQLException {
        String sql = "SELECT * FROM livro WHERE isbn = ?";
        
        try (Connection conn = SQLiteConnection.getConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, isbn);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Livro livro = new Livro(
                        rs.getString("titulo"),
                        buscarAutoresDoLivro(conn, rs.getInt("id")),
                        rs.getString("isbn"),
                        rs.getInt("quantidade_disponivel")
                    );
                    livro.setId(rs.getInt("id"));
                    return livro;
                }
            }
        }
        return null;
    }
    
    private List<Autor> buscarAutoresDoLivro(Connection conn, int livroId) throws SQLException {
        List<Autor> autores = new ArrayList<>();
        String sql = "SELECT a.* FROM autor a " +
                     "JOIN livro_autor la ON a.id = la.autor_id " +
                     "WHERE la.livro_id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, livroId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    autores.add(new Autor(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("nacionalidade")
                    ));
                }
            }
        }
        return autores;
    }
    
    public void atualizarQuantidadeDisponivel(Connection conn, int livroId, int novaQuantidade) throws SQLException {
    String sql = "UPDATE livro SET quantidade_disponivel = ? WHERE id = ?";
    
    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setInt(1, novaQuantidade);
        pstmt.setInt(2, livroId);
        pstmt.executeUpdate();
        }
    }
}