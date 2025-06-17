public class Cat {

    private String name;
    private String ras;

    public Cat(String name, String ras){
        this.name = name;
        this.ras = ras;
    }

    public String getName(){
        return  this.name;
    }

    public String getRas(){
        return  this.ras;
    }

    public void setName(String n){
        this.name = n;
    }

    public void setRas(String r){
        this.ras = r;
    }
}
