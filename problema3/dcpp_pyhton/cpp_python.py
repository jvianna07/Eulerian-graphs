class Grafo:
    def __init__(self):
        self.vertices = {}
        
    def adicionar_vertices(self, *vertices):
        for vertice in vertices:
            if vertice not in self.vertices:
                self.vertices[vertice] = {}
            else:
                print(f'Vertice {vertice} ja existe no grafo')
    
    def adicionar_aresta(self, origem, destino, peso):
        if origem in self.vertices and destino in self.vertices:
            self.vertices[origem][destino] = peso
            self.vertices[destino][origem] = peso
        elif origem not in self.vertices:
            print(f'O vertice {origem} nao existe no grafo')
        elif destino not in self.vertices:
            print(f'O vertice {destino} nao existe no grafo')
    
    def imprimir_lista_adjacencia(self):
        for vertice in self.vertices:
            vizinhos = self.vertices[vertice]
            vizinhos_formatados = ', '.join([f'({v}, {peso})' for v, peso in vizinhos.items()])
            print(f'{vertice}: {vizinhos_formatados}')
    
    def encontrar_ciclo_euleriano(self, vertice_inicial):
        # Verifica se o grafo possui um ciclo euleriano válido
        for vertice in self.vertices:
            if len(self.vertices[vertice]) % 2 != 0:
                print("O grafo não possui um ciclo euleriano.")
                return None
        
        # Verifica se o vértice inicial existe no grafo
        if vertice_inicial not in self.vertices:
            print(f'O vertice {vertice_inicial} nao existe no grafo')
            return None
        
        vertice_atual = vertice_inicial
        ciclo = [vertice_atual]
        custo_total = 0
        
        while True:
            vizinhos = self.vertices[vertice_atual]
            
            if len(vizinhos) == 0:
                break
            
            vizinho = list(vizinhos.keys())[0]
            peso = self.vertices[vertice_atual][vizinho]
            
            ciclo.append(vizinho)
            custo_total += peso
            
            del self.vertices[vertice_atual][vizinho]
            del self.vertices[vizinho][vertice_atual]
            
            vertice_atual = vizinho
        
        return ciclo, custo_total



# meu_grafo=Grafo()
# meu_grafo.adicionar_vertices("A", "B", "C", "D", "E","F")
# meu_grafo.adicionar_aresta("A","B",1)
# meu_grafo.adicionar_aresta("A","E",4)
# meu_grafo.adicionar_aresta("B","E",3)
# meu_grafo.adicionar_aresta("E","F",1)
# meu_grafo.adicionar_aresta("F","D",3)
# meu_grafo.adicionar_aresta("D","C",2)
# meu_grafo.adicionar_aresta("C","B",7)
# meu_grafo.adicionar_aresta("B","D",2)
# meu_grafo.adicionar_aresta("E","D",2)

# meu_grafo.imprimir_lista_adjacencia()
# print(meu_grafo.encontrar_ciclo_euleriano('B'))