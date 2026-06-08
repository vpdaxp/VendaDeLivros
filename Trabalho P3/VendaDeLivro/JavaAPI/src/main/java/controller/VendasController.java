package controller;

import io.javalin.http.Context;
import model.Produto;
import model.LivroFisico;
import service.BancoDeDados;
import service.Vendas; // Importando a sua classe de regras de negócio
import java.util.Arrays;
import java.util.List;

public class VendasController {
    
    public static void processarCompra(Context ctx) {
        try {
            // 1. O Javalin converte o JSON do carrinho em um Array de Produtos
            Produto[] arrayCarrinho = ctx.bodyAsClass(Produto[].class);
            List<Produto> itensCarrinho = Arrays.asList(arrayCarrinho);
            
            // 2. Chama a sua classe que já está pronta para gerar o recibo
            // (Ajuste essa linha conforme o nome real do seu método na classe Vendas)
            Vendas serviceVendas = new Vendas();
            String recibo = serviceVendas.processarVenda(itensCarrinho);
            
            // 3. Lógica de dar baixa no estoque do servidor
            List<Produto> estoqueServidor = BancoDeDados.getEstoque();
            
            for (Produto pCarrinho : itensCarrinho) {
                for (Produto pEstoque : estoqueServidor) {
                    if (pEstoque.getId() == pCarrinho.getId() && pEstoque instanceof LivroFisico) {
                        LivroFisico lfEstoque = (LivroFisico) pEstoque;
                        LivroFisico lfCarrinho = (LivroFisico) pCarrinho;
                        
                        // Subtrai do estoque a quantidade comprada
                        lfEstoque.setEstoque(lfEstoque.getEstoque() - lfCarrinho.getEstoque());
                    }
                }
            }
            
            // 4. Salva a alteração física no arquivo estoque.json
            BancoDeDados.salvarNoArquivo();
            
            // 5. Devolve o recibo formatado para o cliente
            ctx.status(200);
            ctx.json("{ \"recibo\": \"" + recibo.replace("\n", "\\n") + "\" }");
            
        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(400);
            ctx.json("{ \"erro\": \"Falha ao processar a venda. Verifique o JSON do carrinho.\" }");
        }
    }
}