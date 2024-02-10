package cc.cosmetica.kupe.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates this method may differ based on the version and/or leaves the sandbox by accessing unstable classes.
 * This is most commonly on api methods that access or use minecraft objects other than ResourceLocation.
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface LeavesSandbox {
}
