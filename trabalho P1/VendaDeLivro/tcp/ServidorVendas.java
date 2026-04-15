package tcp;

import io.PojoInputStream;
import model.Produto;
import service.Vendas;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class ServidorVendas {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            System.out.println("Servidor de Vendas iniciado na porta 12345...");

            while (true) {
                try (Socket clientSocket = serverSocket.accept()) {
                    System.out.println("\nCliente conectado: " + clientSocket.getInetAddress());

                    PojoInputStream pis = new PojoInputStream(clientSocket.getInputStream());
                    List<Produto> produtosRecebidos = pis.lerDados();

                    Vendas serviceVendas = new Vendas();
                    String reciboConfirmacao = serviceVendas.processarVenda(produtosRecebidos);

                    System.out.println("Processando venda para o cliente...");
                    
                    DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());
                    dos.writeUTF(reciboConfirmacao);
                    dos.flush();
                    
                    System.out.println("Recibo enviado com sucesso.");

                } catch (Exception e) {
                    System.err.println("Erro ao processar cliente: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}