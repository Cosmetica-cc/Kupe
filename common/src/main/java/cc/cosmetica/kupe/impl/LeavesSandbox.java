package cc.cosmetica.kupe.impl;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates this method may differ based on the version and/or leaves the sandbox by accessing unstable classes.
 * This is most commonly on methods that access or use minecraft objects other than ResourceLocation.
 * <p></p>
 * Such methods should also be made clear in their name that they leave the sandbox, to prevent people accidentally
 * using them and introducing version-specific code unexpectedly.
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface LeavesSandbox {
}
