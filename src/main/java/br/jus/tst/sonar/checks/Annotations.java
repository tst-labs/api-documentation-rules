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

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.sonar.plugins.java.api.tree.AnnotationTree;
import org.sonar.plugins.java.api.tree.ClassTree;
import org.sonar.plugins.java.api.tree.IdentifierTree;
import org.sonar.plugins.java.api.tree.MethodTree;
import org.sonar.plugins.java.api.tree.ModifiersTree;
import org.sonar.plugins.java.api.tree.Tree;

/**
 * Operates only on annotations.
 * @since 1.0
 */
public final class Annotations {
    /**
     * ModifiersTree provided by Sonar.
     */
    private final ModifiersTree tree;

    /**
     * Main constructor.
     * @param tree ModifiersTree.
     */
    public Annotations(final ModifiersTree tree) {
        this.tree = tree;
    }

    /**
     * Extracts the ModifiersTree contained in a ClassTree.
     * @param tree ClassTree.
     */
    public Annotations(final ClassTree tree) {
        this(tree.modifiers());
    }

    /**
     * Extracts the ModifiersTree contained in a MethodTree.
     * @param tree MethodTree.
     */
    public Annotations(final MethodTree tree) {
        this(tree.modifiers());
    }

    /**
     * The stream contains only annotations.
     * @return IdentifierTree.
     */
    public Stream<IdentifierTree> stream() {
        final Stream<org.sonar.plugins.java.api.tree.AnnotationTree> stream =
            this.tree.annotations().stream();
        return stream
            .filter(Annotations.annotationIdentifiersOnly())
            .map(Annotations.toIdentifierTree());
    }

    /**
     * Maps Annotations to IdentifierTree.
     * @return Mapping function.
     */
    private static Function<AnnotationTree, IdentifierTree> toIdentifierTree() {
        return annTree -> (IdentifierTree) annTree.annotationType();
    }

    /**
     * Used to filter only annotation identifiers.
     * @return Predicate.
     */
    private static Predicate<AnnotationTree> annotationIdentifiersOnly() {
        return ann -> ann.annotationType().is(Tree.Kind.IDENTIFIER);
    }
}
