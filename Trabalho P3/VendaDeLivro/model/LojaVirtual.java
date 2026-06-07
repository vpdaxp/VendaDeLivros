package model;

import java.util.ArrayList;
import java.util.List;

public class LojaVirtual {
    private String nome;
    private String endereco;
    private String telefone;

    public LojaVirtual() {
    }

    public LojaVirtual(String nome, String endereco, String telefone) {
        this.nome = nome;
        this.endereco = endereco;
        this.telefone = telefone;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
    private List<Produto> pedidosRealizados = new ArrayList<>();

    public void adicionarPedido(Produto p) {
        this.pedidosRealizados.add(p);
    }

    public List<Produto> getPedidosRealizados() {
        return pedidosRealizados;
    }

}