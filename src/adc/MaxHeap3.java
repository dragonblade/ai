package adc;

public class MaxHeap3 {
	
	public void heapify3(int[] E, int i) {
		int max=i;
		int left = 3*i-1;
		int mid = 3*i;
		int right = 3*i+1;
		
		if (left <= E.length && E[left] > E[i]) {
			max = left;
		}
		
		if(mid <= E.length && E[mid] > E[max]) {
			max = mid;
		}
		
		if(mid <= E.length && E[right] > E[max]) {
			max = right;
		}
		
		if(max != i) {
			//Swap E[i] and E[max]
			int temp = E[i];
			E[i] = E[max];
			E[max] = temp;
			
			//Call heapify3 at node max
			heapify3(E, max);
		}
	}
	
	public void buildHeap3(int[] E) {
		for(int i=(Math.round(E.length/3)); i>=0; i--) {
			heapify3(E, i-1);
		}
	}
}
