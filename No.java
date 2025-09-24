/*
 * Projeto 1 | Compressão de Arquivos com o Algoritmo de Huffman
 * Prof. Dr. Jean M. Laine
 * Turma 04N
 *
 * Bruna Amorim Maia (RA 10431883)
 * Rafael Araujo Cabral Moreira (RA 10441919)
 * Rute Willemann (RA 10436781)
 */

// No.java

import java.io.Serializable;

/**
 * Representa um nó na árvore de Huffman
 * Pode ser tanto um nó folha (que contém um caractere) quanto um nó interno (que une dois sub-nós)
 * A classe implementa:
 * - Comparable: para que os nós possam ser comparados e ordenados na Fila de Prioridades (Min-Heap)
 * - Serializable: para que a árvore (ou a tabela de frequências) possa ser gravada em um arquivo
 */
public class No implements Comparable<No>, Serializable {
    
    // Atributos do nó
    private final char caractere;       // Armazena o caractere (válido apenas para nós folha)
    private final int frequencia;       // Frequência do caractere ou a soma das frequências dos filhos
    private No esquerda;                // Referência para o filho da esquerda na árvore
    private No direita;                 // Referência para o filho da direita na árvore

    /**
     * Construtor para nós INTERNOS da árvore
     * Um nó interno não possui um caractere próprio, mas armazena a soma das frequências de seus filhos
     * @param frequencia A frequência combinada dos nós filhos
     * @param esquerda O nó filho da esquerda
     * @param direita O nó filho da direita
     */
    public No(int frequencia, No esquerda, No direita) {
        this.caractere = '\0'; // Caractere nulo ('\0') indica que este é um nó interno
        this.frequencia = frequencia;
        this.esquerda = esquerda;
        this.direita = direita;
    }
    
    /**
     * Construtor para nós FOLHA da árvore
     * Um nó folha representa um caractere real do arquivo
     * @param caractere O caractere que este nó representa
     * @param frequencia A frequência com que este caractere apareceu no arquivo
     */
    public No(char caractere, int frequencia) {
        this.caractere = caractere;
        this.frequencia = frequencia;
        this.esquerda = null; // Nós folha não têm filhos
        this.direita = null;  // Nós folha não têm filhos
    }

    // --- MÉTODOS GETTERS ---
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
     * Método auxiliar para verificar se o nó é uma folha
     * Um nó é uma folha se e somente se ambos os seus filhos são nulos
     * @return true se for uma folha, false caso contrário
     */
    public boolean isFolha() {
        return this.esquerda == null && this.direita == null;
    }

    /**
     * Implementação do método compareTo, exigido pela interface Comparable
     * Este método é o "cérebro" da Fila de Prioridades, pois define como os nós são ordenados
     * Ele compara a frequência deste nó com a de outro nó
     * @param outroNo O outro nó a ser comparado
     * @return um valor negativo se a frequência deste nó for menor, 
     * positivo se for maior, e zero se forem iguais
     */
    @Override
    public int compareTo(No outroNo) {
        // Usa o método estático da classe Integer para comparar os dois valores de frequência
        return Integer.compare(this.frequencia, outroNo.frequencia);
    }

    /**
     * Representação em String do nó, para facilitar a depuração e a impressão no console
     */
    @Override
    public String toString() {
        // Se for um nó folha, mostra o caractere e a frequência
        if (isFolha()) {
            return "No('" + this.caractere + "', " + this.frequencia + ")";
        }
        // Se for um nó interno, indica isso e mostra a frequência somada
        return "No(interno, " + this.frequencia + ")";
    }
}
