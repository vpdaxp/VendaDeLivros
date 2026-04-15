package tcp;

import io.PojoOutputStream;
import model.Produto;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class ClienteVendas {
    private final String HOST = "localhost";
    private final int PORTA = 12345;

    public void comunicarComServidor(List<Produto> lista) {
        try (Socket socket = new Socket(HOST, PORTA)) {
            System.out.println("Conectado ao servidor " + HOST + ":" + PORTA);

            Produto[] arrayProdutos = lista.toArray(new Produto[0]);
            int numObjetos = arrayProdutos.length;

            PojoOutputStream pos = new PojoOutputStream(arrayProdutos, numObjetos, socket.getOutputStream());
            pos.enviarDados();
            System.out.println("Requisição enviada ao servidor...");

            DataInputStream dis = new DataInputStream(socket.getInputStream());
            String respostaServidor = dis.readUTF();

            System.out.println("\n--- RESPOSTA DO SERVIDOR (REPLY) ---");
            System.out.println(respostaServidor);
            System.out.println("------------------------------------");

        } catch (IOException e) {
            System.err.println("Erro na comunicação TCP: " + e.getMessage());
            System.err.println("Certifique-se de que o ServidorVendas está rodando.");
        }
    }
}