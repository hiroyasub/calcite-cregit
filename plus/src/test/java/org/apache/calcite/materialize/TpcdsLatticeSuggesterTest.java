begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|materialize
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|adapter
operator|.
name|tpcds
operator|.
name|TpcdsSchema
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|config
operator|.
name|CalciteConnectionConfigImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|config
operator|.
name|CalciteConnectionProperty
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|config
operator|.
name|CalciteSystemProperty
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|plan
operator|.
name|Contexts
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|prepare
operator|.
name|PlannerImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|rel
operator|.
name|RelRoot
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|schema
operator|.
name|SchemaPlus
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|sql
operator|.
name|SqlNode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|sql
operator|.
name|parser
operator|.
name|SqlParseException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|sql
operator|.
name|parser
operator|.
name|SqlParser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|sql
operator|.
name|validate
operator|.
name|SqlConformanceEnum
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|test
operator|.
name|CalciteAssert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|tools
operator|.
name|FrameworkConfig
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|tools
operator|.
name|Frameworks
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|tools
operator|.
name|Planner
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|tools
operator|.
name|RelConversionException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|tools
operator|.
name|ValidationException
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|tpcds
operator|.
name|query
operator|.
name|Query
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|jupiter
operator|.
name|api
operator|.
name|Disabled
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|jupiter
operator|.
name|api
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
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
name|is
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|MatcherAssert
operator|.
name|assertThat
import|;
end_import

begin_comment
comment|/**  * Unit tests for {@link LatticeSuggester}.  */
end_comment

begin_class
specifier|public
class|class
name|TpcdsLatticeSuggesterTest
block|{
specifier|private
name|String
name|number
parameter_list|(
name|String
name|s
parameter_list|)
block|{
specifier|final
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|line
range|:
name|s
operator|.
name|split
argument_list|(
literal|"\n"
argument_list|)
control|)
block|{
name|b
operator|.
name|append
argument_list|(
operator|++
name|i
argument_list|)
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
operator|.
name|append
argument_list|(
name|line
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
return|return
name|b
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|private
name|void
name|checkFoodMartAll
parameter_list|(
name|boolean
name|evolve
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|Tester
name|t
init|=
operator|new
name|Tester
argument_list|()
operator|.
name|tpcds
argument_list|()
operator|.
name|withEvolve
argument_list|(
name|evolve
argument_list|)
decl_stmt|;
for|for
control|(
name|Query
name|query
range|:
name|Query
operator|.
name|values
argument_list|()
control|)
block|{
specifier|final
name|String
name|sql
init|=
name|query
operator|.
name|sql
argument_list|(
operator|new
name|Random
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|replaceAll
argument_list|(
literal|"as returns"
argument_list|,
literal|"as \"returns\""
argument_list|)
operator|.
name|replaceAll
argument_list|(
literal|"sum\\(returns\\)"
argument_list|,
literal|"sum(\"returns\")"
argument_list|)
operator|.
name|replaceAll
argument_list|(
literal|", returns"
argument_list|,
literal|", \"returns\""
argument_list|)
operator|.
name|replaceAll
argument_list|(
literal|"14 days"
argument_list|,
literal|"interval '14' day"
argument_list|)
operator|.
name|replaceAll
argument_list|(
literal|"substr\\(([^,]*),([^,]*),([^)]*)\\)"
argument_list|,
literal|"substring($1 from $2 for $3)"
argument_list|)
decl_stmt|;
if|if
condition|(
name|CalciteSystemProperty
operator|.
name|DEBUG
operator|.
name|value
argument_list|()
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Query #"
operator|+
name|query
operator|.
name|id
operator|+
literal|"\n"
operator|+
name|number
argument_list|(
name|sql
argument_list|)
argument_list|)
expr_stmt|;
block|}
switch|switch
condition|(
name|query
operator|.
name|id
condition|)
block|{
case|case
literal|6
case|:
case|case
literal|9
case|:
continue|continue;
comment|// NPE
block|}
if|if
condition|(
name|query
operator|.
name|id
operator|>
literal|11
condition|)
block|{
break|break;
block|}
name|t
operator|.
name|addQuery
argument_list|(
name|sql
argument_list|)
expr_stmt|;
block|}
comment|// The graph of all tables and hops
specifier|final
name|String
name|expected
init|=
literal|"graph(vertices: ["
operator|+
literal|"[tpcds, CATALOG_SALES], "
operator|+
literal|"[tpcds, CUSTOMER], "
operator|+
literal|"[tpcds, CUSTOMER_ADDRESS], "
operator|+
literal|"[tpcds, CUSTOMER_DEMOGRAPHICS], "
operator|+
literal|"[tpcds, DATE_DIM], "
operator|+
literal|"[tpcds, ITEM], "
operator|+
literal|"[tpcds, PROMOTION], "
operator|+
literal|"[tpcds, STORE], "
operator|+
literal|"[tpcds, STORE_RETURNS], "
operator|+
literal|"[tpcds, STORE_SALES], "
operator|+
literal|"[tpcds, WEB_SALES]], "
operator|+
literal|"edges: "
operator|+
literal|"[Step([tpcds, CATALOG_SALES], [tpcds, CUSTOMER], CS_SHIP_CUSTOMER_SK:C_CUSTOMER_SK),"
operator|+
literal|" Step([tpcds, CATALOG_SALES], [tpcds, DATE_DIM], CS_SOLD_DATE_SK:D_DATE_SK),"
operator|+
literal|" Step([tpcds, STORE_RETURNS], [tpcds, CUSTOMER], SR_CUSTOMER_SK:C_CUSTOMER_SK),"
operator|+
literal|" Step([tpcds, STORE_RETURNS], [tpcds, DATE_DIM], SR_RETURNED_DATE_SK:D_DATE_SK),"
operator|+
literal|" Step([tpcds, STORE_RETURNS], [tpcds, STORE], SR_STORE_SK:S_STORE_SK),"
operator|+
literal|" Step([tpcds, STORE_RETURNS], [tpcds, STORE_RETURNS], SR_STORE_SK:SR_STORE_SK),"
operator|+
literal|" Step([tpcds, STORE_SALES], [tpcds, CUSTOMER], SS_CUSTOMER_SK:C_CUSTOMER_SK),"
operator|+
literal|" Step([tpcds, STORE_SALES], [tpcds, CUSTOMER_DEMOGRAPHICS], SS_CDEMO_SK:CD_DEMO_SK),"
operator|+
literal|" Step([tpcds, STORE_SALES], [tpcds, DATE_DIM], SS_SOLD_DATE_SK:D_DATE_SK),"
operator|+
literal|" Step([tpcds, STORE_SALES], [tpcds, ITEM], SS_ITEM_SK:I_ITEM_SK),"
operator|+
literal|" Step([tpcds, STORE_SALES], [tpcds, PROMOTION], SS_PROMO_SK:P_PROMO_SK),"
operator|+
literal|" Step([tpcds, WEB_SALES], [tpcds, CUSTOMER], WS_BILL_CUSTOMER_SK:C_CUSTOMER_SK),"
operator|+
literal|" Step([tpcds, WEB_SALES], [tpcds, DATE_DIM], WS_SOLD_DATE_SK:D_DATE_SK)])"
decl_stmt|;
name|assertThat
argument_list|(
name|t
operator|.
name|suggester
operator|.
name|space
operator|.
name|g
operator|.
name|toString
argument_list|()
argument_list|,
name|is
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|evolve
condition|)
block|{
name|assertThat
argument_list|(
name|t
operator|.
name|suggester
operator|.
name|space
operator|.
name|nodeMap
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|t
operator|.
name|suggester
operator|.
name|latticeMap
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|t
operator|.
name|suggester
operator|.
name|space
operator|.
name|pathMap
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
literal|10
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertThat
argument_list|(
name|t
operator|.
name|suggester
operator|.
name|space
operator|.
name|nodeMap
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|t
operator|.
name|suggester
operator|.
name|latticeMap
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|t
operator|.
name|suggester
operator|.
name|space
operator|.
name|pathMap
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
literal|10
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Disabled
argument_list|(
literal|"Throws NPE with both Maven and Gradle"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testTpcdsAll
parameter_list|()
throws|throws
name|Exception
block|{
name|checkFoodMartAll
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Disabled
argument_list|(
literal|"Throws NPE with both Maven and Gradle"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testTpcdsAllEvolve
parameter_list|()
throws|throws
name|Exception
block|{
name|checkFoodMartAll
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/** Test helper. */
specifier|private
specifier|static
class|class
name|Tester
block|{
specifier|final
name|LatticeSuggester
name|suggester
decl_stmt|;
specifier|private
specifier|final
name|FrameworkConfig
name|config
decl_stmt|;
name|Tester
parameter_list|()
block|{
name|this
argument_list|(
name|config
argument_list|(
name|CalciteAssert
operator|.
name|SchemaSpec
operator|.
name|BLANK
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|Tester
parameter_list|(
name|FrameworkConfig
name|config
parameter_list|)
block|{
name|this
operator|.
name|config
operator|=
name|config
expr_stmt|;
name|suggester
operator|=
operator|new
name|LatticeSuggester
argument_list|(
name|config
argument_list|)
expr_stmt|;
block|}
name|Tester
name|tpcds
parameter_list|()
block|{
specifier|final
name|SchemaPlus
name|rootSchema
init|=
name|Frameworks
operator|.
name|createRootSchema
argument_list|(
literal|true
argument_list|)
decl_stmt|;
specifier|final
name|double
name|scaleFactor
init|=
literal|0.01d
decl_stmt|;
specifier|final
name|SchemaPlus
name|schema
init|=
name|rootSchema
operator|.
name|add
argument_list|(
literal|"tpcds"
argument_list|,
operator|new
name|TpcdsSchema
argument_list|(
name|scaleFactor
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|FrameworkConfig
name|config
init|=
name|Frameworks
operator|.
name|newConfigBuilder
argument_list|()
operator|.
name|parserConfig
argument_list|(
name|SqlParser
operator|.
name|Config
operator|.
name|DEFAULT
argument_list|)
operator|.
name|context
argument_list|(
name|Contexts
operator|.
name|of
argument_list|(
operator|new
name|CalciteConnectionConfigImpl
argument_list|(
operator|new
name|Properties
argument_list|()
argument_list|)
operator|.
name|set
argument_list|(
name|CalciteConnectionProperty
operator|.
name|CONFORMANCE
argument_list|,
name|SqlConformanceEnum
operator|.
name|LENIENT
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
argument_list|)
operator|.
name|defaultSchema
argument_list|(
name|schema
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
return|return
name|withConfig
argument_list|(
name|config
argument_list|)
return|;
block|}
name|Tester
name|withConfig
parameter_list|(
name|FrameworkConfig
name|config
parameter_list|)
block|{
return|return
operator|new
name|Tester
argument_list|(
name|config
argument_list|)
return|;
block|}
name|List
argument_list|<
name|Lattice
argument_list|>
name|addQuery
parameter_list|(
name|String
name|q
parameter_list|)
throws|throws
name|SqlParseException
throws|,
name|ValidationException
throws|,
name|RelConversionException
block|{
specifier|final
name|Planner
name|planner
init|=
operator|new
name|PlannerImpl
argument_list|(
name|config
argument_list|)
decl_stmt|;
specifier|final
name|SqlNode
name|node
init|=
name|planner
operator|.
name|parse
argument_list|(
name|q
argument_list|)
decl_stmt|;
specifier|final
name|SqlNode
name|node2
init|=
name|planner
operator|.
name|validate
argument_list|(
name|node
argument_list|)
decl_stmt|;
specifier|final
name|RelRoot
name|root
init|=
name|planner
operator|.
name|rel
argument_list|(
name|node2
argument_list|)
decl_stmt|;
return|return
name|suggester
operator|.
name|addQuery
argument_list|(
name|root
operator|.
name|project
argument_list|()
argument_list|)
return|;
block|}
comment|/** Parses a query returns its graph. */
name|LatticeRootNode
name|node
parameter_list|(
name|String
name|q
parameter_list|)
throws|throws
name|SqlParseException
throws|,
name|ValidationException
throws|,
name|RelConversionException
block|{
specifier|final
name|List
argument_list|<
name|Lattice
argument_list|>
name|list
init|=
name|addQuery
argument_list|(
name|q
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|list
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|rootNode
return|;
block|}
specifier|static
name|Frameworks
operator|.
name|ConfigBuilder
name|config
parameter_list|(
name|CalciteAssert
operator|.
name|SchemaSpec
name|spec
parameter_list|)
block|{
specifier|final
name|SchemaPlus
name|rootSchema
init|=
name|Frameworks
operator|.
name|createRootSchema
argument_list|(
literal|true
argument_list|)
decl_stmt|;
specifier|final
name|SchemaPlus
name|schema
init|=
name|CalciteAssert
operator|.
name|addSchema
argument_list|(
name|rootSchema
argument_list|,
name|spec
argument_list|)
decl_stmt|;
return|return
name|Frameworks
operator|.
name|newConfigBuilder
argument_list|()
operator|.
name|parserConfig
argument_list|(
name|SqlParser
operator|.
name|Config
operator|.
name|DEFAULT
argument_list|)
operator|.
name|defaultSchema
argument_list|(
name|schema
argument_list|)
return|;
block|}
name|Tester
name|withEvolve
parameter_list|(
name|boolean
name|evolve
parameter_list|)
block|{
if|if
condition|(
name|evolve
operator|==
name|config
operator|.
name|isEvolveLattice
argument_list|()
condition|)
block|{
return|return
name|this
return|;
block|}
specifier|final
name|Frameworks
operator|.
name|ConfigBuilder
name|configBuilder
init|=
name|Frameworks
operator|.
name|newConfigBuilder
argument_list|(
name|config
argument_list|)
decl_stmt|;
return|return
operator|new
name|Tester
argument_list|(
name|configBuilder
operator|.
name|evolveLattice
argument_list|(
literal|true
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End TpcdsLatticeSuggesterTest.java
end_comment

end_unit

