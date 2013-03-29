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
name|impl
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
name|Enumerator
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
name|impl
operator|.
name|java
operator|.
name|JavaTypeFactory
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
name|jdbc
operator|.
name|OptiqConnection
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
name|jdbc
operator|.
name|OptiqPrepare
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
name|OJPreparingStmt
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
name|RelOptTable
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
name|RelOptUtil
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
name|util
operator|.
name|Util
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
name|Type
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

begin_comment
comment|/**  * Table whose contents are defined using an SQL statement.  *  *<p>It is not evaluated; it is expanded during query planning.</p>  */
end_comment

begin_class
specifier|public
class|class
name|ViewTable
parameter_list|<
name|T
parameter_list|>
extends|extends
name|AbstractTable
argument_list|<
name|T
argument_list|>
implements|implements
name|TranslatableTable
argument_list|<
name|T
argument_list|>
block|{
specifier|private
specifier|final
name|String
name|viewSql
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|schemaPath
decl_stmt|;
specifier|public
name|ViewTable
parameter_list|(
name|Schema
name|schema
parameter_list|,
name|Type
name|elementType
parameter_list|,
name|RelDataType
name|relDataType
parameter_list|,
name|String
name|tableName
parameter_list|,
name|String
name|viewSql
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|schemaPath
parameter_list|)
block|{
name|super
argument_list|(
name|schema
argument_list|,
name|elementType
argument_list|,
name|relDataType
argument_list|,
name|tableName
argument_list|)
expr_stmt|;
name|this
operator|.
name|viewSql
operator|=
name|viewSql
expr_stmt|;
name|this
operator|.
name|schemaPath
operator|=
name|schemaPath
expr_stmt|;
block|}
comment|/** Table function that returns a view. */
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|TableFunction
argument_list|<
name|T
argument_list|>
name|viewFunction
parameter_list|(
specifier|final
name|Schema
name|schema
parameter_list|,
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|String
name|viewSql
parameter_list|,
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|schemaPath
parameter_list|)
block|{
return|return
operator|new
name|ViewTableFunction
argument_list|<
name|T
argument_list|>
argument_list|(
name|schema
argument_list|,
name|name
argument_list|,
name|viewSql
argument_list|,
name|schemaPath
argument_list|)
return|;
block|}
specifier|public
name|Enumerator
argument_list|<
name|T
argument_list|>
name|enumerator
parameter_list|()
block|{
return|return
name|schema
operator|.
name|getQueryProvider
argument_list|()
operator|.
operator|<
name|T
operator|>
name|createQuery
argument_list|(
name|getExpression
argument_list|()
argument_list|,
name|elementType
argument_list|)
operator|.
name|enumerator
argument_list|()
return|;
block|}
specifier|public
name|RelNode
name|toRel
parameter_list|(
name|RelOptTable
operator|.
name|ToRelContext
name|context
parameter_list|,
name|RelOptTable
name|relOptTable
parameter_list|)
block|{
return|return
name|expandView
argument_list|(
name|context
operator|.
name|getPreparingStmt
argument_list|()
argument_list|,
operator|(
operator|(
name|JavaTypeFactory
operator|)
name|context
operator|.
name|getCluster
argument_list|()
operator|.
name|getTypeFactory
argument_list|()
operator|)
operator|.
name|createType
argument_list|(
name|elementType
argument_list|)
argument_list|,
name|viewSql
argument_list|)
return|;
block|}
specifier|private
name|RelNode
name|expandView
parameter_list|(
name|OJPreparingStmt
name|preparingStmt
parameter_list|,
name|RelDataType
name|rowType
parameter_list|,
name|String
name|queryString
parameter_list|)
block|{
try|try
block|{
name|RelNode
name|rel
init|=
name|preparingStmt
operator|.
name|expandView
argument_list|(
name|rowType
argument_list|,
name|queryString
argument_list|,
name|schemaPath
argument_list|)
decl_stmt|;
name|rel
operator|=
name|RelOptUtil
operator|.
name|createCastRel
argument_list|(
name|rel
argument_list|,
name|rowType
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|rel
operator|=
name|preparingStmt
operator|.
name|flattenTypes
argument_list|(
name|rel
argument_list|,
literal|false
argument_list|)
expr_stmt|;
return|return
name|rel
return|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
throw|throw
name|Util
operator|.
name|newInternal
argument_list|(
name|e
argument_list|,
literal|"Error while parsing view definition:  "
operator|+
name|queryString
argument_list|)
throw|;
block|}
block|}
specifier|private
specifier|static
class|class
name|ViewTableFunction
parameter_list|<
name|T
parameter_list|>
implements|implements
name|TableFunction
argument_list|<
name|T
argument_list|>
block|{
specifier|private
specifier|final
name|JavaTypeFactory
name|typeFactory
decl_stmt|;
specifier|private
specifier|final
name|OptiqConnection
name|optiqConnection
decl_stmt|;
specifier|private
specifier|final
name|String
name|viewSql
decl_stmt|;
specifier|private
specifier|final
name|Schema
name|schema
decl_stmt|;
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|schemaPath
decl_stmt|;
specifier|private
name|ViewTableFunction
parameter_list|(
name|Schema
name|schema
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|viewSql
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|schemaPath
parameter_list|)
block|{
name|this
operator|.
name|viewSql
operator|=
name|viewSql
expr_stmt|;
name|this
operator|.
name|schema
operator|=
name|schema
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|schemaPath
operator|=
name|schemaPath
expr_stmt|;
name|this
operator|.
name|optiqConnection
operator|=
operator|(
name|OptiqConnection
operator|)
name|schema
operator|.
name|getQueryProvider
argument_list|()
expr_stmt|;
name|this
operator|.
name|typeFactory
operator|=
name|optiqConnection
operator|.
name|getTypeFactory
argument_list|()
expr_stmt|;
block|}
specifier|public
name|List
argument_list|<
name|Parameter
argument_list|>
name|getParameters
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
name|Table
argument_list|<
name|T
argument_list|>
name|apply
parameter_list|(
name|List
argument_list|<
name|Object
argument_list|>
name|arguments
parameter_list|)
block|{
name|OptiqPrepare
operator|.
name|ParseResult
name|parsed
init|=
name|OptiqPrepare
operator|.
name|DEFAULT_FACTORY
operator|.
name|apply
argument_list|()
operator|.
name|parse
argument_list|(
operator|new
name|OptiqPrepare
operator|.
name|Context
argument_list|()
block|{
specifier|public
name|JavaTypeFactory
name|getTypeFactory
parameter_list|()
block|{
return|return
name|typeFactory
return|;
block|}
specifier|public
name|Schema
name|getRootSchema
parameter_list|()
block|{
return|return
name|optiqConnection
operator|.
name|getRootSchema
argument_list|()
return|;
block|}
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getDefaultSchemaPath
parameter_list|()
block|{
return|return
name|schemaPath
return|;
block|}
block|}
argument_list|,
name|viewSql
argument_list|)
decl_stmt|;
return|return
operator|new
name|ViewTable
argument_list|<
name|T
argument_list|>
argument_list|(
name|schema
argument_list|,
name|typeFactory
operator|.
name|getJavaClass
argument_list|(
name|parsed
operator|.
name|rowType
argument_list|)
argument_list|,
name|parsed
operator|.
name|rowType
argument_list|,
name|name
argument_list|,
name|viewSql
argument_list|,
name|schemaPath
argument_list|)
return|;
block|}
specifier|public
name|Type
name|getElementType
parameter_list|()
block|{
return|return
name|apply
argument_list|(
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|)
operator|.
name|getElementType
argument_list|()
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End ViewTable.java
end_comment

end_unit

