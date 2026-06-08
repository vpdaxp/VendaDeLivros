package model;

import java.util.ArrayList;
import java.util.List;

public class Carrinho {
    private List<Produto> itens = new ArrayList<>();

    public void adicionar(Produto p) {
        this.itens.add(p);
    }

    public void remover(Produto p) {
        this.itens.remove(p);
    }

    public List<Produto> getItens() {
        return itens;
    }

    public void limpar() {
        this.itens.clear();
    }

    public boolean isEmpty() {
        return itens.isEmpty();
    }
}