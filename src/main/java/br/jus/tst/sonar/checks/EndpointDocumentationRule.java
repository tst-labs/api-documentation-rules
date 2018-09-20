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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.JavaFileScanner;
import org.sonar.plugins.java.api.JavaFileScannerContext;
import org.sonar.plugins.java.api.tree.BaseTreeVisitor;
import org.sonar.plugins.java.api.tree.IdentifierTree;
import org.sonar.plugins.java.api.tree.MethodTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.samples.java.checks.PrinterVisitor;

/**
 * Java methods implementing HTTP methods must be documented.
 * @since 1.0
 */
@Rule(
    key = "br.jus.tst.sonar.checks.EndpointDocumentationRule",
    priority = Priority.BLOCKER,
    // @checkstyle LineLengthCheck (1 line)
    description = "A method annotated with @GET, @POST, @DELETE or @POST must also be annotated with @ApiOperation"
)
public final class EndpointDocumentationRule extends BaseTreeVisitor
    implements JavaFileScanner {

    /**
     * HTTP methods.
     */
    private static final Set<String> HTTP_METHODS = new HashSet<>(
        Arrays.asList("GET", "PUT", "DELETE", "POST", "OPTIONS", "HEAD")
    );

    /**
     * Scanner context.
     */
    private JavaFileScannerContext context;

    @Override
    public void scanFile(final JavaFileScannerContext jfsc) {
        this.context = jfsc;
        scan(this.context.getTree());
        final Logger logger = org.slf4j.LoggerFactory
            .getLogger(ApiDocumentationRule.class);
        if (logger.isDebugEnabled()) {
            logger.debug(PrinterVisitor.print(this.context.getTree()));
        }
    }

    @Override
    public void visitMethod(final MethodTree tree) {
        final String operation = "ApiOperation";
        final AtomicReference<Tree> path = new AtomicReference<>();
        final Set<String> anns = new Annotations(tree).stream()
            .peek(
                idf -> {
                    if (EndpointDocumentationRule.HTTP_METHODS
                        .contains(idf.name())) {
                        path.set(idf);
                    }
                }
            )
            .map(IdentifierTree::name)
            .collect(Collectors.toSet());
        if (EndpointDocumentationRule.isEndpoitMethod(anns)
            && !anns.contains(operation)) {
            final String template =
                "Methods annotated with @%s must also be annotated with @%s";
            this.context.reportIssue(
                this,
                path.get(),
                String.format(
                    template,
                    ((IdentifierTree) path.get()).name(),
                    operation
                )
            );
        }
        super.visitMethod(tree);
    }

    /**
     * A method is a REST endpoint if it is annotated with <code>@GET</code>,
     * <code>@POST</code>, <code>@DELETE</code> or <code>@POST</code>.
     * @param found All annotations found.
     * @return Boolean.
     */
    private static boolean isEndpoitMethod(final Set<String> found) {
        return found.removeAll(EndpointDocumentationRule.HTTP_METHODS);
    }

}
