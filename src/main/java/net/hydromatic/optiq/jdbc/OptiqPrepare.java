begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|jdbc
package|;
end_package

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
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
name|linq4j
operator|.
name|expressions
operator|.
name|Expression
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
name|Expressions
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
name|rules
operator|.
name|java
operator|.
name|*
import|;
end_import

begin_import
import|import
name|openjava
operator|.
name|ptree
operator|.
name|ClassDeclaration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|janino
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
name|oj
operator|.
name|stmt
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
name|RelCollation
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
name|RelNode
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
name|relopt
operator|.
name|volcano
operator|.
name|VolcanoPlanner
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
name|reltype
operator|.
name|RelDataTypeFactory
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
name|RelDataTypeField
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
name|parser
operator|.
name|SqlParseException
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
name|SqlParser
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
name|MultisetSqlType
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
name|SqlToRelConverter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|InvocationTargetException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|Map
import|;
end_import

begin_comment
comment|/**  * Shit just got real.  *  * @author jhyde  */
end_comment

begin_class
class|class
name|OptiqPrepare
block|{
specifier|public
specifier|static
name|PrepareResult
name|prepare
parameter_list|(
name|OptiqStatement
name|statement
parameter_list|,
name|String
name|sql
parameter_list|)
block|{
specifier|final
name|RelDataTypeFactory
name|typeFactory
init|=
name|statement
operator|.
name|connection
operator|.
name|typeFactory
decl_stmt|;
name|OptiqCatalogReader
name|catalogReader
init|=
operator|new
name|OptiqCatalogReader
argument_list|(
name|statement
operator|.
name|connection
operator|.
name|rootSchema
argument_list|,
name|typeFactory
argument_list|)
decl_stmt|;
name|RelOptConnectionImpl
name|relOptConnection
init|=
operator|new
name|RelOptConnectionImpl
argument_list|(
name|catalogReader
argument_list|)
decl_stmt|;
specifier|final
name|OptiqPreparingStmt
name|preparingStmt
init|=
operator|new
name|OptiqPreparingStmt
argument_list|(
name|relOptConnection
argument_list|,
name|typeFactory
argument_list|,
name|statement
operator|.
name|connection
operator|.
name|rootSchema
argument_list|)
decl_stmt|;
name|preparingStmt
operator|.
name|setResultCallingConvention
argument_list|(
name|CallingConvention
operator|.
name|ENUMERABLE
argument_list|)
expr_stmt|;
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
decl_stmt|;
try|try
block|{
name|sqlNode
operator|=
name|parser
operator|.
name|parseQuery
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SqlParseException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"parse failed"
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|SqlValidator
name|validator
init|=
operator|new
name|SqlValidatorImpl
argument_list|(
name|SqlStdOperatorTable
operator|.
name|instance
argument_list|()
argument_list|,
name|catalogReader
argument_list|,
name|typeFactory
argument_list|,
name|SqlConformance
operator|.
name|Default
argument_list|)
block|{ }
decl_stmt|;
specifier|final
name|PreparedResult
name|preparedResult
init|=
name|preparingStmt
operator|.
name|prepareSql
argument_list|(
name|sqlNode
argument_list|,
name|Object
operator|.
name|class
argument_list|,
name|validator
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|// TODO: parameters
specifier|final
name|List
argument_list|<
name|OptiqParameter
argument_list|>
name|parameters
init|=
name|Collections
operator|.
name|emptyList
argument_list|()
decl_stmt|;
comment|// TODO: column meta data
name|RelDataType
name|x
init|=
name|validator
operator|.
name|getValidatedNodeType
argument_list|(
name|sqlNode
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|OptiqResultSetMetaData
operator|.
name|ColumnMetaData
argument_list|>
name|columns
init|=
operator|new
name|ArrayList
argument_list|<
name|OptiqResultSetMetaData
operator|.
name|ColumnMetaData
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|RelDataTypeField
name|field
range|:
name|x
operator|.
name|getFields
argument_list|()
control|)
block|{
name|RelDataType
name|type
init|=
name|field
operator|.
name|getType
argument_list|()
decl_stmt|;
name|columns
operator|.
name|add
argument_list|(
operator|new
name|OptiqResultSetMetaData
operator|.
name|ColumnMetaData
argument_list|(
name|columns
operator|.
name|size
argument_list|()
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
name|type
operator|.
name|isNullable
argument_list|()
condition|?
literal|1
else|:
literal|0
argument_list|,
literal|true
argument_list|,
literal|0
argument_list|,
name|field
operator|.
name|getName
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|type
operator|.
name|getSqlTypeName
argument_list|()
operator|.
name|allowsPrec
argument_list|()
operator|&&
literal|false
condition|?
name|type
operator|.
name|getPrecision
argument_list|()
else|:
operator|-
literal|1
argument_list|,
name|type
operator|.
name|getSqlTypeName
argument_list|()
operator|.
name|allowsScale
argument_list|()
condition|?
name|type
operator|.
name|getScale
argument_list|()
else|:
operator|-
literal|1
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|type
operator|.
name|getSqlTypeName
argument_list|()
operator|.
name|getJdbcOrdinal
argument_list|()
argument_list|,
name|type
operator|.
name|getSqlTypeName
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|OptiqResultSetMetaData
name|resultSetMetaData
init|=
operator|new
name|OptiqResultSetMetaData
argument_list|(
name|statement
argument_list|,
literal|null
argument_list|,
name|columns
argument_list|)
decl_stmt|;
return|return
operator|new
name|PrepareResult
argument_list|(
name|sql
argument_list|,
name|parameters
argument_list|,
name|resultSetMetaData
argument_list|,
operator|(
name|Enumerable
operator|)
name|preparedResult
operator|.
name|execute
argument_list|()
argument_list|)
return|;
block|}
specifier|static
class|class
name|PrepareResult
block|{
specifier|final
name|String
name|sql
decl_stmt|;
comment|// for debug
specifier|final
name|List
argument_list|<
name|OptiqParameter
argument_list|>
name|parameterList
decl_stmt|;
specifier|final
name|OptiqResultSetMetaData
name|resultSetMetaData
decl_stmt|;
specifier|final
name|RawEnumerable
name|enumerable
decl_stmt|;
specifier|public
name|PrepareResult
parameter_list|(
name|String
name|sql
parameter_list|,
name|List
argument_list|<
name|OptiqParameter
argument_list|>
name|parameterList
parameter_list|,
name|OptiqResultSetMetaData
name|resultSetMetaData
parameter_list|,
name|RawEnumerable
name|enumerable
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|sql
operator|=
name|sql
expr_stmt|;
name|this
operator|.
name|parameterList
operator|=
name|parameterList
expr_stmt|;
name|this
operator|.
name|resultSetMetaData
operator|=
name|resultSetMetaData
expr_stmt|;
name|this
operator|.
name|enumerable
operator|=
name|enumerable
expr_stmt|;
block|}
specifier|public
name|Enumerator
name|execute
parameter_list|()
block|{
return|return
name|enumerable
operator|.
name|enumerator
argument_list|()
return|;
block|}
block|}
specifier|private
specifier|static
class|class
name|OptiqPreparingStmt
extends|extends
name|OJPreparingStmt
block|{
specifier|private
specifier|final
name|RelOptPlanner
name|planner
decl_stmt|;
specifier|private
specifier|final
name|RexBuilder
name|rexBuilder
decl_stmt|;
specifier|private
specifier|final
name|Schema
name|schema
decl_stmt|;
specifier|public
name|OptiqPreparingStmt
parameter_list|(
name|RelOptConnection
name|connection
parameter_list|,
name|RelDataTypeFactory
name|typeFactory
parameter_list|,
name|Schema
name|schema
parameter_list|)
block|{
name|super
argument_list|(
name|connection
argument_list|)
expr_stmt|;
name|this
operator|.
name|schema
operator|=
name|schema
expr_stmt|;
name|planner
operator|=
operator|new
name|VolcanoPlanner
argument_list|()
expr_stmt|;
name|planner
operator|.
name|addRelTraitDef
argument_list|(
name|CallingConventionTraitDef
operator|.
name|instance
argument_list|)
expr_stmt|;
name|RelOptUtil
operator|.
name|registerAbstractRels
argument_list|(
name|planner
argument_list|)
expr_stmt|;
name|planner
operator|.
name|addRule
argument_list|(
name|JavaRules
operator|.
name|ENUMERABLE_JOIN_RULE
argument_list|)
expr_stmt|;
name|planner
operator|.
name|addRule
argument_list|(
name|JavaRules
operator|.
name|ENUMERABLE_CALC_RULE
argument_list|)
expr_stmt|;
name|rexBuilder
operator|=
operator|new
name|RexBuilder
argument_list|(
name|typeFactory
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|SqlToRelConverter
name|getSqlToRelConverter
parameter_list|(
name|SqlValidator
name|validator
parameter_list|,
name|RelOptConnection
name|connection
parameter_list|)
block|{
return|return
operator|new
name|SqlToRelConverter
argument_list|(
name|validator
argument_list|,
name|connection
operator|.
name|getRelOptSchema
argument_list|()
argument_list|,
name|env
argument_list|,
name|planner
argument_list|,
name|connection
argument_list|,
name|rexBuilder
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|EnumerableRelImplementor
name|getRelImplementor
parameter_list|(
name|RexBuilder
name|rexBuilder
parameter_list|)
block|{
return|return
operator|new
name|EnumerableRelImplementor
argument_list|(
name|rexBuilder
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|String
name|getClassRoot
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|protected
name|String
name|getCompilerClassName
parameter_list|()
block|{
return|return
literal|"org.eigenbase.javac.JaninoCompiler"
return|;
block|}
annotation|@
name|Override
specifier|protected
name|String
name|getJavaRoot
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|protected
name|String
name|getTempPackageName
parameter_list|()
block|{
return|return
literal|"foo"
return|;
block|}
annotation|@
name|Override
specifier|protected
name|String
name|getTempMethodName
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|protected
name|String
name|getTempClassName
parameter_list|()
block|{
return|return
literal|"Foo"
return|;
block|}
annotation|@
name|Override
specifier|protected
name|boolean
name|shouldAlwaysWriteJavaFile
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|protected
name|boolean
name|shouldSetConnectionInfo
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|protected
name|RelNode
name|flattenTypes
parameter_list|(
name|RelNode
name|rootRel
parameter_list|,
name|boolean
name|restructure
parameter_list|)
block|{
return|return
name|rootRel
return|;
block|}
annotation|@
name|Override
specifier|protected
name|RelNode
name|decorrelate
parameter_list|(
name|SqlNode
name|query
parameter_list|,
name|RelNode
name|rootRel
parameter_list|)
block|{
return|return
name|rootRel
return|;
block|}
annotation|@
name|Override
specifier|protected
name|PreparedExecution
name|implement
parameter_list|(
name|RelDataType
name|rowType
parameter_list|,
name|RelNode
name|rootRel
parameter_list|,
name|SqlKind
name|sqlKind
parameter_list|,
name|ClassDeclaration
name|decl
parameter_list|,
name|Argument
index|[]
name|args
parameter_list|)
block|{
name|RelDataType
name|resultType
init|=
name|rootRel
operator|.
name|getRowType
argument_list|()
decl_stmt|;
name|boolean
name|isDml
init|=
name|sqlKind
operator|.
name|belongsTo
argument_list|(
name|SqlKind
operator|.
name|DML
argument_list|)
decl_stmt|;
name|javaCompiler
operator|=
name|createCompiler
argument_list|()
expr_stmt|;
name|EnumerableRelImplementor
name|relImplementor
init|=
name|getRelImplementor
argument_list|(
name|rootRel
operator|.
name|getCluster
argument_list|()
operator|.
name|getRexBuilder
argument_list|()
argument_list|)
decl_stmt|;
name|Expression
name|expr
init|=
name|relImplementor
operator|.
name|implementRoot
argument_list|(
operator|(
name|EnumerableRel
operator|)
name|rootRel
argument_list|)
decl_stmt|;
name|String
name|s
init|=
name|Expressions
operator|.
name|toString
argument_list|(
name|expr
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|s
argument_list|)
expr_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Queryable
argument_list|>
name|map
init|=
name|relImplementor
operator|.
name|map
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Class
argument_list|>
name|classList
init|=
operator|new
name|ArrayList
argument_list|<
name|Class
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Queryable
argument_list|>
name|entry
range|:
name|map
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|classList
operator|.
name|add
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|final
name|ExpressionEvaluator
name|ee
decl_stmt|;
try|try
block|{
name|ee
operator|=
operator|new
name|ExpressionEvaluator
argument_list|(
name|s
argument_list|,
name|Enumerable
operator|.
name|class
argument_list|,
name|map
operator|.
name|keySet
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|map
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|,
name|classList
operator|.
name|toArray
argument_list|(
operator|new
name|Class
index|[
name|map
operator|.
name|size
argument_list|()
index|]
argument_list|)
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
name|Helper
operator|.
name|INSTANCE
operator|.
name|wrap
argument_list|(
literal|"Error while compiling generated Java code:\n"
operator|+
name|s
argument_list|,
name|e
argument_list|)
throw|;
block|}
if|if
condition|(
name|timingTracer
operator|!=
literal|null
condition|)
block|{
name|timingTracer
operator|.
name|traceTime
argument_list|(
literal|"end codegen"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|timingTracer
operator|!=
literal|null
condition|)
block|{
name|timingTracer
operator|.
name|traceTime
argument_list|(
literal|"end compilation"
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|PreparedExecution
argument_list|(
literal|null
argument_list|,
name|rootRel
argument_list|,
name|resultType
argument_list|,
name|isDml
argument_list|,
name|mapTableModOp
argument_list|(
name|isDml
argument_list|,
name|sqlKind
argument_list|)
argument_list|,
literal|null
argument_list|)
block|{
specifier|public
name|Object
name|execute
parameter_list|()
block|{
try|try
block|{
return|return
name|ee
operator|.
name|evaluate
argument_list|(
name|map
operator|.
name|values
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|Object
index|[
name|map
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|InvocationTargetException
name|e
parameter_list|)
block|{
throw|throw
name|Helper
operator|.
name|INSTANCE
operator|.
name|wrap
argument_list|(
literal|"Error while executing"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
return|;
block|}
block|}
specifier|private
specifier|static
class|class
name|Table
implements|implements
name|SqlValidatorTable
implements|,
name|RelOptTable
block|{
specifier|private
specifier|final
name|RelOptSchema
name|schema
decl_stmt|;
specifier|private
specifier|final
name|RelDataType
name|rowType
decl_stmt|;
specifier|private
specifier|final
name|String
index|[]
name|names
decl_stmt|;
specifier|private
specifier|final
name|Expression
name|expression
decl_stmt|;
specifier|private
specifier|final
name|Queryable
name|queryable
decl_stmt|;
specifier|public
name|Table
parameter_list|(
name|RelOptSchema
name|schema
parameter_list|,
name|RelDataType
name|rowType
parameter_list|,
name|String
index|[]
name|names
parameter_list|,
name|Expression
name|expression
parameter_list|,
name|Queryable
name|queryable
parameter_list|)
block|{
name|this
operator|.
name|schema
operator|=
name|schema
expr_stmt|;
name|this
operator|.
name|rowType
operator|=
name|rowType
expr_stmt|;
name|this
operator|.
name|names
operator|=
name|names
expr_stmt|;
name|this
operator|.
name|expression
operator|=
name|expression
expr_stmt|;
name|this
operator|.
name|queryable
operator|=
name|queryable
expr_stmt|;
block|}
specifier|public
name|double
name|getRowCount
parameter_list|()
block|{
return|return
literal|100
return|;
block|}
specifier|public
name|RelOptSchema
name|getRelOptSchema
parameter_list|()
block|{
return|return
name|schema
return|;
block|}
specifier|public
name|RelNode
name|toRel
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelOptConnection
name|connection
parameter_list|)
block|{
return|return
operator|new
name|JavaRules
operator|.
name|EnumerableTableAccessRel
argument_list|(
name|cluster
argument_list|,
name|this
argument_list|,
name|connection
argument_list|,
name|expression
argument_list|,
name|queryable
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
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
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
name|String
index|[]
name|getQualifiedName
parameter_list|()
block|{
return|return
name|names
return|;
block|}
specifier|public
name|SqlMonotonicity
name|getMonotonicity
parameter_list|(
name|String
name|columnName
parameter_list|)
block|{
return|return
name|SqlMonotonicity
operator|.
name|NotMonotonic
return|;
block|}
specifier|public
name|SqlAccessType
name|getAllowedAccess
parameter_list|()
block|{
return|return
name|SqlAccessType
operator|.
name|READ_ONLY
return|;
block|}
block|}
specifier|private
specifier|static
class|class
name|OptiqCatalogReader
implements|implements
name|SqlValidatorCatalogReader
implements|,
name|RelOptSchema
block|{
specifier|private
specifier|final
name|Schema
name|schema
decl_stmt|;
specifier|private
specifier|final
name|RelDataTypeFactory
name|typeFactory
decl_stmt|;
specifier|private
specifier|final
name|Expression
name|rootExpression
init|=
name|Expressions
operator|.
name|variable
argument_list|(
name|Map
operator|.
name|class
argument_list|,
literal|"root"
argument_list|)
decl_stmt|;
specifier|public
name|OptiqCatalogReader
parameter_list|(
name|Schema
name|schema
parameter_list|,
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|schema
operator|=
name|schema
expr_stmt|;
name|this
operator|.
name|typeFactory
operator|=
name|typeFactory
expr_stmt|;
block|}
specifier|public
name|Table
name|getTable
parameter_list|(
specifier|final
name|String
index|[]
name|names
parameter_list|)
block|{
name|Schema
name|schema2
init|=
name|schema
decl_stmt|;
name|Expression
name|expression
init|=
name|rootExpression
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|names
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|String
name|name
init|=
name|names
index|[
name|i
index|]
decl_stmt|;
specifier|final
name|SchemaObject
name|schemaObject
init|=
name|schema2
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Expression
argument_list|>
name|arguments
init|=
name|Collections
operator|.
name|emptyList
argument_list|()
decl_stmt|;
name|expression
operator|=
name|schema2
operator|.
name|getExpression
argument_list|(
name|expression
argument_list|,
name|schemaObject
argument_list|,
name|name
argument_list|,
name|arguments
argument_list|)
expr_stmt|;
if|if
condition|(
name|schemaObject
operator|instanceof
name|Function
condition|)
block|{
if|if
condition|(
name|i
operator|!=
name|names
operator|.
name|length
operator|-
literal|1
condition|)
block|{
return|return
literal|null
return|;
block|}
name|RelDataType
name|type
init|=
operator|(
operator|(
name|Function
operator|)
name|schemaObject
operator|)
operator|.
name|getType
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|instanceof
name|MultisetSqlType
condition|)
block|{
name|Object
name|o
init|=
operator|(
operator|(
name|Function
operator|)
name|schemaObject
operator|)
operator|.
name|evaluate
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|Enumerable
argument_list|<
name|Object
argument_list|>
name|enumerable
decl_stmt|;
if|if
condition|(
name|o
operator|instanceof
name|Object
index|[]
condition|)
block|{
name|enumerable
operator|=
name|Linq4j
operator|.
name|asEnumerable
argument_list|(
operator|(
name|Object
index|[]
operator|)
name|o
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|o
operator|instanceof
name|Enumerable
condition|)
block|{
name|enumerable
operator|=
operator|(
name|Enumerable
argument_list|<
name|Object
argument_list|>
operator|)
name|o
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Cannot convert to Enumerable"
argument_list|)
throw|;
block|}
name|Queryable
name|queryable
init|=
name|enumerable
operator|.
name|asQueryable
argument_list|()
decl_stmt|;
return|return
operator|new
name|Table
argument_list|(
name|this
argument_list|,
name|type
operator|.
name|getComponentType
argument_list|()
argument_list|,
name|names
argument_list|,
name|toEnumerable
argument_list|(
name|expression
argument_list|)
argument_list|,
name|queryable
argument_list|)
return|;
block|}
block|}
if|if
condition|(
name|schemaObject
operator|instanceof
name|SchemaLink
condition|)
block|{
name|schema2
operator|=
operator|(
operator|(
name|SchemaLink
operator|)
name|schemaObject
operator|)
operator|.
name|schema
expr_stmt|;
continue|continue;
block|}
return|return
literal|null
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|private
name|Expression
name|toEnumerable
parameter_list|(
name|Expression
name|expression
parameter_list|)
block|{
name|Class
name|type
init|=
name|expression
operator|.
name|getType
argument_list|()
decl_stmt|;
if|if
condition|(
name|Enumerable
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|type
argument_list|)
condition|)
block|{
return|return
name|expression
return|;
block|}
if|if
condition|(
name|type
operator|.
name|isArray
argument_list|()
condition|)
block|{
return|return
name|Expressions
operator|.
name|call
argument_list|(
name|Linq4j
operator|.
name|class
argument_list|,
literal|"asEnumerable"
argument_list|,
name|Collections
operator|.
expr|<
name|Class
operator|>
name|emptyList
argument_list|()
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|expression
argument_list|)
argument_list|)
return|;
block|}
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"cannot convert expression ["
operator|+
name|expression
operator|+
literal|"] to enumerable"
argument_list|)
throw|;
block|}
specifier|public
name|RelDataType
name|getNamedType
parameter_list|(
name|SqlIdentifier
name|typeName
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|List
argument_list|<
name|SqlMoniker
argument_list|>
name|getAllSchemaObjectNames
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|names
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|String
name|getSchemaName
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|Table
name|getTableForMember
parameter_list|(
name|String
index|[]
name|names
parameter_list|)
block|{
return|return
name|getTable
argument_list|(
name|names
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
block|}
specifier|private
specifier|static
class|class
name|RelOptConnectionImpl
implements|implements
name|RelOptConnection
block|{
specifier|private
specifier|final
name|RelOptSchema
name|schema
decl_stmt|;
specifier|public
name|RelOptConnectionImpl
parameter_list|(
name|RelOptSchema
name|schema
parameter_list|)
block|{
name|this
operator|.
name|schema
operator|=
name|schema
expr_stmt|;
block|}
specifier|public
name|RelOptSchema
name|getRelOptSchema
parameter_list|()
block|{
return|return
name|schema
return|;
block|}
specifier|public
name|Object
name|contentsAsArray
parameter_list|(
name|String
name|qualifier
parameter_list|,
name|String
name|tableName
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End OptiqPrepare.java
end_comment

end_unit

