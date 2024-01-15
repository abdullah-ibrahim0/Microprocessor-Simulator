package RegisterFile;

public class RegisterInt {
	private String Name;
	private String Qi;
	private int Content;
	
	public RegisterInt(String name, String qi,int content) {
		this.Name = name;
		this.Qi = qi;
		this.Content=content;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getQi() {
		return Qi;
	}

	public void setQi(String qi) {
		Qi = qi;
	}

	public int getContent() {
		return Content;
	}

	public void setContent(int content) {
		Content = content;
	}
	public void printRegister() {
        System.out.println(Name+"     " + Qi+"     " +Content  );
    }
	
}