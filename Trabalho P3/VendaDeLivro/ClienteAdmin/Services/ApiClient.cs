using System;
using System.Collections.Generic;
using System.Net.Http;
using System.Net.Http.Json;
using System.Text.Json;
using System.Threading.Tasks;
using ClienteAdmin.Models;

namespace ClienteAdmin.Services
{
    public class ApiClient
    {
        private static readonly HttpClient client = new HttpClient();
        private const string BASE_URL = "http://localhost:8080";

        public static async Task ListarEstoque()
        {
            try
            {
                Console.WriteLine("\nConectando ao servidor Java...");
                var response = await client.GetAsync($"{BASE_URL}/produtos");
                
                response.EnsureSuccessStatusCode(); 
                
                var json = await response.Content.ReadAsStringAsync();
                
                var livros = JsonSerializer.Deserialize<List<Livro>>(json);
                
                Console.WriteLine("\n=== ESTOQUE ATUAL DO SERVIDOR ===");
                if (livros == null || livros.Count == 0)
                {
                    Console.WriteLine("O estoque está vazio.");
                    return;
                }

                foreach (var livro in livros)
                {
                    string pesoFormatado = livro.Peso.HasValue ? $"{livro.Peso}kg" : "N/A";
                    string quantidadeStr = livro.Estoque.HasValue ? livro.Estoque.ToString() : "N/A";
                    
                    string tipoFormatado = string.IsNullOrEmpty(livro.Tipo) ? "N/A" : livro.Tipo;

                    Console.WriteLine($"ID: {livro.Id} | Tipo: {tipoFormatado} | Livro: {livro.Titulo} | Autor: {livro.Autor} | Preço: R$ {livro.Preco:F2} | Peso: {pesoFormatado} | Qtd: {quantidadeStr}");
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine($"\n[ERRO DE REDE] Não foi possível falar com a API: {ex.Message}");
            }
        }

        public static async Task CadastrarLivro(Livro novoLivro)
        {
            try
            {
                Console.WriteLine("\nEnviando dados para o servidor...");
                
                var response = await client.PostAsJsonAsync($"{BASE_URL}/estoque", novoLivro);
                
                var resultado = await response.Content.ReadAsStringAsync();
                
                if (response.IsSuccessStatusCode)
                {
                    Console.WriteLine($"\n[SUCESSO] O Java respondeu: {resultado}");
                }
                else
                {
                    Console.WriteLine($"\n[ERRO] O Java recusou o cadastro. Código: {response.StatusCode}");
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine($"\n[ERRO DE REDE] Não foi possível falar com a API: {ex.Message}");
            }
        }

        public static async Task<Livro?> BuscarLivro(int id)
        {
            try
            {
                var response = await client.GetAsync($"{BASE_URL}/produtos");
                if (response.IsSuccessStatusCode)
                {
                    var json = await response.Content.ReadAsStringAsync();
                    var livros = JsonSerializer.Deserialize<List<Livro>>(json);
                    return livros?.Find(l => l.Id == id);
                }
            }
            catch (Exception ex) { Console.WriteLine($"Erro ao buscar: {ex.Message}"); }
            return null;
        }

        public static async Task AtualizarLivro(int id, Livro livroAtualizado)
        {
            try
            {
                Console.WriteLine("\nEnviando atualização para o servidor...");
                var response = await client.PutAsJsonAsync($"{BASE_URL}/estoque/{id}", livroAtualizado);
                var resultado = await response.Content.ReadAsStringAsync();
                Console.WriteLine($"\n[SUCESSO] O Java respondeu: {resultado}");
            }
            catch (Exception ex)
            {
                Console.WriteLine($"\n[ERRO DE REDE]: {ex.Message}");
            }
        }

        public static async Task RemoverLivro(int id)
        {
            try
            {
                Console.WriteLine($"\nSolicitando a remoção do ID {id} ao servidor...");
                var response = await client.DeleteAsync($"{BASE_URL}/estoque/{id}");
                
                var resultado = await response.Content.ReadAsStringAsync();
                
                if (response.IsSuccessStatusCode)
                {
                    Console.WriteLine($"\n[SUCESSO] O Java respondeu: {resultado}");
                }
                else
                {
                    Console.WriteLine($"\n[ERRO] Falha ao remover. Código: {response.StatusCode}");
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine($"\n[ERRO DE REDE] Não foi possível falar com a API: {ex.Message}");
            }
        }
    }
}