/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.models.datamodel.rule;

/**
 * This is used to specify that the bound fact should be retracted
 * when the rule fires.
 */
public class ActionRetractFact
        implements
        IAction {

    public ActionRetractFact() {
    }

    public ActionRetractFact( final String var ) {
        this.variableName = var;
    }

    private String variableName;

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName( String variableName ) {
        this.variableName = variableName;
    }
}
