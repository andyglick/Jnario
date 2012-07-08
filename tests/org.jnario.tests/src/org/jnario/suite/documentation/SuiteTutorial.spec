package org.jnario.suite.documentation

import static org.jnario.jnario.test.util.ResultMatchers.*
import com.google.inject.Inject
import org.jnario.jnario.test.util.ModelStore
import org.jnario.runner.CreateWith
import org.jnario.jnario.test.util.SuiteTestCreator
import static extension org.jnario.jnario.test.util.SuiteExecutor.*
import static extension org.jnario.jnario.test.util.Resources.*

/*
 * Suites in Jnario serve two purposes:
 * 
 * 1. **Grouping:** A suite groups multiple specifications and executes them together. For example, a single 
 *    Jnario suite could run all specifications in a project or there are different two suites, one containing 
 *    all unit specifications and one containing all acceptance specifications.  
 * 2. **Documentation:** Jnario generates HTML reports for suites making them a great way to provide additional
 *    documentation. For example, one can create an overview page describing the overall architecture
 *    of an application, which then links to other specifications describing the different parts 
 *    in more detail. 
 *    The overview page of the [Jnario documentation](http://jnario.org/org/jnario/JnarioDocumentationSuite.html)
 *    is a good example for this.
 */
@CreateWith(typeof(SuiteTestCreator))
describe "Introducing Jnario Suites"{

	/*
	 * First of all you need a running installation of Jnario. If you haven't installed 
	 * Jnario yet, follow the [install guide](../../jnario/documentation/InstallingJnarioSpec.html).
	 * To get started first create a new Java project and a new Jnario suite using the suite wizard
 	 * (**File** -> **New** -> **Other** -> **Jnario** -> **Suite**).
 	 * <p align="center"><img src="/img/tutorial/suite_wizard.png" alt="New Suite Wizard"/></p>
 	 * Name the suite file "MySuite" and as description use "My Jnario Suite". The created suite will
 	 * look like:
 	 * 
	 * <span class="label label-info">Tip</span> If the editor shows a validation error, 
	 * because of the missing jnario libraries, add them 
 	 * to the classpath using the quickfix.
 	 * 
 	 * <p align="center"><img src="/img/tutorial/suite_buildpath.png" alt="New Suite Error"/></p>
	 *
	 */
	describe "Getting Started"{
		@Inject extension ModelStore 
		/*
		 * A suite consists of a title and a list of references to other specifications, where 
		 * a specification is either feature definition, a unit spec or another suite. The suite 
		 * title is defined by '**#**' followed by the title. In our example suite 
		 * it is: `#My Suite`. Referenced specifications are listed after '**-**' by their name
		 * in quotes. 
		 * 
		 * <span class="label label-info">Info</span> In order to reference specifications defined in
		 * different packages it is necessary to add corresponding import statements. The 
		 * best thing is to use the code-completion as it will automatically add the
		 * required imports.
		 * 
		 * @lang(none)
		 * @filter('''|\.parseSuite.nrOfParseAndLinkingErrors => 2) 
		 */
		fact "A simple Suite"{
			'''
			package demo
			
			#My Suite
			
			- "My Feature"
			- "My Spec"
			'''.parseSuite.nrOfParseAndLinkingErrors => 2
		}
		
		/*
		 * The editor will show two validation errors as the referenced specifications 
		 * cannot be resolved. Fix this by adding a new feature file "MyFeature.feature"
		 * in the same package:

		 * <pre class="prettyprint lang-feature">
		 * package demo
		 * Feature: My Feature
		 * Scenario: My Scenario
		 *   When something happens
		 *   Then it happens
	   	 * </pre>
	   	 * 
	   	 * and a new spec file "MySpec.spec":
	   	 * 
	   	 * <pre class="prettyprint lang-feature">
	   	 * package demo
		 *	
		 * describe "My Spec"{
		 *   fact "hello".length => 5
		 * }
		 * </pre>
		 * 
		 * When the validation errors are fixed we can execute the suite as a normal JUnit 
		 * test via right-click and **run-as->JUnit Test**.
		 * 
	 	 * <span class="label label-info">Tip</span> There is a quick fix (CMD/Ctrl + 1)
	 	 *  available for unresolved specifications to create a new one.
		 */
		fact "Fixing the Errors"{
		}
		
		def parseExampleScenarioAndSpec(){
			 '''
				package demo
				Feature: My Feature
				Scenario: My Scenario
					When something happens
					Then it happens
				'''.parseScenario
				
			'''
				package demo
				
				describe "My Spec"{
					fact "hello".length => 5
				}
			'''.parseSpec
		}
		
		/*
		 * Suites can be structured hierarchically:
		 * 
		 *     #Headline
		 *     ##Section 1
		 *     ###Subsection 1.1 
		 *     ###Subsection 1.2
		 *     ##Section 2
		 *     ###Subsection 2.1
		 *     ###Subsection 2.2
		 * 
		 * Each subsection can separately list specifications. The specifications
		 * will be executed in the same hierarchy:
		 * 
 	 	 * <p align="center"><img src="/img/tutorial/suite_execution.png" alt="Running a suite"/></p>
		 * 
		 * Each section can have additional text written in [Markdown Syntax]
		 * (http://daringfireball.net/projects/markdown/syntax). This text will
		 * also be generated into the HTML reports. The referenced specs can also have
		 * a text description after a colon.
		 * 
		 * <span class="label label-info">Important</span> The characters '#' and '-' are
		 * keywords in Jnario suites. They must be escaped like this '\\#' and '\\-' if 
		 * they are used in normal text.
 	 	 *  
	 	 * @filter(parseExampleScenarioAndSpec|'''|\.parseSuite.hasNoParseAndLinkingErrors) 
	 	 * @lang(none)
		 */
		fact "Structuring a Suite"{
			parseExampleScenarioAndSpec
			'''
			package demo

			#My Suite
			
			This is the description of  suite. It is possible to use 
			[Markdown Syntax](http://daringfireball.net/projects/markdown/syntax)
			for **formatting** the text and adding images or links. 
			
			##My Features
			
			Here we list all our features...
			
			- "My Feature": this is an example feature.
			
			##My Specs
			
			...and here are all our unit specs:
			
			- "My Spec": this is an example unit spec.
			   with a multiline description.
			'''.parseSuite.hasNoParseAndLinkingErrors
		}
		
		fact "Selecting multiple Specifications"
	}

}