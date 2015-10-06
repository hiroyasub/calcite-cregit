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
name|avatica
operator|.
name|util
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
name|avatica
operator|.
name|ColumnMetaData
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
name|sql
operator|.
name|SQLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Struct
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
comment|/** Implementation of JDBC {@link Struct}. */
end_comment

begin_class
specifier|public
class|class
name|StructImpl
implements|implements
name|Struct
block|{
specifier|private
specifier|final
name|List
name|list
decl_stmt|;
specifier|public
name|StructImpl
parameter_list|(
name|List
name|list
parameter_list|)
block|{
name|this
operator|.
name|list
operator|=
name|list
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
specifier|final
name|Iterator
name|iterator
init|=
name|list
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
return|return
literal|"{}"
return|;
block|}
specifier|final
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"{"
argument_list|)
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
name|append
argument_list|(
name|buf
argument_list|,
name|iterator
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
return|return
name|buf
operator|.
name|append
argument_list|(
literal|"}"
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
name|buf
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|getSQLTypeName
parameter_list|()
throws|throws
name|SQLException
block|{
return|return
literal|"ROW"
return|;
block|}
annotation|@
name|Override
specifier|public
name|Object
index|[]
name|getAttributes
parameter_list|()
throws|throws
name|SQLException
block|{
return|return
name|list
operator|.
name|toArray
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Object
index|[]
name|getAttributes
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
name|map
parameter_list|)
throws|throws
name|SQLException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
comment|// TODO
block|}
specifier|private
name|void
name|append
parameter_list|(
name|StringBuilder
name|buf
parameter_list|,
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|==
literal|null
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|"null"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|buf
operator|.
name|append
argument_list|(
name|o
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Factory that can create a result set based on a list of values. */
specifier|public
interface|interface
name|Factory
block|{
name|ResultSet
name|create
parameter_list|(
name|ColumnMetaData
operator|.
name|AvaticaType
name|elementType
parameter_list|,
name|Iterable
argument_list|<
name|Object
argument_list|>
name|iterable
parameter_list|)
function_decl|;
block|}
block|}
end_class

begin_comment
comment|// End StructImpl.java
end_comment

end_unit

