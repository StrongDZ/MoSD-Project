#!/bin/sh

# Replace environment variables in template
envsubst '$PORT $SERVER_NAME $VITE_BACKEND_HOST $VITE_BACKEND_PORT $CORS_ALLOWED_ORIGIN' \
  < /etc/nginx/templates/default.conf.template \
  > /etc/nginx/conf.d/default.conf

# Start nginx
exec nginx -g 'daemon off;'

