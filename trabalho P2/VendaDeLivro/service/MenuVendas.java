package service;

import model.*;
import rmi.MecanismoRMI;
import io.SerializadorManual;

import java.util.Scanner;
import java.util.List;

public class MenuVendas {
    private Carrinho carrinho = new Carrinho(); // Segunda agregação aplicada aqui
    private Scanner scanner = new Scanner(System.in);
    private MecanismoRMI rmi;
    
    private final String SERVER_IP = "localhost";
    private final int SERVER_PORT = 12345;

    public MenuVendas() {
        try {
            this.rmi = new MecanismoRMI();
        } catch (Exception e) {
            System.err.println("Erro ao iniciar subsistema RMI: " + e.getMessage());
        }
    }

    public void iniciar() {
        int opcao = -1;
        while (opcao != 0) {
            System.out.println("\n=== LIVRARIA - ÁREA DO CLIENTE (VIA RMI) ===");
            System.out.println("1. Ver Livros Disponíveis (Vitrine Remota)");
            System.out.println("2. Adicionar ao Carrinho");
            System.out.println("3. GERENCIAR CARRINHO (Ver / Remover / Finalizar)");
            System.out.println("0. Sair");
            System.out.print("Escolha: ");
            opcao = scanner.nextInt();
            scanner.nextLine();

            switch (opcao) {
                case 1: listarDisponiveis(); break;
                case 2: adicionarAoCarrinho(); break;
                case 3: gerenciarCarrinho(); break;
                case 0: System.out.println("Saindo..."); break;
            }
        }
    }

    private List<Produto> buscarEstoqueRemoto() throws Exception {
        byte[] respostaBytes = rmi.doOperation("EstoqueCentral", "listarProdutos", new byte[0], SERVER_IP, SERVER_PORT);
        return SerializadorManual.bytesParaListaProdutos(respostaBytes);
    }

    private void listarDisponiveis() {
        System.out.println("\n--- Vitrine de Livros (Buscando no Servidor...) ---");
        try {
            List<Produto> estoque = buscarEstoqueRemoto();
            if (estoque.isEmpty()) System.out.println("Estoque do servidor vazio.");
            else estoque.forEach(System.out::println);
        } catch (Exception e) {
            System.out.println("Erro ao atualizar vitrine via RMI: " + e.getMessage());
        }
    }

    private void adicionarAoCarrinho() {
        System.out.print("Digite o ID do livro: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        try {
            List<Produto> estoque = buscarEstoqueRemoto();
            Produto p = null;
            for (Produto prod : estoque) {
                if (prod.getId() == id) { p = prod; break; }
            }

            if (p == null) {
                System.out.println("Livro não encontrado no estoque remoto.");
                return;
            }

            if (p instanceof LivroFisico) {
                LivroFisico lf = (LivroFisico) p;
                System.out.println("Disponível no servidor: " + lf.getEstoque());
                System.out.print("Quantidade desejada: ");
                int qtd = scanner.nextInt();
                scanner.nextLine();

                if (qtd > 0 && qtd <= lf.getEstoque()) {
                    LivroFisico itemCarrinho = new LivroFisico(lf.getId(), lf.getTitulo(), lf.getDescricao(), 
                                               lf.getCategoria(), lf.getAutor(), lf.getPreco(), 
                                               lf.getPeso(), qtd, lf.getTipoDeCapa());
                    carrinho.adicionar(itemCarrinho);
                    System.out.println("Adicionado ao carrinho local!");
                } else {
                    System.out.println("Erro: Quantidade indisponível.");
                }
            } else {
                carrinho.adicionar(p);
                System.out.println("Adicionado ao carrinho local!");
            }
        } catch (Exception e) {
            System.out.println("Erro ao validar estoque remoto: " + e.getMessage());
        }
    }

    private void gerenciarCarrinho() {
        if (carrinho.isEmpty()) {
            System.out.println("\n[!] Seu carrinho está vazio.");
            return;
        }

        int subOpcao = -1;
        while (subOpcao != 0) {
            System.out.println("\n--- SEU CARRINHO ---");
            double totalGeral = 0;

            for (Produto p : carrinho.getItens()) {
                double subtotal = p.getPreco();
                String info = "";

                if (p instanceof LivroFisico) {
                    int qtd = ((LivroFisico) p).getEstoque();
                    subtotal = p.getPreco() * qtd;
                    info = String.format(" | Qtd: %d | Subtotal: R$ %.2f", qtd, subtotal);
                }
                System.out.println("- ID: " + p.getId() + " | " + p.getTitulo() + info);
                totalGeral += subtotal;
            }

            System.out.printf("\nTOTAL ATUAL: R$ %.2f\n", totalGeral);
            System.out.println("---------------------------");
            System.out.println("1. Finalizar Compra (Invocação RMI)");
            System.out.println("2. Remover Item do Carrinho");
            System.out.println("0. Voltar ao Menu Principal");
            System.out.print("Escolha: ");
            subOpcao = scanner.nextInt();
            scanner.nextLine();

            if (subOpcao == 1) {
                finalizarVenda();
                subOpcao = 0;
            } else if (subOpcao == 2) {
                removerDoCarrinho();
                if (carrinho.isEmpty()) subOpcao = 0;
            }
        }
    }

    private void removerDoCarrinho() {
        System.out.print("Digite o ID para remover do carrinho: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        Produto itemRemover = null;
        for (Produto p : carrinho.getItens()) {
            if (p.getId() == id) { itemRemover = p; break; }
        }

        if (itemRemover != null) {
            carrinho.remover(itemRemover);
            System.out.println("Item removido do carrinho local.");
        } else {
            System.out.println("Item não encontrado.");
        }
    }

    private void finalizarVenda() {
        System.out.print("Confirmar fechamento e envio remoto? (s/n): ");
        if (scanner.nextLine().equalsIgnoreCase("s")) {
            try {
                // Serialização manual da lista agregada pelo carrinho
                byte[] carrinhoBytes = SerializadorManual.listaProdutosParaBytes(carrinho.getItens());
                
                // Invocação remota do método "processarVenda"
                byte[] respostaBytes = rmi.doOperation("ServicoVendas", "processarVenda", carrinhoBytes, SERVER_IP, SERVER_PORT);
                String reciboServidor = SerializadorManual.bytesParaString(respostaBytes);

                System.out.println("\n--- RECIBO RECEBIDO VIA RMI ---");
                System.out.println(reciboServidor);
                System.out.println("--------------------------------");
                
                carrinho.limpar();
            } catch (Exception e) {
                System.out.println("Erro ao finalizar venda por RMI: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        new MenuVendas().iniciar();
    }
}