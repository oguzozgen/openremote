/*
 * Copyright 2010 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.openremote.model.value;

import org.openremote.model.value.impl.ValueFactoryImpl;

/**
 * Vends out implementations of {@link Value} and {@link ValueFactory}.
 */
public class Values {

    public static StringValue create(String string) {
        return instance().create(string);
    }

    public static BooleanValue create(boolean bool) {
        return instance().create(bool);
    }

    public static ArrayValue createArray() {
        return instance().createArray();
    }

    public static Null createNull() {
        return instance().createNull();
    }

    public static NumberValue create(double number) {
        return instance().create(number);
    }

    public static ObjectValue createObject() {
        return instance().createObject();
    }

    public static ValueFactory instance() {
        return ValueFactoryImpl.INSTANCE;
    }

    public static <T extends Value> T parse(String jsonString) throws ValueException {
        return instance().parse(jsonString);
    }
}
