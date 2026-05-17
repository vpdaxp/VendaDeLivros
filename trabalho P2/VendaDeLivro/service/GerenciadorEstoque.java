package service;

import model.*;
import rmi.MecanismoRMI;
import io.SerializadorManual;

import java.util.Scanner;
import java.util.List;

public class GerenciadorEstoque {
    private Scanner scanner;
    private MecanismoRMI rmi;
    
    private final String SERVER_IP = "localhost";
    private final int SERVER_PORT = 12345;

    public GerenciadorEstoque() {
        this.scanner = new Scanner(System.in);
        try {
            this.rmi = new MecanismoRMI();
        } catch (Exception e) {
            System.err.println("Erro ao iniciar subsistema RMI: " + e.getMessage());
        }
    }

    public void exibirMenu() {
        int opcao = -1;
        while (opcao != 0) {
            System.out.println("\n=== GERENCIAMENTO DE ESTOQUE REMOTO (RMI) ===");
            System.out.println("1. Cadastrar Novo Livro");
            System.out.println("2. Consultar Inventário");
            System.out.println("3. EDITAR Livro Existente");
            System.out.println("4. Remover Livro");
            System.out.println("0. Sair");
            System.out.print("Escolha: ");
            opcao = scanner.nextInt();
            scanner.nextLine();

            switch (opcao) {
                case 1: adicionarAoEstoque(); break;
                case 2: listarEstoque(); break;
                case 3: editarLivro(); break;
                case 4: removerDoEstoque(); break;
                case 0: System.out.println("Encerrando..."); break;
                default: System.out.println("Opção inválida!");
            }
        }
    }

    private List<Produto> obterEstoqueRemoto() throws Exception {
        byte[] respostaBytes = rmi.doOperation("EstoqueCentral", "listarProdutos", new byte[0], SERVER_IP, SERVER_PORT);
        return SerializadorManual.bytesParaListaProdutos(respostaBytes);
    }

    private void listarEstoque() {
        System.out.println("\n--- Inventário Remoto ---");
        try {
            List<Produto> estoque = obterEstoqueRemoto();
            if (estoque.isEmpty()) System.out.println("Estoque vazio no servidor.");
            else estoque.forEach(System.out::println);
        } catch (Exception e) {
            System.out.println("Erro ao listar estoque via RMI: " + e.getMessage());
        }
    }

    private void adicionarAoEstoque() {
        try {
            // Obtém o estoque remoto para calcular o próximo ID incremental único de forma distribuída
            List<Produto> estoque = obterEstoqueRemoto();
            int proximoId = 1;
            for (Produto prod : estoque) {
                if (prod.getId() >= proximoId) {
                    proximoId = prod.getId() + 1;
                }
            }

            System.out.println("\n--- Cadastro Remoto (ID gerado: " + proximoId + ") ---");
            System.out.print("Título: "); String t = scanner.nextLine();
            System.out.print("Autor: "); String a = scanner.nextLine();
            System.out.print("Preço(Preço separado por vírgula) : "); double p = scanner.nextDouble();
            System.out.println("Tipo: [1] Físico | [2] Digital | [3] Colecionável");
            int tipo = scanner.nextInt(); scanner.nextLine();

            Produto novo = null;
            if (tipo == 1) {
                System.out.print("Peso(Peso separado por vírgula): "); double pe = scanner.nextDouble();
                System.out.print("Qtd: "); int q = scanner.nextInt(); scanner.nextLine();
                System.out.print("Capa: "); String c = scanner.nextLine();
                novo = new LivroFisico(proximoId, t, "Fisico", "Geral", a, p, pe, q, c);
            } else if (tipo == 2) {
                System.out.print("Formato: "); String f = scanner.nextLine();
                System.out.print("Tamanho(Tamanho separado por vírgula): "); double ta = scanner.nextDouble(); scanner.nextLine();
                System.out.print("Link: "); String l = scanner.nextLine();
                novo = new LivroDigital(proximoId, t, "Digital", "E-book", a, p, f, ta, l);
            } else if (tipo == 3) {
                System.out.print("Estado: "); String es = scanner.nextLine();
                System.out.print("Série: "); String s = scanner.nextLine();
                System.out.print("Autografado (true/false): "); boolean au = scanner.nextBoolean();
                novo = new LivroColecionavel(proximoId, t, "Raro", "Coleção", a, p, es, s, au);
            }

            if (novo != null) {
                byte[] prodBytes = SerializadorManual.produtoParaBytes(novo);
                rmi.doOperation("EstoqueCentral", "adicionarAoEstoque", prodBytes, SERVER_IP, SERVER_PORT);
                System.out.println("Cadastrado com sucesso no servidor!");
            }
        } catch (Exception e) {
            System.out.println("Erro ao adicionar produto via RMI: " + e.getMessage());
        }
    }

    private void removerDoEstoque() {
        System.out.print("ID para remover: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        try {
            byte[] idBytes = SerializadorManual.intParaBytes(id);
            rmi.doOperation("EstoqueCentral", "removerDoEstoque", idBytes, SERVER_IP, SERVER_PORT);
            System.out.println("Removido com sucesso no servidor.");
        } catch (Exception e) {
            System.out.println("Erro ao remover produto via RMI: " + e.getMessage());
        }
    }

    private void editarLivro() {
        System.out.print("Informe o ID do livro que deseja editar: ");
        String idInput = scanner.nextLine();
        if (idInput.isEmpty()) return;
        
        int id;
        try {
            id = Integer.parseInt(idInput);
        } catch (NumberFormatException e) {
            System.out.println("Erro: ID inválido.");
            return;
        }

        try {
            List<Produto> estoque = obterEstoqueRemoto();
            Produto p = null;
            for (Produto item : estoque) {
                if (item.getId() == id) {
                    p = item;
                    break;
                }
            }

            if (p == null) {
                System.out.println("Erro: ID não encontrado no servidor.");
                return;
            }

            System.out.println("\n--- Edição Remota (Pressione ENTER para manter o valor atual) ---");

            System.out.print("Novo Título [" + p.getTitulo() + "]: ");
            String titulo = scanner.nextLine();
            if (!titulo.trim().isEmpty()) p.setTitulo(titulo);

            System.out.print("Nova Descrição [" + p.getDescricao() + "]: ");
            String desc = scanner.nextLine();
            if (!desc.trim().isEmpty()) p.setDescricao(desc);

            System.out.print("Nova Categoria [" + p.getCategoria() + "]: ");
            String cat = scanner.nextLine();
            if (!cat.trim().isEmpty()) p.setCategoria(cat);

            System.out.print("Novo Autor [" + p.getAutor() + "]: ");
            String autor = scanner.nextLine();
            if (!autor.trim().isEmpty()) p.setAutor(autor);

            System.out.print("Novo Preço [" + p.getPreco() + "](Preço separado por vírgula): ");
            String precoStr = scanner.nextLine();
            if (!precoStr.trim().isEmpty()) p.setPreco(Double.parseDouble(precoStr.replace(",", ".")));

            if (p instanceof LivroFisico) {
                LivroFisico lf = (LivroFisico) p;
                System.out.print("Novo Peso [" + lf.getPeso() + "kg](Peso separado por vírgula): ");
                String pesoStr = scanner.nextLine();
                if (!pesoStr.trim().isEmpty()) lf.setPeso(Double.parseDouble(pesoStr.replace(",", ".")));

                System.out.print("Novo Estoque [" + lf.getEstoque() + " unidades](Quantidade separada por vírgula): ");
                String estStr = scanner.nextLine();
                if (!estStr.trim().isEmpty()) lf.setEstoque(Integer.parseInt(estStr.replace(",", ".")));

                System.out.print("Novo Tipo de Capa [" + lf.getTipoDeCapa() + "]: ");
                String capa = scanner.nextLine();
                if (!capa.trim().isEmpty()) lf.setTipoDeCapa(capa);
                
            } else if (p instanceof LivroDigital) {
                LivroDigital ld = (LivroDigital) p;
                System.out.print("Novo Formato [" + ld.getFormato() + "]: ");
                String formato = scanner.nextLine();
                if (!formato.trim().isEmpty()) ld.setFormato(formato);

                System.out.print("Novo Tamanho [" + ld.getTamanho() + "MB](Tamanho separado por vírgula): ");
                String tamStr = scanner.nextLine();
                if (!tamStr.trim().isEmpty()) ld.setTamanho(Double.parseDouble(tamStr.replace(",", ".")));

                System.out.print("Novo Link [" + ld.getLinkDownload() + "]: ");
                String link = scanner.nextLine();
                if (!link.trim().isEmpty()) ld.setLinkDownload(link);
                
            } else if (p instanceof LivroColecionavel) {
                LivroColecionavel lc = (LivroColecionavel) p;
                System.out.print("Novo Estado [" + lc.getEstado() + "]: ");
                String estado = scanner.nextLine();
                if (!estado.trim().isEmpty()) lc.setEstado(estado);

                System.out.print("Nova Série [" + lc.getSerie() + "]: ");
                String serie = scanner.nextLine();
                if (!serie.trim().isEmpty()) lc.setSerie(serie);

                System.out.print("Autografado (true/false) [" + lc.isAutrografado() + "]: ");
                String autStr = scanner.nextLine();
                if (!autStr.trim().isEmpty()) lc.setAutrografado(Boolean.parseBoolean(autStr));
            }

            // Envia o objeto modificado de volta para atualização remota
            byte[] prodBytes = SerializadorManual.produtoParaBytes(p);
            rmi.doOperation("EstoqueCentral", "editarProduto", prodBytes, SERVER_IP, SERVER_PORT);
            System.out.println("\n[OK] Alterações salvas com sucesso no servidor!");

        } catch (Exception e) {
            System.out.println("Erro ao editar produto via RMI: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new GerenciadorEstoque().exibirMenu();
    }
}