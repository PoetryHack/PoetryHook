/**
 * Created: 06.26.2024
 */

package net.poetryhack.poetryhook.util;

import net.poetryhack.poetryhook.annotations.ShouldReturn;
import net.poetryhack.poetryhook.annotations.ToReturn;

import java.util.Objects;

/**
 * @since 1.0.0
 * @author sootysplash
 */
public class MixinReturnObject {
    private final Object object;
    private final boolean shouldReturn;
    public MixinReturnObject(Object object, boolean shouldReturn) {
        this.object = object;
        this.shouldReturn = shouldReturn;
    }
    public MixinReturnObject(boolean shouldReturn) {
        this(null, shouldReturn);
    }
    @ShouldReturn
    public boolean shouldReturn() {
        return shouldReturn;
    }
    @ToReturn
    public Object toReturn() {
        return object;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MixinReturnObject that)) return false;
        return shouldReturn == that.shouldReturn && Objects.equals(object, that.object);
    }
    @Override
    public int hashCode() {
        return Objects.hash(object, shouldReturn);
    }
    @Override
    public String toString() {
        return "MixinObject{" +
                "object=" + object +
                ", shouldReturn=" + shouldReturn +
                '}';
    }
}
