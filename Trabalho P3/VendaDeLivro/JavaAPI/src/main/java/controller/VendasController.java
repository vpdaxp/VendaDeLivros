package controller;

import io.javalin.http.Context;
import model.Produto;
import model.LivroFisico;
import service.BancoDeDados;
import service.Vendas;
import java.util.Arrays;
import java.util.List;

public class VendasController {
    
    public static void processarCompra(Context ctx) {
        try {
            Produto[] arrayCarrinho = ctx.bodyAsClass(Produto[].class);
            List<Produto> itensCarrinho = Arrays.asList(arrayCarrinho);
            
            Vendas serviceVendas = new Vendas();
            String recibo = serviceVendas.processarVenda(itensCarrinho);
            
            List<Produto> estoqueServidor = BancoDeDados.getEstoque();
            
            for (Produto pCarrinho : itensCarrinho) {
                for (Produto pEstoque : estoqueServidor) {
                    if (pEstoque.getId() == pCarrinho.getId() && pEstoque instanceof LivroFisico) {
                        LivroFisico lfEstoque = (LivroFisico) pEstoque;
                        LivroFisico lfCarrinho = (LivroFisico) pCarrinho;
                        
                        lfEstoque.setEstoque(lfEstoque.getEstoque() - lfCarrinho.getEstoque());
                    }
                }
            }
            
            BancoDeDados.salvarNoArquivo();
            
            ctx.status(200);
            ctx.json("{ \"recibo\": \"" + recibo.replace("\n", "\\n") + "\" }");
            
        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(400);
            ctx.json("{ \"erro\": \"Falha ao processar a venda. Verifique o JSON do carrinho.\" }");
        }
    }
}