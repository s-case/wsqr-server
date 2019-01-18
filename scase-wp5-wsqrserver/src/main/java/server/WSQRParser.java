package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader; 

/**
 * Provides a parser to read a WSQR representation.
 * 
 * @author Davide Tosi, Carola Bianchi, Marco Compagnoni and Matteo Tegnenti
 */

public class WSQRParser {
	public final static String S_XML="<?xml";
	public final static String S_WSQR="WSQRMeasure";
	public final static String S_IM="<InternalMeasure>";
	public final static String S_EM="<ExternalMeasure>";
	public final static String S_MD="<MeasureDefinition";
	public final static String M_N="<MeasureName>";
	public final static String M_VK="<MeasureValueKind>";
	public final static String M_V="<MeasureValue>";
	public final static String M_VMN="ValidationMeansName";
	public final static String M_AIU="AccuracyIndicatorUsed";
	public final static String M_AL="AccuracyLevel";
	public final static String M_STU="StatisticalTestUsed";
	public final static String M_PV="PValue";
	public final static String M_SSL="StatisticalSignificanceLevel";
	
	public static void parseService(Service service, String content) {
		BufferedReader br = new BufferedReader(new StringReader(content));
		String line=null;
		String measure="";
		boolean internal=true;
		
		try {
			while((line=br.readLine())!=null){
				if(line.contains(S_XML)||line.contains(S_WSQR)){
					continue;
				} else {
					if(line.contains(S_IM)){
						internal=true;
						measure="";
					} if(line.contains(S_EM)){
						internal=false;
						measure="";
					}
					measure+=line+"\n";
					//System.err.println("LINE:" +line);
					if(line.contains(S_MD)){
						if(internal){
							service.addInternalMeasure(measure);
						} else {
							service.addExternalMeasure(measure);
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void parseMeasure(Measure measure, String content) {
		BufferedReader br = new BufferedReader(new StringReader(content));
		String line=null;
		String f_v;
		try {
			while((line=br.readLine()) != null){
				measure.setName(findValue(line,M_N));
				measure.setValue((f_v=findValue(line,M_V))!=null?Float.valueOf(f_v):-1);
				measure.setValueKind(findValue(line,M_VK));
				measure.setValidationMeans(findValue(line,M_VMN));
				measure.setAccuracyIndicator(findValue(line,M_AIU));
				measure.setAccuracyLevel((f_v=findValue(line,M_AL))!=null?Float.valueOf(f_v):-1);
				measure.setpValue((f_v=findValue(line,M_PV))!=null?Float.valueOf(f_v):-1);
				measure.setStatisticalTest(findValue(line,M_STU));
				measure.setStatisticalSignificance((f_v=findValue(line,M_SSL))!=null?Float.valueOf(f_v):-1);
			}
		} catch (IOException e) {
			e.printStackTrace();	
		}
	}

	private static String findValue(String line, String value){
		if(line.contains(value)){
			//System.out.println(line);
			if(line.contains("\"")){
				return line.substring(line.indexOf("\"")+1, line.lastIndexOf("\""));
			} else {
				return line.substring(line.indexOf(">")+1, line.lastIndexOf("<"));
			} 
		}
		return null;
	}
}
