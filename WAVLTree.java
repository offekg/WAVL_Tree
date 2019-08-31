
/**
 *
 * WAVLTree
 *
 * An implementation of a WAVL Tree.
 * (Haupler, Sen & Tarajan ‘15)
 *
 */

public class WAVLTree {

	private final WAVLNode EXT = new WAVLNode(-1, null, null);
	private WAVLNode root;
	private WAVLNode max;
	private WAVLNode min;
	
	
	public WAVLTree() {
		root = EXT; //initiate an empty tree
	}
	
	public WAVLTree(int key, String value) {
		root = new WAVLNode(key, value, EXT); //initiate tree with root
		max = root;
		min = root;
	}
	
	
  /**
   * public boolean empty()
   *
   * returns true if and only if the tree is empty
   *
   */
  public boolean empty() {
    if(root == EXT)
    	return true;
    return false;
  }

 /**
   * public String search(int k)
   *
   * returns the info of an item with key k if it exists in the tree
   * otherwise, returns null
   */
  public String search(int k)
  {
      if(empty())
    	  return null;
	  WAVLNode current = root;
       while(current != EXT) {
    	   if(current.getKey() == k)
    		   return current.getValue();
    	   else if(current.getKey() > k)
    		   current = current.getLeft();
    	   else 
    		   current = current.getRight();
       }
       return null;
  }

  
  private WAVLNode singleRotation(WAVLNode current, String side) { 
	  WAVLNode x = current;
	  WAVLNode z = x.parent;
	  int tempSBS = x.subTreeSize; //saves temp subTreeSize for reordering
	  if (z == root)
		  root = x;
	  else if(z.parent.right == z) //pair x with z's parent
		  z.parent.right = x;
	  else
		  z.parent.left = x;
	  x.parent = z.parent;
	  x.subTreeSize = z.subTreeSize;
	  
	  switch(side) {
	  case "right" :
		  z.left = x.right;
		  z.subTreeSize = z.subTreeSize - tempSBS + x.right.subTreeSize; //fixes sub tree size
		  x.right.parent = z; // pair "b", x right child to z
		  x.right = z;
		  break;
	  case "left":
		  z.right = x.left;
		  z.subTreeSize = z.subTreeSize - tempSBS + x.left.subTreeSize; //fixes sub tree size
		  x.left.parent = z; // pair "b", x left child to z
		  x.left = z;		  
		  break;
	  default: break;
	  }
	  z.parent = x;
	  if (z.getRight() == EXT && z.getLeft() == EXT) {
		  z.rank = 0;
		  z.subTreeSize = 1;
	  }
	  else
		  z.rank -= 1;
	  return z;
  }
  
  /**
   * public int insert(int k, String i)
   *
   * inserts an item with key k and info i to the WAVL tree.
   * the tree must remain valid (keep its invariants).
   * returns the number of rebalancing operations, or 0 if no rebalancing operations were necessary.
   * returns -1 if an item with key k already exists in the tree.
   */
   public int insert(int k, String i) {
	      if (empty()) {
	    	  root = new WAVLNode(k, i, EXT);
	    	  min = root;
	    	  max = root;
	    	  return 0;
	      }
          WAVLNode current = root;
          while(true) {
        	  if(current.getKey() == k)
        		  return -1;
        	  else if(current.getKey() > k) {
        		  if(current.getLeft()!=EXT)
        			  current = current.getLeft();
        		  else {
        			  current.left = new WAVLNode(k, i, current);
        			  if(k < min.getKey()) //check if inserted is smaller than min
        				  min = current.left;
        			  WAVLNode temp = current;
        			  while (temp != EXT) {
        				  temp.subTreeSize++;
        				  temp = temp.parent;
        			  }
        			  break;
        		  }
        	  }
        	  else { 
        		  if (current.getRight() != EXT)
        			  current = current.getRight();
        		  else {
        			  current.right = new WAVLNode(k, i, current);
        			  if (k > max.getKey())
        				  max = current.right; //check if k is larger then max, then update
        			  WAVLNode temp = current;
        			  while (temp != EXT) {
        				  temp.subTreeSize++;
        				  temp = temp.parent;
        			  }
        			  break;
        		  }
        	  }
         }
         
         if(current.right != EXT && current.left != EXT) //if parent of inserted wasn't a leaf, no rebalancing needed.
        	 return 0;
         //if parent was a leaf:
         int countBalance = 1;
         current.rank += 1;
         WAVLNode parent = current.parent;
         while(parent != EXT && parent.rank == current.rank) { //while promotion makes rank dif 0 
        	
        	 if(parent.rank - parent.right.rank == 2 || parent.rank - parent.left.rank == 2) { //if we can't promote parent because dif with other son will be > 2
        		 if(parent.right == current) {
        			 if(current.rank == current.left.rank + 2) { //inner child rank dif 2, do 1 rotation left
        				 singleRotation(current, "left");
        				 return 2 + countBalance;
        			 }
        			 else { //inner child rank diff 1, do double rotation- right, then left
        				 WAVLNode rotator = current.left; 
        				 singleRotation(rotator, "right");
        				 singleRotation(rotator, "left");
        				 rotator.rank += 1; //promote the new top node
        				 return 5 + countBalance;
        			 }
        		 }
            	 else { //case 2: current is left child
            		 if(current.rank == current.right.rank + 2) { //inner child rank diff 2, do 1 rotation left
        				 singleRotation(current, "right");
        				 return 2 + countBalance;
            		 }
        			 else { //inner child rank diff 1, do double rotation- right, then left
        				 WAVLNode rotator = current.right; 
        				 singleRotation(rotator, "left");
        				 singleRotation(rotator, "right");
        				 rotator.rank += 1; //promote the new top node
        				 return 5 + countBalance;
        			 }
            	 } 
        	 }
        	 current = parent;
        	 current.rank += 1;
        	 parent = parent.parent;
        	 countBalance++;
         }
         return countBalance;
         
         // Update min/max
   }

   /**
   * public int delete(int k)
   *
   * deletes an item with key k from the binary tree, if it is there;
   * the tree must remain valid (keep its invariants).
   * returns the number of rebalancing operations, or 0 if no rebalancing operations were needed.
   * returns -1 if an item with key k was not found in the tree.
   */
   public int delete(int k)
   {
           if (empty() || search(k) == null) //case: empty list or list doesn't conatin k
        	   return -1;
           if (k == root.getKey() && root.getRight() == EXT && root.getLeft() == EXT) {
        	   root = EXT;
        	   return 0;
           }
           WAVLNode current = root;
           while (current.getKey() != k) {
        	   current.subTreeSize -= 1;
        	   if (current.getKey() > k) {
        		  current = current.getLeft();
        	   }
        	   else {
        		   current = current.getRight();
        	   }
           }
           
           int countBalance = 0;
    	   if (current == min)
    		   min = successor(current);
    	   if (current == max)
    		   max = predeccessor(current);
    	   WAVLNode parent = current.parent;
    	   if (current.getRight() == EXT && current.getLeft() == EXT) { // if I'm a leaf   
        	   countBalance += deleteLeaf(current);
           }
    	   
    	   else if (current.getRight() == EXT || current.getLeft() == EXT) { // If I'm Unary
        	    deleteUnary(current);
    		    if (parent == EXT) // if all rank diffs are good
    			   return 0; 
    	   }
           
           if (current.getRight() != EXT && current.getLeft() != EXT) { // binary node
        	   countBalance += deleteBinary(current);
           }
           
           countBalance += rebalance(parent);
           
           return countBalance;
           
   }
   
   public int rebalance(WAVLNode parent) {
	   int delCase;
	   int countBalance = 0;
	   while (parent != EXT) {
    	   //System.out.println("parent :" + parent.getKey());
    	   if ((delCase  = deleteCases(parent)) != 0) {
    		   System.out.println("CASE :" + delCase);
        	   if (delCase == 1) {
        		   parent.rank -= 1;
        		   countBalance +=1;
        	   }
        	   WAVLNode son;
        	   String direction; //for single rotation
        	   String sonDir; //for double rotation
        	   if (parent.rank - parent.right.rank == 1) {
        		   son = parent.right;
        		   direction = "left";
        		   sonDir = "right";
        	   }
        	   else {
        		   son = parent.left;
        		   direction = "right";
        		   sonDir = "left";
        	   }
        	   if (delCase == 2) { //double demote
        		   parent.rank -= 1;
        		   son.rank -= 1;
        		   countBalance += 2;
        	   }
        	   if (delCase == 3) {
        		   son.rank += 1;
        		   parent = singleRotation(son, direction);
        		   countBalance += 3;
        	   }
        	   if (delCase == 4) {
        		   if(sonDir == "right") { //means that sons left side is to be double rotated
        			   WAVLNode sonSon = son.left; 
        			   sonSon.rank += 1;
        			   singleRotation(sonSon, "right");
        			   sonSon.rank += 1;
        			  parent =  singleRotation(sonSon, "left");
        			   countBalance += 5;
        		    }
        		    if(sonDir == "left") { //means that sons left side is to be double rotated
        			   WAVLNode sonSon = son.right; 
        			   sonSon.rank += 1;
        			   singleRotation(sonSon, "left");
        			   sonSon.rank += 1;
        			  parent =  singleRotation(sonSon, "right");
        			   countBalance += 5;
        		    }    
        	   }
    	   }
    	   parent = parent.parent;
       }
	   return countBalance;
   }
   
   public int deleteLeaf(WAVLNode current) {
	   WAVLNode parent = current.parent;
	   
       if (parent.getRight() == EXT || parent.getLeft() == EXT) { // if father will be a leaf
    	   parent.subTreeSize = 1;
    	   parent.rank = 0;
    	   parent.right = EXT;
    	   parent.left = EXT;
    	   current.parent = null;
    	   // We need to change ranks and STS up to the top
    	   return 1;
       }
       else { // If father has another son
    	   if (parent.getRight() == current) { // if the right son is the one to be deleted
    		   current.parent = null;
			   parent.right = EXT;
    		   if (parent.getLeft().rank == 0) { // if brother is a leaf
    			   int countBalance = rebalance(parent);
    			   return countBalance;
    		   }
    		   else { // brother is not a leaf
    			   parent.getLeft().rank++;
    			   WAVLNode z = singleRotation(parent.getLeft(), "right");
    			   return 3 + rebalance(z);
    		   }
    	   }
    	   else { // if the left son is the one to be deleted
    		   current.parent = null;
			   parent.left = EXT;
    		   if (parent.getRight().rank == 0) { // if brother is a leaf
    			   rebalance(parent);
    			   return 0;
    		   }
    		   else { // brother is not a leaf
    			   parent.getRight().rank++;
    			   WAVLNode z = singleRotation(parent.getRight(), "left");
    			   return 3 + rebalance(z);
    		   }
    	   }
       }
   }

   public WAVLNode deleteUnary(WAVLNode current) {
	   WAVLNode parent = current.parent;
	   if (current.getRight() != EXT) { // That means that the right son exists
		   if (parent == EXT)
			   root = current.getRight();
		   else if (parent.getRight() == current)
    		   parent.right = current.getRight();
    	   else
    		   parent.left = current.getRight();
		   current.right.parent = current.getParent();
		   
		   current.parent = null;
		   current.right = null;
		   //parent.subTreeSize -= 1;
	   }
	   else { // That means that the left son exists
    	   if (parent == EXT)
    		   root = current.getLeft();
    	   else if (parent.getRight() == current)
    		   parent.right = current.getLeft();
    	   else
    		   parent.left = current.getLeft();
		   current.left.parent = current.getParent();
		   
		   current.parent = null;
		   current.left = null;
		   //parent.subTreeSize -= 1;
	   }
	   
	   return parent;
   }
   
   public int deleteBinary(WAVLNode current) {
	   current.subTreeSize -= 1;
//	   current.rank -= 1;
	   WAVLNode pred = predeccessor(current,true); //will dempte subtreesize on the way to finding predeccessor
	   int p_key = pred.key;
	   String p_value = pred.value;
	   int countBalance = 0;
	   
	   if (pred.getRight() == EXT && pred.getLeft() == EXT) { // if pred a leaf   
    	   countBalance = deleteLeaf(pred);
       }
	   
	   else if (pred.getRight() == EXT || pred.getLeft() == EXT) { // If pred Unary
		   deleteUnary(pred);		   
	   }
	     
	   current.key = p_key;
	   current.value = p_value;
	   
	   return countBalance;
   }
   
   public int deleteCases(WAVLNode parent) {
	   //System.out.println((parent.rank - parent.right.rank) + " - " + (parent.rank - parent.left.rank));
	   if (parent.rank - parent.right.rank < 3  && parent.rank - parent.left.rank < 3) {//all is fine		   
		   return 0;
	   }
		   
	   if (parent.rank - parent.left.rank >= 3) {
		   if (parent.rank - parent.right.rank == 2) //only need to demote parent by 1
			   return 1;
		   if (parent.rank - parent.right.rank == 1) {
			   WAVLNode son = parent.right;
			   if (son.rank - son.left.rank == 2 && son.rank - son.right.rank == 2) //case 2: double demotion
				   return 2;
			   if (son.rank - son.right.rank == 1)
				   return 3;
			   if (son.rank - son.right.rank == 2)
				   return 4;
		   }
	   }
       else {
			if (parent.rank - parent.left.rank == 2) //only need to demote parent by 1
				   return 1;
			if (parent.rank - parent.left.rank == 1) {
				   WAVLNode son = parent.left;
				   if (son.rank - son.left.rank == 2 && son.rank - son.right.rank == 2) //case 2: double demotion
					   return 2;
				   if (son.rank - son.left.rank == 1)
					   return 3;
				   if (son.rank - son.left.rank == 2)
					   return 4;
			   }
		}
	   return 0; // will not get here
	   }
  
   /**
    * public String min()
    *
    * Returns the info of the item with the smallest key in the tree,
    * or null if the tree is empty
    */
   public String min()
   {
       if(!empty())    
    	   return min.getValue();
       return null;
   }

   /**
    * public String max()
    *
    * Returns the info of the item with the largest key in the tree,
    * or null if the tree is empty
    */
   public String max()
   {
           if(!empty())
        	   return max.getValue();
           return null;
   }

   /**
   * public int[] keysToArray()
   *
   * Returns a sorted array which contains all keys in the tree,
   * or an empty array if the tree is empty.
   */
   public int[] keysToArray()
   {
       int size = size();
	   if(size == 0)
    	   return new int[] {};
	   int[] arr = new int[size];
        WAVLNode x = min;
        for(int i = 0; i < size; i++) {
        	arr[i] = x.getKey();
        	x = successor(x);
        }
        return arr;           
   }
   
   public WAVLNode successor(WAVLNode x) {
	   if(x == max) //no succesor
		   return null; 
	   if(x.getRight() != EXT) {
		   x = x.getRight();
		   while(x.getLeft() != EXT) {
			   x =  x.getLeft();
		   }
	   }
	   else {
		   while(x.getParent().getLeft() != x) {
			   x = x.getParent();
		   }
		   x = x.getParent();
	   }
	   return x;
   }
   
   /**
    * public WAVLNode predeccessor(WAVLNode x, boolean demoteSubTreeSize)
    *
    * Returns the WAVLNode with the closest smaller value to x, or null if x is the min.
    * optional to lower all subtree sizes on the way there: for use in deleteBinary 
    */
   public WAVLNode predeccessor(WAVLNode x, boolean demoteSubTreeSize) {
	   if(x == min)
		   return null;
	   if(x.getLeft() != EXT) {
		   x = x.getLeft(); 
		   while(x.getRight() != EXT) {
			   if(demoteSubTreeSize) //if we are going to delete the pred in deleteBinary
				   x.subTreeSize -= 1;
			   x =  x.getRight();
		   }
	   }
	   else {
		   while(x.getParent().getRight() != x) {
			   x = x.getParent();
		   }
		   x = x.getParent();
	   }
	   return x;
   }
   
   public WAVLNode predeccessor(WAVLNode x) {
	   return predeccessor(x, false);
   }

   /**
   * public String[] infoToArray()
   *
   * Returns an array which contains all info in the tree,
   * sorted by their respective keys,
   * or an empty array if the tree is empty.
   */
   public String[] infoToArray()
   {
      int size = size();
	  if(size == 0)
    	   return new String[] {};
	  String[] arr = new String[size];
       WAVLNode x = min;
       for(int i = 0; i < size; i++) {
    	   arr[i] = x.getValue();
    	   x = successor(x);
       }
       return arr;                    
   }

   /**
    * public int size()
    *
    * Returns the number of nodes in the tree.
    *
    */
   public int size()
   {
           return root.getSubtreeSize();
   }
   
     /**
    * public WAVLNode getRoot()
    *
    * Returns the root WAVL node, or null if the tree is empty
    *
    */
   public WAVLNode getRoot()
   {
       if(root == EXT)
    	   return null;
	   return root;
   }
     /**
    * public int select(int i)
    *
    * Returns the value of the i'th smallest key (return -1 if tree is empty)
    * Example 1: select(1) returns the value of the node with minimal key 
        * Example 2: select(size()) returns the value of the node with maximal key 
        * Example 3: select(2) returns the value 2nd smallest minimal node, i.e the value of the node minimal node's successor  
    *
    */   
   public String select(int i)
   {
	   if (empty() || i > size())
		   return null;
	   if (i == 1)
		   return min.getValue();
	   if (i == size())
		   return max.getValue();
	   
	   WAVLNode current = root;
	   while (true) {
		   int s = current.getLeft().getSubtreeSize();
		   if (s + 1 == i)
			   return current.getValue();
		   if (i < s)
	           current = current.getLeft();
		   else {
			   current = current.getRight();
			   i -= (s + 1);
		   }
	   }
	   
	   
	   
   }

   /**
   * public class WAVLNode
   * @inv 0<(this.parent.rank-this.rank)<3 
   */
  public class WAVLNode{
                private int key;
                private String value;
                public int rank;
                public WAVLNode parent;
                public WAVLNode left;
                public WAVLNode right;
                public int subTreeSize;
             
	  			public WAVLNode (int key, String value, WAVLNode parent) {
	  				this.key = key;
	  				this.value = value;
	  				if(key==-1) {  //iff external node rank is -1
	  					this.rank = -1;
	  					subTreeSize = 0;
	  				}
	  				else {
	  					this.rank = 0;
	  					subTreeSize = 1;
	  				}
	  				this.parent = parent;
	  				left = EXT;
	  				right = EXT;
	  			}
	  			
	  			public WAVLNode getParent() {
	  				return parent;
	  			}
	  			public int getKey()
                {
                        return key; 
                }
                public String getValue()
                {
                        return value; 
                }
                public WAVLNode getLeft()
                {
                        return left; 
                }
                public WAVLNode getRight()
                {
                        return right; 
                }
                public boolean isInnerNode()
                {
                        return rank>=0; 
                }

                public int getSubtreeSize() {
                		return subTreeSize;

                }
  }

}

