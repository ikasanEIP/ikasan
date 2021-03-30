<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:ns="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:x="http://www.bookprices.com/xsd/book-prices.xsd"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
    <xsl:param name="schemaLocation" select="'undefined'"/>

    <xsl:template match="//books">
        <AdjustedBooks>
            <xsl:attribute name="xsi:noNamespaceSchemaLocation">
                <xsl:value-of select="$schemaLocation"/>
            </xsl:attribute>
            <xsl:apply-templates select="/books/book"/>
        </AdjustedBooks>
    </xsl:template>

    <xsl:template match="/books/book">
        <xsl:element name="AdjustedBook">
            <xsl:attribute name="id">
                <xsl:value-of select="@id"/>
            </xsl:attribute>
            <xsl:element name="Author">
                <xsl:value-of select="author"/>
            </xsl:element>
            <xsl:element name="Title">
                <xsl:value-of select="title"/>
            </xsl:element>
            <xsl:element name="Genre">
                <xsl:value-of select="genre"/>
            </xsl:element>
            <xsl:element name="Price">
                <xsl:value-of select="format-number(price * 1.10,'###.##')"/>
            </xsl:element>
            <xsl:element name="PubDate">
                <xsl:value-of select="pub_date"/>
            </xsl:element>
            <xsl:element name="Review">
                <xsl:value-of select="review"/>
            </xsl:element>
        </xsl:element>
    </xsl:template>

</xsl:stylesheet>

