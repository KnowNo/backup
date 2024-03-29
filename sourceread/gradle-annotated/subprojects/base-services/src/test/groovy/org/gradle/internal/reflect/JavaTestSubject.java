/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.internal.reflect;

@SuppressWarnings("UnusedDeclaration")
public class JavaTestSubject {

    private String myProp = "myValue";
    private boolean myBooleanProp = true;

    public int publicField;

    public String getMyProperty() {
        return myProp;
    }

    public void setMyProperty(String value) {
        myProp = value;
    }

    public boolean isMyBooleanProperty() {
        return myBooleanProp;
    }

    public void setMyBooleanProperty(boolean value) {
        myBooleanProp = value;
    }

    public boolean getMyOtherBooleanProperty() {
        return true;
    }

    public String isNotABooleanProperty() {
        return null;
    }

    public static String getStaticProperty() {
        return null;
    }

    public static void setStaticProperty(String value) {
    }

    public void getVoidProperty() {
    }

    public String getParamProperty(String param) {
        return null;
    }

    public void setParamProperty() {
    }

    public void setParamProperty(String param, String someOther) {
    }

    public void setWriteOnly(String param) {
    }

    public void throwsException() {
        throw new IllegalStateException();
    }

    protected String protectedMethod() {
        return "parent";
    }

    protected String overridden() {
        return "parent";
    }

    protected String getProtectedProperty() {
        return null;
    }

    protected void setProtectedProperty(String value) {
    }

    private String getPrivateProperty() {
        return null;
    }

    private void setPrivateProperty(String value) {
    }
}
