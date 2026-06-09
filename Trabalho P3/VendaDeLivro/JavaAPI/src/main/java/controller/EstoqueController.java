package controller;

import io.javalin.http.Context;
import model.Produto;
import service.BancoDeDados;

public class EstoqueController {
    
    public static void adicionarLivro(Context ctx) {
        try {
            Produto novoLivro = ctx.bodyAsClass(Produto.class);
            
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
        
        BancoDeDados.remover(id);
        
        System.out.println("Livro ID " + id + " removido.");
        ctx.json("{ \"mensagem\": \"Livro ID " + id + " removido do estoque!\" }");
    }

    public static void atualizarLivro(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Produto livroAtualizado = ctx.bodyAsClass(Produto.class);
            
            BancoDeDados.atualizar(id, livroAtualizado);
            
            System.out.println("Livro ID " + id + " atualizado.");
            ctx.status(200);
            ctx.json("{ \"mensagem\": \"Livro ID " + id + " atualizado com sucesso!\" }");
        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(400);
            ctx.json("{ \"erro\": \"Formato inválido ao atualizar.\" }");
        }
    }

}