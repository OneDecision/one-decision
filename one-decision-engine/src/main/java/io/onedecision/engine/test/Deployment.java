
package io.onedecision.engine.test;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation for a test method to create and delete a deployment around a test
 * method.
 * 
 * <p>
 * Usage:
 * </p>
 * 
 * <pre>
 * package org.example;
 * 
 * ...
 * 
 * public class ExampleTest {
 * 
 *   &#64;Deployment(resources = { "com/example/Definitions1.dmn" },
 *     tenantId = "example")
 *   public void testForATenantDeploymentWithASingleResource() {
 *     // a deployment will be available in the repository
 *     // containing the single resource for the specified tenant
 *   }
 * </pre>
 * 
 * @author Tim Stephenson
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Deployment {

  /** Specify resources that make up the process definition. */
  public String[] resources() default {};
  
  /** Specify tenantId to deploy for */
  public String tenantId() default "";
}
