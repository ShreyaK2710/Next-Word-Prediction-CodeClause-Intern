import java.util.*;
import java.io.IOException;

public class Trie {
	
	private class TrieNode {
		Map<Character, TrieNode> children = new TreeMap<>();
		boolean aword;
		ArrayList<String> mfu = new ArrayList<String>();
	}

	private TrieNode root;
	private ArrayList<WordItem> dict;
   
   public Trie() {
		this.root = new TrieNode();
      this.dict = new ArrayList<WordItem>();
	}

   public void insertDictionary(String s) throws IOException {
      WordProcessor wp = new WordProcessor();
	   dict = wp.fileRead(s);
      
      for(int i = 0; i < dict.size(); i++)
      {
         this.insertString(dict.get(i).getWord());
      }
   }
   
   public boolean hasDictionary() {
      return dict.size() > 0;
   }

	public void insertString(String s) {
		insertString(root, s);
	}

	private void insertString(TrieNode root, String s) {
		TrieNode cur = root;
		String str = "";
      int focc;
      int locc;
      
      for (char ch : s.toCharArray()) {
      
			TrieNode next = cur.children.get(ch);
			if (next == null)
				cur.children.put(ch, next = new TrieNode());
			
         str += ch;
         cur = next;
         if(cur.mfu.size() <= 0)
         {
            focc = lowIndex(str);
         
            if(focc >= 0) {
               locc = highIndex(str, focc);
               Object[] objectArray = dict.subList(focc, locc+1).toArray();
               WordItem[] subdic = Arrays.copyOf(objectArray, objectArray.length, WordItem[].class);
               Arrays.sort(subdic, new WordItem.WordComparator(true));
               
               for(int i = subdic.length-1; i >= 0; i--)
               {
                  cur.mfu.add(subdic[i].getWord());
               }
            }
         }
		}
		cur.aword = true;
	}
   
   private int lowIndex(String s) {
      int low = 0, high = dict.size() - 1, mid;
      
      while(low <= high)
      {
         mid = (low + high) / 2;
         String fword = dict.get(mid).getWord();
         
         if(s.compareTo(fword) < 0) {
            if(low == high)
            {
               if(fword.length() >= s.length())
               {
                  if(s.compareTo(fword.substring(0, s.length()) ) == 0)
                     return mid;
               }
               else return -1;
            }
            high = mid;
         }
         else if(s.compareTo(fword) > 0)
         {
            if(mid-1 > 0)
            {
               if(fword.length() >= s.length())
               {
                  if(s.compareTo( (dict.get(mid-1)).getWord() ) < 0 &&
                     s.compareTo(fword.substring(0, s.length()) ) == 0)
                  {
                     return mid;
                  }
               }
               else if(low == high) return -1;
            }
            low = mid + 1;            
         }
         else return mid;
      }
      return -1;
   }
   
   private int highIndex(String s, int firstIndex) {
      
      int nextIndex = firstIndex;
      String nword = ""; // next word
      while(nextIndex < dict.size()-1) 
      {
         nword = dict.get(nextIndex+1).getWord();
         if(nword.length() < s.length()) break;
         if(s.compareTo(nword.substring(0, s.length()) ) != 0 ) {
            break;
         }
         nextIndex++;
      }
      return nextIndex;
   }
	
	public void printSorted() {
		printSorted(root, "");
	}

	private void printSorted(TrieNode node, String s) {
		if (node.aword) {
			System.out.println(s);
		}
		for (Character ch : node.children.keySet()) {
			printSorted(node.children.get(ch), s + ch);
		}
	}
	
	public ArrayList<String> findWord(String s) { 
		TrieNode node;
      if( (node = findWord(root, s)) == null)
         return new ArrayList<String>();
         
      return node.mfu;
	}
	
	private TrieNode findWord(TrieNode node, String s) {
		if(s != null) {
			String rest = s.substring(1);
			char ch = s.charAt(0);
			TrieNode child = node.children.get(ch);
			if( (s.length() == 1 && child != null) || child == null )
				return child;
			else
				return findWord(child, rest);
		}
		return new TrieNode();
	}

	// Usage example
	public static void main(String[] args) {
		        
		Trie tr = new Trie();
		
      try {
         tr.insertDictionary("files/dictsmall.txt");
      }
      catch(IOException e) {
         System.out.println("Unable to read dictionary");
         System.exit(0);
      }
      int low = tr.lowIndex("ba");
      tr.highIndex("ba", low);
		tr.printSorted();
	}
}
