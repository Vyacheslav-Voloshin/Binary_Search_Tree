import java.io.Serializable;
import java.util.*;

public class CustomTree extends AbstractList<String> implements Serializable,Cloneable {
    /**
     * ����������  ����� ��� ������;
     */
    Entry<String> root;

    /**
     * �������� ��� ��������� �������� ������.
     */
    private transient ArrayList<Entry<String>> queue;

    /**
     * root ���������������� �� ���������, � ���������� ������ ��������� ������������ ��� "����� �������� ������� ���
     * �������� ������ ������"; newLineRootElement - ������ ������� ����� ������ ������� ������.
     */
    public CustomTree() {
        root = new Entry<String>(null);
        root.newLineRootElement = true;
        root.lineNumber = 0;
    }

    /**
     * ����� getParent ��������� �������� String value, ���� � ����� Entry c ��������� elementName �������������
     * �������� value, � ���������� �������� elementName � ���������� Entry (Entry.parent)
     *
     * @param value elementName ����������� Entry
     * @return parent.elementName ���������� Entry (Entry.parent)
     */
    public String getParent(String value) {
        setValidCollection();
        String s = null;
        for (Entry<String> entry : queue) {
            if (entry.lineNumber != 1) {
                if (entry.elementName.equals(value)) {
                    s = entry.parent.elementName;
                    break;
                }
            }
        }
        return s;
    }

    /**
     * ����� setUpCollection �������� �� ������, ������� � �������� Entry<String> root � �������������� ��� �������� �
     * queue;
     *
     * @param root ��������� ������� Entry<String> ��� ������������� ������� �� ������.
     */
    private void setUpCollection(Entry<String> root) {
        queue = new ArrayList<>();
        Queue<Entry<String>> subQueue = new LinkedList<Entry<String>>();
        queue.add(root);
        subQueue.add(root);
        do {
            if (!subQueue.isEmpty()) {
                root = subQueue.poll();
            }
            if (root.leftChild != null) {
                queue.add(root.leftChild);
                subQueue.add(root.leftChild);
            }
            if (root.rightChild != null) {
                queue.add(root.rightChild);
                subQueue.add(root.rightChild);
            }
        } while (!subQueue.isEmpty());

    }

    /**
     * ����� setValidCollection ���������� ������ ��������� ��������� ������, � ����� ������� 1 ������� ���������,
     * ������� �������� ����������� ������.
     * ������������ � ������� remove � �.�. ��� ��������� ������� �� ���������.
     */
    private void setValidCollection() {
        setUpCollection(root);
        queue.remove(0);
    }

    @Override
    public String set(int index, String element) {
        throw new UnsupportedOperationException();
    }

    /**
     * ����� add ��������� ������� � ������. ������� ��������������� ������ ��������� ���������, ����� ����������
     * �������� �� ���������. ��� �������� ������ ������� ����������� ������� checkChildren �� ����������� �����
     * �����. isAvailableToAddChildren() ���������� true ���� ����� ����������� �������; ����� ���� ��������� �
     * ���������������� ����� ������� Entry<String>, ����������� � ���������. ����� ���� ��������� ������
     * ����������� � ���������������� � ������� setValidCollection();
     *
     * @param s ������ (String) ������� ���������� �������� � ���������;
     * @return true ����� ���������� ������ ��������;
     */
    @Override
    public boolean add(String s) {
        setUpCollection(root);
        for (Entry<String> entry : queue) {
            entry.checkChildren();
            if (entry.isAvailableToAddChildren()) {
                createChild(entry, s);
                setValidCollection();
                return true;
            }
        }

        return false;
    }

    @Override
    public void add(int index, String element) {
        throw new UnsupportedOperationException();
    }

    /**
     * ���. ����� ��� ������������. ��� �������� ������ ���������� 2 �������� newLineRootElement;
     * ���� ����� �������� ����������� ��� ����� �������� � ������� ����� newLineRootElement;
     *
     * @param entry Entry<String> �� ��������� true ���������� newLineRootElement, ������� ��������
     *              ���������;
     * @return ������ ������� � Entry<String> entry � ���������� ����� ��� parent �� ��������� true
     * ���������� newLineRootElement, ������� �������� ���������;
     */
    private List<Entry<String>> getNewLineRootElementsCollection(Entry<String> entry) {
        ArrayList<Entry<String>> list = new ArrayList<>();
        list.add(entry);
        if ((entry.parent != null) && (entry.parent.newLineRootElement)) {
            list.addAll(getNewLineRootElementsCollection(entry.parent));
        }
        return list;
    }

    /**
     * ����� createChild ������� ����� ������� Entry<String> � ������������� �������� ���������� parent
     *
     * @param parent ������������ ������� Entry<String>;
     * @param s      �������� elementName ��� ������ �������� Entry<String>;
     */
    private void createChild(Entry<String> parent, String s) {
        Entry<String> newOne = new Entry<String>(s);
        newOne.parent = parent;
        newOne.lineNumber = parent.lineNumber + 1;
        setChild(parent, newOne);
    }

    /**
     * ����� setChild ����������� ���������� left/rightChild �������� ������ �� ������� Entry<String> child;
     * ���� ���������� newLineRootElement �������� ����� �������� true, �� ��� �������� ���������� �������, �
     * �������� ��������� �� false;
     *
     * @param parent ������������ ������� Entry<String>
     * @param child  �������-������� Entry<String>
     */
    private void setChild(Entry<String> parent, Entry<String> child) {
        if (parent.availableToAddLeftChildren) {
            parent.leftChild = child;
            parent.availableToAddLeftChildren = false;
            if (parent.newLineRootElement) {
                List<Entry<String>> list = getNewLineRootElementsCollection(parent);
                for (Entry<String> entry : list) {
                    entry.newLineRootElement = false;
                }
                child.newLineRootElement = true;
            }
        } else {
            parent.rightChild = child;
            parent.availableToAddRightChildren = false;
            if (parent.newLineRootElement) {
                List<Entry<String>> list = getNewLineRootElementsCollection(parent);
                for (Entry<String> entry : list) {
                    entry.newLineRootElement = false;
                }
                child.newLineRootElement = true;
            }
        }
    }

    /**
     * Unsupported Operation
     *
     * @param index param
     * @return UnsupportedOperationException();
     */
    @Override
    public String get(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int index, Collection<? extends String> c) {
        throw new UnsupportedOperationException();
    }

    /**
     * @return ������ ��������� ��������� Entry<String>;
     */
    @Override
    public int size() {
        setValidCollection();
        return queue.size();
    }

    @Override
    public String remove(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<String> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }

    static class Entry<T> implements Serializable {

        String elementName;
        int lineNumber;
        boolean availableToAddLeftChildren, availableToAddRightChildren;
        boolean newLineRootElement;
        Entry<T> parent, leftChild, rightChild;

        private Entry(String name) {
            elementName = name;
            newLineRootElement = false;
            availableToAddLeftChildren = true;
            availableToAddRightChildren = true;
        }

        private void checkChildren() {
            if (this.leftChild != null) {
                availableToAddLeftChildren = false;
            }
            if (this.rightChild != null) {
                availableToAddRightChildren = false;
            }
        }

        public boolean isAvailableToAddChildren() {
            return this.availableToAddRightChildren || this.availableToAddLeftChildren;
        }
    }
}
