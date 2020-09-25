package Lab.Commands;

import Lab.Service.Authorizator;

import java.io.Serializable;

public class Meta implements Serializable {
    private static final long serialVersionUID = 4562371567263178L;
    public Meta(){
        username= Authorizator.getUsername();
        password=Authorizator.getPassword();
        isSuccessful=true;
    }
    Meta(String name, Element element){
        this.name=name;
        this.element=element;
        username= Authorizator.getUsername();
        password=Authorizator.getPassword();
        isSuccessful=true;
    }
    Meta(String error){
        this.error=error;
        isSuccessful=false;
    }
    private String username;
    private String password;
    private transient String error;
    String name;
    Element element;
    private transient boolean isSuccessful=false;
    public boolean isSuccessful(){
        return isSuccessful;
    }
    public String getError(){
        return error;
    }
    public String getName(){
        return name;
    }
}
