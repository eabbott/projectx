#groovy -cp lib/commons-logging-1.1.jar:lib/dom4j-1.6.1.jar:lib/geronimo-stax-api_1.0_spec-1.0.jar:lib/junit-3.8.1.jar:lib/log4j-1.2.13.jar:lib/poi-3.7-20101029.jar:lib/poi-examples-3.7-20101029.jar:lib/poi-ooxml-3.7-20101029.jar:lib/poi-ooxml-schemas-3.7-20101029.jar:lib/poi-scratchpad-3.7-20101029.jar:lib/xmlbeans-2.3.0.jar:lib/jazzy.jar score
#groovy -cp `ls htmlunitlib/* | awk '{ ORS=":"; print }'` web
groovy -cp `ls lib/* | awk '{ ORS=":"; print }'` projx $1
#echo groovy -cp `ls lib/* | awk '{ ORS=";"; print }'` web
