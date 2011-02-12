echo groovy -cp `ls lib/* | awk '{ ORS=";"; print }'`. projx > runprojx.bat
