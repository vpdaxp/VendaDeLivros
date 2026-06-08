package controller;

import io.javalin.http.Context;
import model.LivroFisico;
import model.LivroDigital;
import model.Produto;
import java.util.ArrayList;
import java.util.List;

public class CatalogoController {
    
    // Simulando o nosso banco de dados em memória por enquanto
    private static List<Produto> vitrine = new ArrayList<>();

    // O bloco estático inicializa os dados assim que a classe é chamada
    static {
        vitrine.add(new LivroFisico(1, "O Senhor dos Anéis", "Fantasia Épica", "Aventura", "J.R.R. Tolkien", 120.00, 1.2, 10, "Capa Dura"));
        vitrine.add(new LivroDigital(2, "Clean Code", "Boas práticas de programação", "Tecnologia", "Robert C. Martin", 85.50, "PDF", 5.5, "http://download.exemplo.com"));
    }

    public static void listarVitrine(Context ctx) {
        // O Javalin usa o Gson automaticamente por baixo dos panos!
        // Ele pega a sua List<Produto> do Java e transforma numa string JSON perfeita.
        ctx.json(vitrine);
    }
}