package server;
import java.io.Serializable;

/**
 * Provides the list of measures and attributes that compose the WSQR XML representation.
 * 
 * @author Davide Tosi, Carola Bianchi, Marco Compagnoni and Matteo Tegnenti
 */

public class Measure implements Serializable{
	private String content;
	public  final String M_N="<MeasureName>";
	public  final String M_VK="<MeasureValueKind>";
	public  final String M_V="<MeasureValue>";
	public  final String M_VMN="ValidationMeansName";
	public  final String M_AIU="AccuracyIndicatorUsed";
	public  final String M_AL="AccuracyLevel";
	public  final String M_STU="StatisticalTestUsed";
	public  final String M_PV="PValue";
	public  final String M_SSL="StatisticalSignificanceLevel";
	private String name; //G 
	private String valueKind; //G
	private float value; //G
	private String validationMeans; //I-E
	private String accuracyIndicator; //E
	private float accuracyLevel; //E
	private String statisticalTest; //E
	private float pValue; //E
	private float statisticalSignificance; //E 
	private String description; //I-E
	private String formula; //I-E
	private String measureKind; //I
	private String attributeName; //I
	private String attributeValue; //I
	
	public Measure(String content){
		setContent(content);
		WSQRParser.parseMeasure(this, content);
	}
	
	public String getName(){
		return name;
	}
	
	public String getContent(){
		return content;
	}
	
	public String getValueKind(){
		return valueKind;
	}
	
	public float getValue(){
		return value;
	}
	
	public String toString(){
		return content;
	}

	public String getValidationMeans() {
		return validationMeans;
	}

	public void setValidationMeans(String validationMeans) {
		if(validationMeans!=null)
		this.validationMeans = validationMeans;
	}

	public String getAccuracyIndicator() {
		return accuracyIndicator;
	}

	public void setAccuracyIndicator(String accuracyIndicator) {
		if(accuracyIndicator!=null)
		this.accuracyIndicator = accuracyIndicator;
	}

	public float getAccuracyLevel() {
		return accuracyLevel;
	}

	public void setAccuracyLevel(float accuracyLevel) {
		if(accuracyLevel!=-1)
		this.accuracyLevel = accuracyLevel;
	}

	public String getStatisticalTest() {
		return statisticalTest;
	}

	public void setStatisticalTest(String statisticalTest) {
		if(statisticalTest!=null)
		this.statisticalTest = statisticalTest;
	}

	public float getpValue() {
		return pValue;
	}

	public void setpValue(float pValue) {
		if(pValue!=-1)
		this.pValue = pValue;
	}

	public float getStatisticalSignificance() {
		return statisticalSignificance;
	}

	public void setStatisticalSignificance(float statisticalSignificance) {
		if(statisticalSignificance!=-1)
			this.statisticalSignificance = statisticalSignificance;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setName(String name) {
		if(name!=null)
			this.name = name;
	}

	public void setValueKind(String valueKind) {
		if(valueKind!=null)
			this.valueKind = valueKind;
	}

	public void setValue(float value) {
		if(value!=-1)
			this.value = value;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public void setFormula(String formula) {
		this.formula = formula;
	}
	
	public String getFormula() {
		return this.formula;
	}

	public String getMeasureKind() {
		return this.measureKind;
	}
	
	public void setMeasureKind(String mk){
		measureKind = mk;
	}

	public String getAttributeName() {
		return attributeName;
	}
	
	public void setAttributeName(String an){
		attributeName = an;
	}

	public String getAttributeValue() {
		return attributeValue;
	}
	
	public void setAttributeValue(String av){
		attributeValue = av;
	}
}