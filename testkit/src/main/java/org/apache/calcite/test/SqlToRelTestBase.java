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
name|test
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
name|plan
operator|.
name|RelOptCluster
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
name|RelTraitSet
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
name|RelNode
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
name|RelShuttle
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
name|core
operator|.
name|Correlate
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
name|core
operator|.
name|CorrelationId
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
name|core
operator|.
name|JoinRelType
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
name|hint
operator|.
name|RelHint
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
name|catalog
operator|.
name|MockCatalogReader
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
name|util
operator|.
name|ImmutableBitSet
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

begin_comment
comment|/**  * SqlToRelTestBase is an abstract base for tests which involve conversion from  * SQL to relational algebra.  *  *<p>SQL statements to be translated can use the schema defined in  * {@link MockCatalogReader}; note that this is slightly different from  * Farrago's SALES schema. If you get a parser or validator error from your test  * SQL, look down in the stack until you see "Caused by", which will usually  * tell you the real error.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|SqlToRelTestBase
block|{
comment|//~ Static fields/initializers ---------------------------------------------
specifier|protected
specifier|static
specifier|final
name|String
name|NL
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
decl_stmt|;
comment|//~ Instance fields --------------------------------------------------------
comment|//~ Methods ----------------------------------------------------------------
comment|/** Creates the test fixture that determines the behavior of tests.    * Sub-classes that, say, test different parser implementations should    * override. */
specifier|public
name|SqlToRelFixture
name|fixture
parameter_list|()
block|{
return|return
name|SqlToRelFixture
operator|.
name|DEFAULT
return|;
block|}
comment|/** Sets the SQL statement for a test. */
specifier|public
specifier|final
name|SqlToRelFixture
name|sql
parameter_list|(
name|String
name|sql
parameter_list|)
block|{
return|return
name|fixture
argument_list|()
operator|.
name|expression
argument_list|(
literal|false
argument_list|)
operator|.
name|withSql
argument_list|(
name|sql
argument_list|)
return|;
block|}
specifier|public
specifier|final
name|SqlToRelFixture
name|expr
parameter_list|(
name|String
name|sql
parameter_list|)
block|{
return|return
name|fixture
argument_list|()
operator|.
name|expression
argument_list|(
literal|true
argument_list|)
operator|.
name|withSql
argument_list|(
name|sql
argument_list|)
return|;
block|}
comment|//~ Inner Classes ----------------------------------------------------------
comment|/**    * Custom implementation of Correlate for testing.    */
specifier|public
specifier|static
class|class
name|CustomCorrelate
extends|extends
name|Correlate
block|{
specifier|public
name|CustomCorrelate
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelTraitSet
name|traits
parameter_list|,
name|List
argument_list|<
name|RelHint
argument_list|>
name|hints
parameter_list|,
name|RelNode
name|left
parameter_list|,
name|RelNode
name|right
parameter_list|,
name|CorrelationId
name|correlationId
parameter_list|,
name|ImmutableBitSet
name|requiredColumns
parameter_list|,
name|JoinRelType
name|joinType
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|traits
argument_list|,
name|hints
argument_list|,
name|left
argument_list|,
name|right
argument_list|,
name|correlationId
argument_list|,
name|requiredColumns
argument_list|,
name|joinType
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Correlate
name|copy
parameter_list|(
name|RelTraitSet
name|traitSet
parameter_list|,
name|RelNode
name|left
parameter_list|,
name|RelNode
name|right
parameter_list|,
name|CorrelationId
name|correlationId
parameter_list|,
name|ImmutableBitSet
name|requiredColumns
parameter_list|,
name|JoinRelType
name|joinType
parameter_list|)
block|{
return|return
operator|new
name|CustomCorrelate
argument_list|(
name|getCluster
argument_list|()
argument_list|,
name|traitSet
argument_list|,
name|hints
argument_list|,
name|left
argument_list|,
name|right
argument_list|,
name|correlationId
argument_list|,
name|requiredColumns
argument_list|,
name|joinType
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelNode
name|withHints
parameter_list|(
name|List
argument_list|<
name|RelHint
argument_list|>
name|hintList
parameter_list|)
block|{
return|return
operator|new
name|CustomCorrelate
argument_list|(
name|getCluster
argument_list|()
argument_list|,
name|traitSet
argument_list|,
name|hintList
argument_list|,
name|left
argument_list|,
name|right
argument_list|,
name|correlationId
argument_list|,
name|requiredColumns
argument_list|,
name|joinType
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelNode
name|accept
parameter_list|(
name|RelShuttle
name|shuttle
parameter_list|)
block|{
return|return
name|shuttle
operator|.
name|visit
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

