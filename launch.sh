## ./launch.sh -g Assignment1 "scratch/Dawson_Deterious/political systems.docx" 
export JAVA_OPTS="-XX:MaxPermSize=128m -Xmx384m"
groovy -cp `ls lib/* | awk '{ ORS=":"; print }'` projx "$1" "$2" "$3"
