import java.util.HashMap;
import java.util.Map;

    class MyHashTable {
    private int tablesize;
    private MyLinkedObject[] table;

    public MyHashTable(int size)
    {
        this.tablesize = size;
        this.table = new MyLinkedObject[size];
    }

    public void insert(String word)
    {
        int index = hash(word);
        if(table[index] == null)
        {
            table[index] = new MyLinkedObject(word);
        }
        else
        {
            table[index].setWord(word);
        } 
    }

    public void display()
    {
        for(int i = 0; i < tablesize; i++)
        {
            if(table[i] != null)
            {
                MyLinkedObject current = table[i];
                while(current != null)
                {
                    System.out.println("Index: " + i + ", Word: " + current.getWord() + ", Count: " + current.getCount());
                    current = current.getNext();
                }
            }
        }
    }

    public int getHashtableSize()
    {
        return tablesize;
    }

    public MyLinkedObject[] getHashtable()
    {
        return table;
    }

    public MyLinkedObject getObjectByIndex(int index) {
        return table[index];
    }
    public Map<String, Integer> getWordsMap() {
		Map<String, Integer> probs = new HashMap<>();
		for (MyLinkedObject bucket : table) {
			MyLinkedObject current = bucket;
			while (current != null) {
				probs.put(current.getWord(), current.getCount());
				current = current.getNext();
			}
		}
		return probs;
	}

    private int hash(String word)
    {
        //MyHashFunction Function1 = new MultiplicationHash(tablesize);
        MyHashFunction hashFunction = new MidSquareHash(tablesize);
        return hashFunction.hash(word);
    }
}


/**
 * MyLinkedObject
 */
class MyLinkedObject 
{

//------------------ Private instances ---------------------//    
    private String word;
    private int count;
    private MyLinkedObject next;

//------------------ Constructor ---------------------------//    

    public MyLinkedObject(String w) // Constructor to initialize the class MyLinkedObject
    {
        word = w;
        count = 1;
        next = null;
    }

//------------------- SetWord() Logic -----------------------//   

    public void setWord(String w)
    {
        if(w.compareTo(word) == 0)
        {
            count++;
        }
        else if(w.compareTo(word)>0)
        {
            if(next == null)
            {
                next = new MyLinkedObject(w);
            }
            else if(w.compareTo(next.word)<0)
            {
                setNext(new MyLinkedObject(w));
            }
            else
            {
                next.setWord(w);
            }
        }
    }

//------------------------ Extra method for convinience regarding swaping of word from next.word and returning instance values------------------------------//

    public void setNext(MyLinkedObject obj1) // swaping words
    {
        MyLinkedObject temp = this.next;
        this.next = obj1;
        obj1.next = temp;
    }

    public String getWord() // returning word
    {
        return word;
    }

    public int getCount() // returning word count
    {
        return count;
    }

    public MyLinkedObject getNext() // returning next word
    {
        return next;
    }
}

//---------------------------------------------------------- Hash Function ------------------------------------------------------------------------------------//

abstract class MyHashFunction 
{
    int tablesize;
    public MyHashFunction(int size)
    {
        this.tablesize = size;
    }

    public abstract int hash(String word);
}

//------------------------------------------------------ UnicodeHash Fuction Algorithm ------------------------------------------------------------------------//

class  UnicodeHashFunction extends MyHashFunction
{
    public UnicodeHashFunction(int size)
    {
        super(size);
    }

    @Override
    public int hash(String word)
    {
        if (word!=null && !word.isEmpty()) 
        {
            int unicode = word.charAt(0) % tablesize;
            return unicode;      
        }
        return -1;
    }
}

//------------------------------------------------------ Sum of Unicode Hash Fuction Algorithm ------------------------------------------------------------------------//


class UnicodeSumHashFunction extends MyHashFunction
{
    public UnicodeSumHashFunction(int size)
    {
        super(size);
    }
    @Override
    public int hash(String word)
    {
        if (word!=null && !word.isEmpty()) 
        {
            int unicodeSum = 0;
            for(char ch: word.toCharArray())
            {
                unicodeSum += (int) ch;
            }    
            return unicodeSum % tablesize;  
        }
        return -1;
    }
}

//------------------------------------------------------ HashCode Fuction Algorithm ------------------------------------------------------------------------//


class HashCodeFunction extends MyHashFunction {
    public HashCodeFunction(int hashtableSize) {
        super(hashtableSize);
    }

    @Override
    public int hash(String word) {
        if (word != null && !word.isEmpty()) {
            int hashCode = word.hashCode();
            return Math.abs(hashCode % tablesize);
        }
        return -1; // or throw an exception for invalid input
    }
}

//------------------------------------------------------ Multiplication Hash Function Algorithm ------------------------------------------------------------------------//


class MultiplicationHash extends MyHashFunction {

    public MultiplicationHash(int hashtableSize) {
        super(hashtableSize);
    }

    @Override
    public int hash(String word) {
        double constantA = 0.629;
        if (word != null && !word.isEmpty()) {
            double hashCode = word.hashCode() * constantA;
            double fractionalPart = hashCode - Math.floor(hashCode);
            return (int) Math.floor(fractionalPart * tablesize);
        }
        return -1; // or throw an exception for invalid input
    }
}

//------------------------------------------------------ MidSquare Hash Function Algorithm (Optimal) ------------------------------------------------------------------------//


class MidSquareHash extends MyHashFunction {
    public MidSquareHash(int hashtableSize) {
        super(hashtableSize);
    }

    @Override
    public int hash(String word) {
        if (word != null && !word.isEmpty()) {
            int key = word.hashCode();
            long square = (long) key * key;
            
            // Extract a portion from the middle (e.g., middle 10 digits)
            int mid = (int) ((square / 100) % 1000000000);
            
            return mid % tablesize;
        }
        return -1; // or throw an exception for invalid input
    }
}

//------------------------------------------------------ End Of File ------------------------------------------------------------------------//

