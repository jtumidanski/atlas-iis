if [[ "$1" = "NO-CACHE" ]]
then
   docker build --no-cache --tag atlas-iis:latest .
else
   docker build --tag atlas-iis:latest .
fi
