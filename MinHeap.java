// Adicione os nomes dos integrantes do grupo aqui
// Nome 1, Nome 2, Nome 3, Nome 4

import java.util.ArrayList;
import java.util.NoSuchElementException;

/**
 * Implementação de uma Fila de Prioridades usando um Min-Heap.
 * A estrutura é representada por um ArrayList de Nós.
 */
public class MinHeap {

    private final ArrayList<No> heap;

    public MinHeap() {
        this.heap = new ArrayList<>();
    }

    /**
     * Insere um novo nó no heap, mantendo a propriedade do Min-Heap.
     * @param no O nó a ser inserido.
     */
    public void inserir(No no) {
        heap.add(no);
        int indiceAtual = heap.size() - 1;
        while (indiceAtual > 0) {
            int indicePai = (indiceAtual - 1) / 2;
            if (heap.get(indiceAtual).compareTo(heap.get(indicePai)) < 0) {
                swap(indiceAtual, indicePai);
                indiceAtual = indicePai;
            } else {
                break;
            }
        }
    }

    /**
     * Remove e retorna o nó de menor frequência (a raiz do heap).
     * @return O nó com a menor frequência.
     * @throws NoSuchElementException se o heap estiver vazio.
     */
    public No removerMin() {
        if (isEmpty()) {
            throw new NoSuchElementException("Heap está vazio.");
        }

        No min = heap.get(0);
        No ultimoElemento = heap.remove(heap.size() - 1);
        
        if (!isEmpty()) {
            heap.set(0, ultimoElemento);
            heapify(0);
        }

        return min;
    }

    /**
     * Retorna o tamanho atual do heap.
     * @return o número de elementos no heap.
     */
    public int tamanho() {
        return heap.size();
    }
    
    /**
     * Verifica se o heap está vazio.
     * @return true se o heap não contiver elementos.
     */
    public boolean isEmpty() {
        return heap.isEmpty();
    }

    /**
     * Retorna a representação do heap como um ArrayList.
     * @return O ArrayList que representa o heap.
     */
    public ArrayList<No> getHeapAsList() {
        return new ArrayList<>(heap);
    }
    
    /**
     * Garante a propriedade do Min-Heap a partir de um determinado índice.
     * @param indice O índice a partir do qual a verificação deve começar.
     */
    private void heapify(int indice) {
        int menor = indice;
        int esquerda = 2 * indice + 1;
        int direita = 2 * indice + 2;

        if (esquerda < heap.size() && heap.get(esquerda).compareTo(heap.get(menor)) < 0) {
            menor = esquerda;
        }

        if (direita < heap.size() && heap.get(direita).compareTo(heap.get(menor)) < 0) {
            menor = direita;
        }

        if (menor != indice) {
            swap(indice, menor);
            heapify(menor);
        }
    }
    
    /**
     * Troca dois elementos de posição no heap.
     * @param i O índice do primeiro elemento.
     * @param j O índice do segundo elemento.
     */
    private void swap(int i, int j) {
        No temp = heap.get(i);
        heap.set(i, heap.get(j));
        heap.set(j, temp);
    }
}