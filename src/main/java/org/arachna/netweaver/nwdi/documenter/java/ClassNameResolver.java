/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.java;

import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.expr.NameExpr;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Provider of absolute class names for a given list of imports.
 * 
 * @author Dirk Weigenand
 */
public class ClassNameResolver {
    /**
     * a class name ending with [] indicates an array.
     */
    private static final String BRACKETS = "[]";

    /**
     * Mapping for classes in java.lang.
     */
    private static final Map<String, String> JAVA_LANG = new HashMap<String, String>();

    // initialize mapping for classes in java.lang
    static {
        final Class classes[] =
            new Class[] { Appendable.class, CharSequence.class, Cloneable.class, Comparable.class, Iterable.class,
                Readable.class, Runnable.class, Thread.UncaughtExceptionHandler.class, Boolean.class, Byte.class,
                Character.class, Character.Subset.class, Character.UnicodeBlock.class, Class.class, ClassLoader.class,
                Compiler.class, Double.class, Enum.class, Float.class, InheritableThreadLocal.class, Integer.class,
                Long.class, Math.class, Number.class, Object.class, Package.class, Process.class, ProcessBuilder.class,
                Runtime.class, RuntimePermission.class, SecurityManager.class, Short.class, StackTraceElement.class,
                StrictMath.class, String.class, StringBuffer.class, StringBuilder.class, System.class, Thread.class,
                ThreadGroup.class, ThreadLocal.class, Throwable.class, Void.class, Thread.State.class,
                ArithmeticException.class, ArrayIndexOutOfBoundsException.class, ArrayStoreException.class,
                ClassCastException.class, ClassNotFoundException.class, CloneNotSupportedException.class,
                EnumConstantNotPresentException.class, Exception.class, IllegalAccessException.class,
                IllegalArgumentException.class, IllegalMonitorStateException.class, IllegalStateException.class,
                IllegalThreadStateException.class, IndexOutOfBoundsException.class, InstantiationException.class,
                InterruptedException.class, NegativeArraySizeException.class, NoSuchFieldException.class,
                NoSuchMethodException.class, NullPointerException.class, NumberFormatException.class,
                RuntimeException.class, SecurityException.class, StringIndexOutOfBoundsException.class,
                TypeNotPresentException.class, UnsupportedOperationException.class, AbstractMethodError.class,
                AssertionError.class, ClassCircularityError.class, ClassFormatError.class, Error.class,
                ExceptionInInitializerError.class, IllegalAccessError.class, IncompatibleClassChangeError.class,
                InstantiationError.class, InternalError.class, LinkageError.class, NoClassDefFoundError.class,
                NoSuchFieldError.class, NoSuchMethodError.class, OutOfMemoryError.class, StackOverflowError.class,
                ThreadDeath.class, UnknownError.class, UnsatisfiedLinkError.class, UnsupportedClassVersionError.class,
                VerifyError.class, VirtualMachineError.class, Deprecated.class, Override.class, SuppressWarnings.class, };

        for (final Class clazz : classes) {
            JAVA_LANG.put(clazz.getSimpleName(), clazz.getCanonicalName());
        }
    }

    /**
     * mapping from class name to a {@link NameExpr}.
     */
    private final Map<String, NameExpr> classNameMapping = new HashMap<String, NameExpr>();

    /**
     * Name of package this ClassNameResolver shall resolve class names for.
     */
    private final String packageName;

    /**
     * Create a <code>ClassNameResolver</code> using the given package name and
     * list of imports.
     * 
     * @param packageName
     *            package name for which to resolve class names.
     * @param imports
     *            list of imports of the class
     */
    public ClassNameResolver(final String packageName, final Collection<ImportDeclaration> imports) {
        this.packageName = packageName;

        if (imports != null) {
            for (final ImportDeclaration declaration : imports) {
                final NameExpr nameExpression = declaration.getName();
                classNameMapping.put(nameExpression.getName(), nameExpression);
            }
        }
    }

    /**
     * Determine the full class name (i.e. String --> java.lang.String) of the
     * given {@link Parameter} object.
     * 
     * @param parameter
     *            the Parameter to determine the class name for.
     * @return the class name resolved either from the <code>java.lang</code>
     *         package, from the imported classes or the current package.
     */
    public String resolveClassName(final Parameter parameter) {
        return resolveClassName(parameter.getType().toString());
    }

    /**
     * Determine the full class name (i.e. String --> java.lang.String) of the
     * given unqualified class name.
     * 
     * @param className
     *            unqualified class name to determine the fully qualified class
     *            name for.
     * @return the class name resolved either from the <code>java.lang</code>
     *         package, from the imported classes or the current package.
     */
    public String resolveClassName(final String className) {
        boolean isVarArgs = false;
        String resolvedClassName = className;

        if (className.endsWith(BRACKETS)) {
            resolvedClassName = className.substring(0, className.length() - 2);
            isVarArgs = true;
        }

        if (JAVA_LANG.containsKey(resolvedClassName)) {
            resolvedClassName = JAVA_LANG.get(resolvedClassName);
        }
        else if (classNameMapping.containsKey(resolvedClassName)) {
            resolvedClassName = classNameMapping.get(resolvedClassName).toString();
        }
        else {
            resolvedClassName = packageName + '.' + resolvedClassName;
        }

        return isVarArgs ? resolvedClassName + BRACKETS : resolvedClassName;
    }
}
