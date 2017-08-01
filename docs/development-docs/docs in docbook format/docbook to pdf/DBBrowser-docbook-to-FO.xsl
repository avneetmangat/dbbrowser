<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">
	
	<xsl:variable name="PAGE_WIDTH_IN_CM">21</xsl:variable>
	<xsl:variable name="PAGE_HEIGHT_IN_CM">29.7</xsl:variable>
	<xsl:variable name="PAGE_LEFT_MARGIN_IN_CM">1</xsl:variable>
	<xsl:variable name="PAGE_RIGHT_MARGIN_IN_CM">1</xsl:variable>
	<xsl:variable name="PAGE_TOP_MARGIN_IN_CM">1</xsl:variable>
	<xsl:variable name="PAGE_BOTTOM_MARGIN_IN_CM">1.5</xsl:variable>

	<xsl:variable name="BACKGROUND_COLOUR_FOR_NOTES">rgb(190,190,190)</xsl:variable>
	<xsl:variable name="FONT_COLOUR_FOR_CHAPTER_TITLE">rgb(94,94,210)</xsl:variable>
	<xsl:variable name="BACKGROUND_COLOUR_FOR_DUMMY_TEXT">white</xsl:variable>
	<xsl:variable name="FONT_SIZE_FOR_CHAPTER_TITLE">20pt</xsl:variable>
	<xsl:variable name="LEFT_MARGIN_FOR_NOTES">1cm</xsl:variable>
	<xsl:variable name="SPACE_FOR_NOTES">16.5cm</xsl:variable>
	<xsl:variable name="RIGHT_MARGIN_FOR_NOTES">1cm</xsl:variable>

	<xsl:template  match="book">
		<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
			<xsl:message></xsl:message>		
			<xsl:message>***** XSLT processor details *****</xsl:message>
			<xsl:message>XSL version: <xsl:value-of select="system-property('xsl:version')" /></xsl:message>		
			<xsl:message>XSL vendor: <xsl:value-of select="system-property('xsl:vendor')" /></xsl:message>		
			<xsl:message>XSL vendor url: <xsl:value-of select="system-property('xsl:vendor-url')" /></xsl:message>		
			<xsl:message></xsl:message>		
			<xsl:message>Started processing DBBrowser docbook XML document...</xsl:message>
			<xsl:message></xsl:message>	
			
			<!--Setup page sizes-->
			<fo:layout-master-set>
				<fo:simple-page-master master-name="a4_first_page_header">
					<xsl:attribute name="page-height"><xsl:value-of select="$PAGE_HEIGHT_IN_CM" />cm</xsl:attribute>
					<xsl:attribute name="page-width"><xsl:value-of select="$PAGE_WIDTH_IN_CM"/>cm</xsl:attribute>
					<xsl:attribute name="margin-left"><xsl:value-of select="$PAGE_LEFT_MARGIN_IN_CM"/>cm</xsl:attribute>
					<xsl:attribute name="margin-right"><xsl:value-of select="$PAGE_RIGHT_MARGIN_IN_CM"/>cm</xsl:attribute>
					<xsl:attribute name="margin-top"><xsl:value-of select="$PAGE_TOP_MARGIN_IN_CM" />cm</xsl:attribute>
					<xsl:attribute name="margin-bottom"><xsl:value-of select="$PAGE_BOTTOM_MARGIN_IN_CM" />cm</xsl:attribute>
					<fo:region-body margin-top="2cm" margin-bottom="1.5cm"/>
					<fo:region-before extent="1cm" region-name="page_header"/>
					<fo:region-after extent="1cm" region-name="page_footer"/>
				</fo:simple-page-master>
				<fo:simple-page-master master-name="a4_all_pages_except_first_page_header">
					<xsl:attribute name="page-height"><xsl:value-of select="$PAGE_HEIGHT_IN_CM" />cm</xsl:attribute>
					<xsl:attribute name="page-width"><xsl:value-of select="$PAGE_WIDTH_IN_CM" />cm</xsl:attribute>
					<xsl:attribute name="margin-top"><xsl:value-of select="$PAGE_TOP_MARGIN_IN_CM" />cm</xsl:attribute>
					<xsl:attribute name="margin-bottom"><xsl:value-of select="$PAGE_BOTTOM_MARGIN_IN_CM" />cm</xsl:attribute>
					<xsl:attribute name="margin-left"><xsl:value-of select="$PAGE_LEFT_MARGIN_IN_CM" />cm</xsl:attribute>
					<xsl:attribute name="margin-right"><xsl:value-of select="$PAGE_RIGHT_MARGIN_IN_CM" />cm</xsl:attribute>
					
					<fo:region-body margin-top="2cm" margin-bottom="1.5cm"/>
					<fo:region-before extent="1cm" region-name="all_pages_except_first_page_header"/>
					<fo:region-after extent="1cm" region-name="page_footer"/>		
				</fo:simple-page-master>
				
				<!--Set the page header and footer for the pages-->
				<fo:page-sequence-master master-name="document">
					<fo:repeatable-page-master-alternatives>
						<fo:conditional-page-master-reference page-position="first"  master-reference="a4_first_page_header"/>
						<fo:conditional-page-master-reference page-position="rest" master-reference="a4_all_pages_except_first_page_header"/>
					</fo:repeatable-page-master-alternatives>
				</fo:page-sequence-master>									
			</fo:layout-master-set>

			<!--Format the contents of the page-->
			<fo:page-sequence master-reference="document" font-size="10pt">

				<!--Add contents to the page headers-->
				<xsl:message>Processing page headers...</xsl:message>       	
				<fo:static-content flow-name="all_pages_except_first_page_header">
					<xsl:for-each select="title">
						<fo:block border-width="1pt" border-style="solid" text-align="center">
							<xsl:value-of select="."/>
						</fo:block>
					</xsl:for-each>	
				</fo:static-content>	
				<xsl:message>...finished processing first page header sections</xsl:message> 

				<!--Add contents to the page footers-->
				<xsl:message>Processing page footers...</xsl:message>       	
				<fo:static-content flow-name="page_footer">
					<xsl:for-each select="title">
						<fo:block border-width="1" border-style="solid" text-align="center">
							<fo:block>Page <fo:page-number/> of <fo:page-number-citation ref-id="end_of_doc"/></fo:block>
						</fo:block>
					</xsl:for-each>	
				</fo:static-content>	
				<xsl:message>...finished processing first page footer sections</xsl:message> 
				
				<!--Process sections which appear in the page-->				
				<fo:flow flow-name="xsl-region-body">
					
					<!--Print dummy text to push the other logos to the bottom of the page-->
					<xsl:call-template name="printDummyBlock">
						<xsl:with-param name="i">0</xsl:with-param>
						<xsl:with-param name="max">10</xsl:with-param>			
					</xsl:call-template>
										
					<!--Setup first page-->
					<fo:table table-layout="fixed">
						<fo:table-column column-width="6cm"/>
						<fo:table-column column-width="12cm"/>
						<fo:table-body>
							<fo:table-row>
								<fo:table-cell>
									<fo:block>
										<xsl:attribute name="color"><xsl:value-of select="$BACKGROUND_COLOUR_FOR_DUMMY_TEXT"/></xsl:attribute>
										X
									</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block>
										<fo:external-graphic src="splash-screen.jpg" width="7cm" height="5cm" />
									</fo:block>
								</fo:table-cell>								
							</fo:table-row>
						</fo:table-body>
					</fo:table>	
					
					<!--Print dummy text to push the other logos to the bottom of the page-->
					<xsl:call-template name="printDummyBlock">
						<xsl:with-param name="i">0</xsl:with-param>
						<xsl:with-param name="max">24</xsl:with-param>			
					</xsl:call-template>				
					
					<fo:table table-layout="fixed">
						<fo:table-column column-width="14cm"/>
						<fo:table-column column-width="4cm"/>
						<fo:table-body>
							<fo:table-row>
								<fo:table-cell>
									<fo:block><fo:external-graphic src="hww.jpg" width="4cm" height="4cm" /></fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block><fo:external-graphic src="sflogo.jpg" width="9cm" height="3cm" /></fo:block>
								</fo:table-cell>								
							</fo:table-row>
						</fo:table-body>
					</fo:table>
					
					<!--Add a page break-->
					<fo:block break-after="page" />
										
					<!--Build the table of contents-->
					<fo:block>
						<xsl:attribute name="color"><xsl:value-of select="$FONT_COLOUR_FOR_CHAPTER_TITLE"/></xsl:attribute>
						<xsl:attribute name="font-size"><xsl:value-of select="$FONT_SIZE_FOR_CHAPTER_TITLE"/></xsl:attribute>
						Contents
					</fo:block>

					<xsl:message>Building Table of contents...</xsl:message>       	
					<fo:table table-layout="fixed">
						<fo:table-column column-width="16cm"/>
						<fo:table-column column-width="2cm"/>
						<fo:table-body>
							<xsl:for-each select="chapter">
								<fo:table-row>
									<fo:table-cell>
										<fo:block color="blue">
											<fo:basic-link>
												<xsl:attribute name="internal-destination">
													<xsl:value-of select="@id"/>
												</xsl:attribute>
												<xsl:value-of select="title"/>
											</fo:basic-link>
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block>
											<fo:page-number-citation ref-id="">
												<xsl:attribute name="ref-id">
													<xsl:value-of select="@id"/>
												</xsl:attribute>										
											</fo:page-number-citation>
										</fo:block>
									</fo:table-cell>
								</fo:table-row>
							</xsl:for-each>
						</fo:table-body>
					</fo:table>
					<xsl:message>...finished building Table of contents</xsl:message>       	
					<xsl:message></xsl:message> 

					<!--Add a page break-->
					<fo:block break-after="page">
						<xsl:attribute name="color"><xsl:value-of select="$BACKGROUND_COLOUR_FOR_DUMMY_TEXT"/></xsl:attribute>
						End of contents list
					</fo:block>

					<!--Main page body-->
					<xsl:message>Processing sections which appear in the page...</xsl:message>       	
					<xsl:for-each select="chapter">
						<!--Each section appears as a table-->
						<fo:table table-layout="fixed">
							<!--Add the attribute for id so hyperlinks work-->
							<xsl:attribute name="id">
								<xsl:value-of select="@id"/>
							</xsl:attribute>							

							<fo:table-column column-width="18.5cm"/>
							<fo:table-body>
								<!--Show the chapter title-->
								<fo:table-row>
									<fo:table-cell>
										<fo:block>
											<xsl:attribute name="color"><xsl:value-of select="$FONT_COLOUR_FOR_CHAPTER_TITLE"/></xsl:attribute>
											<xsl:attribute name="font-size"><xsl:value-of select="$FONT_SIZE_FOR_CHAPTER_TITLE"/></xsl:attribute>
											<xsl:value-of select="title"/>
										</fo:block>										
									</fo:table-cell>
								</fo:table-row>

								<!--Each paragraph appears as a table row-->
								<xsl:for-each select="para | note">
									<fo:table-row>
										<fo:table-cell>
											<xsl:choose>
												<!--Process a note-->
												<xsl:when test="name() = 'note'">
													<fo:block>
														<fo:table table-layout="fixed">
															<fo:table-column>
																<xsl:attribute name="column-width"><xsl:value-of select="$LEFT_MARGIN_FOR_NOTES"/></xsl:attribute>
															</fo:table-column>
															<fo:table-column>
																<xsl:attribute name="column-width"><xsl:value-of select="$SPACE_FOR_NOTES"/></xsl:attribute>
															</fo:table-column>
															<fo:table-column>
																<xsl:attribute name="column-width"><xsl:value-of select="$RIGHT_MARGIN_FOR_NOTES"/></xsl:attribute>
															</fo:table-column>
															<fo:table-body>
																<fo:table-row>
																	<fo:table-cell>
																		<xsl:attribute name="background-color"><xsl:value-of select="$BACKGROUND_COLOUR_FOR_DUMMY_TEXT"/></xsl:attribute>
																		X
																	</fo:table-cell>
																	<fo:table-cell>
																		<fo:block border-width="0.5pt" border-style="solid">
																			<xsl:attribute name="background-color"><xsl:value-of select="$BACKGROUND_COLOUR_FOR_NOTES"/></xsl:attribute>
																			<xsl:value-of select="."/>
																		</fo:block>
																	</fo:table-cell>
																	<fo:table-cell border-start-width="0.5pt" border-start-style="solid">
																		<xsl:attribute name="background-color"><xsl:value-of select="$BACKGROUND_COLOUR_FOR_DUMMY_TEXT"/></xsl:attribute>
																		X
																	</fo:table-cell>																
																</fo:table-row>
															</fo:table-body>
														</fo:table>
													</fo:block>
												</xsl:when>
												
												<!--Process a paragraph-->
												<xsl:otherwise>
													<!--Process a screenshot-->
													<xsl:for-each select="screenshot">		<!--Processing a screenshot in a para-->
														<xsl:message>Processing file ../<xsl:value-of select="mediaobject/imageobject/imagedata/@fileref"/></xsl:message>
														
														<!--Showing the screenshot-->
														<fo:block border-width="0.5pt" border-style="solid">
															<fo:external-graphic>
																<xsl:attribute name="src">../<xsl:value-of select="mediaobject/imageobject/imagedata/@fileref"/></xsl:attribute>
																<xsl:attribute name="width"><xsl:value-of select="mediaobject/imageobject/imagedata/@width"/></xsl:attribute>
																<xsl:attribute name="height"><xsl:value-of select="mediaobject/imageobject/imagedata/@depth"/></xsl:attribute>
															</fo:external-graphic>
														</fo:block>
													</xsl:for-each>		
													
													<!--Process a table-->
													<xsl:for-each select="table">		<!--Processing a table in a para-->
														<xsl:message>Processing table <xsl:value-of select="title"/></xsl:message>
															<fo:block>
																<fo:block border-width="0.5pt" border-style="solid"><xsl:value-of select="title"/></fo:block>
																<fo:table table-layout="fixed">
																	<!--Calculate the number of columns and set it-->
																	<xsl:for-each select="tgroup/thead/row/entry">
																		<fo:table-column>
																			<!--xsl:attribute name="column-width"><xsl:value-of select="($PAGE_WIDTH_IN_CM - $PAGE_LEFT_MARGIN_IN_CM - $PAGE_RIGHT_MARGIN_IN_CM) div (../../../@cols)"/>cm</xsl:attribute-->
																			<xsl:attribute name="column-width"><xsl:value-of select="($PAGE_WIDTH_IN_CM - $PAGE_LEFT_MARGIN_IN_CM - $PAGE_RIGHT_MARGIN_IN_CM - 0.5) div (../../../@cols)" />cm</xsl:attribute>
																		</fo:table-column>
																	</xsl:for-each>
																	
																	<!--Show the table headers-->
																	<fo:table-header>
																		<fo:table-row background-color="gray">
																		<xsl:for-each select="tgroup/thead/row/entry">
																			<fo:table-cell>
																				<fo:block border-width="0.5pt" border-style="solid">
																					<xsl:value-of select="."/>
																				</fo:block>
																			</fo:table-cell>
																		</xsl:for-each>
																		</fo:table-row>	
																	</fo:table-header>	
																	<fo:table-body>
																		<!--Show the table data-->
																		<xsl:for-each select="tgroup/tbody/row">																		
																		<fo:table-row>
																			<xsl:for-each select="entry">
																				<fo:table-cell border-width="0.5pt" border-style="solid">
																					<fo:block>
																						<xsl:value-of select="."/>
																					</fo:block>
																				</fo:table-cell>
																			</xsl:for-each>	
																		</fo:table-row>	
																		</xsl:for-each>
																	</fo:table-body>														
																</fo:table>	
															</fo:block>														
															
															<fo:table table-layout="fixed">
																<!--Calculate the number of columns and set it-->
																<xsl:for-each select="tgroup/thead/row/entry">
																	<fo:table-column>
																		<xsl:attribute name="column-width"><xsl:value-of select="($PAGE_WIDTH_IN_CM - $PAGE_LEFT_MARGIN_IN_CM - $PAGE_RIGHT_MARGIN_IN_CM) div (../../../@cols)"/>cm</xsl:attribute>
																	</fo:table-column>
																</xsl:for-each>
																
																<fo:table-body>
																	<fo:table-row>
																		<xsl:for-each select="tgroup/thead/row/entry">
																			<fo:table-cell>
																				<xsl:value-of select="."/>
																			</fo:table-cell>
																		</xsl:for-each>	
																	</fo:table-row>	
																</fo:table-body>														
																
															</fo:table>
													</xsl:for-each>	
													
													<!--If there is a itemized list in the paragraph, show it as a list-->
													<xsl:for-each select="itemizedlist">		<!--Processing a itemized list in a para-->
														<xsl:message>Processing itemizedList...<xsl:value-of select="name()" /></xsl:message>
													    <xsl:for-each select="listitem">
													    	<fo:block>
															    <xsl:call-template name="printBulletPoint" />
															    <fo:inline><xsl:value-of select="para" /></fo:inline>
															</fo:block>
													    </xsl:for-each>													
													</xsl:for-each>
													
													<!--If contents of the current para is empty-->
													<xsl:if test=" . = '' ">
														<fo:block><xsl:attribute name="color"><xsl:value-of select="$BACKGROUND_COLOUR_FOR_DUMMY_TEXT"/></xsl:attribute>X</fo:block>
													</xsl:if>	
													
													<!--If the para has no child element, show it as it is-->
													<xsl:if test="count( child::* ) = 0">
														<fo:block><xsl:value-of select="."/></fo:block>
													</xsl:if>															
												</xsl:otherwise>
											</xsl:choose>
										</fo:table-cell>
									</fo:table-row>
								</xsl:for-each>
							</fo:table-body>								
						</fo:table>
					</xsl:for-each>

					<!--Added block id for end of page marker-->
					<fo:block id="end_of_doc"></fo:block>

					<xsl:message>...finished processing sections which appear in the page</xsl:message>       	
					<xsl:message></xsl:message> 
				</fo:flow>
			</fo:page-sequence>
			
			<xsl:message></xsl:message>						
			<xsl:message>...finished processing DBBrowser docbook XML document</xsl:message>
		</fo:root>
	</xsl:template>
	
	<xsl:template name="printDummyBlock">
		<xsl:param name="i"/>
		<xsl:param name="max"/>	
		<xsl:if test="$i &lt;= $max">
			<fo:block><xsl:attribute name="color"><xsl:value-of select="$BACKGROUND_COLOUR_FOR_DUMMY_TEXT"/></xsl:attribute>X</fo:block>	
			<xsl:call-template name="printDummyBlock">
				<xsl:with-param name="i"><xsl:value-of select="$i + 1" /></xsl:with-param>
				<xsl:with-param name="max"><xsl:value-of select="$max" /></xsl:with-param>			
			</xsl:call-template>
		</xsl:if>		
	</xsl:template>
	
	<!--Display a bullet point-->
	<xsl:template name="printBulletPoint">
		<fo:inline><xsl:attribute name="color"><xsl:value-of select="$BACKGROUND_COLOUR_FOR_DUMMY_TEXT"/></xsl:attribute>XXX</fo:inline>
		<fo:inline font-size="12pt" text-align="left" text-indent="0.2cm">&#x2022;</fo:inline>
	</xsl:template>	
</xsl:stylesheet>
