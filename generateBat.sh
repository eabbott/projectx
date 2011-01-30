echo groovy -cp `ls lib/* | awk '{ ORS=";"; print }'`. projx %1 > runprojx.bat
