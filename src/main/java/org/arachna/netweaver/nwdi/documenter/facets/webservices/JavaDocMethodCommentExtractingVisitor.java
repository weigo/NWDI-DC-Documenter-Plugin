/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.facets.webservices;

import japa.parser.ast.body.JavadocComment;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.arachna.netweaver.nwdi.documenter.java.ClassNameResolver;
import org.arachna.netweaver.nwdi.documenter.java.JavaDocCommentContainer;
import org.arachna.netweaver.nwdi.documenter.java.TagDescriptor;

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

    /**
     * regular expression for parameters.
     */
    private final Pattern parameterPattern = Pattern.compile("(\\w+)\\s+(.*)");

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
                updateDescriptions(method, javaDoc);
            }
        }
    }

    /**
     * @param method
     * @param javaDoc
     */
    private void updateDescriptions(final Function method, final JavadocComment javaDoc) {
        final JavaDocCommentContainer content = new JavaDocCommentContainer(javaDoc.getContent());

        if (content.getTagDescriptors("@inheritdoc").isEmpty()) {
            method.setDescription(content.getDescription());
            final Iterator<Parameter> parameters = method.getParameters().iterator();

            for (final TagDescriptor descriptor : content.getTagDescriptors("@param")) {
                if (parameters.hasNext()) {
                    final Matcher matcher = parameterPattern.matcher(descriptor.getDescription());

                    if (matcher.matches()) {
                        parameters.next().setDescription(matcher.group(2));
                    }
                }
            }
        }
    }

    private Function getFunction(final MethodDeclaration methodDeclaration) {
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

    /**
     * Determine whether the parameter list from the parsed java source matches
     * that of a web service method.
     * 
     * @param methodParameters
     *            parameters of method from java source
     * @param parameters
     *            parameters from web service method
     * @return <code>true</code> when the parameters match by type or the given
     *         list of method parameters is <code>null</code> and the web
     *         service parameters are empty, <code>false</code> otherwise.
     */
    private boolean parametersMatch(final List<japa.parser.ast.body.Parameter> methodParameters,
        final Collection<Parameter> parameters) {
        if (methodParameters == null && parameters.isEmpty()) {
            return true;
        }

        boolean matches = methodParameters.size() == parameters.size();

        if (matches) {
            final Iterator<japa.parser.ast.body.Parameter> params = methodParameters.iterator();

            japa.parser.ast.body.Parameter parameter;

            for (final Parameter param : parameters) {
                parameter = params.next();

                final String className = classNameResolver.resolveClassName(parameter);
                final org.arachna.netweaver.nwdi.documenter.facets.webservices.Type type = param.getType();

                if (!type.getName().equals(className)) {
                    matches = false;
                    break;
                }
            }
        }

        return matches;
    }
}
