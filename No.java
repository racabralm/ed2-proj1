// Adicione os nomes dos integrantes do grupo aqui
// Nome 1, Nome 2, Nome 3, Nome 4

import java.io.Serializable;

/**
 * Representa um nó na árvore de Huffman.
 * Pode ser um nó folha (com um caractere) ou um nó interno (sem caractere).
 * A classe implementa Comparable para ser usada na Fila de Prioridades (Min-Heap).
 * A classe implementa Serializable para que a árvore possa ser salva em arquivo.
 */
public class No implements Comparable<No>, Serializable {
    
    // Atributos do nó
    private final char caractere;
    private final int frequencia;
    private No esquerda;
    private No direita;

    /**
     * Construtor para nós internos.
     * @param frequencia A frequência combinada dos nós filhos.
     * @param esquerda O filho da esquerda.
     * @param direita O filho da direita.
     */
    public No(int frequencia, No esquerda, No direita) {
        this.caractere = '\0'; // Caractere nulo para nós internos
        this.frequencia = frequencia;
        this.esquerda = esquerda;
        this.direita = direita;
    }
    
    /**
     * Construtor para nós folha.
     * @param caractere O caractere do nó.
     * @param frequencia A frequência do caractere.
     */
    public No(char caractere, int frequencia) {
        this.caractere = caractere;
        this.frequencia = frequencia;
        this.esquerda = null;
        this.direita = null;
    }

    // Getters
    public char getCaractere() {
        return caractere;
    }

    public int getFrequencia() {
        return frequencia;
    }

    public No getEsquerda() {
        return esquerda;
    }

    public No getDireita() {
        return direita;
    }
    
    /**
     * Verifica se o nó é uma folha (não tem filhos).
     * @return true se for uma folha, false caso contrário.
     */
    public boolean isFolha() {
        return this.esquerda == null && this.direita == null;
    }

    /**
     * Compara este nó com outro nó com base na frequência.
     * Essencial para a Fila de Prioridades (Min-Heap).
     * @param outroNo O outro nó a ser comparado.
     * @return um valor negativo se esta frequência for menor, positivo se for maior, e zero se forem iguais.
     */
    @Override
    public int compareTo(No outroNo) {
        return Integer.compare(this.frequencia, outroNo.frequencia);
    }

    /**
     * Representação em String do nó, para depuração e impressão no console.
     */
    @Override
    public String toString() {
        if (isFolha()) {
            return "No('" + this.caractere + "', " + this.frequencia + ")";
        }
        return "No(interno, " + this.frequencia + ")";
    }
}