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
import org.drools.workbench.models.datamodel.rule.FreeFormLine;
import org.drools.workbench.models.datamodel.rule.FromCollectCompositeFactPattern;
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
    public void testSimpleSingleValue() {
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

        m.addLhsItem( p );

        m.addRow( new String[]{ "foo" } );

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
    public void testSimpleSingleTemplateValueSingleLiteralValue() {
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
        con2.setValue( "bar" );
        con2.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        p.addConstraint( con2 );

        m.addLhsItem( p );

        m.addRow( new String[]{ "foo" } );

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
        connective.setValue( "$f2" );

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
        connective.setValue( "$f2" );

        X.setConnectives( new ConnectiveConstraint[ 1 ] );
        X.getConnectives()[ 0 ] = connective;

        String expected = "rule \"t1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "Person( field1 == \"bar\" )\n" +
                "then\n" +
                "end\n";

        m.addRow( new String[]{ null, "bar" } );

        checkMarshall( expected,
                       m );
    }

    @Test
    public void testSimpleFromCollect() {
        TemplateModel m = new TemplateModel();
        m.name = "r1";

        FactPattern fp = new FactPattern( "Person" );

        SingleFieldConstraint sfc = new SingleFieldConstraint( "field1" );
        sfc.setConstraintValueType( SingleFieldConstraint.TYPE_TEMPLATE );
        sfc.setFieldType( DataType.TYPE_STRING );
        sfc.setOperator( "==" );
        sfc.setValue( "$f1" );

        fp.addConstraint( sfc );

        FromCollectCompositeFactPattern fac = new FromCollectCompositeFactPattern();
        fac.setRightPattern( fp );
        fac.setFactPattern( new FactPattern( "java.util.List" ) );
        m.addLhsItem( fac );

        String expected = "rule \"r1_0\"\n"
                + "dialect \"mvel\"\n"
                + "when\n"
                + "java.util.List( ) from collect ( Person( field1 == \"foo\" ) ) \n"
                + "then\n"
                + "end";

        m.addRow( new String[]{ "foo" } );

        checkMarshall( expected,
                       m );
    }

    @Test
    public void testSimpleFromCollectBothValues() {
        TemplateModel m = new TemplateModel();
        m.name = "r1";

        FactPattern fp = new FactPattern( "Person" );
        SingleFieldConstraint sfc = new SingleFieldConstraint( "field1" );
        sfc.setConstraintValueType( SingleFieldConstraint.TYPE_TEMPLATE );
        sfc.setFieldType( DataType.TYPE_STRING );
        sfc.setOperator( "==" );
        sfc.setValue( "$f1" );
        fp.addConstraint( sfc );

        FactPattern fp2 = new FactPattern( "java.util.List" );
        SingleFieldConstraint sfc2 = new SingleFieldConstraint( "size" );
        sfc2.setConstraintValueType( SingleFieldConstraint.TYPE_TEMPLATE );
        sfc2.setFieldType( DataType.TYPE_NUMERIC_INTEGER );
        sfc2.setOperator( ">" );
        sfc2.setValue( "$f2" );
        fp2.addConstraint( sfc2 );

        FromCollectCompositeFactPattern fac = new FromCollectCompositeFactPattern();
        fac.setRightPattern( fp );
        fac.setFactPattern( fp2 );
        m.addLhsItem( fac );

        String expected = "rule \"r1_0\"\n"
                + "dialect \"mvel\"\n"
                + "when\n"
                + "java.util.List( size > 1 ) from collect ( Person( field1 == \"foo\" ) ) \n"
                + "then\n"
                + "end";

        m.addRow( new String[]{ "1", "foo" } );

        checkMarshall( expected,
                       m );
    }

    @Test
    public void testSimpleFromCollectFirstValue() {
        TemplateModel m = new TemplateModel();
        m.name = "r1";

        FactPattern fp = new FactPattern( "Person" );
        SingleFieldConstraint sfc = new SingleFieldConstraint( "field1" );
        sfc.setConstraintValueType( SingleFieldConstraint.TYPE_TEMPLATE );
        sfc.setFieldType( DataType.TYPE_STRING );
        sfc.setOperator( "==" );
        sfc.setValue( "$f1" );
        fp.addConstraint( sfc );

        FactPattern fp2 = new FactPattern( "java.util.List" );
        SingleFieldConstraint sfc2 = new SingleFieldConstraint( "size" );
        sfc2.setConstraintValueType( SingleFieldConstraint.TYPE_TEMPLATE );
        sfc2.setFieldType( DataType.TYPE_NUMERIC_INTEGER );
        sfc2.setOperator( ">" );
        sfc2.setValue( "$f2" );
        fp2.addConstraint( sfc2 );

        FromCollectCompositeFactPattern fac = new FromCollectCompositeFactPattern();
        fac.setRightPattern( fp );
        fac.setFactPattern( fp2 );
        m.addLhsItem( fac );

        String expected = "rule \"r1_0\"\n"
                + "dialect \"mvel\"\n"
                + "when\n"
                + "java.util.List( size > 1 ) from collect ( Person( ) )\n"
                + "then\n"
                + "end";

        m.addRow( new String[]{ "1", null } );

        checkMarshall( expected,
                       m );
    }

    @Test
    public void testSimpleFromCollectSecondValue() {
        TemplateModel m = new TemplateModel();
        m.name = "r1";

        FactPattern fp = new FactPattern( "Person" );
        SingleFieldConstraint sfc = new SingleFieldConstraint( "field1" );
        sfc.setConstraintValueType( SingleFieldConstraint.TYPE_TEMPLATE );
        sfc.setFieldType( DataType.TYPE_STRING );
        sfc.setOperator( "==" );
        sfc.setValue( "$f1" );
        fp.addConstraint( sfc );

        FactPattern fp2 = new FactPattern( "java.util.List" );
        SingleFieldConstraint sfc2 = new SingleFieldConstraint( "size" );
        sfc2.setConstraintValueType( SingleFieldConstraint.TYPE_TEMPLATE );
        sfc2.setFieldType( DataType.TYPE_NUMERIC_INTEGER );
        sfc2.setOperator( ">" );
        sfc2.setValue( "$f2" );
        fp2.addConstraint( sfc2 );

        FromCollectCompositeFactPattern fac = new FromCollectCompositeFactPattern();
        fac.setRightPattern( fp );
        fac.setFactPattern( fp2 );
        m.addLhsItem( fac );

        String expected = "rule \"r1_0\"\n"
                + "dialect \"mvel\"\n"
                + "when\n"
                + "java.util.List() from collect ( Person( field1 == \"foo\" ) )"
                + "then\n"
                + "end";

        m.addRow( new String[]{ null, "foo" } );

        checkMarshall( expected,
                       m );
    }

    @Test
    public void testSimpleFromCollectMultipleSubPatternValues() {
        TemplateModel m = new TemplateModel();
        m.name = "r1";

        FactPattern fp = new FactPattern( "Person" );
        SingleFieldConstraint sfc = new SingleFieldConstraint( "field1" );
        sfc.setConstraintValueType( SingleFieldConstraint.TYPE_TEMPLATE );
        sfc.setFieldType( DataType.TYPE_STRING );
        sfc.setOperator( "==" );
        sfc.setValue( "$f1" );
        fp.addConstraint( sfc );

        SingleFieldConstraint sfc1 = new SingleFieldConstraint( "field2" );
        sfc1.setConstraintValueType( SingleFieldConstraint.TYPE_TEMPLATE );
        sfc1.setFieldType( DataType.TYPE_STRING );
        sfc1.setOperator( "==" );
        sfc1.setValue( "$f2" );
        fp.addConstraint( sfc1 );

        FactPattern fp2 = new FactPattern( "java.util.List" );
        SingleFieldConstraint sfc2 = new SingleFieldConstraint( "size" );
        sfc2.setConstraintValueType( SingleFieldConstraint.TYPE_TEMPLATE );
        sfc2.setFieldType( DataType.TYPE_NUMERIC_INTEGER );
        sfc2.setOperator( ">" );
        sfc2.setValue( "$f3" );
        fp2.addConstraint( sfc2 );

        FromCollectCompositeFactPattern fac = new FromCollectCompositeFactPattern();
        fac.setRightPattern( fp );
        fac.setFactPattern( fp2 );
        m.addLhsItem( fac );

        String expected = "rule \"r1_0\"\n"
                + "dialect \"mvel\"\n"
                + "when\n"
                + "java.util.List( size > 1 ) from collect ( Person( field1 == \"foo\", field2 == \"bar\" ) ) \n"
                + "then\n"
                + "end";

        m.addRow( new String[]{ "1", "foo", "bar" } );

        checkMarshall( expected,
                       m );
    }

    @Test
    public void testSimpleFromCollectMultipleSubPatternValuesFirstValue() {
        TemplateModel m = new TemplateModel();
        m.name = "r1";

        FactPattern fp = new FactPattern( "Person" );
        SingleFieldConstraint sfc = new SingleFieldConstraint( "field1" );
        sfc.setConstraintValueType( SingleFieldConstraint.TYPE_TEMPLATE );
        sfc.setFieldType( DataType.TYPE_STRING );
        sfc.setOperator( "==" );
        sfc.setValue( "$f1" );
        fp.addConstraint( sfc );

        SingleFieldConstraint sfc1 = new SingleFieldConstraint( "field2" );
        sfc1.setConstraintValueType( SingleFieldConstraint.TYPE_TEMPLATE );
        sfc1.setFieldType( DataType.TYPE_STRING );
        sfc1.setOperator( "==" );
        sfc1.setValue( "$f2" );
        fp.addConstraint( sfc1 );

        FactPattern fp2 = new FactPattern( "java.util.List" );
        SingleFieldConstraint sfc2 = new SingleFieldConstraint( "size" );
        sfc2.setConstraintValueType( SingleFieldConstraint.TYPE_TEMPLATE );
        sfc2.setFieldType( DataType.TYPE_NUMERIC_INTEGER );
        sfc2.setOperator( ">" );
        sfc2.setValue( "$f3" );
        fp2.addConstraint( sfc2 );

        FromCollectCompositeFactPattern fac = new FromCollectCompositeFactPattern();
        fac.setRightPattern( fp );
        fac.setFactPattern( fp2 );
        m.addLhsItem( fac );

        String expected = "rule \"r1_0\"\n"
                + "dialect \"mvel\"\n"
                + "when\n"
                + "java.util.List( size > 1 ) from collect ( Person( field1 == \"foo\" ) ) \n"
                + "then\n"
                + "end";

        m.addRow( new String[]{ "1", "foo", null } );

        checkMarshall( expected,
                       m );
    }

    @Test
    public void testSimpleFromCollectMultipleSubPatternValuesSecondValue() {
        TemplateModel m = new TemplateModel();
        m.name = "r1";

        FactPattern fp = new FactPattern( "Person" );
        SingleFieldConstraint sfc = new SingleFieldConstraint( "field1" );
        sfc.setConstraintValueType( SingleFieldConstraint.TYPE_TEMPLATE );
        sfc.setFieldType( DataType.TYPE_STRING );
        sfc.setOperator( "==" );
        sfc.setValue( "$f1" );
        fp.addConstraint( sfc );

        SingleFieldConstraint sfc1 = new SingleFieldConstraint( "field2" );
        sfc1.setConstraintValueType( SingleFieldConstraint.TYPE_TEMPLATE );
        sfc1.setFieldType( DataType.TYPE_STRING );
        sfc1.setOperator( "==" );
        sfc1.setValue( "$f2" );
        fp.addConstraint( sfc1 );

        FactPattern fp2 = new FactPattern( "java.util.List" );
        SingleFieldConstraint sfc2 = new SingleFieldConstraint( "size" );
        sfc2.setConstraintValueType( SingleFieldConstraint.TYPE_TEMPLATE );
        sfc2.setFieldType( DataType.TYPE_NUMERIC_INTEGER );
        sfc2.setOperator( ">" );
        sfc2.setValue( "$f3" );
        fp2.addConstraint( sfc2 );

        FromCollectCompositeFactPattern fac = new FromCollectCompositeFactPattern();
        fac.setRightPattern( fp );
        fac.setFactPattern( fp2 );
        m.addLhsItem( fac );

        String expected = "rule \"r1_0\"\n"
                + "dialect \"mvel\"\n"
                + "when\n"
                + "java.util.List( size > 1 ) from collect ( Person( field2 == \"bar\" ) ) \n"
                + "then\n"
                + "end";

        m.addRow( new String[]{ "1", null, "bar" } );

        checkMarshall( expected,
                       m );
    }

    @Test
    public void testFreeFormLineBothValues() {
        TemplateModel m = new TemplateModel();
        m.name = "r1";

        FreeFormLine ffl = new FreeFormLine();
        ffl.setText( "Person( field1 == \"@{f1}\", field2 == \"@{f2}\" )" );
        m.addLhsItem( ffl );

        String expected = "rule \"r1_0\"\n"
                + "dialect \"mvel\"\n"
                + "when\n"
                + "Person( field1 == \"foo\", field2 == \"bar\" ) \n"
                + "then\n"
                + "end";

        m.addRow( new String[]{ "foo", "bar" } );

        checkMarshall( expected,
                       m );
    }

    @Test
    public void testFreeFormLineFirstValue() {
        TemplateModel m = new TemplateModel();
        m.name = "r1";

        FreeFormLine ffl = new FreeFormLine();
        ffl.setText( "Person( field1 == \"@{f1}\", field2 == \"@{f2}\" )" );
        m.addLhsItem( ffl );

        String expected = "rule \"r1_0\"\n"
                + "dialect \"mvel\"\n"
                + "when\n"
                + "then\n"
                + "end";

        m.addRow( new String[]{ "foo", null } );

        checkMarshall( expected,
                       m );
    }

    @Test
    public void testFreeFormLineSecondValue() {
        TemplateModel m = new TemplateModel();
        m.name = "r1";

        FreeFormLine ffl = new FreeFormLine();
        ffl.setText( "Person( field1 == \"@{f1}\", field2 == \"@{f2}\" )" );
        m.addLhsItem( ffl );

        String expected = "rule \"r1_0\"\n"
                + "dialect \"mvel\"\n"
                + "when\n"
                + "then\n"
                + "end";

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
