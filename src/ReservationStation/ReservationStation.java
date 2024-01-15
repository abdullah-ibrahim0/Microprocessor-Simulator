package ReservationStation;

public class ReservationStation {
	private String name;
	private int busy;
	private String op;
	private String vj;
	private String Vk;
	private String Qj;
	private String Qk;
	private String A;
	
	private int addDWait;
	private int subDWait;
	private int addIWait;
	private int subIWait;
	private int MULWait;
	private int DIVWait;
	private int BENZWait;
	
	private boolean addDWaitF;
	private boolean subDWaitF;
	private boolean addIWaitF;
	private boolean subIWaitF;
	private boolean MULWaitF;
	private boolean DIVWaitF;
	private boolean BENZWaitF;
	
	
	public ReservationStation(String name, int busy, String op, String vj, String vk, String qj, String qk, String a,
			int addDWait, int subDWait, int addIWait, int subIWait,int MULWait,int DIVWait,int BENZWait, boolean addDWaitF, boolean subDWaitF,
			boolean addIWaitF, boolean subIWaitF,boolean MULWaitF,boolean DIVWaitF,boolean BENZWaitF) {
		this.name = name;
		this.busy = busy;
		this.op = op;
		this.vj = vj;
		this.Vk = vk;
		this.Qj = qj;
		this.Qk = qk;
		this.A = a;
		this.addDWait = addDWait;
		this.subDWait = subDWait;
		this.addIWait = addIWait;
		this.subIWait = subIWait;
		this.MULWait = MULWait;
		this.DIVWait = DIVWait;
		this.BENZWait=BENZWait;
		this.addDWaitF = addDWaitF;
		this.subDWaitF = subDWaitF;
		this.addIWaitF = addIWaitF;
		this.subIWaitF = subIWaitF;
		this.MULWaitF = MULWaitF;
		this.DIVWaitF = DIVWaitF;
		this.BENZWaitF = BENZWaitF;
	}



	public int getBENZWait() {
		return BENZWait;
	}



	public void setBENZWait(int bENZWait) {
		BENZWait = bENZWait;
	}



	public boolean isBENZWaitF() {
		return BENZWaitF;
	}



	public void setBENZWaitF(boolean bENZWaitF) {
		BENZWaitF = bENZWaitF;
	}



	public int getMULWait() {
		return MULWait;
	}



	public void setMULWait(int mULWait) {
		MULWait = mULWait;
	}



	public int getDIVWait() {
		return DIVWait;
	}



	public void setDIVWait(int dIVWait) {
		DIVWait = dIVWait;
	}



	public boolean isMULWaitF() {
		return MULWaitF;
	}



	public void setMULWaitF(boolean mULWaitF) {
		MULWaitF = mULWaitF;
	}



	public boolean isDIVWaitF() {
		return DIVWaitF;
	}



	public void setDIVWaitF(boolean dIVWaitF) {
		DIVWaitF = dIVWaitF;
	}



	public boolean isAddDWaitF() {
		return addDWaitF;
	}



	public void setAddDWaitF(boolean addDWaitF) {
		this.addDWaitF = addDWaitF;
	}



	public boolean isSubDWaitF() {
		return subDWaitF;
	}



	public void setSubDWaitF(boolean subDWaitF) {
		this.subDWaitF = subDWaitF;
	}



	public boolean isAddIWaitF() {
		return addIWaitF;
	}



	public void setAddIWaitF(boolean addIWaitF) {
		this.addIWaitF = addIWaitF;
	}



	public boolean isSubIWaitF() {
		return subIWaitF;
	}



	public void setSubIWaitF(boolean subIWaitF) {
		this.subIWaitF = subIWaitF;
	}



	public int getAddDWait() {
		return addDWait;
	}



	public void setAddDWait(int addDWait) {
		this.addDWait = addDWait;
	}



	public int getSubDWait() {
		return subDWait;
	}



	public void setSubDWait(int subDWait) {
		this.subDWait = subDWait;
	}



	public int getAddIWait() {
		return addIWait;
	}



	public void setAddIWait(int addIWait) {
		this.addIWait = addIWait;
	}



	public int getSubIWait() {
		return subIWait;
	}



	public void setSubIWait(int subIWait) {
		this.subIWait = subIWait;
	}



	public String getName() {
		return name;
	}

	public int getBusy() {
		return busy;
	}

	public String getOp() {
		return op;
	}

	public String getVj() {
		return vj;
	}

	public String getVk() {
		return Vk;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setBusy(int busy) {
		this.busy = busy;
	}

	public void setOp(String op) {
		this.op = op;
	}

	public void setVj(String vj) {
		this.vj = vj;
	}

	public void setVk(String vk) {
		Vk = vk;
	}

	public void setQj(String qj) {
		Qj = qj;
	}

	public void setQk(String qk) {
		Qk = qk;
	}

	public void setA(String a) {
		A = a;
	}

	public String getQj() {
		return Qj;
	}

	public String getQk() {
		return Qk;
	}

	public String getA() {
		return A;
	}
	public void printReservation() {
		System.out.println(name+"     " + busy+"   " + op+"      " + vj+"    " + Vk+"    " + Qj+"   " + Qk+"    " + A + "\n");
	}
	
	
}
