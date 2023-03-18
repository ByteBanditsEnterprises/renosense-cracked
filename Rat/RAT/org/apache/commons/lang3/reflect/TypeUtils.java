//Raddon On Top!

package org.apache.commons.lang3.reflect;

import java.lang.reflect.*;
import org.apache.commons.lang3.*;
import java.util.*;
import org.apache.commons.lang3.builder.*;

public class TypeUtils
{
    public static final WildcardType WILDCARD_ALL;
    
    private static <T> StringBuilder appendAllTo(final StringBuilder builder, final String sep, final T... types) {
        Validate.notEmpty(Validate.noNullElements(types));
        if (types.length > 0) {
            builder.append(toString(types[0]));
            for (int i = 1; i < types.length; ++i) {
                builder.append(sep).append(toString(types[i]));
            }
        }
        return builder;
    }
    
    private static void appendRecursiveTypes(final StringBuilder builder, final int[] recursiveTypeIndexes, final Type[] argumentTypes) {
        for (int i = 0; i < recursiveTypeIndexes.length; ++i) {
            appendAllTo(builder.append('<'), ", ", argumentTypes[i].toString()).append('>');
        }
        final Type[] argumentsFiltered = (Type[])ArrayUtils.removeAll((Object[])argumentTypes, recursiveTypeIndexes);
        if (argumentsFiltered.length > 0) {
            appendAllTo(builder.append('<'), ", ", argumentsFiltered).append('>');
        }
    }
    
    private static String classToString(final Class<?> cls) {
        if (cls.isArray()) {
            return toString((Type)cls.getComponentType()) + "[]";
        }
        final StringBuilder buf = new StringBuilder();
        if (cls.getEnclosingClass() != null) {
            buf.append(classToString(cls.getEnclosingClass())).append('.').append(cls.getSimpleName());
        }
        else {
            buf.append(cls.getName());
        }
        if (cls.getTypeParameters().length > 0) {
            buf.append('<');
            appendAllTo(buf, ", ", cls.getTypeParameters());
            buf.append('>');
        }
        return buf.toString();
    }
    
    public static boolean containsTypeVariables(final Type type) {
        if (type instanceof TypeVariable) {
            return true;
        }
        if (type instanceof Class) {
            return ((Class)type).getTypeParameters().length > 0;
        }
        if (type instanceof ParameterizedType) {
            for (final Type arg : ((ParameterizedType)type).getActualTypeArguments()) {
                if (containsTypeVariables(arg)) {
                    return true;
                }
            }
            return false;
        }
        if (type instanceof WildcardType) {
            final WildcardType wild = (WildcardType)type;
            return containsTypeVariables(getImplicitLowerBounds(wild)[0]) || containsTypeVariables(getImplicitUpperBounds(wild)[0]);
        }
        return type instanceof GenericArrayType && containsTypeVariables(((GenericArrayType)type).getGenericComponentType());
    }
    
    private static boolean containsVariableTypeSameParametrizedTypeBound(final TypeVariable<?> typeVariable, final ParameterizedType parameterizedType) {
        return ArrayUtils.contains((Object[])typeVariable.getBounds(), (Object)parameterizedType);
    }
    
    public static Map<TypeVariable<?>, Type> determineTypeArguments(final Class<?> cls, final ParameterizedType superParameterizedType) {
        Validate.notNull(cls, "cls", new Object[0]);
        Validate.notNull(superParameterizedType, "superParameterizedType", new Object[0]);
        final Class<?> superClass = getRawType(superParameterizedType);
        if (!isAssignable(cls, superClass)) {
            return null;
        }
        if (cls.equals(superClass)) {
            return getTypeArguments(superParameterizedType, superClass, null);
        }
        final Type midType = getClosestParentType(cls, superClass);
        if (midType instanceof Class) {
            return determineTypeArguments((Class<?>)midType, superParameterizedType);
        }
        final ParameterizedType midParameterizedType = (ParameterizedType)midType;
        final Class<?> midClass = getRawType(midParameterizedType);
        final Map<TypeVariable<?>, Type> typeVarAssigns = determineTypeArguments(midClass, superParameterizedType);
        mapTypeVariablesToArguments(cls, midParameterizedType, typeVarAssigns);
        return typeVarAssigns;
    }
    
    private static boolean equals(final GenericArrayType genericArrayType, final Type type) {
        return type instanceof GenericArrayType && equals(genericArrayType.getGenericComponentType(), ((GenericArrayType)type).getGenericComponentType());
    }
    
    private static boolean equals(final ParameterizedType parameterizedType, final Type type) {
        if (type instanceof ParameterizedType) {
            final ParameterizedType other = (ParameterizedType)type;
            if (equals(parameterizedType.getRawType(), other.getRawType()) && equals(parameterizedType.getOwnerType(), other.getOwnerType())) {
                return equals(parameterizedType.getActualTypeArguments(), other.getActualTypeArguments());
            }
        }
        return false;
    }
    
    public static boolean equals(final Type type1, final Type type2) {
        if (Objects.equals(type1, type2)) {
            return true;
        }
        if (type1 instanceof ParameterizedType) {
            return equals((ParameterizedType)type1, type2);
        }
        if (type1 instanceof GenericArrayType) {
            return equals((GenericArrayType)type1, type2);
        }
        return type1 instanceof WildcardType && equals((WildcardType)type1, type2);
    }
    
    private static boolean equals(final Type[] type1, final Type[] type2) {
        if (type1.length == type2.length) {
            for (int i = 0; i < type1.length; ++i) {
                if (!equals(type1[i], type2[i])) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    private static boolean equals(final WildcardType wildcardType, final Type type) {
        if (type instanceof WildcardType) {
            final WildcardType other = (WildcardType)type;
            return equals(getImplicitLowerBounds(wildcardType), getImplicitLowerBounds(other)) && equals(getImplicitUpperBounds(wildcardType), getImplicitUpperBounds(other));
        }
        return false;
    }
    
    private static Type[] extractTypeArgumentsFrom(final Map<TypeVariable<?>, Type> mappings, final TypeVariable<?>[] variables) {
        final Type[] result = new Type[variables.length];
        int index = 0;
        for (final TypeVariable<?> var : variables) {
            Validate.isTrue(mappings.containsKey(var), "missing argument mapping for %s", toString((Type)var));
            result[index++] = mappings.get(var);
        }
        return result;
    }
    
    private static int[] findRecursiveTypes(final ParameterizedType parameterizedType) {
        final Type[] filteredArgumentTypes = Arrays.copyOf(parameterizedType.getActualTypeArguments(), parameterizedType.getActualTypeArguments().length);
        int[] indexesToRemove = new int[0];
        for (int i = 0; i < filteredArgumentTypes.length; ++i) {
            if (filteredArgumentTypes[i] instanceof TypeVariable && containsVariableTypeSameParametrizedTypeBound((TypeVariable<?>)filteredArgumentTypes[i], parameterizedType)) {
                indexesToRemove = ArrayUtils.add(indexesToRemove, i);
            }
        }
        return indexesToRemove;
    }
    
    public static GenericArrayType genericArrayType(final Type componentType) {
        return new GenericArrayTypeImpl((Type)Validate.notNull(componentType, "componentType", new Object[0]));
    }
    
    private static String genericArrayTypeToString(final GenericArrayType genericArrayType) {
        return String.format("%s[]", toString(genericArrayType.getGenericComponentType()));
    }
    
    public static Type getArrayComponentType(final Type type) {
        if (type instanceof Class) {
            final Class<?> cls = (Class<?>)type;
            return cls.isArray() ? cls.getComponentType() : null;
        }
        if (type instanceof GenericArrayType) {
            return ((GenericArrayType)type).getGenericComponentType();
        }
        return null;
    }
    
    private static Type getClosestParentType(final Class<?> cls, final Class<?> superClass) {
        if (superClass.isInterface()) {
            final Type[] interfaceTypes = cls.getGenericInterfaces();
            Type genericInterface = null;
            for (final Type midType : interfaceTypes) {
                Class<?> midClass = null;
                if (midType instanceof ParameterizedType) {
                    midClass = getRawType((ParameterizedType)midType);
                }
                else {
                    if (!(midType instanceof Class)) {
                        throw new IllegalStateException("Unexpected generic interface type found: " + midType);
                    }
                    midClass = (Class<?>)midType;
                }
                if (isAssignable(midClass, superClass) && isAssignable(genericInterface, (Type)midClass)) {
                    genericInterface = midType;
                }
            }
            if (genericInterface != null) {
                return genericInterface;
            }
        }
        return cls.getGenericSuperclass();
    }
    
    public static Type[] getImplicitBounds(final TypeVariable<?> typeVariable) {
        Validate.notNull(typeVariable, "typeVariable", new Object[0]);
        final Type[] bounds = typeVariable.getBounds();
        return (bounds.length == 0) ? new Type[] { Object.class } : normalizeUpperBounds(bounds);
    }
    
    public static Type[] getImplicitLowerBounds(final WildcardType wildcardType) {
        Validate.notNull(wildcardType, "wildcardType", new Object[0]);
        final Type[] bounds = wildcardType.getLowerBounds();
        return (bounds.length == 0) ? new Type[] { null } : bounds;
    }
    
    public static Type[] getImplicitUpperBounds(final WildcardType wildcardType) {
        Validate.notNull(wildcardType, "wildcardType", new Object[0]);
        final Type[] bounds = wildcardType.getUpperBounds();
        return (bounds.length == 0) ? new Type[] { Object.class } : normalizeUpperBounds(bounds);
    }
    
    private static Class<?> getRawType(final ParameterizedType parameterizedType) {
        final Type rawType = parameterizedType.getRawType();
        if (!(rawType instanceof Class)) {
            throw new IllegalStateException("Wait... What!? Type of rawType: " + rawType);
        }
        return (Class<?>)rawType;
    }
    
    public static Class<?> getRawType(final Type type, final Type assigningType) {
        if (type instanceof Class) {
            return (Class<?>)type;
        }
        if (type instanceof ParameterizedType) {
            return getRawType((ParameterizedType)type);
        }
        if (type instanceof TypeVariable) {
            if (assigningType == null) {
                return null;
            }
            final Object genericDeclaration = ((TypeVariable)type).getGenericDeclaration();
            if (!(genericDeclaration instanceof Class)) {
                return null;
            }
            final Map<TypeVariable<?>, Type> typeVarAssigns = getTypeArguments(assigningType, (Class<?>)genericDeclaration);
            if (typeVarAssigns == null) {
                return null;
            }
            final Type typeArgument = typeVarAssigns.get(type);
            if (typeArgument == null) {
                return null;
            }
            return getRawType(typeArgument, assigningType);
        }
        else {
            if (type instanceof GenericArrayType) {
                final Class<?> rawComponentType = getRawType(((GenericArrayType)type).getGenericComponentType(), assigningType);
                return Array.newInstance(rawComponentType, 0).getClass();
            }
            if (type instanceof WildcardType) {
                return null;
            }
            throw new IllegalArgumentException("unknown type: " + type);
        }
    }
    
    private static Map<TypeVariable<?>, Type> getTypeArguments(Class<?> cls, final Class<?> toClass, final Map<TypeVariable<?>, Type> subtypeVarAssigns) {
        if (!isAssignable(cls, toClass)) {
            return null;
        }
        if (cls.isPrimitive()) {
            if (toClass.isPrimitive()) {
                return new HashMap<TypeVariable<?>, Type>();
            }
            cls = (Class<?>)ClassUtils.primitiveToWrapper((Class)cls);
        }
        final HashMap<TypeVariable<?>, Type> typeVarAssigns = (subtypeVarAssigns == null) ? new HashMap<TypeVariable<?>, Type>() : new HashMap<TypeVariable<?>, Type>(subtypeVarAssigns);
        if (toClass.equals(cls)) {
            return typeVarAssigns;
        }
        return getTypeArguments(getClosestParentType(cls, toClass), toClass, typeVarAssigns);
    }
    
    public static Map<TypeVariable<?>, Type> getTypeArguments(final ParameterizedType type) {
        return getTypeArguments(type, getRawType(type), null);
    }
    
    private static Map<TypeVariable<?>, Type> getTypeArguments(final ParameterizedType parameterizedType, final Class<?> toClass, final Map<TypeVariable<?>, Type> subtypeVarAssigns) {
        final Class<?> cls = getRawType(parameterizedType);
        if (!isAssignable(cls, toClass)) {
            return null;
        }
        final Type ownerType = parameterizedType.getOwnerType();
        Map<TypeVariable<?>, Type> typeVarAssigns;
        if (ownerType instanceof ParameterizedType) {
            final ParameterizedType parameterizedOwnerType = (ParameterizedType)ownerType;
            typeVarAssigns = getTypeArguments(parameterizedOwnerType, getRawType(parameterizedOwnerType), subtypeVarAssigns);
        }
        else {
            typeVarAssigns = ((subtypeVarAssigns == null) ? new HashMap<TypeVariable<?>, Type>() : new HashMap<TypeVariable<?>, Type>(subtypeVarAssigns));
        }
        final Type[] typeArgs = parameterizedType.getActualTypeArguments();
        final TypeVariable<?>[] typeParams = cls.getTypeParameters();
        for (int i = 0; i < typeParams.length; ++i) {
            final Type typeArg = typeArgs[i];
            typeVarAssigns.put(typeParams[i], typeVarAssigns.getOrDefault(typeArg, typeArg));
        }
        if (toClass.equals(cls)) {
            return typeVarAssigns;
        }
        return getTypeArguments(getClosestParentType(cls, toClass), toClass, typeVarAssigns);
    }
    
    public static Map<TypeVariable<?>, Type> getTypeArguments(final Type type, final Class<?> toClass) {
        return getTypeArguments(type, toClass, null);
    }
    
    private static Map<TypeVariable<?>, Type> getTypeArguments(final Type type, final Class<?> toClass, final Map<TypeVariable<?>, Type> subtypeVarAssigns) {
        if (type instanceof Class) {
            return getTypeArguments((Class<?>)type, toClass, subtypeVarAssigns);
        }
        if (type instanceof ParameterizedType) {
            return getTypeArguments((ParameterizedType)type, toClass, subtypeVarAssigns);
        }
        if (type instanceof GenericArrayType) {
            return getTypeArguments(((GenericArrayType)type).getGenericComponentType(), toClass.isArray() ? toClass.getComponentType() : toClass, subtypeVarAssigns);
        }
        if (type instanceof WildcardType) {
            for (final Type bound : getImplicitUpperBounds((WildcardType)type)) {
                if (isAssignable(bound, toClass)) {
                    return getTypeArguments(bound, toClass, subtypeVarAssigns);
                }
            }
            return null;
        }
        if (type instanceof TypeVariable) {
            for (final Type bound : getImplicitBounds((TypeVariable<?>)type)) {
                if (isAssignable(bound, toClass)) {
                    return getTypeArguments(bound, toClass, subtypeVarAssigns);
                }
            }
            return null;
        }
        throw new IllegalStateException("found an unhandled type: " + type);
    }
    
    public static boolean isArrayType(final Type type) {
        return type instanceof GenericArrayType || (type instanceof Class && ((Class)type).isArray());
    }
    
    private static boolean isAssignable(final Type type, final Class<?> toClass) {
        if (type == null) {
            return toClass == null || !toClass.isPrimitive();
        }
        if (toClass == null) {
            return false;
        }
        if (toClass.equals(type)) {
            return true;
        }
        if (type instanceof Class) {
            return ClassUtils.isAssignable((Class)type, (Class)toClass);
        }
        if (type instanceof ParameterizedType) {
            return isAssignable(getRawType((ParameterizedType)type), toClass);
        }
        if (type instanceof TypeVariable) {
            for (final Type bound : ((TypeVariable)type).getBounds()) {
                if (isAssignable(bound, toClass)) {
                    return true;
                }
            }
            return false;
        }
        if (type instanceof GenericArrayType) {
            return toClass.equals(Object.class) || (toClass.isArray() && isAssignable(((GenericArrayType)type).getGenericComponentType(), toClass.getComponentType()));
        }
        if (type instanceof WildcardType) {
            return false;
        }
        throw new IllegalStateException("found an unhandled type: " + type);
    }
    
    private static boolean isAssignable(final Type type, final GenericArrayType toGenericArrayType, final Map<TypeVariable<?>, Type> typeVarAssigns) {
        if (type == null) {
            return true;
        }
        if (toGenericArrayType == null) {
            return false;
        }
        if (toGenericArrayType.equals(type)) {
            return true;
        }
        final Type toComponentType = toGenericArrayType.getGenericComponentType();
        if (type instanceof Class) {
            final Class<?> cls = (Class<?>)type;
            return cls.isArray() && isAssignable(cls.getComponentType(), toComponentType, typeVarAssigns);
        }
        if (type instanceof GenericArrayType) {
            return isAssignable(((GenericArrayType)type).getGenericComponentType(), toComponentType, typeVarAssigns);
        }
        if (type instanceof WildcardType) {
            for (final Type bound : getImplicitUpperBounds((WildcardType)type)) {
                if (isAssignable(bound, toGenericArrayType)) {
                    return true;
                }
            }
            return false;
        }
        if (type instanceof TypeVariable) {
            for (final Type bound : getImplicitBounds((TypeVariable<?>)type)) {
                if (isAssignable(bound, toGenericArrayType)) {
                    return true;
                }
            }
            return false;
        }
        if (type instanceof ParameterizedType) {
            return false;
        }
        throw new IllegalStateException("found an unhandled type: " + type);
    }
    
    private static boolean isAssignable(final Type type, final ParameterizedType toParameterizedType, final Map<TypeVariable<?>, Type> typeVarAssigns) {
        if (type == null) {
            return true;
        }
        if (toParameterizedType == null) {
            return false;
        }
        if (type instanceof GenericArrayType) {
            return false;
        }
        if (toParameterizedType.equals(type)) {
            return true;
        }
        final Class<?> toClass = getRawType(toParameterizedType);
        final Map<TypeVariable<?>, Type> fromTypeVarAssigns = getTypeArguments(type, toClass, null);
        if (fromTypeVarAssigns == null) {
            return false;
        }
        if (fromTypeVarAssigns.isEmpty()) {
            return true;
        }
        final Map<TypeVariable<?>, Type> toTypeVarAssigns = getTypeArguments(toParameterizedType, toClass, typeVarAssigns);
        for (final TypeVariable<?> var : toTypeVarAssigns.keySet()) {
            final Type toTypeArg = unrollVariableAssignments(var, toTypeVarAssigns);
            final Type fromTypeArg = unrollVariableAssignments(var, fromTypeVarAssigns);
            if (toTypeArg == null && fromTypeArg instanceof Class) {
                continue;
            }
            if (fromTypeArg != null && toTypeArg != null && !toTypeArg.equals(fromTypeArg) && (!(toTypeArg instanceof WildcardType) || !isAssignable(fromTypeArg, toTypeArg, typeVarAssigns))) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isAssignable(final Type type, final Type toType) {
        return isAssignable(type, toType, null);
    }
    
    private static boolean isAssignable(final Type type, final Type toType, final Map<TypeVariable<?>, Type> typeVarAssigns) {
        if (toType == null || toType instanceof Class) {
            return isAssignable(type, (Class<?>)toType);
        }
        if (toType instanceof ParameterizedType) {
            return isAssignable(type, (ParameterizedType)toType, typeVarAssigns);
        }
        if (toType instanceof GenericArrayType) {
            return isAssignable(type, (GenericArrayType)toType, typeVarAssigns);
        }
        if (toType instanceof WildcardType) {
            return isAssignable(type, (WildcardType)toType, typeVarAssigns);
        }
        if (toType instanceof TypeVariable) {
            return isAssignable(type, (TypeVariable<?>)toType, typeVarAssigns);
        }
        throw new IllegalStateException("found an unhandled type: " + toType);
    }
    
    private static boolean isAssignable(final Type type, final TypeVariable<?> toTypeVariable, final Map<TypeVariable<?>, Type> typeVarAssigns) {
        if (type == null) {
            return true;
        }
        if (toTypeVariable == null) {
            return false;
        }
        if (toTypeVariable.equals(type)) {
            return true;
        }
        if (type instanceof TypeVariable) {
            final Type[] implicitBounds;
            final Type[] bounds = implicitBounds = getImplicitBounds((TypeVariable<?>)type);
            for (final Type bound : implicitBounds) {
                if (isAssignable(bound, toTypeVariable, typeVarAssigns)) {
                    return true;
                }
            }
        }
        if (type instanceof Class || type instanceof ParameterizedType || type instanceof GenericArrayType || type instanceof WildcardType) {
            return false;
        }
        throw new IllegalStateException("found an unhandled type: " + type);
    }
    
    private static boolean isAssignable(final Type type, final WildcardType toWildcardType, final Map<TypeVariable<?>, Type> typeVarAssigns) {
        if (type == null) {
            return true;
        }
        if (toWildcardType == null) {
            return false;
        }
        if (toWildcardType.equals(type)) {
            return true;
        }
        final Type[] toUpperBounds = getImplicitUpperBounds(toWildcardType);
        final Type[] toLowerBounds = getImplicitLowerBounds(toWildcardType);
        if (type instanceof WildcardType) {
            final WildcardType wildcardType = (WildcardType)type;
            final Type[] upperBounds = getImplicitUpperBounds(wildcardType);
            final Type[] lowerBounds = getImplicitLowerBounds(wildcardType);
            for (Type toBound : toUpperBounds) {
                toBound = substituteTypeVariables(toBound, typeVarAssigns);
                for (final Type bound : upperBounds) {
                    if (!isAssignable(bound, toBound, typeVarAssigns)) {
                        return false;
                    }
                }
            }
            for (Type toBound : toLowerBounds) {
                toBound = substituteTypeVariables(toBound, typeVarAssigns);
                for (final Type bound : lowerBounds) {
                    if (!isAssignable(toBound, bound, typeVarAssigns)) {
                        return false;
                    }
                }
            }
            return true;
        }
        for (final Type toBound2 : toUpperBounds) {
            if (!isAssignable(type, substituteTypeVariables(toBound2, typeVarAssigns), typeVarAssigns)) {
                return false;
            }
        }
        for (final Type toBound2 : toLowerBounds) {
            if (!isAssignable(substituteTypeVariables(toBound2, typeVarAssigns), type, typeVarAssigns)) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isInstance(final Object value, final Type type) {
        return type != null && ((value == null) ? (!(type instanceof Class) || !((Class)type).isPrimitive()) : isAssignable(value.getClass(), type, null));
    }
    
    private static <T> void mapTypeVariablesToArguments(final Class<T> cls, final ParameterizedType parameterizedType, final Map<TypeVariable<?>, Type> typeVarAssigns) {
        final Type ownerType = parameterizedType.getOwnerType();
        if (ownerType instanceof ParameterizedType) {
            mapTypeVariablesToArguments((Class<Object>)cls, (ParameterizedType)ownerType, typeVarAssigns);
        }
        final Type[] typeArgs = parameterizedType.getActualTypeArguments();
        final TypeVariable<?>[] typeVars = getRawType(parameterizedType).getTypeParameters();
        final List<TypeVariable<Class<T>>> typeVarList = Arrays.asList(cls.getTypeParameters());
        for (int i = 0; i < typeArgs.length; ++i) {
            final TypeVariable<?> typeVar = typeVars[i];
            final Type typeArg = typeArgs[i];
            if (typeVarList.contains(typeArg) && typeVarAssigns.containsKey(typeVar)) {
                typeVarAssigns.put((TypeVariable<?>)typeArg, typeVarAssigns.get(typeVar));
            }
        }
    }
    
    public static Type[] normalizeUpperBounds(final Type[] bounds) {
        Validate.notNull(bounds, "bounds", new Object[0]);
        if (bounds.length < 2) {
            return bounds;
        }
        final Set<Type> types = new HashSet<Type>(bounds.length);
        for (final Type type1 : bounds) {
            boolean subtypeFound = false;
            for (final Type type2 : bounds) {
                if (type1 != type2 && isAssignable(type2, type1, null)) {
                    subtypeFound = true;
                    break;
                }
            }
            if (!subtypeFound) {
                types.add(type1);
            }
        }
        return types.toArray(ArrayUtils.EMPTY_TYPE_ARRAY);
    }
    
    public static final ParameterizedType parameterize(final Class<?> rawClass, final Map<TypeVariable<?>, Type> typeVariableMap) {
        Validate.notNull(rawClass, "rawClass", new Object[0]);
        Validate.notNull(typeVariableMap, "typeVariableMap", new Object[0]);
        return parameterizeWithOwner(null, rawClass, extractTypeArgumentsFrom(typeVariableMap, rawClass.getTypeParameters()));
    }
    
    public static final ParameterizedType parameterize(final Class<?> rawClass, final Type... typeArguments) {
        return parameterizeWithOwner(null, rawClass, typeArguments);
    }
    
    private static String parameterizedTypeToString(final ParameterizedType parameterizedType) {
        final StringBuilder builder = new StringBuilder();
        final Type useOwner = parameterizedType.getOwnerType();
        final Class<?> raw = (Class<?>)parameterizedType.getRawType();
        if (useOwner == null) {
            builder.append(raw.getName());
        }
        else {
            if (useOwner instanceof Class) {
                builder.append(((Class)useOwner).getName());
            }
            else {
                builder.append(useOwner.toString());
            }
            builder.append('.').append(raw.getSimpleName());
        }
        final int[] recursiveTypeIndexes = findRecursiveTypes(parameterizedType);
        if (recursiveTypeIndexes.length > 0) {
            appendRecursiveTypes(builder, recursiveTypeIndexes, parameterizedType.getActualTypeArguments());
        }
        else {
            appendAllTo(builder.append('<'), ", ", parameterizedType.getActualTypeArguments()).append('>');
        }
        return builder.toString();
    }
    
    public static final ParameterizedType parameterizeWithOwner(final Type owner, final Class<?> rawClass, final Map<TypeVariable<?>, Type> typeVariableMap) {
        Validate.notNull(rawClass, "rawClass", new Object[0]);
        Validate.notNull(typeVariableMap, "typeVariableMap", new Object[0]);
        return parameterizeWithOwner(owner, rawClass, extractTypeArgumentsFrom(typeVariableMap, rawClass.getTypeParameters()));
    }
    
    public static final ParameterizedType parameterizeWithOwner(final Type owner, final Class<?> rawClass, final Type... typeArguments) {
        Validate.notNull(rawClass, "rawClass", new Object[0]);
        Type useOwner;
        if (rawClass.getEnclosingClass() == null) {
            Validate.isTrue(owner == null, "no owner allowed for top-level %s", rawClass);
            useOwner = null;
        }
        else if (owner == null) {
            useOwner = rawClass.getEnclosingClass();
        }
        else {
            Validate.isTrue(isAssignable(owner, rawClass.getEnclosingClass()), "%s is invalid owner type for parameterized %s", owner, rawClass);
            useOwner = owner;
        }
        Validate.noNullElements(typeArguments, "null type argument at index %s", new Object[0]);
        Validate.isTrue(rawClass.getTypeParameters().length == typeArguments.length, "invalid number of type parameters specified: expected %d, got %d", rawClass.getTypeParameters().length, typeArguments.length);
        return new ParameterizedTypeImpl((Class)rawClass, useOwner, typeArguments);
    }
    
    private static Type substituteTypeVariables(final Type type, final Map<TypeVariable<?>, Type> typeVarAssigns) {
        if (!(type instanceof TypeVariable) || typeVarAssigns == null) {
            return type;
        }
        final Type replacementType = typeVarAssigns.get(type);
        if (replacementType == null) {
            throw new IllegalArgumentException("missing assignment type for type variable " + type);
        }
        return replacementType;
    }
    
    public static String toLongString(final TypeVariable<?> typeVariable) {
        Validate.notNull(typeVariable, "typeVariable", new Object[0]);
        final StringBuilder buf = new StringBuilder();
        final GenericDeclaration d = (GenericDeclaration)typeVariable.getGenericDeclaration();
        if (d instanceof Class) {
            Class<?> c;
            for (c = (Class<?>)d; c.getEnclosingClass() != null; c = c.getEnclosingClass()) {
                buf.insert(0, c.getSimpleName()).insert(0, '.');
            }
            buf.insert(0, c.getName());
        }
        else if (d instanceof Type) {
            buf.append(toString((Type)d));
        }
        else {
            buf.append(d);
        }
        return buf.append(':').append(typeVariableToString(typeVariable)).toString();
    }
    
    private static <T> String toString(final T object) {
        return (object instanceof Type) ? toString((Type)object) : object.toString();
    }
    
    public static String toString(final Type type) {
        Validate.notNull(type);
        if (type instanceof Class) {
            return classToString((Class<?>)type);
        }
        if (type instanceof ParameterizedType) {
            return parameterizedTypeToString((ParameterizedType)type);
        }
        if (type instanceof WildcardType) {
            return wildcardTypeToString((WildcardType)type);
        }
        if (type instanceof TypeVariable) {
            return typeVariableToString((TypeVariable<?>)type);
        }
        if (type instanceof GenericArrayType) {
            return genericArrayTypeToString((GenericArrayType)type);
        }
        throw new IllegalArgumentException(ObjectUtils.identityToString((Object)type));
    }
    
    public static boolean typesSatisfyVariables(final Map<TypeVariable<?>, Type> typeVariableMap) {
        Validate.notNull(typeVariableMap, "typeVariableMap", new Object[0]);
        for (final Map.Entry<TypeVariable<?>, Type> entry : typeVariableMap.entrySet()) {
            final TypeVariable<?> typeVar = entry.getKey();
            final Type type = entry.getValue();
            for (final Type bound : getImplicitBounds(typeVar)) {
                if (!isAssignable(type, substituteTypeVariables(bound, typeVariableMap), typeVariableMap)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private static String typeVariableToString(final TypeVariable<?> typeVariable) {
        final StringBuilder buf = new StringBuilder(typeVariable.getName());
        final Type[] bounds = typeVariable.getBounds();
        if (bounds.length > 0 && (bounds.length != 1 || !Object.class.equals(bounds[0]))) {
            buf.append(" extends ");
            appendAllTo(buf, " & ", typeVariable.getBounds());
        }
        return buf.toString();
    }
    
    private static Type[] unrollBounds(final Map<TypeVariable<?>, Type> typeArguments, final Type[] bounds) {
        Type[] result = bounds;
        for (int i = 0; i < result.length; ++i) {
            final Type unrolled = unrollVariables(typeArguments, result[i]);
            if (unrolled == null) {
                result = (Type[])ArrayUtils.remove((Object[])result, i--);
            }
            else {
                result[i] = unrolled;
            }
        }
        return result;
    }
    
    private static Type unrollVariableAssignments(TypeVariable<?> typeVariable, final Map<TypeVariable<?>, Type> typeVarAssigns) {
        Type result;
        while (true) {
            result = typeVarAssigns.get(typeVariable);
            if (!(result instanceof TypeVariable) || result.equals(typeVariable)) {
                break;
            }
            typeVariable = (TypeVariable<?>)result;
        }
        return result;
    }
    
    public static Type unrollVariables(Map<TypeVariable<?>, Type> typeArguments, final Type type) {
        if (typeArguments == null) {
            typeArguments = Collections.emptyMap();
        }
        if (containsTypeVariables(type)) {
            if (type instanceof TypeVariable) {
                return unrollVariables(typeArguments, typeArguments.get(type));
            }
            if (type instanceof ParameterizedType) {
                final ParameterizedType p = (ParameterizedType)type;
                Map<TypeVariable<?>, Type> parameterizedTypeArguments;
                if (p.getOwnerType() == null) {
                    parameterizedTypeArguments = typeArguments;
                }
                else {
                    parameterizedTypeArguments = new HashMap<TypeVariable<?>, Type>(typeArguments);
                    parameterizedTypeArguments.putAll(getTypeArguments(p));
                }
                final Type[] args = p.getActualTypeArguments();
                for (int i = 0; i < args.length; ++i) {
                    final Type unrolled = unrollVariables(parameterizedTypeArguments, args[i]);
                    if (unrolled != null) {
                        args[i] = unrolled;
                    }
                }
                return parameterizeWithOwner(p.getOwnerType(), (Class<?>)p.getRawType(), args);
            }
            if (type instanceof WildcardType) {
                final WildcardType wild = (WildcardType)type;
                return wildcardType().withUpperBounds(unrollBounds(typeArguments, wild.getUpperBounds())).withLowerBounds(unrollBounds(typeArguments, wild.getLowerBounds())).build();
            }
        }
        return type;
    }
    
    public static WildcardTypeBuilder wildcardType() {
        return new WildcardTypeBuilder();
    }
    
    private static String wildcardTypeToString(final WildcardType wildcardType) {
        final StringBuilder buf = new StringBuilder().append('?');
        final Type[] lowerBounds = wildcardType.getLowerBounds();
        final Type[] upperBounds = wildcardType.getUpperBounds();
        if (lowerBounds.length > 1 || (lowerBounds.length == 1 && lowerBounds[0] != null)) {
            appendAllTo(buf.append(" super "), " & ", lowerBounds);
        }
        else if (upperBounds.length > 1 || (upperBounds.length == 1 && !Object.class.equals(upperBounds[0]))) {
            appendAllTo(buf.append(" extends "), " & ", upperBounds);
        }
        return buf.toString();
    }
    
    public static <T> Typed<T> wrap(final Class<T> type) {
        return wrap((Type)type);
    }
    
    public static <T> Typed<T> wrap(final Type type) {
        return (Typed<T>)(() -> type);
    }
    
    static {
        WILDCARD_ALL = wildcardType().withUpperBounds(Object.class).build();
    }
    
    private static final class GenericArrayTypeImpl implements GenericArrayType
    {
        private final Type componentType;
        
        private GenericArrayTypeImpl(final Type componentType) {
            this.componentType = componentType;
        }
        
        @Override
        public boolean equals(final Object obj) {
            return obj == this || (obj instanceof GenericArrayType && equals(this, (Type)obj));
        }
        
        @Override
        public Type getGenericComponentType() {
            return this.componentType;
        }
        
        @Override
        public int hashCode() {
            int result = 1072;
            result |= this.componentType.hashCode();
            return result;
        }
        
        @Override
        public String toString() {
            return TypeUtils.toString((Type)this);
        }
    }
    
    private static final class ParameterizedTypeImpl implements ParameterizedType
    {
        private final Class<?> raw;
        private final Type useOwner;
        private final Type[] typeArguments;
        
        private ParameterizedTypeImpl(final Class<?> rawClass, final Type useOwner, final Type[] typeArguments) {
            this.raw = rawClass;
            this.useOwner = useOwner;
            this.typeArguments = Arrays.copyOf(typeArguments, typeArguments.length, (Class<? extends Type[]>)Type[].class);
        }
        
        @Override
        public boolean equals(final Object obj) {
            return obj == this || (obj instanceof ParameterizedType && equals(this, (Type)obj));
        }
        
        @Override
        public Type[] getActualTypeArguments() {
            return this.typeArguments.clone();
        }
        
        @Override
        public Type getOwnerType() {
            return this.useOwner;
        }
        
        @Override
        public Type getRawType() {
            return this.raw;
        }
        
        @Override
        public int hashCode() {
            int result = 1136;
            result |= this.raw.hashCode();
            result <<= 4;
            result |= Objects.hashCode(this.useOwner);
            result <<= 8;
            result |= Arrays.hashCode(this.typeArguments);
            return result;
        }
        
        @Override
        public String toString() {
            return TypeUtils.toString((Type)this);
        }
    }
    
    public static class WildcardTypeBuilder implements Builder<WildcardType>
    {
        private Type[] upperBounds;
        private Type[] lowerBounds;
        
        private WildcardTypeBuilder() {
        }
        
        public WildcardType build() {
            return new WildcardTypeImpl(this.upperBounds, this.lowerBounds);
        }
        
        public WildcardTypeBuilder withLowerBounds(final Type... bounds) {
            this.lowerBounds = bounds;
            return this;
        }
        
        public WildcardTypeBuilder withUpperBounds(final Type... bounds) {
            this.upperBounds = bounds;
            return this;
        }
    }
    
    private static final class WildcardTypeImpl implements WildcardType
    {
        private final Type[] upperBounds;
        private final Type[] lowerBounds;
        
        private WildcardTypeImpl(final Type[] upperBounds, final Type[] lowerBounds) {
            this.upperBounds = (Type[])ObjectUtils.defaultIfNull((Object)upperBounds, (Object)ArrayUtils.EMPTY_TYPE_ARRAY);
            this.lowerBounds = (Type[])ObjectUtils.defaultIfNull((Object)lowerBounds, (Object)ArrayUtils.EMPTY_TYPE_ARRAY);
        }
        
        @Override
        public boolean equals(final Object obj) {
            return obj == this || (obj instanceof WildcardType && equals(this, (Type)obj));
        }
        
        @Override
        public Type[] getLowerBounds() {
            return this.lowerBounds.clone();
        }
        
        @Override
        public Type[] getUpperBounds() {
            return this.upperBounds.clone();
        }
        
        @Override
        public int hashCode() {
            int result = 18688;
            result |= Arrays.hashCode(this.upperBounds);
            result <<= 8;
            result |= Arrays.hashCode(this.lowerBounds);
            return result;
        }
        
        @Override
        public String toString() {
            return TypeUtils.toString((Type)this);
        }
    }
}
