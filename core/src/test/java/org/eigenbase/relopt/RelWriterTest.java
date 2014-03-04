begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|relopt
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|math
operator|.
name|BigDecimal
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|rel
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|reltype
operator|.
name|RelDataType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|rex
operator|.
name|RexBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|SqlExplainLevel
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|fun
operator|.
name|SqlStdOperatorTable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|type
operator|.
name|SqlTypeName
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|util
operator|.
name|Util
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|SchemaPlus
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|impl
operator|.
name|java
operator|.
name|ReflectiveSchema
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|test
operator|.
name|JdbcTest
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|tools
operator|.
name|Frameworks
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|util
operator|.
name|BitSets
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertThat
import|;
end_import

begin_comment
comment|/**  * Unit test for {@link org.eigenbase.rel.RelJson}.  */
end_comment

begin_class
specifier|public
class|class
name|RelWriterTest
block|{
specifier|public
specifier|static
specifier|final
name|String
name|XX
init|=
literal|"{\n"
operator|+
literal|"  rels: [\n"
operator|+
literal|"    {\n"
operator|+
literal|"      id: \"0\",\n"
operator|+
literal|"      relOp: \"TableAccessRel\",\n"
operator|+
literal|"      table: [\n"
operator|+
literal|"        \"hr\",\n"
operator|+
literal|"        \"emps\"\n"
operator|+
literal|"      ],\n"
operator|+
literal|"      inputs: []\n"
operator|+
literal|"    },\n"
operator|+
literal|"    {\n"
operator|+
literal|"      id: \"1\",\n"
operator|+
literal|"      relOp: \"FilterRel\",\n"
operator|+
literal|"      condition: {\n"
operator|+
literal|"        op: \"=\",\n"
operator|+
literal|"        operands: [\n"
operator|+
literal|"          {\n"
operator|+
literal|"            input: 1\n"
operator|+
literal|"          },\n"
operator|+
literal|"          10\n"
operator|+
literal|"        ]\n"
operator|+
literal|"      }\n"
operator|+
literal|"    },\n"
operator|+
literal|"    {\n"
operator|+
literal|"      id: \"2\",\n"
operator|+
literal|"      relOp: \"AggregateRel\",\n"
operator|+
literal|"      group: [\n"
operator|+
literal|"        0\n"
operator|+
literal|"      ],\n"
operator|+
literal|"      aggs: [\n"
operator|+
literal|"        {\n"
operator|+
literal|"          agg: \"COUNT\",\n"
operator|+
literal|"          type: {\n"
operator|+
literal|"            type: \"INTEGER\",\n"
operator|+
literal|"            nullable: false\n"
operator|+
literal|"          },\n"
operator|+
literal|"          distinct: true,\n"
operator|+
literal|"          operands: [\n"
operator|+
literal|"            1\n"
operator|+
literal|"          ]\n"
operator|+
literal|"        },\n"
operator|+
literal|"        {\n"
operator|+
literal|"          agg: \"COUNT\",\n"
operator|+
literal|"          type: {\n"
operator|+
literal|"            type: \"INTEGER\",\n"
operator|+
literal|"            nullable: false\n"
operator|+
literal|"          },\n"
operator|+
literal|"          distinct: false,\n"
operator|+
literal|"          operands: []\n"
operator|+
literal|"        }\n"
operator|+
literal|"      ]\n"
operator|+
literal|"    }\n"
operator|+
literal|"  ]\n"
operator|+
literal|"}"
decl_stmt|;
comment|/**    * Unit test for {@link org.eigenbase.rel.RelJsonWriter} on    * a simple tree of relational expressions, consisting of a table, a filter    * and an aggregate node.    */
annotation|@
name|Test
specifier|public
name|void
name|testWriter
parameter_list|()
block|{
name|String
name|s
init|=
name|Frameworks
operator|.
name|withPlanner
argument_list|(
operator|new
name|Frameworks
operator|.
name|PlannerAction
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
specifier|public
name|String
name|apply
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelOptSchema
name|relOptSchema
parameter_list|,
name|SchemaPlus
name|rootSchema
parameter_list|)
block|{
name|rootSchema
operator|.
name|add
argument_list|(
operator|new
name|ReflectiveSchema
argument_list|(
name|rootSchema
argument_list|,
literal|"hr"
argument_list|,
operator|new
name|JdbcTest
operator|.
name|HrSchema
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|TableAccessRel
name|table
init|=
operator|new
name|TableAccessRel
argument_list|(
name|cluster
argument_list|,
name|relOptSchema
operator|.
name|getTableForMember
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"hr"
argument_list|,
literal|"emps"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|RexBuilder
name|rexBuilder
init|=
name|cluster
operator|.
name|getRexBuilder
argument_list|()
decl_stmt|;
name|FilterRel
name|filter
init|=
operator|new
name|FilterRel
argument_list|(
name|cluster
argument_list|,
name|table
argument_list|,
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|EQUALS
argument_list|,
name|rexBuilder
operator|.
name|makeFieldAccess
argument_list|(
name|rexBuilder
operator|.
name|makeRangeReference
argument_list|(
name|table
argument_list|)
argument_list|,
literal|"deptno"
argument_list|,
literal|true
argument_list|)
argument_list|,
name|rexBuilder
operator|.
name|makeExactLiteral
argument_list|(
name|BigDecimal
operator|.
name|TEN
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|RelJsonWriter
name|writer
init|=
operator|new
name|RelJsonWriter
argument_list|()
decl_stmt|;
specifier|final
name|RelDataType
name|intType
init|=
name|cluster
operator|.
name|getTypeFactory
argument_list|()
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|INTEGER
argument_list|)
decl_stmt|;
name|AggregateRel
name|aggregate
init|=
operator|new
name|AggregateRel
argument_list|(
name|cluster
argument_list|,
name|filter
argument_list|,
name|BitSets
operator|.
name|of
argument_list|(
literal|0
argument_list|)
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
operator|new
name|AggregateCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|COUNT
argument_list|,
literal|true
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
literal|1
argument_list|)
argument_list|,
name|intType
argument_list|,
literal|"c"
argument_list|)
argument_list|,
operator|new
name|AggregateCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|COUNT
argument_list|,
literal|false
argument_list|,
name|ImmutableList
operator|.
expr|<
name|Integer
operator|>
name|of
argument_list|()
argument_list|,
name|intType
argument_list|,
literal|"d"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|aggregate
operator|.
name|explain
argument_list|(
name|writer
argument_list|)
expr_stmt|;
return|return
name|writer
operator|.
name|asString
argument_list|()
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|s
argument_list|,
name|is
argument_list|(
name|XX
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Unit test for {@link org.eigenbase.rel.RelJsonReader}.    */
annotation|@
name|Test
specifier|public
name|void
name|testReader
parameter_list|()
block|{
name|String
name|s
init|=
name|Frameworks
operator|.
name|withPlanner
argument_list|(
operator|new
name|Frameworks
operator|.
name|PlannerAction
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
specifier|public
name|String
name|apply
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelOptSchema
name|relOptSchema
parameter_list|,
name|SchemaPlus
name|rootSchema
parameter_list|)
block|{
name|SchemaPlus
name|schema
init|=
name|rootSchema
operator|.
name|add
argument_list|(
operator|new
name|ReflectiveSchema
argument_list|(
name|rootSchema
argument_list|,
literal|"hr"
argument_list|,
operator|new
name|JdbcTest
operator|.
name|HrSchema
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|RelJsonReader
name|reader
init|=
operator|new
name|RelJsonReader
argument_list|(
name|cluster
argument_list|,
name|relOptSchema
argument_list|,
name|schema
argument_list|)
decl_stmt|;
name|RelNode
name|node
decl_stmt|;
try|try
block|{
name|node
operator|=
name|reader
operator|.
name|read
argument_list|(
name|XX
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
return|return
name|RelOptUtil
operator|.
name|dumpPlan
argument_list|(
literal|""
argument_list|,
name|node
argument_list|,
literal|false
argument_list|,
name|SqlExplainLevel
operator|.
name|EXPPLAN_ATTRIBUTES
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|Util
operator|.
name|toLinux
argument_list|(
name|s
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"AggregateRel(group=[{0}], agg#0=[COUNT(DISTINCT $1)], agg#1=[COUNT()])\n"
operator|+
literal|"  FilterRel(condition=[=($1, 10)])\n"
operator|+
literal|"    TableAccessRel(table=[[hr, emps]])\n"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End RelWriterTest.java
end_comment

end_unit

