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
name|csv
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
name|rules
operator|.
name|java
operator|.
name|EnumerableConvention
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
name|JavaRules
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
name|Pair
import|;
end_import

begin_import
import|import
name|au
operator|.
name|com
operator|.
name|bytecode
operator|.
name|opencsv
operator|.
name|CSVReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
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
comment|/**  * Table based on a CSV file.  */
end_comment

begin_class
specifier|public
class|class
name|CsvTable
extends|extends
name|AbstractQueryable
argument_list|<
name|Object
index|[]
argument_list|>
implements|implements
name|TranslatableTable
argument_list|<
name|Object
index|[]
argument_list|>
block|{
specifier|private
specifier|final
name|Schema
name|schema
decl_stmt|;
specifier|private
specifier|final
name|String
name|tableName
decl_stmt|;
specifier|private
specifier|final
name|File
name|file
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
name|CsvFieldType
argument_list|>
name|fieldTypes
decl_stmt|;
comment|/** Creates a CsvTable. */
name|CsvTable
parameter_list|(
name|Schema
name|schema
parameter_list|,
name|String
name|tableName
parameter_list|,
name|File
name|file
parameter_list|,
name|RelDataType
name|rowType
parameter_list|,
name|List
argument_list|<
name|CsvFieldType
argument_list|>
name|fieldTypes
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
name|tableName
operator|=
name|tableName
expr_stmt|;
name|this
operator|.
name|file
operator|=
name|file
expr_stmt|;
name|this
operator|.
name|rowType
operator|=
name|rowType
expr_stmt|;
name|this
operator|.
name|fieldTypes
operator|=
name|fieldTypes
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
literal|"CsvTable {"
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
name|getQueryProvider
argument_list|()
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
name|Class
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
name|RelDataType
name|getRowType
parameter_list|()
block|{
return|return
name|rowType
return|;
block|}
specifier|public
name|Statistic
name|getStatistic
parameter_list|()
block|{
return|return
name|Statistics
operator|.
name|UNKNOWN
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
name|convert_
argument_list|(
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
argument_list|,
name|CsvTable
operator|.
name|class
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
return|return
operator|new
name|CsvEnumerator
argument_list|(
name|file
argument_list|,
name|fieldTypes
operator|.
name|toArray
argument_list|(
operator|new
name|CsvFieldType
index|[
name|fieldTypes
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
return|;
block|}
comment|/** Returns an enumerable over a given projection of the fields. */
specifier|public
name|Enumerable
argument_list|<
name|Object
index|[]
argument_list|>
name|project
parameter_list|(
specifier|final
name|int
index|[]
name|fields
parameter_list|)
block|{
return|return
operator|new
name|AbstractEnumerable
argument_list|<
name|Object
index|[]
argument_list|>
argument_list|()
block|{
specifier|public
name|Enumerator
argument_list|<
name|Object
index|[]
argument_list|>
name|enumerator
parameter_list|()
block|{
return|return
operator|new
name|CsvEnumerator
argument_list|(
name|file
argument_list|,
name|fieldTypes
operator|.
name|toArray
argument_list|(
operator|new
name|CsvFieldType
index|[
name|fieldTypes
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|,
name|fields
argument_list|)
return|;
block|}
block|}
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
operator|new
name|JavaRules
operator|.
name|EnumerableTableAccessRel
argument_list|(
name|context
operator|.
name|getCluster
argument_list|()
argument_list|,
name|context
operator|.
name|getCluster
argument_list|()
operator|.
name|traitSetOf
argument_list|(
name|EnumerableConvention
operator|.
name|ARRAY
argument_list|)
argument_list|,
name|relOptTable
argument_list|,
name|getExpression
argument_list|()
argument_list|,
name|getElementType
argument_list|()
argument_list|)
return|;
block|}
comment|/** Deduces the names and types of a table's columns by reading the first line    * of a CSV file. */
specifier|static
name|RelDataType
name|deduceRowType
parameter_list|(
name|JavaTypeFactory
name|typeFactory
parameter_list|,
name|File
name|file
parameter_list|,
name|List
argument_list|<
name|CsvFieldType
argument_list|>
name|fieldTypes
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|RelDataType
argument_list|>
name|types
init|=
operator|new
name|ArrayList
argument_list|<
name|RelDataType
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|names
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|CSVReader
name|reader
init|=
literal|null
decl_stmt|;
try|try
block|{
name|reader
operator|=
operator|new
name|CSVReader
argument_list|(
operator|new
name|FileReader
argument_list|(
name|file
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
index|[]
name|strings
init|=
name|reader
operator|.
name|readNext
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|string
range|:
name|strings
control|)
block|{
specifier|final
name|String
name|name
decl_stmt|;
specifier|final
name|CsvFieldType
name|fieldType
decl_stmt|;
specifier|final
name|int
name|colon
init|=
name|string
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
decl_stmt|;
if|if
condition|(
name|colon
operator|>=
literal|0
condition|)
block|{
name|name
operator|=
name|string
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|colon
argument_list|)
expr_stmt|;
name|String
name|typeString
init|=
name|string
operator|.
name|substring
argument_list|(
name|colon
operator|+
literal|1
argument_list|)
decl_stmt|;
name|fieldType
operator|=
name|CsvFieldType
operator|.
name|of
argument_list|(
name|typeString
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|name
operator|=
name|string
expr_stmt|;
name|fieldType
operator|=
literal|null
expr_stmt|;
block|}
specifier|final
name|RelDataType
name|type
decl_stmt|;
if|if
condition|(
name|fieldType
operator|==
literal|null
condition|)
block|{
name|type
operator|=
name|typeFactory
operator|.
name|createJavaType
argument_list|(
name|String
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|type
operator|=
name|fieldType
operator|.
name|toType
argument_list|(
name|typeFactory
argument_list|)
expr_stmt|;
block|}
name|names
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|fieldTypes
operator|.
name|add
argument_list|(
name|fieldType
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
finally|finally
block|{
if|if
condition|(
name|reader
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
block|}
block|}
if|if
condition|(
name|names
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|names
operator|.
name|add
argument_list|(
literal|"line"
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
name|typeFactory
operator|.
name|createJavaType
argument_list|(
name|String
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|typeFactory
operator|.
name|createStructType
argument_list|(
name|Pair
operator|.
name|zip
argument_list|(
name|names
argument_list|,
name|types
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End CsvTable.java
end_comment

end_unit

