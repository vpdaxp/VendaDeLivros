package service;

import model.*;
import io.*;
import tcp.ClienteVendas;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.*;

public class MenuVendas {
    private List<Produto> estoqueLocal = new ArrayList<>();
    private List<Produto> carrinho = new ArrayList<>();
    private Scanner scanner = new Scanner(System.in);
    private final String ARQUIVO_ESTOQUE = "estoque.dat";

    public void iniciar() {
        carregarEstoque();
        
        int opcao = -1;
        while (opcao != 0) {
            System.out.println("\n=== LIVRARIA - ÁREA DO CLIENTE ===");
            System.out.println("1. Ver Livros Disponíveis");
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

    private void listarDisponiveis() {
        System.out.println("\n--- Vitrine de Livros ---");
        if (estoqueLocal.isEmpty()) {
            System.out.println("Estoque vazio.");
        } else {
            estoqueLocal.forEach(System.out::println);
        }
    }

    private void adicionarAoCarrinho() {
        System.out.print("Digite o ID do livro: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        Produto p = buscarNoEstoque(id);
        if (p == null) {
            System.out.println("Livro não encontrado.");
            return;
        }

        if (p instanceof LivroFisico) {
            LivroFisico lf = (LivroFisico) p;
            System.out.println("Disponível: " + lf.getEstoque());
            System.out.print("Quantidade desejada: ");
            int qtd = scanner.nextInt();
            scanner.nextLine();

            if (qtd > 0 && qtd <= lf.getEstoque()) {
                LivroFisico itemCarrinho = new LivroFisico(lf.getId(), lf.getTitulo(), lf.getDescricao(), 
                                           lf.getCategoria(), lf.getAutor(), lf.getPreco(), 
                                           lf.getPeso(), qtd, lf.getTipoDeCapa());
                carrinho.add(itemCarrinho);
                lf.setEstoque(lf.getEstoque() - qtd);
                System.out.println("Adicionado ao carrinho!");
            } else {
                System.out.println("Erro: Quantidade indisponível.");
            }
        } else {
            carrinho.add(p);
            System.out.println("Adicionado ao carrinho!");
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

            for (Produto p : carrinho) {
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
            System.out.println("1. Finalizar Compra (Enviar ao Servidor)");
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
        System.out.print("Digite o ID para remover: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        Produto itemRemover = null;
        for (Produto p : carrinho) {
            if (p.getId() == id) {
                itemRemover = p;
                break;
            }
        }

        if (itemRemover != null) {
            if (itemRemover instanceof LivroFisico) {
                LivroFisico lfCarrinho = (LivroFisico) itemRemover;
                Produto pEstoque = buscarNoEstoque(id);
                if (pEstoque instanceof LivroFisico) {
                    ((LivroFisico) pEstoque).setEstoque(((LivroFisico) pEstoque).getEstoque() + lfCarrinho.getEstoque());
                }
            }
            carrinho.remove(itemRemover);
            System.out.println("Item removido com sucesso.");
        } else {
            System.out.println("Item não encontrado no carrinho.");
        }
    }

    private void finalizarVenda() {
        System.out.print("Confirmar envio ao servidor? (s/n): ");
        if (scanner.nextLine().equalsIgnoreCase("s")) {
            salvarEstoqueAtualizado();
            
            new ClienteVendas().comunicarComServidor(carrinho); 
            
            carrinho.clear();
            System.out.println("Venda concluída e enviada!");
        }
    }

    private Produto buscarNoEstoque(int id) {
        for (Produto p : estoqueLocal) {
            if (p.getId() == id) return p;
        }
        return null;
    }

    private void carregarEstoque() {
        File f = new File(ARQUIVO_ESTOQUE);
        if (!f.exists()) return;
        try (FileInputStream fis = new FileInputStream(f)) {
            PojoInputStream pis = new PojoInputStream(fis);
            estoqueLocal = pis.lerDados();
        } catch (IOException e) {
            System.out.println("Erro ao ler arquivo de estoque.");
        }
    }

    private void salvarEstoqueAtualizado() {
        try (FileOutputStream fos = new FileOutputStream(ARQUIVO_ESTOQUE)) {
            PojoOutputStream pos = new PojoOutputStream(estoqueLocal.toArray(new Produto[0]), estoqueLocal.size(), fos);
            pos.enviarDados();
        } catch (IOException e) {
            System.err.println("Erro ao atualizar estoque local.");
        }
    }

    public static void main(String[] args) {
        new MenuVendas().iniciar();
    }
}