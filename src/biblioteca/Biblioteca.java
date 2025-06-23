package biblioteca;

import banco.SQLiteConnection;
import banco.dao.LeitorDAO;
import banco.dao.LivroDAO;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

    /*
    Metodos a serem implementados
    Adicionar Livros
    Buscar Livros
    Emprestar Livros
    */
public class Biblioteca {
    private List<Livro> livros;
    private List<Leitor> leitores;
    private List<Emprestimo> emprestimos;
    
    
    public Biblioteca() {
        this.livros = new ArrayList<>();
        this.leitores = new ArrayList<>();
        this.emprestimos = new ArrayList<>();
    }
    
    public void cadastrarLivro (Scanner read) {
        System.out.println("======= CADASTRO DE LIVRO =======");
        System.out.println("Titulo: ");
        String titulo = read.nextLine();
        
        System.out.println("ISBN: ");
        String isbn = read.nextLine();
        
        System.out.println("Quantidade Disponivel: ");
        int qntDisponivel = read.nextInt();
        read.nextLine();
        
        if (qntDisponivel < 0) {
            System.err.println("Quantidade não pode ser negativa!");
            return;
        }
        
        //Cadastro de autores
        List<Autor> autores = new ArrayList<>();
        System.out.println("\nCadastro de Autores (digite 'sair' para finalizar):");
        while(true) {
            System.out.print("Nome do autor: ");
            String nomeAutor = read.nextLine();
            
            if(nomeAutor.equalsIgnoreCase("sair")) {
                break;
            }
            
            System.out.print("Nacionalidade: ");
            String nacionalidade = read.nextLine();
            
            Autor autor = new Autor(0, nomeAutor, nacionalidade);
            autores.add(autor);
        }
        
        if(autores.isEmpty()) {
            System.err.println("O livro deve conter pelo menos um autor!");
            return;
        }
        
        Livro livro = new Livro(titulo, autores, isbn, qntDisponivel);
        livros.add(livro);
        System.out.println("Livro cadastrado com sucesso!");
        
        try {
            LivroDAO livroDAO = new LivroDAO();
            livroDAO.insertLivro(livro);
            System.out.println("\nLivro cadastrado com sucesso!");
        } catch (Exception e) {
            System.err.println("Erro ao cadastrar livro no banco!");
        }
    }
    
    public void mostrarLivros() {
        System.out.println("====== LISTA DE LIVROS ======");
        
        try {
        List<Livro> livros = new LivroDAO().listarTodosLivros(); // Busca do banco
        
        if (livros.isEmpty()) {
            System.err.println("Não há livros cadastrados.");
        } else {
            for (Livro livro : livros) {
                livro.ExibirLivro();
            }
        }
    } catch (SQLException e) {
        System.err.println("Erro ao buscar livros: " + e.getMessage());
    }
}
        
    
    public Livro buscarLivroPorIsbn(String isbn) {
    if (isbn == null) {
        return null;
    }
    
    // Primeiro busca na lista em memória
    if (livros != null) {
        for (Livro livro : livros) {
            if (isbn.equalsIgnoreCase(livro.getIsbn())) {
                return livro;
            }
        }
    }
    
    // Se não encontrou, busca no banco
    try {
        LivroDAO livroDAO = new LivroDAO();
        Livro livro = livroDAO.buscarPorIsbn(isbn);
        
        if (livro != null) {
            // Verifica se já não está na lista antes de adicionar
            if (livros == null) {
                livros = new ArrayList<>();
            }
            
            boolean jaExiste = livros.stream()
                .anyMatch(l -> l.getIsbn().equalsIgnoreCase(isbn));
            
            if (!jaExiste) {
                livros.add(livro);
            }
            return livro;
        }
    } catch (Exception e) {
        System.err.println("Erro ao buscar livro: " + e.getMessage());
    }
    
    return null;
}
    
    //--------------------------------------------------------------------------
    
    public void cadastrarLeitor(Scanner read) {
        System.out.println("====== CADASTRAR USUÁRIO ======");
        System.out.print("Nome: ");
        String nome = read.nextLine();

        try {
        Leitor leitor = new Leitor(nome);
        LeitorDAO leitorDAO = new LeitorDAO();
        int idGerado = leitorDAO.insertLeitor(leitor);
        
        if (idGerado == -1) {
            System.out.println("Falha ao cadastrar leitor no banco de dados.");
        } else {
            leitor.setId(String.valueOf(idGerado));
            System.out.println("Leitor cadastrado com sucesso!");
            System.out.println("ID: " + idGerado);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao conectar com o banco de dados: " + e.getMessage());
                }
        
    }
    
    public Leitor buscarLeitor(String id) {
        for (Leitor leitor : leitores) {
            if (leitor.getId().equals(id)) {
                return leitor;
            }
        }
        return null;
    }
    
    //--------------------------------------------------------------------------
    
    public void emprestarLivro(Scanner read) {
        System.out.println("====== EMPRESTAR LIVRO ======");
        System.out.println("ISBN do livro: ");
        String isbn = read.nextLine();
        System.out.println("ID do leitor: ");
        String idLeitor = read.nextLine();
        
        try {
        // Busca no banco de dados
        LivroDAO livroDAO = new LivroDAO();
        LeitorDAO leitorDAO = new LeitorDAO();
        EmprestimoDAO emprestimoDAO = new EmprestimoDAO();
        
        Livro livro = livroDAO.buscarPorIsbn(isbn);
        Leitor leitor = leitorDAO.buscarPorId(idLeitor);
        
        if (livro == null || leitor == null) {
            System.err.println("Livro ou usuário não encontrado!");
            return;
        }
        
        if (livro.getQntDisponivel() <= 0) {
            System.err.println("Livro indisponível no momento.");
            return;
        }
        
        // Atualiza no banco de dados (transação)
        Connection conn = SQLiteConnection.getConexao();
        try {
            conn.setAutoCommit(false); // Inicia transação
            
            // 1. Atualiza quantidade disponível
            livroDAO.atualizarQuantidadeDisponivel(conn, livro.getId(), livro.getQntDisponivel() - 1);
            
            // 2. Registra empréstimo
            Emprestimo emprestimo = new Emprestimo(livro, leitor, LocalDate.now().toString());
            emprestimoDAO.inserirEmprestimo(conn, emprestimo);
            
            conn.commit(); // Confirma transação
            
            // Atualiza objeto em memória
            livro.setQntDisponivel(livro.getQntDisponivel() - 1);
            emprestimos.add(emprestimo);
            
            System.out.println("Empréstimo realizado com sucesso!");
            
        } catch (SQLException e) {
            try {
                conn.rollback(); // Em caso de erro, desfaz as alterações
            } catch (SQLException ex) {
                System.err.println("Erro ao fazer rollback: " + ex.getMessage());
            }
            System.err.println("Erro ao realizar empréstimo: " + e.getMessage());
        } finally {
            try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException e) {
                System.err.println("Erro ao fechar conexão: " + e.getMessage());
            }
        }
        
    } catch (SQLException e) {
        System.err.println("Erro ao acessar banco de dados: " + e.getMessage());
    }
}
    
    public void devolverLivro(Scanner read) {
        System.out.println("====== DEVOLUÇÃO DE LIVROS ======");
        System.out.print("ISBN do livro: ");
        String isbn = read.nextLine();
        
        Livro livro = buscarLivroPorIsbn(isbn);
        if (livro == null) {
            System.err.println("Livro não encontrado!");
            return;
        }
        
        boolean livroEmprestado = false;
        for (Emprestimo emp : emprestimos) {
            if (emp.getLivro().getIsbn().equals(isbn)) {
                livroEmprestado = true;
                break;
            }
        }
        
        if (!livroEmprestado) {
            System.err.println("Este livro não foi emprestado!");
            return;
        }
        
        livro.setQntDisponivel(livro.getQntDisponivel() + 1);
        System.out.println("Livro devolvido com sucesso!");
    }
    
}
