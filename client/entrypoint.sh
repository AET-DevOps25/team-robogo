#!/bin/sh
# This script starts the Nuxt server in the background and then starts Nginx in the foreground.

# Set the default GATEWAY_HOST to localhost if not set
export GATEWAY_HOST="${GATEWAY_HOST:-localhost}"

echo "GATEWAY_HOST: $GATEWAY_HOST"

envsubst '${GATEWAY_HOST}' < /etc/nginx/conf.d/default.conf.template > /etc/nginx/conf.d/default.conf

node .output/server/index.mjs &

echo "Waiting for other services to be ready..."
sleep 5

exec nginx -g 'daemon off;' 