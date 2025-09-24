// Adicione os nomes dos integrantes do grupo aqui
// Nome 1, Nome 2, Nome 3, Nome 4

import java.io.*;
import java.util.*;

public class Huffman {

    private static final int TAMANHO_ASCII = 256;

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Uso incorreto. Comandos:");
            System.out.println("Para comprimir: java -jar huffman.jar -c <arquivo_original> <arquivo_comprimido>");
            System.out.println("Para descomprimir: java -jar huffman.jar -d <arquivo_comprimido> <arquivo_restaurado>");
            return;
        }

        String opcao = args[0];
        String arquivoEntrada = args[1];
        String arquivoSaida = args[2];

        try {
            if (opcao.equals("-c")) {
                comprimir(arquivoEntrada, arquivoSaida);
            } else if (opcao.equals("-d")) {
                descomprimir(arquivoEntrada, arquivoSaida);
            } else {
                System.out.println("Opção inválida: " + opcao);
            }
        } catch (IOException e) {
            System.err.println("Erro ao processar o arquivo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --- LÓGICA DE COMPRESSÃO ---

    public static void comprimir(String arquivoEntrada, String arquivoSaida) throws IOException {
        long tempoInicio = System.nanoTime();

        // Passo 1: Análise de Frequência
        byte[] dadosArquivo = new FileInputStream(arquivoEntrada).readAllBytes();
        int[] tabelaFrequencia = construirTabelaFrequencia(dadosArquivo);
        System.out.println("ETAPA 1: Tabela de Frequencia de Caracteres");
        imprimirTabelaFrequencia(tabelaFrequencia);

        // Passo 2: Construção do Min-Heap
        MinHeap minHeap = construirMinHeap(tabelaFrequencia);
        System.out.println("\nETAPA 2: Min-Heap Inicial (Vetor)");
        System.out.println(minHeap.getHeapAsList());

        // Passo 3: Construção da Árvore de Huffman
        No raiz = construirArvoreHuffman(minHeap);
        System.out.println("\nETAPA 3: Arvore de Huffman");
        imprimirArvore(raiz, "");

        // Passo 4: Geração da Tabela de Códigos
        String[] tabelaCodigos = new String[TAMANHO_ASCII];
        gerarTabelaCodigos(raiz, "", tabelaCodigos);
        System.out.println("\nETAPA 4: Tabela de Codigos de Huffman");
        imprimirTabelaCodigos(tabelaCodigos, tabelaFrequencia);

        // Passo 5: Codificação e Escrita do Arquivo
        byte[] dadosComprimidos = codificarDados(dadosArquivo, tabelaCodigos);
        escreverArquivoComprimido(arquivoSaida, tabelaFrequencia, dadosComprimidos);
        
        long tempoFim = System.nanoTime();
        
        // Etapa 5: Resumo
        System.out.println("\nETAPA 5: Resumo da Compressao");
        File original = new File(arquivoEntrada);
        File comprimido = new File(arquivoSaida);
        long tamanhoOriginal = original.length();
        long tamanhoComprimido = comprimido.length();
        double taxaCompressao = 100.0 * (1.0 - (double)tamanhoComprimido / tamanhoOriginal);

        System.out.printf("Tamanho original...: %d bytes\n", tamanhoOriginal);
        System.out.printf("Tamanho comprimido.: %d bytes\n", tamanhoComprimido);
        System.out.printf("Taxa de compressao.: %.2f%%\n", taxaCompressao);
        System.out.printf("Tempo de compressao: %.3f ms\n", (tempoFim - tempoInicio) / 1e6);
    }

    private static int[] construirTabelaFrequencia(byte[] dados) {
        int[] freq = new int[TAMANHO_ASCII];
        for (byte b : dados) {
            freq[b & 0xFF]++;
        }
        return freq;
    }

    private static MinHeap construirMinHeap(int[] tabelaFrequencia) {
        MinHeap minHeap = new MinHeap();
        for (int i = 0; i < TAMANHO_ASCII; i++) {
            if (tabelaFrequencia[i] > 0) {
                minHeap.inserir(new No((char) i, tabelaFrequencia[i]));
            }
        }
        return minHeap;
    }

    private static No construirArvoreHuffman(MinHeap minHeap) {
        while (minHeap.tamanho() > 1) {
            No esquerda = minHeap.removerMin();
            No direita = minHeap.removerMin();
            int freqSoma = esquerda.getFrequencia() + direita.getFrequencia();
            No pai = new No(freqSoma, esquerda, direita);
            minHeap.inserir(pai);
        }
        return minHeap.removerMin();
    }

    private static void gerarTabelaCodigos(No no, String codigo, String[] tabelaCodigos) {
        if (no == null) return;
        if (no.isFolha()) {
            tabelaCodigos[no.getCaractere()] = codigo;
        } else {
            gerarTabelaCodigos(no.getEsquerda(), codigo + "0", tabelaCodigos);
            gerarTabelaCodigos(no.getDireita(), codigo + "1", tabelaCodigos);
        }
    }

    private static byte[] codificarDados(byte[] dadosOriginais, String[] tabelaCodigos) {
        StringBuilder sb = new StringBuilder();
        for (byte b : dadosOriginais) {
            sb.append(tabelaCodigos[b & 0xFF]);
        }

        String bitString = sb.toString();
        int tamanho = bitString.length();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        // Guarda o número de bits para lidar com padding no final
        baos.write((byte) (tamanho % 8));

        for (int i = 0; i < tamanho; i += 8) {
            String byteString = (i + 8 <= tamanho) ? bitString.substring(i, i + 8) : bitString.substring(i);
            int valorByte = Integer.parseInt(byteString, 2);
            baos.write(valorByte);
        }

        return baos.toByteArray();
    }

    private static void escreverArquivoComprimido(String nomeArquivo, int[] tabelaFrequencia, byte[] dadosComprimidos) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(nomeArquivo))) {
            oos.writeObject(tabelaFrequencia);
            oos.write(dadosComprimidos);
        }
    }

    // --- LÓGICA DE DESCOMPRESSÃO ---

    public static void descomprimir(String arquivoEntrada, String arquivoSaida) throws IOException {
        long tempoInicio = System.nanoTime();
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(arquivoEntrada))) {
            int[] tabelaFrequencia = (int[]) ois.readObject();

            MinHeap minHeap = construirMinHeap(tabelaFrequencia);
            No raiz = construirArvoreHuffman(minHeap);

            byte[] dadosComprimidos = ois.readAllBytes();
            byte[] dadosDescomprimidos = decodificarDados(raiz, dadosComprimidos);

            try (FileOutputStream fos = new FileOutputStream(arquivoSaida)) {
                fos.write(dadosDescomprimidos);
            }
        } catch (ClassNotFoundException e) {
            throw new IOException("Formato de arquivo inválido ou corrompido.", e);
        }
        
        long tempoFim = System.nanoTime();
        System.out.println("Arquivo descomprimido com sucesso!");
        System.out.printf("Tempo de descompressão: %.3f ms\n", (tempoFim - tempoInicio) / 1e6);
    }

    private static byte[] decodificarDados(No raiz, byte[] dadosComprimidos) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        int paddingBits = dadosComprimidos[0];
        if (paddingBits == 0) paddingBits = 8;
        
        StringBuilder bitString = new StringBuilder();
        for (int i = 1; i < dadosComprimidos.length; i++) {
            String s = Integer.toBinaryString(dadosComprimidos[i] & 0xFF);
            
            // Adiciona padding à esquerda se necessário
            if (i < dadosComprimidos.length -1 || paddingBits == 8) {
                 s = String.format("%8s", s).replace(' ', '0');
            } else {
                 s = String.format("%" + paddingBits + "s", s).replace(' ', '0');
            }
            bitString.append(s);
        }

        No noAtual = raiz;
        for (int i = 0; i < bitString.length(); i++) {
            if (bitString.charAt(i) == '0') {
                noAtual = noAtual.getEsquerda();
            } else {
                noAtual = noAtual.getDireita();
            }

            if (noAtual.isFolha()) {
                baos.write(noAtual.getCaractere());
                noAtual = raiz;
            }
        }
        return baos.toByteArray();
    }
    
    // --- MÉTODOS DE IMPRESSÃO ---

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