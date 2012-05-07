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
name|math
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
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
name|*
import|;
end_import

begin_comment
comment|/**  * JavaToSqlTypeConversionRules defines mappings from common Java types to  * corresponding SQL types.  *  * @author John V. Sichi  * @version $Id$  */
end_comment

begin_class
specifier|public
class|class
name|JavaToSqlTypeConversionRules
block|{
comment|//~ Static fields/initializers ---------------------------------------------
specifier|private
specifier|static
specifier|final
name|JavaToSqlTypeConversionRules
name|instance
init|=
operator|new
name|JavaToSqlTypeConversionRules
argument_list|()
decl_stmt|;
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|Map
argument_list|<
name|Class
argument_list|<
name|?
argument_list|>
argument_list|,
name|SqlTypeName
argument_list|>
name|rules
init|=
operator|new
name|HashMap
argument_list|<
name|Class
argument_list|<
name|?
argument_list|>
argument_list|,
name|SqlTypeName
argument_list|>
argument_list|()
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|private
name|JavaToSqlTypeConversionRules
parameter_list|()
block|{
name|rules
operator|.
name|put
argument_list|(
name|Integer
operator|.
name|class
argument_list|,
name|SqlTypeName
operator|.
name|INTEGER
argument_list|)
expr_stmt|;
name|rules
operator|.
name|put
argument_list|(
name|int
operator|.
name|class
argument_list|,
name|SqlTypeName
operator|.
name|INTEGER
argument_list|)
expr_stmt|;
name|rules
operator|.
name|put
argument_list|(
name|Long
operator|.
name|class
argument_list|,
name|SqlTypeName
operator|.
name|BIGINT
argument_list|)
expr_stmt|;
name|rules
operator|.
name|put
argument_list|(
name|long
operator|.
name|class
argument_list|,
name|SqlTypeName
operator|.
name|BIGINT
argument_list|)
expr_stmt|;
name|rules
operator|.
name|put
argument_list|(
name|Short
operator|.
name|class
argument_list|,
name|SqlTypeName
operator|.
name|SMALLINT
argument_list|)
expr_stmt|;
name|rules
operator|.
name|put
argument_list|(
name|short
operator|.
name|class
argument_list|,
name|SqlTypeName
operator|.
name|SMALLINT
argument_list|)
expr_stmt|;
name|rules
operator|.
name|put
argument_list|(
name|byte
operator|.
name|class
argument_list|,
name|SqlTypeName
operator|.
name|TINYINT
argument_list|)
expr_stmt|;
name|rules
operator|.
name|put
argument_list|(
name|Byte
operator|.
name|class
argument_list|,
name|SqlTypeName
operator|.
name|TINYINT
argument_list|)
expr_stmt|;
name|rules
operator|.
name|put
argument_list|(
name|Float
operator|.
name|class
argument_list|,
name|SqlTypeName
operator|.
name|REAL
argument_list|)
expr_stmt|;
name|rules
operator|.
name|put
argument_list|(
name|float
operator|.
name|class
argument_list|,
name|SqlTypeName
operator|.
name|REAL
argument_list|)
expr_stmt|;
name|rules
operator|.
name|put
argument_list|(
name|Double
operator|.
name|class
argument_list|,
name|SqlTypeName
operator|.
name|DOUBLE
argument_list|)
expr_stmt|;
name|rules
operator|.
name|put
argument_list|(
name|double
operator|.
name|class
argument_list|,
name|SqlTypeName
operator|.
name|DOUBLE
argument_list|)
expr_stmt|;
name|rules
operator|.
name|put
argument_list|(
name|boolean
operator|.
name|class
argument_list|,
name|SqlTypeName
operator|.
name|BOOLEAN
argument_list|)
expr_stmt|;
name|rules
operator|.
name|put
argument_list|(
name|Boolean
operator|.
name|class
argument_list|,
name|SqlTypeName
operator|.
name|BOOLEAN
argument_list|)
expr_stmt|;
name|rules
operator|.
name|put
argument_list|(
name|byte
index|[]
operator|.
expr|class
argument_list|,
name|SqlTypeName
operator|.
name|VARBINARY
argument_list|)
expr_stmt|;
name|rules
operator|.
name|put
argument_list|(
name|String
operator|.
name|class
argument_list|,
name|SqlTypeName
operator|.
name|VARCHAR
argument_list|)
expr_stmt|;
name|rules
operator|.
name|put
argument_list|(
name|char
index|[]
operator|.
expr|class
argument_list|,
name|SqlTypeName
operator|.
name|VARCHAR
argument_list|)
expr_stmt|;
name|rules
operator|.
name|put
argument_list|(
name|Character
operator|.
name|class
argument_list|,
name|SqlTypeName
operator|.
name|CHAR
argument_list|)
expr_stmt|;
name|rules
operator|.
name|put
argument_list|(
name|char
operator|.
name|class
argument_list|,
name|SqlTypeName
operator|.
name|CHAR
argument_list|)
expr_stmt|;
name|rules
operator|.
name|put
argument_list|(
name|Date
operator|.
name|class
argument_list|,
name|SqlTypeName
operator|.
name|DATE
argument_list|)
expr_stmt|;
name|rules
operator|.
name|put
argument_list|(
name|Timestamp
operator|.
name|class
argument_list|,
name|SqlTypeName
operator|.
name|TIMESTAMP
argument_list|)
expr_stmt|;
name|rules
operator|.
name|put
argument_list|(
name|Time
operator|.
name|class
argument_list|,
name|SqlTypeName
operator|.
name|TIME
argument_list|)
expr_stmt|;
name|rules
operator|.
name|put
argument_list|(
name|BigDecimal
operator|.
name|class
argument_list|,
name|SqlTypeName
operator|.
name|DECIMAL
argument_list|)
expr_stmt|;
name|rules
operator|.
name|put
argument_list|(
name|ResultSet
operator|.
name|class
argument_list|,
name|SqlTypeName
operator|.
name|CURSOR
argument_list|)
expr_stmt|;
name|rules
operator|.
name|put
argument_list|(
name|List
operator|.
name|class
argument_list|,
name|SqlTypeName
operator|.
name|COLUMN_LIST
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**      * Returns the {@link org.eigenbase.util.Glossary#SingletonPattern      * singleton} instance.      */
specifier|public
specifier|static
name|JavaToSqlTypeConversionRules
name|instance
parameter_list|()
block|{
return|return
name|instance
return|;
block|}
comment|/**      * Returns a corresponding {@link SqlTypeName} for a given Java class.      *      * @param javaClass the Java class to lookup      *      * @return a corresponding SqlTypeName if found, otherwise null is returned      */
specifier|public
name|SqlTypeName
name|lookup
parameter_list|(
name|Class
name|javaClass
parameter_list|)
block|{
return|return
name|rules
operator|.
name|get
argument_list|(
name|javaClass
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End JavaToSqlTypeConversionRules.java
end_comment

end_unit

