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
import java.util.HashMap;

/**
 * Utility class to handle the boilerplate of injection and ejection
 *
 * @author majorsopa
 * @author sootysplash
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public final class PoetryHookInjector {
    /**
     * @param inst                              {@link Instrumentation} object of the agent
     * @param unregisterTransformersImmediately if the transformers should be removed immediately after injection
     * @param mixinBases                        {@link MixinMethod} subclass objects to inject
     * @return ArrayList of {@link Class} objects which can be used for ejection
     * @author majorsopa, revised by sootysplash
     * @see #ejectMixins(Instrumentation, ArrayList)
     * @since 1.0.0
     */
    public static ArrayList<Class<?>> injectMixins(Instrumentation inst, boolean unregisterTransformersImmediately, MixinBase... mixinBases) {
        HashMap<Class<?>, MixinMethod[]> mixinsForClass = new HashMap<>();

        ArrayList<MixinMethod> mixinMethods = new ArrayList<>(mixinBases.length * 2);
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

        retransformAllRelevantClasses(inst, classesToRetransform);

        for (MixinMethod mixin : mixinMethods) {
            if (!mixin.loaded) {
                throw new PoetryHookException("Failed to inject Mixin: " + mixin.methodToCall.getDeclaringClass().getName() + " / " + mixin.methodToCall.getName());
            }
        }
        // sootysplash end

        if (unregisterTransformersImmediately) {
            ejectMixins(inst, transformers);
        }

        return classesToRetransform;
    }

    /**
     * Removes the transformers for all mixins and retransforms classes
     *
     * @param inst         {@link Instrumentation} object that created the transformers
     * @param transformers ArrayList of {@link ClassFileTransformer} objects created by the agent
     * @author majorsopa
     * @see #injectMixins(Instrumentation, boolean, MixinBase...)
     * @see #retransformAllRelevantClasses(Instrumentation, ArrayList)
     * @since 1.0.0
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
     * @param inst                 Agent {@link Instrumentation} object
     * @param classesToRetransform {@link ArrayList} of classes that are to be retransformed
     * @author sootysplash, seperated into api method by majorsopa
     * @since 1.0.0
     */
    public static void retransformAllRelevantClasses(
            Instrumentation inst,
            ArrayList<Class<?>> classesToRetransform
    ) {
        for (Class<?> clazz : classesToRetransform) {
            try {
                inst.retransformClasses(clazz); // majorsopa
            } catch (Throwable e) {
                System.err.println("Error when transforming " + clazz.getName());
                throw new PoetryHookException(e);
            }
        }
    }
}
