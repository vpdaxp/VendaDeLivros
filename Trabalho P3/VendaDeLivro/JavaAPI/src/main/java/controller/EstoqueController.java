package controller;

import io.javalin.http.Context;

public class EstoqueController {
    public static void adicionarLivro(Context ctx) {
        ctx.json("{ \"mensagem\": \"Rota de adicionar livro funcionando!\" }");
    }
    
    public static void removerLivro(Context ctx) {
        String id = ctx.pathParam("id");
        ctx.json("{ \"mensagem\": \"Rota de remover livro ID " + id + " funcionando!\" }");
    }
}