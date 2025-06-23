package biblioteca;

import java.util.List;

/**
     Título, autor, ISBN, quantidade disponível.
 */
public class Livro {
    private int id;
    private String titulo;
    private List<Autor> autores;
    private String isbn;
    private int qntDisponivel;

    public Livro(int id, String titulo, List<Autor> autores, String isbn, int qntDisponivel) {
        this.id = id;
        this.titulo = titulo;
        this.autores = autores;
        this.isbn = isbn;
        this.qntDisponivel = qntDisponivel;
    }
    
    //construtor sem id
    public Livro(String titulo, List<Autor> autores, String isbn, 
                 int qntDisponivel) {
        this(0, titulo, autores, isbn, qntDisponivel);
    }

    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public List<Autor> getAutor() {
        return autores;
    }

    public void setAutores(List<Autor> autores) {
        this.autores = autores;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public int getQntDisponivel() {
        return qntDisponivel;
    }

    public void setQntDisponivel(int qntDisponivel) {
        if (qntDisponivel >= 0) {
            this.qntDisponivel = qntDisponivel;
        } else {
            System.err.println("A quantidade não pode ser negativa!");
        }
        
    }
    
    
    public void ExibirLivro() {
        System.out.println("=================================");
        System.out.println("Titulo: " + titulo);
        System.out.println("Autor: " + formatarAutores());
        System.out.println("ISBN: " + isbn);
        System.out.println("Disponiveis: " + qntDisponivel);
        System.out.println("=================================");
        }

       private String formatarAutores() {
        if (autores == null || autores.isEmpty()) {
            return "Nenhum autor cadastrado";
        }
        
        StringBuilder sb = new StringBuilder();
        for (Autor autor : autores) {
            sb.append(autor.getNome()).append(", ");
        }
        return sb.substring(0, sb.length() - 2); // Remove a última vírgula e espaço
    }
}
