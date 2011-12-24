package de.bmw.carit.jnario.spec.compiler;

import static com.google.common.collect.Sets.newHashSet;
import static org.eclipse.xtext.util.Strings.convertToJavaString;

import java.util.Iterator;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.common.types.JvmTypeReference;
import org.eclipse.xtext.serializer.ISerializer;
import org.eclipse.xtext.xbase.XAbstractFeatureCall;
import org.eclipse.xtext.xbase.XBinaryOperation;
import org.eclipse.xtext.xbase.XBlockExpression;
import org.eclipse.xtext.xbase.XExpression;
import org.eclipse.xtext.xbase.compiler.IAppendable;
import org.eclipse.xtext.xbase.util.XExpressionHelper;
import org.eclipse.xtext.xtend2.compiler.Xtend2Compiler;

import com.google.common.collect.Iterables;
import com.google.inject.Inject;

import de.bmw.carit.jnario.spec.spec.Assertion;

@SuppressWarnings("restriction")
public class SpecCompiler extends Xtend2Compiler {

	@Inject
	private ISerializer serializer;
	
	
	@Inject
	private XExpressionHelper expressionHelper; 

	
	public void _toJavaStatement(Assertion assertion, IAppendable b, boolean isReferenced) {
		if (assertion.getExpression() == null){
			return;
		}
		if (assertion.getExpression() instanceof XBlockExpression) {
			XBlockExpression blockExpression = (XBlockExpression) assertion.getExpression();
			generateMultiAssertions(blockExpression.getExpressions(), b);
		}else{
			generateSingleAssertion(assertion.getExpression(), b);
		}
	}
	
	private void generateSingleAssertion(XExpression expr, IAppendable b) {
		internalToJavaStatement(expr, b, true);
		b.append("\norg.junit.Assert.assertTrue(");
		generateMessageFor(expr, b);
		b.append(", ");
		internalToJavaExpression(expr, b);
		b.append(");\n");
	}

	private void generateMultiAssertions(EList<XExpression> expressions, IAppendable b) {
		for (XExpression expr : expressions) {
			generateSingleAssertion(expr, b);
		}
	}

	public void generateMessageFor(XExpression expression, IAppendable b) {
		b.append("\"\\nExpected ");
		b.append(serialize(expression));
		b.append(" but:\"");
		Set<String> valueExpressions = newHashSet();
		Iterator<XExpression> subExpressions = allSubExpressions(expression);
		if(subExpressions.hasNext()){
			while(subExpressions.hasNext()){
				appendActualValues(subExpressions.next(), b, valueExpressions);
			}
		}else{
			toLiteralValue(expression, b, valueExpressions);
		}
	}

	protected String serialize(XExpression expression) {
		String result = serializer.serialize(expression);
		result = result.trim();
		result = removeSurroundingParentheses(result).trim();
		return convertToJavaString(result);
	}

	protected String removeSurroundingParentheses(String result) {
		if(result.startsWith("(") && result.endsWith(")")){
			result = result.substring(1, result.length()-1);
		}
		return result;
	}

	protected void appendActualValues(XExpression expression, IAppendable b, Set<String> valueExpressions) {
		Iterator<XExpression> subExpressions = allSubExpressions(expression);
		toLiteralValue(expression, b, valueExpressions);
		while(subExpressions.hasNext()){
			appendActualValues(subExpressions.next(), b, valueExpressions);
		}
	}

	protected Iterator<XExpression> allSubExpressions(XExpression expression) {
		return Iterables.filter(expression.eContents(), XExpression.class).iterator();
	}

	protected void toLiteralValue(XExpression expression, IAppendable b, Set<String> valueMappings) {
		if(expressionHelper.isLiteral(expression)){
			return;
		}
		String expr = serialize(expression);
		if(valueMappings.contains(expr)){
			return;
		}
		valueMappings.add(expr);
		b.append("\n + \"\\n     ");
		b.append(expr);
		b.append(" is \" + ");
		boolean isString = isString(expression);
		if(isString){
			b.append("\"\\\"\" + ");
		} 
		toJavaExpression(expression, b);
		if(isString){
			b.append(" + \"\\\"\"");
		}
	}
	private boolean isString(XExpression expression) {
		JvmTypeReference expectedType = getTypeProvider().getType(expression, true);
		return expectedType != null && expectedType.getQualifiedName().equals(String.class.getName());
	}
	
	@Override
	protected boolean isVariableDeclarationRequired(XExpression expr,
			IAppendable b) {
		if (expr instanceof Assertion){
			return false;
		}
		return super.isVariableDeclarationRequired(expr, b);
	}
	
	/* 
	 * Overridden to evaluate all expressions first to be visible when generating the assertion message.
	 */
	protected void generateShortCircuitInvocation(final XAbstractFeatureCall binaryOperation,
			final IAppendable b) {
		if(EcoreUtil2.getContainerOfType(binaryOperation, Assertion.class) == null){
			super.generateShortCircuitInvocation(binaryOperation, b);
		}
		XExpression leftOperand = ((XBinaryOperation)binaryOperation).getLeftOperand();
		declareSyntheticVariable(binaryOperation, b);
		prepareExpression(leftOperand, b);
		
		for (XExpression arg : binaryOperation.getExplicitArguments()) {
			if (arg!=leftOperand)
				prepareExpression(arg, b);
		}
		
		b.append("\nif (");
		if (binaryOperation.getConcreteSyntaxFeatureName().equals(expressionHelper.getAndOperator())) {
			b.append("!");
		}
		toJavaExpression(leftOperand, b);
		b.append(") {").increaseIndentation();
		boolean rightOperand = binaryOperation.getConcreteSyntaxFeatureName().equals(expressionHelper.getOrOperator());
		b.append("\n").append(b.getName(binaryOperation)).append(" = ").append(Boolean.toString(rightOperand)).append(";");
		
		b.decreaseIndentation().append("\n} else {").increaseIndentation();
		
		if (binaryOperation.getImplicitReceiver()!=null) {
			internalToJavaStatement(binaryOperation.getImplicitReceiver(), b, true);
		}
		
		
		b.append("\n").append(b.getName(binaryOperation)).append(" = ");
		featureCalltoJavaExpression(binaryOperation, b);
		b.append(";");
		b.decreaseIndentation().append("\n}");
	}
	
}
