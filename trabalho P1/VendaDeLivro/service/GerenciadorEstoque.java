package service;

import model.*;
import io.*;
import java.util.Scanner;
import java.io.*;
import java.util.List;

public class GerenciadorEstoque {
    private LojaVirtual loja;
    private Scanner scanner;
    private int ultimoIdGerado;
    
    private final String ARQUIVO_ESTOQUE = "estoque.dat";
    private final String ARQUIVO_CONFIG = "config.dat";

    public GerenciadorEstoque() {
        this.loja = new LojaVirtual("Estoque Central - UFC Quixadá", "Campus Quixadá", "88-0000");
        this.scanner = new Scanner(System.in);
        carregarConfig();
        carregarEstoque();
    }

    public void exibirMenu() {
        int opcao = -1;
        while (opcao != 0) {
            System.out.println("\n=== GERENCIAMENTO DE ESTOQUE (CRUD) ===");
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

    Produto p = null;
    for (Produto item : loja.getPedidosRealizados()) {
        if (item.getId() == id) {
            p = item;
            break;
        }
    }

    if (p == null) {
        System.out.println("Erro: ID não encontrado.");
        return;
    }

    System.out.println("\n--- Edição (Pressione ENTER para manter o valor atual) ---");

    // --- CAMPOS COMUNS (PRODUTO) ---
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

    System.out.print("Novo Preço [" + p.getPreco() + "]: ");
    String precoStr = scanner.nextLine();
    if (!precoStr.trim().isEmpty()) p.setPreco(Double.parseDouble(precoStr.replace(",", ".")));

    // --- CAMPOS ESPECÍFICOS (SUBCLASSES) ---
    if (p instanceof LivroFisico) {
        LivroFisico lf = (LivroFisico) p;
        
        System.out.print("Novo Peso [" + lf.getPeso() + "kg]: ");
        String pesoStr = scanner.nextLine();
        if (!pesoStr.trim().isEmpty()) lf.setPeso(Double.parseDouble(pesoStr.replace(",", ".")));

        System.out.print("Novo Estoque [" + lf.getEstoque() + " unidades]: ");
        String estStr = scanner.nextLine();
        if (!estStr.trim().isEmpty()) lf.setEstoque(Integer.parseInt(estStr));

        System.out.print("Novo Tipo de Capa [" + lf.getTipoDeCapa() + "]: ");
        String capa = scanner.nextLine();
        if (!capa.trim().isEmpty()) lf.setTipoDeCapa(capa);
        
    } else if (p instanceof LivroDigital) {
        LivroDigital ld = (LivroDigital) p;
        
        System.out.print("Novo Formato [" + ld.getFormato() + "]: ");
        String formato = scanner.nextLine();
        if (!formato.trim().isEmpty()) ld.setFormato(formato);

        System.out.print("Novo Tamanho [" + ld.getTamanho() + "MB]: ");
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

    salvarEstoque(); 
    System.out.println("\n[OK] Alterações salvas com sucesso!");
}

    private void adicionarAoEstoque() {
        this.ultimoIdGerado++;
        int id = this.ultimoIdGerado;
        System.out.println("\n--- Cadastro (ID: " + id + ") ---");
        System.out.print("Título: "); String t = scanner.nextLine();
        System.out.print("Autor: "); String a = scanner.nextLine();
        System.out.print("Preço: "); double p = scanner.nextDouble();
        System.out.println("Tipo: [1] Físico | [2] Digital | [3] Colecionável");
        int tipo = scanner.nextInt(); scanner.nextLine();

        Produto novo = null;
        if (tipo == 1) {
            System.out.print("Peso: "); double pe = scanner.nextDouble();
            System.out.print("Qtd: "); int q = scanner.nextInt(); scanner.nextLine();
            System.out.print("Capa: "); String c = scanner.nextLine();
            novo = new LivroFisico(id, t, "Fisico", "Geral", a, p, pe, q, c);
        } else if (tipo == 2) {
            System.out.print("Formato: "); String f = scanner.nextLine();
            System.out.print("Tamanho: "); double ta = scanner.nextDouble(); scanner.nextLine();
            System.out.print("Link: "); String l = scanner.nextLine();
            novo = new LivroDigital(id, t, "Digital", "E-book", a, p, f, ta, l);
        } else if (tipo == 3) {
            System.out.print("Estado: "); String es = scanner.nextLine();
            System.out.print("Série: "); String s = scanner.nextLine();
            System.out.print("Autografado (true/false): "); boolean au = scanner.nextBoolean();
            novo = new LivroColecionavel(id, t, "Raro", "Coleção", a, p, es, s, au);
        }

        if (novo != null) {
            loja.adicionarPedido(novo);
            salvarEstoque(); salvarConfig();
            System.out.println("Cadastrado!");
        }
    }

    private void listarEstoque() {
        System.out.println("\n--- Inventário ---");
        if (loja.getPedidosRealizados().isEmpty()) System.out.println("Vazio.");
        else loja.getPedidosRealizados().forEach(System.out::println);
    }

    private void removerDoEstoque() {
        System.out.print("ID para remover: ");
        int id = scanner.nextInt();
        if (loja.getPedidosRealizados().removeIf(prod -> prod.getId() == id)) {
            salvarEstoque();
            System.out.println("Removido.");
        }
    }

    private void salvarEstoque() {
        try (FileOutputStream fos = new FileOutputStream(ARQUIVO_ESTOQUE)) {
            List<Produto> prods = loja.getPedidosRealizados();
            PojoOutputStream pos = new PojoOutputStream(prods.toArray(new Produto[0]), prods.size(), fos);
            pos.enviarDados();
        } catch (IOException e) { System.err.println("Erro ao salvar."); }
    }

    private void carregarEstoque() {
        File f = new File(ARQUIVO_ESTOQUE);
        if (!f.exists()) return;
        try (FileInputStream fis = new FileInputStream(f)) {
            PojoInputStream pis = new PojoInputStream(fis);
            loja.getPedidosRealizados().addAll(pis.lerDados());
        } catch (IOException e) { System.out.println("Erro ao carregar."); }
    }

    private void salvarConfig() {
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(ARQUIVO_CONFIG))) {
            dos.writeInt(this.ultimoIdGerado);
        } catch (IOException e) { }
    }

    private void carregarConfig() {
        File f = new File(ARQUIVO_CONFIG);
        if (!f.exists()) { this.ultimoIdGerado = 0; return; }
        try (DataInputStream dis = new DataInputStream(new FileInputStream(f))) {
            this.ultimoIdGerado = dis.readInt();
        } catch (IOException e) { this.ultimoIdGerado = 0; }
    }

    public static void main(String[] args) {
        new GerenciadorEstoque().exibirMenu();
    }
}
