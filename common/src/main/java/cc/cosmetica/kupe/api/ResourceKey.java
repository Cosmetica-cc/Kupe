/*
 * Copyright 2024, 2025 Cosmetica
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cc.cosmetica.kupe.api;

import cc.cosmetica.kupe.impl.LeavesSandbox;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

/**
 * Represents a resource in the filesystem or virtually.
 * @implNote created to have a consistent identifier class in all minecraft versions and other environments.
 */
public final class ResourceKey {
    public ResourceKey(String namespace, String path) {
        // apply same restrictions as ResourceLocation
        if (!ResourceLocation.isValidResourceLocation(namespace + ":" + path)) {
            throw new IllegalArgumentException("Invalid Resource Key");
        }

        this.namespace = namespace;
        this.path = path;
    }

    @LeavesSandbox
    public ResourceKey(ResourceLocation location) {
        this.namespace = location.getNamespace();
        this.path = location.getPath();
    }

    private final String namespace;
    private final String path;
    private ResourceLocation location;

    public String getPath() {
        return this.path;
    }

    public String getNamespace() {
        return this.namespace;
    }

    /**
     * Get a translation key for this resource key under the given group.
     * @param group the group under which to place the translation key.
     * @return a translation key, in the format "group.namespace.path[-1]"
     */
    public Text translationKey(String group) {
        String[] path = this.path.split("/");
        return Text.translatable(group + "." + this.namespace + "." + path[path.length - 1]);
    }

    @LeavesSandbox
    public ResourceLocation toResourceLocation() {
        if (this.location != null) return this.location;
        return this.location = new ResourceLocation(this.namespace, this.path);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || this.getClass() != o.getClass())
            return false;

        ResourceKey that = (ResourceKey) o;
        return Objects.equals(this.namespace, that.namespace) && Objects.equals(this.path, that.path);
    }

    @Override
    public int hashCode() {
        return 31 * this.namespace.hashCode() + this.path.hashCode();
    }

    @Override
    public String toString() {
        return this.namespace + ":" + this.path;
    }
}
