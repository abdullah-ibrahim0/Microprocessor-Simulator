package Buffers;

public class StoreBuffer {
	private String name;
	private int busy;
	private int address;
	private String Q;
	private float V;
	private int storeWait;
	private boolean storeWaitF;
	
	public StoreBuffer(String name, int busy, int address, String q, float v, int storeWait,boolean storeWaitF) {
		super();
		this.name = name;
		this.busy = busy;
		this.address = address;
		this.Q = q;
		this.V = v;
		this.storeWait = storeWait;
		this.storeWaitF = storeWaitF;
	}
	public boolean isStoreWaitF() {
		return storeWaitF;
	}
	public void setStoreWaitF(boolean storeWaitF) {
		this.storeWaitF = storeWaitF;
	}
	public int getStoreWait() {
		return storeWait;
	}
	public void setStoreWait(int storeWait) {
		this.storeWait = storeWait;
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
	public String getQ() {
		return this.Q;
	}
	public void setQ(String q) {
		this.Q = q;
	}
	public float getV() {
		return this.V;
	}
	public void setV(float v) {
		this.V = v;
	}
	
	public void printStoreBuffer() {
        System.out.println("Name: " + name + ", Busy: " + busy + ", Address: " + address +", Q:" +Q+", V:"+V);
    }
	
}
