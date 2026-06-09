using System;
using System.Threading.Tasks;
using ClienteAdmin.Models;
using ClienteAdmin.Services;

namespace ClienteAdmin
{
    class Program
    {
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
                Console.WriteLine("4. Alterar Livro"); // <-- AQUI ESTÁ A NOVA OPÇÃO
                Console.WriteLine("5. Remover Livro"); // <-- O REMOVER PASSOU PARA A OPÇÃO 5
                Console.WriteLine("0. Sair");
                Console.Write("\nEscolha uma opção: ");

                string opcao = Console.ReadLine() ?? "";

                switch (opcao)
                {
                    case "1": await ApiClient.ListarEstoque(); PausarTela(); break;
                    case "2": await CadastrarFisico(); PausarTela(); break;
                    case "3": await CadastrarDigital(); PausarTela(); break;
                    case "4": await AlterarProduto(); PausarTela(); break; // <-- LIGAÇÃO AO MÉTODO NOVO
                    case "5": await RemoverProduto(); PausarTela(); break; // <-- ATUALIZADO PARA 5
                    case "0": rodando = false; break;
                    default: Console.WriteLine("Opção inválida!"); PausarTela(); break;
                }
            }
        }

        static void PausarTela()
        {
            Console.WriteLine("\nPressione ENTER para voltar ao menu...");
            Console.ReadLine();
        }

        static Livro LerDadosBasicos(string tipoProduto)
        {
            Livro l = new Livro();
            l.Tipo = tipoProduto; 
            
            Console.Write("Título: ");
            l.Titulo = Console.ReadLine();

            Console.Write("Categoria: ");
            l.Categoria = Console.ReadLine();

            Console.Write("Autor: ");
            l.Autor = Console.ReadLine();

            // --- NOVA PARTE: Descrição Opcional ---
            Console.Write("Descrição (Opcional - dê Enter para pular): ");
            string desc = Console.ReadLine() ?? "";
            if (!string.IsNullOrWhiteSpace(desc))
            {
                l.Descricao = desc;
            }

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

            Console.Write("Tipo de Capa: ");
            novoLivro.TipoDeCapa = Console.ReadLine();

            await ApiClient.CadastrarLivro(novoLivro); 
        }

        static async Task AlterarProduto()
        {
            Console.Write("\nDigite o ID do livro que deseja alterar: ");
            if (!int.TryParse(Console.ReadLine(), out int id))
            {
                Console.WriteLine("ID inválido.");
                return;
            }

            // Busca os dados atuais lá no Java
            Livro? livroAtual = await ApiClient.BuscarLivro(id);
            if (livroAtual == null)
            {
                Console.WriteLine("Livro não encontrado no estoque!");
                return;
            }

            Console.WriteLine($"\n--- Alterando Livro: {livroAtual.Titulo} ---");
            Console.WriteLine("(DICA: Pressione ENTER sem digitar nada para manter o valor atual)\n");

            Console.Write($"Título [{livroAtual.Titulo}]: ");
            string entrada = Console.ReadLine() ?? "";
            if (!string.IsNullOrWhiteSpace(entrada)) livroAtual.Titulo = entrada;

            Console.Write($"Categoria [{livroAtual.Categoria}]: ");
            entrada = Console.ReadLine() ?? "";
            if (!string.IsNullOrWhiteSpace(entrada)) livroAtual.Categoria = entrada;

            Console.Write($"Autor [{livroAtual.Autor}]: ");
            entrada = Console.ReadLine() ?? "";
            if (!string.IsNullOrWhiteSpace(entrada)) livroAtual.Autor = entrada;

            Console.Write($"Descrição [{livroAtual.Descricao}]: ");
            entrada = Console.ReadLine() ?? "";
            if (!string.IsNullOrWhiteSpace(entrada)) livroAtual.Descricao = entrada;

            Console.Write($"Preço [{livroAtual.Preco}]: ");
            entrada = Console.ReadLine() ?? "";
            if (!string.IsNullOrWhiteSpace(entrada) && double.TryParse(entrada, out double preco))
            {
                livroAtual.Preco = preco;
            }

            // Verifica as propriedades exclusivas de acordo com o Tipo salvo
            if (livroAtual.Tipo == "Fisico")
            {
                Console.Write($"Peso [{livroAtual.Peso}]: ");
                entrada = Console.ReadLine() ?? "";
                if (!string.IsNullOrWhiteSpace(entrada) && double.TryParse(entrada, out double peso))
                {
                    livroAtual.Peso = peso;
                }

                Console.Write($"Quantidade em Estoque [{livroAtual.Estoque}]: ");
                entrada = Console.ReadLine() ?? "";
                if (!string.IsNullOrWhiteSpace(entrada) && int.TryParse(entrada, out int estoque))
                {
                    livroAtual.Estoque = estoque;
                }

                Console.Write($"Tipo de Capa [{livroAtual.TipoDeCapa}]: ");
                entrada = Console.ReadLine() ?? "";
                if (!string.IsNullOrWhiteSpace(entrada)) livroAtual.TipoDeCapa = entrada;
            }
            else if (livroAtual.Tipo == "Digital")
            {
                Console.Write($"Formato [{livroAtual.Formato}]: ");
                entrada = Console.ReadLine() ?? "";
                if (!string.IsNullOrWhiteSpace(entrada)) livroAtual.Formato = entrada;

                Console.Write($"Tamanho [{livroAtual.Tamanho}]: ");
                entrada = Console.ReadLine() ?? "";
                if (!string.IsNullOrWhiteSpace(entrada) && double.TryParse(entrada, out double tamanho))
                {
                    livroAtual.Tamanho = tamanho;
                }

                Console.Write($"Link de Download [{livroAtual.LinkDownload}]: ");
                entrada = Console.ReadLine() ?? "";
                if (!string.IsNullOrWhiteSpace(entrada)) livroAtual.LinkDownload = entrada;
            }

            // Envia o pacote com as informações mantidas e alteradas para o Java
            await ApiClient.AtualizarLivro(id, livroAtual);
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

            await ApiClient.CadastrarLivro(novoLivro);
        }

        static async Task RemoverProduto()
        {
            Console.Write("\nDigite o ID do livro que deseja remover: ");
            if (int.TryParse(Console.ReadLine(), out int id))
            {
                await ApiClient.RemoverLivro(id);
            }
        }
    }
}