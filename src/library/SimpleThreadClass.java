package library;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SimpleThreadClass {
	private Collection<Callable<Void>> processes;
	private ExecutorService  threadPool;
	public SimpleThreadClass(int threadNumber){
		threadPool = Executors.newFixedThreadPool(threadNumber);

		processes= new LinkedList<Callable<Void>>();
	}

	public void addProcess(Callable<Void> process){
		processes.add(process);
	}


	public void threadExecute(){
		try {
		    threadPool.invokeAll(processes);
		} catch (InterruptedException e) {
		    throw new RuntimeException(e);
		} finally {
		    threadPool.shutdown();
		}
	}
}
