import io.javalin.Javalin;
import controller.CatalogoController;
import controller.EstoqueController;
import controller.VendasController;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.json.JavalinJackson;

public class ServidorAPI {
    public static void main(String[] args) {
        
        ObjectMapper mapper = new ObjectMapper();
        
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        Javalin app = Javalin.create(config -> {
            config.plugins.enableCors(cors -> {
                cors.add(it -> it.anyHost());
            });
            config.jsonMapper(new JavalinJackson(mapper)); 
        }).start(8080);

        app.get("/produtos", CatalogoController::listarVitrine);

        app.put("/estoque/{id}", EstoqueController::atualizarLivro);

        app.post("/estoque", EstoqueController::adicionarLivro);
        app.delete("/estoque/{id}", EstoqueController::removerLivro);

        app.post("/vendas", VendasController::processarCompra);

        System.out.println("Servidor da Livraria iniciado com sucesso!");
    }
}