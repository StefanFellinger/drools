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

package org.drools.workbench.models.guided.template.backend;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.drools.core.util.ReflectiveVisitor;
import org.drools.core.util.StringUtils;
import org.drools.template.DataProvider;
import org.drools.template.DataProviderCompiler;
import org.drools.template.objects.ArrayDataProvider;
import org.drools.workbench.models.commons.backend.rule.DRLConstraintValueBuilder;
import org.drools.workbench.models.commons.backend.rule.RuleModelDRLPersistenceImpl;
import org.drools.workbench.models.commons.backend.rule.RuleModelPersistence;
import org.drools.workbench.models.datamodel.rule.ActionFieldValue;
import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.datamodel.rule.CompositeFieldConstraint;
import org.drools.workbench.models.datamodel.rule.FieldConstraint;
import org.drools.workbench.models.datamodel.rule.FieldNatureType;
import org.drools.workbench.models.datamodel.rule.FreeFormLine;
import org.drools.workbench.models.datamodel.rule.FromCollectCompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.IFactPattern;
import org.drools.workbench.models.datamodel.rule.InterpolationVariable;
import org.drools.workbench.models.datamodel.rule.RuleModel;
import org.drools.workbench.models.datamodel.rule.SingleFieldConstraint;
import org.drools.workbench.models.guided.template.shared.TemplateModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class persists a {@link TemplateModel} to DRL template
 */
public class RuleTemplateModelDRLPersistenceImpl
        extends RuleModelDRLPersistenceImpl {

    private static final Pattern patternTemplateKey = Pattern.compile("@\\{(.+?)\\}");

    private static final Logger               log      = LoggerFactory.getLogger(RuleTemplateModelDRLPersistenceImpl.class);
    private static final RuleModelPersistence INSTANCE = new RuleTemplateModelDRLPersistenceImpl();

    private RuleTemplateModelDRLPersistenceImpl() {
        super();
    }

    public static RuleModelPersistence getInstance() {
        return INSTANCE;
    }

    protected LHSPatternVisitor getLHSPatternVisitor(final boolean isDSLEnhanced,
                                                     final StringBuilder buf,
                                                     final String nestedIndentation,
                                                     final boolean isNegated) {
        return new LHSPatternVisitor(isDSLEnhanced,
                                     bindingsPatterns,
                                     bindingsFields,
                                     constraintValueBuilder,
                                     buf,
                                     nestedIndentation,
                                     isNegated);
    }

    @Override
    protected RHSActionVisitor getRHSActionVisitor(boolean isDSLEnhanced,
                                                   StringBuilder buf,
                                                   String indentation) {
        return new RHSActionVisitor(isDSLEnhanced,
                                    bindingsPatterns,
                                    bindingsFields,
                                    constraintValueBuilder,
                                    buf,
                                    indentation);
    }

    public static class LHSPatternVisitor extends RuleModelDRLPersistenceImpl.LHSPatternVisitor {

        public LHSPatternVisitor(final boolean isDSLEnhanced,
                                 final Map<String, IFactPattern> bindingsPatterns,
                                 final Map<String, FieldConstraint> bindingsFields,
                                 final DRLConstraintValueBuilder constraintValueBuilder,
                                 final StringBuilder b,
                                 final String indentation,
                                 final boolean isPatternNegated) {
            super(isDSLEnhanced,
                  bindingsPatterns,
                  bindingsFields,
                  constraintValueBuilder,
                  b,
                  indentation,
                  isPatternNegated);
        }

        protected int generateConstraint(int printedCount, StringBuilder buffer, FieldConstraint constr) {
            buffer.append("@if{" + ((SingleFieldConstraint)constr).getValue() + " != empty}");
            printedCount =  super.generateConstraint(printedCount, buffer, constr);
            buf.append("@end{}");
            return printedCount;
        }


        protected void generateNestedConstraint(StringBuilder buffer, CompositeFieldConstraint cfc, FieldConstraint[] nestedConstraints, int i, FieldConstraint nestedConstr) {
            buffer.append("@if{" + ((SingleFieldConstraint)nestedConstr).getValue() + " != empty}");
            super.generateNestedConstraint(buf, cfc, nestedConstraints, i, nestedConstr);
            buf.append("@end{}");
        }

        @Override
        public void visitFreeFormLine( FreeFormLine ffl ) {
            final Matcher matcherTemplateKey = patternTemplateKey.matcher( ffl.getText() );

            boolean found = matcherTemplateKey.find();
            if ( found ) {
            buf.append("@if{");
                boolean addAnd = false;
                while ( found ) {
                    String varName = matcherTemplateKey.group( 1 );
                    if ( addAnd ) {
                        buf.append( " && ");
                    }
                    buf.append(varName + " != empty");
                    addAnd = true;
                    found = matcherTemplateKey.find();
                }
                buf.append("}");


                super.visitFreeFormLine( ffl );
                buf.append("@end{}");
            } else {
                // no variables found
                super.visitFreeFormLine( ffl );
            }
        }

        public void visitFromCollectCompositeFactPattern( final FromCollectCompositeFactPattern pattern,
                                                          final boolean isSubPattern ) {

            if ( pattern.getRightPattern() instanceof FreeFormLine ) {
                // this allows MVEL to skip the collect, if any vars are empty
                // note this actually duplicates another inner check for the FFL itself
                // @TODO the FFL should get a reference to the parent, so it can avoid this duplication.
                final FreeFormLine ffl = (FreeFormLine) pattern.getRightPattern();
                final Matcher matcherTemplateKey = patternTemplateKey.matcher( ffl.getText() );

                boolean found = matcherTemplateKey.find();
                if ( found ) {
                    buf.append("@if{");
                    boolean addAnd = false;
                    while ( found ) {
                        String varName = matcherTemplateKey.group( 1 );
                        if ( addAnd ) {
                            buf.append( " && ");
                        }
                        buf.append(varName + " != empty");
                        addAnd = true;
                    }
                    buf.append("}");
                    super.visitFromCollectCompositeFactPattern(pattern, isSubPattern);
                    buf.append("@end{}");
                    found = matcherTemplateKey.find();
                } else {
                    // no variables found
                    super.visitFromCollectCompositeFactPattern(pattern, isSubPattern);
                }
            } else {
                super.visitFromCollectCompositeFactPattern(pattern, isSubPattern);
            }
        }
    }

    public static class RHSActionVisitor extends RuleModelDRLPersistenceImpl.RHSActionVisitor {

        public RHSActionVisitor(final boolean isDSLEnhanced,
                                final Map<String, IFactPattern> bindingsPatterns,
                                final Map<String, FieldConstraint> bindingsFields,
                                final DRLConstraintValueBuilder constraintValueBuilder,
                                final StringBuilder b,
                                final String indentation) {
            super( isDSLEnhanced,
                   bindingsPatterns,
                   bindingsFields,
                   constraintValueBuilder,
                   b,
                   indentation );
        }

        protected void generateSetMethodCall(String variableName, ActionFieldValue fieldValue) {
            buf.append("@if{" + fieldValue.getValue() + " != empty}");
            super.generateSetMethodCall(variableName, fieldValue);
            buf.append("@end{}");
        }

        @Override
        public void visitFreeFormLine( FreeFormLine ffl ) {
            final Matcher matcherTemplateKey = patternTemplateKey.matcher( ffl.getText() );
            boolean found = matcherTemplateKey.find();
            if ( found ) {
                buf.append("@if{");
                boolean addAnd = false;
                while ( found ) {
                    String varName = matcherTemplateKey.group( 1 );
                    if ( addAnd ) {
                        buf.append( " && ");
                    }
                    buf.append(varName + " != empty");
                    addAnd = true;
                    found = matcherTemplateKey.find();
                }
                buf.append("}");

                super.visitFreeFormLine( ffl );
                buf.append("@end{}");
            }  else {
                // no variables found
                super.visitFreeFormLine( ffl );
            }
        }
    }

    @Override
    public String marshal(final RuleModel model) {

        //Build rule
        final String ruleTemplate = marshalRule(model);
        log.debug("ruleTemplate:\n{}",
                  ruleTemplate);

        log.debug("generated template:\n{}", ruleTemplate);

        final DataProvider dataProvider = chooseDataProvider(model);
        final DataProviderCompiler tplCompiler = new DataProviderCompiler();
        final String generatedDrl = tplCompiler.compile(dataProvider,
                                                        new ByteArrayInputStream(ruleTemplate.getBytes()),
                                                        false );
        log.debug("generated drl:\n{}", generatedDrl);

        return generatedDrl;
    }

    protected String marshalRule(final RuleModel model) {
        boolean isDSLEnhanced = model.hasDSLSentences();
        bindingsPatterns = new HashMap<String, IFactPattern>();
        bindingsFields = new HashMap<String, FieldConstraint>();

        StringBuilder buf = new StringBuilder();

        //Build rule
        this.marshalRuleHeader(model,
                               buf);
        super.marshalMetadata(buf,
                              model);
        super.marshalAttributes(buf,
                                model);

        buf.append("\twhen\n");
        super.marshalLHS(buf,
                         model,
                         isDSLEnhanced);
        buf.append("\tthen\n");
        super.marshalRHS(buf,
                         model,
                         isDSLEnhanced);
        this.marshalFooter(buf);
        return buf.toString();
    }

    private DataProvider chooseDataProvider(final RuleModel model) {
        DataProvider dataProvider;
        TemplateModel tplModel = (TemplateModel) model;
        if (tplModel.getRowsCount() > 0) {
            dataProvider = new ArrayDataProvider(tplModel.getTableAsArray());
        } else {
            dataProvider = generateEmptyIterator();
        }
        return dataProvider;
    }

    private DataProvider generateEmptyIterator() {
        return new DataProvider() {

            public boolean hasNext() {
                return false;
            }

            public String[] next() {
                return new String[0];
            }
        };
    }

    @Override
    protected void marshalRuleHeader(final RuleModel model,
                                     final StringBuilder buf) {
        //Append Template header
        TemplateModel templateModel = (TemplateModel) model;
        buf.append("template header\n");

        InterpolationVariable[] interpolationVariables = templateModel.getInterpolationVariablesList();
        if (interpolationVariables.length == 0) {
            buf.append("test_var").append('\n');
        } else {
            for (InterpolationVariable var : interpolationVariables) {
                buf.append(var.getVarName()).append('\n');
            }
        }
        buf.append("\n");

        //Append Package header
        super.marshalPackageHeader(model,
                                   buf);

        //Append Template definition
        buf.append("\ntemplate \"").append(super.marshalRuleName(templateModel)).append("\"\n\n");
        super.marshalRuleHeader(model,
                                buf);
    }

    @Override
    protected String marshalRuleName(final RuleModel model) {
        return super.marshalRuleName(model) + "_@{row.rowNumber}";
    }

    @Override
    protected void marshalFooter(final StringBuilder buf) {
        super.marshalFooter(buf);
        buf.append("\nend template");
    }

    }
