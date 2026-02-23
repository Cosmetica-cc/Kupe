/*
 * Kupe Testmod - Test and Example code for usage of the Kupe Library.
 * Written in 2024-2025 by Cosmetica Contributors
 * To the extent possible under law, the author(s) have dedicated all copyright and related and neighboring rights to this software to the public domain worldwide. This software is distributed without any warranty.
 * You should have received a copy of the CC0 Public Domain Dedication along with this software. If not, see <http://creativecommons.org/publicdomain/zero/1.0/>.
 */

package cc.cosmetica.kupe.testmod.gui.widget;

import cc.cosmetica.kupe.api.State;
import cc.cosmetica.kupe.api.Text;
import cc.cosmetica.kupe.api.gui.Button;
import cc.cosmetica.kupe.api.gui.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class SubmitButton<T> extends Button {
    /**
     * Create a new button with the given text.
     *
     * @param text      the text to provide.
     * @param onClicked the action to perform on clicked.
     * @param state     the state for entry.
     */
    public SubmitButton(Text text, Consumer<T> onClicked, State<T> state) {
        super(text, () -> onClicked.accept(state.peek()));
        this.state = state;
    }

    private final State<T> state;

    @Override
    public List<Component> build() {
        T object = this.state.acquire(this);

        boolean disabled;
        if (object instanceof String) {
            disabled = ((String)object).isEmpty();
        } else if (object instanceof Collection) {
            disabled = ((Collection<?>)object).isEmpty();
        } else if (object instanceof Optional) {
            disabled = !((Optional<?>)object).isPresent();
        } else {
            disabled = object != null;
        }

        this.setDisabled(disabled);
        return Collections.emptyList();
    }
}
