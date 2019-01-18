package eu.scase.qosontology;

import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides an API for creating a SPARQL query. Upon calling the methods if this class, a string query is created that
 * conforms to the rules of SPARQL. There are four query-construction functions allowing to select the web services
 * ({@link #selectWebServices}) and the measures ({@link #selectMeasures}) included in the query and apply filters
 * ({@link #filter}) or control the order of the returned results ({@link #sort}).<br>
 * <br>
 * This class also allows Method Chaining (suggested reading: <a href="http://en.wikipedia.org/wiki/Method_chaining">
 * http://en.wikipedia.org/wiki/Method_chaining</a>), i.e.
 * allowing to chain the methods such as in the following example:<br>
 * {@code OntologyQoSQuery query = new OntologyQoSQuery()}<br>
 * &nbsp;&nbsp;&nbsp;{@code .selectWebServices("ArtistRegistry", "Code2Web").selectMeasures("Accessability_AC")}<br>
 * &nbsp;&nbsp;&nbsp;{@code .filter("Accessability_AC > 0.95").sort("DESC(Accessability_AC)");}<br>
 * <br>
 * This class allows creating very complex queries since any function can be called multiple times (e.g. apply more
 * filters). Although the order of the functions is not important, it is highly recommended that functions
 * {@link #selectWebServices} and {@link #selectMeasures} are called before any {@link #sort} or {@link #filter}
 * functions so that the queries are easy to comprehend.
 * 
 * @author Themistoklis Diamantopoulos and Davide Tosi
 */
public class OntologyQoSQuery {

	/** The select part of the query which includes the measures. */
	private String selectString;

	/** The first where part of the query which includes the web service names. */
	private String whereStringWS;

	/** The second where part of the query which includes the measures. */
	private String whereStringM;

	/** The third where part of the query which includes the filter expressions. */
	private String whereStringF;

	/** The fourth where part of the query which includes variable definitions. */
	private String whereStringD;

	/** The order_by part of the query which includes the sort expressions. */
	private String orderString;

	/** A map containing the names of the measures and their properties. */
	private HashMap<String, HashSet<String>> measuresMap;

	/** A set containing the names of the web services included in the query. */
	private HashSet<String> serviceNamesSet;

	/** A set containing the names of the variables included in the query. */
	private HashSet<String> variablesSet;

	/**
	 * Initializes this query. All parts of the query ({@link #selectString}, {@link #whereStringM},
	 * {@link #whereStringWS}, {@link #whereStringF}, {@link #orderString}) are set to their default values, and the
	 * three sets ({@link #measureNamesSet}, {@link #serviceNamesSet}), and {@link #selectSet} are also initialized.
	 */
	public OntologyQoSQuery() {
		selectString = "";
		whereStringWS = "";
		whereStringM = "";
		whereStringF = "";
		whereStringD = "";
		orderString = "";
		measuresMap = new HashMap<String, HashSet<String>>();
		serviceNamesSet = new HashSet<String>();
		variablesSet = new HashSet<String>();
	}

	/**
	 * Declares the web services that are going to be queried. Creates the first part of the where string
	 * ({@code whereStringWS}).
	 * 
	 * @param webServiceNames the names of the web services that the query is performed on.
	 * @return {@code this} object in order to allow method chaining.
	 */
	public OntologyQoSQuery selectWebServices(final String... webServiceNames) {
		whereStringWS = "WHERE {\n";
		whereStringWS += "   ?webservice wn:WebServiceName ?WebServiceName;\n      ";
		for (String webServiceName : webServiceNames) {
			if (!serviceNamesSet.contains(webServiceName)) {
				serviceNamesSet.add(webServiceName);
				whereStringWS += "{?webservice wn:WebServiceName '" + webServiceName + "'} UNION ";
			}
		}
		whereStringWS = whereStringWS.substring(0, whereStringWS.length() - 7);
		return this;
	} 

	/**
	 * Adds a new variable to be able to select it and perform queries on it. The new variable may either be a measure
	 * (e.g. {@code Successability_SU}) or a property of a measure (e.g. {@code Successability_SU__PValue} or
	 * {@code PValue(Successability_SU)}).
	 * 
	 * @param selectVariable the new variable to be added.
	 */
	private void addSelectVariable(String selectVariable) {
		String[] variableParts = selectVariable.split("__");
		String measureName = variableParts[0];
		if (!measuresMap.containsKey(measureName)) {
			measuresMap.put(measureName, new HashSet<String>());
		}
		if (variableParts.length == 2) {
			String property = variableParts[1];
			measuresMap.get(measureName).add(property);
		}
	}

	/**
	 * Declares the measures that are going to be selected. Note that this function does not need to be called at all at
	 * some cases! When the filter and sort functions are called, the measures taking part will be automatically added
	 * (if they are not already) in the query. This function is thus called in order to add any specific measure that we
	 * would like to see in the final result, regardless of whether it is used for some query condition. However, you
	 * are STRONGLY advised to declare all the measures that are going to be used in this function in order to create a
	 * more readable query.
	 * 
	 * @param measureNames the names of the measure to be included in the query.
	 * @return {@code this} object in order to allow method chaining.
	 */
	public OntologyQoSQuery selectMeasures(final String... measureNames) {
		for (String measureName : measureNames) {
			addSelectVariable(transformExpression(measureName));
		}
		return this;
	}

	/**
	 * Declares a number of sort predicates for this query. Sort predicates are declared as strings of the form
	 * {@code "DESC(measureName)"} (for descending sort order) or {@code "ASC(measureName)"} (for ascending sort order).
	 * If a string of the form {@code "measureName"} is provided (i.e. without {@code DESC} or {@code ASC}), then the
	 * predicate defaults to the ascending sort order. Examples function calls include:<br>
	 * {@code "ASC(measureName)"} : sort first by ascending {@code measureName}.<br>
	 * {@code "DESC(measureName1)", "measureName2"} : sort first by descending {@code measureName1}, and then by
	 * ascending {@code measureName2}.<br>
	 * {@code "DESC(MeasureValueKind(measureName))} : sort by descending value kind for measure {@code measureName}.<br>
	 * <br>
	 * Note that calling this function multiple times actually adds more sort predicates to the original list. The
	 * services are anyway sorted using first the first predicate, after that the second, etc. The calling order between
	 * this function and function {@link #filter} is irrelevant.
	 * 
	 * @param sortPredicates the sort predicates to be applied on the query.
	 * @return {@code this} object in order to allow method chaining.
	 */
	public OntologyQoSQuery sort(final String... sortPredicates) {
		if (orderString == "")
			orderString = "ORDER BY";
		for (String sortPredicate : sortPredicates) {
			sortPredicate = transformSortPredicate(sortPredicate);
			orderString += " " + sortPredicate;
		}
		return this;
	}

	/**
	 * Declares some filter predicates for this query. Filter predicates are declared as strings of various forms
	 * depending on the type of the measure to be filtered and the filter requirements. Examples include:<br>
	 * {@code "measureName > 0.95"} : checking that the value of the measure is lower than a number.<br>
	 * {@code "measureName = 0.9"} : checking that the value of the measure is equal to a number.<br>
	 * {@code "MeasureValueKind(measureName) = 'Raw'"} : checking the measure kind ({@code "Raw"} or {@code "Average"}).<br>
	 * {@code "PValue(measureName) > 0.2"} : checking the p-value of the measure's validation statistical test.<br>
	 * <br>
	 * Note that the filter predicates of this function are connected with the {@code OR} operator, meaning that the
	 * function will filter (and therefore keep) all the results that cover one or more of the predicates. For example,
	 * if the function is given two arguments: {@code "Accessability_AC > 0.95"} and {@code "Accessability_AC < 0.75"}
	 * then it keeps all instances of which the {@code Accessability_AC} is either lower than 0.75 or greater than 0.95.<br>
	 * <br>
	 * Performing an {@code AND} operation requires calling the function again. Notice, for example, the query:<br>
	 * {@code OntologyQoSQuery query = new OntologyQoSQuery()}<br>
	 * &nbsp;&nbsp;&nbsp;{@code .selectWebServices("ArtistRegistry", "Code2Web").selectMeasures("Accessability_AC")}<br>
	 * &nbsp;&nbsp;&nbsp;{@code .filter("Accessability_AC > 0.95", "Accessability_AC < 0.75")}<br>
	 * &nbsp;&nbsp;&nbsp;{@code .filter("MeasureValueKind(Accessability_AC) = 'Raw'");}<br>
	 * This query returns instances that have {@code Accessability_AC} lower than 0.75 {@code OR}
	 * {@code Accessability_AC} greater than 0.95 {@code AND} the kind of the {@code Accessability_AC} measure is 'Raw'.
	 * 
	 * @param filterPredicates the filter predicates to be applied on the query (unioned with the {@code OR} operator).
	 * @return {@code this} object in order to allow method chaining.
	 */
	public OntologyQoSQuery filter(final String... filterPredicates) {
		String finalPredicates = "";
		for (String filterPredicate : filterPredicates) {
			filterPredicate = transformFilterPredicate(filterPredicate);
			finalPredicates += filterPredicate + " || ";
		}
		whereStringF += "\n   FILTER(" + finalPredicates.substring(0, finalPredicates.length() - 4) + ").";
		return this;
	}

	/**
	 * Declares a new variable to be used for this query. The new variable can be defined using common math operators
	 * (e.g. +, -, *, /), measure names, or other variables. See
	 * <a href="http://www.w3.org/TR/sparql11-query/#OperatorMapping">http://www.w3.org/TR/sparql11-query/#
	 * OperatorMapping</a>) for a list of supported operators. Examples include:<br>
	 * {@code "DoubleBugs", "2 * Bugs"}<br>
	 * {@code "RegressionOfX", "0.23 * X * X + 0.65 * X + 0.87"}<br>
	 * <br>
	 * Note that the exponential operator is not supported, so one can either use the multiplication operator or use
	 * some kind of approximation. This function can be called multiple times, so that temporary variables are also
	 * declared. For example: {@code "MyTempVariableForBugs", "2 * Bugs"}<br>
	 * {@code "FinalQuality", "1 / MyTempVariableForBugs"}<br>
	 * <br>
	 * The calling order between this function and functions {@link #filter} and {@link #sort} is irrelevant.
	 * 
	 * @param variableName the name of the new variable to be defined.
	 * @param variableDefinition the definition of the variable including common math operators.
	 * @return {@code this} object in order to allow method chaining.
	 */
	public OntologyQoSQuery define(final String variableName, final String variableDefinition) {
		String varDef = variableDefinition;
		for (String measureName : measuresMap.keySet()) {
			varDef = varDef.replaceAll("(^|[^a-zA-Z_])" + measureName, "$1?" + measureName);
		}
		for (String avariableName : variablesSet) {
			varDef = varDef.replaceAll("(^|[^a-zA-Z_])" + avariableName, "$1?" + avariableName);
		}
		variablesSet.add(variableName);
		whereStringD += "\n   BIND((" + varDef + ") AS ?" + variableName + ").";
		return this;
	}

	/**
	 * Returns the first match of the regular expression {@code regex} when performed on the {@code string}. If there is
	 * no such match, this function returns {@code null}.
	 * 
	 * @param string the string to be seeked.
	 * @param regex the regular expression to match against the string.
	 * @return the first match of {@code regex} on the {@code string}, or {@code null} if there is no such match.
	 */
	private static String getFirstStringAccordingtoRegex(String string, String regex) {
		Matcher newleftpartmatcher = Pattern.compile(regex).matcher(string);
		if (newleftpartmatcher.find()) {
			return newleftpartmatcher.group(0).trim();
		} else {
			return null;
		}
	}

	/**
	 * Transforms the expression given to a format compliant with the query language. Examples:<br>
	 * {@code ASC(MeasureValue(Successability_SU))} becomes {@code ASC(Successability_SU__MeasureValue)}.
	 * {@code PValue(Successability_SU) > 0.5} becomes {@code Successability_SU__PValue > 0.5}.
	 * 
	 * Additionally, this function adds the variables and the measures taking part in the expression.
	 * 
	 * @param expression the expression to be transformed.
	 * @return the transformed expression.
	 */
	private String transformExpression(String expression) {
		String complexCommand = getFirstStringAccordingtoRegex(expression, ".*\\(.*\\)");
		if (complexCommand != null) {
			// Complex command such as MeasureValue(Accessability_AC)
			String property = getFirstStringAccordingtoRegex(complexCommand, ".*\\(");
			property = property.substring(0, property.length() - 1).trim(); // Remove trailing parentheses
			String measure = getFirstStringAccordingtoRegex(complexCommand, "\\(.*\\)");
			measure = measure.substring(1, measure.length() - 1).trim(); // Remove leading and trailing parentheses
			if (property.equals("MeasureValue")) {
				addSelectVariable(measure);
				return measure;
			} else {
				addSelectVariable(measure + "__" + property);
				return measure + "__" + property;
			}
		} else {
			// Simple command such as Accessability_AC or full command such as Successability_SU__PValue
			addSelectVariable(expression);
			return expression;
		}
	}

	/**
	 * Transforms a predicate using regular expressions. This function is needed to allow all different formats for sort
	 * filters. For example, supported formats include {@code Successability_SU}, {@code ASC(Successability_SU)}, or
	 * more complex ones including also metric properties, like {@code ASC(MeasureValue(Successability_SU))} and
	 * {@code ASC(Successability_SU__MeasureValue)}. Note e.g. that the second to last of these examples has to be
	 * transformed to the last one.
	 * 
	 * @param sortPredicate the predicate to be transformed.
	 * @return the new (transformed) predicate.
	 */
	public String transformSortPredicate(String sortPredicate) {
		String leftPart = getFirstStringAccordingtoRegex(sortPredicate, "(DESC\\s*\\(.*\\))|(ASC\\s*\\(.*\\))");
		if (leftPart != null) {
			String innerLeftPart = getFirstStringAccordingtoRegex(leftPart, "(?<=\\().*(?=\\))");
			if (sortPredicate.contains("DESC"))
				return "DESC(?" + transformExpression(innerLeftPart) + ")";
			else
				// if (sortPredicate.contains("ASC"))
				return "ASC(?" + transformExpression(innerLeftPart) + ")";
		} else {
			return "?" + transformExpression(sortPredicate);
		}
	}

	/**
	 * Transforms a predicate using regular expressions. This function is needed to allow two formats for the properties
	 * of individuals in the left part of the filter. For example, {@code PValue(Successability_SU) > 0.5} is the same
	 * as {@code Successability_SU__PValue > 0.5}, however only the second one can be used in the query.
	 * 
	 * @param filterPredicate the predicate to be transformed.
	 * @return the new (transformed) predicate.
	 */
	public String transformFilterPredicate(String filterPredicate) {
		filterPredicate = filterPredicate.replaceAll("==", "=");
		String leftPart = getFirstStringAccordingtoRegex(filterPredicate, "[a-zA-Z0-9\\(_\\s*]*\\)");
		if (leftPart == null)
			leftPart = getFirstStringAccordingtoRegex(filterPredicate, "[a-zA-Z0-9_]*");
		return filterPredicate.replace(leftPart, "?" + transformExpression(leftPart));
	}

	/**
	 * This method overrides the known {@code toString} method that returns a representation of the string. The main
	 * difference, however, is that the string returned is a SPARQL query. So it can be used for querying the ontology. <br>
	 * <br>
	 * <i>(Note: This function is not very fast, so it is suggested to call it only once every time the query
	 * changes.)</i>
	 * 
	 * @return a string representation of the object.
	 */
	@Override
	public String toString() {
		selectString = "SELECT ?WebServiceName";
		whereStringM = "";
		for (String measureName : measuresMap.keySet()) {
			selectString += " ?" + measureName;
			if (!variablesSet.contains(measureName)) {
				whereStringM += "\n   ?webservice wn:has_measure ?" + measureName + "measure.\n";
				whereStringM += "      ?" + measureName + "measure a wn:" + measureName + ".\n";
				whereStringM += "      ?" + measureName + "measure wn:MeasureValue ?" + measureName + ".";
				for (String property : measuresMap.get(measureName)) {
					selectString += " ?" + measureName + "__" + property;
					whereStringM += "\n      ?" + measureName + "measure wn:" + property + " ?" + measureName + "__"
							+ property + ".";
				}
			}
		}
		String whereString = whereStringWS + whereStringM + whereStringD + whereStringF;
		return selectString + "\n" + whereString + "\n}" + (orderString != "" ? "\n" + orderString : "");
	}
}
