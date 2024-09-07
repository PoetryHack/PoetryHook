/**
 * Created: 11.05.2024 (MM:DD:YYY)
 */

package net.poetryhack.poetryhook;

import net.poetryhack.poetryhook.exceptions.PoetryHookException;
import net.poetryhack.poetryhook.util.MixinClassFileTransformer;
import net.poetryhack.poetryhook.util.MixinMethod;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Utility class to handle the boilerplate of injection and ejection
 * @author majorsopa
 * @author sootysplash
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public final class PoetryHookInjector {
    /**
     * @param inst {@link Instrumentation} object of the agent
     * @param mixinBases {@link MixinMethod} subclass objects to inject
     * @return ArrayList of {@link ClassFileTransformer} objects which can be used for ejection
     * @see #ejectMixins(Instrumentation, ArrayList)
     * @since 1.0.0
     * @author majorsopa, revised by sootysplash
     */
    public static ArrayList<ClassFileTransformer> injectMixins(Instrumentation inst, MixinBase ... mixinBases) {
        HashMap<Class<?>, MixinMethod[]> mixinsForClass = new HashMap<>();

        ArrayList<MixinMethod> mixinMethods = new ArrayList<>();
        for (MixinBase base : mixinBases) {
            mixinMethods.addAll(base.mixins());
        }

        // majorsopa start
        ArrayList<ClassFileTransformer> transformers = new ArrayList<>();
        ArrayList<Class<?>> classesToRetransform = new ArrayList<>();
        for (MixinMethod mixin : mixinMethods) {
            try {
                MixinClassFileTransformer transformer = new MixinClassFileTransformer(mixin);
                transformers.add(transformer);
                inst.addTransformer(transformer, true);

                Class<?> injectTo = mixin.injectTo;
                classesToRetransform.add(injectTo);
                // majorsopa end

                // sootysplash start
                MixinMethod[] mms = mixinsForClass.getOrDefault(injectTo, new MixinMethod[]{});
                MixinMethod[] to = new MixinMethod[mms.length + 1];
                System.arraycopy(mms, 0, to, 0, mms.length);
                to[mms.length] = mixin;
                mixinsForClass.put(injectTo, to);
            } catch (Throwable e) {
                e.printStackTrace(System.err);
            }
        }

        retransformAllRelevantClasses(inst, classesToRetransform, mixinsForClass, mixinBases.length);

        for (MixinMethod mixin : mixinMethods) {
            if (!mixin.loaded) {
                throw new PoetryHookException("Failed to inject Mixin: " + mixin.methodToCall.getDeclaringClass().getName() + " / " + mixin.methodToCall.getName());
            }
        }
        // sootysplash end

        return transformers;
    }

    /**
     * @param inst {@link Instrumentation} object that created the transformers
     * @param transformers ArrayList of {@link ClassFileTransformer} objects created by the agent
     * @see #injectMixins(Instrumentation, MixinBase...)
     * @see #retransformAllRelevantClasses(Instrumentation, ArrayList, HashMap, int)
     * @since 1.0.0
     * @author majorsopa
     */
    public static void ejectMixins(Instrumentation inst, ArrayList<ClassFileTransformer> transformers) {
        for (ClassFileTransformer transformer : transformers) {
            try {
                inst.removeTransformer(transformer);
            } catch (Throwable e) {
                throw new PoetryHookException(e);
            }
        }
    }

    /**
     * @param inst Agent {@link Instrumentation} object
     * @param classesToRetransform {@link ArrayList} of classes that are to be retransformed
     * @param mixinsForClass {@link HashMap} of {@link Class} keys to {@link MixinMethod} arrays which are the mixins for the classes being hooked
     * @param poolLength {@link int} for the size of {@link ThreadPoolExecutor} made for async retransformation
     * @since 1.0.0
     * @author sootysplash, seperated into api method by majorsopa
     */
    public static void retransformAllRelevantClasses(
            Instrumentation inst,
            ArrayList<Class<?>> classesToRetransform,
            HashMap<Class<?>, MixinMethod[]> mixinsForClass,
            int poolLength
    ) {
        ThreadPoolExecutor tpe = (ThreadPoolExecutor) Executors.newFixedThreadPool(poolLength);

        for (Class<?> clazz : classesToRetransform) {
            tpe.execute(() -> {
                synchronized (clazz) {
                    try {
                        inst.retransformClasses(clazz); // majorsopa
                    } catch (Throwable e) {
                        System.err.println("Mixins for class: " + Arrays.toString(mixinsForClass.getOrDefault(clazz, new MixinMethod[]{})));
                        throw new PoetryHookException(e);
                    }
                }
            });
        }
        tpe.shutdown();
        //noinspection StatementWithEmptyBody
        while (tpe.getActiveCount() > 0) {}
    }
}
