#!/bin/sh
chown -R appuser:appgroup /app/uploads
exec java -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -jar app.jar