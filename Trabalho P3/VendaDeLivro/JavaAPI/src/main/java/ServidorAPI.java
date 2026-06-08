import io.javalin.Javalin;
import controller.CatalogoController;
import controller.EstoqueController;
import controller.VendasController;

public class ServidorAPI {
    public static void main(String[] args) {
        
        // 1. Inicia o servidor Javalin na porta 8080
        Javalin app = Javalin.create(config -> {
            // Permite que seus clientes em Python/C# façam requisições sem erro de CORS
            config.plugins.enableCors(cors -> {
                cors.add(it -> it.anyHost());
            });
        }).start(8080);

        // 2. Mapeamento dos 3 Objetos Distribuídos (Rotas da API)

        // Objeto 1: Catálogo (Para o cliente listar produtos)
        app.get("/produtos", CatalogoController::listarVitrine);

        // Objeto 2: Estoque (Para o admin adicionar/remover do servidor)
        app.post("/estoque", EstoqueController::adicionarLivro);
        app.delete("/estoque/{id}", EstoqueController::removerLivro);

        // Objeto 3: Vendas (Para o cliente fechar o carrinho)
        app.post("/vendas", VendasController::processarCompra);

        System.out.println("Servidor da Livraria iniciado com sucesso!");
    }
}