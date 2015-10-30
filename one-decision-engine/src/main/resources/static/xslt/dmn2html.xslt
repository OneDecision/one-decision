<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE xsl:stylesheet [
  <!ENTITY % w3centities-f PUBLIC "-//W3C//ENTITIES Combined Set//EN//XML"
      "http://www.w3.org/2003/entities/2007/w3centities-f.ent">
  %w3centities-f;
]>
<xsl:stylesheet version="1.0" 
  xmlns="http://www.omg.org/spec/DMN/20130901"
  xmlns:dmn="http://www.omg.org/spec/DMN/20130901"
  xmlns:html="http://www.w3.org/1999/xhtml"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:output method="html" omit-xml-declaration="yes"/>
  
  <xsl:template match="/">
    <!-- <html><head>
		  <meta charset="utf-8"/>
		  <title>One Decision</title>
		  <link href="http://localhost:8090/webjars/bootstrap/3.3.5/css/bootstrap.min.css" rel="stylesheet"/>
		  <link href="http://localhost:8090/webjars/bootstrap/3.3.5/css/bootstrap-theme.min.css" rel="stylesheet"/>
		  <link href="http://localhost:8090/css/one-decision-1.1.0.css" rel="stylesheet"/>
		  <link href="http://localhost:8090/css/decisions-1.1.0.css" rel="stylesheet"/>
		  <link rel="icon" type="image/png" href="http://localhost:8090/images/one-decision-icon-16x16.png"/>
		<style>
		  .input { 
		    background-color: #ccccff !important;
		  }
		  .output { 
        background-color: #ffcccc !important;
      }
      span.glyphicon-remove,
		  td.add-rule,
		  th.expr-action,
		  tr.newConclusion,
		  tr.newCondition { 
		    display:none;
		  }
		</style></head><body> -->
		
		<xsl:apply-templates select="//dmn:Decision"/>
		
		<!-- </body></html> -->
	</xsl:template>
	
	<!-- Rule as row -->
  <xsl:template match="dmn:Decision[dmn:DecisionTable/@preferedOrientation='Rule-as-Column']">
    <section id="dtSect" class="entity-fields">
      <!-- <h2>
        <span>Decision Table <xsl:value-of select="@id"/></span> 
        <!- -  <div class="pull-right"> 
          <span class="glyphicon glyphicon-remove admin" aria-hidden="true" onclick="ractive.delete(ractive.get('decision'))" title="Delete"></span> 
        </div>- ->
      </h2> -->
      <table id="decisionTable" class="decision-table table table-striped">
        <thead>
          <tr>
            <th class="decision-name">
              <xsl:element name="input">
                <xsl:attribute name="value"><xsl:value-of select="@id"/></xsl:attribute>
              </xsl:element>
            </th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <th class="expr-name hit-policy">
              <xsl:element name="input">
                <xsl:attribute name="autocomplete">false</xsl:attribute>
                <xsl:attribute name="class">edit typeahead</xsl:attribute>
                <xsl:attribute name="placeholder">Click to set hit policy</xsl:attribute>
                <xsl:attribute name="title">Hit policy for the table</xsl:attribute>
                <xsl:attribute name="value"><xsl:value-of select="substring(dmn:DecisionTable/@hitPolicy,1,1)"/></xsl:attribute>
              </xsl:element>
            </th>
            
            <xsl:apply-templates select=".//dmn:inputExpression"/>
            <xsl:apply-templates select=".//dmn:outputDefinition"/>
          </tr>
          <xsl:apply-templates select=".//dmn:rule" mode="rule-as-row"/>
		    </tbody>
		  </table>
    </section>
  
  </xsl:template>
  
  <!--  The real rule as column -->
  <xsl:template match="dmn:Decision[dmn:DecisionTable/@preferedOrientation='Rule-as-Column2']">
    <section id="dtSect" class="entity-fields">
      <h2>
        <span>Decision Table</span> 
        <div class="pull-right"> 
          <span class="glyphicon glyphicon-remove" aria-hidden="true" onclick="ractive.delete(ractive.get('decision'))" title="Delete"></span> 
        </div>
      </h2> 
      <table id="decisionTable" class="decision-table table table-striped">
        <thead>
          <tr>
            <th class="decision-name" colspan="3">
              <xsl:element name="input">
                <xsl:attribute name="value"><xsl:value-of select="@id"/></xsl:attribute>
              </xsl:element>
            </th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <xsl:apply-templates select=".//dmn:inputExpression"/>
            <xsl:apply-templates select=".//dmn:outputDefinition"/>
          </tr>
          <xsl:apply-templates select=".//dmn:rule" mode="rule-as-row"/>
          
          <tr class="rule">
            <th>&nbsp;</th>
            <th class="expr-name hit-policy" colspan="2">
              <xsl:element name="input">
                <xsl:attribute name="autocomplete">false</xsl:attribute>
                <xsl:attribute name="class">edit typeahead</xsl:attribute>
                <xsl:attribute name="placeholder">Click to set hit policy</xsl:attribute>
                <xsl:attribute name="title">Hit policy for the table</xsl:attribute>
                <xsl:attribute name="value"><xsl:value-of select="substring(dmn:DecisionTable/@hitPolicy,1,1)"/></xsl:attribute>
              </xsl:element>
            </th>
          </tr>
        </tbody>
      </table>
    </section>
  
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
  
  <xsl:template match="dmn:inputEntry">
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
  
  <xsl:template match="dmn:inputExpression">
    <th class="expr-name input">
      <xsl:element name="input">
        <xsl:attribute name="autocomplete">false</xsl:attribute>
        <xsl:attribute name="class">edit typeahead input</xsl:attribute>
        <xsl:attribute name="placeholder">Select...</xsl:attribute>
        <xsl:attribute name="title"></xsl:attribute>
        <xsl:attribute name="value"><xsl:value-of select="dmn:text"/></xsl:attribute>
      </xsl:element>
    </th>
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
  
  <xsl:template match="dmn:outputDefinition">
    <th class="expr-name output">
      <xsl:element name="input">
        <xsl:attribute name="autocomplete">false</xsl:attribute>
        <xsl:attribute name="class">edit typeahead output</xsl:attribute>
        <xsl:attribute name="placeholder">Select...</xsl:attribute>
        <xsl:attribute name="title"></xsl:attribute>
        <xsl:attribute name="value"><xsl:value-of select="@href"/></xsl:attribute>
      </xsl:element>
    </th>
  </xsl:template>

	<!-- standard copy template -->
	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*"/>
			<xsl:apply-templates/>
		</xsl:copy>
	</xsl:template>	
</xsl:stylesheet>