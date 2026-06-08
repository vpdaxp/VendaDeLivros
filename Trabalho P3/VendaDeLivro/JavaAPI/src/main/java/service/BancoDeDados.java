package service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.Produto;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BancoDeDados {
    private static final String ARQUIVO = "estoque.json";
    private static ObjectMapper mapper = new ObjectMapper();
    private static List<Produto> estoqueCache = new ArrayList<>();

    // O bloco estático carrega os dados do arquivo assim que o servidor liga
    static {
        carregarDoArquivo();
    }

    public static List<Produto> getEstoque() {
        return estoqueCache;
    }

    public static void adicionar(Produto p) {
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
            System.err.println("Erro ao salvar no arquivo JSON: " + e.getMessage());
        }
    }

    private static void carregarDoArquivo() {
        try {
            File file = new File(ARQUIVO);
            if (file.exists()) {
                estoqueCache = mapper.readValue(file, new TypeReference<List<Produto>>(){});
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar o arquivo JSON: " + e.getMessage());
        }
    }
}