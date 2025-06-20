#!/bin/sh
# This script starts the Nuxt server in the background and then starts Nginx in the foreground.

# Start the Nuxt server on port 3000
echo "Starting Nuxt server..."
node .output/server/index.mjs &

# Wait for a few seconds to allow other services to start up
echo "Waiting for other services to be ready..."
sleep 5

# Start Nginx
echo "Starting Nginx..."
nginx -g 'daemon off;' 