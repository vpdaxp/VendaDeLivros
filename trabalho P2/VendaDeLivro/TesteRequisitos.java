import model.*;
import io.*;
import java.io.*;
import java.util.List;

public class TesteRequisitos {

    public static void main(String[] args) {
        Produto[] produtosParaTeste = new Produto[3];
        produtosParaTeste[0] = new LivroFisico(101, "Java Distribuído", "Livro técnico", "TI", "karacol", 150.0, 1.2, 10, "Capa Dura");
        produtosParaTeste[1] = new LivroDigital(102, "Redes de Computadores", "Ebook", "Redes", "Tanenbaum", 80.0, "PDF", 15.5, "http://download.com");
        produtosParaTeste[2] = new LivroColecionavel(103, "Ragnarok Lore", "Raro", "Games", "Gravity", 250.0, "Novo", "Edição 1", true);

        String NOME_ARQUIVO_TESTE = "teste_unificado.bin";

        try {
            System.out.println("=== INICIANDO TESTES DE REQUISITOS ===\n");

            System.out.println(">>> Testando Requisito 2.b.i: Saída Padrão (Console)");
            try (PojoOutputStream posConsole = new PojoOutputStream(produtosParaTeste, 3, System.out)) {
                posConsole.enviarDados();
            }
            System.out.println("\n[OK] Dados enviados para System.out\n");

            System.out.println(">>> Testando Requisito 2.b.ii: Arquivo (" + NOME_ARQUIVO_TESTE + ")");
            try (FileOutputStream fos = new FileOutputStream(NOME_ARQUIVO_TESTE);
                 PojoOutputStream posArquivo = new PojoOutputStream(produtosParaTeste, 3, fos)) {
                posArquivo.enviarDados();
            }
            System.out.println("[OK] Arquivo gerado com sucesso.\n");

            System.out.println(">>> Testando Requisito 3.c: Leitura de Arquivo");
            try (FileInputStream fis = new FileInputStream(NOME_ARQUIVO_TESTE);
                 PojoInputStream pis = new PojoInputStream(fis)) {
                List<Produto> recuperados = pis.lerDados();
                for (Produto p : recuperados) {
                    System.out.println("Objeto Recuperado: " + p.getTitulo());
                }
            }
            System.out.println("[OK] Objetos reconstruídos sem perda de dados.\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}