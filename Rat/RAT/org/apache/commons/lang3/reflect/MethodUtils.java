//Raddon On Top!

package org.apache.commons.lang3.reflect;

import org.apache.commons.lang3.*;
import java.util.stream.*;
import java.util.function.*;
import java.util.*;
import java.lang.reflect.*;
import java.lang.annotation.*;

public class MethodUtils
{
    private static final Comparator<Method> METHOD_BY_SIGNATURE;
    
    public static Object invokeMethod(final Object object, final String methodName) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return invokeMethod(object, methodName, ArrayUtils.EMPTY_OBJECT_ARRAY, null);
    }
    
    public static Object invokeMethod(final Object object, final boolean forceAccess, final String methodName) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return invokeMethod(object, forceAccess, methodName, ArrayUtils.EMPTY_OBJECT_ARRAY, null);
    }
    
    public static Object invokeMethod(final Object object, final String methodName, Object... args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        args = ArrayUtils.nullToEmpty(args);
        final Class<?>[] parameterTypes = (Class<?>[])ClassUtils.toClass(args);
        return invokeMethod(object, methodName, args, parameterTypes);
    }
    
    public static Object invokeMethod(final Object object, final boolean forceAccess, final String methodName, Object... args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        args = ArrayUtils.nullToEmpty(args);
        final Class<?>[] parameterTypes = (Class<?>[])ClassUtils.toClass(args);
        return invokeMethod(object, forceAccess, methodName, args, parameterTypes);
    }
    
    public static Object invokeMethod(final Object object, final boolean forceAccess, final String methodName, Object[] args, Class<?>[] parameterTypes) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        parameterTypes = (Class<?>[])ArrayUtils.nullToEmpty((Class[])parameterTypes);
        args = ArrayUtils.nullToEmpty(args);
        Method method = null;
        String messagePrefix;
        if (forceAccess) {
            messagePrefix = "No such method: ";
            method = getMatchingMethod(object.getClass(), methodName, parameterTypes);
            if (method != null && !method.isAccessible()) {
                method.setAccessible(true);
            }
        }
        else {
            messagePrefix = "No such accessible method: ";
            method = getMatchingAccessibleMethod(object.getClass(), methodName, parameterTypes);
        }
        if (method == null) {
            throw new NoSuchMethodException(messagePrefix + methodName + "() on object: " + object.getClass().getName());
        }
        args = toVarArgs(method, args);
        return method.invoke(object, args);
    }
    
    public static Object invokeMethod(final Object object, final String methodName, final Object[] args, final Class<?>[] parameterTypes) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return invokeMethod(object, false, methodName, args, parameterTypes);
    }
    
    public static Object invokeExactMethod(final Object object, final String methodName) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return invokeExactMethod(object, methodName, ArrayUtils.EMPTY_OBJECT_ARRAY, null);
    }
    
    public static Object invokeExactMethod(final Object object, final String methodName, Object... args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        args = ArrayUtils.nullToEmpty(args);
        final Class<?>[] parameterTypes = (Class<?>[])ClassUtils.toClass(args);
        return invokeExactMethod(object, methodName, args, parameterTypes);
    }
    
    public static Object invokeExactMethod(final Object object, final String methodName, Object[] args, Class<?>[] parameterTypes) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        args = ArrayUtils.nullToEmpty(args);
        parameterTypes = (Class<?>[])ArrayUtils.nullToEmpty((Class[])parameterTypes);
        final Method method = getAccessibleMethod(object.getClass(), methodName, parameterTypes);
        if (method == null) {
            throw new NoSuchMethodException("No such accessible method: " + methodName + "() on object: " + object.getClass().getName());
        }
        return method.invoke(object, args);
    }
    
    public static Object invokeExactStaticMethod(final Class<?> cls, final String methodName, Object[] args, Class<?>[] parameterTypes) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        args = ArrayUtils.nullToEmpty(args);
        parameterTypes = (Class<?>[])ArrayUtils.nullToEmpty((Class[])parameterTypes);
        final Method method = getAccessibleMethod(cls, methodName, parameterTypes);
        if (method == null) {
            throw new NoSuchMethodException("No such accessible method: " + methodName + "() on class: " + cls.getName());
        }
        return method.invoke(null, args);
    }
    
    public static Object invokeStaticMethod(final Class<?> cls, final String methodName, Object... args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        args = ArrayUtils.nullToEmpty(args);
        final Class<?>[] parameterTypes = (Class<?>[])ClassUtils.toClass(args);
        return invokeStaticMethod(cls, methodName, args, parameterTypes);
    }
    
    public static Object invokeStaticMethod(final Class<?> cls, final String methodName, Object[] args, Class<?>[] parameterTypes) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        args = ArrayUtils.nullToEmpty(args);
        parameterTypes = (Class<?>[])ArrayUtils.nullToEmpty((Class[])parameterTypes);
        final Method method = getMatchingAccessibleMethod(cls, methodName, parameterTypes);
        if (method == null) {
            throw new NoSuchMethodException("No such accessible method: " + methodName + "() on class: " + cls.getName());
        }
        args = toVarArgs(method, args);
        return method.invoke(null, args);
    }
    
    private static Object[] toVarArgs(final Method method, Object[] args) {
        if (method.isVarArgs()) {
            final Class<?>[] methodParameterTypes = method.getParameterTypes();
            args = getVarArgs(args, methodParameterTypes);
        }
        return args;
    }
    
    static Object[] getVarArgs(final Object[] args, final Class<?>[] methodParameterTypes) {
        if (args.length == methodParameterTypes.length && (args[args.length - 1] == null || args[args.length - 1].getClass().equals(methodParameterTypes[methodParameterTypes.length - 1]))) {
            return args;
        }
        final Object[] newArgs = new Object[methodParameterTypes.length];
        System.arraycopy(args, 0, newArgs, 0, methodParameterTypes.length - 1);
        final Class<?> varArgComponentType = methodParameterTypes[methodParameterTypes.length - 1].getComponentType();
        final int varArgLength = args.length - methodParameterTypes.length + 1;
        Object varArgsArray = Array.newInstance(ClassUtils.primitiveToWrapper((Class)varArgComponentType), varArgLength);
        System.arraycopy(args, methodParameterTypes.length - 1, varArgsArray, 0, varArgLength);
        if (varArgComponentType.isPrimitive()) {
            varArgsArray = ArrayUtils.toPrimitive(varArgsArray);
        }
        newArgs[methodParameterTypes.length - 1] = varArgsArray;
        return newArgs;
    }
    
    public static Object invokeExactStaticMethod(final Class<?> cls, final String methodName, Object... args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        args = ArrayUtils.nullToEmpty(args);
        final Class<?>[] parameterTypes = (Class<?>[])ClassUtils.toClass(args);
        return invokeExactStaticMethod(cls, methodName, args, parameterTypes);
    }
    
    public static Method getAccessibleMethod(final Class<?> cls, final String methodName, final Class<?>... parameterTypes) {
        try {
            return getAccessibleMethod(cls.getMethod(methodName, parameterTypes));
        }
        catch (NoSuchMethodException e) {
            return null;
        }
    }
    
    public static Method getAccessibleMethod(Method method) {
        if (!MemberUtils.isAccessible((Member)method)) {
            return null;
        }
        final Class<?> cls = method.getDeclaringClass();
        if (Modifier.isPublic(cls.getModifiers())) {
            return method;
        }
        final String methodName = method.getName();
        final Class<?>[] parameterTypes = method.getParameterTypes();
        method = getAccessibleMethodFromInterfaceNest(cls, methodName, parameterTypes);
        if (method == null) {
            method = getAccessibleMethodFromSuperclass(cls, methodName, parameterTypes);
        }
        return method;
    }
    
    private static Method getAccessibleMethodFromSuperclass(final Class<?> cls, final String methodName, final Class<?>... parameterTypes) {
        for (Class<?> parentClass = cls.getSuperclass(); parentClass != null; parentClass = parentClass.getSuperclass()) {
            if (Modifier.isPublic(parentClass.getModifiers())) {
                try {
                    return parentClass.getMethod(methodName, parameterTypes);
                }
                catch (NoSuchMethodException e) {
                    return null;
                }
            }
        }
        return null;
    }
    
    private static Method getAccessibleMethodFromInterfaceNest(Class<?> cls, final String methodName, final Class<?>... parameterTypes) {
        while (cls != null) {
            final Class<?>[] interfaces2;
            final Class<?>[] interfaces = interfaces2 = cls.getInterfaces();
            for (final Class<?> anInterface : interfaces2) {
                if (Modifier.isPublic(anInterface.getModifiers())) {
                    try {
                        return anInterface.getDeclaredMethod(methodName, parameterTypes);
                    }
                    catch (NoSuchMethodException ex) {
                        final Method method = getAccessibleMethodFromInterfaceNest(anInterface, methodName, parameterTypes);
                        if (method != null) {
                            return method;
                        }
                    }
                }
            }
            cls = cls.getSuperclass();
        }
        return null;
    }
    
    public static Method getMatchingAccessibleMethod(final Class<?> cls, final String methodName, final Class<?>... parameterTypes) {
        try {
            final Method method = cls.getMethod(methodName, parameterTypes);
            MemberUtils.setAccessibleWorkaround((AccessibleObject)method);
            return method;
        }
        catch (NoSuchMethodException ex) {
            final Method[] methods = cls.getMethods();
            final List<Method> matchingMethods = new ArrayList<Method>();
            for (final Method method2 : methods) {
                if (method2.getName().equals(methodName) && MemberUtils.isMatchingMethod(method2, (Class[])parameterTypes)) {
                    matchingMethods.add(method2);
                }
            }
            matchingMethods.sort(MethodUtils.METHOD_BY_SIGNATURE);
            Method bestMatch = null;
            for (final Method method3 : matchingMethods) {
                final Method accessibleMethod = getAccessibleMethod(method3);
                if (accessibleMethod != null && (bestMatch == null || MemberUtils.compareMethodFit(accessibleMethod, bestMatch, (Class[])parameterTypes) < 0)) {
                    bestMatch = accessibleMethod;
                }
            }
            if (bestMatch != null) {
                MemberUtils.setAccessibleWorkaround((AccessibleObject)bestMatch);
            }
            if (bestMatch != null && bestMatch.isVarArgs() && bestMatch.getParameterTypes().length > 0 && parameterTypes.length > 0) {
                final Class<?>[] methodParameterTypes = bestMatch.getParameterTypes();
                final Class<?> methodParameterComponentType = methodParameterTypes[methodParameterTypes.length - 1].getComponentType();
                final String methodParameterComponentTypeName = ClassUtils.primitiveToWrapper((Class)methodParameterComponentType).getName();
                final Class<?> lastParameterType = parameterTypes[parameterTypes.length - 1];
                final String parameterTypeName = (lastParameterType == null) ? null : lastParameterType.getName();
                final String parameterTypeSuperClassName = (lastParameterType == null) ? null : lastParameterType.getSuperclass().getName();
                if (parameterTypeName != null && parameterTypeSuperClassName != null && !methodParameterComponentTypeName.equals(parameterTypeName) && !methodParameterComponentTypeName.equals(parameterTypeSuperClassName)) {
                    return null;
                }
            }
            return bestMatch;
        }
    }
    
    public static Method getMatchingMethod(final Class<?> cls, final String methodName, final Class<?>... parameterTypes) {
        Validate.notNull(cls, "cls", new Object[0]);
        Validate.notEmpty(methodName, "methodName", new Object[0]);
        final List<Method> methods = Arrays.stream(cls.getDeclaredMethods()).filter(method -> method.getName().equals(methodName)).collect((Collector<? super Method, ?, List<Method>>)Collectors.toList());
        ClassUtils.getAllSuperclasses((Class)cls).stream().map(Class::getDeclaredMethods).flatMap(Arrays::stream).filter(method -> method.getName().equals(methodName)).forEach(methods::add);
        for (final Method method2 : methods) {
            if (Arrays.deepEquals(method2.getParameterTypes(), parameterTypes)) {
                return method2;
            }
        }
        final TreeMap<Integer, List<Method>> candidates = new TreeMap<Integer, List<Method>>();
        final int distance;
        final Map<Integer, List<Method>> map;
        final List<Method> candidatesAtDistance;
        methods.stream().filter(method -> ClassUtils.isAssignable((Class[])parameterTypes, (Class[])method.getParameterTypes(), true)).forEach(method -> {
            distance = distance(parameterTypes, method.getParameterTypes());
            candidatesAtDistance = map.computeIfAbsent(distance, k -> new ArrayList());
            candidatesAtDistance.add(method);
            return;
        });
        if (candidates.isEmpty()) {
            return null;
        }
        final List<Method> bestCandidates = candidates.values().iterator().next();
        if (bestCandidates.size() == 1) {
            return bestCandidates.get(0);
        }
        throw new IllegalStateException(String.format("Found multiple candidates for method %s on class %s : %s", methodName + Arrays.stream(parameterTypes).map((Function<? super Class<?>, ?>)String::valueOf).collect((Collector<? super Object, ?, String>)Collectors.joining(",", "(", ")")), cls.getName(), bestCandidates.stream().map((Function<? super Object, ?>)Method::toString).collect((Collector<? super Object, ?, String>)Collectors.joining(",", "[", "]"))));
    }
    
    private static int distance(final Class<?>[] fromClassArray, final Class<?>[] toClassArray) {
        int answer = 0;
        if (!ClassUtils.isAssignable((Class[])fromClassArray, (Class[])toClassArray, true)) {
            return -1;
        }
        for (int offset = 0; offset < fromClassArray.length; ++offset) {
            final Class<?> aClass = fromClassArray[offset];
            final Class<?> toClass = toClassArray[offset];
            if (aClass != null) {
                if (!aClass.equals(toClass)) {
                    if (ClassUtils.isAssignable((Class)aClass, (Class)toClass, true) && !ClassUtils.isAssignable((Class)aClass, (Class)toClass, false)) {
                        ++answer;
                    }
                    else {
                        answer += 2;
                    }
                }
            }
        }
        return answer;
    }
    
    public static Set<Method> getOverrideHierarchy(final Method method, final ClassUtils.Interfaces interfacesBehavior) {
        Validate.notNull(method);
        final Set<Method> result = new LinkedHashSet<Method>();
        result.add(method);
        final Class<?>[] parameterTypes = method.getParameterTypes();
        final Class<?> declaringClass = method.getDeclaringClass();
        final Iterator<Class<?>> hierarchy = ClassUtils.hierarchy((Class)declaringClass, interfacesBehavior).iterator();
        hierarchy.next();
    Label_0053:
        while (hierarchy.hasNext()) {
            final Class<?> c = hierarchy.next();
            final Method m = getMatchingAccessibleMethod(c, method.getName(), parameterTypes);
            if (m == null) {
                continue;
            }
            if (Arrays.equals(m.getParameterTypes(), parameterTypes)) {
                result.add(m);
            }
            else {
                final Map<TypeVariable<?>, Type> typeArguments = TypeUtils.getTypeArguments(declaringClass, m.getDeclaringClass());
                for (int i = 0; i < parameterTypes.length; ++i) {
                    final Type childType = TypeUtils.unrollVariables(typeArguments, method.getGenericParameterTypes()[i]);
                    final Type parentType = TypeUtils.unrollVariables(typeArguments, m.getGenericParameterTypes()[i]);
                    if (!TypeUtils.equals(childType, parentType)) {
                        continue Label_0053;
                    }
                }
                result.add(m);
            }
        }
        return result;
    }
    
    public static Method[] getMethodsWithAnnotation(final Class<?> cls, final Class<? extends Annotation> annotationCls) {
        return getMethodsWithAnnotation(cls, annotationCls, false, false);
    }
    
    public static List<Method> getMethodsListWithAnnotation(final Class<?> cls, final Class<? extends Annotation> annotationCls) {
        return getMethodsListWithAnnotation(cls, annotationCls, false, false);
    }
    
    public static Method[] getMethodsWithAnnotation(final Class<?> cls, final Class<? extends Annotation> annotationCls, final boolean searchSupers, final boolean ignoreAccess) {
        final List<Method> annotatedMethodsList = getMethodsListWithAnnotation(cls, annotationCls, searchSupers, ignoreAccess);
        return annotatedMethodsList.toArray(ArrayUtils.EMPTY_METHOD_ARRAY);
    }
    
    public static List<Method> getMethodsListWithAnnotation(final Class<?> cls, final Class<? extends Annotation> annotationCls, final boolean searchSupers, final boolean ignoreAccess) {
        Validate.notNull(cls, "cls", new Object[0]);
        Validate.notNull(annotationCls, "annotationCls", new Object[0]);
        final List<Class<?>> classes = searchSupers ? getAllSuperclassesAndInterfaces(cls) : new ArrayList<Class<?>>();
        classes.add(0, cls);
        final List<Method> annotatedMethods = new ArrayList<Method>();
        for (final Class<?> acls : classes) {
            final Method[] array;
            final Method[] methods = array = (ignoreAccess ? acls.getDeclaredMethods() : acls.getMethods());
            for (final Method method : array) {
                if (method.getAnnotation(annotationCls) != null) {
                    annotatedMethods.add(method);
                }
            }
        }
        return annotatedMethods;
    }
    
    public static <A extends Annotation> A getAnnotation(final Method method, final Class<A> annotationCls, final boolean searchSupers, final boolean ignoreAccess) {
        Validate.notNull(method, "method", new Object[0]);
        Validate.notNull(annotationCls, "annotationCls", new Object[0]);
        if (!ignoreAccess && !MemberUtils.isAccessible((Member)method)) {
            return null;
        }
        A annotation = method.getAnnotation(annotationCls);
        if (annotation == null && searchSupers) {
            final Class<?> mcls = method.getDeclaringClass();
            final List<Class<?>> classes = getAllSuperclassesAndInterfaces(mcls);
            for (final Class<?> acls : classes) {
                final Method equivalentMethod = ignoreAccess ? getMatchingMethod(acls, method.getName(), method.getParameterTypes()) : getMatchingAccessibleMethod(acls, method.getName(), method.getParameterTypes());
                if (equivalentMethod != null) {
                    annotation = equivalentMethod.getAnnotation(annotationCls);
                    if (annotation != null) {
                        break;
                    }
                    continue;
                }
            }
        }
        return annotation;
    }
    
    private static List<Class<?>> getAllSuperclassesAndInterfaces(final Class<?> cls) {
        if (cls == null) {
            return null;
        }
        final List<Class<?>> allSuperClassesAndInterfaces = new ArrayList<Class<?>>();
        final List<Class<?>> allSuperclasses = (List<Class<?>>)ClassUtils.getAllSuperclasses((Class)cls);
        int superClassIndex = 0;
        final List<Class<?>> allInterfaces = (List<Class<?>>)ClassUtils.getAllInterfaces((Class)cls);
        int interfaceIndex = 0;
        while (interfaceIndex < allInterfaces.size() || superClassIndex < allSuperclasses.size()) {
            Class<?> acls;
            if (interfaceIndex >= allInterfaces.size()) {
                acls = allSuperclasses.get(superClassIndex++);
            }
            else if (superClassIndex >= allSuperclasses.size() || interfaceIndex < superClassIndex || superClassIndex >= interfaceIndex) {
                acls = allInterfaces.get(interfaceIndex++);
            }
            else {
                acls = allSuperclasses.get(superClassIndex++);
            }
            allSuperClassesAndInterfaces.add(acls);
        }
        return allSuperClassesAndInterfaces;
    }
    
    static {
        METHOD_BY_SIGNATURE = Comparator.comparing((Function<? super Method, ? extends Comparable>)Method::toString);
    }
}
