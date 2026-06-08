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
        // Instância única do mensageiro apontando para a sua API Java
        private static readonly HttpClient client = new HttpClient();
        private const string BASE_URL = "http://localhost:8080";

        // 1. MÉTODO GET: Buscar a vitrine
        public static async Task ListarEstoque()
        {
            try
            {
                Console.WriteLine("\nConectando ao servidor Java...");
                var response = await client.GetAsync($"{BASE_URL}/produtos");
                
                // Se o Java devolver erro 400 ou 500, ele cai no catch
                response.EnsureSuccessStatusCode(); 
                
                var json = await response.Content.ReadAsStringAsync();
                
                // Transforma o JSON recebido de volta em uma Lista de Livros no C#
                var livros = JsonSerializer.Deserialize<List<Livro>>(json);
                
                Console.WriteLine("\n=== ESTOQUE ATUAL DO SERVIDOR ===");
                if (livros == null || livros.Count == 0)
                {
                    Console.WriteLine("O estoque está vazio.");
                    return;
                }

                foreach (var livro in livros)
                {
                    Console.WriteLine($"ID: {livro.Id} | [{livro.Tipo}] {livro.Titulo} | Preço: R$ {livro.Preco:F2}");
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine($"\n[ERRO DE REDE] Não foi possível falar com a API: {ex.Message}");
            }
        }

        // 2. MÉTODO POST: Enviar um livro novo
        public static async Task CadastrarLivro(Livro novoLivro)
        {
            try
            {
                Console.WriteLine("\nEnviando dados para o servidor...");
                
                // O PostAsJsonAsync converte o objeto 'novoLivro' para JSON automaticamente!
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

        // 3. MÉTODO DELETE: Remover um livro
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