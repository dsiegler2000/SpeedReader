package utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;
import java.util.UUID;

public class Utils {

	public static File writeToFile(String text, String filename){
		
		if(filename.contains(".txt") || filename.contains("/")){
			
			throw new IllegalArgumentException("This method takes the name of a file, not its path!");
			
		}
		
		File file = new File(filename + ".txt");
		
		if(!file.exists()){
			
			try {
				
				file.createNewFile();
				
			} catch (IOException e) {

				System.err.println("Error creating the file!");
				e.printStackTrace();
				
			}
			
		}
		
		FileWriter writer = null;
		
		try {
			
			writer = new FileWriter(file);
			
		} catch (IOException e) {

			System.err.println("Error creating the writer!");
			e.printStackTrace();
			
		}
		
		try {
			
			writer.write(text);
			
		} catch (IOException e) {

			System.err.println("Error writing to the file!");
			e.printStackTrace();
			
		}
		
		try {
			
			writer.close();
			
		} catch (IOException e) {

			System.err.println("Error closing the writer!");
			e.printStackTrace();
			
		}
		
		return file;
		
	}
	
	public static void useQueue(){
		
		// Let's just say i got bored for a bit
		
		Queue<Stack<String>> q = new LinkedList<>();
		
		for(int i = 0; i < 100; i++){
			
			Stack<String> s = new Stack<>();
			
			for(int j = 0; j < 100; j++){
				
				s.add(UUID.randomUUID().toString());
				
			}
			
			q.add(s);
			
		}
		
	}
	
	public static <E> File writeToFile(List<E> text, String filename){
		
		StringBuilder str = new StringBuilder();
		
		for(E e : text){
			
			str.append(e.toString());
			
		}
		
		return writeToFile(str.toString(), filename);
		
	}
	
	public static <E> File writeToFile(E[] text, String filename){
		
		StringBuilder str = new StringBuilder();
		
		for(E e : text){
			
			str.append(e.toString());
			
		}
		
		return writeToFile(str.toString(), filename);
		
	}
	
	public static void printTheGospel(){
		
		System.out.println("The following are infallable truths:");
		System.out.println("Sweeney is GOAT");
		System.out.println("Programming is GOAT");
		System.out.println("Big A is the source of all GOAT programming knowledge");

	}
	
}
