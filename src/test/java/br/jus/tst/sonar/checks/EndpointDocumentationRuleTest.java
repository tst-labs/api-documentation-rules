/*
 * Copyright (C) 2018 Tribunal Superior do Trabalho.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package br.jus.tst.sonar.checks;

import org.junit.Test;
import org.sonar.java.checks.verifier.JavaCheckVerifier;

/**
 * Unit tests.
 * @since 1.0
 */
public final class EndpointDocumentationRuleTest {

    /**
     * Should detect violations.
     */
    @Test
    public void detected() {
        final EndpointDocumentationRule check = new EndpointDocumentationRule();
        JavaCheckVerifier
            .verify("src/test/files/EndpointAnnotationCheck.java", check);
    }

    /**
     * No violations should be found.
     */
    @Test
    public void shouldNotDetect() {
        final EndpointDocumentationRule check = new EndpointDocumentationRule();
        JavaCheckVerifier.verifyNoIssue(
            "src/test/files/EndpointAnnotationCheckCompliant.java",
            check
        );
    }

}
