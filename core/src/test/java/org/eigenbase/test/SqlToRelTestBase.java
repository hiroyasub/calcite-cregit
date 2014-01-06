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
name|test
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
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
name|relopt
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
name|*
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
name|*
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
name|*
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
name|parser
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
name|sql
operator|.
name|type
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
name|sql
operator|.
name|validate
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
name|sql2rel
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
name|util
operator|.
name|*
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
name|prepare
operator|.
name|Prepare
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|expressions
operator|.
name|Expression
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Iterables
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
name|*
import|;
end_import

begin_comment
comment|/**  * SqlToRelTestBase is an abstract base for tests which involve conversion from  * SQL to relational algebra.  *  *<p>SQL statements to be translated can use the schema defined in {@link  * MockCatalogReader}; note that this is slightly different from Farrago's SALES  * schema. If you get a parser or validator error from your test SQL, look down  * in the stack until you see "Caused by", which will usually tell you the real  * error.  */
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
specifier|protected
specifier|final
name|Tester
name|tester
init|=
name|createTester
argument_list|()
decl_stmt|;
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|SqlToRelTestBase
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|Tester
name|createTester
parameter_list|()
block|{
return|return
operator|new
name|TesterImpl
argument_list|(
name|getDiffRepos
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Returns the default diff repository for this test, or null if there is      * no repository.      *      *<p>The default implementation returns null.      *      *<p>Sub-classes that want to use a diff repository can override.      * Sub-sub-classes can override again, inheriting test cases and overriding      * selected test results.      *      *<p>And individual test cases can override by providing a different      * tester object.      *      * @return Diff repository      */
specifier|protected
name|DiffRepository
name|getDiffRepos
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|//~ Inner Interfaces -------------------------------------------------------
comment|/**      * Helper class which contains default implementations of methods used for      * running sql-to-rel conversion tests.      */
specifier|public
specifier|static
interface|interface
name|Tester
block|{
comment|/**          * Converts a SQL string to a {@link RelNode} tree.          *          * @param sql SQL statement          *          * @return Relational expression, never null          */
name|RelNode
name|convertSqlToRel
parameter_list|(
name|String
name|sql
parameter_list|)
function_decl|;
name|SqlNode
name|parseQuery
parameter_list|(
name|String
name|sql
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**          * Factory method to create a {@link SqlValidator}.          */
name|SqlValidator
name|createValidator
parameter_list|(
name|SqlValidatorCatalogReader
name|catalogReader
parameter_list|,
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
function_decl|;
comment|/**          * Factory method for a          * {@link net.hydromatic.optiq.prepare.Prepare.CatalogReader}.          */
name|Prepare
operator|.
name|CatalogReader
name|createCatalogReader
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
function_decl|;
name|RelOptPlanner
name|createPlanner
parameter_list|()
function_decl|;
comment|/**          * Returns the {@link SqlOperatorTable} to use.          */
name|SqlOperatorTable
name|getOperatorTable
parameter_list|()
function_decl|;
comment|/**          * Returns the SQL dialect to test.          */
name|SqlConformance
name|getConformance
parameter_list|()
function_decl|;
comment|/**          * Checks that a SQL statement converts to a given plan.          *          * @param sql SQL query          * @param plan Expected plan          */
name|void
name|assertConvertsTo
parameter_list|(
name|String
name|sql
parameter_list|,
name|String
name|plan
parameter_list|)
function_decl|;
comment|/**          * Checks that a SQL statement converts to a given plan, optionally          * trimming columns that are not needed.          *          * @param sql SQL query          * @param plan Expected plan          */
name|void
name|assertConvertsTo
parameter_list|(
name|String
name|sql
parameter_list|,
name|String
name|plan
parameter_list|,
name|boolean
name|trim
parameter_list|)
function_decl|;
comment|/**          * Returns the diff repository.          *          * @return Diff repository          */
name|DiffRepository
name|getDiffRepos
parameter_list|()
function_decl|;
comment|/**          * Returns the validator.          *          * @return Validator          */
name|SqlValidator
name|getValidator
parameter_list|()
function_decl|;
block|}
comment|//~ Inner Classes ----------------------------------------------------------
comment|/**      * Mock implementation of {@link RelOptSchema}.      */
specifier|protected
specifier|static
class|class
name|MockRelOptSchema
implements|implements
name|RelOptSchemaWithSampling
block|{
specifier|private
specifier|final
name|SqlValidatorCatalogReader
name|catalogReader
decl_stmt|;
specifier|private
specifier|final
name|RelDataTypeFactory
name|typeFactory
decl_stmt|;
specifier|public
name|MockRelOptSchema
parameter_list|(
name|SqlValidatorCatalogReader
name|catalogReader
parameter_list|,
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
block|{
name|this
operator|.
name|catalogReader
operator|=
name|catalogReader
expr_stmt|;
name|this
operator|.
name|typeFactory
operator|=
name|typeFactory
expr_stmt|;
block|}
specifier|public
name|RelOptTable
name|getTableForMember
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|names
parameter_list|)
block|{
specifier|final
name|SqlValidatorTable
name|table
init|=
name|catalogReader
operator|.
name|getTable
argument_list|(
name|names
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|rowType
init|=
name|table
operator|.
name|getRowType
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RelCollation
argument_list|>
name|collationList
init|=
name|deduceMonotonicity
argument_list|(
name|table
argument_list|)
decl_stmt|;
if|if
condition|(
name|names
operator|.
name|size
argument_list|()
operator|<
literal|3
condition|)
block|{
name|String
index|[]
name|newNames2
init|=
block|{
literal|"CATALOG"
block|,
literal|"SALES"
block|,
literal|""
block|}
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|newNames
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|newNames
operator|.
name|size
argument_list|()
operator|<
name|newNames2
operator|.
name|length
condition|)
block|{
name|newNames
operator|.
name|add
argument_list|(
name|i
argument_list|,
name|newNames2
index|[
name|i
index|]
argument_list|)
expr_stmt|;
operator|++
name|i
expr_stmt|;
block|}
name|names
operator|=
name|newNames
expr_stmt|;
block|}
return|return
name|createColumnSet
argument_list|(
name|table
argument_list|,
name|names
argument_list|,
name|rowType
argument_list|,
name|collationList
argument_list|)
return|;
block|}
specifier|private
name|List
argument_list|<
name|RelCollation
argument_list|>
name|deduceMonotonicity
parameter_list|(
name|SqlValidatorTable
name|table
parameter_list|)
block|{
specifier|final
name|RelDataType
name|rowType
init|=
name|table
operator|.
name|getRowType
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RelCollation
argument_list|>
name|collationList
init|=
operator|new
name|ArrayList
argument_list|<
name|RelCollation
argument_list|>
argument_list|()
decl_stmt|;
comment|// Deduce which fields the table is sorted on.
name|int
name|i
init|=
operator|-
literal|1
decl_stmt|;
for|for
control|(
name|RelDataTypeField
name|field
range|:
name|rowType
operator|.
name|getFieldList
argument_list|()
control|)
block|{
operator|++
name|i
expr_stmt|;
specifier|final
name|SqlMonotonicity
name|monotonicity
init|=
name|table
operator|.
name|getMonotonicity
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|monotonicity
operator|!=
name|SqlMonotonicity
operator|.
name|NotMonotonic
condition|)
block|{
specifier|final
name|RelFieldCollation
operator|.
name|Direction
name|direction
init|=
name|monotonicity
operator|.
name|isDecreasing
argument_list|()
condition|?
name|RelFieldCollation
operator|.
name|Direction
operator|.
name|Descending
else|:
name|RelFieldCollation
operator|.
name|Direction
operator|.
name|Ascending
decl_stmt|;
name|collationList
operator|.
name|add
argument_list|(
name|RelCollationImpl
operator|.
name|of
argument_list|(
operator|new
name|RelFieldCollation
argument_list|(
name|i
argument_list|,
name|direction
argument_list|,
name|RelFieldCollation
operator|.
name|NullDirection
operator|.
name|UNSPECIFIED
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|collationList
return|;
block|}
specifier|public
name|RelOptTable
name|getTableForMember
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|names
parameter_list|,
specifier|final
name|String
name|datasetName
parameter_list|,
name|boolean
index|[]
name|usedDataset
parameter_list|)
block|{
specifier|final
name|RelOptTable
name|table
init|=
name|getTableForMember
argument_list|(
name|names
argument_list|)
decl_stmt|;
comment|// If they're asking for a sample, just for test purposes,
comment|// assume there's a table called "<table>:<sample>".
name|RelOptTable
name|datasetTable
init|=
operator|new
name|DelegatingRelOptTable
argument_list|(
name|table
argument_list|)
block|{
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getQualifiedName
parameter_list|()
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|super
operator|.
name|getQualifiedName
argument_list|()
argument_list|)
decl_stmt|;
name|list
operator|.
name|set
argument_list|(
name|list
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|,
name|list
operator|.
name|get
argument_list|(
name|list
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
operator|+
literal|":"
operator|+
name|datasetName
argument_list|)
expr_stmt|;
return|return
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|list
argument_list|)
return|;
block|}
block|}
decl_stmt|;
if|if
condition|(
name|usedDataset
operator|!=
literal|null
condition|)
block|{
assert|assert
name|usedDataset
operator|.
name|length
operator|==
literal|1
assert|;
name|usedDataset
index|[
literal|0
index|]
operator|=
literal|true
expr_stmt|;
block|}
return|return
name|datasetTable
return|;
block|}
specifier|protected
name|MockColumnSet
name|createColumnSet
parameter_list|(
name|SqlValidatorTable
name|table
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|names
parameter_list|,
specifier|final
name|RelDataType
name|rowType
parameter_list|,
specifier|final
name|List
argument_list|<
name|RelCollation
argument_list|>
name|collationList
parameter_list|)
block|{
return|return
operator|new
name|MockColumnSet
argument_list|(
name|names
argument_list|,
name|rowType
argument_list|,
name|collationList
argument_list|)
return|;
block|}
specifier|public
name|RelDataTypeFactory
name|getTypeFactory
parameter_list|()
block|{
return|return
name|typeFactory
return|;
block|}
specifier|public
name|void
name|registerRules
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|)
throws|throws
name|Exception
block|{
block|}
specifier|protected
class|class
name|MockColumnSet
implements|implements
name|RelOptTable
block|{
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|names
decl_stmt|;
specifier|private
specifier|final
name|RelDataType
name|rowType
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|RelCollation
argument_list|>
name|collationList
decl_stmt|;
specifier|protected
name|MockColumnSet
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|names
parameter_list|,
name|RelDataType
name|rowType
parameter_list|,
specifier|final
name|List
argument_list|<
name|RelCollation
argument_list|>
name|collationList
parameter_list|)
block|{
name|this
operator|.
name|names
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|names
argument_list|)
expr_stmt|;
name|this
operator|.
name|rowType
operator|=
name|rowType
expr_stmt|;
name|this
operator|.
name|collationList
operator|=
name|collationList
expr_stmt|;
block|}
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
name|unwrap
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|clazz
parameter_list|)
block|{
if|if
condition|(
name|clazz
operator|.
name|isInstance
argument_list|(
name|this
argument_list|)
condition|)
block|{
return|return
name|clazz
operator|.
name|cast
argument_list|(
name|this
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getQualifiedName
parameter_list|()
block|{
return|return
name|names
return|;
block|}
specifier|public
name|double
name|getRowCount
parameter_list|()
block|{
comment|// use something other than 0 to give costing tests
comment|// some room, and make emps bigger than depts for
comment|// join asymmetry
if|if
condition|(
name|Iterables
operator|.
name|getLast
argument_list|(
name|names
argument_list|)
operator|.
name|equals
argument_list|(
literal|"EMP"
argument_list|)
condition|)
block|{
return|return
literal|1000
return|;
block|}
else|else
block|{
return|return
literal|100
return|;
block|}
block|}
specifier|public
name|RelDataType
name|getRowType
parameter_list|()
block|{
return|return
name|rowType
return|;
block|}
specifier|public
name|RelOptSchema
name|getRelOptSchema
parameter_list|()
block|{
return|return
name|MockRelOptSchema
operator|.
name|this
return|;
block|}
specifier|public
name|RelNode
name|toRel
parameter_list|(
name|ToRelContext
name|context
parameter_list|)
block|{
return|return
operator|new
name|TableAccessRel
argument_list|(
name|context
operator|.
name|getCluster
argument_list|()
argument_list|,
name|this
argument_list|)
return|;
block|}
specifier|public
name|List
argument_list|<
name|RelCollation
argument_list|>
name|getCollationList
parameter_list|()
block|{
return|return
name|collationList
return|;
block|}
specifier|public
name|boolean
name|isKey
parameter_list|(
name|BitSet
name|columns
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|Expression
name|getExpression
parameter_list|(
name|Class
name|clazz
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
specifier|private
specifier|static
class|class
name|DelegatingRelOptTable
implements|implements
name|RelOptTable
block|{
specifier|private
specifier|final
name|RelOptTable
name|parent
decl_stmt|;
specifier|public
name|DelegatingRelOptTable
parameter_list|(
name|RelOptTable
name|parent
parameter_list|)
block|{
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
block|}
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
name|unwrap
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|clazz
parameter_list|)
block|{
if|if
condition|(
name|clazz
operator|.
name|isInstance
argument_list|(
name|this
argument_list|)
condition|)
block|{
return|return
name|clazz
operator|.
name|cast
argument_list|(
name|this
argument_list|)
return|;
block|}
return|return
name|parent
operator|.
name|unwrap
argument_list|(
name|clazz
argument_list|)
return|;
block|}
specifier|public
name|Expression
name|getExpression
parameter_list|(
name|Class
name|clazz
parameter_list|)
block|{
return|return
name|parent
operator|.
name|getExpression
argument_list|(
name|clazz
argument_list|)
return|;
block|}
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getQualifiedName
parameter_list|()
block|{
return|return
name|parent
operator|.
name|getQualifiedName
argument_list|()
return|;
block|}
specifier|public
name|double
name|getRowCount
parameter_list|()
block|{
return|return
name|parent
operator|.
name|getRowCount
argument_list|()
return|;
block|}
specifier|public
name|RelDataType
name|getRowType
parameter_list|()
block|{
return|return
name|parent
operator|.
name|getRowType
argument_list|()
return|;
block|}
specifier|public
name|RelOptSchema
name|getRelOptSchema
parameter_list|()
block|{
return|return
name|parent
operator|.
name|getRelOptSchema
argument_list|()
return|;
block|}
specifier|public
name|RelNode
name|toRel
parameter_list|(
name|ToRelContext
name|context
parameter_list|)
block|{
return|return
operator|new
name|TableAccessRel
argument_list|(
name|context
operator|.
name|getCluster
argument_list|()
argument_list|,
name|this
argument_list|)
return|;
block|}
specifier|public
name|List
argument_list|<
name|RelCollation
argument_list|>
name|getCollationList
parameter_list|()
block|{
return|return
name|parent
operator|.
name|getCollationList
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|isKey
parameter_list|(
name|BitSet
name|columns
parameter_list|)
block|{
return|return
name|parent
operator|.
name|isKey
argument_list|(
name|columns
argument_list|)
return|;
block|}
block|}
comment|/**      * Default implementation of {@link Tester}, using mock classes {@link      * MockRelOptSchema} and {@link MockRelOptPlanner}.      */
specifier|public
specifier|static
class|class
name|TesterImpl
implements|implements
name|Tester
block|{
specifier|private
name|RelOptPlanner
name|planner
decl_stmt|;
specifier|private
name|SqlOperatorTable
name|opTab
decl_stmt|;
specifier|private
specifier|final
name|DiffRepository
name|diffRepos
decl_stmt|;
specifier|private
name|RelDataTypeFactory
name|typeFactory
decl_stmt|;
comment|/**          * Creates a TesterImpl.          *          * @param diffRepos Diff repository          */
specifier|protected
name|TesterImpl
parameter_list|(
name|DiffRepository
name|diffRepos
parameter_list|)
block|{
name|this
operator|.
name|diffRepos
operator|=
name|diffRepos
expr_stmt|;
block|}
specifier|public
name|RelNode
name|convertSqlToRel
parameter_list|(
name|String
name|sql
parameter_list|)
block|{
name|Util
operator|.
name|pre
argument_list|(
name|sql
operator|!=
literal|null
argument_list|,
literal|"sql != null"
argument_list|)
expr_stmt|;
specifier|final
name|SqlNode
name|sqlQuery
decl_stmt|;
try|try
block|{
name|sqlQuery
operator|=
name|parseQuery
argument_list|(
name|sql
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|Util
operator|.
name|newInternal
argument_list|(
name|e
argument_list|)
throw|;
comment|// todo: better handling
block|}
specifier|final
name|RelDataTypeFactory
name|typeFactory
init|=
name|getTypeFactory
argument_list|()
decl_stmt|;
specifier|final
name|Prepare
operator|.
name|CatalogReader
name|catalogReader
init|=
name|createCatalogReader
argument_list|(
name|typeFactory
argument_list|)
decl_stmt|;
specifier|final
name|SqlValidator
name|validator
init|=
name|createValidator
argument_list|(
name|catalogReader
argument_list|,
name|typeFactory
argument_list|)
decl_stmt|;
specifier|final
name|SqlToRelConverter
name|converter
init|=
name|createSqlToRelConverter
argument_list|(
name|validator
argument_list|,
name|catalogReader
argument_list|,
name|typeFactory
argument_list|)
decl_stmt|;
name|converter
operator|.
name|setTrimUnusedFields
argument_list|(
literal|true
argument_list|)
expr_stmt|;
specifier|final
name|SqlNode
name|validatedQuery
init|=
name|validator
operator|.
name|validate
argument_list|(
name|sqlQuery
argument_list|)
decl_stmt|;
specifier|final
name|RelNode
name|rel
init|=
name|converter
operator|.
name|convertQuery
argument_list|(
name|validatedQuery
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Util
operator|.
name|post
argument_list|(
name|rel
operator|!=
literal|null
argument_list|,
literal|"return != null"
argument_list|)
expr_stmt|;
return|return
name|rel
return|;
block|}
specifier|protected
name|SqlToRelConverter
name|createSqlToRelConverter
parameter_list|(
specifier|final
name|SqlValidator
name|validator
parameter_list|,
specifier|final
name|Prepare
operator|.
name|CatalogReader
name|catalogReader
parameter_list|,
specifier|final
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
block|{
return|return
operator|new
name|SqlToRelConverter
argument_list|(
literal|null
argument_list|,
name|validator
argument_list|,
name|catalogReader
argument_list|,
name|getPlanner
argument_list|()
argument_list|,
operator|new
name|RexBuilder
argument_list|(
name|typeFactory
argument_list|)
argument_list|)
return|;
block|}
specifier|protected
specifier|final
name|RelDataTypeFactory
name|getTypeFactory
parameter_list|()
block|{
if|if
condition|(
name|typeFactory
operator|==
literal|null
condition|)
block|{
name|typeFactory
operator|=
name|createTypeFactory
argument_list|()
expr_stmt|;
block|}
return|return
name|typeFactory
return|;
block|}
specifier|protected
name|RelDataTypeFactory
name|createTypeFactory
parameter_list|()
block|{
return|return
operator|new
name|SqlTypeFactoryImpl
argument_list|()
return|;
block|}
specifier|protected
specifier|final
name|RelOptPlanner
name|getPlanner
parameter_list|()
block|{
if|if
condition|(
name|planner
operator|==
literal|null
condition|)
block|{
name|planner
operator|=
name|createPlanner
argument_list|()
expr_stmt|;
block|}
return|return
name|planner
return|;
block|}
specifier|public
name|SqlNode
name|parseQuery
parameter_list|(
name|String
name|sql
parameter_list|)
throws|throws
name|Exception
block|{
name|SqlParser
name|parser
init|=
operator|new
name|SqlParser
argument_list|(
name|sql
argument_list|)
decl_stmt|;
name|SqlNode
name|sqlNode
init|=
name|parser
operator|.
name|parseQuery
argument_list|()
decl_stmt|;
return|return
name|sqlNode
return|;
block|}
specifier|public
name|SqlConformance
name|getConformance
parameter_list|()
block|{
return|return
name|SqlConformance
operator|.
name|Default
return|;
block|}
specifier|public
name|SqlValidator
name|createValidator
parameter_list|(
name|SqlValidatorCatalogReader
name|catalogReader
parameter_list|,
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
block|{
return|return
operator|new
name|FarragoTestValidator
argument_list|(
name|getOperatorTable
argument_list|()
argument_list|,
operator|new
name|MockCatalogReader
argument_list|(
name|typeFactory
argument_list|)
argument_list|,
name|typeFactory
argument_list|,
name|getConformance
argument_list|()
argument_list|)
return|;
block|}
specifier|public
specifier|final
name|SqlOperatorTable
name|getOperatorTable
parameter_list|()
block|{
if|if
condition|(
name|opTab
operator|==
literal|null
condition|)
block|{
name|opTab
operator|=
name|createOperatorTable
argument_list|()
expr_stmt|;
block|}
return|return
name|opTab
return|;
block|}
comment|/**          * Creates an operator table.          *          * @return New operator table          */
specifier|protected
name|SqlOperatorTable
name|createOperatorTable
parameter_list|()
block|{
specifier|final
name|MockSqlOperatorTable
name|opTab
init|=
operator|new
name|MockSqlOperatorTable
argument_list|(
name|SqlStdOperatorTable
operator|.
name|instance
argument_list|()
argument_list|)
decl_stmt|;
name|MockSqlOperatorTable
operator|.
name|addRamp
argument_list|(
name|opTab
argument_list|)
expr_stmt|;
return|return
name|opTab
return|;
block|}
specifier|public
name|Prepare
operator|.
name|CatalogReader
name|createCatalogReader
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
block|{
return|return
operator|new
name|MockCatalogReader
argument_list|(
name|typeFactory
argument_list|)
return|;
block|}
specifier|public
name|RelOptPlanner
name|createPlanner
parameter_list|()
block|{
return|return
operator|new
name|MockRelOptPlanner
argument_list|()
return|;
block|}
specifier|public
name|void
name|assertConvertsTo
parameter_list|(
name|String
name|sql
parameter_list|,
name|String
name|plan
parameter_list|)
block|{
name|assertConvertsTo
argument_list|(
name|sql
argument_list|,
name|plan
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|assertConvertsTo
parameter_list|(
name|String
name|sql
parameter_list|,
name|String
name|plan
parameter_list|,
name|boolean
name|trim
parameter_list|)
block|{
name|String
name|sql2
init|=
name|getDiffRepos
argument_list|()
operator|.
name|expand
argument_list|(
literal|"sql"
argument_list|,
name|sql
argument_list|)
decl_stmt|;
name|RelNode
name|rel
init|=
name|convertSqlToRel
argument_list|(
name|sql2
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|rel
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertValid
argument_list|(
name|rel
argument_list|)
expr_stmt|;
if|if
condition|(
name|trim
condition|)
block|{
specifier|final
name|RelFieldTrimmer
name|trimmer
init|=
name|createFieldTrimmer
argument_list|()
decl_stmt|;
name|rel
operator|=
name|trimmer
operator|.
name|trim
argument_list|(
name|rel
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rel
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertValid
argument_list|(
name|rel
argument_list|)
expr_stmt|;
block|}
comment|// NOTE jvs 28-Mar-2006:  insert leading newline so
comment|// that plans come out nicely stacked instead of first
comment|// line immediately after CDATA start
name|String
name|actual
init|=
name|NL
operator|+
name|RelOptUtil
operator|.
name|toString
argument_list|(
name|rel
argument_list|)
decl_stmt|;
name|diffRepos
operator|.
name|assertEquals
argument_list|(
literal|"plan"
argument_list|,
name|plan
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
comment|/**          * Creates a RelFieldTrimmer.          *          * @return Field trimmer          */
specifier|public
name|RelFieldTrimmer
name|createFieldTrimmer
parameter_list|()
block|{
return|return
operator|new
name|RelFieldTrimmer
argument_list|(
name|getValidator
argument_list|()
argument_list|)
return|;
block|}
comment|/**          * Checks that every node of a relational expression is valid.          *          * @param rel Relational expression          */
specifier|protected
name|void
name|assertValid
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
name|SqlToRelConverterTest
operator|.
name|RelValidityChecker
name|checker
init|=
operator|new
name|SqlToRelConverterTest
operator|.
name|RelValidityChecker
argument_list|()
decl_stmt|;
name|checker
operator|.
name|go
argument_list|(
name|rel
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|checker
operator|.
name|invalidCount
argument_list|)
expr_stmt|;
block|}
specifier|public
name|DiffRepository
name|getDiffRepos
parameter_list|()
block|{
return|return
name|diffRepos
return|;
block|}
specifier|public
name|SqlValidator
name|getValidator
parameter_list|()
block|{
specifier|final
name|RelDataTypeFactory
name|typeFactory
init|=
name|getTypeFactory
argument_list|()
decl_stmt|;
specifier|final
name|SqlValidatorCatalogReader
name|catalogReader
init|=
name|createCatalogReader
argument_list|(
name|typeFactory
argument_list|)
decl_stmt|;
return|return
name|createValidator
argument_list|(
name|catalogReader
argument_list|,
name|typeFactory
argument_list|)
return|;
block|}
block|}
specifier|private
specifier|static
class|class
name|FarragoTestValidator
extends|extends
name|SqlValidatorImpl
block|{
specifier|public
name|FarragoTestValidator
parameter_list|(
name|SqlOperatorTable
name|opTab
parameter_list|,
name|SqlValidatorCatalogReader
name|catalogReader
parameter_list|,
name|RelDataTypeFactory
name|typeFactory
parameter_list|,
name|SqlConformance
name|conformance
parameter_list|)
block|{
name|super
argument_list|(
name|opTab
argument_list|,
name|catalogReader
argument_list|,
name|typeFactory
argument_list|,
name|conformance
argument_list|)
expr_stmt|;
block|}
comment|// override SqlValidator
specifier|public
name|boolean
name|shouldExpandIdentifiers
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End SqlToRelTestBase.java
end_comment

end_unit

