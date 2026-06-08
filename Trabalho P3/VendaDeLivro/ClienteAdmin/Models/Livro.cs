using System.Text.Json.Serialization;

namespace ClienteAdmin.Models
{
    public class Livro
    {
        // Campos Comuns a todos os produtos
        [JsonPropertyName("id")]
        public int Id { get; set; }

        [JsonPropertyName("titulo")]
        public string? Titulo { get; set; }

        // ESSA É A VARIÁVEL MAIS IMPORTANTE! 
        // É ela que avisa o Java se é "Fisico", "Digital" ou "Colecionavel"
        [JsonPropertyName("tipo")]
        public string? Tipo { get; set; } 

        [JsonPropertyName("categoria")]
        public string? Categoria { get; set; }

        [JsonPropertyName("autor")]
        public string? Autor { get; set; }

        [JsonPropertyName("descricao")]
        public string? Descricao { get; set; }

        [JsonPropertyName("preco")]
        public double Preco { get; set; }


        // --- Campos exclusivos de Livro Físico ---
        // O "?" significa que pode ser nulo (caso o livro seja digital)
        
        [JsonPropertyName("peso")]
        public double? Peso { get; set; }

        [JsonPropertyName("estoque")]
        public int? Estoque { get; set; }

        [JsonPropertyName("tipoDeCapa")]
        public string? TipoDeCapa { get; set; }


        // --- Campos exclusivos de Livro Digital ---

        [JsonPropertyName("formato")]
        public string? Formato { get; set; }

        [JsonPropertyName("tamanho")]
        public double? Tamanho { get; set; }

        [JsonPropertyName("linkDownload")]
        public string? LinkDownload { get; set; }
    }
}