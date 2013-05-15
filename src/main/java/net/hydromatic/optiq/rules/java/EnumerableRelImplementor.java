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
name|Enumerable
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
name|Queryable
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
name|jdbc
operator|.
name|JavaTypeFactoryImpl
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
name|Executable
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
name|RelImplementorImpl
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
name|RelImplementor
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
comment|/**  * Subclass of {@link RelImplementor} for relational operators  * of one of the {@link EnumerableConvention} calling  * conventions.  *  * @author jhyde  */
end_comment

begin_class
specifier|public
class|class
name|EnumerableRelImplementor
extends|extends
name|RelImplementorImpl
block|{
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Queryable
argument_list|>
name|map
init|=
operator|new
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Queryable
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|EnumerableRelImplementor
parameter_list|(
name|RexBuilder
name|rexBuilder
parameter_list|)
block|{
name|super
argument_list|(
name|rexBuilder
argument_list|)
expr_stmt|;
block|}
specifier|public
name|BlockExpression
name|visitChild
parameter_list|(
name|EnumerableRel
name|parent
parameter_list|,
name|int
name|ordinal
parameter_list|,
name|EnumerableRel
name|child
parameter_list|)
block|{
return|return
operator|(
name|BlockExpression
operator|)
name|super
operator|.
name|visitChild
argument_list|(
name|parent
argument_list|,
name|ordinal
argument_list|,
name|child
argument_list|)
return|;
block|}
specifier|public
name|BlockExpression
name|visitChildInternal
parameter_list|(
name|RelNode
name|child
parameter_list|,
name|int
name|ordinal
parameter_list|)
block|{
return|return
operator|(
operator|(
name|EnumerableRel
operator|)
name|child
operator|)
operator|.
name|implement
argument_list|(
name|this
argument_list|)
return|;
block|}
specifier|public
name|ClassDeclaration
name|implementRoot
parameter_list|(
name|EnumerableRel
name|rootRel
parameter_list|)
block|{
specifier|final
name|BlockExpression
name|implement
init|=
name|rootRel
operator|.
name|implement
argument_list|(
name|this
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|MemberDeclaration
argument_list|>
name|memberDeclarations
init|=
operator|new
name|ArrayList
argument_list|<
name|MemberDeclaration
argument_list|>
argument_list|()
decl_stmt|;
name|declareSyntheticClasses
argument_list|(
name|implement
argument_list|,
name|memberDeclarations
argument_list|)
expr_stmt|;
name|ParameterExpression
name|root
init|=
name|Expressions
operator|.
name|parameter
argument_list|(
name|Modifier
operator|.
name|FINAL
argument_list|,
name|DataContext
operator|.
name|class
argument_list|,
literal|"root"
argument_list|)
decl_stmt|;
name|memberDeclarations
operator|.
name|add
argument_list|(
name|Expressions
operator|.
name|methodDecl
argument_list|(
name|Modifier
operator|.
name|PUBLIC
argument_list|,
name|Enumerable
operator|.
name|class
argument_list|,
name|BuiltinMethod
operator|.
name|EXECUTABLE_EXECUTE
operator|.
name|method
operator|.
name|getName
argument_list|()
argument_list|,
name|Expressions
operator|.
name|list
argument_list|(
name|root
argument_list|)
argument_list|,
name|implement
argument_list|)
argument_list|)
expr_stmt|;
name|memberDeclarations
operator|.
name|add
argument_list|(
name|Expressions
operator|.
name|methodDecl
argument_list|(
name|Modifier
operator|.
name|PUBLIC
argument_list|,
name|Type
operator|.
name|class
argument_list|,
name|BuiltinMethod
operator|.
name|TYPED_GET_ELEMENT_TYPE
operator|.
name|method
operator|.
name|getName
argument_list|()
argument_list|,
name|Collections
operator|.
expr|<
name|ParameterExpression
operator|>
name|emptyList
argument_list|()
argument_list|,
name|Blocks
operator|.
name|toFunctionBlock
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
name|rootRel
operator|.
name|getPhysType
argument_list|()
operator|.
name|getJavaRowType
argument_list|()
argument_list|)
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|Expressions
operator|.
name|classDecl
argument_list|(
name|Modifier
operator|.
name|PUBLIC
argument_list|,
literal|"Baz"
argument_list|,
literal|null
argument_list|,
name|Collections
operator|.
expr|<
name|Type
operator|>
name|singletonList
argument_list|(
name|Executable
operator|.
name|class
argument_list|)
argument_list|,
name|memberDeclarations
argument_list|)
return|;
block|}
specifier|private
name|void
name|declareSyntheticClasses
parameter_list|(
name|BlockExpression
name|implement
parameter_list|,
name|List
argument_list|<
name|MemberDeclaration
argument_list|>
name|memberDeclarations
parameter_list|)
block|{
specifier|final
name|LinkedHashSet
argument_list|<
name|Type
argument_list|>
name|types
init|=
operator|new
name|LinkedHashSet
argument_list|<
name|Type
argument_list|>
argument_list|()
decl_stmt|;
name|implement
operator|.
name|accept
argument_list|(
operator|new
name|TypeFinder
argument_list|(
name|types
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Type
name|type
range|:
name|types
control|)
block|{
if|if
condition|(
name|type
operator|instanceof
name|JavaTypeFactoryImpl
operator|.
name|SyntheticRecordType
condition|)
block|{
name|memberDeclarations
operator|.
name|add
argument_list|(
name|classDecl
argument_list|(
operator|(
name|JavaTypeFactoryImpl
operator|.
name|SyntheticRecordType
operator|)
name|type
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|ClassDeclaration
name|classDecl
parameter_list|(
name|JavaTypeFactoryImpl
operator|.
name|SyntheticRecordType
name|type
parameter_list|)
block|{
name|ClassDeclaration
name|classDeclaration
init|=
name|Expressions
operator|.
name|classDecl
argument_list|(
name|Modifier
operator|.
name|PUBLIC
operator||
name|Modifier
operator|.
name|STATIC
argument_list|,
name|type
operator|.
name|getName
argument_list|()
argument_list|,
literal|null
argument_list|,
name|Collections
operator|.
expr|<
name|Type
operator|>
name|emptyList
argument_list|()
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|MemberDeclaration
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
comment|// For each field:
comment|//   public T0 f0;
comment|//   ...
for|for
control|(
name|Types
operator|.
name|RecordField
name|field
range|:
name|type
operator|.
name|getRecordFields
argument_list|()
control|)
block|{
name|classDeclaration
operator|.
name|memberDeclarations
operator|.
name|add
argument_list|(
name|Expressions
operator|.
name|fieldDecl
argument_list|(
name|Modifier
operator|.
name|PUBLIC
argument_list|,
name|Expressions
operator|.
name|parameter
argument_list|(
name|field
operator|.
name|getType
argument_list|()
argument_list|,
name|field
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Constructor:
comment|//   Foo(T0 f0, ...) { this.f0 = f0; ... }
specifier|final
name|BlockBuilder
name|blockBuilder
init|=
operator|new
name|BlockBuilder
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|ParameterExpression
argument_list|>
name|parameters
init|=
operator|new
name|ArrayList
argument_list|<
name|ParameterExpression
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|ParameterExpression
name|thisParameter
init|=
name|Expressions
operator|.
name|parameter
argument_list|(
name|type
argument_list|,
literal|"this"
argument_list|)
decl_stmt|;
for|for
control|(
name|Types
operator|.
name|RecordField
name|field
range|:
name|type
operator|.
name|getRecordFields
argument_list|()
control|)
block|{
specifier|final
name|ParameterExpression
name|parameter
init|=
name|Expressions
operator|.
name|parameter
argument_list|(
name|field
operator|.
name|getType
argument_list|()
argument_list|,
name|field
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|parameters
operator|.
name|add
argument_list|(
name|parameter
argument_list|)
expr_stmt|;
name|blockBuilder
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
name|Expressions
operator|.
name|field
argument_list|(
name|thisParameter
argument_list|,
name|field
argument_list|)
argument_list|,
name|parameter
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|classDeclaration
operator|.
name|memberDeclarations
operator|.
name|add
argument_list|(
name|Expressions
operator|.
name|constructorDecl
argument_list|(
name|Modifier
operator|.
name|PUBLIC
argument_list|,
name|type
argument_list|,
name|parameters
argument_list|,
name|blockBuilder
operator|.
name|toBlock
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// equals method():
comment|//   public boolean equals(Object o) {
comment|//       if (this == o) return true;
comment|//       if (!(o instanceof MyClass)) return false;
comment|//       final MyClass that = (MyClass) o;
comment|//       return this.f0 == that.f0
comment|//&& equal(this.f1, that.f1)
comment|//         ...
comment|//   }
specifier|final
name|BlockBuilder
name|blockBuilder2
init|=
operator|new
name|BlockBuilder
argument_list|()
decl_stmt|;
specifier|final
name|ParameterExpression
name|thatParameter
init|=
name|Expressions
operator|.
name|parameter
argument_list|(
name|type
argument_list|,
literal|"that"
argument_list|)
decl_stmt|;
specifier|final
name|ParameterExpression
name|oParameter
init|=
name|Expressions
operator|.
name|parameter
argument_list|(
name|Object
operator|.
name|class
argument_list|,
literal|"o"
argument_list|)
decl_stmt|;
name|blockBuilder2
operator|.
name|add
argument_list|(
name|Expressions
operator|.
name|ifThen
argument_list|(
name|Expressions
operator|.
name|equal
argument_list|(
name|thisParameter
argument_list|,
name|oParameter
argument_list|)
argument_list|,
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
literal|true
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|blockBuilder2
operator|.
name|add
argument_list|(
name|Expressions
operator|.
name|ifThen
argument_list|(
name|Expressions
operator|.
name|not
argument_list|(
name|Expressions
operator|.
name|typeIs
argument_list|(
name|oParameter
argument_list|,
name|type
argument_list|)
argument_list|)
argument_list|,
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
literal|false
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|blockBuilder2
operator|.
name|add
argument_list|(
name|Expressions
operator|.
name|declare
argument_list|(
name|Modifier
operator|.
name|FINAL
argument_list|,
name|thatParameter
argument_list|,
name|Expressions
operator|.
name|convert_
argument_list|(
name|oParameter
argument_list|,
name|type
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Expression
argument_list|>
name|conditions
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
name|Types
operator|.
name|RecordField
name|field
range|:
name|type
operator|.
name|getRecordFields
argument_list|()
control|)
block|{
name|conditions
operator|.
name|add
argument_list|(
name|Primitive
operator|.
name|is
argument_list|(
name|field
operator|.
name|getType
argument_list|()
argument_list|)
condition|?
name|Expressions
operator|.
name|equal
argument_list|(
name|Expressions
operator|.
name|field
argument_list|(
name|thisParameter
argument_list|,
name|field
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
name|Expressions
operator|.
name|field
argument_list|(
name|thatParameter
argument_list|,
name|field
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
else|:
name|Expressions
operator|.
name|call
argument_list|(
name|Utilities
operator|.
name|class
argument_list|,
literal|"equal"
argument_list|,
name|Expressions
operator|.
name|field
argument_list|(
name|thisParameter
argument_list|,
name|field
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
name|Expressions
operator|.
name|field
argument_list|(
name|thatParameter
argument_list|,
name|field
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|blockBuilder2
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
name|foldAnd
argument_list|(
name|conditions
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|classDeclaration
operator|.
name|memberDeclarations
operator|.
name|add
argument_list|(
name|Expressions
operator|.
name|methodDecl
argument_list|(
name|Modifier
operator|.
name|PUBLIC
argument_list|,
name|boolean
operator|.
name|class
argument_list|,
literal|"equals"
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|oParameter
argument_list|)
argument_list|,
name|blockBuilder2
operator|.
name|toBlock
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// hashCode method:
comment|//   public int hashCode() {
comment|//     int h = 0;
comment|//     h = hash(h, f0);
comment|//     ...
comment|//     return h;
comment|//   }
specifier|final
name|BlockBuilder
name|blockBuilder3
init|=
operator|new
name|BlockBuilder
argument_list|()
decl_stmt|;
specifier|final
name|ParameterExpression
name|hParameter
init|=
name|Expressions
operator|.
name|parameter
argument_list|(
name|int
operator|.
name|class
argument_list|,
literal|"h"
argument_list|)
decl_stmt|;
specifier|final
name|ConstantExpression
name|constantZero
init|=
name|Expressions
operator|.
name|constant
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|blockBuilder3
operator|.
name|add
argument_list|(
name|Expressions
operator|.
name|declare
argument_list|(
literal|0
argument_list|,
name|hParameter
argument_list|,
name|constantZero
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Types
operator|.
name|RecordField
name|field
range|:
name|type
operator|.
name|getRecordFields
argument_list|()
control|)
block|{
name|blockBuilder3
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
name|hParameter
argument_list|,
name|Expressions
operator|.
name|call
argument_list|(
name|Utilities
operator|.
name|class
argument_list|,
literal|"hash"
argument_list|,
name|Arrays
operator|.
expr|<
name|Expression
operator|>
name|asList
argument_list|(
name|hParameter
argument_list|,
name|Expressions
operator|.
name|field
argument_list|(
name|thisParameter
argument_list|,
name|field
argument_list|)
argument_list|)
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|blockBuilder3
operator|.
name|add
argument_list|(
name|Expressions
operator|.
name|return_
argument_list|(
literal|null
argument_list|,
name|hParameter
argument_list|)
argument_list|)
expr_stmt|;
name|classDeclaration
operator|.
name|memberDeclarations
operator|.
name|add
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
literal|"hashCode"
argument_list|,
name|Collections
operator|.
expr|<
name|ParameterExpression
operator|>
name|emptyList
argument_list|()
argument_list|,
name|blockBuilder3
operator|.
name|toBlock
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// compareTo method:
comment|//   public int compareTo(MyClass that) {
comment|//     int c;
comment|//     c = compare(this.f0, that.f0);
comment|//     if (c != 0) return c;
comment|//     ...
comment|//     return 0;
comment|//   }
specifier|final
name|BlockBuilder
name|blockBuilder4
init|=
operator|new
name|BlockBuilder
argument_list|()
decl_stmt|;
specifier|final
name|ParameterExpression
name|cParameter
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
name|blockBuilder4
operator|.
name|add
argument_list|(
name|Expressions
operator|.
name|declare
argument_list|(
literal|0
argument_list|,
name|cParameter
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|ConditionalStatement
name|conditionalStatement
init|=
name|Expressions
operator|.
name|ifThen
argument_list|(
name|Expressions
operator|.
name|notEqual
argument_list|(
name|cParameter
argument_list|,
name|constantZero
argument_list|)
argument_list|,
name|Expressions
operator|.
name|return_
argument_list|(
literal|null
argument_list|,
name|cParameter
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|Types
operator|.
name|RecordField
name|field
range|:
name|type
operator|.
name|getRecordFields
argument_list|()
control|)
block|{
name|blockBuilder4
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
name|cParameter
argument_list|,
name|Expressions
operator|.
name|call
argument_list|(
name|Utilities
operator|.
name|class
argument_list|,
name|field
operator|.
name|nullable
argument_list|()
condition|?
literal|"compareNullsLast"
else|:
literal|"compare"
argument_list|,
name|Expressions
operator|.
name|field
argument_list|(
name|thisParameter
argument_list|,
name|field
argument_list|)
argument_list|,
name|Expressions
operator|.
name|field
argument_list|(
name|thatParameter
argument_list|,
name|field
argument_list|)
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|blockBuilder4
operator|.
name|add
argument_list|(
name|conditionalStatement
argument_list|)
expr_stmt|;
block|}
name|blockBuilder4
operator|.
name|add
argument_list|(
name|Expressions
operator|.
name|return_
argument_list|(
literal|null
argument_list|,
name|constantZero
argument_list|)
argument_list|)
expr_stmt|;
name|classDeclaration
operator|.
name|memberDeclarations
operator|.
name|add
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
literal|"compareTo"
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|thatParameter
argument_list|)
argument_list|,
name|blockBuilder4
operator|.
name|toBlock
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// toString method:
comment|//   public String toString() {
comment|//     return "{f0=" + f0
comment|//       + ", f1=" + f1
comment|//       ...
comment|//       + "}";
comment|//   }
specifier|final
name|BlockBuilder
name|blockBuilder5
init|=
operator|new
name|BlockBuilder
argument_list|()
decl_stmt|;
name|Expression
name|expression5
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Types
operator|.
name|RecordField
name|field
range|:
name|type
operator|.
name|getRecordFields
argument_list|()
control|)
block|{
if|if
condition|(
name|expression5
operator|==
literal|null
condition|)
block|{
name|expression5
operator|=
name|Expressions
operator|.
name|constant
argument_list|(
literal|"{"
operator|+
name|field
operator|.
name|getName
argument_list|()
operator|+
literal|"="
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|expression5
operator|=
name|Expressions
operator|.
name|add
argument_list|(
name|expression5
argument_list|,
name|Expressions
operator|.
name|constant
argument_list|(
literal|", "
operator|+
name|field
operator|.
name|getName
argument_list|()
operator|+
literal|"="
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|expression5
operator|=
name|Expressions
operator|.
name|add
argument_list|(
name|expression5
argument_list|,
name|Expressions
operator|.
name|field
argument_list|(
name|thisParameter
argument_list|,
name|field
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|expression5
operator|=
name|expression5
operator|==
literal|null
condition|?
name|Expressions
operator|.
name|constant
argument_list|(
literal|"{}"
argument_list|)
else|:
name|Expressions
operator|.
name|add
argument_list|(
name|expression5
argument_list|,
name|Expressions
operator|.
name|constant
argument_list|(
literal|"}"
argument_list|)
argument_list|)
expr_stmt|;
name|blockBuilder5
operator|.
name|add
argument_list|(
name|Expressions
operator|.
name|return_
argument_list|(
literal|null
argument_list|,
name|expression5
argument_list|)
argument_list|)
expr_stmt|;
name|classDeclaration
operator|.
name|memberDeclarations
operator|.
name|add
argument_list|(
name|Expressions
operator|.
name|methodDecl
argument_list|(
name|Modifier
operator|.
name|PUBLIC
argument_list|,
name|String
operator|.
name|class
argument_list|,
literal|"toString"
argument_list|,
name|Collections
operator|.
expr|<
name|ParameterExpression
operator|>
name|emptyList
argument_list|()
argument_list|,
name|blockBuilder5
operator|.
name|toBlock
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|classDeclaration
return|;
block|}
specifier|public
name|Expression
name|register
parameter_list|(
name|Queryable
name|queryable
parameter_list|)
block|{
name|String
name|name
init|=
literal|"v"
operator|+
name|map
operator|.
name|size
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|queryable
argument_list|)
expr_stmt|;
return|return
name|Expressions
operator|.
name|variable
argument_list|(
name|queryable
operator|.
name|getClass
argument_list|()
argument_list|,
name|name
argument_list|)
return|;
block|}
specifier|private
specifier|static
class|class
name|TypeFinder
extends|extends
name|Visitor
block|{
specifier|private
specifier|final
name|LinkedHashSet
argument_list|<
name|Type
argument_list|>
name|types
decl_stmt|;
name|TypeFinder
parameter_list|(
name|LinkedHashSet
argument_list|<
name|Type
argument_list|>
name|types
parameter_list|)
block|{
name|this
operator|.
name|types
operator|=
name|types
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Expression
name|visit
parameter_list|(
name|NewExpression
name|newExpression
parameter_list|,
name|List
argument_list|<
name|Expression
argument_list|>
name|arguments
parameter_list|,
name|List
argument_list|<
name|MemberDeclaration
argument_list|>
name|memberDeclarations
parameter_list|)
block|{
name|types
operator|.
name|add
argument_list|(
name|newExpression
operator|.
name|type
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|visit
argument_list|(
name|newExpression
argument_list|,
name|arguments
argument_list|,
name|memberDeclarations
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Expression
name|visit
parameter_list|(
name|NewArrayExpression
name|newArrayExpression
parameter_list|,
name|List
argument_list|<
name|Expression
argument_list|>
name|expressions
parameter_list|)
block|{
name|Type
name|type
init|=
name|newArrayExpression
operator|.
name|type
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
specifier|final
name|Type
name|componentType
init|=
name|Types
operator|.
name|getComponentType
argument_list|(
name|type
argument_list|)
decl_stmt|;
if|if
condition|(
name|componentType
operator|==
literal|null
condition|)
block|{
break|break;
block|}
name|type
operator|=
name|componentType
expr_stmt|;
block|}
name|types
operator|.
name|add
argument_list|(
name|type
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|visit
argument_list|(
name|newArrayExpression
argument_list|,
name|expressions
argument_list|)
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End EnumerableRelImplementor.java
end_comment

end_unit

