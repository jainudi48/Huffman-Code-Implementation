import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.lang.IllegalStateException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.Logger;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.File;
import java.nio.IntBuffer;
import java.nio.ByteBuffer;

class Node
{
    private long frequency;
    private String ch;

    private Node leftChild;
    private Node rightChild;

    public void setFrequency(long frequency){this.frequency = frequency;}
    public void setString(String ch){this.ch = ch;}
    public void setLeftChild(Node leftChild){this.leftChild = leftChild;}
    public void setRightChild(Node rightChild){this.rightChild = rightChild;}

    public long getFrequency(){return this.frequency;}
    public String getString(){return this.ch;}
    public Node getLeftChild(){return this.leftChild;}
    public Node getRightChild(){return this.rightChild;}
    public Node(long frequency, Node leftChild, Node rightChild)
    {
        setFrequency(frequency);
        setString(null);
        setLeftChild(leftChild);
        setRightChild(leftChild);
    }
    public Node(long frequency, String ch, Node leftChild, Node rightChild)
    {
        setFrequency(frequency);
        setString(ch);
        setLeftChild(leftChild);
        setRightChild(rightChild);
    }

    public Node(long frequency, String ch)
    {
        setFrequency(frequency);
        setString(ch);
        setLeftChild(null);
        setRightChild(null);
    }
    public void print()
    {
        System.out.println("Data : " + this.getString() + "\tFrequency : " + this.getFrequency() + "\tLeftChild : " + this.getLeftChild().getString() + "\tRightChild : " + this.getRightChild().getString());
    }
}


class HuffmanMinHeap
{
    private int capacity = 10;
    private int size = 0;
    
    Node[] items = new Node[capacity];

    public int getSize()
    {
        return size;
    }

    private int getLeftChildIndex(int parentIndex){return 2 * parentIndex + 1;}
    private int getRightChildIndex(int parentIndex){ return 2* parentIndex + 2;}
    private int getParentIndex(int childIndex){return (childIndex - 1) / 2;}
    
    private boolean hasLeftChild(int index){return getLeftChildIndex(index) < size;}
    private boolean hasRightChild(int index){return getRightChildIndex(index)<size;}
    private boolean hasParent(int index){return getParentIndex(index)>=0;}

    private Node leftChild(int index){return items[getLeftChildIndex(index)];}
    private Node rightChild(int index){return items[getRightChildIndex(index)];}
    private Node parent(int index){return items[getParentIndex(index)];}

    private void swap(int indexOne, int indexTwo)
    {
        Node temp = items[indexOne];
        items[indexOne] = items[indexTwo];
        items[indexTwo] = temp;
    }

    public void ensureCapacity()
    {
        if(size==capacity)
        {
            items = Arrays.copyOf(items, capacity * 2);
            capacity *= 2;
        }
    }

    public Node peepMin()
    {
        if(size == 0)
        {
            return null;
        }
        return items[0]; 
    }

    public Node extractMin()
    {
        if(size == 0)
        {
            return null;
        }
        Node item = items[0];
        items[0] = items[size - 1];
        size--;
        heapifyDown();
        return item;
    }
    public void add(Node item)
    {
        ensureCapacity();
        items[size] = item;
        size++;
        heapifyUp(); 
    } 
    public void heapifyUp()
    {
          int index = size - 1;
          while(hasParent(index) && parent(index).getFrequency() > items[index].getFrequency())
          {
              swap(getParentIndex(index), index);
              index = getParentIndex(index);
          }
    }
    public void heapifyDown()
    {
        int index = 0;
        while(hasLeftChild(index))
        {
            int smallerChildIndex = getLeftChildIndex(index);
            if(hasRightChild(index) && rightChild(index).getFrequency() < leftChild(index).getFrequency())
            {
                smallerChildIndex = getRightChildIndex(index);
            }
            if(items[index].getFrequency() < items[smallerChildIndex].getFrequency())
            {
                break;
            }
            else{
                swap(index, smallerChildIndex);
                index = smallerChildIndex;
            }
        }
    }
    public void print()
    {    
        for(int i=0; i<size; i++)
        {
            System.out.println("character : " + items[i].getString() + "\t" + "frequency : " + items[i].getFrequency());
        }
    }
}

class MainClass
{
    HuffmanMinHeap heap = null;
    ArrayList<DataSet> dataList = new ArrayList<DataSet>();
    ArrayList<DataSet> builtHuffmanDataList = new ArrayList<DataSet>();
    String serialisedFileName = "DataRecordFile.txt";
    String filePath; 
    public static void main(String[] args) {

        MainClass mc = new MainClass();
        mc.implementHuffman(args[0]);
        
    }
    private void implementHuffman(String filePath)
    {
        heap = new HuffmanMinHeap();
        this.filePath = filePath;
        readFile();
        addNodes();
        // printHeap();      

        Node root = encode();
        int arr[] = new int[10000], top = 0;
        System.out.println("Value\tFrequency\tHuffman Code");
        printHuffmanCode(root, arr, top);
        writeEncodedFile();
    }
    
    private void addNodes()
    {
        copyDataSetToExecute();
        // heap.add(new Node(5, "a"));
        // heap.add(new Node(9, "b"));
        // heap.add(new Node(12, "c"));
        // heap.add(new Node(23, "d"));
        // heap.add(new Node(16, "e"));
        // heap.add(new Node(45, "f"));   
    }
    private void printHeap()
    {
        heap.print();
    }

    private Node encode()
    {
        Node min1 = null,  min2 = null;
        int sumOfFrequencies = 0;
        while(heap.getSize() > 1)
        {
            Node newNode = null;
            sumOfFrequencies = 0;
            min1 = heap.extractMin();
            sumOfFrequencies += min1.getFrequency();
            
            min2 = heap.extractMin();
            sumOfFrequencies += min2.getFrequency();
            
            newNode = new Node(sumOfFrequencies, "$", min1, min2);
            heap.add(newNode);
        }
        return heap.peepMin();
    }
    private void printHuffmanCode(Node root, int arr[], int top)
    {
        if(root.getLeftChild()!=null)
        {
            arr[top] = 0;
            printHuffmanCode(root.getLeftChild(), arr, top+1);
        }
        if(root.getRightChild()!=null)
        {
            arr[top] = 1;
            printHuffmanCode(root.getRightChild(), arr, top+1);
        }
        if(root.getLeftChild()==null && root.getRightChild() == null)
        {
            System.out.print(root.getString() + "\t" + root.getFrequency() + "\t\t" );
            for(int i=0; i<top; i++)
            {
                System.out.print(arr[i]);
            }
            System.out.println("");
            
            DataSet ds = new DataSet();
            ds.setValue(Integer.parseInt(root.getString()));
            ds.setFrequency(root.getFrequency());
            ds.setHuffmanCode(arr);
            builtHuffmanDataList.add(ds);
        }
    }

    private void readFile()
    {
        try{
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            int c;
            try{
                while((c = fis.read()) != -1)
                {
                    addToList(c);
                }
            }
            catch(IOException ex)
            {
                System.out.println(ex);
            }
        }
        catch(FileNotFoundException ex)
        {
            System.out.println(ex);
        }
        
    }

    private void addToList(int value)
    {
        boolean found = false;
        Iterator iterate = dataList.iterator();
        DataSet dataSet = new DataSet();
        DataSet ds = null;
        dataSet.setValue(value);
        dataSet.setFrequency(1);
        while(iterate.hasNext())
        {
            ds = (DataSet)iterate.next();
            if(ds.getValue() == value)
            {
                ds.setFrequency(ds.getFrequency() + 1);
                iterate.remove();
                found = true;
                break;
            }
        }
        if(found == true)
        {
            dataSet = ds;
        }
        dataList.add(dataSet);
    }

    private void copyDataSetToExecute()
    {
        Iterator iterate = dataList.iterator();
        while(iterate.hasNext())
        {
            DataSet ds = (DataSet)iterate.next();
            heap.add(new Node(ds.getFrequency(), String.valueOf(ds.getValue())));
        }
    }

    private void writeEncodedFile()
    {
        String encodedFilePath = filePath + ".huf";
        File encodedFile = new File(encodedFilePath);
        File originalFile = new File(filePath);
        FileOutputStream fos = null;
        FileInputStream fis = null;
        try
        {
            int c;
            fos = new FileOutputStream(encodedFile);
            fis = new FileInputStream(originalFile);
            while( (c = fis.read()) != -1 )
            {
                Iterator iterate = builtHuffmanDataList.iterator();
                while(iterate.hasNext())
                {
                    DataSet ds = (DataSet)iterate.next();
                    if(ds.getValue() == c)
                    {
                        int huffmanCodeArray[] = ds.getHuffmanCode();
                        ByteBuffer byteBuffer = ByteBuffer.allocate(huffmanCodeArray.length * 4);        
                        IntBuffer intBuffer = byteBuffer.asIntBuffer();
                        intBuffer.put(huffmanCodeArray);
                        byte[] array = byteBuffer.array();
                        fos.write(array);
                        break;
                    }
                }
            }
        }
        catch(Exception ex){}
        System.out.println("File " + filePath + ".huf has been created successfully!");
    }
}

class DataSet 
{
    private int value;
    private long frequency;
    private int huffmanCode[];

    public int[] getHuffmanCode()
    {
        return huffmanCode;
    }
    public void setHuffmanCode(int huffmanCode[])
    {
        this.huffmanCode = huffmanCode;
    }
    public int getValue()
    {
        return value;
    }
    public long getFrequency()
    {
        return frequency;
    }
    public void setValue(int value)
    {
        this.value = value;
    }
    public void setFrequency(long frequency)
    {
        this.frequency = frequency;
    }
}