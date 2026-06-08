package model;

public class LivroDigital extends Produto {
    private String formato;
    private double tamanho;
    private String linkDownload;

    public LivroDigital() {
    }

    public LivroDigital(int id, String titulo, String descricao, String categoria, String autor, double preco, String formato, double tamanho, String linkDownload) {
        super(id, titulo, descricao, categoria, autor, preco);
        this.formato = formato;
        this.tamanho = tamanho;
        this.linkDownload = linkDownload;
    }

    public double getTamanho() {
        return tamanho;
    }

    public void setTamanho(double tamanho) {
        this.tamanho = tamanho;
    }

    public String getLinkDownload() {
        return linkDownload;
    }
    
    public void setLinkDownload(String linkDownload) {
        this.linkDownload = linkDownload;
    }
    public String getFormato() {
        return formato;
    }

    public void setFormato(String formato) {
        this.formato = formato;
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" | Formato: %s | Tamanho: %.2f MB | Link de Download: %s", formato, tamanho, linkDownload);
    }
}