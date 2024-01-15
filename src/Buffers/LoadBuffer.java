package Buffers;

public class LoadBuffer {
	private String name;
	private int busy;
	private int address;
	private int loadWait;
	private boolean loadWaitF;
	
	
	public LoadBuffer(String name, int busy, int address,int loadWait,boolean loadWaitF) {
		this.name = name;
		this.busy = busy;
		this.address = address;
		this.loadWait = loadWait;
		this.loadWaitF = loadWaitF;
	}
	public boolean isLoadWaitF() {
		return loadWaitF;
	}
	public void setLoadWaitF(boolean loadWaitF) {
		this.loadWaitF = loadWaitF;
	}
	public int getLoadWait() {
		return loadWait;
	}
	public void setLoadWait(int loadWait) {
		this.loadWait = loadWait;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getBusy() {
		return busy;
	}
	public void setBusy(int busy) {
		this.busy = busy;
	}
	public int getAddress() {
		return address;
	}
	public void setAddress(int address) {
		this.address = address;
	}
	public void printLoadBuffer() {
        System.out.println( name + "     " + busy + "     " + address);
    }
	
}
