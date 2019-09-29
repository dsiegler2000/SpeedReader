package randocrapola;

import java.util.ArrayList;
import java.util.List;

public class TreeSimilarity {
	
	/**
	 * 1. Get average number of nodes
	 * @param t1 First tree
	 * @param t2 Second tree
	 * @return Double 0-1 with 1 being the same, 0 being different
	 */
	public static double treeSimilarity(Tree t1, Tree t2){
		
		double numNodes = (countNodes(t1) + countNodes(t2)) / 2d;
						
		return recurse(t1, t2) / numNodes;
		
	}
	
	public static int recurse(Tree t1, Tree t2){
		
		if(t1 != null && t2 != null){
						
			int sum = (t1.getData().equals(t2.getData()) ? 1 : 0);
			
			for(int i = 0; i < t1.getChildren().size(); i++){
				
				sum += recurse(t1.getChildren().get(i), i < t2.getChildren().size() ? t2.getChildren().get(i) : null);
				
			}
			
			return sum;
			
		}
		
		if(t1 != null && t2 != null){
			
			return 0;
			
		}
		
		if(t1 == null && t2 == null){
			
			return 1;
			
		}
		
		return 0;
		
	}
	
	public static int countNodes(Tree t){
		
		if(t.isLeaf()){
			
			return 1;
			
		}
		
		int sum = 1;
		
		for(Tree c : t.getChildren()){
			
			sum += countNodes(c);
			
		}
		
		return sum;
		
	}

}

class Tree {
	
	private List<Tree> children;
	
	private String data;
		
	public Tree(String data){
		
		children = new ArrayList<>();
		
		this.data = data;
		
	}
	
	public void addChild(Tree child){
		
		children.add(child);
		
	}
	
	public boolean isLeaf(){
		
		return children.size() == 0;
		
	}
	
	public String getData(){
		
		return data;
		
	}
	
	public List<Tree> getChildren(){
		
		return children;
		
	}
	
	@Override
	public String toString(){
		
		return "Data: " + data + " Children: " + children;
		
	}
	
}