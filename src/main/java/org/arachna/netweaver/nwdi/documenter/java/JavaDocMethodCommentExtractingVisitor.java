/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.java;

import japa.parser.ast.body.JavadocComment;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.arachna.netweaver.nwdi.documenter.webservices.Function;
import org.arachna.netweaver.nwdi.documenter.webservices.Parameter;

/**
 * Implementation of {@link VoidVisitorAdapter} that extracts JavaDoc comments
 * from methods.
 * 
 * @author Dirk Weigenand
 */
public class JavaDocMethodCommentExtractingVisitor extends VoidVisitorAdapter {
    /**
     * Map methods names to {@link Function} objects.
     */
    private final Map<String, List<Function>> methodMapping = new HashMap<String, List<Function>>();

    /**
     * Provider for full class names from not fully qualified names.
     */
    private final ClassNameResolver classNameResolver;

    private final Pattern pattern = Pattern.compile(".*@inheritdoc.*", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

    /**
     * Create an instance of a
     * <code>JavaDocMethodCommentExtractingVisitor</code> that updates the given
     * methods with JavaDoc comments from the respective methods.
     * 
     * @param classNameProvider
     * 
     * @param methods
     *            list of methods to update with JavaDoc comments.
     */
    public JavaDocMethodCommentExtractingVisitor(final ClassNameResolver classNameProvider,
        final Iterable<Function> methods) {
        classNameResolver = classNameProvider;

        for (final Function method : methods) {
            List<Function> mappedMethods = methodMapping.get(method.getOriginalName());

            if (mappedMethods == null) {
                mappedMethods = new ArrayList<Function>();
            }

            mappedMethods.add(method);
            methodMapping.put(method.getOriginalName(), mappedMethods);
        }
    }

    @Override
    public void visit(final MethodDeclaration methodDeclaration, final Object arg) {
        final Function method = getFunction(methodDeclaration);

        if (method != null) {
            final JavadocComment javaDoc = methodDeclaration.getJavaDoc();

            if (javaDoc != null) {
                final String content = javaDoc.getContent();

                if (!content.toLowerCase().contains("@inheritdoc")) {
                    method.setDescription(content);
                    System.err.println(method.getOriginalName() + ": " + javaDoc.getContent());
                }
            }
        }
    }

    Function getFunction(final MethodDeclaration methodDeclaration) {
        final List<Function> mappedMethods = methodMapping.get(methodDeclaration.getName());
        Function method = null;

        if (mappedMethods != null) {
            for (final Function mappedMethod : mappedMethods) {

                if (parametersMatch(methodDeclaration.getParameters(), mappedMethod.getParameters())) {
                    method = mappedMethod;
                    break;
                }
            }
        }

        return method;
    }

    private boolean parametersMatch(final List<japa.parser.ast.body.Parameter> methodParameters,
        final Collection<Parameter> parameters) {
        if (methodParameters == null && parameters.isEmpty()) {
            return true;
        }

        if (methodParameters.size() != parameters.size()) {
            return false;
        }

        final Iterator<japa.parser.ast.body.Parameter> params = methodParameters.iterator();
        boolean matches = true;
        japa.parser.ast.body.Parameter parameter;

        for (final Parameter param : parameters) {
            parameter = params.next();

            final String className = classNameResolver.resolveClassName(parameter);
            final org.arachna.netweaver.nwdi.documenter.webservices.Type type = param.getType();

            if (!type.getName().equals(className)) {
                matches = false;
                break;
            }
        }

        return matches;

    }
}
