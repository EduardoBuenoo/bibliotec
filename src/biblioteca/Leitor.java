package biblioteca;

public class Leitor {
    private String id;
    private String nome;
    
    public Leitor (String nome) {
        this.nome = nome;
    }
    
    public Leitor( String id, String nome) {
        this.id = id;
        this.nome = nome;
    }
    
    

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
    
    /*
    public void exibirUser() {
        System.out.println("=================================");
        System.out.println("ID: " + id);
        System.out.println("Nome: " + nome);
        
    }
    */
}
