begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|net
operator|.
name|hydromatic
operator|.
name|optiq
package|;
end_package

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
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * A collection of objects in a schema that have the same name.  *  *<p>As well as providing the objects, the Overload interface provides a  * method to resolve a particular call to a particular function, based on  * the types of the arguments. A particular implementation might allow the  * following:</p>  *  *<ul>  *  *<li>Implicit conversions. For example, supply a {@code float} argument  * for {@code double} parameter</li>  *  *<li>Allow parameters to have default values. For example, you can call  * {@code double logarithm(double value, double base = Math.E)} with either  * one or two arguments.</li>  *  *<li>Derive extra type information from the arguments. For example, a call  * to the "substring" function with a first argument of type "VARCHAR(30)"  * yields a result of type "VARCHAR(30)).</li>  *  *</ul>  *  *<p>It is up to the implementation how to choose between the various  * options.</p>  *  *<p>Note that a schema that consists only of tables (which have unique  * names, and no parameters) will not return any overloads.</p>  */
end_comment

begin_interface
specifier|public
interface|interface
name|Overload
extends|extends
name|SchemaObject
block|{
comment|/**      * Resolves this overload to a particular function, or returns null if      * there is no matching function.      *      * @param argumentTypes Parameter types      * @return Resolved function, or null if no match      */
name|Function
name|resolve
parameter_list|(
name|List
argument_list|<
name|RelDataType
argument_list|>
name|argumentTypes
parameter_list|)
function_decl|;
block|}
end_interface

begin_comment
comment|// End Overload.java
end_comment

end_unit

