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
name|sql
operator|.
name|type
package|;
end_package

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
name|ImmutableSet
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
name|util
operator|.
name|HashSet
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
name|Set
import|;
end_import

begin_comment
comment|/**  * Rules that determine whether a type is castable from another type.  *  *<p>These rules specify the conversion matrix with explicit CAST.  *  *<p>The implicit type coercion matrix should be a sub-set of this explicit one.  * We do not define an implicit type coercion matrix, instead we have specific  * coercion rules for all kinds of SQL contexts which actually define the "matrix".  *  *<p>To add a new implementation to this class, follow  * these steps:  *  *<ol>  *<li>Initialize a {@link SqlTypeMappingRules.Builder} instance  *   with default mappings of {@link SqlTypeCoercionRule#INSTANCE}.</li>  *<li>Modify the mappings with the Builder.</li>  *<li>Construct a new {@link SqlTypeCoercionRule} instance with method  *   {@link #instance(Map)}.</li>  *<li>Set the {@link SqlTypeCoercionRule} instance into the  *   {@link org.apache.calcite.sql.validate.SqlValidator}.</li>  *</ol>  *  *<p>The code snippet below illustrates how to implement a customized instance.  *  *<pre>  *     // Initialize a Builder instance with the default mappings.  *     Builder builder = SqlTypeMappingRules.builder();  *     builder.addAll(SqlTypeCoercionRules.instance().getTypeMapping());  *  *     // Do the tweak, for example, if we want to add a rule to allow  *     // coerce BOOLEAN to TIMESTAMP.  *     builder.add(SqlTypeName.TIMESTAMP,  *         builder.copyValues(SqlTypeName.TIMESTAMP)  *             .add(SqlTypeName.BOOLEAN).build());  *  *     // Initialize a SqlTypeCoercionRules with the new builder mappings.  *     SqlTypeCoercionRules typeCoercionRules = SqlTypeCoercionRules.instance(builder.map);  *  *     // Set the SqlTypeCoercionRules instance into the SqlValidator.  *     SqlValidator.Config validatorConf ...;  *     validatorConf.withTypeCoercionRules(typeCoercionRules);  *     // Use this conf to initialize the SqlValidator.  *</pre>  */
end_comment

begin_class
specifier|public
class|class
name|SqlTypeCoercionRule
implements|implements
name|SqlTypeMappingRule
block|{
comment|//~ Static fields/initializers ---------------------------------------------
specifier|private
specifier|static
specifier|final
name|SqlTypeCoercionRule
name|INSTANCE
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|ThreadLocal
argument_list|<
annotation|@
name|Nullable
name|SqlTypeCoercionRule
argument_list|>
name|THREAD_PROVIDERS
init|=
name|ThreadLocal
operator|.
name|withInitial
argument_list|(
parameter_list|()
lambda|->
name|SqlTypeCoercionRule
operator|.
name|INSTANCE
argument_list|)
decl_stmt|;
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|Map
argument_list|<
name|SqlTypeName
argument_list|,
name|ImmutableSet
argument_list|<
name|SqlTypeName
argument_list|>
argument_list|>
name|map
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a {@code SqlTypeCoercionRules} with specified type mappings {@code map}.    *    *<p>Make this constructor private intentionally, use {@link #instance()}.    *    * @param map The type mapping, for each map entry, the values types can be coerced to    *            the key type    */
specifier|private
name|SqlTypeCoercionRule
parameter_list|(
name|Map
argument_list|<
name|SqlTypeName
argument_list|,
name|ImmutableSet
argument_list|<
name|SqlTypeName
argument_list|>
argument_list|>
name|map
parameter_list|)
block|{
name|this
operator|.
name|map
operator|=
name|ImmutableMap
operator|.
name|copyOf
argument_list|(
name|map
argument_list|)
expr_stmt|;
block|}
static|static
block|{
comment|// We use coerceRules when we're casting
specifier|final
name|SqlTypeMappingRules
operator|.
name|Builder
name|coerceRules
init|=
name|SqlTypeMappingRules
operator|.
name|builder
argument_list|()
decl_stmt|;
name|coerceRules
operator|.
name|addAll
argument_list|(
name|SqlTypeAssignmentRule
operator|.
name|instance
argument_list|()
operator|.
name|getTypeMapping
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|Set
argument_list|<
name|SqlTypeName
argument_list|>
name|rule
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
comment|// Make numbers symmetrical,
comment|// and make VARCHAR, CHAR, BOOLEAN and TIMESTAMP castable to/from numbers
name|rule
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|TINYINT
argument_list|)
expr_stmt|;
name|rule
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|SMALLINT
argument_list|)
expr_stmt|;
name|rule
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|INTEGER
argument_list|)
expr_stmt|;
name|rule
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|BIGINT
argument_list|)
expr_stmt|;
name|rule
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|DECIMAL
argument_list|)
expr_stmt|;
name|rule
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|FLOAT
argument_list|)
expr_stmt|;
name|rule
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|REAL
argument_list|)
expr_stmt|;
name|rule
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|DOUBLE
argument_list|)
expr_stmt|;
name|rule
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|CHAR
argument_list|)
expr_stmt|;
name|rule
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|VARCHAR
argument_list|)
expr_stmt|;
name|rule
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|BOOLEAN
argument_list|)
expr_stmt|;
name|rule
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|TIMESTAMP
argument_list|)
expr_stmt|;
name|rule
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|TIMESTAMP_WITH_LOCAL_TIME_ZONE
argument_list|)
expr_stmt|;
name|coerceRules
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|TINYINT
argument_list|,
name|rule
argument_list|)
expr_stmt|;
name|coerceRules
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|SMALLINT
argument_list|,
name|rule
argument_list|)
expr_stmt|;
name|coerceRules
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|INTEGER
argument_list|,
name|rule
argument_list|)
expr_stmt|;
name|coerceRules
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|BIGINT
argument_list|,
name|rule
argument_list|)
expr_stmt|;
name|coerceRules
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|FLOAT
argument_list|,
name|rule
argument_list|)
expr_stmt|;
name|coerceRules
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|REAL
argument_list|,
name|rule
argument_list|)
expr_stmt|;
name|coerceRules
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|DECIMAL
argument_list|,
name|rule
argument_list|)
expr_stmt|;
name|coerceRules
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|DOUBLE
argument_list|,
name|rule
argument_list|)
expr_stmt|;
name|coerceRules
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|CHAR
argument_list|,
name|rule
argument_list|)
expr_stmt|;
name|coerceRules
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|VARCHAR
argument_list|,
name|rule
argument_list|)
expr_stmt|;
comment|// Exact numeric types are castable from intervals
for|for
control|(
name|SqlTypeName
name|exactType
range|:
name|SqlTypeName
operator|.
name|EXACT_TYPES
control|)
block|{
name|coerceRules
operator|.
name|add
argument_list|(
name|exactType
argument_list|,
name|coerceRules
operator|.
name|copyValues
argument_list|(
name|exactType
argument_list|)
operator|.
name|addAll
argument_list|(
name|SqlTypeName
operator|.
name|INTERVAL_TYPES
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Intervals are castable from exact numeric
for|for
control|(
name|SqlTypeName
name|typeName
range|:
name|SqlTypeName
operator|.
name|INTERVAL_TYPES
control|)
block|{
name|coerceRules
operator|.
name|add
argument_list|(
name|typeName
argument_list|,
name|coerceRules
operator|.
name|copyValues
argument_list|(
name|typeName
argument_list|)
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|TINYINT
argument_list|)
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|SMALLINT
argument_list|)
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|INTEGER
argument_list|)
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|BIGINT
argument_list|)
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|DECIMAL
argument_list|)
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|CHAR
argument_list|)
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|VARCHAR
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// BINARY is castable from VARBINARY, CHARACTERS.
name|coerceRules
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|BINARY
argument_list|,
name|coerceRules
operator|.
name|copyValues
argument_list|(
name|SqlTypeName
operator|.
name|BINARY
argument_list|)
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|VARBINARY
argument_list|)
operator|.
name|addAll
argument_list|(
name|SqlTypeName
operator|.
name|CHAR_TYPES
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
comment|// VARBINARY is castable from BINARY, CHARACTERS.
name|coerceRules
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|VARBINARY
argument_list|,
name|coerceRules
operator|.
name|copyValues
argument_list|(
name|SqlTypeName
operator|.
name|VARBINARY
argument_list|)
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|BINARY
argument_list|)
operator|.
name|addAll
argument_list|(
name|SqlTypeName
operator|.
name|CHAR_TYPES
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
comment|// VARCHAR is castable from BOOLEAN, DATE, TIME, TIMESTAMP, numeric types, binary and
comment|// intervals
name|coerceRules
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|VARCHAR
argument_list|,
name|coerceRules
operator|.
name|copyValues
argument_list|(
name|SqlTypeName
operator|.
name|VARCHAR
argument_list|)
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|CHAR
argument_list|)
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|BOOLEAN
argument_list|)
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|DATE
argument_list|)
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|TIME
argument_list|)
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|TIMESTAMP
argument_list|)
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|TIMESTAMP_WITH_LOCAL_TIME_ZONE
argument_list|)
operator|.
name|addAll
argument_list|(
name|SqlTypeName
operator|.
name|BINARY_TYPES
argument_list|)
operator|.
name|addAll
argument_list|(
name|SqlTypeName
operator|.
name|NUMERIC_TYPES
argument_list|)
operator|.
name|addAll
argument_list|(
name|SqlTypeName
operator|.
name|INTERVAL_TYPES
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
comment|// CHAR is castable from BOOLEAN, DATE, TIME, TIMESTAMP, numeric types, binary and
comment|// intervals
name|coerceRules
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|CHAR
argument_list|,
name|coerceRules
operator|.
name|copyValues
argument_list|(
name|SqlTypeName
operator|.
name|CHAR
argument_list|)
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|VARCHAR
argument_list|)
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|BOOLEAN
argument_list|)
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|DATE
argument_list|)
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|TIME
argument_list|)
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|TIMESTAMP
argument_list|)
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|TIMESTAMP_WITH_LOCAL_TIME_ZONE
argument_list|)
operator|.
name|addAll
argument_list|(
name|SqlTypeName
operator|.
name|BINARY_TYPES
argument_list|)
operator|.
name|addAll
argument_list|(
name|SqlTypeName
operator|.
name|NUMERIC_TYPES
argument_list|)
operator|.
name|addAll
argument_list|(
name|SqlTypeName
operator|.
name|INTERVAL_TYPES
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
comment|// BOOLEAN is castable from ...
name|coerceRules
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|BOOLEAN
argument_list|,
name|coerceRules
operator|.
name|copyValues
argument_list|(
name|SqlTypeName
operator|.
name|BOOLEAN
argument_list|)
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|CHAR
argument_list|)
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|VARCHAR
argument_list|)
operator|.
name|addAll
argument_list|(
name|SqlTypeName
operator|.
name|NUMERIC_TYPES
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
comment|// DATE, TIME, and TIMESTAMP are castable from
comment|// CHAR and VARCHAR.
comment|// DATE is castable from...
name|coerceRules
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|DATE
argument_list|,
name|coerceRules
operator|.
name|copyValues
argument_list|(
name|SqlTypeName
operator|.
name|DATE
argument_list|)
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|TIMESTAMP
argument_list|)
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|TIMESTAMP_WITH_LOCAL_TIME_ZONE
argument_list|)
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|CHAR
argument_list|)
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|VARCHAR
argument_list|)
operator|.
name|addAll
argument_list|(
name|SqlTypeName
operator|.
name|BINARY_TYPES
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
comment|// TIME is castable from...
name|coerceRules
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|TIME
argument_list|,
name|coerceRules
operator|.
name|copyValues
argument_list|(
name|SqlTypeName
operator|.
name|TIME
argument_list|)
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|TIME_WITH_LOCAL_TIME_ZONE
argument_list|)
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|TIMESTAMP
argument_list|)
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|TIMESTAMP_WITH_LOCAL_TIME_ZONE
argument_list|)
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|CHAR
argument_list|)
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|VARCHAR
argument_list|)
operator|.
name|addAll
argument_list|(
name|SqlTypeName
operator|.
name|BINARY_TYPES
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
comment|// TIME WITH LOCAL TIME ZONE is castable from...
name|coerceRules
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|TIME_WITH_LOCAL_TIME_ZONE
argument_list|,
name|coerceRules
operator|.
name|copyValues
argument_list|(
name|SqlTypeName
operator|.
name|TIME_WITH_LOCAL_TIME_ZONE
argument_list|)
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|TIME
argument_list|)
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|TIMESTAMP
argument_list|)
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|TIMESTAMP_WITH_LOCAL_TIME_ZONE
argument_list|)
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|CHAR
argument_list|)
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|VARCHAR
argument_list|)
operator|.
name|addAll
argument_list|(
name|SqlTypeName
operator|.
name|BINARY_TYPES
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
comment|// TIMESTAMP is castable from...
name|coerceRules
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|TIMESTAMP
argument_list|,
name|coerceRules
operator|.
name|copyValues
argument_list|(
name|SqlTypeName
operator|.
name|TIMESTAMP
argument_list|)
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|TIMESTAMP_WITH_LOCAL_TIME_ZONE
argument_list|)
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|DATE
argument_list|)
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|TIME
argument_list|)
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|TIME_WITH_LOCAL_TIME_ZONE
argument_list|)
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|CHAR
argument_list|)
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|VARCHAR
argument_list|)
operator|.
name|addAll
argument_list|(
name|SqlTypeName
operator|.
name|BINARY_TYPES
argument_list|)
operator|.
name|addAll
argument_list|(
name|SqlTypeName
operator|.
name|NUMERIC_TYPES
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
comment|// TIMESTAMP WITH LOCAL TIME ZONE is castable from...
name|coerceRules
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|TIMESTAMP_WITH_LOCAL_TIME_ZONE
argument_list|,
name|coerceRules
operator|.
name|copyValues
argument_list|(
name|SqlTypeName
operator|.
name|TIMESTAMP_WITH_LOCAL_TIME_ZONE
argument_list|)
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|TIMESTAMP
argument_list|)
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|DATE
argument_list|)
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|TIME
argument_list|)
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|TIME_WITH_LOCAL_TIME_ZONE
argument_list|)
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|CHAR
argument_list|)
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|VARCHAR
argument_list|)
operator|.
name|addAll
argument_list|(
name|SqlTypeName
operator|.
name|BINARY_TYPES
argument_list|)
operator|.
name|addAll
argument_list|(
name|SqlTypeName
operator|.
name|NUMERIC_TYPES
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|INSTANCE
operator|=
operator|new
name|SqlTypeCoercionRule
argument_list|(
name|coerceRules
operator|.
name|map
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/** Returns an instance. */
specifier|public
specifier|static
name|SqlTypeCoercionRule
name|instance
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|THREAD_PROVIDERS
operator|.
name|get
argument_list|()
argument_list|,
literal|"threadProviders"
argument_list|)
return|;
block|}
comment|/** Returns an instance with specified type mappings. */
specifier|public
specifier|static
name|SqlTypeCoercionRule
name|instance
parameter_list|(
name|Map
argument_list|<
name|SqlTypeName
argument_list|,
name|ImmutableSet
argument_list|<
name|SqlTypeName
argument_list|>
argument_list|>
name|map
parameter_list|)
block|{
return|return
operator|new
name|SqlTypeCoercionRule
argument_list|(
name|map
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Map
argument_list|<
name|SqlTypeName
argument_list|,
name|ImmutableSet
argument_list|<
name|SqlTypeName
argument_list|>
argument_list|>
name|getTypeMapping
parameter_list|()
block|{
return|return
name|this
operator|.
name|map
return|;
block|}
block|}
end_class

end_unit

