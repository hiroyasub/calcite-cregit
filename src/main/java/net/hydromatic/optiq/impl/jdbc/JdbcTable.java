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
name|function
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
name|DataContext
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
name|Table
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
name|sql
operator|.
name|SqlWriter
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
name|pretty
operator|.
name|SqlPrettyWriter
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
name|sql
operator|.
name|ResultSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Queryable that gets its data from a table within a JDBC connection.  *  *<p>The idea is not to read the whole table, however. The idea is to use  * this as a building block for a query, by applying Queryable operators  * such as {@link net.hydromatic.linq4j.Queryable#where(net.hydromatic.linq4j.function.Predicate2)}.  * The resulting queryable can then be converted to a SQL query, which can be  * executed efficiently on the JDBC server.</p>  *  * @author jhyde  */
end_comment

begin_class
class|class
name|JdbcTable
extends|extends
name|AbstractQueryable
argument_list|<
name|Object
index|[]
argument_list|>
implements|implements
name|Table
argument_list|<
name|Object
index|[]
argument_list|>
block|{
specifier|private
specifier|final
name|JdbcSchema
name|schema
decl_stmt|;
specifier|private
specifier|final
name|String
name|tableName
decl_stmt|;
specifier|private
specifier|final
name|RelDataType
name|rowType
decl_stmt|;
specifier|public
name|JdbcTable
parameter_list|(
name|RelDataType
name|rowType
parameter_list|,
name|JdbcSchema
name|schema
parameter_list|,
name|String
name|tableName
parameter_list|)
block|{
name|this
operator|.
name|rowType
operator|=
name|rowType
expr_stmt|;
name|this
operator|.
name|schema
operator|=
name|schema
expr_stmt|;
name|this
operator|.
name|tableName
operator|=
name|tableName
expr_stmt|;
assert|assert
name|rowType
operator|!=
literal|null
assert|;
assert|assert
name|schema
operator|!=
literal|null
assert|;
assert|assert
name|tableName
operator|!=
literal|null
assert|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"JdbcTable {"
operator|+
name|tableName
operator|+
literal|"}"
return|;
block|}
specifier|public
name|QueryProvider
name|getProvider
parameter_list|()
block|{
return|return
name|schema
operator|.
name|queryProvider
return|;
block|}
specifier|public
name|DataContext
name|getDataContext
parameter_list|()
block|{
return|return
name|schema
return|;
block|}
specifier|public
name|Type
name|getElementType
parameter_list|()
block|{
return|return
name|Object
index|[]
operator|.
name|class
return|;
block|}
specifier|public
name|Expression
name|getExpression
parameter_list|()
block|{
return|return
name|Expressions
operator|.
name|call
argument_list|(
name|schema
operator|.
name|getExpression
argument_list|()
argument_list|,
literal|"getTable"
argument_list|,
name|Expressions
operator|.
expr|<
name|Expression
operator|>
name|list
argument_list|()
operator|.
name|append
argument_list|(
name|Expressions
operator|.
name|constant
argument_list|(
name|tableName
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
name|Expressions
operator|.
name|constant
argument_list|(
name|getElementType
argument_list|()
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|Iterator
argument_list|<
name|Object
index|[]
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|Linq4j
operator|.
name|enumeratorIterator
argument_list|(
name|enumerator
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|Enumerator
argument_list|<
name|Object
index|[]
argument_list|>
name|enumerator
parameter_list|()
block|{
name|SqlWriter
name|writer
init|=
operator|new
name|SqlPrettyWriter
argument_list|(
name|schema
operator|.
name|dialect
argument_list|)
decl_stmt|;
name|writer
operator|.
name|keyword
argument_list|(
literal|"select"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|literal
argument_list|(
literal|"*"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|keyword
argument_list|(
literal|"from"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|identifier
argument_list|(
literal|"foodmart"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|literal
argument_list|(
literal|"."
argument_list|)
expr_stmt|;
name|writer
operator|.
name|identifier
argument_list|(
name|tableName
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql
init|=
name|writer
operator|.
name|toString
argument_list|()
decl_stmt|;
name|Function1
argument_list|<
name|ResultSet
argument_list|,
name|Function0
argument_list|<
name|Object
index|[]
argument_list|>
argument_list|>
name|rowBuilderFactory
decl_stmt|;
name|rowBuilderFactory
operator|=
name|JdbcUtils
operator|.
name|ObjectArrayRowBuilder
operator|.
name|factory
argument_list|(
name|JdbcUtils
operator|.
name|getPrimitives
argument_list|(
name|schema
operator|.
name|typeFactory
argument_list|,
name|rowType
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|JdbcUtils
operator|.
name|sqlEnumerator
argument_list|(
name|sql
argument_list|,
name|schema
argument_list|,
name|rowBuilderFactory
argument_list|)
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
block|}
end_class

begin_comment
comment|// End JdbcTable.java
end_comment

end_unit

