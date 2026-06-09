import requests
import os

BASE_URL = "http://localhost:8080"
carrinho = []

def limpar_tela():
    os.system('cls' if os.name == 'nt' else 'clear')

def listar_vitrine():
    print("\n--- VITRINE DA LIVRARIA ---")
    try:
        resposta = requests.get(f"{BASE_URL}/produtos")
        
        if resposta.status_code == 200:
            livros = resposta.json()
            if not livros:
                print("A loja está vazia no momento.")
                return []

            for livro in livros:
                tipo = livro.get("tipo", "N/A")
                estoque = livro.get("estoque", "Digital")
                print(f"[{livro['id']}] {livro['titulo']} ({tipo}) - R$ {livro['preco']:.2f} | Disponível: {estoque}")
            
            return livros
        else:
            print("Erro ao carregar a vitrine.")
            return []
    except requests.exceptions.RequestException as e:
        print(f"Erro de ligação com o servidor Java: {e}")
        return []

def adicionar_ao_carrinho():
    livros = listar_vitrine()
    if not livros:
        return

    try:
        id_escolhido = int(input("\nDigite o ID do livro que deseja comprar: "))
        
        livro_encontrado = next((l for l in livros if l['id'] == id_escolhido), None)
        
        if livro_encontrado:
            if livro_encontrado.get("tipo") == "Fisico":
                if livro_encontrado.get("estoque", 0) <= 0:
                    print("Lamentamos, este livro físico está esgotado.")
                    return
                
                livro_para_comprar = livro_encontrado.copy()
                livro_para_comprar["estoque"] = 1 
                carrinho.append(livro_para_comprar)
            else:
                carrinho.append(livro_encontrado)
                
            print(f"\nLivro '{livro_encontrado['titulo']}' adicionado ao carrinho com sucesso!")
        else:
            print("\nID não encontrado.")
    except ValueError:
        print("\nPor favor, digite um número de ID válido.")

def ver_carrinho():
    print("\n--- O SEU CARRINHO ---")
    if not carrinho:
        print("O carrinho está vazio.")
        return

    total = 0
    for i, item in enumerate(carrinho):
        print(f"{i + 1}. {item['titulo']} - R$ {item['preco']:.2f}")
        total += item['preco']
    
    print("-" * 25)
    print(f"Total a pagar: R$ {total:.2f}")

def finalizar_compra():
    global carrinho
    if not carrinho:
        print("\nNão pode finalizar a compra com o carrinho vazio.")
        return

    ver_carrinho()
    confirmacao = input("\nDeseja confirmar a compra? (S/N): ").upper()
    
    if confirmacao == 'S':
        print("\nA processar a compra no servidor Java...")
        try:
            resposta = requests.post(f"{BASE_URL}/vendas", json=carrinho)
            
            if resposta.status_code == 200:
                dados = resposta.json()
                print("\n=== RECIBO DA COMPRA ===")
                recibo_texto = dados.get("recibo", "Compra realizada com sucesso!")
                print(recibo_texto.replace('\\n', '\n'))
                
                carrinho.clear()
            else:
                print(f"\nFalha ao processar a venda. O Java respondeu: Erro {resposta.status_code}")
        except requests.exceptions.RequestException as e:
            print(f"\nErro de rede ao contactar o servidor: {e}")
    else:
        print("\nCompra cancelada.")

def main():
    rodando = True
    while rodando:
        limpar_tela()
        print("=== BEM-VINDO À LIVRARIA ===")
        print(f"Itens no carrinho: {len(carrinho)}")
        print("1. Ver Vitrine de Livros")
        print("2. Adicionar Livro ao Carrinho")
        print("3. Ver Carrinho")
        print("4. Finalizar Compra")
        print("0. Sair")
        
        opcao = input("\nEscolha uma opção: ")

        if opcao == "1":
            listar_vitrine()
            input("\nPressione ENTER para voltar...")
        elif opcao == "2":
            adicionar_ao_carrinho()
            input("\nPressione ENTER para voltar...")
        elif opcao == "3":
            ver_carrinho()
            input("\nPressione ENTER para voltar...")
        elif opcao == "4":
            finalizar_compra()
            input("\nPressione ENTER para voltar...")
        elif opcao == "0":
            rodando = False
            print("Obrigado por visitar a nossa loja!")
        else:
            print("Opção inválida.")
            input("\nPressione ENTER para voltar...")

if __name__ == "__main__":
    main()