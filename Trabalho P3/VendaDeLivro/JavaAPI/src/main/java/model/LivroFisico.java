package model;

public class LivroFisico extends Produto {
    private String tipoDeCapa;
    private double peso;
    private int estoque;

    public LivroFisico() {
    }

    public LivroFisico(int id, String titulo, String descricao, String categoria, String autor, double preco, double peso, int estoque, String tipoDeCapa) {
        super(id, titulo, descricao, categoria, autor, preco);
        this.peso = peso;
        this.estoque = estoque;
        this.tipoDeCapa = tipoDeCapa;
    }

    public String getTipoDeCapa() {
        return tipoDeCapa;
    }

    public void setTipoDeCapa(String tipoDeCapa) {
        this.tipoDeCapa = tipoDeCapa;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public int getEstoque() {
        return estoque;
    }

    public void setEstoque(int estoque) {
        this.estoque = estoque;
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" | Tipo de Capa: %s | Peso: %.2f kg | Estoque: %d", tipoDeCapa, peso, estoque);
    }
}