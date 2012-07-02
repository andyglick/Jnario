package org.jnario.feature.tests.integration;

import org.eclipse.xtext.xbase.lib.InputOutput;
import org.jnario.jnario.test.util.FeatureExecutor;
import org.jnario.runner.FeatureRunner;
import org.jnario.runner.Named;
import org.jnario.runner.Order;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(FeatureRunner.class)
@Named("Scenario: Access a variable that was assigned a value of a variable defined in a different step")
@SuppressWarnings("all")
public class AccessOfVariablesFeatureAccessAVariableThatWasAssignedAValueOfAVariableDefinedInADifferentStep {
  @Test
  @Order(0)
  @Named("Given a jnario file")
  public void givenAJnarioFile() {
    this.jnarioFile = "\n\t\t\t\tpackage bootstrap3\n\t\t\t\tFeature: Variable test\n\t\t\t\t\tScenario: Some scenario\n\t\t\t\t\t\tint x\n\t\t\t\t\t\tint y\n\t\t\t\t\t\tGiven a step with a variable\n\t\t\t\t\t\t\tx = 3\n\t\t\t\t\t\tWhen I assign it a different value\n\t\t\t\t\t\t\ty = x\n\t\t\t\t\t\tThen y should be 3\n\t\t\t\t\t\t\ty => 3\n\t\t\t";
  }
  
  @Test
  @Order(1)
  @Ignore
  @Named("[PENDING] When it is executed")
  public void whenItIsExecuted() {
    
  }
  
  @Test
  @Order(2)
  @Named("Then it should be successful")
  public void thenItShouldBeSuccessful() {
      InputOutput.<String>println("executing scenario");
      FeatureExecutor.executesSuccessfully(jnarioFile);
  }
  
  CharSequence jnarioFile;
}
