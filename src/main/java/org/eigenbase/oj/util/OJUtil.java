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
name|oj
operator|.
name|util
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
name|openjava
operator|.
name|mop
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
name|*
import|;
end_import

begin_import
import|import
name|openjava
operator|.
name|ptree
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
name|oj
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
name|runtime
operator|.
name|RestartableIterator
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
name|*
import|;
end_import

begin_comment
comment|/**  * Static utilities for manipulating OpenJava expressions.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|OJUtil
block|{
comment|//~ Static fields/initializers ---------------------------------------------
static|static
block|{
name|OJSystem
operator|.
name|initConstants
argument_list|()
expr_stmt|;
block|}
specifier|public
specifier|static
specifier|final
name|OJClass
name|clazzVoid
init|=
name|OJClass
operator|.
name|forClass
argument_list|(
name|void
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|OJClass
name|clazzObject
init|=
name|OJClass
operator|.
name|forClass
argument_list|(
name|java
operator|.
name|lang
operator|.
name|Object
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|OJClass
name|clazzObjectArray
init|=
name|OJClass
operator|.
name|arrayOf
argument_list|(
name|clazzObject
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|OJClass
name|clazzCollection
init|=
name|OJClass
operator|.
name|forClass
argument_list|(
name|java
operator|.
name|util
operator|.
name|Collection
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|OJClass
name|clazzMap
init|=
name|OJClass
operator|.
name|forClass
argument_list|(
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|OJClass
name|clazzMapEntry
init|=
name|OJClass
operator|.
name|forClass
argument_list|(
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|OJClass
name|clazzHashtable
init|=
name|OJClass
operator|.
name|forClass
argument_list|(
name|java
operator|.
name|util
operator|.
name|Hashtable
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|OJClass
name|clazzEnumeration
init|=
name|OJClass
operator|.
name|forClass
argument_list|(
name|java
operator|.
name|util
operator|.
name|Enumeration
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|OJClass
name|clazzIterator
init|=
name|OJClass
operator|.
name|forClass
argument_list|(
name|java
operator|.
name|util
operator|.
name|Iterator
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|OJClass
name|clazzIterable
init|=
name|OJClass
operator|.
name|forClass
argument_list|(
name|org
operator|.
name|eigenbase
operator|.
name|runtime
operator|.
name|Iterable
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|OJClass
name|clazzTupleIter
init|=
name|OJClass
operator|.
name|forClass
argument_list|(
name|org
operator|.
name|eigenbase
operator|.
name|runtime
operator|.
name|TupleIter
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|OJClass
name|clazzVector
init|=
name|OJClass
operator|.
name|forClass
argument_list|(
name|java
operator|.
name|util
operator|.
name|Vector
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|OJClass
name|clazzComparable
init|=
name|OJClass
operator|.
name|forClass
argument_list|(
name|java
operator|.
name|lang
operator|.
name|Comparable
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|OJClass
name|clazzComparator
init|=
name|OJClass
operator|.
name|forClass
argument_list|(
name|java
operator|.
name|util
operator|.
name|Comparator
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|OJClass
name|clazzResultSet
init|=
name|OJClass
operator|.
name|forClass
argument_list|(
name|java
operator|.
name|sql
operator|.
name|ResultSet
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|OJClass
name|clazzClass
init|=
name|OJClass
operator|.
name|forClass
argument_list|(
name|java
operator|.
name|lang
operator|.
name|Class
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|OJClass
name|clazzString
init|=
name|OJClass
operator|.
name|forClass
argument_list|(
name|java
operator|.
name|lang
operator|.
name|String
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|OJClass
name|clazzSet
init|=
name|OJClass
operator|.
name|forClass
argument_list|(
name|java
operator|.
name|util
operator|.
name|Set
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|OJClass
name|clazzSQLException
init|=
name|OJClass
operator|.
name|forClass
argument_list|(
name|java
operator|.
name|sql
operator|.
name|SQLException
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|OJClass
name|clazzEntry
init|=
name|OJClass
operator|.
name|forClass
argument_list|(
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|OJClass
index|[]
name|emptyArrayOfOJClass
init|=
operator|new
name|OJClass
index|[]
block|{}
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|ModifierList
name|modFinal
init|=
operator|new
name|ModifierList
argument_list|(
name|ModifierList
operator|.
name|FINAL
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|OJClass
name|clazzInteger
init|=
name|OJClass
operator|.
name|forClass
argument_list|(
name|java
operator|.
name|lang
operator|.
name|Integer
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|TypeName
name|tnInt
init|=
name|TypeName
operator|.
name|forOJClass
argument_list|(
name|OJSystem
operator|.
name|INT
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|OJClass
name|clazzList
init|=
name|OJClass
operator|.
name|forClass
argument_list|(
name|java
operator|.
name|util
operator|.
name|List
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|TypeName
name|tnList
init|=
name|TypeName
operator|.
name|forOJClass
argument_list|(
name|clazzList
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|OJClass
name|clazzArrays
init|=
name|OJClass
operator|.
name|forClass
argument_list|(
name|java
operator|.
name|util
operator|.
name|Arrays
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|TypeName
name|tnArrays
init|=
name|TypeName
operator|.
name|forOJClass
argument_list|(
name|clazzArrays
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|TypeName
name|tnObject
init|=
name|TypeName
operator|.
name|forOJClass
argument_list|(
name|OJSystem
operator|.
name|OBJECT
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|OJClass
name|clazzRestartableIterator
init|=
name|OJClass
operator|.
name|forClass
argument_list|(
name|RestartableIterator
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
name|TypeName
name|tnRestartableIterator
init|=
name|TypeName
operator|.
name|forOJClass
argument_list|(
name|clazzRestartableIterator
argument_list|)
decl_stmt|;
comment|/**      * Each thread's enclosing {@link OJClass}. Synthetic classes are declared      * as inner classes of this.      */
specifier|public
specifier|static
specifier|final
name|ThreadLocal
argument_list|<
name|OJClass
argument_list|>
name|threadDeclarers
init|=
operator|new
name|ThreadLocal
argument_list|<
name|OJClass
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|ThreadLocal
argument_list|<
name|OJTypeFactory
argument_list|>
name|threadTypeFactories
init|=
operator|new
name|ThreadLocal
argument_list|<
name|OJTypeFactory
argument_list|>
argument_list|()
decl_stmt|;
comment|//~ Methods ----------------------------------------------------------------
specifier|public
specifier|static
name|void
name|setThreadTypeFactory
parameter_list|(
name|OJTypeFactory
name|typeFactory
parameter_list|)
block|{
name|threadTypeFactories
operator|.
name|set
argument_list|(
name|typeFactory
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|OJTypeFactory
name|threadTypeFactory
parameter_list|()
block|{
return|return
name|threadTypeFactories
operator|.
name|get
argument_list|()
return|;
block|}
specifier|public
specifier|static
name|RelDataType
name|ojToType
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|,
name|OJClass
name|ojClass
parameter_list|)
block|{
if|if
condition|(
name|ojClass
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|(
operator|(
name|OJTypeFactory
operator|)
name|typeFactory
operator|)
operator|.
name|toType
argument_list|(
name|ojClass
argument_list|)
return|;
block|}
comment|/**      * Converts a {@link RelDataType} to a {@link TypeName}.      *      * @pre threadDeclarers.get() != null      */
specifier|public
specifier|static
name|TypeName
name|toTypeName
parameter_list|(
name|RelDataType
name|rowType
parameter_list|,
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
block|{
return|return
name|TypeName
operator|.
name|forOJClass
argument_list|(
name|typeToOJClass
argument_list|(
name|rowType
argument_list|,
name|typeFactory
argument_list|)
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|OJClass
name|typeToOJClass
parameter_list|(
name|OJClass
name|declarer
parameter_list|,
name|RelDataType
name|rowType
parameter_list|,
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
block|{
name|OJTypeFactory
name|ojTypeFactory
init|=
operator|(
name|OJTypeFactory
operator|)
name|typeFactory
decl_stmt|;
return|return
name|ojTypeFactory
operator|.
name|toOJClass
argument_list|(
name|declarer
argument_list|,
name|rowType
argument_list|)
return|;
block|}
comment|/**      * Converts a {@link RelDataType} to a {@link OJClass}.      *      * @pre threadDeclarers.get() != null      */
specifier|public
specifier|static
name|OJClass
name|typeToOJClass
parameter_list|(
name|RelDataType
name|rowType
parameter_list|,
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
block|{
name|OJClass
name|declarer
init|=
name|threadDeclarers
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|declarer
operator|==
literal|null
condition|)
block|{
assert|assert
operator|(
literal|false
operator|)
operator|:
literal|"threadDeclarers.get() != null"
assert|;
block|}
return|return
name|typeToOJClass
argument_list|(
name|declarer
argument_list|,
name|rowType
argument_list|,
name|typeFactory
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Object
name|literalValue
parameter_list|(
name|Literal
name|literal
parameter_list|)
block|{
name|String
name|value
init|=
name|literal
operator|.
name|toString
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|literal
operator|.
name|getLiteralType
argument_list|()
condition|)
block|{
case|case
name|Literal
operator|.
name|BOOLEAN
case|:
return|return
name|value
operator|.
name|equals
argument_list|(
literal|"true"
argument_list|)
condition|?
name|Boolean
operator|.
name|TRUE
else|:
name|Boolean
operator|.
name|FALSE
return|;
case|case
name|Literal
operator|.
name|INTEGER
case|:
return|return
operator|new
name|Integer
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|value
argument_list|)
argument_list|)
return|;
case|case
name|Literal
operator|.
name|LONG
case|:
name|value
operator|=
name|value
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|value
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
comment|// remove 'l'
return|return
operator|new
name|Integer
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|value
argument_list|)
argument_list|)
return|;
case|case
name|Literal
operator|.
name|FLOAT
case|:
name|value
operator|=
name|value
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|value
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
comment|// remove 'f'
return|return
operator|new
name|Double
argument_list|(
name|Double
operator|.
name|parseDouble
argument_list|(
name|value
argument_list|)
argument_list|)
return|;
case|case
name|Literal
operator|.
name|DOUBLE
case|:
name|value
operator|=
name|value
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|value
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
comment|// remove 'd'
return|return
operator|new
name|Double
argument_list|(
name|Double
operator|.
name|parseDouble
argument_list|(
name|value
argument_list|)
argument_list|)
return|;
case|case
name|Literal
operator|.
name|CHARACTER
case|:
return|return
name|value
operator|.
name|substring
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
return|;
comment|// 'x' --> x
case|case
name|Literal
operator|.
name|STRING
case|:
return|return
name|Util
operator|.
name|stripDoubleQuotes
argument_list|(
name|value
argument_list|)
return|;
comment|// "foo" --> foo
case|case
name|Literal
operator|.
name|NULL
case|:
return|return
literal|null
return|;
default|default:
throw|throw
name|Util
operator|.
name|newInternal
argument_list|(
literal|"unknown literal type "
operator|+
name|literal
operator|.
name|getLiteralType
argument_list|()
argument_list|)
throw|;
block|}
block|}
specifier|public
specifier|static
name|TypeName
name|typeNameForClass
parameter_list|(
name|Class
name|clazz
parameter_list|)
block|{
return|return
name|TypeName
operator|.
name|forOJClass
argument_list|(
name|OJClass
operator|.
name|forClass
argument_list|(
name|clazz
argument_list|)
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|String
name|replaceDotWithDollar
parameter_list|(
name|String
name|base
parameter_list|,
name|int
name|i
parameter_list|)
block|{
return|return
name|base
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|i
argument_list|)
operator|+
literal|'$'
operator|+
name|base
operator|.
name|substring
argument_list|(
name|i
operator|+
literal|1
argument_list|)
return|;
block|}
comment|/**      * Guesses the row-type of an expression which has type<code>clazz</code>.      * For example, {@link String}[] --> {@link String}; {@link      * java.util.Iterator} --> {@link Object}.      */
specifier|public
specifier|static
specifier|final
name|OJClass
name|guessRowType
parameter_list|(
name|OJClass
name|clazz
parameter_list|)
block|{
if|if
condition|(
name|clazz
operator|.
name|isArray
argument_list|()
condition|)
block|{
return|return
name|clazz
operator|.
name|getComponentType
argument_list|()
return|;
block|}
if|else if
condition|(
name|clazzIterator
operator|.
name|isAssignableFrom
argument_list|(
name|clazz
argument_list|)
operator|||
name|clazzEnumeration
operator|.
name|isAssignableFrom
argument_list|(
name|clazz
argument_list|)
operator|||
name|clazzVector
operator|.
name|isAssignableFrom
argument_list|(
name|clazz
argument_list|)
operator|||
name|clazzCollection
operator|.
name|isAssignableFrom
argument_list|(
name|clazz
argument_list|)
operator|||
name|clazzResultSet
operator|.
name|isAssignableFrom
argument_list|(
name|clazz
argument_list|)
condition|)
block|{
return|return
name|clazzObject
return|;
block|}
if|else if
condition|(
name|clazzHashtable
operator|.
name|isAssignableFrom
argument_list|(
name|clazz
argument_list|)
operator|||
name|clazzMap
operator|.
name|isAssignableFrom
argument_list|(
name|clazz
argument_list|)
condition|)
block|{
return|return
name|clazzEntry
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
comment|/**      * Sets a {@link ParseTreeVisitor} going on a parse tree, and returns the      * result.      */
specifier|public
specifier|static
name|ParseTree
name|go
parameter_list|(
name|ParseTreeVisitor
name|visitor
parameter_list|,
name|ParseTree
name|p
parameter_list|)
block|{
name|ObjectList
name|holder
init|=
operator|new
name|ObjectList
argument_list|(
name|p
argument_list|)
decl_stmt|;
try|try
block|{
name|p
operator|.
name|accept
argument_list|(
name|visitor
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|StopIterationException
name|e
parameter_list|)
block|{
comment|// ignore the exception -- it was just a way to abort the traversal
block|}
catch|catch
parameter_list|(
name|ParseTreeException
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
literal|"while visiting expression "
operator|+
name|p
argument_list|)
throw|;
block|}
return|return
operator|(
name|ParseTree
operator|)
name|holder
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
comment|/**      * Sets a {@link ParseTreeVisitor} going on a given non-relational      * expression, and returns the result.      */
specifier|public
specifier|static
name|Expression
name|go
parameter_list|(
name|ParseTreeVisitor
name|visitor
parameter_list|,
name|Expression
name|p
parameter_list|)
block|{
return|return
operator|(
name|Expression
operator|)
name|go
argument_list|(
name|visitor
argument_list|,
operator|(
name|ParseTree
operator|)
name|p
argument_list|)
return|;
block|}
comment|/**      * Ensures that an expression is an object. Primitive expressions are      * wrapped in a constructor (for example, the<code>int</code> expression      *<code>2 + 3</code> becomes<code>new Integer(2 + 3)</code>);      * non-primitive expressions are unchanged.      *      * @param exp an expression      * @param clazz<code>exp</code>'s type      *      * @return a call to the constructor of a wrapper class if<code>exp</code>      * is primitive,<code>exp</code> otherwise      */
specifier|public
specifier|static
name|Expression
name|box
parameter_list|(
name|OJClass
name|clazz
parameter_list|,
name|Expression
name|exp
parameter_list|)
block|{
if|if
condition|(
name|clazz
operator|.
name|isPrimitive
argument_list|()
condition|)
block|{
return|return
operator|new
name|AllocationExpression
argument_list|(
name|clazz
operator|.
name|primitiveWrapper
argument_list|()
argument_list|,
operator|new
name|ExpressionList
argument_list|(
name|exp
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|exp
return|;
block|}
block|}
comment|/**      * Gets the root environment, which is always a {@link GlobalEnvironment}.      *      * @param env environment to start search from      */
specifier|public
specifier|static
name|GlobalEnvironment
name|getGlobalEnvironment
parameter_list|(
name|Environment
name|env
parameter_list|)
block|{
for|for
control|(
init|;
condition|;
control|)
block|{
name|Environment
name|parent
init|=
name|env
operator|.
name|getParent
argument_list|()
decl_stmt|;
if|if
condition|(
name|parent
operator|==
literal|null
condition|)
block|{
return|return
operator|(
name|GlobalEnvironment
operator|)
name|env
return|;
block|}
else|else
block|{
name|env
operator|=
name|parent
expr_stmt|;
block|}
block|}
block|}
comment|/**      * If env is a {@link ClassEnvironment} for declarerName, records new inner      * class innerName; otherwise, delegates up the environment hierarchy.      *      * @param env environment to start search from      * @param declarerName fully-qualified name of enclosing class      * @param innerName simple name of inner class      */
specifier|public
specifier|static
name|void
name|recordMemberClass
parameter_list|(
name|Environment
name|env
parameter_list|,
name|String
name|declarerName
parameter_list|,
name|String
name|innerName
parameter_list|)
block|{
do|do
block|{
if|if
condition|(
name|env
operator|instanceof
name|ClassEnvironment
condition|)
block|{
if|if
condition|(
name|declarerName
operator|.
name|equals
argument_list|(
name|env
operator|.
name|currentClassName
argument_list|()
argument_list|)
condition|)
block|{
operator|(
operator|(
name|ClassEnvironment
operator|)
name|env
operator|)
operator|.
name|recordMemberClass
argument_list|(
name|innerName
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
else|else
block|{
name|env
operator|=
name|env
operator|.
name|getParent
argument_list|()
expr_stmt|;
block|}
block|}
do|while
condition|(
name|env
operator|!=
literal|null
condition|)
do|;
block|}
specifier|public
specifier|static
name|OJClass
name|getType
parameter_list|(
name|Environment
name|env
parameter_list|,
name|Expression
name|exp
parameter_list|)
block|{
try|try
block|{
name|OJClass
name|clazz
init|=
name|exp
operator|.
name|getType
argument_list|(
name|env
argument_list|)
decl_stmt|;
assert|assert
operator|(
name|clazz
operator|!=
literal|null
operator|)
assert|;
return|return
name|clazz
return|;
block|}
catch|catch
parameter_list|(
name|Exception
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
literal|"while deriving type for '"
operator|+
name|exp
operator|+
literal|"'"
argument_list|)
throw|;
block|}
block|}
comment|/**      * Counts the number of nodes in a parse tree.      *      * @param parseTree tree to walk      *      * @return count of nodes      */
specifier|public
specifier|static
name|int
name|countParseTreeNodes
parameter_list|(
name|ParseTree
name|parseTree
parameter_list|)
block|{
name|int
name|n
init|=
literal|1
decl_stmt|;
if|if
condition|(
name|parseTree
operator|instanceof
name|NonLeaf
condition|)
block|{
name|Object
index|[]
name|contents
init|=
operator|(
operator|(
name|NonLeaf
operator|)
name|parseTree
operator|)
operator|.
name|getContents
argument_list|()
decl_stmt|;
for|for
control|(
name|Object
name|obj
range|:
name|contents
control|)
block|{
if|if
condition|(
name|obj
operator|instanceof
name|ParseTree
condition|)
block|{
name|n
operator|+=
name|countParseTreeNodes
argument_list|(
operator|(
name|ParseTree
operator|)
name|obj
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|n
operator|+=
literal|1
expr_stmt|;
block|}
block|}
block|}
if|else if
condition|(
name|parseTree
operator|instanceof
name|openjava
operator|.
name|ptree
operator|.
name|List
condition|)
block|{
name|Enumeration
name|e
init|=
operator|(
operator|(
name|openjava
operator|.
name|ptree
operator|.
name|List
operator|)
name|parseTree
operator|)
operator|.
name|elements
argument_list|()
decl_stmt|;
while|while
condition|(
name|e
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|Object
name|obj
init|=
operator|(
name|Object
operator|)
name|e
operator|.
name|nextElement
argument_list|()
decl_stmt|;
if|if
condition|(
name|obj
operator|instanceof
name|ParseTree
condition|)
block|{
name|n
operator|+=
name|countParseTreeNodes
argument_list|(
operator|(
name|ParseTree
operator|)
name|obj
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|n
operator|+=
literal|1
expr_stmt|;
block|}
block|}
block|}
return|return
name|n
return|;
block|}
specifier|public
specifier|static
name|ExpressionList
name|expressionList
parameter_list|(
name|Expression
modifier|...
name|expressions
parameter_list|)
block|{
name|ExpressionList
name|list
init|=
operator|new
name|ExpressionList
argument_list|()
decl_stmt|;
for|for
control|(
name|Expression
name|expression
range|:
name|expressions
control|)
block|{
name|list
operator|.
name|add
argument_list|(
name|expression
argument_list|)
expr_stmt|;
block|}
return|return
name|list
return|;
block|}
comment|//~ Inner Classes ----------------------------------------------------------
comment|/**      * A<code>StopIterationException</code> is a way to tell a {@link      * openjava.ptree.util.ParseTreeVisitor} to halt traversal of the tree, but      * is not regarded as an error.      */
specifier|public
specifier|static
class|class
name|StopIterationException
extends|extends
name|ParseTreeException
block|{
specifier|public
name|StopIterationException
parameter_list|()
block|{
block|}
block|}
block|}
end_class

begin_comment
comment|// End OJUtil.java
end_comment

end_unit

