package com.nqzero.unflect;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import static com.nqzero.unflect.UnsafeWrapper.uu;
import java.lang.reflect.Method;

public class Unflect {
    public static final String splitChar = "\\.";
    static Safer<AccessibleObject,Boolean> override = build(AccessibleObject.class,"override");
    static void makeAccessible(AccessibleObject accessor) {
        override.putBoolean(accessor,true);
    }
    static void unLog() {
        try {
            Class cls = Class.forName("jdk.internal.module.IllegalAccessLogger");
            build(cls,"logger").putObjectVolatile(null,null);
        }
        catch (ClassNotFoundException ex) {}
    }


    static boolean dbg = false;

    public static void godMode() {
        try {
            Class base = Unflect.class;
            Class klass = base.getClassLoader().loadClass(base.getPackageName() + ".Support11");
            Method method = klass.getMethod("godMode");
            method.invoke(null);
        }
        catch (Throwable ex) {}
            
    }

    
    public static Object getObject(Object cl,String name) {
        try {
            Field field = cl.getClass().getDeclaredField(name);
            long offset = uu.objectFieldOffset(field);
            return uu.getObject(cl,offset);
        }
        catch (Exception ex) {}
        return null;
    }
    public interface Meth<VV> {
        public VV meth(Object obj,long offset);
    }
    public static <VV> VV getField(Object cl,Meth<VV> meth,String ... names) {
        if (names.length==1)
            names = names[0].split(splitChar);
        try {
            int num = names.length-1;
            for (int ii = 0; ii <= num; ii++) {
                Field field = cl.getClass().getDeclaredField(names[ii]);
                long offset = uu.objectFieldOffset(field);
                if (ii < num)
                    cl = uu.getObject(cl,offset);
                else
                    return meth.meth(cl,offset);
            }
        }
        catch (Exception ex) {}
        return null;
    }

    public static <TT,VV> Safer<TT,VV> build(Class<TT> klass,String name) {
        String [] names = name.split(splitChar);
        String firstName = names.length==0 ? name : names[0];
        Safer<TT,VV> ref = new Safer(klass,firstName);
        for (int ii=1; ii < names.length; ii++)
            ref.chain(names[ii]);
        return ref;
    }

    public static <TT,VV> Safer<TT,VV> build(TT sample,String name) {
        return build((Class<TT>) sample.getClass(),name);
    }
    
    static Field getSuperField(Class klass,String name) {
        for (; klass != Object.class; klass = klass.getSuperclass()) {
            try {
                return klass.getDeclaredField(name);
            }
            catch (NoSuchFieldException ex) {}
            catch (SecurityException ex) {}
        }
        return null;
    }
    
    public static Object getField(Class klass,String name) {
        try {
            Field f = klass.getDeclaredField(name);
            f.setAccessible(true);
            return f.get(null);
        }
        catch (Exception e) { return null; }
    }
    
    
}