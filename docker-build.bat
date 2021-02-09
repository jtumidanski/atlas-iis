@ECHO OFF
IF "%1"=="NO-CACHE" (docker build --no-cache -f Dockerfile --tag atlas-iis:latest .)
IF NOT "%1"=="NO-CACHE" (docker build -f Dockerfile --tag atlas-iis:latest .)
