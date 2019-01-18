package server;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Provides the WSQR Service Instance. 
 * 
 * @author Davide Tosi, Carola Bianchi, Marco Compagnoni and Matteo Tegnenti
 */

public class Service implements Serializable{
	private String serviceName;
	private ArrayList<Measure> internal;
	private ArrayList<Measure> external;
	
	public Service(String n){
		serviceName=n;
		internal=new ArrayList<Measure>();
		external=new ArrayList<Measure>();
	}
	
	public Service(String n, String s){
		this(n);
		WSQRParser.parseService(this,s);
	}
	
	public void addInternalMeasure(String measure){
		internal.add(new Measure(measure));
	}
	
	public void addExternalMeasure(String measure){
		external.add(new Measure(measure));
	}
	
	public String getName(){
		return serviceName;
	}
	
	public ArrayList<Measure> getInternal(){
		return internal;
	}
	
	public ArrayList<Measure> getExternal(){
		return external;
	}
	
	public String toString(){
		String result="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
				"<WSQRMeasure>\n";
		for(int i=0;i<internal.size();i++){
			result+=internal.get(i)+"\n";
		}
		for(int i=0;i<external.size();i++){
			result+=external.get(i)+"\n";
		}
		result+="</WSQRMeasure>";
		return result;
	}
}
