package pn.util;

/**
 * Generates a sequencial long integer number
 * @author meslin
 *
 */
public class Sequencial {
	private static Sequencial instance;
	private long seq;
	
	private Sequencial() {
		this.seq = 1;
	}
	
	public static synchronized Sequencial getInstance() {
		if(instance == null) {
			instance = new Sequencial();
		}
		return instance;
	}
	
	/**
	 * Gets a sequencial unique number
	 * @return a sequencial autoincrement long integer number
	 */
	public synchronized long getSeq() {
		return this.seq++;
	}
}
