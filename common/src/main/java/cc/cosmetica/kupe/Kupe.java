package cc.cosmetica.kupe;

public class Kupe {
    public static final String MOD_ID = "kupe";
    // We can use this if we don't want to use DeferredRegister
    
    public static void init() {
        System.out.println(ExampleExpectPlatform.getConfigDirectory().toAbsolutePath().normalize().toString());
    }
}