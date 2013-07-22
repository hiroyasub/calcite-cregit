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
name|rules
operator|.
name|java
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
name|Function1
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
name|BuiltinMethod
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
name|runtime
operator|.
name|Utilities
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
name|RelFieldCollation
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
name|RelDataTypeField
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
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Modifier
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
name|*
import|;
end_import

begin_comment
comment|/** Implementation of {@link PhysType}. */
end_comment

begin_class
specifier|public
class|class
name|PhysTypeImpl
implements|implements
name|PhysType
block|{
specifier|private
specifier|final
name|JavaTypeFactory
name|typeFactory
decl_stmt|;
specifier|private
specifier|final
name|RelDataType
name|rowType
decl_stmt|;
specifier|private
specifier|final
name|Type
name|javaRowClass
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|Class
argument_list|>
name|fieldClasses
init|=
operator|new
name|ArrayList
argument_list|<
name|Class
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|JavaRowFormat
name|format
decl_stmt|;
comment|/** Creates a PhysTypeImpl. */
name|PhysTypeImpl
parameter_list|(
name|JavaTypeFactory
name|typeFactory
parameter_list|,
name|RelDataType
name|rowType
parameter_list|,
name|Type
name|javaRowClass
parameter_list|,
name|JavaRowFormat
name|format
parameter_list|)
block|{
name|this
operator|.
name|typeFactory
operator|=
name|typeFactory
expr_stmt|;
name|this
operator|.
name|rowType
operator|=
name|rowType
expr_stmt|;
name|this
operator|.
name|javaRowClass
operator|=
name|javaRowClass
expr_stmt|;
name|this
operator|.
name|format
operator|=
name|format
expr_stmt|;
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
name|fieldClasses
operator|.
name|add
argument_list|(
name|JavaRules
operator|.
name|EnumUtil
operator|.
name|javaRowClass
argument_list|(
name|typeFactory
argument_list|,
name|field
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
specifier|static
name|PhysType
name|of
parameter_list|(
name|JavaTypeFactory
name|typeFactory
parameter_list|,
name|RelDataType
name|rowType
parameter_list|,
name|JavaRowFormat
name|format
parameter_list|)
block|{
specifier|final
name|JavaRowFormat
name|format2
init|=
name|format
operator|.
name|optimize
argument_list|(
name|rowType
argument_list|)
decl_stmt|;
specifier|final
name|Type
name|javaRowClass
init|=
name|format2
operator|.
name|javaRowClass
argument_list|(
name|typeFactory
argument_list|,
name|rowType
argument_list|)
decl_stmt|;
return|return
operator|new
name|PhysTypeImpl
argument_list|(
name|typeFactory
argument_list|,
name|rowType
argument_list|,
name|javaRowClass
argument_list|,
name|format2
argument_list|)
return|;
block|}
specifier|static
name|PhysType
name|of
parameter_list|(
specifier|final
name|JavaTypeFactory
name|typeFactory
parameter_list|,
name|Type
name|javaRowClass
parameter_list|)
block|{
specifier|final
name|Types
operator|.
name|RecordType
name|recordType
init|=
operator|(
name|Types
operator|.
name|RecordType
operator|)
name|javaRowClass
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Types
operator|.
name|RecordField
argument_list|>
name|recordFields
init|=
name|recordType
operator|.
name|getRecordFields
argument_list|()
decl_stmt|;
name|RelDataType
name|rowType
init|=
name|typeFactory
operator|.
name|createStructType
argument_list|(
operator|new
name|AbstractList
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|RelDataType
argument_list|>
argument_list|>
argument_list|()
block|{
specifier|public
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|RelDataType
argument_list|>
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
specifier|final
name|Types
operator|.
name|RecordField
name|field
init|=
name|recordFields
operator|.
name|get
argument_list|(
name|index
argument_list|)
decl_stmt|;
return|return
name|Pair
operator|.
name|of
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|,
name|typeFactory
operator|.
name|createType
argument_list|(
name|field
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|recordFields
operator|.
name|size
argument_list|()
return|;
block|}
block|}
argument_list|)
decl_stmt|;
comment|// Do not optimize if there are 0 or 1 fields.
return|return
operator|new
name|PhysTypeImpl
argument_list|(
name|typeFactory
argument_list|,
name|rowType
argument_list|,
name|javaRowClass
argument_list|,
name|JavaRowFormat
operator|.
name|CUSTOM
argument_list|)
return|;
block|}
specifier|public
name|JavaRowFormat
name|getFormat
parameter_list|()
block|{
return|return
name|format
return|;
block|}
specifier|public
name|PhysType
name|project
parameter_list|(
specifier|final
name|List
argument_list|<
name|Integer
argument_list|>
name|integers
parameter_list|,
name|JavaRowFormat
name|format
parameter_list|)
block|{
name|RelDataType
name|projectedRowType
init|=
name|typeFactory
operator|.
name|createStructType
argument_list|(
operator|new
name|AbstractList
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|RelDataType
argument_list|>
argument_list|>
argument_list|()
block|{
specifier|public
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|RelDataType
argument_list|>
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|rowType
operator|.
name|getFieldList
argument_list|()
operator|.
name|get
argument_list|(
name|index
argument_list|)
return|;
block|}
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|integers
operator|.
name|size
argument_list|()
return|;
block|}
block|}
argument_list|)
decl_stmt|;
return|return
name|of
argument_list|(
name|typeFactory
argument_list|,
name|projectedRowType
argument_list|,
name|format
operator|.
name|optimize
argument_list|(
name|projectedRowType
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|Expression
name|generateSelector
parameter_list|(
name|ParameterExpression
name|parameter
parameter_list|,
name|List
argument_list|<
name|Integer
argument_list|>
name|fields
parameter_list|)
block|{
return|return
name|generateSelector
argument_list|(
name|parameter
argument_list|,
name|fields
argument_list|,
name|format
argument_list|)
return|;
block|}
specifier|public
name|Expression
name|generateSelector
parameter_list|(
name|ParameterExpression
name|parameter
parameter_list|,
name|List
argument_list|<
name|Integer
argument_list|>
name|fields
parameter_list|,
name|JavaRowFormat
name|targetFormat
parameter_list|)
block|{
comment|// Optimize target format
switch|switch
condition|(
name|fields
operator|.
name|size
argument_list|()
condition|)
block|{
case|case
literal|0
case|:
name|targetFormat
operator|=
name|JavaRowFormat
operator|.
name|LIST
expr_stmt|;
break|break;
case|case
literal|1
case|:
name|targetFormat
operator|=
name|JavaRowFormat
operator|.
name|SCALAR
expr_stmt|;
break|break;
block|}
specifier|final
name|PhysType
name|targetPhysType
init|=
name|project
argument_list|(
name|fields
argument_list|,
name|targetFormat
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|format
condition|)
block|{
case|case
name|SCALAR
case|:
return|return
name|Expressions
operator|.
name|call
argument_list|(
name|BuiltinMethod
operator|.
name|IDENTITY_SELECTOR
operator|.
name|method
argument_list|)
return|;
default|default:
return|return
name|Expressions
operator|.
name|lambda
argument_list|(
name|Function1
operator|.
name|class
argument_list|,
name|targetPhysType
operator|.
name|record
argument_list|(
name|fieldReferences
argument_list|(
name|parameter
argument_list|,
name|fields
argument_list|)
argument_list|)
argument_list|,
name|parameter
argument_list|)
return|;
block|}
block|}
specifier|public
name|Expression
name|selector
parameter_list|(
name|ParameterExpression
name|parameter
parameter_list|,
name|List
argument_list|<
name|Integer
argument_list|>
name|fields
parameter_list|,
name|JavaRowFormat
name|targetFormat
parameter_list|)
block|{
comment|// Optimize target format
switch|switch
condition|(
name|fields
operator|.
name|size
argument_list|()
condition|)
block|{
case|case
literal|0
case|:
name|targetFormat
operator|=
name|JavaRowFormat
operator|.
name|LIST
expr_stmt|;
break|break;
case|case
literal|1
case|:
name|targetFormat
operator|=
name|JavaRowFormat
operator|.
name|SCALAR
expr_stmt|;
break|break;
block|}
specifier|final
name|PhysType
name|targetPhysType
init|=
name|project
argument_list|(
name|fields
argument_list|,
name|targetFormat
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|format
condition|)
block|{
case|case
name|SCALAR
case|:
return|return
name|parameter
return|;
default|default:
return|return
name|targetPhysType
operator|.
name|record
argument_list|(
name|fieldReferences
argument_list|(
name|parameter
argument_list|,
name|fields
argument_list|)
argument_list|)
return|;
block|}
block|}
specifier|public
name|List
argument_list|<
name|Expression
argument_list|>
name|accessors
parameter_list|(
name|Expression
name|v1
parameter_list|,
name|List
argument_list|<
name|Integer
argument_list|>
name|argList
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|Expression
argument_list|>
name|expressions
init|=
operator|new
name|ArrayList
argument_list|<
name|Expression
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|field
range|:
name|argList
control|)
block|{
name|expressions
operator|.
name|add
argument_list|(
name|Types
operator|.
name|castIfNecessary
argument_list|(
name|fieldClass
argument_list|(
name|field
argument_list|)
argument_list|,
name|fieldReference
argument_list|(
name|v1
argument_list|,
name|field
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|expressions
return|;
block|}
specifier|public
name|Pair
argument_list|<
name|Expression
argument_list|,
name|Expression
argument_list|>
name|generateCollationKey
parameter_list|(
specifier|final
name|List
argument_list|<
name|RelFieldCollation
argument_list|>
name|collations
parameter_list|)
block|{
specifier|final
name|Expression
name|selector
decl_stmt|;
if|if
condition|(
name|collations
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
name|RelFieldCollation
name|collation
init|=
name|collations
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|ParameterExpression
name|parameter
init|=
name|Expressions
operator|.
name|parameter
argument_list|(
name|javaRowClass
argument_list|,
literal|"v"
argument_list|)
decl_stmt|;
name|selector
operator|=
name|Expressions
operator|.
name|lambda
argument_list|(
name|Function1
operator|.
name|class
argument_list|,
name|fieldReference
argument_list|(
name|parameter
argument_list|,
name|collation
operator|.
name|getFieldIndex
argument_list|()
argument_list|)
argument_list|,
name|parameter
argument_list|)
expr_stmt|;
return|return
name|Pair
operator|.
expr|<
name|Expression
operator|,
name|Expression
operator|>
name|of
argument_list|(
name|selector
argument_list|,
name|Expressions
operator|.
name|call
argument_list|(
name|BuiltinMethod
operator|.
name|NULLS_COMPARATOR
operator|.
name|method
argument_list|,
name|Expressions
operator|.
name|constant
argument_list|(
name|collation
operator|.
name|nullDirection
operator|==
name|RelFieldCollation
operator|.
name|NullDirection
operator|.
name|FIRST
argument_list|)
argument_list|,
name|Expressions
operator|.
name|constant
argument_list|(
name|collation
operator|.
name|getDirection
argument_list|()
operator|==
name|RelFieldCollation
operator|.
name|Direction
operator|.
name|Descending
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
name|selector
operator|=
name|Expressions
operator|.
name|call
argument_list|(
name|BuiltinMethod
operator|.
name|IDENTITY_SELECTOR
operator|.
name|method
argument_list|)
expr_stmt|;
comment|// int c;
comment|// c = Utilities.compare(v0, v1);
comment|// if (c != 0) return c; // or -c if descending
comment|// ...
comment|// return 0;
name|BlockBuilder
name|body
init|=
operator|new
name|BlockBuilder
argument_list|()
decl_stmt|;
specifier|final
name|ParameterExpression
name|parameterV0
init|=
name|Expressions
operator|.
name|parameter
argument_list|(
name|javaRowClass
argument_list|,
literal|"v0"
argument_list|)
decl_stmt|;
specifier|final
name|ParameterExpression
name|parameterV1
init|=
name|Expressions
operator|.
name|parameter
argument_list|(
name|javaRowClass
argument_list|,
literal|"v1"
argument_list|)
decl_stmt|;
specifier|final
name|ParameterExpression
name|parameterC
init|=
name|Expressions
operator|.
name|parameter
argument_list|(
name|int
operator|.
name|class
argument_list|,
literal|"c"
argument_list|)
decl_stmt|;
name|body
operator|.
name|add
argument_list|(
name|Expressions
operator|.
name|declare
argument_list|(
literal|0
argument_list|,
name|parameterC
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|RelFieldCollation
name|collation
range|:
name|collations
control|)
block|{
specifier|final
name|int
name|index
init|=
name|collation
operator|.
name|getFieldIndex
argument_list|()
decl_stmt|;
name|Expression
name|arg0
init|=
name|fieldReference
argument_list|(
name|parameterV0
argument_list|,
name|index
argument_list|)
decl_stmt|;
name|Expression
name|arg1
init|=
name|fieldReference
argument_list|(
name|parameterV1
argument_list|,
name|index
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|Primitive
operator|.
name|flavor
argument_list|(
name|fieldClass
argument_list|(
name|index
argument_list|)
argument_list|)
condition|)
block|{
case|case
name|OBJECT
case|:
name|arg0
operator|=
name|Types
operator|.
name|castIfNecessary
argument_list|(
name|Comparable
operator|.
name|class
argument_list|,
name|arg0
argument_list|)
expr_stmt|;
name|arg1
operator|=
name|Types
operator|.
name|castIfNecessary
argument_list|(
name|Comparable
operator|.
name|class
argument_list|,
name|arg1
argument_list|)
expr_stmt|;
block|}
specifier|final
name|boolean
name|nullsFirst
init|=
name|collation
operator|.
name|nullDirection
operator|==
name|RelFieldCollation
operator|.
name|NullDirection
operator|.
name|FIRST
decl_stmt|;
specifier|final
name|boolean
name|descending
init|=
name|collation
operator|.
name|getDirection
argument_list|()
operator|==
name|RelFieldCollation
operator|.
name|Direction
operator|.
name|Descending
decl_stmt|;
name|body
operator|.
name|add
argument_list|(
name|Expressions
operator|.
name|statement
argument_list|(
name|Expressions
operator|.
name|assign
argument_list|(
name|parameterC
argument_list|,
name|Expressions
operator|.
name|call
argument_list|(
name|Utilities
operator|.
name|class
argument_list|,
name|fieldNullable
argument_list|(
name|index
argument_list|)
condition|?
operator|(
name|nullsFirst
condition|?
literal|"compareNullsFirst"
else|:
literal|"compareNullsLast"
operator|)
else|:
literal|"compare"
argument_list|,
name|arg0
argument_list|,
name|arg1
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|body
operator|.
name|add
argument_list|(
name|Expressions
operator|.
name|ifThen
argument_list|(
name|Expressions
operator|.
name|notEqual
argument_list|(
name|parameterC
argument_list|,
name|Expressions
operator|.
name|constant
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|,
name|Expressions
operator|.
name|return_
argument_list|(
literal|null
argument_list|,
name|descending
condition|?
name|Expressions
operator|.
name|negate
argument_list|(
name|parameterC
argument_list|)
else|:
name|parameterC
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|body
operator|.
name|add
argument_list|(
name|Expressions
operator|.
name|return_
argument_list|(
literal|null
argument_list|,
name|Expressions
operator|.
name|constant
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|MemberDeclaration
argument_list|>
name|memberDeclarations
init|=
name|Expressions
operator|.
expr|<
name|MemberDeclaration
operator|>
name|list
argument_list|(
name|Expressions
operator|.
name|methodDecl
argument_list|(
name|Modifier
operator|.
name|PUBLIC
argument_list|,
name|int
operator|.
name|class
argument_list|,
literal|"compare"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|parameterV0
argument_list|,
name|parameterV1
argument_list|)
argument_list|,
name|body
operator|.
name|toBlock
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|JavaRules
operator|.
name|BRIDGE_METHODS
condition|)
block|{
specifier|final
name|ParameterExpression
name|parameterO0
init|=
name|Expressions
operator|.
name|parameter
argument_list|(
name|Object
operator|.
name|class
argument_list|,
literal|"o0"
argument_list|)
decl_stmt|;
specifier|final
name|ParameterExpression
name|parameterO1
init|=
name|Expressions
operator|.
name|parameter
argument_list|(
name|Object
operator|.
name|class
argument_list|,
literal|"o1"
argument_list|)
decl_stmt|;
name|BlockBuilder
name|bridgeBody
init|=
operator|new
name|BlockBuilder
argument_list|()
decl_stmt|;
name|bridgeBody
operator|.
name|add
argument_list|(
name|Expressions
operator|.
name|return_
argument_list|(
literal|null
argument_list|,
name|Expressions
operator|.
name|call
argument_list|(
name|Expressions
operator|.
name|parameter
argument_list|(
name|Comparable
operator|.
name|class
argument_list|,
literal|"this"
argument_list|)
argument_list|,
name|BuiltinMethod
operator|.
name|COMPARATOR_COMPARE
operator|.
name|method
argument_list|,
name|Expressions
operator|.
name|convert_
argument_list|(
name|parameterO0
argument_list|,
name|javaRowClass
argument_list|)
argument_list|,
name|Expressions
operator|.
name|convert_
argument_list|(
name|parameterO1
argument_list|,
name|javaRowClass
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|memberDeclarations
operator|.
name|add
argument_list|(
name|JavaRules
operator|.
name|EnumUtil
operator|.
name|overridingMethodDecl
argument_list|(
name|BuiltinMethod
operator|.
name|COMPARATOR_COMPARE
operator|.
name|method
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|parameterO0
argument_list|,
name|parameterO1
argument_list|)
argument_list|,
name|bridgeBody
operator|.
name|toBlock
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|Pair
operator|.
expr|<
name|Expression
operator|,
name|Expression
operator|>
name|of
argument_list|(
name|selector
argument_list|,
name|Expressions
operator|.
name|new_
argument_list|(
name|Comparator
operator|.
name|class
argument_list|,
name|Collections
operator|.
expr|<
name|Expression
operator|>
name|emptyList
argument_list|()
argument_list|,
name|memberDeclarations
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|Expression
name|generateComparator
parameter_list|(
name|RelCollation
name|collation
parameter_list|)
block|{
comment|// int c;
comment|// c = Utilities.compare(v0, v1);
comment|// if (c != 0) return c; // or -c if descending
comment|// ...
comment|// return 0;
name|BlockBuilder
name|body
init|=
operator|new
name|BlockBuilder
argument_list|()
decl_stmt|;
specifier|final
name|ParameterExpression
name|parameterV0
init|=
name|Expressions
operator|.
name|parameter
argument_list|(
name|javaRowClass
argument_list|,
literal|"v0"
argument_list|)
decl_stmt|;
specifier|final
name|ParameterExpression
name|parameterV1
init|=
name|Expressions
operator|.
name|parameter
argument_list|(
name|javaRowClass
argument_list|,
literal|"v1"
argument_list|)
decl_stmt|;
specifier|final
name|ParameterExpression
name|parameterC
init|=
name|Expressions
operator|.
name|parameter
argument_list|(
name|int
operator|.
name|class
argument_list|,
literal|"c"
argument_list|)
decl_stmt|;
name|body
operator|.
name|add
argument_list|(
name|Expressions
operator|.
name|declare
argument_list|(
literal|0
argument_list|,
name|parameterC
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|RelFieldCollation
name|fieldCollation
range|:
name|collation
operator|.
name|getFieldCollations
argument_list|()
control|)
block|{
specifier|final
name|int
name|index
init|=
name|fieldCollation
operator|.
name|getFieldIndex
argument_list|()
decl_stmt|;
name|Expression
name|arg0
init|=
name|fieldReference
argument_list|(
name|parameterV0
argument_list|,
name|index
argument_list|)
decl_stmt|;
name|Expression
name|arg1
init|=
name|fieldReference
argument_list|(
name|parameterV1
argument_list|,
name|index
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|Primitive
operator|.
name|flavor
argument_list|(
name|fieldClass
argument_list|(
name|index
argument_list|)
argument_list|)
condition|)
block|{
case|case
name|OBJECT
case|:
name|arg0
operator|=
name|Types
operator|.
name|castIfNecessary
argument_list|(
name|Comparable
operator|.
name|class
argument_list|,
name|arg0
argument_list|)
expr_stmt|;
name|arg1
operator|=
name|Types
operator|.
name|castIfNecessary
argument_list|(
name|Comparable
operator|.
name|class
argument_list|,
name|arg1
argument_list|)
expr_stmt|;
block|}
specifier|final
name|boolean
name|nullsFirst
init|=
name|fieldCollation
operator|.
name|nullDirection
operator|==
name|RelFieldCollation
operator|.
name|NullDirection
operator|.
name|FIRST
decl_stmt|;
specifier|final
name|boolean
name|descending
init|=
name|fieldCollation
operator|.
name|getDirection
argument_list|()
operator|==
name|RelFieldCollation
operator|.
name|Direction
operator|.
name|Descending
decl_stmt|;
name|body
operator|.
name|add
argument_list|(
name|Expressions
operator|.
name|statement
argument_list|(
name|Expressions
operator|.
name|assign
argument_list|(
name|parameterC
argument_list|,
name|Expressions
operator|.
name|call
argument_list|(
name|Utilities
operator|.
name|class
argument_list|,
name|fieldNullable
argument_list|(
name|index
argument_list|)
condition|?
operator|(
name|nullsFirst
operator|!=
name|descending
condition|?
literal|"compareNullsFirst"
else|:
literal|"compareNullsLast"
operator|)
else|:
literal|"compare"
argument_list|,
name|arg0
argument_list|,
name|arg1
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|body
operator|.
name|add
argument_list|(
name|Expressions
operator|.
name|ifThen
argument_list|(
name|Expressions
operator|.
name|notEqual
argument_list|(
name|parameterC
argument_list|,
name|Expressions
operator|.
name|constant
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|,
name|Expressions
operator|.
name|return_
argument_list|(
literal|null
argument_list|,
name|descending
condition|?
name|Expressions
operator|.
name|negate
argument_list|(
name|parameterC
argument_list|)
else|:
name|parameterC
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|body
operator|.
name|add
argument_list|(
name|Expressions
operator|.
name|return_
argument_list|(
literal|null
argument_list|,
name|Expressions
operator|.
name|constant
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|MemberDeclaration
argument_list|>
name|memberDeclarations
init|=
name|Expressions
operator|.
expr|<
name|MemberDeclaration
operator|>
name|list
argument_list|(
name|Expressions
operator|.
name|methodDecl
argument_list|(
name|Modifier
operator|.
name|PUBLIC
argument_list|,
name|int
operator|.
name|class
argument_list|,
literal|"compare"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|parameterV0
argument_list|,
name|parameterV1
argument_list|)
argument_list|,
name|body
operator|.
name|toBlock
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|JavaRules
operator|.
name|BRIDGE_METHODS
condition|)
block|{
specifier|final
name|ParameterExpression
name|parameterO0
init|=
name|Expressions
operator|.
name|parameter
argument_list|(
name|Object
operator|.
name|class
argument_list|,
literal|"o0"
argument_list|)
decl_stmt|;
specifier|final
name|ParameterExpression
name|parameterO1
init|=
name|Expressions
operator|.
name|parameter
argument_list|(
name|Object
operator|.
name|class
argument_list|,
literal|"o1"
argument_list|)
decl_stmt|;
name|BlockBuilder
name|bridgeBody
init|=
operator|new
name|BlockBuilder
argument_list|()
decl_stmt|;
name|bridgeBody
operator|.
name|add
argument_list|(
name|Expressions
operator|.
name|return_
argument_list|(
literal|null
argument_list|,
name|Expressions
operator|.
name|call
argument_list|(
name|Expressions
operator|.
name|parameter
argument_list|(
name|Comparable
operator|.
name|class
argument_list|,
literal|"this"
argument_list|)
argument_list|,
name|BuiltinMethod
operator|.
name|COMPARATOR_COMPARE
operator|.
name|method
argument_list|,
name|Expressions
operator|.
name|convert_
argument_list|(
name|parameterO0
argument_list|,
name|javaRowClass
argument_list|)
argument_list|,
name|Expressions
operator|.
name|convert_
argument_list|(
name|parameterO1
argument_list|,
name|javaRowClass
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|memberDeclarations
operator|.
name|add
argument_list|(
name|JavaRules
operator|.
name|EnumUtil
operator|.
name|overridingMethodDecl
argument_list|(
name|BuiltinMethod
operator|.
name|COMPARATOR_COMPARE
operator|.
name|method
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|parameterO0
argument_list|,
name|parameterO1
argument_list|)
argument_list|,
name|bridgeBody
operator|.
name|toBlock
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|Expressions
operator|.
name|new_
argument_list|(
name|Comparator
operator|.
name|class
argument_list|,
name|Collections
operator|.
expr|<
name|Expression
operator|>
name|emptyList
argument_list|()
argument_list|,
name|memberDeclarations
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
specifier|public
name|Expression
name|record
parameter_list|(
name|List
argument_list|<
name|Expression
argument_list|>
name|expressions
parameter_list|)
block|{
return|return
name|format
operator|.
name|record
argument_list|(
name|javaRowClass
argument_list|,
name|expressions
argument_list|)
return|;
block|}
specifier|public
name|Type
name|getJavaRowType
parameter_list|()
block|{
return|return
name|javaRowClass
return|;
block|}
specifier|public
name|Expression
name|comparer
parameter_list|()
block|{
return|return
name|format
operator|.
name|comparer
argument_list|()
return|;
block|}
specifier|private
name|List
argument_list|<
name|Expression
argument_list|>
name|fieldReferences
parameter_list|(
specifier|final
name|Expression
name|parameter
parameter_list|,
specifier|final
name|List
argument_list|<
name|Integer
argument_list|>
name|fields
parameter_list|)
block|{
return|return
operator|new
name|AbstractList
argument_list|<
name|Expression
argument_list|>
argument_list|()
block|{
specifier|public
name|Expression
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|fieldReference
argument_list|(
name|parameter
argument_list|,
name|fields
operator|.
name|get
argument_list|(
name|index
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|fields
operator|.
name|size
argument_list|()
return|;
block|}
block|}
return|;
block|}
specifier|public
name|Class
name|fieldClass
parameter_list|(
name|int
name|field
parameter_list|)
block|{
return|return
name|fieldClasses
operator|.
name|get
argument_list|(
name|field
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|fieldNullable
parameter_list|(
name|int
name|field
parameter_list|)
block|{
return|return
name|rowType
operator|.
name|getFieldList
argument_list|()
operator|.
name|get
argument_list|(
name|field
argument_list|)
operator|.
name|getType
argument_list|()
operator|.
name|isNullable
argument_list|()
return|;
block|}
specifier|public
name|Expression
name|generateAccessor
parameter_list|(
name|List
argument_list|<
name|Integer
argument_list|>
name|fields
parameter_list|)
block|{
name|ParameterExpression
name|v1
init|=
name|Expressions
operator|.
name|parameter
argument_list|(
name|javaRowClass
argument_list|,
literal|"v1"
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|fields
operator|.
name|size
argument_list|()
condition|)
block|{
case|case
literal|0
case|:
return|return
name|Expressions
operator|.
name|lambda
argument_list|(
name|Function1
operator|.
name|class
argument_list|,
name|Expressions
operator|.
name|field
argument_list|(
literal|null
argument_list|,
name|Collections
operator|.
name|class
argument_list|,
literal|"EMPTY_LIST"
argument_list|)
argument_list|,
name|v1
argument_list|)
return|;
case|case
literal|1
case|:
name|int
name|field0
init|=
name|fields
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|// new Function1<Employee, Res> {
comment|//    public Res apply(Employee v1) {
comment|//        return v1.<fieldN>;
comment|//    }
comment|// }
name|Class
name|returnType
init|=
name|fieldClasses
operator|.
name|get
argument_list|(
name|field0
argument_list|)
decl_stmt|;
name|Expression
name|fieldReference
init|=
name|Types
operator|.
name|castIfNecessary
argument_list|(
name|returnType
argument_list|,
name|fieldReference
argument_list|(
name|v1
argument_list|,
name|field0
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|Expressions
operator|.
name|lambda
argument_list|(
name|Function1
operator|.
name|class
argument_list|,
name|fieldReference
argument_list|,
name|v1
argument_list|)
return|;
default|default:
comment|// new Function1<Employee, List> {
comment|//    public List apply(Employee v1) {
comment|//        return Arrays.asList(
comment|//            new Object[] {v1.<fieldN>, v1.<fieldM>});
comment|//    }
comment|// }
name|Expressions
operator|.
name|FluentList
argument_list|<
name|Expression
argument_list|>
name|list
init|=
name|Expressions
operator|.
name|list
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|field
range|:
name|fields
control|)
block|{
name|list
operator|.
name|add
argument_list|(
name|fieldReference
argument_list|(
name|v1
argument_list|,
name|field
argument_list|)
argument_list|)
expr_stmt|;
block|}
switch|switch
condition|(
name|list
operator|.
name|size
argument_list|()
condition|)
block|{
case|case
literal|2
case|:
return|return
name|Expressions
operator|.
name|lambda
argument_list|(
name|Function1
operator|.
name|class
argument_list|,
name|Expressions
operator|.
name|call
argument_list|(
name|List
operator|.
name|class
argument_list|,
literal|null
argument_list|,
name|BuiltinMethod
operator|.
name|LIST2
operator|.
name|method
argument_list|,
name|list
argument_list|)
argument_list|,
name|v1
argument_list|)
return|;
case|case
literal|3
case|:
return|return
name|Expressions
operator|.
name|lambda
argument_list|(
name|Function1
operator|.
name|class
argument_list|,
name|Expressions
operator|.
name|call
argument_list|(
name|List
operator|.
name|class
argument_list|,
literal|null
argument_list|,
name|BuiltinMethod
operator|.
name|LIST3
operator|.
name|method
argument_list|,
name|list
argument_list|)
argument_list|,
name|v1
argument_list|)
return|;
default|default:
return|return
name|Expressions
operator|.
name|lambda
argument_list|(
name|Function1
operator|.
name|class
argument_list|,
name|Expressions
operator|.
name|call
argument_list|(
name|BuiltinMethod
operator|.
name|ARRAYS_AS_LIST
operator|.
name|method
argument_list|,
name|Expressions
operator|.
name|newArrayInit
argument_list|(
name|Object
operator|.
name|class
argument_list|,
name|list
argument_list|)
argument_list|)
argument_list|,
name|v1
argument_list|)
return|;
block|}
block|}
block|}
specifier|public
name|Expression
name|fieldReference
parameter_list|(
name|Expression
name|expression
parameter_list|,
name|int
name|field
parameter_list|)
block|{
return|return
name|format
operator|.
name|field
argument_list|(
name|expression
argument_list|,
name|field
argument_list|,
name|fieldClass
argument_list|(
name|field
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End PhysTypeImpl.java
end_comment

end_unit

