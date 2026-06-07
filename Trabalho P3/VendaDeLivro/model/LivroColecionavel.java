package model;

public class LivroColecionavel extends Produto {
    private String estado;
    private String serie;
    private boolean autrografado;

    public LivroColecionavel() {
    }

    public LivroColecionavel(int id, String titulo, String descricao, String categoria, String autor,double preco, String estado, String serie, boolean autrografado ) {
        super(id, titulo, descricao, categoria,autor, preco);
        this.estado = estado;
        this.serie = serie;
        this.autrografado = autrografado;
    }

    public String getEstado() {
        return estado;
    }
    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public boolean isAutrografado() {
        return autrografado;
    }

    public void setAutrografado(boolean autrografado) {
        this.autrografado = autrografado;
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" | Estado: %s | Série: %s | Autografado: %b", estado, serie, autrografado);
    }
}