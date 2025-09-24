/*
 * Projeto 1 | Compressão de Arquivos com o Algoritmo de Huffman
 * Prof. Dr. Jean M. Laine
 * Turma 04N
 *
 * Bruna Amorim Maia (RA 10431883)
 * Rafael Araujo Cabral Moreira (RA 10441919)
 * Rute Willemann (RA 10436781)
 */

// MinHeap.java

import java.util.ArrayList;
import java.util.NoSuchElementException;

/**
 * Implementação de uma Fila de Prioridades utilizando a estrutura de dados Min-Heap
 * O heap é representado implicitamente por um ArrayList de Nós
 * O elemento de menor prioridade (menor frequência) está sempre na raiz (índice 0)
 */
public class MinHeap {

    // O ArrayList armazena os nós da árvore, mantendo a propriedade do Min-Heap
    private final ArrayList<No> heap;

    // Construtor que inicializa o ArrayList vazio
    public MinHeap() {
        this.heap = new ArrayList<>();
    }

    /**
     * Insere um novo nó no heap, mantendo a propriedade de ordenação do Min-Heap
     * O processo é conhecido como "sift-up" ou "bubble-up"
     * @param no O nó a ser inserido
     */
    public void inserir(No no) {
        // 1 Adiciona o novo nó no final da lista
        heap.add(no);
        int indiceAtual = heap.size() - 1;

        // 2 Sobe o nó na árvore (no ArrayList) até encontrar sua posição correta
        // Isso acontece enquanto o nó não for a raiz e for menor que seu pai
        while (indiceAtual > 0) {
            int indicePai = (indiceAtual - 1) / 2; // Fórmula para encontrar o pai
            
            // Compara o nó atual com seu pai
            if (heap.get(indiceAtual).compareTo(heap.get(indicePai)) < 0) {
                // Se o filho for menor que o pai, eles trocam de lugar
                swap(indiceAtual, indicePai);
                // O índice atual agora é o do pai, para continuar subindo
                indiceAtual = indicePai;
            } else {
                // Se o nó já é maior ou igual ao seu pai, ele está na posição correta
                break;
            }
        }
    }

    /**
     * Remove e retorna o nó de menor frequência (a raiz do heap)
     * O processo é conhecido como "sift-down" ou "bubble-down"
     * @return O nó com a menor frequência (prioridade máxima)
     */
    public No removerMin() {
        if (isEmpty()) {
            throw new NoSuchElementException("O Heap está vazio");
        }

        // 1 O menor elemento está sempre na raiz (índice 0)
        No min = heap.get(0);
        
        // 2 Pega o último elemento da lista para colocá-lo na raiz
        No ultimoElemento = heap.remove(heap.size() - 1);
        
        // Se o heap não ficou vazio após a remoção do último
        if (!isEmpty()) {
            // Coloca o último elemento na raiz
            heap.set(0, ultimoElemento);
            // Reordena o heap a partir da raiz para restaurar a propriedade do Min-Heap
            heapify(0);
        }

        return min; // Retorna o elemento mínimo que foi salvo
    }
    
    /**
     * Restaura a propriedade do Min-Heap a partir de um determinado índice, "afundando" o nó
     * Este é o processo "sift-down"
     * @param indice O índice a partir do qual a verificação deve começar
     */
    private void heapify(int indice) {
        int menor = indice;
        int esquerda = 2 * indice + 1; // Fórmula para o filho da esquerda
        int direita = 2 * indice + 2;  // Fórmula para o filho da direita

        // Verifica se o filho da esquerda existe e é menor que o pai atual
        if (esquerda < heap.size() && heap.get(esquerda).compareTo(heap.get(menor)) < 0) {
            menor = esquerda;
        }

        // Verifica se o filho da direita existe e é menor que o "menor" atual (pai ou filho esquerdo)
        if (direita < heap.size() && heap.get(direita).compareTo(heap.get(menor)) < 0) {
            menor = direita;
        }

        // Se o menor nó não for mais o pai original, eles precisam trocar
        if (menor != indice) {
            swap(indice, menor);
            // Chama recursivamente a função para continuar afundando o nó a partir de sua nova posição
            heapify(menor);
        }
    }
    
    /**
     * Método auxiliar para trocar dois elementos de posição no ArrayList
     * @param i O índice do primeiro elemento
     * @param j O índice do segundo elemento
     */
    private void swap(int i, int j) {
        No temp = heap.get(i);
        heap.set(i, heap.get(j));
        heap.set(j, temp);
    }
    
    // Retorna o número de elementos no heap
    public int tamanho() {
        return heap.size();
    }
    
    // Verifica se o heap está vazio
    public boolean isEmpty() {
        return heap.isEmpty();
    }

    // Retorna a representação do heap como um ArrayList, para impressão
    public ArrayList<No> getHeapAsList() {
        return new ArrayList<>(heap);
    }
}
