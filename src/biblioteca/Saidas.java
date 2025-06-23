package biblioteca;

import banco.SQLiteConnection;
import banco.dao.LeitorDAO;
import java.util.Scanner;
import java.sql.*;
import java.io.UnsupportedEncodingException;

public class Saidas {

    /*
    Funcionalidades Principais:
Cadastrar Livro

Título, autor, ISBN, quantidade disponível.

Listar Livros

Mostrar todos os livros cadastrados.

Buscar Livro

Por título, autor ou ISBN.

Empréstimo de Livro

Registrar qual usuário pegou o livro e diminuir a quantidade disponível.

Devolução de Livro

Aumentar a quantidade disponível quando o livro é devolvido.

Menu Interativo

Interface no console para o usuário escolher as opções.
    
    Leitor leitor = new Leitor ("1234", "Leonardo");
        leitor.exibirUser();
        
        Livro tonsdecinza = new Livro ("Cinquenta tons de cinza", "E. L. James", "9780345803481", 25);
        tonsdecinza.ExibirLivro();
    */
    public static void main(String[] args) {
        
        System.out.println("Status: " + SQLiteConnection.conectar());
        
        try (Connection conn = SQLiteConnection.getConexao()) {
            System.out.println("Conexão feita com sucesso!");
        } catch (SQLException e) {
            System.err.println("Erro ao se conectar com " + e.getMessage());
        }
    
        Biblioteca biblioteca = new Biblioteca();
        Scanner read = new Scanner(System.in);
        
        try {
           System.setOut(new java.io.PrintStream(System.out, true, "UTF-8"));
       } catch (java.io.UnsupportedEncodingException e) {
           System.out.println("Erro ao configurar UTF-8");
       }
        
        while(true) {
            System.out.println("======= MENU ======");
            System.out.println("1- Cadastrar Livro");
            System.out.println("2- Listar Livros");
            System.out.println("3- Cadastrar usuario");
            System.out.println("4- Emprestar livro");
            System.out.println("5- Devolver livro");
            System.out.println("6- Sair");
            System.out.println("Escolha uma opção: ");
            
            int opcao = read.nextInt();
            read.nextLine();
            
            switch (opcao) {
                case 1:
                    biblioteca.cadastrarLivro(read);
                    break;
                case 2:
                    biblioteca.mostrarLivros();
                    break;
                case 3:
                    biblioteca.cadastrarLeitor(read);
                    break;
                case 4:
                    biblioteca.emprestarLivro(read);
                    break;
                case 5:
                    biblioteca.devolverLivro(read);
                    break;
                case 6:
                    System.out.println("Fechando o sistema...");
                    System.exit(0);
                default:
                    System.err.println("Opção invalida!");
            }
        }
    }
    
}
