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
name|sql
operator|.
name|type
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
name|util
operator|.
name|Util
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
name|ImmutableMap
import|;
end_import

begin_comment
comment|/**  * SqlTypeExplicitPrecedenceList implements the  * {@link RelDataTypePrecedenceList} interface via an explicit list of  * {@link SqlTypeName} entries.  */
end_comment

begin_class
specifier|public
class|class
name|SqlTypeExplicitPrecedenceList
implements|implements
name|RelDataTypePrecedenceList
block|{
comment|//~ Static fields/initializers ---------------------------------------------
comment|// NOTE jvs 25-Jan-2005:  the null entries delimit equivalence
comment|// classes
specifier|private
specifier|static
specifier|final
name|List
argument_list|<
name|SqlTypeName
argument_list|>
name|NUMERIC_TYPES
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|SqlTypeName
operator|.
name|TINYINT
argument_list|,
literal|null
argument_list|,
name|SqlTypeName
operator|.
name|SMALLINT
argument_list|,
literal|null
argument_list|,
name|SqlTypeName
operator|.
name|INTEGER
argument_list|,
literal|null
argument_list|,
name|SqlTypeName
operator|.
name|BIGINT
argument_list|,
literal|null
argument_list|,
name|SqlTypeName
operator|.
name|DECIMAL
argument_list|,
literal|null
argument_list|,
name|SqlTypeName
operator|.
name|REAL
argument_list|,
literal|null
argument_list|,
name|SqlTypeName
operator|.
name|FLOAT
argument_list|,
name|SqlTypeName
operator|.
name|DOUBLE
argument_list|)
decl_stmt|;
comment|/**    * Map from SqlTypeName to corresponding precedence list.    *    * @sql.2003 Part 2 Section 9.5    */
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|SqlTypeName
argument_list|,
name|SqlTypeExplicitPrecedenceList
argument_list|>
name|TYPE_NAME_TO_PRECEDENCE_LIST
init|=
name|ImmutableMap
operator|.
expr|<
name|SqlTypeName
decl_stmt|,
name|SqlTypeExplicitPrecedenceList
decl|>
name|builder
argument_list|()
decl|.
name|put
argument_list|(
name|SqlTypeName
operator|.
name|BOOLEAN
argument_list|,
name|list
argument_list|(
name|SqlTypeName
operator|.
name|BOOLEAN
argument_list|)
argument_list|)
decl|.
name|put
argument_list|(
name|SqlTypeName
operator|.
name|TINYINT
argument_list|,
name|numeric
argument_list|(
name|SqlTypeName
operator|.
name|TINYINT
argument_list|)
argument_list|)
decl|.
name|put
argument_list|(
name|SqlTypeName
operator|.
name|INTEGER
argument_list|,
name|numeric
argument_list|(
name|SqlTypeName
operator|.
name|INTEGER
argument_list|)
argument_list|)
decl|.
name|put
argument_list|(
name|SqlTypeName
operator|.
name|BIGINT
argument_list|,
name|numeric
argument_list|(
name|SqlTypeName
operator|.
name|BIGINT
argument_list|)
argument_list|)
decl|.
name|put
argument_list|(
name|SqlTypeName
operator|.
name|DECIMAL
argument_list|,
name|numeric
argument_list|(
name|SqlTypeName
operator|.
name|DECIMAL
argument_list|)
argument_list|)
decl|.
name|put
argument_list|(
name|SqlTypeName
operator|.
name|REAL
argument_list|,
name|numeric
argument_list|(
name|SqlTypeName
operator|.
name|REAL
argument_list|)
argument_list|)
decl|.
name|put
argument_list|(
name|SqlTypeName
operator|.
name|FLOAT
argument_list|,
name|numeric
argument_list|(
name|SqlTypeName
operator|.
name|FLOAT
argument_list|)
argument_list|)
decl|.
name|put
argument_list|(
name|SqlTypeName
operator|.
name|DOUBLE
argument_list|,
name|numeric
argument_list|(
name|SqlTypeName
operator|.
name|DOUBLE
argument_list|)
argument_list|)
decl|.
name|put
argument_list|(
name|SqlTypeName
operator|.
name|CHAR
argument_list|,
name|list
argument_list|(
name|SqlTypeName
operator|.
name|CHAR
argument_list|,
name|SqlTypeName
operator|.
name|VARCHAR
argument_list|)
argument_list|)
decl|.
name|put
argument_list|(
name|SqlTypeName
operator|.
name|VARCHAR
argument_list|,
name|list
argument_list|(
name|SqlTypeName
operator|.
name|VARCHAR
argument_list|)
argument_list|)
decl|.
name|put
argument_list|(
name|SqlTypeName
operator|.
name|BINARY
argument_list|,
name|list
argument_list|(
name|SqlTypeName
operator|.
name|BINARY
argument_list|,
name|SqlTypeName
operator|.
name|VARBINARY
argument_list|)
argument_list|)
decl|.
name|put
argument_list|(
name|SqlTypeName
operator|.
name|VARBINARY
argument_list|,
name|list
argument_list|(
name|SqlTypeName
operator|.
name|VARBINARY
argument_list|)
argument_list|)
decl|.
name|put
argument_list|(
name|SqlTypeName
operator|.
name|DATE
argument_list|,
name|list
argument_list|(
name|SqlTypeName
operator|.
name|DATE
argument_list|)
argument_list|)
decl|.
name|put
argument_list|(
name|SqlTypeName
operator|.
name|TIME
argument_list|,
name|list
argument_list|(
name|SqlTypeName
operator|.
name|TIME
argument_list|)
argument_list|)
decl|.
name|put
argument_list|(
name|SqlTypeName
operator|.
name|TIMESTAMP
argument_list|,
name|list
argument_list|(
name|SqlTypeName
operator|.
name|TIMESTAMP
argument_list|)
argument_list|)
decl|.
name|put
argument_list|(
name|SqlTypeName
operator|.
name|INTERVAL_YEAR_MONTH
argument_list|,
name|list
argument_list|(
name|SqlTypeName
operator|.
name|INTERVAL_YEAR_MONTH
argument_list|)
argument_list|)
decl|.
name|put
argument_list|(
name|SqlTypeName
operator|.
name|INTERVAL_DAY_TIME
argument_list|,
name|list
argument_list|(
name|SqlTypeName
operator|.
name|INTERVAL_DAY_TIME
argument_list|)
argument_list|)
decl|.
name|build
argument_list|()
decl_stmt|;
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|List
argument_list|<
name|SqlTypeName
argument_list|>
name|typeNames
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|SqlTypeExplicitPrecedenceList
parameter_list|(
name|List
argument_list|<
name|SqlTypeName
argument_list|>
name|typeNames
parameter_list|)
block|{
name|this
operator|.
name|typeNames
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|typeNames
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|private
specifier|static
name|SqlTypeExplicitPrecedenceList
name|list
parameter_list|(
name|SqlTypeName
modifier|...
name|array
parameter_list|)
block|{
return|return
operator|new
name|SqlTypeExplicitPrecedenceList
argument_list|(
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|array
argument_list|)
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|SqlTypeExplicitPrecedenceList
name|numeric
parameter_list|(
name|SqlTypeName
name|typeName
parameter_list|)
block|{
name|int
name|i
init|=
name|getListPosition
argument_list|(
name|typeName
argument_list|,
name|NUMERIC_TYPES
argument_list|)
decl_stmt|;
return|return
operator|new
name|SqlTypeExplicitPrecedenceList
argument_list|(
name|Util
operator|.
name|skip
argument_list|(
name|NUMERIC_TYPES
argument_list|,
name|i
argument_list|)
argument_list|)
return|;
block|}
comment|// implement RelDataTypePrecedenceList
specifier|public
name|boolean
name|containsType
parameter_list|(
name|RelDataType
name|type
parameter_list|)
block|{
name|SqlTypeName
name|typeName
init|=
name|type
operator|.
name|getSqlTypeName
argument_list|()
decl_stmt|;
return|return
name|typeName
operator|!=
literal|null
operator|&&
name|typeNames
operator|.
name|contains
argument_list|(
name|typeName
argument_list|)
return|;
block|}
comment|// implement RelDataTypePrecedenceList
specifier|public
name|int
name|compareTypePrecedence
parameter_list|(
name|RelDataType
name|type1
parameter_list|,
name|RelDataType
name|type2
parameter_list|)
block|{
assert|assert
name|containsType
argument_list|(
name|type1
argument_list|)
assert|;
assert|assert
name|containsType
argument_list|(
name|type2
argument_list|)
assert|;
name|int
name|p1
init|=
name|getListPosition
argument_list|(
name|type1
operator|.
name|getSqlTypeName
argument_list|()
argument_list|,
name|typeNames
argument_list|)
decl_stmt|;
name|int
name|p2
init|=
name|getListPosition
argument_list|(
name|type2
operator|.
name|getSqlTypeName
argument_list|()
argument_list|,
name|typeNames
argument_list|)
decl_stmt|;
return|return
name|p2
operator|-
name|p1
return|;
block|}
specifier|private
specifier|static
name|int
name|getListPosition
parameter_list|(
name|SqlTypeName
name|type
parameter_list|,
name|List
argument_list|<
name|SqlTypeName
argument_list|>
name|list
parameter_list|)
block|{
name|int
name|i
init|=
name|list
operator|.
name|indexOf
argument_list|(
name|type
argument_list|)
decl_stmt|;
assert|assert
name|i
operator|!=
operator|-
literal|1
assert|;
comment|// adjust for precedence equivalence classes
for|for
control|(
name|int
name|j
init|=
name|i
operator|-
literal|1
init|;
name|j
operator|>=
literal|0
condition|;
operator|--
name|j
control|)
block|{
if|if
condition|(
name|list
operator|.
name|get
argument_list|(
name|j
argument_list|)
operator|==
literal|null
condition|)
block|{
return|return
name|j
return|;
block|}
block|}
return|return
name|i
return|;
block|}
specifier|static
name|RelDataTypePrecedenceList
name|getListForType
parameter_list|(
name|RelDataType
name|type
parameter_list|)
block|{
name|SqlTypeName
name|typeName
init|=
name|type
operator|.
name|getSqlTypeName
argument_list|()
decl_stmt|;
if|if
condition|(
name|typeName
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|TYPE_NAME_TO_PRECEDENCE_LIST
operator|.
name|get
argument_list|(
name|typeName
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End SqlTypeExplicitPrecedenceList.java
end_comment

end_unit

