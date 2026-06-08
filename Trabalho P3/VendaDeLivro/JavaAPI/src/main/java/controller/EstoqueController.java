package controller;

import io.javalin.http.Context;
import model.Produto;
import service.BancoDeDados; // Importando o nosso banco

public class EstoqueController {
    
    public static void adicionarLivro(Context ctx) {
        try {
            Produto novoLivro = ctx.bodyAsClass(Produto.class);
            
            // AGORA SIM! Salvando na lista central e no arquivo
            BancoDeDados.adicionar(novoLivro); 
            
            System.out.println("Novo livro salvo no JSON: " + novoLivro.getTitulo());
            
            ctx.status(201);
            ctx.json("{ \"mensagem\": \"Livro '" + novoLivro.getTitulo() + "' cadastrado com sucesso!\" }");
            
        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(400);
            ctx.json("{ \"erro\": \"Formato de JSON inválido para Produto.\" }");
        }
    }
    
    public static void removerLivro(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        
        // Remove da lista central e atualiza o arquivo
        BancoDeDados.remover(id);
        
        System.out.println("Livro ID " + id + " removido.");
        ctx.json("{ \"mensagem\": \"Livro ID " + id + " removido do estoque!\" }");
    }
}