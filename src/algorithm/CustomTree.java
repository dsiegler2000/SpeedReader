package algorithm;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import edu.stanford.nlp.trees.Tree;

public class CustomTree {

	private List<CustomTree> children;

	private String data;

	private CustomTree parent;
	
	public CustomTree(CustomTree root, CustomTree parent){
		
		this.data = root.getData();
		
		this.parent = parent;
		
		this.children = new ArrayList<>();
		
		for(CustomTree t : root.getChildren()){
			
			children.add(new CustomTree(t, this));
			
		}
		
	}
	
	public CustomTree(Tree root, CustomTree parent){
				
		this.data = root.nodeString().split("\\[")[0];
		
		this.parent = parent;
		
		this.children = new ArrayList<>();

		for(Tree t : root.getChildrenAsList()){
			
			children.add(new CustomTree(t, this));
			
		}
		
	}

	public void addChild(CustomTree child) {

		children.add(child);

	}

	public boolean isLeaf() {

		return children.size() == 0;

	}

	public String getData() {

		return data;

	}

	public List<CustomTree> getChildren() {

		return children;

	}
	
	public CustomTree getParent(){
		
		return parent;
		
	}
	
	public void removeLeaf(CustomTree leaf){
						
		if(this.isLeaf() && this.equals(leaf)){
			
			throw new IllegalStateException("Cannot remove because this tree is only one node!");
			
		}
		
		Queue<CustomTree> roots = new LinkedList<>();
		roots.add(this);
		
		while(!roots.isEmpty()){
						
			CustomTree root = roots.remove();

			if(root.isLeaf()){
				
				// System.out.println(leaf + "\n" + root);
				
			}
			
			if(root.isLeaf() && root.equals(leaf)){
				
				// System.out.println("Removing: " + root);
				
				root.getParent().getChildren().remove(root);
				
				// System.out.println("Now: " + this.toString());
				
				return;
				
			}
			
			for(CustomTree t : root.getChildren()){
				
				roots.add(t);
				
			}
			
		}
		
	}
	
	public List<CustomTree> leaves(){
		
		List<CustomTree> leaves = new ArrayList<>();

		Queue<CustomTree> nextParent = new LinkedList<>();
		nextParent.add(this);

		while (!nextParent.isEmpty()) {

			CustomTree parent = nextParent.remove();

			if (parent.getChildren().size() == 0) {
				
				leaves.add(parent);

			}

			for (CustomTree c : parent.getChildren()) {

				nextParent.add(c);

			}

		}

		return leaves;
		
	}

	@Override
	public String toString() {

		return "Data: " + data + " Children: " + children;

	}
	
	@Override
	public boolean equals(Object o){
		
		return (o instanceof CustomTree) && ((CustomTree) o).getData().equals(this.getData());
		
	}

}
