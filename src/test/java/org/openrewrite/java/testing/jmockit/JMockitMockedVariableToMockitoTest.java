/*
 * Copyright 2024 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openrewrite.java.testing.jmockit;

import org.junit.jupiter.api.Test;
import org.openrewrite.DocumentExample;
import org.openrewrite.InMemoryExecutionContext;
import org.openrewrite.java.JavaParser;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.java.Assertions.java;

class JMockitMockedVariableToMockitoTest implements RewriteTest {
    @Override
    public void defaults(RecipeSpec spec) {
        spec
          .parser(JavaParser.fromJavaVersion()
            .logCompilationWarningsAndErrors(true)
            .classpathFromResources(new InMemoryExecutionContext(),
              "junit-jupiter-api-5.9",
              "jmockit-1.49",
              "mockito-core-3.12",
              "mockito-junit-jupiter-3.12"
            ))
          .recipeFromResource(
            "/META-INF/rewrite/jmockit.yml",
            "org.openrewrite.java.testing.jmockit.JMockitToMockito"
          );
    }

    @DocumentExample
    @Test
    void mockedVariableTest() {
        //language=java
        rewriteRun(
          java(
            """
              import mockit.Mocked;
              
              import static org.junit.jupiter.api.Assertions.assertNotNull;
                          
              class A {
                  @Mocked
                  Object mockedObject;
              
                  void test(@Mocked Object o, @Mocked Object o2) {
                      assertNotNull(o);
                      assertNotNull(o2);
                  }
              }
              """,
            """
              import org.mockito.Mock;
              import org.mockito.Mockito;
              
              import static org.junit.jupiter.api.Assertions.assertNotNull;
                          
              class A {
                  @Mock
                  Object mockedObject;
              
                  void test() {
                      Object o = Mockito.mock(Object.class);
                      Object o2 = Mockito.mock(Object.class);
                      assertNotNull(o);
                      assertNotNull(o2);
                  }
              }
              """
          )
        );
    }

    @Test
    void noVariableTest() {
        rewriteRun(
          //language=java
          java(
            """
              import mockit.Mocked;
              
              import static org.junit.jupiter.api.Assertions.assertNotNull;
                          
              class A {
                  @Mocked
                  Object mockedObject;
              
                  void test() {
                      assertNotNull(mockedObject);
                  }
              }
              """,
            """
              import org.mockito.Mock;
              
              import static org.junit.jupiter.api.Assertions.assertNotNull;
                          
              class A {
                  @Mock
                  Object mockedObject;
              
                  void test() {
                      assertNotNull(mockedObject);
                  }
              }
              """
          )
        );
    }
}
