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

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.JavaFileScanner;
import org.sonar.plugins.java.api.JavaFileScannerContext;
import org.sonar.plugins.java.api.tree.BaseTreeVisitor;
import org.sonar.plugins.java.api.tree.ClassTree;
import org.sonar.plugins.java.api.tree.IdentifierTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.samples.java.checks.PrinterVisitor;

/**
 * Classes defining a REST resource must be annotated with <code>@Api</code>.
 *
 * @since 1.0
 */
@Rule(
    key = "br.jus.tst.sonar.checks.ApiDocumentationRule",
    priority = Priority.BLOCKER,
    // @checkstyle LineLengthCheck (1 line)
    description = "A class annotated with @Path must also be annotated with @Api"
)
public final class ApiDocumentationRule extends BaseTreeVisitor
    implements JavaFileScanner {

    /**
     * Annotation name.
     */
    private static final String PATH = "Path";

    /**
     * Annotation name.
     */
    private static final String API = "Api";

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
    public void visitClass(final ClassTree tree) {
        final AtomicReference<Tree> path = new AtomicReference<>();
        final Set<String> anns = tree.modifiers().annotations()
            .stream()
            .filter(
                annTree -> annTree.annotationType().is(Tree.Kind.IDENTIFIER)
            )
            .map(annTree -> (IdentifierTree) annTree.annotationType())
            .peek(
                idf -> {
                    if (idf.name().equals(ApiDocumentationRule.PATH)) {
                        path.set(idf);
                    }
                }
            )
            .map(IdentifierTree::name)
            .collect(Collectors.toSet());
        if (anns.contains(ApiDocumentationRule.PATH)
            && !anns.contains(ApiDocumentationRule.API)) {
            final String template =
                "Classes annotated with @%s must also be annotated with @%s";
            this.context.reportIssue(
                this,
                Optional.ofNullable(path.get()).orElse(tree),
                String.format(
                    template,
                    ApiDocumentationRule.PATH,
                    ApiDocumentationRule.API
                )
            );
        }
        super.visitClass(tree);
    }

}
