//Raddon On Top!

package okhttp3.internal.annotations;

import javax.annotation.*;
import javax.annotation.meta.*;
import java.lang.annotation.*;

@Documented
@Nonnull
@TypeQualifierDefault({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface EverythingIsNonNull {
}
