package modelo.bplus;

import java.util.*;

public class BPlusTree<K extends Comparable<K>, V> {
    private Node<K, V> root;
    private int maxKeys;

    public BPlusTree(int maxKeys) {
        this.maxKeys = maxKeys;
        this.root = new LeafNode<>();
    }

    public void insert(K key, V value) {
        SplitResult<K, V> result = root.insert(key, value, maxKeys);
        if (result != null) {
            InternalNode<K, V> newRoot = new InternalNode<>();
            newRoot.keys.add(result.key);
            newRoot.children.add(result.left);
            newRoot.children.add(result.right);
            this.root = newRoot;
        }
    }

    public V search(K key) {
        return root.search(key);
    }

    public void delete(K key) {
        root.delete(key);
    }

    public void printLeaves() {
        root.printLeaves();
    }

    public Map<K, V> getAllLeaves() {
        Map<K, V> resultado = new LinkedHashMap<>();
        Node<K, V> node = root;
        while (node instanceof InternalNode) {
            node = ((InternalNode<K, V>) node).children.get(0);
        }
        LeafNode<K, V> hoja = (LeafNode<K, V>) node;
        while (hoja != null) {
            for (int i = 0; i < hoja.keys.size(); i++) {
                resultado.put(hoja.keys.get(i), hoja.values.get(i));
            }
            hoja = hoja.next;
        }
        return resultado;
    }

    public List<String> getLeafNodesAsStrings() {
        List<String> result = new ArrayList<>();
        Node<K, V> node = root;
        while (node instanceof InternalNode) {
            node = ((InternalNode<K, V>) node).children.get(0);
        }
        LeafNode<K, V> hoja = (LeafNode<K, V>) node;
        while (hoja != null) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < hoja.keys.size(); i++) {
                sb.append(hoja.keys.get(i)).append(":").append(hoja.values.get(i)).append("  ");
            }
            result.add(sb.toString().trim());
            hoja = hoja.next;
        }
        return result;
    }
}


// Clases internas del árbol B+

abstract class Node<K extends Comparable<K>, V> {
    abstract SplitResult<K, V> insert(K key, V value, int maxKeys);
    abstract V search(K key);
    abstract void delete(K key);
    abstract void printLeaves();
}

class LeafNode<K extends Comparable<K>, V> extends Node<K, V> {
    List<K> keys = new ArrayList<>();
    List<V> values = new ArrayList<>();
    LeafNode<K, V> next;

    @Override
    SplitResult<K, V> insert(K key, V value, int maxKeys) {
        int index = Collections.binarySearch(keys, key);
        if (index >= 0) {
            values.set(index, value);
            return null;
        }
        index = -index - 1;
        keys.add(index, key);
        values.add(index, value);

        if (keys.size() > maxKeys) {
            return split();
        }
        return null;
    }

    private SplitResult<K, V> split() {
        int mid = keys.size() / 2;
        LeafNode<K, V> sibling = new LeafNode<>();
        sibling.keys.addAll(keys.subList(mid, keys.size()));
        sibling.values.addAll(values.subList(mid, values.size()));

        keys = new ArrayList<>(keys.subList(0, mid));
        values = new ArrayList<>(values.subList(0, mid));

        sibling.next = this.next;
        this.next = sibling;

        return new SplitResult<>(sibling.keys.get(0), this, sibling);
    }

    @Override
    V search(K key) {
        int index = Collections.binarySearch(keys, key);
        return index >= 0 ? values.get(index) : null;
    }

    @Override
    void delete(K key) {
        int index = Collections.binarySearch(keys, key);
        if (index >= 0) {
            keys.remove(index);
            values.remove(index);
        }
    }

    @Override
    void printLeaves() {
        LeafNode<K, V> current = this;
        while (current != null) {
            for (int i = 0; i < current.keys.size(); i++) {
                System.out.println(current.keys.get(i) + " -> " + current.values.get(i));
            }
            current = current.next;
        }
    }
}

class InternalNode<K extends Comparable<K>, V> extends Node<K, V> {
    List<K> keys = new ArrayList<>();
    List<Node<K, V>> children = new ArrayList<>();

    @Override
    SplitResult<K, V> insert(K key, V value, int maxKeys) {
        int index = findChildIndex(key);
        SplitResult<K, V> result = children.get(index).insert(key, value, maxKeys);

        if (result != null) {
            keys.add(index, result.key);
            children.set(index, result.left);
            children.add(index + 1, result.right);

            if (keys.size() > maxKeys) {
                return split();
            }
        }
        return null;
    }

    private int findChildIndex(K key) {
        int i = 0;
        while (i < keys.size() && key.compareTo(keys.get(i)) >= 0) i++;
        return i;
    }

    private SplitResult<K, V> split() {
        int mid = keys.size() / 2;
        InternalNode<K, V> sibling = new InternalNode<>();
        sibling.keys.addAll(keys.subList(mid + 1, keys.size()));
        sibling.children.addAll(children.subList(mid + 1, children.size()));

        K midKey = keys.get(mid);
        keys = new ArrayList<>(keys.subList(0, mid));
        children = new ArrayList<>(children.subList(0, mid + 1));

        return new SplitResult<>(midKey, this, sibling);
    }

    @Override
    V search(K key) {
        return children.get(findChildIndex(key)).search(key);
    }

    @Override
    void delete(K key) {
        children.get(findChildIndex(key)).delete(key);
    }

    @Override
    void printLeaves() {
        if (!children.isEmpty()) {
            children.get(0).printLeaves();
        }
    }
}

class SplitResult<K extends Comparable<K>, V> {
    K key;
    Node<K, V> left, right;

    public SplitResult(K key, Node<K, V> left, Node<K, V> right) {
        this.key = key;
        this.left = left;
        this.right = right;
    }
}


