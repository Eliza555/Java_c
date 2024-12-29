package huffmanBin;
import java.io.*;
import java.util.*;

import java.io.*;
import java.util.*;

public class HuffmanCoding {

    // Метод для построения частотной таблицы
    private Map<Character, Integer> buildFrequencyTable(String text) {
        Map<Character, Integer> frequency = new HashMap<>();
        for (char c : text.toCharArray()) {
            frequency.put(c, frequency.getOrDefault(c, 0) + 1);
        }
        return frequency;
    }

    // Метод для построения дерева Хаффмана
    private HuffmanNode buildHuffmanTree(Map<Character, Integer> frequency) {
        PriorityQueue<HuffmanNode> pq = new PriorityQueue<>(Comparator.comparingInt(n -> n.frequency));

        for (Map.Entry<Character, Integer> entry : frequency.entrySet()) {
            pq.add(new HuffmanNode(entry.getKey(), entry.getValue()));
        }

        // Если есть только один уникальный символ, добавляем фиктивный узел
        if (pq.size() == 1) {
            pq.add(new HuffmanNode('\0', 1)); // '\0' как фиктивный символ
        }

        while (pq.size() > 1) {
            HuffmanNode left = pq.poll();
            HuffmanNode right = pq.poll();
            HuffmanNode parent = new HuffmanNode(left.frequency + right.frequency, left, right);
            pq.add(parent);
        }

        return pq.poll();
    }

    // Метод для построения таблицы кодов
    private Map<Character, String> buildCodes(HuffmanNode root) {
        Map<Character, String> codes = new HashMap<>();
        if (root == null) return codes;

        Stack<NodeWithCode> stack = new Stack<>();
        stack.push(new NodeWithCode(root, ""));

        while (!stack.isEmpty()) {
            NodeWithCode current = stack.pop();
            HuffmanNode node = current.node;
            String code = current.code;

            if (node.left == null && node.right == null) {
                // Если код пустой, назначаем '0' для единственного символа
                if (code.isEmpty()) {
                    codes.put(node.character, "0");
                } else {
                    codes.put(node.character, code);
                }
            } else {
                if (node.right != null) {
                    stack.push(new NodeWithCode(node.right, code + "1"));
                }
                if (node.left != null) {
                    stack.push(new NodeWithCode(node.left, code + "0"));
                }
            }
        }

        return codes;
    }
    // Вспомогательный класс для итеративного обхода дерева
    private static class NodeWithCode {
        HuffmanNode node;
        String code;

        NodeWithCode(HuffmanNode node, String code) {
            this.node = node;
            this.code = code;
        }
    }
    // Метод для сериализации дерева в поток
    private void serializeTree(HuffmanNode root, DataOutputStream out) throws IOException {
        if (root == null) return;
        Stack<HuffmanNode> stack = new Stack<>();
        stack.push(root);
        while (!stack.isEmpty()) {
            HuffmanNode node = stack.pop();
            if (node.left == null && node.right == null) {
                out.writeBoolean(true);
                out.writeChar(node.character);
            } else {
                out.writeBoolean(false);
                // Сначала правого, затем левого, чтобы левый обрабатывался первым
                if (node.right != null) stack.push(node.right);
                if (node.left != null) stack.push(node.left);
            }
        }
    }

    // Метод для десериализации дерева из потока
    public HuffmanNode deserializeTree(DataInputStream in) throws IOException {
        HuffmanNode root;
        boolean isLeaf;

        // Попытка чтения первого узла (корня дерева)
        try {
            isLeaf = in.readBoolean();
        } catch (IOException e) {
            throw new IOException("Поток пуст или поврежден", e);
        }

        if (isLeaf) {
            // Если корень - лист
            char c = in.readChar();
            root = new HuffmanNode(c, 0);
        } else {
            // Если корень - внутренний узел
            root = new HuffmanNode(0, null, null);
        }

        // Стек для отслеживания узлов
        Stack<HuffmanNode> stack = new Stack<>();
        if (!isLeaf) {
            stack.push(root);
        }

        while (!stack.isEmpty()) {
            HuffmanNode current = stack.peek();

            if (current.left == null) {
                // Чтение левого
                try {
                    isLeaf = in.readBoolean();
                } catch (IOException e) {
                    throw new IOException("Некорректный формат файла: ожидается информация о левом ребенке", e);
                }

                if (isLeaf) {
                    // Если левый - лист
                    char c = in.readChar();
                    HuffmanNode leftChild = new HuffmanNode(c, 0);
                    current.left = leftChild;
                } else {
                    // Если - внутренний узел
                    HuffmanNode leftChild = new HuffmanNode(0, null, null);
                    current.left = leftChild;
                    stack.push(leftChild);
                }
            } else if (current.right == null) {
                // Чтение правого
                try {
                    isLeaf = in.readBoolean();
                } catch (IOException e) {
                    throw new IOException("Некорректный формат файла ", e);
                }

                if (isLeaf) {
                    // Если - лист
                    char c = in.readChar();
                    HuffmanNode rightChild = new HuffmanNode(c, 0);
                    current.right = rightChild;
                } else {
                    // Если - внутренний узел
                    HuffmanNode rightChild = new HuffmanNode(0, null, null);
                    current.right = rightChild;
                    stack.push(rightChild);
                }
            } else {
                stack.pop();
            }
        }

        return root;
    }


    // Метод для кодирования файла
    public void encode(String inputPath, String outputPath, boolean displayCodes) throws IOException {
        // Чтение исходного текста
        StringBuilder text = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(inputPath))) {
            int c;
            while ((c = reader.read()) != -1) {
                text.append((char) c);
            }
        }

        // Построение частотной таблицы и дерева Хаффмана
        Map<Character, Integer> frequency = buildFrequencyTable(text.toString());
        HuffmanNode root = buildHuffmanTree(frequency);

        // Построение таблицы кодов
        Map<Character, String> codes = buildCodes(root);

        if (displayCodes) {
            System.out.println("Коды символов:");
            for (Map.Entry<Character, String> entry : codes.entrySet()) {
                System.out.println("'" + entry.getKey() + "': " + entry.getValue());
            }
        }

        // Сериализация дерева и запись закодированных данных
        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(outputPath))) {
            // Сначала запишем дерево
            serializeTree(root, out);

            // Потом закодированные данные
            StringBuilder encodedText = new StringBuilder();
            for (char c : text.toString().toCharArray()) {
                encodedText.append(codes.get(c));
            }

            // Запись длины закодированного текста
            out.writeInt(encodedText.length());

            // Запись битов
            byte b = 0;
            int bitCount = 0;
            for (char bitChar : encodedText.toString().toCharArray()) {
                b <<= 1;
                if (bitChar == '1') {
                    b |= 1;
                }
                bitCount++;
                if (bitCount == 8) {
                    out.writeByte(b);
                    bitCount = 0;
                    b = 0;
                }
            }
            if (bitCount > 0) {
                b <<= (8 - bitCount);
                out.writeByte(b);
            }
        }
    }

    // Метод для декодирования файла
    public void decode(String inputPath, String outputPath, boolean displayCodes) throws IOException {
        try (DataInputStream in = new DataInputStream(new FileInputStream(inputPath))) {
            // Десериализуем дерево
            HuffmanNode root = deserializeTree(in);

            if (root == null) {
                throw new IOException("Пустой файл или неверный формат");
            }

            // Построение таблицы кодов для отображения (если требуется)
            if (displayCodes) {
                Map<Character, String> codes = buildCodes(root);
                System.out.println("Коды символов:");
                for (Map.Entry<Character, String> entry : codes.entrySet()) {
                    System.out.println
                            ("'" + entry.getKey() + "': " + entry.getValue());
                }
            }

            // Чтение длины закодированного текста
            int encodedLength = in.readInt();

            // Чтение битов и декодирование
            StringBuilder decodedText = new StringBuilder();
            HuffmanNode current = root;
            int bitsProcessed = 0;

            while (bitsProcessed < encodedLength) {
                byte currentByte;
                try {
                    currentByte = in.readByte();
                } catch (EOFException e) {
                    throw new IOException("Конец файла неожиданно достигнут при декодировании.");
                }

                for (int bit = 7; bit >= 0 && bitsProcessed < encodedLength; bit--, bitsProcessed++) {
                    boolean isOne = ((currentByte >> bit) & 1) == 1;
                    current = isOne ? current.right : current.left;

                    if (bit == '0') {
                        current = current.left;
                    }

                    if (current.left == null && current.right == null) {
                        decodedText.append(current.character);
                        current = root;
                    }
                }
            }

            // Запись декодированного текста
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
                writer.write(decodedText.toString());
            }
        }
    }
}
