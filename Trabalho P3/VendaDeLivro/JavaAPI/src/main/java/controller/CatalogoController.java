package controller;

import io.javalin.http.Context;
import service.BancoDeDados; // Importando o nosso banco

public class CatalogoController {

    public static void listarVitrine(Context ctx) {
        // Pega a lista oficial (que agora tem os livros adicionados via POST)
        ctx.json(BancoDeDados.getEstoque());
    }
}