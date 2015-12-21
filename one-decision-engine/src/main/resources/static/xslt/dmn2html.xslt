<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE xsl:stylesheet [ <!ENTITY nbsp "&#160;"> ]>
<xsl:stylesheet version="1.0" 
  xmlns="http://www.omg.org/spec/DMN/20151101/dmn.xsd"
  xmlns:dmn="http://www.omg.org/spec/DMN/20151101/dmn.xsd"
  xmlns:html="http://www.w3.org/1999/xhtml"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:param name="drgElementId"/>
  <xsl:output method="html" omit-xml-declaration="yes"/>
  
  <xsl:template match="/">
		
		<xsl:choose>
		  <xsl:when test="$drgElementId">
		    <xsl:apply-templates select="//dmn:businessKnowledgeModel[@id=$drgElementId]"/>
		    <xsl:apply-templates select="//dmn:decision[@id=$drgElementId]"/>
		  </xsl:when>
		  <xsl:otherwise>
		    <xsl:apply-templates select="//dmn:businessKnowledgeModel">
		      <xsl:sort select="@name"/>
		    </xsl:apply-templates>
		    <xsl:apply-templates select="//dmn:decision">
          <xsl:sort select="@name"/>
        </xsl:apply-templates>
		  </xsl:otherwise>
		</xsl:choose>
    
	</xsl:template>
	
  <xsl:template match="dmn:binding">
    <tr>
	    <td class="parameter">
	      <xsl:element name="input">
	        <xsl:attribute name="value"><xsl:value-of select="dmn:parameter/@name"/></xsl:attribute>
	      </xsl:element>
	    </td>
	    <td class="binding-expression">
	      <xsl:apply-templates select="dmn:literalExpression"/>
      </td>
	  </tr>
	</xsl:template>
	
  <xsl:template match="dmn:businessKnowledgeModel">
    <xsl:element name="section">
      <xsl:attribute name="id"><xsl:value-of select="@id"/>Sect</xsl:attribute>
      <xsl:attribute name="class">bkm</xsl:attribute>
      
      <h2><xsl:value-of select="@name"/></h2>
	    
	    <xsl:apply-templates select=".//dmn:context"/>
	    <xsl:apply-templates select=".//dmn:decisionTable"/>
	  </xsl:element>
  </xsl:template>

  <xsl:template match="dmn:context">
    <xsl:element name="section">
      <xsl:attribute name="id"><xsl:value-of select="@id"/>Sect</xsl:attribute>
      <xsl:attribute name="class">context</xsl:attribute>
      <table class="context table">
        <thead>
          <tr>
            <th class="information-item-name">
              <xsl:element name="input">
                <xsl:attribute name="value"><xsl:value-of select="../../@name"/></xsl:attribute>
              </xsl:element>
            </th>
          </tr>
        </thead>
        <tbody>
          <xsl:apply-templates select=".//dmn:contextEntry"/>
        </tbody>
      </table>
    </xsl:element>
  </xsl:template>
  
  <xsl:template match="dmn:contextEntry">
    <tr>
      <xsl:if test="dmn:variable">
	      <td class="parameter">
	        <xsl:element name="input">
	          <xsl:attribute name="value"><xsl:value-of select="dmn:variable/@name"/></xsl:attribute>
	        </xsl:element>
	      </td>
	    </xsl:if>
	    <xsl:element name="td">
	      <xsl:attribute name="class">binding-expression</xsl:attribute>
	      <xsl:if test="dmn:variable">
	        <xsl:attribute name="colspan">2</xsl:attribute>
	      </xsl:if>
        <xsl:apply-templates select="dmn:literalExpression"/>
        <xsl:apply-templates select="dmn:invocation" mode="nested"/>
      </xsl:element>
    </tr>
  </xsl:template>
  
	<xsl:template match="dmn:decision">
	  <xsl:element name="section">
      <xsl:attribute name="id"><xsl:value-of select="@id"/>Sect</xsl:attribute>
      <xsl:attribute name="class">bkm</xsl:attribute>
      
      <h2><xsl:value-of select="@name"/></h2>
      <p><xsl:value-of select="dmn:description"/></p>
      <p>
        <label><xsl:value-of select="dmn:question"/></label>
        <xsl:value-of select="dmn:allowedAnswers"/>
      </p>
      
      <xsl:apply-templates select=".//dmn:context"/>
      <xsl:apply-templates select=".//dmn:decisionTable"/>
      <xsl:apply-templates select=".//dmn:invocation"/>
    </xsl:element>
	</xsl:template>
	
	<!-- Rule as row -->
  <xsl:template match="dmn:decisionTable[@preferredOrientation='Rule-as-Row']">
    <xsl:element name="section">
      <xsl:attribute name="id"><xsl:value-of select="@id"/>Sect</xsl:attribute>
      <xsl:attribute name="class">decision-table</xsl:attribute>
      <!-- <h2>
        <span>decision Table <xsl:value-of select="@id"/></span> 
        <!- -  <div class="pull-right"> 
          <span class="glyphicon glyphicon-remove admin" aria-hidden="true" onclick="ractive.delete(ractive.get('decision'))" title="Delete"></span> 
        </div>- ->
      </h2> -->
      <table id="decisionTable" class="decision-table table table-striped">
        <thead>
          <tr>
            <xsl:element name="th">
              <xsl:attribute name="class">information-item-name</xsl:attribute>
              <xsl:attribute name="colspan">2</xsl:attribute>
              
              <xsl:element name="input">
                <xsl:choose>
                  <xsl:when test="local-name(..) = 'decision'">
                    <xsl:attribute name="value"><xsl:value-of select="../@name"/></xsl:attribute>                  
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:attribute name="value">
                    <xsl:value-of select="../../@name"/></xsl:attribute>
                  </xsl:otherwise>
                </xsl:choose>
              </xsl:element>
            </xsl:element>
          </tr>
          <tr>
	          <xsl:element name="th">
				      <xsl:attribute name="class">expr-name hit-policy</xsl:attribute>
				      <xsl:if test="..//dmn:inputValues or ..//dmn:outputValues"> 
				        <xsl:attribute name="rowspan">2</xsl:attribute>
				      </xsl:if>
              <xsl:apply-templates select="@hitPolicy"/>
            </xsl:element>
            
            <xsl:apply-templates select=".//dmn:input"/>
            <xsl:apply-templates select=".//dmn:output"/>
          </tr>
          <xsl:if test="..//dmn:inputValues or ..//dmn:outputValues"> 
	          <tr>
	            <xsl:apply-templates select=".//dmn:input" mode="allowedValues"/>
              <xsl:apply-templates select=".//dmn:output" mode="allowedValues"/>
	          </tr>
	        </xsl:if>
        </thead>
        <tbody>
          <xsl:apply-templates select=".//dmn:rule" mode="rule-as-row"/>
		    </tbody>
		  </table>
    </xsl:element>
  </xsl:template>
  
  <!--  The real rule as column -->
  <xsl:template match="dmn:decisionTable[@preferredOrientation='Rule-as-Column']">
    <xsl:element name="section">
      <xsl:attribute name="id"><xsl:value-of select="@id"/>Sect</xsl:attribute>
      <xsl:attribute name="class">decision-table</xsl:attribute>
      <h2>
        <span>Decision Table</span> 
        <div class="pull-right"> 
          <span class="glyphicon glyphicon-remove" aria-hidden="true" onclick="ractive.delete(ractive.get('decision'))" title="Delete"></span> 
        </div>
      </h2> 
      <table id="decisionTable" class="decision-table table table-striped">
        <thead>
          <tr>
            <th class="information-item-name" colspan="3">
              <xsl:element name="input">
                <xsl:attribute name="value"><xsl:value-of select="../@name"/></xsl:attribute>
              </xsl:element>
            </th>
          </tr>
          <tr>
            <xsl:apply-templates select=".//dmn:input"/>
            <xsl:apply-templates select=".//dmn:output"/>
          </tr>
          <xsl:if test="..//dmn:inputValues or ..//dmn:outputValues"> 
            <tr>
              <xsl:apply-templates select=".//dmn:input" mode="allowedValues"/>
              <xsl:apply-templates select=".//dmn:output" mode="allowedValues"/>
            </tr>
          </xsl:if>
        </thead>
        <tbody>
          <xsl:apply-templates select=".//dmn:rule" mode="rule-as-row"/>
          
          <tr class="rule">
            <th>&nbsp;</th>
            <xsl:element name="th">
              <xsl:attribute name="class">expr-name hit-policy</xsl:attribute>
              <xsl:if test="..//dmn:inputValues or ..//dmn:outputValues"> 
                <xsl:attribute name="rowspan">2</xsl:attribute>
              </xsl:if>
              <xsl:apply-templates select="@hitPolicy"/>
            </xsl:element>
          </tr>
        </tbody>
      </table>
    </xsl:element>
  </xsl:template>
  
  <xsl:template match="@hitPolicy">
    <xsl:element name="input">
      <xsl:attribute name="autocomplete">false</xsl:attribute>
      <xsl:attribute name="class">edit typeahead</xsl:attribute>
      <xsl:attribute name="placeholder">Click to set hit policy</xsl:attribute>

      <xsl:attribute name="title">Hit policy for the table</xsl:attribute>
      <xsl:attribute name="value">
        <xsl:value-of select="substring(.,1,1)"/>
        <xsl:choose>
          <xsl:when test=".='COLLECT' and ../@aggregation='SUM'">+</xsl:when>
          <xsl:when test=".='COLLECT' and ../@aggregation='COUNT'">#</xsl:when>
          <xsl:when test=".='COLLECT' and ../@aggregation='MIN'">&lt;</xsl:when>
          <xsl:when test=".='COLLECT' and ../@aggregation='MAX'">&gt;</xsl:when>
          <xsl:otherwise></xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
    </xsl:element>
  </xsl:template>
  
  <xsl:template match="dmn:invocation">
    <xsl:element name="table">
      <xsl:attribute name="id"><xsl:value-of select="@id"/></xsl:attribute>
      <xsl:attribute name="class">invocation table</xsl:attribute>
       <thead>
         <tr>
           <th class="information-item-name">
             <xsl:element name="input">
               <xsl:attribute name="value"><xsl:value-of select="../@name"/></xsl:attribute>
             </xsl:element>
           </th>
         </tr>
       </thead>
       <tbody>
         <xsl:apply-templates select="dmn:literalExpression" mode="calledFunction"/>
         <xsl:apply-templates select="dmn:binding"/>
       </tbody>
     </xsl:element>
  </xsl:template>
  
  <xsl:template match="dmn:invocation" mode="nested">
    <xsl:element name="table">
      <xsl:attribute name="id"><xsl:value-of select="@id"/></xsl:attribute>
      <xsl:attribute name="class">invocation table</xsl:attribute>
       <tbody>
         <xsl:apply-templates select="dmn:literalExpression" mode="calledFunction"/>
         <xsl:apply-templates select="dmn:binding"/>
       </tbody>
     </xsl:element>
  </xsl:template>
  
  <xsl:template match="dmn:literalExpression">
    <xsl:element name="input">
      <xsl:attribute name="value"><xsl:apply-templates select="dmn:text"/></xsl:attribute>
    </xsl:element>
  </xsl:template>
  
  <xsl:template match="dmn:literalExpression" mode="calledFunction">
    <tr>
      <td class="expression" colspan="2">
        <xsl:element name="input">
          <xsl:attribute name="value"><xsl:value-of select="dmn:text"/></xsl:attribute>
        </xsl:element>
      </td>
    </tr>
  </xsl:template>

  <xsl:template match="dmn:rule" mode="rule-as-column">
    <tr class="condition" data-condition="Select...">
      <th class="input">
	      <span class="glyphicon glyphicon-list-alt" aria-hidden="true" style="border:0px"></span>
	    </th> 
	    <xsl:apply-templates select="dmn:inputEntry" mode="rule-as-column"/>
      
      <th class="output">
        <span class="glyphicon glyphicon-list-alt" aria-hidden="true" style="border:0px"></span>
      </th>
      <xsl:apply-templates select="dmn:outputEntry" mode="rule-as-column"/>
    </tr>
  </xsl:template>
  
  <xsl:template match="dmn:rule" mode="rule-as-row">
    <tr class="condition" data-condition="Select...">
      <th class="">
        <!-- hide icon <span class="glyphicon glyphicon-list-alt" aria-hidden="true" style="border:0px"></span>-->
        <span><xsl:value-of select="position()"/></span>
      </th> 
      <xsl:apply-templates select="dmn:inputEntry"/>
      <xsl:apply-templates select="dmn:outputEntry"/>
    </tr> 
  </xsl:template>
  
  <xsl:template match="dmn:input">
    <xsl:apply-templates select="dmn:inputExpression"/>
  </xsl:template>
  
  <xsl:template match="dmn:input" mode="allowedValues">
    <xsl:choose>
      <xsl:when test="dmn:inputValues">
		    <th class="expr-name">
		      <xsl:value-of select="dmn:inputValues/dmn:text"/>
		    </th>
      </xsl:when>
      <xsl:otherwise>
		    <th class="expr-name"> </th>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <xsl:template match="dmn:inputEntry">
    <xsl:element name="td">
      <xsl:attribute name="class">
        <xsl:text>expr-name</xsl:text>
        <xsl:if test="position()=last()"> last-input</xsl:if>
      </xsl:attribute>
      <xsl:element name="input">
        <xsl:attribute name="autocomplete">false</xsl:attribute>
        <xsl:attribute name="class">edit typeahead</xsl:attribute>
        <xsl:attribute name="placeholder">Select...</xsl:attribute>
        <xsl:attribute name="title"></xsl:attribute>
        <xsl:attribute name="value"><xsl:value-of select="dmn:text"/></xsl:attribute>
      </xsl:element>
    </xsl:element>
  </xsl:template>
  
  <xsl:template match="dmn:inputExpression">
    <xsl:element name="th">
      <xsl:attribute name="class">
        <xsl:text>expr-name input</xsl:text>
        <!-- TODO how to detect last input? -->
        <xsl:if test="count(preceding-sibling::*)=last()"> last-input</xsl:if>
      </xsl:attribute>
      <xsl:element name="input">
        <xsl:attribute name="autocomplete">false</xsl:attribute>
        <xsl:attribute name="class">edit typeahead input</xsl:attribute>
        <xsl:attribute name="placeholder">Select...</xsl:attribute>
        <xsl:attribute name="title"></xsl:attribute>
        <xsl:attribute name="value"><xsl:value-of select="dmn:text"/></xsl:attribute>
      </xsl:element>
    </xsl:element>
  </xsl:template>
    
  <xsl:template match="dmn:output">
    <th class="expr-name output">
      <xsl:element name="input">
        <xsl:attribute name="autocomplete">false</xsl:attribute>
        <xsl:attribute name="class">edit typeahead output</xsl:attribute>
        <xsl:attribute name="placeholder">Select...</xsl:attribute>
        <xsl:attribute name="title"></xsl:attribute>
        <xsl:attribute name="value"><xsl:value-of select="@name"/></xsl:attribute>
      </xsl:element>
    </th>
  </xsl:template>
  
  <xsl:template match="dmn:output" mode="allowedValues">
    <xsl:choose>
      <xsl:when test="dmn:outputValues">
        <th class="expr-name">
          <xsl:value-of select="dmn:outputValues/dmn:text"/>
        </th>
      </xsl:when>
      <xsl:otherwise>
        <th class="expr-name"> </th>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <xsl:template match="dmn:outputEntry">
    <td class="expr-name">
      <xsl:element name="input">
        <xsl:attribute name="autocomplete">false</xsl:attribute>
        <xsl:attribute name="class">edit typeahead</xsl:attribute>
        <xsl:attribute name="placeholder">Select...</xsl:attribute>
        <xsl:attribute name="title"></xsl:attribute>
        <xsl:attribute name="value"><xsl:value-of select="dmn:text"/></xsl:attribute>
      </xsl:element>
    </td>
  </xsl:template>

  <!-- TODO need to convert to textarea -->
	<xsl:template match="dmn:text" name="insertBreaks">
	  <xsl:param name="pText" select="text()"/>
	
	  <xsl:choose>
	    <xsl:when test="not(contains($pText, '&#10;') or contains($pText, '&#13;'))">
	      <xsl:copy-of select="$pText"/>
	    </xsl:when>
      <xsl:when test="contains($pText, '&#10;')">
        <xsl:value-of select="substring-before($pText, '&#10;')"/>
        <br />
        <xsl:call-template name="insertBreaks">
          <xsl:with-param name="pText" select="substring-after($pText, '&#10;')"/>
        </xsl:call-template>
      </xsl:when>
	    <xsl:otherwise>
	      <xsl:value-of select="substring-before($pText, '&#13;')"/>
	      <br />
	      <xsl:call-template name="insertBreaks">
	        <xsl:with-param name="pText" select="substring-after($pText, '&#13;')"/>
	      </xsl:call-template>
	    </xsl:otherwise>
	  </xsl:choose>
	</xsl:template>

	<!-- suppress all else -->
	<xsl:template match="@*|node()">
	</xsl:template>	
</xsl:stylesheet>