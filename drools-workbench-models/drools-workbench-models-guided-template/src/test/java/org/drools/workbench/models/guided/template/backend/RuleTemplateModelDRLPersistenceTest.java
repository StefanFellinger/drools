/*
 * Copyright 2010 JBoss Inc
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

package org.drools.workbench.models.guided.template.backend;

import org.drools.workbench.models.commons.backend.rule.RuleModelPersistence;
import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.datamodel.rule.CompositeFieldConstraint;
import org.drools.workbench.models.datamodel.rule.ConnectiveConstraint;
import org.drools.workbench.models.datamodel.rule.FactPattern;
import org.drools.workbench.models.datamodel.rule.RuleModel;
import org.drools.workbench.models.datamodel.rule.SingleFieldConstraint;
import org.drools.workbench.models.guided.template.shared.TemplateModel;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class RuleTemplateModelDRLPersistenceTest {

    private RuleModelPersistence ruleModelPersistence;

    @Before
    public void setUp() throws Exception {
        ruleModelPersistence = RuleTemplateModelDRLPersistenceImpl.getInstance();
    }

    private void checkMarshall( String expected,
                                RuleModel m ) {
        String drl = ruleModelPersistence.marshal( m );
        assertNotNull( drl );
        if ( expected != null ) {
            assertEqualsIgnoreWhitespace( expected, drl );
        }
    }

    @Test
    public void testSimpleBothValues() {
        TemplateModel m = new TemplateModel();
        m.name = "t1";

        FactPattern p = new FactPattern( "Person" );
        SingleFieldConstraint con = new SingleFieldConstraint();
        con.setFieldType( DataType.TYPE_STRING );
        con.setFieldName( "field1" );
        con.setOperator( "==" );
        con.setValue( "$f1" );
        con.setConstraintValueType( SingleFieldConstraint.TYPE_TEMPLATE );
        p.addConstraint( con );

        SingleFieldConstraint con2 = new SingleFieldConstraint();
        con.setFieldType( DataType.TYPE_STRING );
        con2.setFieldName( "field2" );
        con2.setOperator( "==" );
        con2.setValue( "$f2" );
        con2.setConstraintValueType( SingleFieldConstraint.TYPE_TEMPLATE );
        p.addConstraint( con2 );

        m.addLhsItem( p );

        m.addRow( new String[]{ "foo", "bar" } );

        String expected = "rule \"t1_0\"" +
                "dialect \"mvel\"\n" +
                "when \n"
                + "Person( field1 == \"foo\", field2 == \"bar\" )"
                + "then \n"
                + "end";

        checkMarshall( expected,
                       m );
    }

    @Test
    public void testSimpleFirstValue() {
        TemplateModel m = new TemplateModel();
        m.name = "t1";

        FactPattern p = new FactPattern( "Person" );
        SingleFieldConstraint con = new SingleFieldConstraint();
        con.setFieldType( DataType.TYPE_STRING );
        con.setFieldName( "field1" );
        con.setOperator( "==" );
        con.setValue( "$f1" );
        con.setConstraintValueType( SingleFieldConstraint.TYPE_TEMPLATE );
        p.addConstraint( con );

        SingleFieldConstraint con2 = new SingleFieldConstraint();
        con.setFieldType( DataType.TYPE_STRING );
        con2.setFieldName( "field2" );
        con2.setOperator( "==" );
        con2.setValue( "$f2" );
        con2.setConstraintValueType( SingleFieldConstraint.TYPE_TEMPLATE );
        p.addConstraint( con2 );

        m.addLhsItem( p );

        m.addRow( new String[]{ "foo", null } );

        String expected = "rule \"t1_0\"" +
                "dialect \"mvel\"\n" +
                "when \n"
                + "Person( field1 == \"foo\" )"
                + "then \n"
                + "end";

        checkMarshall( expected,
                       m );
    }

    @Test
    public void testSimpleSecondValue() {
        TemplateModel m = new TemplateModel();
        m.name = "t1";

        FactPattern p = new FactPattern( "Person" );
        SingleFieldConstraint con = new SingleFieldConstraint();
        con.setFieldType( DataType.TYPE_STRING );
        con.setFieldName( "field1" );
        con.setOperator( "==" );
        con.setValue( "$f1" );
        con.setConstraintValueType( SingleFieldConstraint.TYPE_TEMPLATE );
        p.addConstraint( con );

        SingleFieldConstraint con2 = new SingleFieldConstraint();
        con.setFieldType( DataType.TYPE_STRING );
        con2.setFieldName( "field2" );
        con2.setOperator( "==" );
        con2.setValue( "$f2" );
        con2.setConstraintValueType( SingleFieldConstraint.TYPE_TEMPLATE );
        p.addConstraint( con2 );

        m.addLhsItem( p );

        m.addRow( new String[]{ null, "bar" } );

        String expected = "rule \"t1_0\"" +
                "dialect \"mvel\"\n" +
                "when \n"
                + "Person( field2 == \"bar\" )"
                + "then \n"
                + "end";

        checkMarshall( expected,
                       m );
    }

    @Test
    public void testCompositeConstraintsBothValues() {
        TemplateModel m = new TemplateModel();
        m.name = "t1";

        FactPattern p1 = new FactPattern( "Person" );
        m.addLhsItem( p1 );

        CompositeFieldConstraint comp = new CompositeFieldConstraint();
        comp.setCompositeJunctionType( CompositeFieldConstraint.COMPOSITE_TYPE_OR );
        p1.addConstraint( comp );

        final SingleFieldConstraint X = new SingleFieldConstraint();
        X.setFieldName( "field1" );
        X.setFieldType( DataType.TYPE_STRING );
        X.setConstraintValueType( SingleFieldConstraint.TYPE_TEMPLATE );
        X.setValue( "$f1" );
        X.setOperator( "==" );
        comp.addConstraint( X );

        final SingleFieldConstraint Y = new SingleFieldConstraint();
        Y.setFieldName( "field2" );
        Y.setFieldType( DataType.TYPE_STRING );
        Y.setConstraintValueType( SingleFieldConstraint.TYPE_TEMPLATE );
        Y.setValue( "$f2" );
        Y.setOperator( "==" );
        comp.addConstraint( Y );

        String expected = "rule \"t1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "Person( field1 == \"foo\" || field2 == \"bar\" )\n" +
                "then\n" +
                "end\n";

        m.addRow( new String[]{ "foo", "bar" } );

        checkMarshall( expected,
                       m );
    }

    @Test
    public void testCompositeConstraintsFirstValue() {
        TemplateModel m = new TemplateModel();
        m.name = "t1";

        FactPattern p1 = new FactPattern( "Person" );
        m.addLhsItem( p1 );

        CompositeFieldConstraint comp = new CompositeFieldConstraint();
        comp.setCompositeJunctionType( CompositeFieldConstraint.COMPOSITE_TYPE_OR );
        p1.addConstraint( comp );

        final SingleFieldConstraint X = new SingleFieldConstraint();
        X.setFieldName( "field1" );
        X.setFieldType( DataType.TYPE_STRING );
        X.setConstraintValueType( SingleFieldConstraint.TYPE_TEMPLATE );
        X.setValue( "$f1" );
        X.setOperator( "==" );
        comp.addConstraint( X );

        final SingleFieldConstraint Y = new SingleFieldConstraint();
        Y.setFieldName( "field2" );
        Y.setFieldType( DataType.TYPE_STRING );
        Y.setConstraintValueType( SingleFieldConstraint.TYPE_TEMPLATE );
        Y.setValue( "$f2" );
        Y.setOperator( "==" );
        comp.addConstraint( Y );

        String expected = "rule \"t1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "Person( field1 == \"foo\" )\n" +
                "then\n" +
                "end\n";

        m.addRow( new String[]{ "foo", null } );

        checkMarshall( expected,
                       m );
    }

    @Test
    public void testCompositeConstraintsSecondValue() {
        TemplateModel m = new TemplateModel();
        m.name = "t1";

        FactPattern p1 = new FactPattern( "Person" );
        m.addLhsItem( p1 );

        CompositeFieldConstraint comp = new CompositeFieldConstraint();
        comp.setCompositeJunctionType( CompositeFieldConstraint.COMPOSITE_TYPE_OR );
        p1.addConstraint( comp );

        final SingleFieldConstraint X = new SingleFieldConstraint();
        X.setFieldName( "field1" );
        X.setFieldType( DataType.TYPE_STRING );
        X.setConstraintValueType( SingleFieldConstraint.TYPE_TEMPLATE );
        X.setValue( "$f1" );
        X.setOperator( "==" );
        comp.addConstraint( X );

        final SingleFieldConstraint Y = new SingleFieldConstraint();
        Y.setFieldName( "field2" );
        Y.setFieldType( DataType.TYPE_STRING );
        Y.setConstraintValueType( SingleFieldConstraint.TYPE_TEMPLATE );
        Y.setValue( "$f2" );
        Y.setOperator( "==" );
        comp.addConstraint( Y );

        String expected = "rule \"t1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "Person( field2 == \"bar\" )\n" +
                "then\n" +
                "end\n";

        m.addRow( new String[]{ null, "bar" } );

        checkMarshall( expected,
                       m );
    }

    @Test
    public void testCompositeConstraintWithConnectiveConstraintBothValues() {
        TemplateModel m = new TemplateModel();
        m.name = "t1";

        FactPattern p1 = new FactPattern( "Person" );
        m.addLhsItem( p1 );

        CompositeFieldConstraint comp = new CompositeFieldConstraint();
        comp.setCompositeJunctionType( CompositeFieldConstraint.COMPOSITE_TYPE_OR );
        p1.addConstraint( comp );

        final SingleFieldConstraint X = new SingleFieldConstraint();
        X.setFieldName( "field1" );
        X.setFieldType( DataType.TYPE_STRING );
        X.setConstraintValueType( SingleFieldConstraint.TYPE_TEMPLATE );
        X.setValue( "$f1" );
        X.setOperator( "==" );
        comp.addConstraint( X );

        ConnectiveConstraint connective = new ConnectiveConstraint();
        connective.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        connective.setFieldType( DataType.TYPE_STRING );
        connective.setOperator( "|| ==" );
        connective.setValue( "goo" );

        X.setConnectives( new ConnectiveConstraint[ 1 ] );
        X.getConnectives()[ 0 ] = connective;

        final SingleFieldConstraint Y = new SingleFieldConstraint();
        Y.setFieldName( "field2" );
        Y.setFieldType( DataType.TYPE_STRING );
        Y.setConstraintValueType( SingleFieldConstraint.TYPE_TEMPLATE );
        Y.setValue( "$f2" );
        Y.setOperator( "==" );
        comp.addConstraint( Y );

        String expected = "rule \"t1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "Person( field1 == \"foo\" || == \"goo\" || field2 == \"bar\" )\n" +
                "then\n" +
                "end\n";

        m.addRow( new String[]{ "foo", "bar" } );

        checkMarshall( expected,
                       m );
    }

    @Test
    public void testCompositeConstraintWithConnectiveConstraintFirstValue() {
        TemplateModel m = new TemplateModel();
        m.name = "t1";

        FactPattern p1 = new FactPattern( "Person" );
        m.addLhsItem( p1 );

        CompositeFieldConstraint comp = new CompositeFieldConstraint();
        comp.setCompositeJunctionType( CompositeFieldConstraint.COMPOSITE_TYPE_OR );
        p1.addConstraint( comp );

        final SingleFieldConstraint X = new SingleFieldConstraint();
        X.setFieldName( "field1" );
        X.setFieldType( DataType.TYPE_STRING );
        X.setConstraintValueType( SingleFieldConstraint.TYPE_TEMPLATE );
        X.setValue( "$f1" );
        X.setOperator( "==" );
        comp.addConstraint( X );

        ConnectiveConstraint connective = new ConnectiveConstraint();
        connective.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        connective.setFieldType( DataType.TYPE_STRING );
        connective.setOperator( "|| ==" );
        connective.setValue( "goo" );

        X.setConnectives( new ConnectiveConstraint[ 1 ] );
        X.getConnectives()[ 0 ] = connective;

        final SingleFieldConstraint Y = new SingleFieldConstraint();
        Y.setFieldName( "field2" );
        Y.setFieldType( DataType.TYPE_STRING );
        Y.setConstraintValueType( SingleFieldConstraint.TYPE_TEMPLATE );
        Y.setValue( "$f2" );
        Y.setOperator( "==" );
        comp.addConstraint( Y );

        String expected = "rule \"t1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "Person( field1 == \"foo\" || == \"goo\" )\n" +
                "then\n" +
                "end\n";

        m.addRow( new String[]{ "foo", null } );

        checkMarshall( expected,
                       m );
    }

    @Test
    public void testCompositeConstraintWithConnectiveConstraintSecondValue() {
        TemplateModel m = new TemplateModel();
        m.name = "t1";

        FactPattern p1 = new FactPattern( "Person" );
        m.addLhsItem( p1 );

        CompositeFieldConstraint comp = new CompositeFieldConstraint();
        comp.setCompositeJunctionType( CompositeFieldConstraint.COMPOSITE_TYPE_OR );
        p1.addConstraint( comp );

        final SingleFieldConstraint X = new SingleFieldConstraint();
        X.setFieldName( "field1" );
        X.setFieldType( DataType.TYPE_STRING );
        X.setConstraintValueType( SingleFieldConstraint.TYPE_TEMPLATE );
        X.setValue( "$f1" );
        X.setOperator( "==" );
        comp.addConstraint( X );

        ConnectiveConstraint connective = new ConnectiveConstraint();
        connective.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        connective.setFieldType( DataType.TYPE_STRING );
        connective.setOperator( "|| ==" );
        connective.setValue( "goo" );

        X.setConnectives( new ConnectiveConstraint[ 1 ] );
        X.getConnectives()[ 0 ] = connective;

        final SingleFieldConstraint Y = new SingleFieldConstraint();
        Y.setFieldName( "field2" );
        Y.setFieldType( DataType.TYPE_STRING );
        Y.setConstraintValueType( SingleFieldConstraint.TYPE_TEMPLATE );
        Y.setValue( "$f2" );
        Y.setOperator( "==" );
        comp.addConstraint( Y );

        String expected = "rule \"t1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "Person( field2 == \"bar\" )\n" +
                "then\n" +
                "end\n";

        m.addRow( new String[]{ null, "bar" } );

        checkMarshall( expected,
                       m );
    }

    @Test
    public void testConnectiveConstraintBothValues() {
        TemplateModel m = new TemplateModel();
        m.name = "t1";

        FactPattern p1 = new FactPattern( "Person" );
        m.addLhsItem( p1 );

        final SingleFieldConstraint X = new SingleFieldConstraint();
        X.setFieldName( "field1" );
        X.setFieldType( DataType.TYPE_STRING );
        X.setConstraintValueType( SingleFieldConstraint.TYPE_TEMPLATE );
        X.setValue( "$f1" );
        X.setOperator( "==" );
        p1.addConstraint( X );

        ConnectiveConstraint connective = new ConnectiveConstraint();
        connective.setConstraintValueType( BaseSingleFieldConstraint.TYPE_TEMPLATE );
        connective.setFieldType( DataType.TYPE_STRING );
        connective.setOperator( "|| ==" );
        connective.setValue( "goo" );

        X.setConnectives( new ConnectiveConstraint[ 1 ] );
        X.getConnectives()[ 0 ] = connective;

        String expected = "rule \"t1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "Person( field1 == \"foo\" || == \"bar\" )\n" +
                "then\n" +
                "end\n";

        m.addRow( new String[]{ "foo", "bar" } );

        checkMarshall( expected,
                       m );
    }

    @Test
    public void testConnectiveConstraintFirstValue() {
        TemplateModel m = new TemplateModel();
        m.name = "t1";

        FactPattern p1 = new FactPattern( "Person" );
        m.addLhsItem( p1 );

        final SingleFieldConstraint X = new SingleFieldConstraint();
        X.setFieldName( "field1" );
        X.setFieldType( DataType.TYPE_STRING );
        X.setConstraintValueType( SingleFieldConstraint.TYPE_TEMPLATE );
        X.setValue( "$f1" );
        X.setOperator( "==" );
        p1.addConstraint( X );

        ConnectiveConstraint connective = new ConnectiveConstraint();
        connective.setConstraintValueType( BaseSingleFieldConstraint.TYPE_TEMPLATE );
        connective.setFieldType( DataType.TYPE_STRING );
        connective.setOperator( "|| ==" );
        connective.setValue( "$f2" );

        X.setConnectives( new ConnectiveConstraint[ 1 ] );
        X.getConnectives()[ 0 ] = connective;

        String expected = "rule \"t1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "Person( field1 == \"foo\" )\n" +
                "then\n" +
                "end\n";

        m.addRow( new String[]{ "foo", null } );

        checkMarshall( expected,
                       m );
    }

    @Test
    public void testConnectiveConstraintSecondValue() {
        TemplateModel m = new TemplateModel();
        m.name = "t1";

        FactPattern p1 = new FactPattern( "Person" );
        m.addLhsItem( p1 );

        final SingleFieldConstraint X = new SingleFieldConstraint();
        X.setFieldName( "field1" );
        X.setFieldType( DataType.TYPE_STRING );
        X.setConstraintValueType( SingleFieldConstraint.TYPE_TEMPLATE );
        X.setValue( "$f1" );
        X.setOperator( "==" );
        p1.addConstraint( X );

        ConnectiveConstraint connective = new ConnectiveConstraint();
        connective.setConstraintValueType( BaseSingleFieldConstraint.TYPE_TEMPLATE );
        connective.setFieldType( DataType.TYPE_STRING );
        connective.setOperator( "|| ==" );
        connective.setValue( "goo" );

        X.setConnectives( new ConnectiveConstraint[ 1 ] );
        X.getConnectives()[ 0 ] = connective;

        String expected = "rule \"t1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "Person(  )\n" +
                "then\n" +
                "end\n";

        m.addRow( new String[]{ null, "bar" } );

        checkMarshall( expected,
                       m );
    }

    private void assertEqualsIgnoreWhitespace( final String expected,
                                               final String actual ) {
        final String cleanExpected = expected.replaceAll( "\\s+",
                                                          "" );
        final String cleanActual = actual.replaceAll( "\\s+",
                                                      "" );

        assertEquals( cleanExpected, cleanActual );
    }

}
