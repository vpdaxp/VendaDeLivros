using System;
using System.Threading.Tasks;
using ClienteAdmin.Models;
using ClienteAdmin.Services;

namespace ClienteAdmin
{
    class Program
    {
        // O Main agora é 'async' para podermos usar o 'await' na hora de chamar a API
        static async Task Main(string[] args)
        {
            bool rodando = true;

            while (rodando)
            {
                Console.Clear();
                Console.WriteLine("=== PAINEL DO ADMINISTRADOR - LIVRARIA ===");
                Console.WriteLine("1. Ver Estoque Completo");
                Console.WriteLine("2. Cadastrar Livro Físico");
                Console.WriteLine("3. Cadastrar Livro Digital");
                Console.WriteLine("4. Remover Livro");
                Console.WriteLine("0. Sair");
                Console.Write("\nEscolha uma opção: ");

                string opcao = Console.ReadLine() ?? "";

                switch (opcao)
                {
                    case "1":
                        await ApiClient.ListarEstoque();
                        PausarTela();
                        break;
                    case "2":
                        await CadastrarFisico();
                        PausarTela();
                        break;
                    case "3":
                        await CadastrarDigital();
                        PausarTela();
                        break;
                    case "4":
                        await RemoverProduto();
                        PausarTela();
                        break;
                    case "0":
                        rodando = false;
                        Console.WriteLine("Encerrando o painel...");
                        break;
                    default:
                        Console.WriteLine("Opção inválida!");
                        PausarTela();
                        break;
                }
            }
        }

        static void PausarTela()
        {
            Console.WriteLine("\nPressione ENTER para voltar ao menu...");
            Console.ReadLine();
        }

        // --- Lógica de Captura de Dados ---

        static Livro LerDadosBasicos(string tipoProduto)
        {
            Livro l = new Livro();
            l.Tipo = tipoProduto; // Muito importante: preenche com "Fisico" ou "Digital"

            Console.Write("ID do Livro (número): ");
            l.Id = int.Parse(Console.ReadLine() ?? "0");

            Console.Write("Título: ");
            l.Titulo = Console.ReadLine();

            Console.Write("Categoria: ");
            l.Categoria = Console.ReadLine();

            Console.Write("Autor: ");
            l.Autor = Console.ReadLine();

            Console.Write("Preço (R$): ");
            l.Preco = double.Parse(Console.ReadLine() ?? "0");

            return l;
        }

        static async Task CadastrarFisico()
        {
            Console.WriteLine("\n--- Cadastrar Livro Físico ---");
            Livro novoLivro = LerDadosBasicos("Fisico");

            Console.Write("Peso (kg): ");
            novoLivro.Peso = double.Parse(Console.ReadLine() ?? "0");

            Console.Write("Quantidade em Estoque: ");
            novoLivro.Estoque = int.Parse(Console.ReadLine() ?? "0");

            Console.Write("Tipo de Capa (ex: Brochura, Dura): ");
            novoLivro.TipoDeCapa = Console.ReadLine();

            // Manda o mensageiro arremessar o objeto para o Java
            await ApiClient.CadastrarLivro(novoLivro); 
        }

        static async Task CadastrarDigital()
        {
            Console.WriteLine("\n--- Cadastrar Livro Digital ---");
            Livro novoLivro = LerDadosBasicos("Digital");

            Console.Write("Formato (ex: PDF, EPUB): ");
            novoLivro.Formato = Console.ReadLine();

            Console.Write("Tamanho (MB): ");
            novoLivro.Tamanho = double.Parse(Console.ReadLine() ?? "0");

            Console.Write("Link de Download: ");
            novoLivro.LinkDownload = Console.ReadLine();

            // Manda o mensageiro arremessar o objeto para o Java
            await ApiClient.CadastrarLivro(novoLivro);
        }

        static async Task RemoverProduto()
        {
            Console.Write("\nDigite o ID do livro que deseja remover: ");
            if (int.TryParse(Console.ReadLine(), out int id))
            {
                await ApiClient.RemoverLivro(id);
            }
            else
            {
                Console.WriteLine("ID inválido. Digite um número inteiro.");
            }
        }
    }
}