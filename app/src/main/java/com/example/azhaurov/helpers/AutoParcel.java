package com.example.azhaurov.helpers;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface AutoParcel {

  /**
   * Specifies that AutoParcel should generate an implementation of the annotated class or interface,
   * to serve as a <i>builder</i> for the value-type class it is nested within. As a simple example,
   * here is an alternative way to write the {@code Person} class mentioned in the {@link AutoParcel}
   * example: <pre>
   *
   *   &#64;AutoParcel
   *   abstract class Person {
   *     static Builder builder() {
   *       return new AutoParcel_Person.Builder();
   *     }
   *
   *     abstract String name();
   *     abstract int id();
   *
   *     &#64;AutoParcel.Builder
   *     interface Builder {
   *       Builder name(String x);
   *       Builder id(int x);
   *       Person build();
   *     }
   *   }</pre>
   *
   * <p><b>This API is provisional and subject to change.</b></p>
   *
   * @author Eamonn McManus
   */
  @Retention(RetentionPolicy.SOURCE)
  @Target(ElementType.TYPE)
  public @interface Builder {
  }

  @Retention(RetentionPolicy.SOURCE)
  @Target(ElementType.METHOD)
  public @interface Validate {
  }
}