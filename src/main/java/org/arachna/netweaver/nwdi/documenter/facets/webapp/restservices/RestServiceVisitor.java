/**
 *
 */
package org.arachna.netweaver.nwdi.documenter.facets.webapp.restservices;

import japa.parser.ast.Comment;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.expr.AnnotationExpr;
import japa.parser.ast.expr.MemberValuePair;
import japa.parser.ast.expr.NormalAnnotationExpr;
import japa.parser.ast.expr.SingleMemberAnnotationExpr;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.arachna.netweaver.nwdi.documenter.java.JavaDocCommentContainer;
import org.arachna.netweaver.nwdi.documenter.java.ParamTagDescriptor;

/**
 * VistorAdapter for extracting REST service descriptors from Java sources.
 *
 * @author Dirk Weigenand
 */
public class RestServiceVisitor extends VoidVisitorAdapter<RestService> {
    private final Set<String> httpMethods = new HashSet<String>(Arrays.asList("DELETE", "GET", "POST", "PUT"));

    @Override
    public void visit(final ClassOrInterfaceDeclaration classDef, final RestService service) {
        if (classDef.getAnnotations() == null) {
            return;
        }

        service.setName(classDef.getName());

        final Comment comment = classDef.getComment();

        if (comment != null) {
            final JavaDocCommentContainer content = new JavaDocCommentContainer(comment.getContent());
            service.setDescription(content.getDescription());
        }

        for (final AnnotationExpr annotation : classDef.getAnnotations()) {
            if ("Path".equals(annotation.getName().getName())) {
                final SingleMemberAnnotationExpr expr = (SingleMemberAnnotationExpr)annotation;
                service.setBasePath(expr.getMemberValue().toString().replaceAll("\"", ""));
                break;
            }
        }

        super.visit(classDef, service);
    }

    @Override
    public void visit(final MethodDeclaration methodDeclaration, final RestService service) {
        if (methodDeclaration.getAnnotations() == null) {
            return;
        }

        String httpRequestType = null;
        final Set<String> consumesMediaTypes = new HashSet<String>();
        final Set<String> producesMediaTypes = new HashSet<String>();
        String path = "";

        for (final AnnotationExpr annotation : methodDeclaration.getAnnotations()) {
            final String annotationName = annotation.getName().getName();

            if (isHttpRestMethod(annotationName)) {
                httpRequestType = annotationName;
            }
            else if ("Consumes".equals(annotationName)) {
                getAnnotationValues(consumesMediaTypes, annotation);
            }
            else if ("Produces".equals(annotationName)) {
                getAnnotationValues(producesMediaTypes, annotation);
            }
            else if ("Path".equals(annotationName)) {
                path = getSingleAnnotationValue(annotation);
            }
        }

        if (StringUtils.isNotEmpty(httpRequestType)) {
            final Method method = new Method(httpRequestType, path);
            method.addConsumedMediaTypes(consumesMediaTypes);
            method.addProducedMediaTypes(producesMediaTypes);

            if (methodDeclaration.getParameters() != null) {
                addParameters(methodDeclaration, method);
            }

            final Comment javaDoc = methodDeclaration.getComment();

            if (javaDoc != null) {
                final JavaDocCommentContainer content = new JavaDocCommentContainer(javaDoc.getContent());
                method.setDescription(content.getDescription());

                for (final ParamTagDescriptor descriptor : content.getParamTagDescriptors()) {
                    final Parameter parameter = method.getParameter(descriptor.getParameterName());

                    if (parameter != null) {
                        parameter.setDescription(descriptor.getDescription());
                    }
                }
            }

            service.add(method);
        }
    }

    /**
     * @param methodDeclaration
     * @param method
     */
    private void addParameters(final MethodDeclaration methodDeclaration, final Method method) {
        for (final japa.parser.ast.body.Parameter p : methodDeclaration.getParameters()) {
            final List<AnnotationExpr> annotations = p.getAnnotations();
            ParameterType type = ParameterType.Param;

            if (annotations != null) {
                for (final AnnotationExpr paramAnnotation : annotations) {
                    final String name = paramAnnotation.getName().getName();
                    if ("PathParam".equals(name)) {
                        type = ParameterType.PathParam;
                    }
                    else if ("QueryParam".equals(name)) {
                        type = ParameterType.QueryParam;
                    }
                }
            }

            method.add(new Parameter(type, p.getId().getName()));
        }
    }

    private String getSingleAnnotationValue(final AnnotationExpr annotation) {
        final SingleMemberAnnotationExpr expr = (SingleMemberAnnotationExpr)annotation;

        return StringUtils.trimToEmpty(expr.getMemberValue().toString()).replaceAll("\"", "");
    }

    /**
     * @param valuesHolder
     * @param annotation
     */
    private void getAnnotationValues(final Set<String> valuesHolder, final AnnotationExpr annotation) {
        if (annotation instanceof SingleMemberAnnotationExpr) {
            final SingleMemberAnnotationExpr expr = (SingleMemberAnnotationExpr)annotation;
            valuesHolder.add(getSingleAnnotationValue(expr));
        }
        else if (annotation instanceof NormalAnnotationExpr) {
            final NormalAnnotationExpr expr = (NormalAnnotationExpr)annotation;
            final List<MemberValuePair> pairs = expr.getPairs();

            if (pairs != null) {
                for (final MemberValuePair pair : pairs) {
                    valuesHolder.add(pair.getValue().toString().replaceAll("\"", ""));
                }
            }
        }
    }

    private boolean isHttpRestMethod(final String annotationName) {
        return httpMethods.contains(annotationName);
    }
}
