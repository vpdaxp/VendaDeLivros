package controller;

import io.javalin.http.Context;
import service.BancoDeDados;

public class CatalogoController {

    public static void listarVitrine(Context ctx) {
        ctx.json(BancoDeDados.getEstoque());
    }
}