package model;

public abstract class Produto {
    private int id;
    private String titulo;
    private String descricao;
    private String categoria;
    private String autor;
    private double preco;

    public Produto() {
    }

    public Produto(int id, String titulo, String descricao, String categoria, String autor, double preco) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.categoria = categoria;
        this.autor = autor;
        this.preco = preco;
    }

    // Getters e Setters
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

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    @Override
    public String toString() {
        return String.format("ID: %d | Título: %s | Descrição: %s | Categoria: %s | Autor: %s | Preço: R$ %.2f", id, titulo, descricao, categoria, autor, preco);
    }
}
