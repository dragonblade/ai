package adc;

public class BinaryTree {
	private int value;
	private BinaryTree leftChild;
	private BinaryTree rightChild;
	
	public BinaryTree(int value, BinaryTree leftChild, BinaryTree rightChild) {
		this.value = value;
		this.leftChild = leftChild;
		this.rightChild = rightChild;
	}
	
	public boolean isBst() {
		boolean isLeftBst = false;
		boolean isRightBst = false;
		
		if(this.getLeftChild() != null) {
			if(this.getValue() > getLeftChild().getValue()) {
				isLeftBst = getLeftChild().isBst();
			} else {
				isLeftBst = false;
			}
		} else {
			isLeftBst = true;
		}
		
		if(this.getRightChild() != null) {
			if(this.getValue() <= getRightChild().getValue()) {
				isRightBst = getRightChild().isBst();
			} else {
				isRightBst = false;
			}
		} else {
			isRightBst = true;
		}
		
		return (isLeftBst && isRightBst);

	}
	
	
	public BinaryTree getLeftChild() {
		return this.leftChild;
	}
	
	public BinaryTree getRightChild() {
		return this.rightChild;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public static void main(String[] args) {
		int[] tree = {40, 20, 60, 10, 30, 50, 70};
		BinaryTree test = new BinaryTree(40, new BinaryTree(20, new BinaryTree(10, null, new BinaryTree(9, null, null)), 
				new BinaryTree(30, null, null)), new BinaryTree(60, new BinaryTree(50, null, null),
						new BinaryTree(70, null, null)));
		System.out.println(test.isBst());
	}
}
