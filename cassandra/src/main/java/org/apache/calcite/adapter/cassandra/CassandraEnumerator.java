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
name|adapter
operator|.
name|cassandra
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
name|util
operator|.
name|ByteString
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
name|linq4j
operator|.
name|Enumerator
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
name|type
operator|.
name|RelDataTypeFactory
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
name|type
operator|.
name|RelDataTypeField
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
name|type
operator|.
name|RelDataTypeSystem
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
name|type
operator|.
name|RelProtoDataType
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
name|type
operator|.
name|SqlTypeFactoryImpl
import|;
end_import

begin_import
import|import
name|com
operator|.
name|datastax
operator|.
name|oss
operator|.
name|driver
operator|.
name|api
operator|.
name|core
operator|.
name|cql
operator|.
name|ResultSet
import|;
end_import

begin_import
import|import
name|com
operator|.
name|datastax
operator|.
name|oss
operator|.
name|driver
operator|.
name|api
operator|.
name|core
operator|.
name|cql
operator|.
name|Row
import|;
end_import

begin_import
import|import
name|com
operator|.
name|datastax
operator|.
name|oss
operator|.
name|driver
operator|.
name|api
operator|.
name|core
operator|.
name|data
operator|.
name|TupleValue
import|;
end_import

begin_import
import|import
name|org
operator|.
name|checkerframework
operator|.
name|checker
operator|.
name|nullness
operator|.
name|qual
operator|.
name|Nullable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|time
operator|.
name|Instant
import|;
end_import

begin_import
import|import
name|java
operator|.
name|time
operator|.
name|LocalDate
import|;
end_import

begin_import
import|import
name|java
operator|.
name|time
operator|.
name|LocalTime
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
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
name|LinkedHashSet
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
name|Objects
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|IntStream
import|;
end_import

begin_comment
comment|/** Enumerator that reads from a Cassandra column family. */
end_comment

begin_class
class|class
name|CassandraEnumerator
implements|implements
name|Enumerator
argument_list|<
name|Object
argument_list|>
block|{
specifier|private
specifier|final
name|Iterator
argument_list|<
name|Row
argument_list|>
name|iterator
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|fieldTypes
decl_stmt|;
annotation|@
name|Nullable
specifier|private
name|Row
name|current
decl_stmt|;
comment|/** Creates a CassandraEnumerator.    *    * @param results Cassandra result set ({@link com.datastax.oss.driver.api.core.cql.ResultSet})    * @param protoRowType The type of resulting rows    */
name|CassandraEnumerator
parameter_list|(
name|ResultSet
name|results
parameter_list|,
name|RelProtoDataType
name|protoRowType
parameter_list|)
block|{
name|this
operator|.
name|iterator
operator|=
name|results
operator|.
name|iterator
argument_list|()
expr_stmt|;
name|this
operator|.
name|current
operator|=
literal|null
expr_stmt|;
specifier|final
name|RelDataTypeFactory
name|typeFactory
init|=
operator|new
name|SqlTypeFactoryImpl
argument_list|(
name|RelDataTypeSystem
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|this
operator|.
name|fieldTypes
operator|=
name|protoRowType
operator|.
name|apply
argument_list|(
name|typeFactory
argument_list|)
operator|.
name|getFieldList
argument_list|()
expr_stmt|;
block|}
comment|/** Produces the next row from the results.    *    * @return A new row from the results    */
annotation|@
name|Override
specifier|public
name|Object
name|current
parameter_list|()
block|{
if|if
condition|(
name|fieldTypes
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
comment|// If we just have one field, produce it directly
return|return
name|currentRowField
argument_list|(
literal|0
argument_list|)
return|;
block|}
else|else
block|{
comment|// Build an array with all fields in this row
name|Object
index|[]
name|row
init|=
operator|new
name|Object
index|[
name|fieldTypes
operator|.
name|size
argument_list|()
index|]
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
name|fieldTypes
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|row
index|[
name|i
index|]
operator|=
name|currentRowField
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
return|return
name|row
return|;
block|}
block|}
comment|/** Get a field for the current row from the underlying object.    *    * @param index Index of the field within the Row object    */
specifier|private
annotation|@
name|Nullable
name|Object
name|currentRowField
parameter_list|(
name|int
name|index
parameter_list|)
block|{
assert|assert
name|current
operator|!=
literal|null
assert|;
specifier|final
name|Object
name|o
init|=
name|current
operator|.
name|get
argument_list|(
name|index
argument_list|,
name|CassandraSchema
operator|.
name|CODEC_REGISTRY
operator|.
name|codecFor
argument_list|(
name|current
operator|.
name|getColumnDefinitions
argument_list|()
operator|.
name|get
argument_list|(
name|index
argument_list|)
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|convertToEnumeratorObject
argument_list|(
name|o
argument_list|)
return|;
block|}
comment|/** Convert an object into the expected internal representation.    *    * @param obj Object to convert, if needed    */
specifier|private
annotation|@
name|Nullable
name|Object
name|convertToEnumeratorObject
parameter_list|(
annotation|@
name|Nullable
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|instanceof
name|ByteBuffer
condition|)
block|{
name|ByteBuffer
name|buf
init|=
operator|(
name|ByteBuffer
operator|)
name|obj
decl_stmt|;
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
name|buf
operator|.
name|remaining
argument_list|()
index|]
decl_stmt|;
name|buf
operator|.
name|get
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
operator|new
name|ByteString
argument_list|(
name|bytes
argument_list|)
return|;
block|}
if|else if
condition|(
name|obj
operator|instanceof
name|LocalDate
condition|)
block|{
comment|// converts dates to the expected numeric format
return|return
operator|(
operator|(
name|LocalDate
operator|)
name|obj
operator|)
operator|.
name|toEpochDay
argument_list|()
return|;
block|}
if|else if
condition|(
name|obj
operator|instanceof
name|Date
condition|)
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"JdkObsolete"
argument_list|)
name|long
name|milli
init|=
operator|(
operator|(
name|Date
operator|)
name|obj
operator|)
operator|.
name|toInstant
argument_list|()
operator|.
name|toEpochMilli
argument_list|()
decl_stmt|;
return|return
name|milli
return|;
block|}
if|else if
condition|(
name|obj
operator|instanceof
name|Instant
condition|)
block|{
return|return
operator|(
operator|(
name|Instant
operator|)
name|obj
operator|)
operator|.
name|toEpochMilli
argument_list|()
return|;
block|}
if|else if
condition|(
name|obj
operator|instanceof
name|LocalTime
condition|)
block|{
return|return
operator|(
operator|(
name|LocalTime
operator|)
name|obj
operator|)
operator|.
name|toNanoOfDay
argument_list|()
return|;
block|}
if|else if
condition|(
name|obj
operator|instanceof
name|LinkedHashSet
condition|)
block|{
comment|// MULTISET is handled as an array
return|return
operator|(
operator|(
name|LinkedHashSet
argument_list|<
name|?
argument_list|>
operator|)
name|obj
operator|)
operator|.
name|toArray
argument_list|()
return|;
block|}
if|else if
condition|(
name|obj
operator|instanceof
name|TupleValue
condition|)
block|{
comment|// STRUCT can be handled as an array
specifier|final
name|TupleValue
name|tupleValue
init|=
operator|(
name|TupleValue
operator|)
name|obj
decl_stmt|;
name|int
name|numComponents
init|=
name|tupleValue
operator|.
name|getType
argument_list|()
operator|.
name|getComponentTypes
argument_list|()
operator|.
name|size
argument_list|()
decl_stmt|;
return|return
name|IntStream
operator|.
name|range
argument_list|(
literal|0
argument_list|,
name|numComponents
argument_list|)
operator|.
name|mapToObj
argument_list|(
name|i
lambda|->
name|tupleValue
operator|.
name|get
argument_list|(
name|i
argument_list|,
name|CassandraSchema
operator|.
name|CODEC_REGISTRY
operator|.
name|codecFor
argument_list|(
name|tupleValue
operator|.
name|getType
argument_list|()
operator|.
name|getComponentTypes
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
argument_list|)
operator|.
name|map
argument_list|(
name|this
operator|::
name|convertToEnumeratorObject
argument_list|)
operator|.
name|map
argument_list|(
name|Objects
operator|::
name|requireNonNull
argument_list|)
comment|// "null" cannot appear inside collections
operator|.
name|toArray
argument_list|()
return|;
block|}
return|return
name|obj
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|moveNext
parameter_list|()
block|{
if|if
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|current
operator|=
name|iterator
operator|.
name|next
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|reset
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{
comment|// Nothing to do here
block|}
block|}
end_class

end_unit

