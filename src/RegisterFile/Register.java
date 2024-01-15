package RegisterFile;

public class Register {
	private String Name;
	private String Qi;
	private float Content;
	
	public Register(String name, String qi,float content) {
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

	public float getContent() {
		return Content;
	}

	public void setContent(float content) {
		Content = content;
	}
	public void printRegister() {
        System.out.println(Name+"     " + Qi+"     " +Content  );
    }
	
}
