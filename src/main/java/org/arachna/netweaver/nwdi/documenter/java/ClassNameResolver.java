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
    private static final Map<String, String> JAVA_LANG = new HashMap<String, String>();

    static {
        JAVA_LANG.put(Appendable.class.getSimpleName(), Appendable.class.getCanonicalName());
        JAVA_LANG.put(CharSequence.class.getSimpleName(), CharSequence.class.getCanonicalName());
        JAVA_LANG.put(Cloneable.class.getSimpleName(), Cloneable.class.getCanonicalName());
        JAVA_LANG.put(Comparable.class.getSimpleName(), Comparable.class.getCanonicalName());
        JAVA_LANG.put(Iterable.class.getSimpleName(), Iterable.class.getCanonicalName());
        JAVA_LANG.put(Readable.class.getSimpleName(), Readable.class.getCanonicalName());
        JAVA_LANG.put(Runnable.class.getSimpleName(), Runnable.class.getCanonicalName());
        JAVA_LANG.put(Thread.UncaughtExceptionHandler.class.getSimpleName(),
            Thread.UncaughtExceptionHandler.class.getCanonicalName());
        JAVA_LANG.put(Boolean.class.getSimpleName(), Boolean.class.getCanonicalName());
        JAVA_LANG.put(Byte.class.getSimpleName(), Byte.class.getCanonicalName());
        JAVA_LANG.put(Character.class.getSimpleName(), Character.class.getCanonicalName());
        JAVA_LANG.put(Character.Subset.class.getSimpleName(), Character.Subset.class.getCanonicalName());
        JAVA_LANG.put(Character.UnicodeBlock.class.getSimpleName(), Character.UnicodeBlock.class.getCanonicalName());
        JAVA_LANG.put(Class.class.getSimpleName(), Class.class.getCanonicalName());
        JAVA_LANG.put(ClassLoader.class.getSimpleName(), ClassLoader.class.getCanonicalName());
        JAVA_LANG.put(Compiler.class.getSimpleName(), Compiler.class.getCanonicalName());
        JAVA_LANG.put(Double.class.getSimpleName(), Double.class.getCanonicalName());
        JAVA_LANG.put(Enum.class.getSimpleName(), Enum.class.getCanonicalName());
        JAVA_LANG.put(Float.class.getSimpleName(), Float.class.getCanonicalName());
        JAVA_LANG.put(InheritableThreadLocal.class.getSimpleName(), InheritableThreadLocal.class.getCanonicalName());
        JAVA_LANG.put(Integer.class.getSimpleName(), Integer.class.getCanonicalName());
        JAVA_LANG.put(Long.class.getSimpleName(), Long.class.getCanonicalName());
        JAVA_LANG.put(Math.class.getSimpleName(), Math.class.getCanonicalName());
        JAVA_LANG.put(Number.class.getSimpleName(), Number.class.getCanonicalName());
        JAVA_LANG.put(Object.class.getSimpleName(), Object.class.getCanonicalName());
        JAVA_LANG.put(Package.class.getSimpleName(), Package.class.getCanonicalName());
        JAVA_LANG.put(Process.class.getSimpleName(), Process.class.getCanonicalName());
        JAVA_LANG.put(ProcessBuilder.class.getSimpleName(), ProcessBuilder.class.getCanonicalName());
        JAVA_LANG.put(Runtime.class.getSimpleName(), Runtime.class.getCanonicalName());
        JAVA_LANG.put(RuntimePermission.class.getSimpleName(), RuntimePermission.class.getCanonicalName());
        JAVA_LANG.put(SecurityManager.class.getSimpleName(), SecurityManager.class.getCanonicalName());
        JAVA_LANG.put(Short.class.getSimpleName(), Short.class.getCanonicalName());
        JAVA_LANG.put(StackTraceElement.class.getSimpleName(), StackTraceElement.class.getCanonicalName());
        JAVA_LANG.put(StrictMath.class.getSimpleName(), StrictMath.class.getCanonicalName());
        JAVA_LANG.put(String.class.getSimpleName(), String.class.getCanonicalName());
        JAVA_LANG.put(StringBuffer.class.getSimpleName(), StringBuffer.class.getCanonicalName());
        JAVA_LANG.put(StringBuilder.class.getSimpleName(), StringBuilder.class.getCanonicalName());
        JAVA_LANG.put(System.class.getSimpleName(), System.class.getCanonicalName());
        JAVA_LANG.put(Thread.class.getSimpleName(), Thread.class.getCanonicalName());
        JAVA_LANG.put(ThreadGroup.class.getSimpleName(), ThreadGroup.class.getCanonicalName());
        JAVA_LANG.put(ThreadLocal.class.getSimpleName(), ThreadLocal.class.getCanonicalName());
        JAVA_LANG.put(Throwable.class.getSimpleName(), Throwable.class.getCanonicalName());
        JAVA_LANG.put(Void.class.getSimpleName(), Void.class.getCanonicalName());
        JAVA_LANG.put(Thread.State.class.getSimpleName(), Thread.State.class.getCanonicalName());
        JAVA_LANG.put(ArithmeticException.class.getSimpleName(), ArithmeticException.class.getCanonicalName());
        JAVA_LANG.put(ArrayIndexOutOfBoundsException.class.getSimpleName(),
            ArrayIndexOutOfBoundsException.class.getCanonicalName());
        JAVA_LANG.put(ArrayStoreException.class.getSimpleName(), ArrayStoreException.class.getCanonicalName());
        JAVA_LANG.put(ClassCastException.class.getSimpleName(), ClassCastException.class.getCanonicalName());
        JAVA_LANG.put(ClassNotFoundException.class.getSimpleName(), ClassNotFoundException.class.getCanonicalName());
        JAVA_LANG.put(CloneNotSupportedException.class.getSimpleName(),
            CloneNotSupportedException.class.getCanonicalName());
        JAVA_LANG.put(EnumConstantNotPresentException.class.getSimpleName(),
            EnumConstantNotPresentException.class.getCanonicalName());
        JAVA_LANG.put(Exception.class.getSimpleName(), Exception.class.getCanonicalName());
        JAVA_LANG.put(IllegalAccessException.class.getSimpleName(), IllegalAccessException.class.getCanonicalName());
        JAVA_LANG
            .put(IllegalArgumentException.class.getSimpleName(), IllegalArgumentException.class.getCanonicalName());
        JAVA_LANG.put(IllegalMonitorStateException.class.getSimpleName(),
            IllegalMonitorStateException.class.getCanonicalName());
        JAVA_LANG.put(IllegalStateException.class.getSimpleName(), IllegalStateException.class.getCanonicalName());
        JAVA_LANG.put(IllegalThreadStateException.class.getSimpleName(),
            IllegalThreadStateException.class.getCanonicalName());
        JAVA_LANG.put(IndexOutOfBoundsException.class.getSimpleName(),
            IndexOutOfBoundsException.class.getCanonicalName());
        JAVA_LANG.put(InstantiationException.class.getSimpleName(), InstantiationException.class.getCanonicalName());
        JAVA_LANG.put(InterruptedException.class.getSimpleName(), InterruptedException.class.getCanonicalName());
        JAVA_LANG.put(NegativeArraySizeException.class.getSimpleName(),
            NegativeArraySizeException.class.getCanonicalName());
        JAVA_LANG.put(NoSuchFieldException.class.getSimpleName(), NoSuchFieldException.class.getCanonicalName());
        JAVA_LANG.put(NoSuchMethodException.class.getSimpleName(), NoSuchMethodException.class.getCanonicalName());
        JAVA_LANG.put(NullPointerException.class.getSimpleName(), NullPointerException.class.getCanonicalName());
        JAVA_LANG.put(NumberFormatException.class.getSimpleName(), NumberFormatException.class.getCanonicalName());
        JAVA_LANG.put(RuntimeException.class.getSimpleName(), RuntimeException.class.getCanonicalName());
        JAVA_LANG.put(SecurityException.class.getSimpleName(), SecurityException.class.getCanonicalName());
        JAVA_LANG.put(StringIndexOutOfBoundsException.class.getSimpleName(),
            StringIndexOutOfBoundsException.class.getCanonicalName());
        JAVA_LANG.put(TypeNotPresentException.class.getSimpleName(), TypeNotPresentException.class.getCanonicalName());
        JAVA_LANG.put(UnsupportedOperationException.class.getSimpleName(),
            UnsupportedOperationException.class.getCanonicalName());
        JAVA_LANG.put(AbstractMethodError.class.getSimpleName(), AbstractMethodError.class.getCanonicalName());
        JAVA_LANG.put(AssertionError.class.getSimpleName(), AssertionError.class.getCanonicalName());
        JAVA_LANG.put(ClassCircularityError.class.getSimpleName(), ClassCircularityError.class.getCanonicalName());
        JAVA_LANG.put(ClassFormatError.class.getSimpleName(), ClassFormatError.class.getCanonicalName());
        JAVA_LANG.put(Error.class.getSimpleName(), Error.class.getCanonicalName());
        JAVA_LANG.put(ExceptionInInitializerError.class.getSimpleName(),
            ExceptionInInitializerError.class.getCanonicalName());
        JAVA_LANG.put(IllegalAccessError.class.getSimpleName(), IllegalAccessError.class.getCanonicalName());
        JAVA_LANG.put(IncompatibleClassChangeError.class.getSimpleName(),
            IncompatibleClassChangeError.class.getCanonicalName());
        JAVA_LANG.put(InstantiationError.class.getSimpleName(), InstantiationError.class.getCanonicalName());
        JAVA_LANG.put(InternalError.class.getSimpleName(), InternalError.class.getCanonicalName());
        JAVA_LANG.put(LinkageError.class.getSimpleName(), LinkageError.class.getCanonicalName());
        JAVA_LANG.put(NoClassDefFoundError.class.getSimpleName(), NoClassDefFoundError.class.getCanonicalName());
        JAVA_LANG.put(NoSuchFieldError.class.getSimpleName(), NoSuchFieldError.class.getCanonicalName());
        JAVA_LANG.put(NoSuchMethodError.class.getSimpleName(), NoSuchMethodError.class.getCanonicalName());
        JAVA_LANG.put(OutOfMemoryError.class.getSimpleName(), OutOfMemoryError.class.getCanonicalName());
        JAVA_LANG.put(StackOverflowError.class.getSimpleName(), StackOverflowError.class.getCanonicalName());
        JAVA_LANG.put(ThreadDeath.class.getSimpleName(), ThreadDeath.class.getCanonicalName());
        JAVA_LANG.put(UnknownError.class.getSimpleName(), UnknownError.class.getCanonicalName());
        JAVA_LANG.put(UnsatisfiedLinkError.class.getSimpleName(), UnsatisfiedLinkError.class.getCanonicalName());
        JAVA_LANG.put(UnsupportedClassVersionError.class.getSimpleName(),
            UnsupportedClassVersionError.class.getCanonicalName());
        JAVA_LANG.put(VerifyError.class.getSimpleName(), VerifyError.class.getCanonicalName());
        JAVA_LANG.put(VirtualMachineError.class.getSimpleName(), VirtualMachineError.class.getCanonicalName());
        JAVA_LANG.put(Deprecated.class.getSimpleName(), Deprecated.class.getCanonicalName());
        JAVA_LANG.put(Override.class.getSimpleName(), Override.class.getCanonicalName());
        JAVA_LANG.put(SuppressWarnings.class.getSimpleName(), SuppressWarnings.class.getCanonicalName());
    }

    /**
     * mapping from class name to a {@link NameExpr}.
     */
    private final Map<String, NameExpr> classNameMapping = new HashMap<String, NameExpr>();

    private final String packageName;

    /**
     * 
     * @param imports
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
     */
    public String resolveClassName(final Parameter parameter) {
        return resolveClassName(parameter.getType().toString());
    }

    /**
     * @param className
     * @return
     */
    public String resolveClassName(String className) {
        boolean isVarArgs = false;

        if (className.endsWith("[]")) {
            className = className.substring(0, className.length() - 2);
            isVarArgs = true;
        }

        if (JAVA_LANG.containsKey(className)) {
            className = JAVA_LANG.get(className);
        }
        else if (classNameMapping.containsKey(className)) {
            className = classNameMapping.get(className).toString();
        }
        else {
            className = packageName + '.' + className;
        }

        return isVarArgs ? className + "[]" : className;
    }
}
