package service;

import model.*;
import java.util.List;

public class Vendas {
    
    public String processarVenda(List<Produto> produtos) {
        double total = 0;
        int itensFisicos = 0;
        double pesoTotal = 0;

        StringBuilder relatorio = new StringBuilder();
        relatorio.append("--- RECIBO DE VENDA ---\n");

        for (Produto p : produtos) {
            double precoFinal = p.getPreco();
            
            if (p instanceof LivroFisico) {
                LivroFisico lf = (LivroFisico) p;
                double frete = lf.getPeso() * 2.50;
                precoFinal += frete;
                itensFisicos++;
                pesoTotal += lf.getPeso();
                relatorio.append(String.format("- %s (Físico): R$ %.2f (+ frete)\n", p.getTitulo(), precoFinal));
            } 
            else if (p instanceof LivroDigital) {
                relatorio.append(String.format("- %s (Digital): R$ %.2f (Download liberado)\n", p.getTitulo(), precoFinal));
            }
            
            total += precoFinal;
        }

        relatorio.append("-----------------------\n");
        relatorio.append(String.format("TOTAL A PAGAR: R$ %.2f\n", total));
        if (itensFisicos > 0) {
            relatorio.append(String.format("Peso total de envio: %.2f kg\n", pesoTotal));
        }

        return relatorio.toString();
    }
}