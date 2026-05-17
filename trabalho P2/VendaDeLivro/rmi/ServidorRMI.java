package rmi;

import model.*;
import io.SerializadorManual;
import io.PojoInputStream;
import io.PojoOutputStream;
import service.Vendas;

import java.io.*;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class ServidorRMI {
    private static LojaVirtual loja = new LojaVirtual("Estoque Central RMI", "UFC Quixadá", "88-RMI");
    private static final String ARQUIVO_ESTOQUE = "estoque.dat";
    private static Vendas serviceVendas = new Vendas();

    public static void main(String[] args) {
        try {
            carregarEstoque();
            MecanismoRMI rmi = new MecanismoRMI(12345);
            System.out.println("Servidor RMI customizado ativo na porta 12345...");

            while (true) {
                try {
                    byte[] rawRequest = rmi.getRequest();
                    MensagemRMI msg = MensagemRMI.fromBytes(rawRequest);
                    
                    String metodo = msg.getMethodId();
                    byte[] argumentos = msg.getArguments();
                    byte[] resultadoBytes = new byte[0];

                    System.out.println("[RMI] Invocação recebida para o método: " + metodo);

                    switch (metodo) {
                        case "listarProdutos":
                            resultadoBytes = SerializadorManual.listaProdutosParaBytes(loja.getPedidosRealizados());
                            break;

                        case "processarVenda":
                            List<Produto> itensCarrinho = SerializadorManual.bytesParaListaProdutos(argumentos);
                            String recibo = serviceVendas.processarVenda(itensCarrinho);
                            
                            // Atualiza o estoque local deduzindo quantidades vendidas
                            for (Produto pCarrinho : itensCarrinho) {
                                for (Produto pEstoque : loja.getPedidosRealizados()) {
                                    if (pEstoque.getId() == pCarrinho.getId() && pEstoque instanceof LivroFisico) {
                                        LivroFisico lfEstoque = (LivroFisico) pEstoque;
                                        LivroFisico lfCarrinho = (LivroFisico) pCarrinho;
                                        lfEstoque.setEstoque(lfEstoque.getEstoque() - lfCarrinho.getEstoque());
                                    }
                                }
                            }
                            salvarEstoque();
                            resultadoBytes = SerializadorManual.stringParaBytes(recibo);
                            break;

                        case "adicionarAoEstoque":
                            Produto novoProd = SerializadorManual.bytesParaProduto(argumentos);
                            if (novoProd != null) {
                                loja.adicionarPedido(novoProd);
                                salvarEstoque();
                                System.out.println("Produto adicionado via RMI: " + novoProd.getTitulo());
                            }
                            break;

                        case "removerDoEstoque":
                            int idParaRemover = SerializadorManual.bytesParaInt(argumentos);
                            loja.getPedidosRealizados().removeIf(p -> p.getId() == idParaRemover);
                            salvarEstoque();
                            System.out.println("Produto ID " + idParaRemover + " removido via RMI.");
                            break;
                        
                        case "editarProduto": // ADICIONE ESTE CASE AQUI
                           Produto prodEditado = SerializadorManual.bytesParaProduto(argumentos);
                           if (prodEditado != null) {
                               List<Produto> listaEstoque = loja.getPedidosRealizados();
                               for (int i = 0; i < listaEstoque.size(); i++) {
                                   if (listaEstoque.get(i).getId() == prodEditado.getId()) {
                                       listaEstoque.set(i, prodEditado);
                                       break;
                                   }
                               }
                               salvarEstoque();
                               System.out.println("Produto ID " + prodEditado.getId() + " editado via RMI.");
                           }
                           break;

                        default:
                            System.err.println("Método não reconhecido: " + metodo);
                    }

                    rmi.sendReply(resultadoBytes, rmi.getLastClientAddress(), rmi.getLastClientPort());
                
                } catch (Exception e) {
                    System.err.println("Erro ao processar requisição remota: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            System.err.println("Falha fatal no Servidor RMI: " + e.getMessage());
        }
    }

    private static void carregarEstoque() {
        File f = new File(ARQUIVO_ESTOQUE);
        if (!f.exists()) return;
        try (FileInputStream fis = new FileInputStream(f)) {
            PojoInputStream pis = new PojoInputStream(fis);
            loja.getPedidosRealizados().addAll(pis.lerDados());
        } catch (IOException e) {
            System.out.println("Nenhum estoque inicial carregado.");
        }
    }

    private static void salvarEstoque() {
        try (FileOutputStream fos = new FileOutputStream(ARQUIVO_ESTOQUE)) {
            List<Produto> prods = loja.getPedidosRealizados();
            PojoOutputStream pos = new PojoOutputStream(prods.toArray(new Produto[0]), prods.size(), fos);
            pos.enviarDados();
        } catch (IOException e) {
            System.err.println("Erro ao salvar arquivo de estoque.");
        }
    }
}