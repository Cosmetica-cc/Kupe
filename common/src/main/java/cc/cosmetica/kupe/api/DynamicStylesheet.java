package cc.cosmetica.kupe.api;

import cc.cosmetica.kupe.api.gui.style.Stylesheet;

/**
 * Dynamic Stylesheet. This is similar to using a pure {@link State}&lt;{@link cc.cosmetica.kupe.api.gui.style.Stylesheet Stylesheet}&gt;,
 * but will not rebuild the component when the stylesheet changes. Instead, it will just trigger a resize.
 * Use this when you want to modify the sizes of components at runtime.
 */
public class DynamicStylesheet extends State<Stylesheet> {
	public DynamicStylesheet(Stylesheet initialValue) {
		super(initialValue);
	}

	@Override
	public void set(Stylesheet value) {
		this.value = value;
		// TODO resize root
	}
}
