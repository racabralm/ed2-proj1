/*
 * Projeto 1 | Compressão de Arquivos com o Algoritmo de Huffman
 * Prof. Dr. Jean M. Laine
 * Turma 04N
 *
 * Bruna Amorim Maia (RA 10431883)
 * Rafael Araujo Cabral Moreira (RA 10441919)
 * Rute Willemann (RA 10436781)
 */

// Huffman.java

// @param = descrever um parâmetro (ou argumento) específico que um método recebe
// @return = descrever o que o método retorna como resultado

import java.io.*;
import java.util.*;

/**
 * Classe principal que orquestra todo o processo de compressão e descompressão de arquivos
 * utilizando o algoritmo de Huffman
 */
public class Huffman {

    // Define o tamanho da tabela ASCII padrão (0-255) para a contagem de frequências
    private static final int TAMANHO_ASCII = 256;

    /**
     * Ponto de entrada do programa (método main)
     * Responsável por interpretar os argumentos da linha de comando e chamar o método correto
     * @param args Argumentos da linha de comando (-c ou -d, arquivo de entrada, arquivo de saída)
     */
    public static void main(String[] args) {
        // Valida se o número de argumentos está correto
        if (args.length != 3) {
            System.out.println("Uso incorreto. Comandos:");
            System.out.println("Para comprimir: java -jar huffman.jar -c <arquivo_original> <arquivo_comprimido>");
            System.out.println("Para descomprimir: java -jar huffman.jar -d <arquivo_comprimido> <arquivo_restaurado>");
            return; // Encerra o programa se os argumentos estiverem errados
        }

        // Armazena os argumentos em variáveis
        String opcao = args[0];
        String arquivoEntrada = args[1];
        String arquivoSaida = args[2];

        try {
            // Decide qual método chamar com base na opção (-c ou -d)
            if (opcao.equals("-c")) {
                comprimir(arquivoEntrada, arquivoSaida);
            } else if (opcao.equals("-d")) {
                descomprimir(arquivoEntrada, arquivoSaida);
            } else {
                System.out.println("Opção inválida: " + opcao);
            }
        } catch (IOException e) {
            // Captura possíveis erros de leitura ou escrita de arquivos
            System.err.println("Erro ao processar o arquivo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --- LÓGICA DE COMPRESSÃO ---

    /**
     * Organiza todas as etapas do processo de compressão
     * @param arquivoEntrada Caminho do arquivo a ser comprimido
     * @param arquivoSaida Caminho do arquivo comprimido a ser gerado
     */
    public static void comprimir(String arquivoEntrada, String arquivoSaida) throws IOException {
        long tempoInicio = System.nanoTime(); // Marca o início da contagem de tempo

        // Lê todos os bytes do arquivo de uma vez para a memória
        byte[] dadosArquivo = new FileInputStream(arquivoEntrada).readAllBytes();
        
        // ETAPA 1: Análise de Frequência
        int[] tabelaFrequencia = construirTabelaFrequencia(dadosArquivo);
        System.out.println("ETAPA 1: Tabela de Frequencia de Caracteres");
        imprimirTabelaFrequencia(tabelaFrequencia);

        // ETAPA 2: Criação do Min-Heap
        MinHeap minHeap = construirMinHeap(tabelaFrequencia);
        System.out.println("\nETAPA 2: Min-Heap Inicial (Vetor)");
        System.out.println(minHeap.getHeapAsList());

        // ETAPA 3: Construção da Árvore de Huffman
        No raiz = construirArvoreHuffman(minHeap);
        System.out.println("\nETAPA 3: Arvore de Huffman");
        imprimirArvore(raiz, "");

        // ETAPA 4: Geração da Tabela de Códigos
        String[] tabelaCodigos = new String[TAMANHO_ASCII];
        gerarTabelaCodigos(raiz, "", tabelaCodigos);
        System.out.println("\nETAPA 4: Tabela de Codigos de Huffman");
        imprimirTabelaCodigos(tabelaCodigos, tabelaFrequencia);

        // ETAPA 5: Codificação dos Dados e Escrita do Arquivo
        byte[] dadosComprimidos = codificarDados(dadosArquivo, tabelaCodigos);
        escreverArquivoComprimido(arquivoSaida, tabelaFrequencia, dadosComprimidos);
        
        long tempoFim = System.nanoTime(); // Marca o fim da contagem de tempo
        
        // ETAPA 5 (final): Imprime o resumo da compressão
        System.out.println("\nETAPA 5: Resumo da Compressao");
        File original = new File(arquivoEntrada);
        File comprimido = new File(arquivoSaida);
        long tamanhoOriginal = original.length();
        long tamanhoComprimido = comprimido.length();
        // Fórmula da taxa de compressão
        double taxaCompressao = 100.0 * (1.0 - (double)tamanhoComprimido / tamanhoOriginal);

        System.out.printf("Tamanho original...: %d bytes\n", tamanhoOriginal);
        System.out.printf("Tamanho comprimido.: %d bytes\n", tamanhoComprimido);
        System.out.printf("Taxa de compressao.: %.2f%%\n", taxaCompressao);
        System.out.printf("Tempo de compressao: %.3f ms\n", (tempoFim - tempoInicio) / 1e6);
    }

    /**
     * Lê os bytes do arquivo e conta a frequência de cada um
     * @param dados os bytes do arquivo original
     * @return um vetor de inteiros onde o índice é o código ASCII e o valor é a frequência
     */
    private static int[] construirTabelaFrequencia(byte[] dados) {
        int[] freq = new int[TAMANHO_ASCII];
        // Para cada byte no arquivo
        for (byte b : dados) {
            // Incrementa a contagem para o caractere correspondente
            // `b & 0xFF` converte o byte (que pode ser negativo) para um int de 0 a 255
            freq[b & 0xFF]++;
        }
        return freq;
    }

    /**
     * Cria um Min-Heap e o popula com nós-folha para cada caractere presente no arquivo
     * @param tabelaFrequencia o vetor de frequências gerado
     * @return um Min-Heap pronto para a construção da árvore
     */
    private static MinHeap construirMinHeap(int[] tabelaFrequencia) {
        MinHeap minHeap = new MinHeap();
        // Itera por toda a tabela ASCII
        for (int i = 0; i < TAMANHO_ASCII; i++) {
            // Se o caractere apareceu no arquivo (frequência > 0)
            if (tabelaFrequencia[i] > 0) {
                // Cria um nó-folha e o insere no heap
                minHeap.inserir(new No((char) i, tabelaFrequencia[i]));
            }
        }
        return minHeap;
    }

    /**
     * Constrói a Árvore de Huffman a partir do Min-Heap
     * @param minHeap A fila de prioridades com os nós-folha
     * @return A raiz da Árvore de Huffman completa
     */
    private static No construirArvoreHuffman(MinHeap minHeap) {
        // O processo continua enquanto houver mais de um nó no heap
        while (minHeap.tamanho() > 1) {
            // Remove os dois nós de menor frequência
            No esquerda = minHeap.removerMin();
            No direita = minHeap.removerMin();
            
            // Cria um novo nó interno com a soma das frequências dos filhos
            int freqSoma = esquerda.getFrequencia() + direita.getFrequencia();
            No pai = new No(freqSoma, esquerda, direita);
            
            // Insere o novo nó interno de volta no heap
            minHeap.inserir(pai);
        }
        // O último nó restante no heap é a raiz da árvore
        return minHeap.removerMin();
    }

    /**
     * Percorre a Árvore de Huffman recursivamente para gerar os códigos binários
     * @param no O nó atual na recursão (começa com a raiz)
     * @param codigo O código binário acumulado até o momento
     * @param tabelaCodigos O vetor de Strings onde os códigos finais serão armazenados
     */
    private static void gerarTabelaCodigos(No no, String codigo, String[] tabelaCodigos) {
        if (no == null) return;
        
        // Se for um nó folha, encontramos um caractere
        if (no.isFolha()) {
            // Armazena o código acumulado na posição do caractere
            tabelaCodigos[no.getCaractere()] = codigo;
        } else {
            // Se for um nó interno, continua a recursão
            // Adiciona '0' ao código e desce para a esquerda
            gerarTabelaCodigos(no.getEsquerda(), codigo + "0", tabelaCodigos);
            // Adiciona '1' ao código e desce para a direita
            gerarTabelaCodigos(no.getDireita(), codigo + "1", tabelaCodigos);
        }
    }

    /**
     * Converte os dados originais em uma sequência de bytes comprimidos
     * @param dadosOriginais Os bytes do arquivo original
     * @param tabelaCodigos A tabela de mapeamento de caracteres para códigos binários
     * @return Um vetor de bytes representando os dados comprimidos
     */
    private static byte[] codificarDados(byte[] dadosOriginais, String[] tabelaCodigos) {
        // StringBuilder é eficiente para concatenar muitas strings
        StringBuilder sb = new StringBuilder();
        // Monta uma única string gigante com todos os bits do arquivo
        for (byte b : dadosOriginais) {
            sb.append(tabelaCodigos[b & 0xFF]);
        }

        String bitString = sb.toString();
        int tamanho = bitString.length();
        
        // Usa ByteArrayOutputStream para construir o array de bytes de saída
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        // Armazena o número de bits "úteis" no último byte
        // Se o total de bits for múltiplo de 8, o padding é 8, senão é o resto
        int padding = tamanho % 8;
        if (padding == 0) padding = 8;
        baos.write((byte) padding);

        // Itera pela string de bits, agrupando de 8 em 8
        for (int i = 0; i < tamanho; i += 8) {
            // Pega uma substring de até 8 bits
            String byteString = (i + 8 <= tamanho) ? bitString.substring(i, i + 8) : bitString.substring(i);
            // Converte a string binária para um valor inteiro
            int valorByte = Integer.parseInt(byteString, 2);
            // Escreve o byte no stream de saída
            baos.write(valorByte);
        }

        return baos.toByteArray();
    }

    /**
     * Escreve o cabeçalho (tabela de frequência) e os dados comprimidos no arquivo de saída
     * @param nomeArquivo O caminho do arquivo de saída
     * @param tabelaFrequencia O cabeçalho a ser escrito
     * @param dadosComprimidos Os bytes comprimidos
     */
    private static void escreverArquivoComprimido(String nomeArquivo, int[] tabelaFrequencia, byte[] dadosComprimidos) throws IOException {
        // ObjectOutputStream permite escrever objetos Java diretamente em um arquivo (neste caso, o vetor de int)
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(nomeArquivo))) {
            oos.writeObject(tabelaFrequencia); // Escreve o cabeçalho
            oos.write(dadosComprimidos);       // Escreve os dados comprimidos
        }
    }

    // --- LÓGICA DE DESCOMPRESSÃO ---

    /**
     * Orquestra todas as etapas do processo de descompressão
     * @param arquivoEntrada Caminho do arquivo .huff
     * @param arquivoSaida Caminho do arquivo restaurado a ser gerado
     */
    public static void descomprimir(String arquivoEntrada, String arquivoSaida) throws IOException {
        long tempoInicio = System.nanoTime(); // Marca o início do tempo
        
        // ObjectInputStream permite ler objetos Java de um arquivo
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(arquivoEntrada))) {
            // 1 Lê o cabeçalho (a tabela de frequência) do arquivo
            int[] tabelaFrequencia = (int[]) ois.readObject();

            // 2 Reconstrói a Árvore de Huffman a partir da tabela de frequência
            MinHeap minHeap = construirMinHeap(tabelaFrequencia);
            No raiz = construirArvoreHuffman(minHeap);

            // 3 Lê o restante do arquivo, que são os dados comprimidos
            byte[] dadosComprimidos = ois.readAllBytes();
            
            // 4 Decodifica os dados usando a árvore
            byte[] dadosDescomprimidos = decodificarDados(raiz, dadosComprimidos);

            // 5 Escreve os dados originais no arquivo de saída
            try (FileOutputStream fos = new FileOutputStream(arquivoSaida)) {
                fos.write(dadosDescomprimidos);
            }
        } catch (ClassNotFoundException e) {
            // Erro caso o arquivo não contenha um objeto válido
            throw new IOException("Formato de arquivo inválido ou corrompido", e);
        }
        
        long tempoFim = System.nanoTime(); // Marca o fim do tempo
        System.out.println("Arquivo descomprimido com sucesso!");
        System.out.printf("Tempo de descompressão: %.3f ms\n", (tempoFim - tempoInicio) / 1e6);
    }

    /**
     * Converte os dados comprimidos de volta aos dados originais usando a Árvore de Huffman
     * @param raiz A raiz da árvore reconstruída
     * @param dadosComprimidos Os bytes lidos do arquivo .huff
     * @return Um vetor de bytes com os dados originais
     */
    private static byte[] decodificarDados(No raiz, byte[] dadosComprimidos) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        // O primeiro byte dos dados comprimidos é a informação de padding
        int paddingBits = dadosComprimidos[0];
        if (paddingBits == 0) paddingBits = 8; // Se era múltiplo de 8, o padding é 8
        
        // Monta a string de bits a partir dos bytes lidos
        StringBuilder bitString = new StringBuilder();
        for (int i = 1; i < dadosComprimidos.length; i++) {
            // Converte o byte para sua representação binária em string
            String s = Integer.toBinaryString(dadosComprimidos[i] & 0xFF);
            
            // Adiciona zeros à esquerda para garantir que cada byte gere 8 bits
            // Exceto o último byte, que usa a informação de padding
            if (i < dadosComprimidos.length -1 || paddingBits == 8) {
                 s = String.format("%8s", s).replace(' ', '0');
            } else {
                 s = String.format("%" + paddingBits + "s", s).replace(' ', '0');
            }
            bitString.append(s);
        }

        // Percorre a árvore de acordo com a string de bits
        No noAtual = raiz;
        for (int i = 0; i < bitString.length(); i++) {
            // Se o bit for '0', vai para a esquerda
            if (bitString.charAt(i) == '0') {
                noAtual = noAtual.getEsquerda();
            } else { // Se for '1', vai para a direita
                noAtual = noAtual.getDireita();
            }

            // Se chegou a uma folha, um caractere foi decodificado
            if (noAtual.isFolha()) {
                baos.write(noAtual.getCaractere());
                // Volta para a raiz para decodificar o próximo caractere
                noAtual = raiz;
            }
        }
        return baos.toByteArray();
    }
    
    // --- MÉTODOS DE IMPRESSÃO PARA O CONSOLE ---

    private static void imprimirTabelaFrequencia(int[] tabela) {
        for (int i = 0; i < TAMANHO_ASCII; i++) {
            if (tabela[i] > 0) {
                System.out.printf("Caractere '%c' (ASCII: %d): %d\n", (char) i, i, tabela[i]);
            }
        }
    }

    private static void imprimirArvore(No no, String prefixo) {
        if (no == null) return;
        
        boolean isFolha = no.isFolha();
        String caractere = isFolha ? "'" + no.getCaractere() + "'" : "RAIZ/Nó";
        System.out.println(prefixo + "-> (" + caractere + ", " + no.getFrequencia() + ")");
        
        if (!isFolha) {
            imprimirArvore(no.getEsquerda(), prefixo + "  |--(0)");
            imprimirArvore(no.getDireita(), prefixo + "  |--(1)");
        }
    }

    private static void imprimirTabelaCodigos(String[] tabelaCodigos, int[] tabelaFrequencia) {
        for (int i = 0; i < TAMANHO_ASCII; i++) {
            if (tabelaFrequencia[i] > 0) {
                System.out.printf("Caractere '%c': %s\n", (char) i, tabelaCodigos[i]);
            }
        }
    }
}
