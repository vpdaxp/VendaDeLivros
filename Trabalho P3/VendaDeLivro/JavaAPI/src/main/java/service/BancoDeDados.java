package service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.Produto;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class BancoDeDados {
    private static final String ARQUIVO = "estoque.json";
    private static final String ARQUIVO_ID = "contador_id.txt"; // Novo arquivo para o ID
    private static ObjectMapper mapper = new ObjectMapper();
    private static List<Produto> estoqueCache = new ArrayList<>();

    static {
        carregarDoArquivo();
    }

    // --- NOVO MÉTODO: Gera um ID único e definitivo ---
    public static int gerarNovoId() {
        int idAtual = 0;
        try {
            File arquivoId = new File(ARQUIVO_ID);
            if (arquivoId.exists()) {
                String conteudo = new String(Files.readAllBytes(Paths.get(ARQUIVO_ID)));
                idAtual = Integer.parseInt(conteudo.trim());
            }
            idAtual++; // Incrementa para o novo livro
            Files.write(Paths.get(ARQUIVO_ID), String.valueOf(idAtual).getBytes());
        } catch (Exception e) {
            System.err.println("Erro ao gerenciar o ID: " + e.getMessage());
        }
        return idAtual;
    }

    public static List<Produto> getEstoque() { return estoqueCache; }

    public static void adicionar(Produto p) {
        // Agora o servidor injeta o ID na hora de salvar!
        p.setId(gerarNovoId()); 
        estoqueCache.add(p);
        salvarNoArquivo();
    }

    public static void remover(int id) {
        estoqueCache.removeIf(p -> p.getId() == id);
        salvarNoArquivo();
    }

    public static void salvarNoArquivo() {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(ARQUIVO), estoqueCache);
        } catch (IOException e) {
            System.err.println("Erro ao salvar: " + e.getMessage());
        }
    }

    public static void atualizar(int id, Produto produtoAtualizado) {
        for (int i = 0; i < estoqueCache.size(); i++) {
            if (estoqueCache.get(i).getId() == id) {
                produtoAtualizado.setId(id); // Garante que o ID não seja alterado
                estoqueCache.set(i, produtoAtualizado);
                salvarNoArquivo();
                return;
            }
        }
    }

    private static void carregarDoArquivo() {
        try {
            File file = new File(ARQUIVO);
            if (file.exists()) {
                estoqueCache = mapper.readValue(file, new TypeReference<List<Produto>>(){});
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar: " + e.getMessage());
        }
    }
}