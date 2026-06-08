package controller;

import io.javalin.http.Context;

public class VendasController {
    public static void processarCompra(Context ctx) {
        ctx.json("{ \"mensagem\": \"Rota de finalizar compra funcionando!\" }");
    }
}