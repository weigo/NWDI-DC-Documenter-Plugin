/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.java;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.TypeDeclaration;
import japa.parser.ast.type.ClassOrInterfaceType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.arachna.netweaver.nwdi.documenter.webservices.Function;
import org.arachna.netweaver.nwdi.documenter.webservices.VirtualInterfaceDefinition;

/**
 * Provider for JavaDoc-Documentation for methods.
 * 
 * @author Dirk Weigenand
 */
public class MethodDocumentationProvider {
    /**
     * Encoding to use reading java sources.
     */
    private final String encoding;

    /**
     * Create a new instance of <code></code> using the given encoding.
     * 
     * @param encoding
     *            Encoding to use reading java sources.
     */
    public MethodDocumentationProvider(final String encoding) {
        this.encoding = encoding;
    }

    /**
     * Reads end point class and updates the given virtual interface methods
     * with JavaDoc comments from the respective method comments.
     * 
     * @param sourceFolders
     *            list of source folders containing the end point of the virutal
     *            interface
     * @param virtualInterface
     *            the virtual interface containing the methods whose
     *            descriptions should be updated from the JavaDoc method
     *            comments
     */
    public void execute(final List<String> sourceFolders, final VirtualInterfaceDefinition virtualInterface) {
        try {
            final String endPointClass = virtualInterface.getEndPointClass();
            final MethodDescriptionUpdateResult result =
                updateMethodDescriptions(sourceFolders, virtualInterface, endPointClass);

            updateMethodDescriptionsFromClassesOrInterfaces(sourceFolders, virtualInterface, result);
        }
        catch (final FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (final ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * @param sourceFolders
     * @param virtualInterface
     * @param result
     * @throws ParseException
     * @throws FileNotFoundException
     */
    protected void updateMethodDescriptionsFromClassesOrInterfaces(final List<String> sourceFolders,
        final VirtualInterfaceDefinition virtualInterface, final MethodDescriptionUpdateResult result)
        throws ParseException, FileNotFoundException {
        for (final TypeDeclaration type : result.getCompilationUnit().getTypes()) {
            final ClassOrInterfaceDeclaration classOrInterface = (ClassOrInterfaceDeclaration)type;

            updateMethodDescriptionsFromClassOrInterface(sourceFolders, virtualInterface,
                result.getClassNameResolver(), classOrInterface.getImplements());
            updateMethodDescriptionsFromClassOrInterface(sourceFolders, virtualInterface,
                result.getClassNameResolver(), classOrInterface.getExtends());
        }
    }

    /**
     * @param sourceFolders
     * @param virtualInterface
     * @param result
     * @param classOrInterface
     * @throws ParseException
     * @throws FileNotFoundException
     */
    protected void updateMethodDescriptionsFromClassOrInterface(final List<String> sourceFolders,
        final VirtualInterfaceDefinition virtualInterface, final ClassNameResolver classNameResolver,
        final List<ClassOrInterfaceType> classesOrInterfaces) throws ParseException, FileNotFoundException {
        if (classesOrInterfaces != null) {
            for (final ClassOrInterfaceType t : classesOrInterfaces) {
                final MethodDescriptionUpdateResult result =
                    updateMethodDescriptions(sourceFolders, virtualInterface,
                        classNameResolver.resolveClassName(t.getName()));

                updateMethodDescriptionsFromClassesOrInterfaces(sourceFolders, virtualInterface, result);
            }
        }
    }

    /**
     * @param sourceFolders
     * @param virtualInterface
     * @param endPointClass
     * @throws ParseException
     * @throws FileNotFoundException
     */
    protected MethodDescriptionUpdateResult updateMethodDescriptions(final List<String> sourceFolders,
        final VirtualInterfaceDefinition virtualInterface, final String endPointClass) throws ParseException,
        FileNotFoundException {
        System.err.println("Update description for " + endPointClass);
        final CompilationUnit compilationUnit =
            JavaParser.parse(getEndPointSource(sourceFolders, endPointClass), encoding);
        final ClassNameResolver classNameResolver =
            new ClassNameResolver(virtualInterface.getEndPointClass().substring(0,
                virtualInterface.getEndPointClass().lastIndexOf('.')), compilationUnit.getImports());

        final JavaDocMethodCommentExtractingVisitor visitor =
            new JavaDocMethodCommentExtractingVisitor(classNameResolver,
                getMethodsWithoutDescriptions(virtualInterface));
        compilationUnit.accept(visitor, null);

        return new MethodDescriptionUpdateResult(compilationUnit, classNameResolver);
    }

    private class MethodDescriptionUpdateResult {
        private final CompilationUnit compilationUnit;
        private final ClassNameResolver classNameResolver;

        MethodDescriptionUpdateResult(final CompilationUnit compilationUnit, final ClassNameResolver classNameResolver) {
            this.compilationUnit = compilationUnit;
            this.classNameResolver = classNameResolver;
        }

        /**
         * @return the compilationUnit
         */
        public CompilationUnit getCompilationUnit() {
            return compilationUnit;
        }

        /**
         * @return the classNameResolver
         */
        public ClassNameResolver getClassNameResolver() {
            return classNameResolver;
        }
    }

    /**
     * @param virtualInterface
     * @return
     */
    protected Collection<Function> getMethodsWithoutDescriptions(final VirtualInterfaceDefinition virtualInterface) {
        final List<Function> methods = new LinkedList<Function>();

        for (final Function method : virtualInterface.getMethods()) {
            if (method.getDescription().isEmpty()) {
                methods.add(method);
            }
        }

        return methods;
    }

    private InputStream getEndPointSource(final List<String> sourceFolders, final String endPointClassName)
        throws FileNotFoundException {
        InputStream input = null;
        final String fileName = endPointClassName.replace('.', '/') + ".java";

        for (final String folder : sourceFolders) {
            final File file = new File(folder, fileName);

            if (file.exists()) {
                input = new FileInputStream(file.getAbsolutePath());
                break;
            }
        }

        return input;
    }
}
