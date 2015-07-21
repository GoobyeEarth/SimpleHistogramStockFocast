package test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import library.SimpleThreadClass;

public class TestMainClass {
	
	public static void main(String[] args) {
List<Node> nodes = new ArrayList<TestMainClass.Node>();
		
		for(int i=0; i < 20; i++){
			nodes.add(new Node(i) );
		}
		SimpleThreadClass stc = new SimpleThreadClass(3);
		for (Node node0 : nodes) {
		    final Node node = node0;
		    stc.addProcess(new Callable<Void>() {
				
				@Override
				public Void call(){
					node.process();
					return null;
				}
			});
		}
		
		stc.threadExecute();

	}
	
	public static void test(){
		List<Node> nodes = new ArrayList<TestMainClass.Node>();
		
		for(int i=0; i < 20; i++){
			nodes.add(new Node(i) );
		}
		
		ExecutorService threadPool = Executors.newFixedThreadPool(4);
		 
		Collection<Callable<Void>> processes = new LinkedList<Callable<Void>>();
		
		
		for (Node node0 : nodes) {
		    final Node node = node0;
		    processes.add(new Callable<Void>() {
		        @Override
		        public Void call() {
		            node.process();
		            return null;
		        }
		    });
		}
		 
		try {
		    threadPool.invokeAll(processes);
		} catch (InterruptedException e) {
		    throw new RuntimeException(e);
		} finally {
		    threadPool.shutdown();
		}
	}
	
	static class Node {
		
		private int num;
		public Node(int num){
			this.num = num;
		}
		public void process(){
			System.out.println("test:" + num);
		}
	}
}
