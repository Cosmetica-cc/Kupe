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

package cc.cosmetica.kupe.util;

/**
 * Represents a function of the form (float, float) -> T.
 * @param <T> the return type.
 */
@FunctionalInterface
public interface FloatBiFunction<T> {
    /**
     * Apply this function to the given argument.
     * @param a the first float.
     * @param b the second float.
     * @return the function result.
     */
    T apply(float a, float b);
}
